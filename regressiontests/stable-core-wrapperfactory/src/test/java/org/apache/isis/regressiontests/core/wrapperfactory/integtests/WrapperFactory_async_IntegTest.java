/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.regressiontests.core.wrapperfactory.integtests;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.services.wrapper.control.AsyncControl;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.testdomain.wrapperfactory.Counter;
import org.apache.isis.testdomain.wrapperfactory.Counter_bumpUsingMixin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.SneakyThrows;
import lombok.val;

/**
 * Run "sh enhance.sh -w" first, to enhance the test JDO entities.
 */
class WrapperFactory_async_IntegTest extends CoreWrapperFactory_IntegTestAbstract {

    @Inject WrapperFactory wrapperFactory;
    @Inject TransactionService transactionService;
    @Inject BookmarkService bookmarkService;

    Bookmark bookmark;

    @BeforeEach
    void setup_counter() {

        runWithNewTransaction(() -> {
            counterRepository.persist(newCounter("fred"));
            List<Counter> counters = counterRepository.find();
            assertThat(counters).hasSize(1);

            bookmark = bookmarkService.bookmarkForElseFail(counters.get(0));
        });

        // given
        assertThat(bookmark).isNotNull();

        val counter = bookmarkService.lookup(bookmark, Counter.class).orElseThrow();
        assertThat(counter.getNum()).isNull();
    }

    @SneakyThrows
    @Test
    void async_using_default_executor_service() {

        // when - executing regular action
        runWithNewTransaction(() -> {
            val counter = bookmarkService.lookup(bookmark, Counter.class).orElseThrow();

            val asyncControl = AsyncControl.returning(Counter.class)
                    .with(ForkJoinPool.commonPool());

            wrapperFactory.asyncWrap(counter, asyncControl).increment();

            // let's wait max 5 sec to allow executor to complete before continuing
            asyncControl.waitForResult(5_000, TimeUnit.MILLISECONDS);
            assertTrue(asyncControl.getFuture().isDone()); // verify execution finished
        });

        // then
        runWithNewTransaction(() -> {
            val counter = bookmarkService.lookup(bookmark, Counter.class).orElseThrow();
            assertThat(counter.getNum()).isEqualTo(1L);
        });

        // when - executing mixed-in action
        runWithNewTransaction(() -> {
            val counter = bookmarkService.lookup(bookmark, Counter.class).orElseThrow();
            assertThat(counter.getNum()).isEqualTo(1L);

            val asyncControl = AsyncControl.returning(Counter.class);

            // when
            wrapperFactory.asyncWrapMixin(Counter_bumpUsingMixin.class, counter, asyncControl).act();

            // let's wait max 5 sec to allow executor to complete before continuing
            asyncControl.waitForResult(5_000, TimeUnit.MILLISECONDS);
            assertTrue(asyncControl.getFuture().isDone()); // verify execution finished
        });

        // then
        runWithNewTransaction(() -> {
            val counter = bookmarkService.lookup(bookmark, Counter.class).orElseThrow();
            assertThat(counter).isNotNull();
            assertThat(counter.getNum()).isEqualTo(2L);
        });
    }

}

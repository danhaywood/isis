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
package org.apache.causeway.viewer.restfulobjects.applib;

import org.apache.causeway.viewer.restfulobjects.applib.Rel;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Rel_getName_Test {

    @Test
    public void iana_namespace() throws Exception {
        String name = Rel.SELF.getName();
        assertThat(name, is(equalTo("self")));
    }

    @Test
    public void ro_namespace() throws Exception {
        String name = Rel.DOMAIN_TYPE.getName();
        assertThat(name, is(equalTo("urn:org.restfulobjects:rels/domain-type")));
    }

    @Test
    public void impl_namespace() throws Exception {
        String name = Rel.LAYOUT.getName();
        assertThat(name, is(equalTo("urn:org.apache.causeway.restfulobjects:rels/layout")));
    }
}

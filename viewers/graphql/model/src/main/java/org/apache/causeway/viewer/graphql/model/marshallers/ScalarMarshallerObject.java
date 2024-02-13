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
package org.apache.causeway.viewer.graphql.model.marshallers;

import graphql.Scalars;

import graphql.schema.GraphQLEnumType;

import lombok.val;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.core.config.CausewayConfiguration;

import org.apache.causeway.viewer.graphql.applib.marshallers.ScalarMarshallerAbstract;

import org.apache.causeway.viewer.graphql.model.context.Context;

import org.apache.causeway.viewer.graphql.model.domain.TypeNames;

import org.springframework.stereotype.Component;


/**
 * Acts as a fallback.  We put it last in the list
 */
@Component
@Priority(PriorityPrecedence.LAST)
public class ScalarMarshallerObject extends ScalarMarshallerAbstract<Object> {

    @Inject
    public ScalarMarshallerObject(
            final CausewayConfiguration causewayConfiguration) {
        super(Object.class, Scalars.GraphQLString, causewayConfiguration);
    }

    @Override
    public Object unmarshal(Object graphValue, Class<?> targetType) {
        return graphValue;
    }
}

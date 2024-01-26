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
package org.apache.causeway.viewer.graphql.model.domain;

import org.apache.causeway.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.viewer.graphql.model.context.Context;
import org.apache.causeway.viewer.graphql.model.fetcher.BookmarkedPojo;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectActionParameterProvider;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectActionProvider;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectSpecificationProvider;
import org.apache.causeway.viewer.graphql.model.types.TypeMapper;

import lombok.val;
import lombok.extern.log4j.Log4j2;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

import static org.apache.causeway.viewer.graphql.model.domain.GqlvAction.addGqlArgument;


@Log4j2
public class GqlvActionParamValidate {

    private final Holder holder;
    private final Context context;

    private final GraphQLFieldDefinition field;

    public GqlvActionParamValidate(
            final Holder holder,
            final Context context) {
        this.holder = holder;
        this.context = context;

        val fieldBuilder = newFieldDefinition()
                .name("validate")
                .type(TypeMapper.scalarTypeFor(String.class));
        addGqlArgument(holder.getObjectAction(), fieldBuilder, TypeMapper.InputContext.DISABLE, holder.getParamNum()+1);
        this.field = holder.addField(fieldBuilder.build());
    }


    public void addDataFetcher() {
        context.codeRegistryBuilder.dataFetcher(
                holder.coordinatesFor(field),
                this::disabled
        );
    }

    private String disabled(final DataFetchingEnvironment dataFetchingEnvironment) {

        val sourcePojo = BookmarkedPojo.sourceFrom(dataFetchingEnvironment);
        val objectSpecification = context.specificationLoader.loadSpecification(sourcePojo.getClass());
        if (objectSpecification == null) {
            return "Disabled";
        }

        val objectAction = holder.getObjectAction();
        val managedObject = ManagedObject.adaptSingular(objectSpecification, sourcePojo);
        val actionInteractionHead = objectAction.interactionHead(managedObject);

        val objectActionParameter = objectAction.getParameterById(holder.getObjectActionParameter().getId());
        val argumentManagedObjects = GqlvAction.argumentManagedObjectsFor(dataFetchingEnvironment, objectAction, context.bookmarkService);

        val usable = objectActionParameter.isUsable(actionInteractionHead, argumentManagedObjects, InteractionInitiatedBy.USER);
        return usable.isVetoed() ? usable.getReasonAsString().orElse("Disabled") : null;
    }

    public interface Holder
            extends GqlvHolder,
                    ObjectSpecificationProvider,
                    ObjectActionProvider,
                    ObjectActionParameterProvider {
    }
}

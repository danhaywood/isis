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
package org.apache.isis.viewer.wicket.ui.components.property;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;

import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.viewer.wicket.model.models.ScalarPropertyModel;
import org.apache.isis.viewer.wicket.ui.pages.entity.EntityPage;
import org.apache.isis.viewer.wicket.ui.panels.FormExecutorStrategy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropertyFormExecutorStrategy
implements FormExecutorStrategy<ScalarPropertyModel> {

    private final ScalarPropertyModel propertyModel;

    @Override
    public ScalarPropertyModel getMemberModel() {
        return propertyModel;
    }

    @Override
    public ManagedObject getOwner() {
        return propertyModel.getManagedProperty().getOwner();
    }

    @Override
    public String getReasonInvalidIfAny() {
        return propertyModel.getReasonInvalidIfAny();
    }


    @Override
    public void onExecuteAndProcessResults(final AjaxRequestTarget target) {
        // no-op
    }

    @Override
    public ManagedObject obtainResultAdapter() {
        return propertyModel.applyValueThenReturnOwner();
    }


    @Override
    public void redirectTo(
            final ManagedObject resultAdapter,
            final AjaxRequestTarget target) {

        RequestCycle
            .get()
            .setResponsePage(new EntityPage(propertyModel.getCommonContext(), resultAdapter));
    }


}

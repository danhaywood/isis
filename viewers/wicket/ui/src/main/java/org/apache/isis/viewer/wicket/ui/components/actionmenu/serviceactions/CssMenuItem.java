/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License. */
package org.apache.isis.viewer.wicket.ui.components.actionmenu.serviceactions;

import java.io.Serializable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.Model;

import org.apache.isis.core.commons.internal.base._Strings;
import org.apache.isis.viewer.common.model.menuitem.MenuItemUiModel;
import org.apache.isis.viewer.wicket.ui.components.actionmenu.CssClassFaBehavior;
import org.apache.isis.viewer.wicket.ui.util.Components;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;
import org.apache.isis.viewer.wicket.ui.util.Tooltips;

import lombok.val;

class CssMenuItem 
extends MenuItemUiModel<AbstractLink, CssMenuItem> 
implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ID_MENU_LINK = "menuLink";
    private static final String ID_MENU_LABEL = "menuLabel";
    private static final String ID_SUB_MENU_ITEMS = "subMenuItems";

    public static CssMenuItem newMenuItem(final String name) {
        return new CssMenuItem(name);
    }

    private CssMenuItem(final String name) {
        super(name);
    }
    
    @Override
    protected CssMenuItem newSubMenuItem(final String name) {
        val subMenuItem = newMenuItem(name);
        subMenuItem.setParent(this);
        return subMenuItem;
    }
    
    // //////////////////////////////////////////////////////////////
    // Build wicket components from the menu item.
    // //////////////////////////////////////////////////////////////

    void addTo(final MarkupContainer markupContainer) {

        final Component menuItemComponent = addMenuItemComponentTo(markupContainer);
        addSubMenuItemComponentsIfAnyTo(markupContainer);

        addCssClassAttributesIfRequired(menuItemComponent);
    }

    private Component addMenuItemComponentTo(final MarkupContainer markupContainer) {
        
        val actionUiModel = super.getMenuActionUiModel().getActionLinkUiModel();
        
        final AbstractLink link = actionUiModel.getUiComponent();
        final Label label = new Label(CssMenuItem.ID_MENU_LABEL, Model.of(this.getName()));

        if (link != null) {

            // show link...
            markupContainer.add(link);
            link.add(label);

            if (actionUiModel.getDescription() != null) {
                Tooltips.addTooltip(link, Model.of(actionUiModel.getDescription()));
            }
            if (actionUiModel.isBlobOrClob()) {
                link.add(new CssClassAppender("noVeil"));
            }
            if (actionUiModel.isPrototyping()) {
                link.add(new CssClassAppender("prototype"));
            }

            if (actionUiModel.getCssClass() != null) {
                link.add(new CssClassAppender(actionUiModel.getCssClass()));
            }
            link.add(new CssClassAppender(actionUiModel.getActionIdentifier()));

            String cssClassFa = actionUiModel.getCssClassFa();
            if (!_Strings.isNullOrEmpty(cssClassFa)) {
                label.add(new CssClassFaBehavior(cssClassFa, actionUiModel.getCssClassFaPosition()));
            }

            if (!actionUiModel.isEnabled()) {
                Tooltips.addTooltip(link, actionUiModel.getDisabledReason());
                link.add(new CssClassAppender("disabled"));

                link.setEnabled(false);
            }

            // .. and hide label
            Components.permanentlyHide(markupContainer, CssMenuItem.ID_MENU_LABEL);
            return link;
        }
        else {
            // hide link...
            Components.permanentlyHide(markupContainer, ID_MENU_LINK);
            // ... and show label, along with disabled reason
            Tooltips.addTooltip(link, actionUiModel.getDisabledReason());
            label.add(new AttributeModifier("class", Model.of("disabled")));

            markupContainer.add(label);

            return label;
        }
    }

    private void addSubMenuItemComponentsIfAnyTo(final MarkupContainer menuItemMarkup) {
        val subMenuItems = getSubMenuItems();
        if (subMenuItems.isEmpty()) {
            Components.permanentlyHide(menuItemMarkup, CssMenuItem.ID_SUB_MENU_ITEMS);
        } else {
            menuItemMarkup.add(
                    new CssSubMenuItemsPanel(CssMenuItem.ID_SUB_MENU_ITEMS, subMenuItems));
        }
    }

    private void addCssClassAttributesIfRequired(final Component linkComponent) {
        if (!hasSubMenuItems()) {
            return;
        }
        if (this.hasParent()) {
            linkComponent.add(new CssClassAppender("parent"));
        }
        else {
            linkComponent.add(new CssClassAppender("top-parent"));
        }
    }



}

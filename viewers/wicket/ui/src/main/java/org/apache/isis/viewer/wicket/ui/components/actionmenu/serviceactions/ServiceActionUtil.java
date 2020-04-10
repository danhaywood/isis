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

package org.apache.isis.viewer.wicket.ui.components.actionmenu.serviceactions;

import java.util.List;
import java.util.function.Consumer;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;

import org.apache.isis.applib.services.i18n.TranslationService;
import org.apache.isis.core.commons.internal.base._Strings;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.webapp.context.IsisWebAppCommonContext;
import org.apache.isis.viewer.common.model.action.ActionFactory;
import org.apache.isis.viewer.common.model.menu.MenuUiModel;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.components.actionmenu.CssClassFaBehavior;
import org.apache.isis.viewer.wicket.ui.pages.PageAbstract;
import org.apache.isis.viewer.wicket.ui.util.Confirmations;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;
import org.apache.isis.viewer.wicket.ui.util.Tooltips;

import lombok.val;
import lombok.experimental.UtilityClass;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;

@UtilityClass
//@Log4j2
public final class ServiceActionUtil {

    static void addLeafItem(
            IsisWebAppCommonContext commonContext, 
            CssMenuItem menuItem,
            ListItem<CssMenuItem> listItem,
            MarkupContainer parent) {

        Fragment leafItem = new Fragment("content", "leafItem", parent);

        val actionUiModel = menuItem.getMenuActionUiModel().getActionLinkUiModel();
        val menuItemActionLink = menuItem.getMenuActionUiModel().getActionLinkUiModel().getUiComponent();

        Label menuItemLabel = new Label("menuLinkLabel", menuItem.getName());
        menuItemActionLink.addOrReplace(menuItemLabel);

        listItem.add(new CssClassAppender("isis-" + CssClassAppender.asCssStyle(actionUiModel.getActionIdentifier())));
        if (!actionUiModel.isEnabled()) {
            listItem.add(new CssClassAppender("disabled"));
            menuItemActionLink.setEnabled(false);

            Tooltips.addTooltip(listItem, actionUiModel.getDisabledReason());


        } else {

            if(!_Strings.isNullOrEmpty(actionUiModel.getDescription())) {
                Tooltips.addTooltip(listItem, actionUiModel.getDescription());
            }

            //XXX ISIS-1626, confirmation dialog for no-parameter menu actions
            if (actionUiModel.isRequiresImmediateConfirmation()) {

                val translationService =
                        commonContext.lookupServiceElseFail(TranslationService.class);
                Confirmations
                .addConfirmationDialog(translationService, menuItemActionLink, TooltipConfig.Placement.bottom);
            }

        }
        if (actionUiModel.isPrototyping()) {
            menuItemActionLink.add(new CssClassAppender("prototype"));
        }
        leafItem.add(menuItemActionLink);

        String cssClassFa = actionUiModel.getCssClassFa();
        if (_Strings.isNullOrEmpty(cssClassFa)) {
            menuItemActionLink.add(new CssClassAppender("menuLinkSpacer"));
        } else {
            menuItemLabel.add(new CssClassFaBehavior(cssClassFa, actionUiModel.getCssClassFaPosition()));
        }

        String cssClass = actionUiModel.getCssClass();
        if (!_Strings.isNullOrEmpty(cssClass)) {
            menuItemActionLink.add(new CssClassAppender(cssClass));
        }

        listItem.add(leafItem);
    }

    static void addFolderItem(
            IsisWebAppCommonContext commonContext,
            CssMenuItem subMenuItem,
            ListItem<CssMenuItem> listItem,
            MarkupContainer parent) {

        listItem.add(new CssClassAppender("dropdown-submenu"));

        Fragment folderItem = new Fragment("content", "folderItem", parent);
        listItem.add(folderItem);

        folderItem.add(new Label("folderName", subMenuItem.getName()));
        final List<CssMenuItem> menuItems = subMenuItem.getSubMenuItems();
        ListView<CssMenuItem> subMenuItemsView = new ListView<CssMenuItem>("subMenuItems",
                menuItems) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<CssMenuItem> listItem) {
                CssMenuItem subMenuItem = listItem.getModelObject();

                if (subMenuItem.hasSubMenuItems()) {
                    addFolderItem(commonContext, subMenuItem, listItem, parent);
                } else {
                    addLeafItem(commonContext, subMenuItem, listItem, parent);
                }
            }
        };
        folderItem.add(subMenuItemsView);
    }


    private static class MenuActionFactoryWkt implements ActionFactory<AbstractLink> {

        @Override
        public MenuActionWkt newAction(
                IsisWebAppCommonContext commonContext, 
                String named, 
                ManagedObject serviceAction,
                ObjectAction objectAction) {

            val objectModel = EntityModel.ofAdapter(commonContext, serviceAction);

            return new MenuActionWkt(
                    new MenuActionLinkFactory(PageAbstract.ID_MENU_LINK, objectModel), 
                    named, 
                    objectAction,
                    objectModel);
        }

    }

    public static void buildMenu(
            final IsisWebAppCommonContext commonContext,
            final MenuUiModel menuUiModel,
            final Consumer<CssMenuItem> onNewMenuItem) {

        menuUiModel.buildMenuItems(
                commonContext, 
                new MenuActionFactoryWkt(),
                CssMenuItem::newMenuItem,
                onNewMenuItem);
    }

}

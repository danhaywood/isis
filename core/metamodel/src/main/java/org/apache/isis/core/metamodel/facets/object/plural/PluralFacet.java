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

package org.apache.isis.core.metamodel.facets.object.plural;

import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.all.i8n.HasTranslation;
import org.apache.isis.core.metamodel.facets.object.icon.IconFacet;
import org.apache.isis.core.metamodel.facets.object.title.TitleFacet;

/**
 * Mechanism for obtaining the plural title of an instance of a class, used to
 * label a collection of a certain class.
 *
 * <p>
 * In the standard Apache Isis Programming Model, typically corresponds to a
 * method named <tt>pluralName</tt>. If no plural name is provided, then the
 * framework will attempt to guess the plural name (by adding an <i>s</i> or
 * <i>ies</i> suffix).
 *
 * @see IconFacet
 * @see TitleFacet
 */
public interface PluralFacet extends Facet, HasTranslation {

    /**
     * Originating text to be translated before use with the UI.
     */
    String text();

    String translated();

}

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

package org.apache.isis.core.metamodel.facetapi;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.isis.core.metamodel.context.HasMetaModelContext;

import lombok.NonNull;
import lombok.val;

/**
 * Anything in the metamodel (which also includes peers in the reflector) that
 * can be extended.
 */
public interface FacetHolder extends HasMetaModelContext {

    int getFacetCount();

    /**
     * Get the facet of the specified type (as per the type it reports from
     * {@link Facet#facetType()}).
     */
    <T extends Facet> T getFacet(Class<T> facetType);

    // -- LOOKUP

    default <T extends Facet> Optional<T> lookupFacet(
            @NonNull final Class<T> facetType) {
        return Optional.ofNullable(getFacet(facetType));
    }

    default <T extends Facet> Optional<T> lookupFacet(
            @NonNull final Class<T> facetType,
            @NonNull final Predicate<T> filter) {
        return lookupFacet(facetType).map(facet->filter.test(facet) ? facet : null);
    }

    default <T extends Facet> Optional<T> lookupNonFallbackFacet(
            @NonNull final Class<T> facetType) {
        return lookupFacet(facetType, facet->!facet.getPrecedence().isFallback());
    }

    // -- CONTAINS

    /**
     * Whether there is a facet registered of the specified type.
     */
    boolean containsFacet(Class<? extends Facet> facetType);

    /**
     * Whether there is a facet registered of the specified type that is not a
     * {@link Facet.Precedence#isFallback() fallback} .
     * <p>
     * Convenience; saves having to {@link #getFacet(Class)} and then check if
     * <tt>null</tt> and not a fallback.
     */
    default boolean containsNonFallbackFacet(Class<? extends Facet> facetType) {
        val facet = getFacet(facetType);
        return facet != null
                && !facet.getPrecedence().isFallback();
    }

    /**
     * As {@link #containsNonFallbackFacet(Class)}, with additional requirement, that the
     * facet is <i>explicit</i>, not {@link Facet.Precedence#isInferred() derived}.
     */
    default boolean containsExplicitNonFallbackFacet(Class<? extends Facet> facetType) {
        val facet = getFacet(facetType);
        return facet != null
                && !facet.getPrecedence().isFallback()
                && !facet.getPrecedence().isInferred();
    }

    Stream<Facet> streamFacets();

    default <F extends Facet> Stream<F> streamFacets(Class<F> requiredType) {
        return streamFacets()
                .filter(facet->requiredType.isAssignableFrom(facet.getClass()))
                .map(requiredType::cast);
    }

    /**
     * Adds the facet, extracting its {@link Facet#facetType() type} as the key.
     *
     * <p>
     * Any previously added facet of the same type will be overwritten,
     * when given {@link Facet} has equal or higher precedence.
     * Otherwise is ignored.
     */
    void addFacet(@NonNull Facet facet);

    // -- VALIDATION SUPPORT

    Optional<FacetRanking> getFacetRanking(Class<? extends Facet> facetType);

}

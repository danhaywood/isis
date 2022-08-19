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
package org.apache.isis.viewer.restfulobjects.rendering.domainobjects;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.databind.node.NullNode;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import org.apache.isis.applib.annotation.PriorityPrecedence;
import org.apache.isis.applib.exceptions.recoverable.TextEntryParseException;
import org.apache.isis.applib.value.semantics.ValueDecomposition;
import org.apache.isis.commons.internal.base._Casts;
import org.apache.isis.commons.internal.collections._Maps;
import org.apache.isis.core.metamodel.facets.object.value.ValueSerializer.Format;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.metamodel.spec.ManagedObjects;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.metamodel.util.Facets;
import org.apache.isis.schema.common.v2.ValueType;
import org.apache.isis.viewer.restfulobjects.applib.IsisModuleViewerRestfulObjectsApplib;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.rendering.domainobjects.JsonValueConverter.Context;

import lombok.NonNull;
import lombok.val;
import lombok.extern.log4j.Log4j2;

/**
 * Similar to Isis' value encoding, but with additional support for JSON
 * primitives.
 */
@Service
@Named(IsisModuleViewerRestfulObjectsApplib.NAMESPACE + ".JsonValueEncoder")
@Priority(PriorityPrecedence.EARLY)
@Qualifier("Default")
@Log4j2
public class JsonValueEncoder {

    @Inject private SpecificationLoader specificationLoader;

    private Map<Class<?>, JsonValueConverter> converterByClass = _Maps.newLinkedHashMap();

    @PostConstruct
    public void init() {
        new JsonValueConverters().asList()
            .forEach(converter->converterByClass.put(converter.getValueClass(), converter));
    }

    public ManagedObject asAdapter(
            final ObjectSpecification objectSpec,
            final JsonRepresentation argValueRepr,
            final JsonValueConverter.Context context) {

        if(argValueRepr == null) {
            return null;
        }
        if (objectSpec == null) {
            throw new IllegalArgumentException("ObjectSpecification is required");
        }
        if (!argValueRepr.isValue()) {
            throw new IllegalArgumentException("Representation must be of a value");
        }

        val valueClass = objectSpec.getCorrespondingClass();
        val valueSerializer =
                Facets.valueSerializerElseFail(objectSpec, valueClass);

        final JsonValueConverter jvc = converterByClass.get(ClassUtils.resolvePrimitiveIfNecessary(valueClass));
        if(jvc == null) {
            // best effort
            if (argValueRepr.isString()) {
                final String argStr = argValueRepr.asString();
                return ManagedObject.of(objectSpec,
                        valueSerializer.fromEncodedString(Format.JSON, argStr));
            }
            throw new IllegalArgumentException("Unable to parse value");
        }

        val valueAsPojo = jvc.recoverValueAsPojo(argValueRepr, context);
        if(valueAsPojo != null) {
            return ManagedObject.lazy(specificationLoader, valueAsPojo);
        }

        // last attempt
        if (argValueRepr.isString()) {
            final String argStr = argValueRepr.asString();
            try {
                return ManagedObject.of(objectSpec,
                        valueSerializer.fromEncodedString(Format.JSON, argStr));
            } catch(TextEntryParseException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }

        throw new IllegalArgumentException("Could not parse value '" + argValueRepr.asString() + "' as a " + objectSpec.getFullIdentifier());
    }

    public void appendValueAndFormat(
            final ManagedObject valueAdapter,
            final JsonRepresentation repr,
            final Context context) {

        val valueSpec = valueAdapter.getSpecification();
        val valueClass = valueSpec.getCorrespondingClass();
        val jsonValueConverter = converterByClass.get(valueClass);
        if(jsonValueConverter != null) {
            jsonValueConverter.appendValueAndFormat(valueAdapter, context, repr);
            return;
        } else {
            final Optional<ValueDecomposition> valueDecompositionIfAny = decompose(valueAdapter);
            if(valueDecompositionIfAny.isPresent()) {
                val valueDecomposition = valueDecompositionIfAny.get();
                val valueAsJson = valueDecomposition.toJson();
                valueDecomposition.accept(
                        simple->{
                            // special treatment for BLOB/CLOB/ENUM as these are better represented by a map
                            if(simple.getType() == ValueType.BLOB
                                    || simple.getType() == ValueType.CLOB
                                    || simple.getType() == ValueType.ENUM) {
                                val decompRepr = JsonRepresentation.jsonAsMap(valueAsJson);
                                repr.mapPutJsonRepresentation("value", decompRepr);
                                appendFormats(repr, null, simple.getType().value(), context.isSuppressExtensions());
                            } else {
                                // using string representation from value semantics
                                repr.mapPutString("value", valueAsJson);
                                appendFormats(repr, "string", simple.getType().value(), context.isSuppressExtensions());
                            }
                        },
                        tuple->{
                            val decompRepr = JsonRepresentation.jsonAsMap(valueAsJson);
                            repr.mapPutJsonRepresentation("value", decompRepr);

                            val typeTupleAsFormat = "{"
                                    + tuple.getElements().stream()
                                        .map(el->el.getType().value())
                                        .collect(Collectors.joining(","))
                                    + "}";

                            appendFormats(repr, null, typeTupleAsFormat, context.isSuppressExtensions());
                        });
            } else {
                appendNullAndFormat(repr, context.isSuppressExtensions());
            }
        }
    }

    private NullNode appendNullAndFormat(final JsonRepresentation repr, final boolean suppressExtensions) {
        val value = NullNode.getInstance();
        repr.mapPutJsonNode("value", value);
        appendFormats(repr, "string", "string", suppressExtensions);
        return value;
    }

    private static Optional<ValueDecomposition> decompose(final ManagedObject valueAdapter) {
        if(ManagedObjects.isNullOrUnspecifiedOrEmpty(valueAdapter)) {
            return Optional.empty();
        }
        val valueClass = valueAdapter.getSpecification().getCorrespondingClass();
        val decompositionIfAny = Facets.valueDefaultSemantics(valueAdapter.getSpecification(), valueClass)
                .map(composer->composer.decompose(_Casts.uncheckedCast(valueAdapter.getPojo())));
        if(decompositionIfAny.isEmpty()) {
            val valueSpec = valueAdapter.getSpecification();
            log.warn("{Could not resolve a ValueComposer for {}, "
                    + "falling back to rendering as 'null'. "
                    + "Make sure the framework has access to a ValueSemanticsProvider<{}> "
                    + "that implements ValueComposer<{}>}",
                    valueSpec.getLogicalTypeName(),
                    valueSpec.getCorrespondingClass().getSimpleName(),
                    valueSpec.getCorrespondingClass().getSimpleName());
        }
        return decompositionIfAny;
    }

    @Nullable
    public Object asObject(final @NonNull ManagedObject adapter, final JsonValueConverter.Context context) {

        val objectSpec = adapter.getSpecification();
        val cls = objectSpec.getCorrespondingClass();

        val jsonValueConverter = converterByClass.get(cls);
        if(jsonValueConverter != null) {
            return jsonValueConverter.asObject(adapter, context);
        }

        // else
        return Facets.valueSerializerElseFail(objectSpec, cls)
                .toEncodedString(Format.JSON, _Casts.uncheckedCast(adapter.getPojo()));
    }

    static void appendFormats(final JsonRepresentation repr,
            final @Nullable String format, final @Nullable String extendedFormat, final boolean suppressExtensions) {
        repr.putFormat(format);
        if(!suppressExtensions) {
            repr.putExtendedFormat(extendedFormat);
        }
    }

    // -- NESTED TYPE DECLARATIONS

    public static class ExpectedStringRepresentingValueException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
    }

    /**
     * JUnit support
     */
    public static JsonValueEncoder forTesting(final SpecificationLoader specificationLoader) {
        val jsonValueEncoder = new JsonValueEncoder();
        jsonValueEncoder.specificationLoader = specificationLoader;
        jsonValueEncoder.init();
        return jsonValueEncoder;
    }

}

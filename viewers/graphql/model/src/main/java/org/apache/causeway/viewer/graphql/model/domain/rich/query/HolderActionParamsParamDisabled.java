package org.apache.causeway.viewer.graphql.model.domain.rich.query;

import graphql.schema.GraphQLFieldDefinition;

import org.apache.causeway.applib.services.bookmark.BookmarkService;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAction;
import org.apache.causeway.viewer.graphql.model.domain.Environment;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectActionParameterProvider;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectActionProvider;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectSpecificationProvider;
import org.apache.causeway.viewer.graphql.model.types.TypeMapper;

public interface HolderActionParamsParamDisabled
        extends ObjectSpecificationProvider,
        ObjectActionProvider,
        ObjectActionParameterProvider {
    void addGqlArguments(
            ObjectAction objectAction,
            GraphQLFieldDefinition.Builder fieldBuilder,
            TypeMapper.InputContext inputContext,
            int i);

    Can<ManagedObject> argumentManagedObjectsFor(
            Environment environment,
            ObjectAction objectAction,
            BookmarkService bookmarkService);
}

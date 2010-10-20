package org.apache.isis.extensions.restful.viewer.facets;

import nu.xom.Element;

import org.apache.isis.extensions.restful.viewer.xom.ResourceContext;
import org.apache.isis.metamodel.facets.Facet;
import org.apache.isis.metamodel.interactions.HidingInteractionAdvisor;


public final class TableColumnFacetHiding extends TableColumnFacet {
    public TableColumnFacetHiding(final ResourceContext resourceContext) {
        super("Hiding", resourceContext);
    }

    @Override
    public Element doTd(final Facet facet) {
        return xhtmlRenderer.p(facet instanceof HidingInteractionAdvisor, null);
    }
}

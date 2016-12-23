package org.vertexium.elasticsearch2;

import org.vertexium.Element;
import org.vertexium.PropertyDefinition;

public interface IndexSelectionStrategy {
    String[] getIndicesToQuery(Elasticsearch2SearchIndex es);

    String getIndexName(Elasticsearch2SearchIndex es, Element element);

    String[] getIndexNames(Elasticsearch2SearchIndex es, PropertyDefinition propertyDefinition);

    boolean isIncluded(Elasticsearch2SearchIndex es, String indexName);

    String[] getManagedIndexNames(Elasticsearch2SearchIndex es);

    String[] getIndicesToQuery(ElasticsearchSearchQueryBase query, ElasticsearchElementType elementType);
}

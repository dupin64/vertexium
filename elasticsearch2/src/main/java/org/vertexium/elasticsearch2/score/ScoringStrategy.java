package org.vertexium.elasticsearch2.score;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.vertexium.*;
import org.vertexium.elasticsearch2.Elasticsearch2SearchIndex;
import org.vertexium.elasticsearch2.IndexInfo;
import org.vertexium.search.SearchIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ScoringStrategy {
    private final GraphConfiguration graphConfiguration;

    protected ScoringStrategy(GraphConfiguration graphConfiguration) {
        this.graphConfiguration = graphConfiguration;
    }

    protected GraphConfiguration getGraphConfiguration() {
        return graphConfiguration;
    }

    public abstract void addElement(SearchIndex searchIndex, Graph graph, Element element, Authorizations authorizations);

    public abstract boolean addFieldsToVertexDocument(SearchIndex searchIndex, XContentBuilder jsonBuilder, Vertex vertex, GetResponse existingDocument, Authorizations authorizations) throws IOException;

    public abstract boolean addFieldsToEdgeDocument(SearchIndex searchIndex, XContentBuilder jsonBuilder, Edge edge, GetResponse existingDocument, Authorizations authorizations) throws IOException;

    public abstract int addElement(Elasticsearch2SearchIndex searchIndex, Graph graph, BulkRequest bulkRequest, IndexInfo indexInfo, Element element, Authorizations authorizations);

    public abstract void addFieldsToElementType(XContentBuilder builder) throws IOException;

    public abstract QueryBuilder updateQuery(QueryBuilder query);

    public List<String> getFieldNames() {
        return new ArrayList<>();
    }
}

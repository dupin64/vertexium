package org.vertexium.elasticsearch2;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.vertexium.Authorizations;
import org.vertexium.Direction;
import org.vertexium.Graph;
import org.vertexium.Vertex;
import org.vertexium.elasticsearch2.score.ScoringStrategy;
import org.vertexium.query.VertexQuery;

import java.util.List;

import static org.vertexium.util.IterableUtils.toArray;

public class ElasticsearchSearchVertexQuery extends ElasticsearchSearchQueryBase implements VertexQuery {
    private final Vertex sourceVertex;

    public ElasticsearchSearchVertexQuery(
            Client client,
            Graph graph,
            Vertex sourceVertex,
            String queryString,
            ScoringStrategy scoringStrategy,
            IndexSelectionStrategy indexSelectionStrategy,
            int pageSize,
            Authorizations authorizations
    ) {
        super(client, graph, queryString, scoringStrategy, indexSelectionStrategy, pageSize, authorizations);
        this.sourceVertex = sourceVertex;
    }

    @Override
    protected List<QueryBuilder> getFilters(ElasticsearchElementType elementType) {
        List<QueryBuilder> results = super.getFilters(elementType);
        if (elementType.equals(ElasticsearchElementType.VERTEX)) {
            List<String> edgeLabels = getParameters().getEdgeLabels();
            String[] edgeLabelsArray = edgeLabels == null || edgeLabels.size() == 0
                    ? null
                    : edgeLabels.toArray(new String[edgeLabels.size()]);
            Iterable<String> vertexIds = sourceVertex.getVertexIds(
                    Direction.BOTH,
                    edgeLabelsArray,
                    getParameters().getAuthorizations()
            );
            String[] ids = toArray(vertexIds, String.class);
            results.add(QueryBuilders.idsQuery().ids(ids));
        } else if (elementType.equals(ElasticsearchElementType.EDGE)) {
            QueryBuilder inVertexIdFilter = QueryBuilders.termQuery(Elasticsearch2SearchIndex.IN_VERTEX_ID_FIELD_NAME, sourceVertex.getId());
            QueryBuilder outVertexIdFilter = QueryBuilders.termQuery(Elasticsearch2SearchIndex.OUT_VERTEX_ID_FIELD_NAME, sourceVertex.getId());
            results.add(QueryBuilders.orQuery(inVertexIdFilter, outVertexIdFilter));
        }
        return results;
    }
}

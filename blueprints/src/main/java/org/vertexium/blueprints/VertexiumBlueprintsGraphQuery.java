package org.vertexium.blueprints;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Vertex;
import org.vertexium.Authorizations;

public class VertexiumBlueprintsGraphQuery extends VertexiumBlueprintsQuery implements GraphQuery {
    private VertexiumBlueprintsGraph graph;
    private final org.vertexium.query.GraphQuery q;
    private Authorizations authorizations;

    public VertexiumBlueprintsGraphQuery(VertexiumBlueprintsGraph graph, Authorizations authorizations) {
        this.graph = graph;
        this.q = graph.getGraph().query(authorizations);
        this.authorizations = authorizations;
    }

    @Override
    public GraphQuery has(String key) {
        this.q.has(key);
        return this;
    }

    @Override
    public GraphQuery hasNot(String key) {
        this.q.hasNot(key);
        return this;
    }

    @Override
    public GraphQuery has(String key, Object value) {
        this.q.has(key, value);
        return this;
    }

    @Override
    public GraphQuery hasNot(String key, Object value) {
        this.q.hasNot(key, value);
        return this;
    }

    @Override
    public GraphQuery has(String key, Predicate predicate, Object value) {
        org.vertexium.query.Predicate vertexiumPredicate = toVertexiumPredicate(predicate);
        this.q.has(key, vertexiumPredicate, value);
        return this;
    }

    @Override
    @Deprecated
    public <T extends Comparable<T>> GraphQuery has(String key, T value, Compare compare) {
        org.vertexium.query.Predicate vertexiumPredicate = toVertexiumPredicate(compare);
        this.q.has(key, vertexiumPredicate, value);
        return this;
    }

    @Override
    public <T extends Comparable<?>> GraphQuery interval(String key, T startValue, T endValue) {
        this.q.range(key, startValue, true, endValue, false);
        return this;
    }

    @Override
    public GraphQuery limit(int limit) {
        q.limit(limit);
        return this;
    }

    @Override
    public Iterable<Edge> edges() {
        Iterable<org.vertexium.Edge> edges = q.edges();
        return VertexiumBlueprintsConvert.toBlueprintsEdges(graph, edges, authorizations);
    }

    @Override
    public Iterable<Vertex> vertices() {
        Iterable<org.vertexium.Vertex> vertices = q.vertices();
        return VertexiumBlueprintsConvert.toBlueprintsVertices(graph, vertices, authorizations);
    }
}

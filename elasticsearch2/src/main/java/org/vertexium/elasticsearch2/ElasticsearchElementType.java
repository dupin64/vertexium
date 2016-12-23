package org.vertexium.elasticsearch2;

import org.vertexium.VertexiumException;

public enum ElasticsearchElementType {
    VERTEX("vertex"),
    EDGE("edge");

    private final String key;

    ElasticsearchElementType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ElasticsearchElementType parse(String s) {
        if (s.equals(VERTEX.getKey())) {
            return VERTEX;
        } else if (s.equals(EDGE.getKey())) {
            return EDGE;
        }
        throw new VertexiumException("Could not parse element type: " + s);
    }
}

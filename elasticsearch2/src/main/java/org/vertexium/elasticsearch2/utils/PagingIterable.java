package org.vertexium.elasticsearch2.utils;

import org.vertexium.Element;
import org.vertexium.VertexiumException;
import org.vertexium.elasticsearch2.ElasticsearchGraphQueryIterable;
import org.vertexium.query.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class PagingIterable<T extends Element> implements
        Iterable<T>,
        IterableWithTotalHits<T>,
        IterableWithScores<T>,
        IterableWithHistogramResults<T>,
        IterableWithTermsResults<T>,
        IterableWithGeohashResults<T>,
        IterableWithStatisticsResults<T>,
        QueryResultsIterable<T> {
    private final long skip;
    private final Long limit;
    private boolean isFirstCallToIterator;
    private final ElasticsearchGraphQueryIterable<T> firstIterable;
    private final int pageSize;

    public PagingIterable(long skip, Long limit, int pageSize) {
        this.skip = skip;
        this.limit = limit;
        this.pageSize = pageSize;

        // This is a bit of a hack. Because the underlying iterable is the iterable with geohash results, histogram results, etc.
        //   we need to grab the first iterable to get the results out.
        int firstIterableLimit = Math.min(pageSize, limit == null ? Integer.MAX_VALUE : limit.intValue());
        this.firstIterable = getPageIterable((int) this.skip, firstIterableLimit, true);
        this.isFirstCallToIterator = true;
    }

    @Override
    public GeohashResult getGeohashResults(String name) {
        return this.firstIterable.getGeohashResults(name);
    }

    @Override
    public HistogramResult getHistogramResults(String name) {
        return this.firstIterable.getHistogramResults(name);
    }

    @Override
    public StatisticsResult getStatisticsResults(String name) {
        return this.firstIterable.getStatisticsResults(name);
    }

    @Override
    public TermsResult getTermsResults(String name) {
        return this.firstIterable.getTermsResults(name);
    }

    @Override
    public Map<String, Double> getScores() {
        return this.firstIterable.getScores();
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public <TResult extends AggregationResult> TResult getAggregationResult(String name, Class<? extends TResult> resultType) {
        return this.firstIterable.getAggregationResult(name, resultType);
    }

    @Override
    public long getTotalHits() {
        return this.firstIterable.getTotalHits();
    }

    protected abstract ElasticsearchGraphQueryIterable<T> getPageIterable(int skip, int limit, boolean includeAggregations);

    @Override
    public Iterator<T> iterator() {
        MyIterator<T> it = new MyIterator<>(isFirstCallToIterator ? firstIterable : null, skip, limit, pageSize, new GetPageIterableFunction<T>() {
            @Override
            public ElasticsearchGraphQueryIterable<T> getPageIterable(int skip, int limit, boolean includeAggregations) {
                return PagingIterable.this.getPageIterable(skip, limit, includeAggregations);
            }
        });
        isFirstCallToIterator = false;
        return it;
    }

    private interface GetPageIterableFunction<T extends Element> {
        ElasticsearchGraphQueryIterable<T> getPageIterable(int skip, int limit, boolean includeAggregations);
    }

    private static class MyIterator<T extends Element> implements Iterator<T> {
        private ElasticsearchGraphQueryIterable<T> firstIterable;
        private final int pageSize;
        private final GetPageIterableFunction<T> getPageIterableFunction;
        private int nextSkip;
        private int limit;
        private int currentIteratorCount;
        private Iterator<T> currentIterator;

        public MyIterator(ElasticsearchGraphQueryIterable<T> firstIterable, long skip, Long limit, int pageSize, GetPageIterableFunction<T> getPageIterableFunction) {
            this.firstIterable = firstIterable;
            this.pageSize = pageSize;
            this.getPageIterableFunction = getPageIterableFunction;
            this.nextSkip = (int) skip;
            this.limit = (int) (limit == null ? Integer.MAX_VALUE : limit);
            this.currentIterator = getNextIterator();
        }

        @Override
        public boolean hasNext() {
            while (true) {
                if (currentIterator == null) {
                    if (currentIteratorCount == 0) {
                        return false;
                    }
                    currentIterator = getNextIterator();
                    if (currentIterator == null) {
                        return false;
                    }
                }
                if (currentIterator.hasNext()) {
                    return true;
                }
                currentIterator = null;
            }
        }

        @Override
        public T next() {
            if (hasNext()) {
                limit--;
                currentIteratorCount++;
                return currentIterator.next();
            }
            throw new NoSuchElementException();
        }

        private Iterator<T> getNextIterator() {
            if (limit <= 0) {
                return null;
            }
            int limit = Math.min(pageSize, this.limit);
            currentIteratorCount = 0;
            if (firstIterable == null) {
                firstIterable = getPageIterableFunction.getPageIterable(nextSkip, limit, false);
            }
            Iterator<T> it = firstIterable.iterator();
            firstIterable = null;
            nextSkip += limit;
            return it;
        }

        @Override
        public void remove() {
            throw new VertexiumException("remove not implemented");
        }
    }
}

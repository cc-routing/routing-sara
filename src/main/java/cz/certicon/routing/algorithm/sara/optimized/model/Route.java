package cz.certicon.routing.algorithm.sara.optimized.model;


import cz.certicon.routing.model.graph.Metric;
import gnu.trove.list.array.TLongArrayList;
import java8.util.function.LongToDoubleFunction;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Route {

    private final long[] edges;

    public Route( long[] edges ) {
        this.edges = edges;
    }

    public long[] getEdges() {
        return edges;
    }

    public double calculateDistance( final OptimizedGraph graph, final Metric metric ) {
        return java8.util.J8Arrays.stream( getEdges() ).mapToDouble(
                new LongToDoubleFunction() {
                    @Override
                    public double applyAsDouble( long edgeId ) {
                        return graph.getDistance( graph.getEdgeById( edgeId ), metric );
                    }
                }
        ).sum();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TLongArrayList edges = new TLongArrayList();

        public Builder() {
        }

        public Builder edge( long edge ) {
            edges.add( edge );
            return this;
        }

        public Builder edges( long[] edges ) {
            this.edges.add( edges );
            return this;
        }

        public Route build() {
            return new Route( edges.toArray() );
        }

        public Route buildReverse() {
            TLongArrayList reversed = new TLongArrayList( edges );
            reversed.reverse();
            return new Route( reversed.toArray() );
        }
    }
}

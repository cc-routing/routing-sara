package cz.certicon.routing.algorithm.sara.optimized.model;


import gnu.trove.list.array.TLongArrayList;
import lombok.Builder;

import java.util.Arrays;

/**
 * Created by blaha on 26.10.2016.
 */
public class Route {

    private final long[] edges;

    public Route( long[] edges ) {
        this.edges = edges;
    }

    public long[] getEdges() {
        return edges;
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

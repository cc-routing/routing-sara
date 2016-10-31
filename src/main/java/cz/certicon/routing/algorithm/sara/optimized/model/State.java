package cz.certicon.routing.algorithm.sara.optimized.model;

/**
 * Created by blaha on 27.10.2016.
 */
public class State {
    public final int node;
    public final int edge;
    private final int hashCode;

    public State( int node, int edge ) {
        this.node = node;
        this.edge = edge;
        this.hashCode = 31 * node + edge;
    }

    public boolean isFirst() {
        return edge < 0;
    }

    @Override
    public boolean equals( Object o ) {
        State state = (State) o;
        return state.node == node && state.edge == edge;
//        if ( this == o ) return true;
//        if ( o == null || getClass() != o.getClass() ) return false;
//
//        State state = (State) o;
//
//        if ( node != state.node ) return false;
//        return edge == state.edge;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}

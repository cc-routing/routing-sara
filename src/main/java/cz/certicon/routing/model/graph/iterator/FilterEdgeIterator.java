/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.iterator;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.utils.collections.Iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract filtering edge iterator. Filteres iterated edges based on the provided predicate implementation.
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public abstract class FilterEdgeIterator<N extends Node, E extends Edge> implements Iterator<E> {

    private final int last;
    private final N node;
    private int position = -1;
    private int nextPosition = -1;
    private final List<E> edges;

    /**
     * Constructor for node and collection of edges.
     * Performance note: collection forces this iterator to create a new list
     *
     * @param node  node
     * @param edges all the edges
     */
    public FilterEdgeIterator( N node, Collection<E> edges ) {
        this.node = node;
        this.edges = new ArrayList<>( edges );
        this.last = getLast( this.node, this.edges );
    }

    /**
     * Constructor for node and collection of edges.
     * Safety note: defensive copy is NOT created for performance reasons
     *
     * @param node  node
     * @param edges all the edges
     */
    public FilterEdgeIterator( N node, List<E> edges ) {
        this.node = node;
        this.edges = edges;
        this.last = getLast( this.node, this.edges );
    }

    private int getLast( N node, List<E> edges ) {
        int tmpLast = -1;
        for ( int i = edges.size() - 1; i >= 0; i-- ) {
            E edge = edges.get( i );
            if ( isValid( node, edge ) ) {
                tmpLast = i;
                break;
            }
        }
        return tmpLast;
    }

    @Override
    public boolean hasNext() {
        if ( position + 1 > last ) {
            return false;
        }
        if ( nextPosition < 0 ) {
            int tmpPosition = position + 1;
            E edge = edges.get( tmpPosition );
            while ( tmpPosition < edges.size() - 1 && !isValid( node, edge ) ) {
                tmpPosition++;
                edge = edges.get( tmpPosition );
            }
            if ( tmpPosition < edges.size() ) {
                nextPosition = tmpPosition;
                return true;
            } else {
                position = tmpPosition;
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public E next() {
        if ( hasNext() ) {
            position = nextPosition;
            this.nextPosition = -1;
            return edges.get( position );
        } else {
            throw new IllegalStateException( "No more egdes: call hasNext before asking for next edge!" );
        }
    }

    @Override
    public java.util.Iterator<E> iterator() {
        return this;
    }

    abstract boolean isValid( N node, E edge );

    @Override
    public void remove() {
        throw new UnsupportedOperationException( "Remove not supported" );
    }

}

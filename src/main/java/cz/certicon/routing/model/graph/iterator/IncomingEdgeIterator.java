/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.iterator;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;

import java.util.Collection;
import java.util.List;

/**
 * An implementation of the {@link FilterEdgeIterator} filtering incoming edges.
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class IncomingEdgeIterator<N extends Node, E extends Edge> extends FilterEdgeIterator<N, E> {

    /**
     * Constructor for node and collection of edges.
     * Performance note: collection forces this iterator to create a new list
     *
     * @param node  node
     * @param edges all the edges
     */
    public IncomingEdgeIterator( N node, Collection<E> edges ) {
        super( node, edges );
    }

    /**
     * Constructor for node and collection of edges.
     * Safety note: defensive copy is NOT created for performance reasons
     *
     * @param node  node
     * @param edges all the edges
     */
    public IncomingEdgeIterator( N node, List<E> edges ) {
        super( node, edges );
    }

    @Override
    boolean isValid( N node, E edge ) {
        return edge != null && ( !edge.isOneWay() || edge.getTarget().equals( node ) );
    }

}

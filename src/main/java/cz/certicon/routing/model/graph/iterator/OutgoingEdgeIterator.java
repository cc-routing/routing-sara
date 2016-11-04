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
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 * @param <N> node type
 * @param <E> edge type
 */
public class OutgoingEdgeIterator<N extends Node, E extends Edge> extends FilterEdgeIterator<N, E> {

    public OutgoingEdgeIterator( N node, Collection<E> edges ) {
        super( node, edges );
    }

    public OutgoingEdgeIterator( N node, List<E> edges ) {
        super( node, edges );
    }

    @Override
    boolean isValid( N node, E edge ) {
        return edge != null && ( !edge.isOneWay() || edge.getSource().equals( node ) );
    }

}

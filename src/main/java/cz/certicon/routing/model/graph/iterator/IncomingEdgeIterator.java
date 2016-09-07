/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.iterator;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <N> node type
 * @param <E> edge type
 */
public class IncomingEdgeIterator<N extends Node, E extends Edge> extends FilterEdgeIterator<N, E> {

    public IncomingEdgeIterator( Graph<N, E> graph, N node, Collection<E> edges ) {
        super( graph, node, edges );
    }

    public IncomingEdgeIterator( Graph<N, E> graph, N node, List<E> edges ) {
        super( graph, node, edges );
    }

    @Override
    boolean isValid( Graph<N, E> graph, N node, E edge ) {
        return edge != null && ( !edge.isOneWay( graph ) || edge.getTarget( graph ).equals( node ) );
    }

}

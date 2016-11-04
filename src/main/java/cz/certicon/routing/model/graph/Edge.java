/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.values.Distance;

/**
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public interface Edge<N extends Node, E extends Edge> extends Identifiable {

    /**
     * Returns source node of this edge
     *
     * @return source node
     */
    N getSource();

    /**
     * Returns target node of this edge
     *
     * @return target node
     */
    N getTarget();

    /**
     * Returns other node (node on the opposite side from the given node) of this edge
     *
     * @param node given node
     * @return the other node
     */
    N getOtherNode( N node );

    Distance getTurnDistance( N node, TurnTable turnTable, E targetEdge );

    boolean isOneWay();

    int getSourcePosition();

    int getTargetPosition();

    Distance getLength( Metric metric );

    void setLength( Metric metric, Distance distance );

    boolean isSource( N node );

    boolean isTarget( N node );

    E copy( Graph<N, E> newGraph, N newSource, N newTarget );

    void lock();
}

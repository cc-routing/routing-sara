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
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
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

    /**
     * Returns turn-distance for the given node, its turntable and target edge
     *
     * @param node       given crossroad node
     * @param turnTable  node's turn-table
     * @param targetEdge target edge
     * @return turn-distance from this edge to target edge via node
     */
    Distance getTurnDistance( N node, TurnTable turnTable, E targetEdge );

    /**
     * Returns true if this edge is one-way only
     *
     * @return true if this edge is one-way only, false otherwise
     */
    boolean isOneWay();

    /**
     * Returns position in turn-table of the source node
     *
     * @return position in turn-table of the source node
     */
    int getSourcePosition();

    /**
     * Returns position in the turn-table of the target node
     *
     * @return position in the turn-table of the target node
     */
    int getTargetPosition();

    /**
     * Returns length for the given metric
     *
     * @param metric given metric
     * @return length for the given metric
     */
    Distance getLength( Metric metric );

    /**
     * Sets length for the given metric
     *
     * @param metric   given metric
     * @param distance given length
     */
    void setLength( Metric metric, Distance distance );

    /**
     * Returns whether the given node is source of this edge or not
     *
     * @param node given node
     * @return true if the given node is source of this edge, false otherwise
     */
    boolean isSource( N node );

    /**
     * Returns whether the given node is target of this edge or not
     *
     * @param node given node
     * @return true if the given node is target of this edge, false otherwise
     */
    boolean isTarget( N node );

    /**
     * Creates a copy of this edge inside the new graph for the new source and new target
     *
     * @param newGraph  new graph
     * @param newSource new source
     * @param newTarget new target
     * @return copy of this edge
     */
    E copy( Graph<N, E> newGraph, N newSource, N newTarget );

    /**
     * Locks this edge. All the altering operations after lock is called are forbidden.
     */
    void lock();

    void setSource(N s, int idx);

    void setTarget(N t, int idx);
}

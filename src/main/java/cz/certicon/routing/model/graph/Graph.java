/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.Iterator;

/**
 * Definition of graph
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <N>
 * @param <E>
 */
public interface Graph<N extends Node, E extends Edge> {

    /**
     * Returns amount of nodes in the graph
     *
     * @return amount of nodes in the graph
     */
    int getNodesCount();

    /**
     * Returns nodes' iterator
     *
     * @return nodes' iterator
     */
    Iterator<N> getNodes();

    /**
     * Returns amount of edges in the graph
     *
     * @return amount of edges in the graph
     */
    int getEdgeCount();

    /**
     * Returns edges' iterator
     *
     * @return edges' iterator
     */
    Iterator<E> getEdges();

    /**
     * Returns iterator of edges adjacent to the given node
     *
     * @param node given node
     * @return iterator of node's edges
     */
    Iterator<E> getEdges( N node );

    /**
     * Returns iterator of edges incoming to the given node
     *
     * @param node given node
     * @return iterator of incoming edges
     */
    Iterator<E> getIncomingEdges( N node );

    /**
     * Returns iterator of edges outgoing from the given node
     *
     * @param node given node
     * @return iterator of outgoing edges
     */
    Iterator<E> getOutgoingEdges( N node );

    N getNodeById( long id );

    E getEdgeById( long id );

    /**
     * Returns source node of the given edge
     *
     * @param edge given edge
     * @return source node
     */
    N getSourceNode( E edge );

    /**
     * Returns target node of the given edge
     *
     * @param edge given edge
     * @return target node
     */
    N getTargetNode( E edge );

    /**
     * Returns other node (node on the opposite side from the given node) of the
     * given edge
     *
     * @param edge given edge
     * @param node given node
     * @return the other node
     */
    N getOtherNode( E edge, N node );

    /**
     * Returns cost of the turn from edge to edge via the given node
     *
     * @param node crossroad node
     * @param from from (turn origin) edge
     * @param to to (turn destination) edge
     * @return cost of the turn
     */
    Distance getTurnCost( N node, E from, E to );

    /**
     * Returns Coordinate (latitude, longitude) of the given node. Might throw
     * IllegalStateException, if the Coordinates are not set
     *
     * @param node given node
     * @return coordinate
     */
    Coordinate getNodeCoordinate( N node );

    Distance getLength( Metric metric, E edge );

    void setLength( Metric metric, E edge, Distance distnace );

//    /**
//     * Creates deep copy of this graph
//     *
//     * @return deep copy
//     */
//    Graph copy();
}

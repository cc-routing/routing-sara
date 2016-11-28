/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.utils.collections.Iterator;

import java.util.Set;

/**
 * Definition of graph
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface Graph<N extends Node, E extends Edge> {

    /**
     * Returns amount of nodes in the graph
     *
     * @return amount of nodes in the graph
     */
    int getNodesCount();

    /**
     * Returns nodes' iterator. Do not reuse this iterator (it is iterable just for the convenience of using enhanced foreach loop, it is not reusable)
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
     * Returns node by its id
     *
     * @param id node's id
     * @return node
     */
    N getNodeById( long id );

    /**
     * Returns true if the graph contains node under the given id
     *
     * @param id node's id
     * @return true if the graph contains node's id, false otherwise
     */
    boolean containsNode( long id );

    /**
     * Returns edge by its id
     *
     * @param id edge's id
     * @return edge
     */
    E getEdgeById( long id );

    /**
     * Returns true if the graph contains edge under the given id
     *
     * @param id edge's id
     * @return true if the graph contains edge's id, false otherwise
     */
    boolean containsEdge( long id );

    /**
     * Removes the given node
     *
     * @param node given node
     */
    void removeNode( N node );

    /**
     * Removes the given edge
     *
     * @param edge given edge
     */
    void removeEdge( E edge );

    /**
     * Locks the graph. Also locks all its nodes and edges.
     */
    void lock();

    /**
     * Returns available metrics of this graph. All edges must contain data for each of these metrics
     *
     * @return metics
     */
    Set<Metric> getMetrics();

    /**
     * Returns true if the graph contains the given metric
     *
     * @param metric given metric
     * @return true if the graph contains the given metric, false otherwise
     */
    boolean hasMetric( Metric metric );

    /**
     * Creates deep copy of this graph
     *
     * @return deep copy
     */
    Graph<N, E> copy();

    /**
     * Returns new instance of this graph
     *
     * @param metrics supported metrics of the new graph
     * @return new instance of this graph
     */
    Graph<N, E> newInstance( Set<Metric> metrics );
}

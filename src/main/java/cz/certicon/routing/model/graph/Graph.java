/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.Iterator;

import java.util.Collection;
import java.util.Set;

/**
 * Definition of graph
 *
 * @param <N>
 * @param <E>
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
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

    N getNodeById( long id );

    boolean containsNode( long id );

    E getEdgeById( long id );

    boolean containsEdge( long id );

    void removeNode( N node );

    void removeEdge( E edge );

    void lock();

    Collection<Metric> getMetrics();

    boolean hasMetric(Metric metric);

    /**
     * Creates deep copy of this graph
     *
     * @return deep copy
     */
    Graph<N, E> copy();

    Graph<N, E> newInstance( Set<Metric> metrics );
}

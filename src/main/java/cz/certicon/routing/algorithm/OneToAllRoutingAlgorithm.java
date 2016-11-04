/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import java.util.Map;
import java8.util.Optional;

/**
 * Routing algorithm searching for routes to all the provided edges
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <N> node type
 * @param <E> edge type
 */
public interface OneToAllRoutingAlgorithm<N extends Node<N,E>, E extends Edge<N,E>> {

    /**
     * Routes to all the provided edges (if the route exists).
     *
     * @param graph given graph
     * @param metric route metric
     * @param sourceEdge source edge
     * @param sourceDirection in which direction should the route start
     * @param targetEdges target edges
     * @return
     */
    Map<E, Optional<Route<N,E>>> route( Graph<N, E> graph, Metric metric, E sourceEdge, Direction sourceDirection, Map<E, Direction> targetEdges );

    enum Direction {
        FORWARD, BACKWARD
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import java8.util.Optional;

/**
 * Algorithm search for routes between two points (nodes or edges)
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface RoutingAlgorithm<N extends Node, E extends Edge> {

    /**
     * Returns route between source and target using the given metric as a distance metric
     *
     * @param graph       graph
     * @param metric      metric
     * @param source      source node
     * @param destination target node
     * @return optional route (empty if the route was not found)
     */
    Optional<Route<N, E>> route( Graph<N, E> graph, Metric metric, N source, N destination );

    /**
     * Returns route between source and target using the given metric as a distance metric
     *
     * @param graph       graph
     * @param metric      metric
     * @param source      source edge
     * @param destination target edge
     * @return optional route (empty if the route was not found)
     */
    Optional<Route<N, E>> route( Graph<N, E> graph, Metric metric, E source, E destination );

    /**
     * Returns route between source and target using the given metric as a distance metric. Use this methods for points that lie on some edge between its start and end.
     *
     * @param graph              graph
     * @param metric             metric
     * @param source             source edge
     * @param destination        target edge
     * @param toSourceStart      distance from source coordinates to the start of the source edge
     * @param toSourceEnd        distance from source coordinates to the end of the source edge
     * @param toDestinationStart distance from target coordinates to the start of the target edge
     * @param toDestinationEnd   distance from target coordinates to the end of the target edge
     * @return optional route (empty if the route was not found)
     */
    Optional<Route<N, E>> route( Graph<N, E> graph, Metric metric, E source, E destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd );
}

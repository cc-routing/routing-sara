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
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public interface RoutingAlgorithm<N extends Node, E extends Edge> {

    Optional<Route<N,E>> route( Graph<N, E> graph, Metric metric, N source, N destination );

    Optional<Route<N,E>> route( Graph<N, E> graph, Metric metric, E source, E destination );

    Optional<Route<N,E>> route( Graph<N, E> graph, Metric metric, E source, E destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd );
}

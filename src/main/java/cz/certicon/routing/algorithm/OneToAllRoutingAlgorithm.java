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

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <N> node type
 * @param <E> edge type
 */
public interface OneToAllRoutingAlgorithm<N extends Node, E extends Edge> {

    Map<E, Route<N,E>> route( Graph<N, E> graph, Metric metric, E sourceEdge, Direction sourceDirection, Map<E, Direction> targetEdges );

    public static enum Direction {
        FORWARD, BACKWARD;
    }
}

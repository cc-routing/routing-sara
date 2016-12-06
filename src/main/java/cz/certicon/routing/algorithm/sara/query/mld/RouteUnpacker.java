/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.State;
import java.util.Map;
import java8.util.Optional;

/**
 * Interface for unpacking the routes
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public interface RouteUnpacker<N extends Node, E extends Edge> {

    /**
     * Unpacks the route
     *
     * @param overlayBuilder overlay main class
     * @param metric metric
     * @param endPoint last state{node,edge} in the route
     * @param predecessors map of state predecessors
     * @return optional route (empty if the route was not found)
     */
    Optional<Route<N, E>> unpack(OverlayBuilder overlayBuilder, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors);
}

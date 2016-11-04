/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RouteData;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;

import java.io.IOException;

/**
 * An interface for IO operations with route data, see {@link RouteData} for more details
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface RouteDataDAO {

    /**
     * Persists route data
     *
     * @param route     given route
     * @param routeData route data for the given route
     * @param <N>       node type
     * @param <E>       edge type
     * @throws IOException thrown when an IO exception occurs
     */
    <N extends Node, E extends Edge> void saveRouteData( Route<N, E> route, RouteData<E> routeData ) throws IOException;

    /**
     * Loads route data for the given route
     *
     * @param route given route
     * @param <N>   node type
     * @param <E>   edge type
     * @return an instance of {@link RouteData} for the given route
     * @throws IOException thrown when an IO exception occurs
     */
    <N extends Node, E extends Edge> RouteData<E> loadRouteData( Route<N, E> route ) throws IOException;
}

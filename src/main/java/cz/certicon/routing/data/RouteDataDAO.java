/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RouteData;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface RouteDataDAO {

    <N extends Node, E extends Edge> void saveRouteData( Graph<N, E> graph, Route<N, E> route, RouteData<E> routeData ) throws IOException;

    <N extends Node, E extends Edge> RouteData<E> loadRouteData( Graph<N, E> graph, Route<N, E> route ) throws IOException;
}

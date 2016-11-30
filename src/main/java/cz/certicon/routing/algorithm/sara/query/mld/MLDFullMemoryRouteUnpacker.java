/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayEdge;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayGraph;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayNode;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.State;

import java.util.List;
import java.util.Map;

import java8.util.Optional;

/**
 * @param <N> node type
 * @param <E> edge type
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 */
public class MLDFullMemoryRouteUnpacker<N extends Node<N, E>, E extends Edge<N, E>> implements RouteUnpacker<N, E> {

    @Override
    public Optional<Route<N, E>> unpack( OverlayBuilder overlayGraph, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors ) {
        Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
        RoutingAlgorithm saraDijkstra = new UnpackSaraDijkstraAlgorithm();
        RoutingAlgorithm overlayDijkstra = new UnpackOverlayDijkstraAlgorithm();
        if ( endPoint != null ) {
            State<N, E> currentState = endPoint;
            boolean skipSaraEdge = false;

            while ( currentState != null && !currentState.isFirst() ) {
                if ( !( currentState.getEdge() instanceof OverlayEdge ) ) {
                    if ( !skipSaraEdge ) {
                        builder.addAsFirst( (E) overlayGraph.getSaraEdge( (SaraEdge) currentState.getEdge() ) );
                    }
                    skipSaraEdge = false;
                } else {
                    unpackOverlays( builder, (OverlayEdge) currentState.getEdge(), overlayGraph, metric);
                    skipSaraEdge = true;
                }
                currentState = predecessors.get( currentState );
            }

            Route<N, E> route = builder.build();
            return Optional.of( route );
        } else {
            return Optional.empty();
        }
    }

    private void unpackOverlays( Route.RouteBuilder<N, E> builder, OverlayEdge overlayEdge, OverlayBuilder overlayGraph, Metric metric) {
        List<SaraEdge> zeroRoute = overlayEdge.getZeroRoute(metric);
        if (zeroRoute != null) {
            for (int i = zeroRoute.size() - 2; i >= 0; i--) {
                builder.addAsFirst((E) overlayGraph.getSaraEdge(zeroRoute.get(i)));
            }
        } else {
            List<OverlayEdge> overlayRoute = overlayEdge.getOverlayRoute(metric);
            if (overlayRoute != null) {
                for (int i = overlayRoute.size() - 1; i >= 0; i--) {
                    unpackOverlays(builder, overlayRoute.get(i), overlayGraph, metric);
                }
            }
        }
    }
}

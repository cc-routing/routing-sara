/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayEdge;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.State;

import java.util.List;
import java.util.Map;

import java8.util.Optional;

/**
 * Unpacker which uses shortcuts information alredy stored in the memory
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 */
public class MLDFullMemoryRouteUnpacker<N extends Node<N, E>, E extends Edge<N, E>> implements RouteUnpacker<N, E> {

    @Override
    public Optional<Route<N, E>> unpack(OverlayBuilder overlayBuilder, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors) {
        Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
        if (endPoint != null) {
            State<N, E> currentState = endPoint;
            boolean skipSaraEdge = false;

            while (currentState != null && !currentState.isFirst()) {
                if (!(currentState.getEdge() instanceof OverlayEdge)) {
                    if (!skipSaraEdge) {
                        builder.addAsFirst((E) overlayBuilder.getSaraEdge((SaraEdge) currentState.getEdge()));
                    }
                    skipSaraEdge = false;
                } else {
                    unpackOverlays(builder, (OverlayEdge) currentState.getEdge(), overlayBuilder, metric);
                    skipSaraEdge = true;
                }
                currentState = predecessors.get(currentState);
            }

            Route<N, E> route = builder.build();
            return Optional.of(route);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Unpacks shortcuts from L1+
     *
     * @param builder route builder
     * @param overlayEdge edge to unpack
     * @param overlayBuilder overlay main class
     * @param metric metric
     */
    private void unpackOverlays(Route.RouteBuilder<N, E> builder, OverlayEdge overlayEdge, OverlayBuilder overlayBuilder, Metric metric) {
        List<SaraEdge> zeroRoute = overlayEdge.getZeroRoute(metric);
        if (zeroRoute != null) {
            for (int i = zeroRoute.size() - 2; i >= 0; i--) {
                builder.addAsFirst((E) overlayBuilder.getSaraEdge(zeroRoute.get(i)));
            }
        } else {
            List<OverlayEdge> overlayRoute = overlayEdge.getOverlayRoute(metric);
            if (overlayRoute != null) {
                for (int i = overlayRoute.size() - 1; i >= 0; i--) {
                    unpackOverlays(builder, overlayRoute.get(i), overlayBuilder, metric);
                }
            }
        }
    }
}

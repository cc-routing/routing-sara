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
import cz.certicon.routing.utils.java8.Optional;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class MLDFullMemoryRouteUnpacker<N extends Node<N, E>, E extends Edge<N, E>> implements RouteUnpacker<N, E> {

    Route.RouteBuilder<N, E> builder;
    RoutingAlgorithm saraDijkstra;
    RoutingAlgorithm overlayDijkstra;

    public MLDFullMemoryRouteUnpacker() {
        builder = Route.<N, E>builder();
        saraDijkstra = new UnpackSaraDijkstraAlgorithm();
        overlayDijkstra = new UnpackOverlayDijkstraAlgorithm();
    }

    @Override
    public Optional<Route<N, E>> unpack(Graph<N, E> graph, OverlayBuilder overlayGraph, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors) {
        if (endPoint != null) {
            State<N, E> currentState = endPoint;
            boolean skipSaraEdge = false;

            while (currentState != null && !currentState.isFirst()) {
                if (!(currentState.getEdge() instanceof OverlayEdge)) {
                    if (!skipSaraEdge) {
                        builder.addAsFirst((E)overlayGraph.getGraph().getEdgeById(currentState.getEdge().getId()));
                    }
                    skipSaraEdge = false;
                } else {
                    unpackOverlays((OverlayEdge) currentState.getEdge(), overlayGraph);
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

    private void unpackOverlays(OverlayEdge overlayEdge, OverlayBuilder overlayGraph) {
        if (overlayEdge.saraWay != null) {
            for (int i = overlayEdge.saraWay.size() - 2; i >= 0; i--) {
                builder.addAsFirst((E)overlayGraph.getGraph().getEdgeById(overlayEdge.saraWay.get(i).getId()));
            }
        } else if (overlayEdge.overWay != null) {
            for (int i = overlayEdge.overWay.size() - 1; i >= 0; i--) {
                unpackOverlays(overlayEdge.overWay.get(i), overlayGraph);
            }
        }
    }
}

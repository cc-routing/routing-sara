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
import cz.certicon.routing.model.graph.State;
import java.util.Map;
import java8.util.Optional;

/**
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class MLDRecursiveRouteUnpacker<N extends Node<N, E>, E extends Edge<N, E>> implements RouteUnpacker<N, E> {

    Route.RouteBuilder<N, E> builder;
    RoutingAlgorithm saraDijkstra;
    RoutingAlgorithm overlayDijkstra;

    public MLDRecursiveRouteUnpacker() {
        builder = Route.<N, E>builder();
        saraDijkstra = new UnpackSaraDijkstraAlgorithm();
        overlayDijkstra = new UnpackOverlayDijkstraAlgorithm();
    }

    @Override
    public Optional<Route<N, E>> unpack(OverlayBuilder overlayGraph, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors) {
        if (endPoint != null) {
            State<N, E> currentState = endPoint;

            while (currentState != null && !currentState.isFirst()) {
                if (!(currentState.getNode() instanceof OverlayNode)) {
                    builder.addAsFirst(currentState.getEdge());
                } else {
                    OverlayNode oTo = (OverlayNode) currentState.getNode();
                    currentState = predecessors.get(currentState);
                    if (!(currentState.getNode() instanceof OverlayNode)) {
                        throw new IllegalStateException("OverlayNode not found. Something is wrong.");
                    }
                    OverlayNode oFrom = (OverlayNode) currentState.getNode();
                    unpackHighLevels(oTo.getLevel(), overlayGraph, metric, (E) oFrom.getIncomingEdges().next(), (E) oTo.getOutgoingEdges().next());
                }
                currentState = predecessors.get(currentState);
            }

            Route<N, E> route = builder.build();
            return Optional.of(route);
        } else {
            return Optional.empty();
        }
    }

    private void unpackHighLevels(int level, OverlayBuilder overlayGraph, Metric metric, E fromEdge, E toEdge) {
        if (level == 1) {
            unpackLowestLevel(metric, fromEdge, toEdge);
        } else {
            Optional<Route<N, E>> subResult = overlayDijkstra.route(metric, getOverlayEdgeBelow((OverlayEdge) fromEdge), getOverlayEdgeBelow((OverlayEdge) toEdge));
            Route<N, E> subRoute = subResult.get();
            for (int i = subRoute.getEdgeList().size() - 1; i >= 0; i -= 2) {
                if (i == 0) {
                    break;
                }
                unpackHighLevels(level - 1, overlayGraph, metric, subRoute.getEdgeList().get(i - 2), subRoute.getEdgeList().get(i));
            }
        }
    }

    private void unpackLowestLevel(Metric metric, E fromEdge, E toEdge) {
        OverlayEdge fromOverlayEdge = (OverlayEdge) fromEdge;
        OverlayEdge toOverlayEdge = (OverlayEdge) toEdge;
        Optional<Route<N, E>> subResult = saraDijkstra.route(metric, fromOverlayEdge.getZeroEdge(), toOverlayEdge.getZeroEdge());
        Route<N, E> subRoute = subResult.get();
        for (int i = subRoute.getEdgeList().size() - 2; i >= 0; i--) {
            builder.addAsFirst(subRoute.getEdgeList().get(i));
        }
    }

    private OverlayEdge getOverlayEdgeBelow(OverlayEdge higherOverlayNode) {
        return higherOverlayNode.getSource().getLowerNode().getOutgoingEdges().next();
    }
}

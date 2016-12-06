/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.DijkstraAlgorithm;
import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayEdge;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayNode;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.State;
import java.util.Map;
import java8.util.Optional;

/**
 * TODO: refactor in the same way as forward unpacker and verify it
 *
 * Unpacker which recuresively unpacks the shortcuts from higher levels to the
 * lower ones for the reverse graph
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class MLDReverseRecursiveRouteUnpacker<N extends Node<N, E>, E extends Edge<N, E>> implements RouteUnpacker<N, E> {

    @Override
    public Optional<Route<N, E>> unpack(OverlayBuilder overlayBuilder, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors) {
        if (endPoint != null) {
            RoutingAlgorithm dijkstra = new DijkstraAlgorithm();
            Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
            State<N, E> currentState = endPoint;

            while (currentState != null && !currentState.isFirst()) {
                if (!(currentState.getNode() instanceof OverlayNode)) {
                    builder.addAsLast(currentState.getEdge());
                } else {
                    OverlayNode oFrom = (OverlayNode) currentState.getNode();
                    currentState = predecessors.get(currentState);
                    if (!(currentState.getNode() instanceof OverlayNode)) {
                        throw new IllegalStateException("OverlayNode not found. Something is wrong.");
                    }
                    OverlayNode oTo = (OverlayNode) currentState.getNode();
                    unpackHighLevels(dijkstra, oFrom.getLevel(), overlayBuilder, metric, (E) oFrom.getIncomingEdges().next(), (E) oTo.getOutgoingEdges().next(), builder);
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
     * Unpacks L2+ shortcuts
     *
     * @param router routing algorithm
     * @param level current level
     * @param overlayBuilder overlay main class
     * @param metric metric
     * @param fromEdge source edge
     * @param toEdge target edge
     * @param builder route builder
     */
    private void unpackHighLevels(RoutingAlgorithm router, int level, OverlayBuilder overlayBuilder, Metric metric, E fromEdge, E toEdge, Route.RouteBuilder<N, E> builder) {
        if (level == 1) {
            unpackLowestLevel(router, metric, fromEdge, toEdge, builder);
        } else {
            Optional<Route<N, E>> subResult = router.route(metric, getOverlayEdgeBelow((OverlayEdge) fromEdge), getOverlayEdgeBelow((OverlayEdge) toEdge));
            Route<N, E> subRoute = subResult.get();
            for (int i = 0; i < subRoute.getEdgeList().size() - 1; i += 2) {
                unpackHighLevels(router, level - 1, overlayBuilder, metric, subRoute.getEdgeList().get(i), subRoute.getEdgeList().get(i + 2), builder);
            }
        }
    }

    /**
     * Unpacks L1 shortcuts
     *
     * @param router routing algorithm
     * @param metric metric
     * @param fromEdge source edge
     * @param toEdge target edge
     * @param builder route builder
     */
    private void unpackLowestLevel(RoutingAlgorithm router, Metric metric, E fromEdge, E toEdge, Route.RouteBuilder<N, E> builder) {
        OverlayEdge fromOverlayEdge = (OverlayEdge) fromEdge;
        OverlayEdge toOverlayEdge = (OverlayEdge) toEdge;
        Optional<Route<N, E>> subResult = router.route(metric, fromOverlayEdge.getZeroEdge(), toOverlayEdge.getZeroEdge());
        Route<N, E> subRoute = subResult.get();
        for (int i = 1; i < subRoute.getEdgeList().size(); i++) {
            builder.addAsLast(subRoute.getEdgeList().get(i));
        }
    }

    /**
     * Gets level X-1 overlay edge for level X overlay node
     *
     * @param higherOverlayNode overlay node
     * @return level X-1 overlay edge
     */
    private OverlayEdge getOverlayEdgeBelow(OverlayEdge higherOverlayNode) {
        return higherOverlayNode.getSource().getLowerNode().getOutgoingEdges().next();
    }
}

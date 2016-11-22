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
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayGraph;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayNode;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.utils.java8.Optional;
import java.util.Map;

/**
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class MLDReverseRecursiveRouteUnpacker<N extends Node<N, E>, E extends Edge<N, E>> implements RouteUnpacker<N, E> {

    @Override
    public Optional<Route<N, E>> unpack(Graph<N, E> graph, OverlayBuilder overlayGraph, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors) {
        if (endPoint != null) {
            RoutingAlgorithm dijkstra = new DijkstraAlgorithm();
            Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
            State<N, E> currentState = endPoint;

            while (currentState != null && !currentState.isFirst()) {
                if (!(currentState.getNode() instanceof OverlayNode)) {
                    builder.addAsLast(currentState.getEdge());
                    //System.out.println("SARA " + currentState.getNode().getId() + ";" + currentState.getEdge().getId());
                } else {
                    OverlayNode oFrom = (OverlayNode) currentState.getNode();
                    currentState = predecessors.get(currentState);
                    if (!(currentState.getNode() instanceof OverlayNode)) {
                        throw new IllegalStateException("OverlayNode not found. Something is wrong.");
                    }
                    OverlayNode oTo = (OverlayNode) currentState.getNode();
                    //System.out.println("in OVERLAY " + oTo.level() + " " + oFrom.getId() + ";" + oTo.getId());
                    unpackHighLevels(dijkstra, oFrom.level(), graph, overlayGraph, metric, (E) oFrom.getIncomingEdges().next(), (E) oTo.getOutgoingEdges().next(), builder);
                    //System.out.println("out OVERLAY " + oTo.level() + " " + oFrom.getId() + ";" + oTo.getId());
                }
                currentState = predecessors.get(currentState);
            }

            Route<N, E> route = builder.build();
            return Optional.of(route);
        } else {
            return Optional.empty();
        }
    }

    private void unpackHighLevels(RoutingAlgorithm router, int level, Graph<N, E> graph, OverlayBuilder overlayGraph, Metric metric, E fromEdge, E toEdge, Route.RouteBuilder<N, E> builder) {
        if (level == 1) {
            unpackLowestLevel(router, graph, metric, fromEdge, toEdge, builder);
        } else {
            OverlayGraph oGraph = overlayGraph.getPartitions().get(level - 1).getOverlayGraph();
            Optional<Route<N, E>> subResult = router.route(oGraph, metric, getOverlayEdgeBelow((OverlayEdge) fromEdge), getOverlayEdgeBelow((OverlayEdge) toEdge));
            Route<N, E> subRoute = subResult.get();
            for (int i = 0; i < subRoute.getEdgeList().size() - 1; i += 2) {
                //System.out.println("in OVERLAY " + (level - 1) + " " + subRoute.getEdgeList().get(i).getId());
                unpackHighLevels(router, level - 1, graph, overlayGraph, metric, subRoute.getEdgeList().get(i), subRoute.getEdgeList().get(i + 2), builder);
                //System.out.println("out OVERLAY " + (level - 1) + " " + subRoute.getEdgeList().get(i + 2).getId());
            }
        }
    }

    private void unpackLowestLevel(RoutingAlgorithm router, Graph<N, E> graph, Metric metric, E fromEdge, E toEdge, Route.RouteBuilder<N, E> builder) {
        OverlayEdge fromOverlayEdge = (OverlayEdge) fromEdge;
        OverlayEdge toOverlayEdge = (OverlayEdge) toEdge;
        Optional<Route<N, E>> subResult = router.route(graph, metric, fromOverlayEdge.getSaraEdge(), toOverlayEdge.getSaraEdge());
        Route<N, E> subRoute = subResult.get();
        for (int i = 1; i < subRoute.getEdgeList().size(); i++) {
            builder.addAsLast(subRoute.getEdgeList().get(i));
            //System.out.println(" - " + subRoute.getEdgeList().get(i).getId());
        }
    }

    private OverlayEdge getOverlayEdgeBelow(OverlayEdge higherOverlayNode) {
        return higherOverlayNode.getSource().getLowerNode().getOutgoingEdges().next();
    }
}

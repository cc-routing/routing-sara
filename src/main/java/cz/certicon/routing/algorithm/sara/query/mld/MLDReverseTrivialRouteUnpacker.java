/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.DijkstraAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
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
public class MLDReverseTrivialRouteUnpacker<N extends Node<N, E>, E extends Edge<N, E>> implements RouteUnpacker<N, E> {

    @Override
    public Optional<Route<N, E>> unpack(Graph<N, E> graph, OverlayBuilder overlayGraph, Metric metric, State<N, E> endPoint, Map<State<N, E>, State<N, E>> predecessors) {
        if (endPoint != null) {
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
            Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
            State<N, E> currentState = endPoint;

            while (currentState != null && !currentState.isFirst()) {
                if (!(currentState.getNode() instanceof OverlayNode)) {
                    System.out.println(currentState.getEdge().getId());
                    builder.addAsLast(currentState.getEdge());
                } else {
                    OverlayNode oTo = (OverlayNode) currentState.getNode();
                    currentState = predecessors.get(currentState);
                    if (!(currentState.getNode() instanceof OverlayNode)) {
                        throw new IllegalStateException("OverlayNode not found. Something is wrong.");
                    }
                    System.out.println("OVERLAY lvl " + oTo.level() + " from edge " + oTo.getColumn().getEdge().getId() + " to edge " + currentState.getEdge().getId());
                    Optional<Route<N, E>> subResult = dijkstra.route(graph, metric, oTo.getColumn().getEdge(), currentState.getEdge());
                    Route<N, E> subRoute = subResult.get();
                    for (int i = subRoute.getEdgeList().size() - 2; i >= 0; i--) {
                        System.out.println(subRoute.getEdgeList().get(i).getId());
                        builder.addAsLast(subRoute.getEdgeList().get(i));
                    }
                }
                currentState = predecessors.get(currentState);
            }

            Route<N, E> route = builder.build();
            return Optional.of(route);
        } else {
            return Optional.empty();
        }
    }
}

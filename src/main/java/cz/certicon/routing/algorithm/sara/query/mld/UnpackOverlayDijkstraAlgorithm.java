/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayEdge;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayNode;
import cz.certicon.routing.data.SqliteGraphDataDAO;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.view.DebugViewer;
import cz.certicon.routing.view.JxDebugViewer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java8.util.Optional;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class UnpackOverlayDijkstraAlgorithm<N extends OverlayNode, E extends OverlayEdge> implements RoutingAlgorithm<N, E> {

    @Override
    public Optional<Route<N, E>> route(Metric metric, N source, N destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route(Metric metric, E source, E destination) {
        return route(metric, source, destination, Distance.newInstance(0), Distance.newInstance(0), Distance.newInstance(0), Distance.newInstance(0));
    }

    @Override
    public Optional<Route<N, E>> route(Metric metric, E source, E destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd) {
        Map<State<N, E>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<N, E>> pqueue = new FibonacciHeap<>();
        // create upper bound if the edges are equal and mark it
        Distance upperBound = Distance.newInfinityInstance();
        E singleEdgePath = null;
        if (source.equals(destination)) {
            Distance substract = toSourceEnd.subtract(toDestinationEnd);
            if (source.isOneWay()) {
                // is positive or zero
                if (!substract.isNegative()) {
                    upperBound = substract;
                    singleEdgePath = source;
                }
            } else {
                upperBound = substract.absolute();
                singleEdgePath = source;
            }
        }
        putNodeDistance(nodeDistanceMap, pqueue, new State(source.getTarget(), source), toSourceEnd);
        if (!source.isOneWay()) {
            putNodeDistance(nodeDistanceMap, pqueue, new State(source.getSource(), source), toSourceStart);
        }

        return route(metric, nodeDistanceMap, pqueue, upperBound, new EdgeEndCondition(destination, toDestinationStart, toDestinationEnd), singleEdgePath, destination);
    }

    private Optional<Route<N, E>> route(Metric metric, Map<State<N, E>, Distance> nodeDistanceMap, PriorityQueue<State<N, E>> pqueue, Distance upperBound, EndCondition<N, E> endCondition, E singleEdgePath, E endEdge) {
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> closedStates = new HashSet<>();
        State finalState = null;

        while (!pqueue.isEmpty()) {
            State<N, E> state = pqueue.extractMin();
            Distance distance = nodeDistanceMap.get(state);
            closedStates.add(state);

            Pair<State, Distance> updatePair = endCondition.update(finalState, upperBound, state, distance);
            upperBound = updatePair.b;
            finalState = updatePair.a;
            if (distance.isGreaterThan(upperBound)) {
                pqueue.clear();
                break;
            }
            for (OverlayEdge edge : state.getNode().getOutgoingEdges()) {
                OverlayNode targetNode = edge.getOtherNode(state.getNode());
                State<N, E> targetState = new State(targetNode, edge);

                if (!closedStates.contains(targetState) && state.getNode().getLevel() == targetState.getNode().getLevel()) {
                    Distance targetDistance = (nodeDistanceMap.containsKey(targetState)) ? nodeDistanceMap.get(targetState) : Distance.newInfinityInstance();
                    Distance alternativeDistance = distance.add(edge.getLength(metric)).add(state.isFirst() ? Distance.newInstance(0) : state.getNode().getTurnDistance(state.getEdge(), edge));
                    if (alternativeDistance.isLowerThan(targetDistance)) {
                        putNodeDistance(nodeDistanceMap, pqueue, targetState, alternativeDistance);
                        predecessorMap.put(targetState, state);
                    }
                }
            }
        }
        if (finalState != null) {
            Route.RouteBuilder<N, E> builder = Route.<OverlayNode, OverlayEdge>builder();
            State<N, E> currentState = finalState;
            while (currentState != null && !currentState.isFirst()) {
                builder.addAsFirst(currentState.getEdge());
                currentState = predecessorMap.get(currentState);
            }
            if (endEdge != null) {
                builder.addAsLast(endEdge);
            }
            return Optional.of(builder.build());
        } else if (singleEdgePath != null) {
            Route.RouteBuilder<N, E> builder = Route.<OverlayNode, OverlayEdge>builder();
            builder.addAsFirst(singleEdgePath);
            return Optional.of(builder.build());
        } else {
            return Optional.empty();
        }
    }

    private void putNodeDistance(Map<State<N, E>, Distance> nodeDistanceMap, PriorityQueue<State<N, E>> pqueue, State<N, E> node, Distance distance) {
        pqueue.decreaseKey(node, distance.getValue());
        nodeDistanceMap.put(node, distance);
    }

    private interface EndCondition<N extends Node, E extends Edge> {

        public Pair<State<N, E>, Distance> update(State<N, E> currentFinalState, Distance currentUpperBound, State<N, E> currentState, Distance currentDistance);

    }

    private static class EdgeEndCondition<N extends Node, E extends Edge> implements EndCondition<N, E> {

        private final E destination;
        private final Distance toDestinationStart;
        private final Distance toDestinationEnd;

        public EdgeEndCondition(E destination, Distance toDestinationStart, Distance toDestinationEnd) {
            this.destination = destination;
            this.toDestinationStart = toDestinationStart;
            this.toDestinationEnd = toDestinationEnd;
        }

        @Override
        public Pair<State<N, E>, Distance> update(State<N, E> currentFinalState, Distance currentUpperBound, State<N, E> currentState, Distance currentDistance) {
            if (currentState.getNode().equals(destination.getSource())) {
                Distance completeDistance = currentDistance.add(toDestinationStart).add(currentState.getNode().getTurnDistance(currentState.getEdge(), destination));
                if (completeDistance.isLowerThan(currentUpperBound)) {
                    return new Pair<>(currentState, completeDistance);
                }
            }
            if (!destination.isOneWay() && currentState.getNode().equals(destination.getTarget())) {
                Distance completeDistance = currentDistance.add(toDestinationEnd).add(currentState.getNode().getTurnDistance(currentState.getEdge(), destination));
                if (completeDistance.isLowerThan(currentUpperBound)) {
                    return new Pair<>(currentState, completeDistance);
                }
            }
            return new Pair<>(currentFinalState, currentUpperBound);
        }

    }

}

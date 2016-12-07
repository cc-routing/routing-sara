/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayEdge;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayNode;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.ZeroEdge;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RoutingPoint;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.values.Distance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java8.util.Optional;

/**
 * TODO: parallel version, merging routes, check end condition, refactor of
 * structures
 *
 * Bidirectional algorithm searches for routes between two points (currently
 * only nodes) based on the multilevel structure
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class MultilevelBiDijkstraAlgorithm<N extends Node<N, E>, E extends Edge<N, E>> implements RoutingAlgorithm<N, E> {

    State finalState = null;
    State reverseFinalState = null;

    /**
     * Returns route between source and target using the given metric
     *
     * @param overlayBuilder overlay main class
     * @param metric metric
     * @param source source node
     * @param destination target node
     * @param unpacker route unpacker
     * @return optional route (empty if the route was not found)
     */
    public Optional<Route<N, E>> route(OverlayBuilder overlayBuilder, Metric metric, SaraNode source, SaraNode destination, RouteUnpacker unpacker) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue = new FibonacciHeap<>();

        Map<State<SaraNode, SaraEdge>, Distance> reverseNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> reversePqueue = new FibonacciHeap<>();
        Map<State<OverlayNode, SaraEdge>, Distance> reverseOverlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> reverseOverlayPqueue = new FibonacciHeap<>();

        Map<SaraNode, StateDistancePair> closedSaraNodes = new HashMap<>();
        Map<SaraNode, StateDistancePair> reverseClosedSaraNodes = new HashMap<>();

        putNodeDistance(nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>(source, null), Distance.newInstance(0), closedSaraNodes);
        putNodeDistance(reverseNodeDistanceMap, reversePqueue, new State<SaraNode, SaraEdge>(destination, null), Distance.newInstance(0), reverseClosedSaraNodes);

        return route(overlayBuilder, metric, nodeDistanceMap, pqueue, overlayNodeDistanceMap, overlayPqueue, reverseNodeDistanceMap, reversePqueue, reverseOverlayNodeDistanceMap, reverseOverlayPqueue, source, destination, unpacker, closedSaraNodes, reverseClosedSaraNodes);
    }

    @Override
    public Optional<Route<N, E>> route(Metric metric, N source, N destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route(Metric metric, E source, E destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route(Metric metric, E source, E destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route(Metric metric, RoutingPoint<N, E> source, RoutingPoint<N, E> destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns route between source and target using the given metric
     *
     * @param overlayBuilder overlay main class
     * @param metric metric
     * @param nodeDistanceMap map of tentative distances for L0 graph
     * @param pqueue queue of L0 nodes to be relaxed
     * @param overlayNodeDistanceMap map of tentative distances for L1+ graph
     * @param overlayPqueue queue of L1+ nodes to be relaxed
     * @param reverseNodeDistanceMap map of tentative distances for L0 reverse
     * graph
     * @param reversePqueue queue of L0 reverse nodes to be relaxed
     * @param reverseOverlayNodeDistanceMap map of tentative distances for L1+
     * reverse graph
     * @param reverseOverlayPqueue queue of L1+ reverse nodes to be relaxed
     * @param source source node
     * @param target target node
     * @param unpacker route unpacker
     * @param closedSaraNodes map of L0 closed nodes
     * @param reverseClosedSaraNodes map of L0 reverse closed nodes
     * @return optional route (empty if the route was not found)
     */
    private Optional<Route<N, E>> route(OverlayBuilder overlayBuilder, Metric metric, Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, Map<State<SaraNode, SaraEdge>, Distance> reverseNodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> reversePqueue, Map<State<OverlayNode, SaraEdge>, Distance> reverseOverlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> reverseOverlayPqueue, SaraNode source, SaraNode target, RouteUnpacker unpacker, Map<SaraNode, StateDistancePair> closedSaraNodes, Map<SaraNode, StateDistancePair> reverseClosedSaraNodes) {
        Set<State> closedStates = new HashSet<>();
        Set<State> closedOverlayStates = new HashSet<>();
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> reverseClosedStates = new HashSet<>();
        Set<State> reverseClosedOverlayStates = new HashSet<>();
        Map<State, State> reversePredecessorMap = new HashMap<>();
        Map<OverlayNode, StateDistancePair> closedOverlayNodes = new HashMap<>();
        Map<OverlayNode, StateDistancePair> reverseClosedOverlayNodes = new HashMap<>();

        int noIter = 0;

        double shortestPath = Double.MAX_VALUE;
        long startTime = System.currentTimeMillis();

        while (!pqueue.isEmpty() || !overlayPqueue.isEmpty() || !reversePqueue.isEmpty() || !reverseOverlayPqueue.isEmpty()) {
            //End condition
            System.out.println("fronta min " + (Math.min(pqueue.minValue(), overlayPqueue.minValue()) + Math.min(reversePqueue.minValue(), reverseOverlayPqueue.minValue())));
            System.out.println(pqueue.minValue() + "," + overlayPqueue.minValue() + "   " + reversePqueue.minValue() + "," + reverseOverlayPqueue.minValue());
            System.out.println(pqueue.size() + "," + overlayPqueue.size() + "   " + reversePqueue.size() + "," + reverseOverlayPqueue.size());
            if (shortestPath < (Math.min(pqueue.minValue(), overlayPqueue.minValue()) + Math.min(reversePqueue.minValue(), reverseOverlayPqueue.minValue()))) {
                System.out.println("biMLD - iterations " + noIter);
                System.out.println("biMLD - distance " + shortestPath);
                System.out.println("biMLD - time " + (System.currentTimeMillis() - startTime));
                break;
            }

            //L0 graph
            // --------------------->o---------------------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode
            if (overlayPqueue.isEmpty() || (!pqueue.isEmpty() && pqueue.minValue() < overlayPqueue.minValue())) {
                noIter++;
                State<SaraNode, SaraEdge> state = pqueue.extractMin();
                Distance distance = nodeDistanceMap.get(state);
                closedStates.add(state);

                //relax neighboring nodes
                Iterator<SaraEdge> edges = state.getNode().getOutgoingEdges();
                while (edges.hasNext()) {
                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = transferEdge.getOtherNode(state.getNode());

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayBuilder.getMaxEntryNode(transferNode, (ZeroEdge) transferEdge, source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State transferState = new State(levelNode, transferEdge);
                        if (!closedOverlayStates.contains(transferState)) {
                            Distance transferDistance = (overlayNodeDistanceMap.containsKey(transferState)) ? overlayNodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(transferEdge.getLength(metric)).add(state.isFirst() ? Distance.newInstance(0) : state.getNode().getTurnDistance(state.getEdge(), transferEdge));

                            if (alternativeDistance.isLowerThan(transferDistance)) {
                                putOverlayNodeDistance(overlayNodeDistanceMap, overlayPqueue, transferState, alternativeDistance, closedOverlayNodes, state);
                                predecessorMap.put(transferState, state);
                            }
                        }
                    } // routing is still done in L0
                    else {
                        State transferState = new State(transferNode, transferEdge);
                        if (!closedStates.contains(transferState)) {
                            Distance transferDistance = (nodeDistanceMap.containsKey(transferState)) ? nodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(transferEdge.getLength(metric)).add(state.isFirst() ? Distance.newInstance(0) : state.getNode().getTurnDistance(state.getEdge(), transferEdge));

                            //check connection of forward and reverse run
                            shortestPath = updateShortestPath(shortestPath, reverseClosedSaraNodes, transferNode, alternativeDistance.getValue());

                            if (alternativeDistance.isLowerThan(transferDistance)) {
                                putNodeDistance(nodeDistanceMap, pqueue, transferState, alternativeDistance, closedSaraNodes);
                                predecessorMap.put(transferState, state);
                            }
                        }
                    }
                }
            } // overlay graph
            // ----------)(----------->o--------------------->o-----------)(----------->o
            // state.getEdge   state.getNode   cellEdge   cellNode    borderEdge    borderNode
            else {
                noIter++;
                State<OverlayNode, SaraEdge> overlayState = overlayPqueue.extractMin();
                Distance distance = overlayNodeDistanceMap.get(overlayState);
                closedOverlayStates.add(overlayState);

                //relax neighboring nodes, i.e. exit points in the particular cell + corresponding border edge
                Iterator<OverlayEdge> edges = overlayState.getNode().getOutgoingEdges();
                while (edges.hasNext()) {
                    OverlayEdge cellEdge = edges.next();
                    OverlayNode cellNode = cellEdge.getOtherNode(overlayState.getNode());
                    State cellState = new State(cellNode, cellEdge);

                    //make a move inside the cell through the shortcut
                    //cell node is not store anywhere since we are interested in border node only
                    Distance cellDistance = distance.add(cellEdge.getLength(metric));

                    //check connection of forward and reverse run
                    shortestPath = updateShortestPath(shortestPath, reverseClosedOverlayNodes, cellNode, cellDistance.getValue(), cellState, 1);

                    //use border edge to the next cell
                    OverlayEdge borderEdge = cellNode.getOutgoingEdges().next();//exactly one border edge must exist
                    OverlayNode borderNode = borderEdge.getOtherNode(cellNode);

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayBuilder.getMaxEntryNode(borderNode.getLift().getNode(), borderNode.getLift().getEdge(), source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State borderState = new State(levelNode, borderNode.getLift().getEdge());
                        if (!closedOverlayStates.contains(borderState)) {
                            Distance borderDistance = (overlayNodeDistanceMap.containsKey(borderState)) ? overlayNodeDistanceMap.get(borderState) : Distance.newInfinityInstance();
                            Distance newDistance = cellDistance.add(borderNode.getLift().getEdge().getLength(metric));

                            if (newDistance.isLowerThan(borderDistance)) {
                                putOverlayNodeDistance(overlayNodeDistanceMap, overlayPqueue, borderState, newDistance, closedOverlayNodes, cellState);
                                predecessorMap.put(cellState, overlayState);
                                predecessorMap.put(borderState, cellState);
                            }
                        }
                    } // routing is going back to L0
                    else {
                        State borderState = new State(borderNode.getLift().getNode(), borderNode.getLift().getEdge());
                        if (!closedStates.contains(borderState)) {
                            Distance borderDistance = (nodeDistanceMap.containsKey(borderState)) ? nodeDistanceMap.get(borderState) : Distance.newInfinityInstance();
                            Distance newDistance = cellDistance.add(borderNode.getLift().getEdge().getLength(metric));

                            //check connection of forward and reverse run
                            shortestPath = updateShortestPath(shortestPath, reverseClosedSaraNodes, borderNode.getLift().getNode(), newDistance.getValue());

                            if (newDistance.isLowerThan(borderDistance)) {
                                putNodeDistance(nodeDistanceMap, pqueue, borderState, newDistance, closedSaraNodes);
                                predecessorMap.put(cellState, overlayState);
                                predecessorMap.put(borderState, cellState);
                            }
                        }
                    }
                }
            }

            // -------
            // REVERSE
            // -------
            //L0 graph
            // --------------------->o---------------------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode
            if (reverseOverlayPqueue.isEmpty() || (!reversePqueue.isEmpty() && reversePqueue.minValue() < reverseOverlayPqueue.minValue())) {
                noIter++;
                State<SaraNode, SaraEdge> state = reversePqueue.extractMin();
                Distance distance = reverseNodeDistanceMap.get(state);
                reverseClosedStates.add(state);

                //relax neighboring nodes
                Iterator<SaraEdge> edges = state.getNode().getIncomingEdges();
                while (edges.hasNext()) {
                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = transferEdge.getOtherNode(state.getNode());

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayBuilder.getMaxExitNode(transferNode, (ZeroEdge) transferEdge, source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State transferState = new State(levelNode, transferEdge);
                        if (!reverseClosedOverlayStates.contains(transferState)) {
                            Distance transferDistance = (reverseOverlayNodeDistanceMap.containsKey(transferState)) ? reverseOverlayNodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(transferEdge.getLength(metric)).add(state.isFirst() ? Distance.newInstance(0) : state.getNode().getTurnDistance(transferEdge, state.getEdge()));

                            if (alternativeDistance.isLowerThan(transferDistance)) {
                                putOverlayNodeDistance(reverseOverlayNodeDistanceMap, reverseOverlayPqueue, transferState, alternativeDistance, reverseClosedOverlayNodes, state);
                                reversePredecessorMap.put(transferState, state);
                            }
                        }
                    } // routing is still done in L0
                    else {
                        State transferState = new State(transferNode, transferEdge);
                        if (!reverseClosedStates.contains(transferState)) {
                            Distance transferDistance = (reverseNodeDistanceMap.containsKey(transferState)) ? reverseNodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(transferEdge.getLength(metric)).add(state.isFirst() ? Distance.newInstance(0) : state.getNode().getTurnDistance(transferEdge, state.getEdge()));

                            //check connection of forward and reverse run
                            shortestPath = updateShortestPath(shortestPath, closedSaraNodes, transferNode, alternativeDistance.getValue());

                            if (alternativeDistance.isLowerThan(transferDistance)) {
                                putNodeDistance(reverseNodeDistanceMap, reversePqueue, transferState, alternativeDistance, reverseClosedSaraNodes);
                                reversePredecessorMap.put(transferState, state);
                            }
                        }
                    }
                }
            } // overlay graph
            // ----------)(----------->o--------------------->o-----------)(----------->o
            // state.getEdge   state.getNode   cellEdge   cellNode    borderEdge    borderNode
            else {
                noIter++;
                State<OverlayNode, SaraEdge> overlayState = reverseOverlayPqueue.extractMin();
                Distance distance = reverseOverlayNodeDistanceMap.get(overlayState);
                reverseClosedOverlayStates.add(overlayState);

                //relax neighboring nodes, i.e. exit points in the particular cell + corresponding border edge
                Iterator<OverlayEdge> edges = overlayState.getNode().getIncomingEdges();
                while (edges.hasNext()) {
                    OverlayEdge cellEdge = edges.next();
                    OverlayNode cellNode = cellEdge.getOtherNode(overlayState.getNode());
                    State cellState = new State(cellNode, cellEdge);

                    //make a move inside the cell through the shortcut
                    //cell node is not store anywhere since we are interested in border node only
                    Distance cellDistance = distance.add(cellEdge.getLength(metric));

                    //check connection of forward and reverse run
                    shortestPath = updateShortestPath(shortestPath, closedOverlayNodes, cellNode, cellDistance.getValue(), cellState, -1);

                    //use border edge to the next cell
                    OverlayEdge borderEdge = cellNode.getIncomingEdges().next();//exactly one border edge must exist
                    OverlayNode borderNode = borderEdge.getOtherNode(cellNode);

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayBuilder.getMaxExitNode(borderNode.getLift().getNode(), borderNode.getLift().getEdge(), source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State borderState = new State(levelNode, borderNode.getLift().getEdge());
                        if (!reverseClosedOverlayStates.contains(borderState)) {
                            Distance borderDistance = (reverseOverlayNodeDistanceMap.containsKey(borderState)) ? reverseOverlayNodeDistanceMap.get(borderState) : Distance.newInfinityInstance();
                            Distance newDistance = cellDistance.add(borderNode.getLift().getEdge().getLength(metric));

                            if (newDistance.isLowerThan(borderDistance)) {
                                putOverlayNodeDistance(reverseOverlayNodeDistanceMap, reverseOverlayPqueue, borderState, newDistance, reverseClosedOverlayNodes, cellState);
                                reversePredecessorMap.put(cellState, overlayState);
                                reversePredecessorMap.put(borderState, cellState);
                            }
                        }
                    } // routing is going back to L0
                    else {
                        State borderState = new State(borderNode.getLift().getNode(), borderNode.getLift().getEdge());
                        if (!reverseClosedStates.contains(borderState)) {
                            Distance borderDistance = (reverseNodeDistanceMap.containsKey(borderState)) ? reverseNodeDistanceMap.get(borderState) : Distance.newInfinityInstance();
                            Distance newDistance = cellDistance.add(borderNode.getLift().getEdge().getLength(metric));

                            //check connection of forward and reverse run
                            shortestPath = updateShortestPath(shortestPath, closedSaraNodes, borderNode.getLift().getNode(), newDistance.getValue());

                            if (newDistance.isLowerThan(borderDistance)) {
                                putNodeDistance(reverseNodeDistanceMap, reversePqueue, borderState, newDistance, reverseClosedSaraNodes);
                                reversePredecessorMap.put(cellState, overlayState);
                                reversePredecessorMap.put(borderState, cellState);
                            }
                        }
                    }
                }
            }

        }

        //TODO: connect routes from both sides
        //path unpacking
        return unpacker.unpack(overlayBuilder, metric, finalState, predecessorMap);
        //return unpacker.unpack(graph, overlayBuilder, metric, reverseFinalState, reversePredecessorMap);
    }

    /**
     * Puts L0 node to the queue and its tentative distance to the particular
     * map
     *
     * @param nodeDistanceMap map of tentative distances for L0 graph
     * @param pqueue queue of L0 nodes to be relaxed
     * @param node L0 state to be put into the given structures
     * @param distance distance of state
     * @param closedSaraNodes map of L0 closed nodes
     */
    private void putNodeDistance(Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, State<SaraNode, SaraEdge> node, Distance distance, Map<SaraNode, StateDistancePair> closedSaraNodes) {
        pqueue.decreaseKey(node, distance.getValue());
        nodeDistanceMap.put(node, distance);

        //save state for checking the end condition
        StateDistancePair tmpSaraNode = closedSaraNodes.get(node.getNode());
        if (tmpSaraNode != null) {
            if (distance.getValue() < tmpSaraNode.getDistance()) {
                closedSaraNodes.put(node.getNode(), new StateDistancePair(node, distance.getValue()));
            }
        } else {
            closedSaraNodes.put(node.getNode(), new StateDistancePair(node, distance.getValue()));
        }
    }

    /**
     * Puts L1+ node to the queue and its tentative distance to the particular
     * map
     *
     * @param overlayNodeDistanceMap map of tentative distances for L1+ graph
     * @param overlayPqueue queue of L1+ nodes to be relaxed
     * @param overlayState L1+ state to be put into the given structures
     * @param distance distance of state
     * @param closedOverlayNodes map of L1+ closed nodes
     * @param intermediateState cell state
     */
    private void putOverlayNodeDistance(Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, State<OverlayNode, SaraEdge> overlayState, Distance distance, Map<OverlayNode, StateDistancePair> closedOverlayNodes, State intermediateState) {
        overlayPqueue.decreaseKey(overlayState, distance.getValue());
        overlayNodeDistanceMap.put(overlayState, distance);

        //save state for checking the end condition
        StateDistancePair tmpOverlayNode = closedOverlayNodes.get(overlayState.getNode());
        if (tmpOverlayNode != null) {
            if (distance.getValue() < tmpOverlayNode.getDistance()) {
                closedOverlayNodes.put(overlayState.getNode(), new StateDistancePair(intermediateState, distance.getValue()));
            }
        } else {
            closedOverlayNodes.put(overlayState.getNode(), new StateDistancePair(intermediateState, distance.getValue()));
        }
    }

    /**
     * Updates information about the shortest path found so far
     *
     * @param shortestPath current length of the shortest path
     * @param closedNodes map of L0 closed nodes
     * @param node current node
     * @param nodeDistance tentatiive distance of node
     * @return length of the shortest path
     */
    private double updateShortestPath(double shortestPath, Map<SaraNode, StateDistancePair> closedNodes, SaraNode node, double nodeDistance) {
        StateDistancePair pair = closedNodes.get(node);
        if (pair != null) {
            double candDist = pair.getDistance() + nodeDistance;
            //TODO: turn restriction
            if (candDist < shortestPath) {
                return candDist;
            }
        }
        return shortestPath;
    }

    /**
     * Updates information about the shortest path found so far
     *
     * @param shortestPath current length of the shortest path
     * @param closedNodes map of L1+ closed nodes
     * @param node current node
     * @param nodeDistance tentatiive distance of node
     * @param cellState cell state
     * @param direction direction of the algorithm (-1 reverse, 1 forward)
     * @return length of the shortest path
     */
    private double updateShortestPath(double shortestPath, Map<OverlayNode, StateDistancePair> closedNodes, OverlayNode node, double nodeDistance, State<OverlayNode, OverlayEdge> cellState, int direction) {
        StateDistancePair pair = closedNodes.get(node);
        if (pair != null) {
            double candDist = pair.getDistance() + nodeDistance;
            if (candDist < shortestPath) {
                return candDist;
            }
        }
        return shortestPath;
    }
}

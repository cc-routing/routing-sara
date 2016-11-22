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
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.java8.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class MultilevelBiDijkstraAlgorithm<N extends Node, E extends Edge> implements RoutingAlgorithm<N, E> {

    State finalState = null;
    State reverseFinalState = null;

    public Optional<Route<N, E>> route(Graph<SaraNode, SaraEdge> graph, OverlayBuilder overlayGraph, Metric metric, SaraNode source, SaraNode destination, RouteUnpacker unpacker) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue = new FibonacciHeap<>();

        Map<State<SaraNode, SaraEdge>, Distance> reverseNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> reversePqueue = new FibonacciHeap<>();
        Map<State<OverlayNode, SaraEdge>, Distance> reverseOverlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> reverseOverlayPqueue = new FibonacciHeap<>();

        Map<SaraNode, EdgeDistancePair> closedSaraNodes = new HashMap<>();
        Map<SaraNode, EdgeDistancePair> reverseClosedSaraNodes = new HashMap<>();

        putNodeDistance(nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>(source, null), Distance.newInstance(0), closedSaraNodes);
        putNodeDistance(reverseNodeDistanceMap, reversePqueue, new State<SaraNode, SaraEdge>(destination, null), Distance.newInstance(0), reverseClosedSaraNodes);

        return route(graph, overlayGraph, metric, nodeDistanceMap, pqueue, overlayNodeDistanceMap, overlayPqueue, reverseNodeDistanceMap, reversePqueue, reverseOverlayNodeDistanceMap, reverseOverlayPqueue, source, destination, unpacker, closedSaraNodes, reverseClosedSaraNodes);
    }

    @Override
    public Optional<Route<N, E>> route(Graph<N, E> graph, Metric metric, N source, N destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route(Graph<N, E> graph, Metric metric, E source, E destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route(Graph<N, E> graph, Metric metric, E source, E destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Optional<Route<N, E>> route(Graph<SaraNode, SaraEdge> graph, OverlayBuilder overlayGraph, Metric metric, Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, Map<State<SaraNode, SaraEdge>, Distance> reverseNodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> reversePqueue, Map<State<OverlayNode, SaraEdge>, Distance> reverseOverlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> reverseOverlayPqueue, SaraNode source, SaraNode target, RouteUnpacker unpacker, Map<SaraNode, EdgeDistancePair> closedSaraNodes, Map<SaraNode, EdgeDistancePair> reverseClosedSaraNodes) {
        Set<State> closedStates = new HashSet<>();
        Set<State> closedOverlayStates = new HashSet<>();
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> reverseClosedStates = new HashSet<>();
        Set<State> reverseClosedOverlayStates = new HashSet<>();
        Map<State, State> reversePredecessorMap = new HashMap<>();
        Map<OverlayNode, EdgeDistancePair> closedOverlayNodes = new HashMap<>();
        Map<OverlayNode, EdgeDistancePair> reverseClosedOverlayNodes = new HashMap<>();
        
        int noIter = 0;

        double shortestPath = Double.MAX_VALUE;
        long startTime = System.currentTimeMillis();

        while (!pqueue.isEmpty() || !overlayPqueue.isEmpty() || !reversePqueue.isEmpty() || !reverseOverlayPqueue.isEmpty()) {
            //End condition
            System.out.println("fronta min " + (Math.min(pqueue.minValue(), overlayPqueue.minValue()) + Math.min(reversePqueue.minValue(), reverseOverlayPqueue.minValue())));
            System.out.println(pqueue.minValue() + "," + overlayPqueue.minValue() + "   " + reversePqueue.minValue() + "," + reverseOverlayPqueue.minValue());
            System.out.println(pqueue.size() + "," + overlayPqueue.size() + "   " + reversePqueue.size() + "," + reverseOverlayPqueue.size());
            if (shortestPath == 547432.2183971961 || shortestPath < (Math.min(pqueue.minValue(), overlayPqueue.minValue()) + Math.min(reversePqueue.minValue(), reverseOverlayPqueue.minValue()))) {
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
                Iterator<SaraEdge> edges = graph.getOutgoingEdges(state.getNode());
                while (edges.hasNext()) {
                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = graph.getOtherNode(transferEdge, state.getNode());

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxEntryNode(transferNode, transferEdge, source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State transferState = new State(levelNode, transferEdge);
                        if (!closedOverlayStates.contains(transferState)) {
                            Distance transferDistance = (overlayNodeDistanceMap.containsKey(transferState)) ? overlayNodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(graph.getLength(metric, transferEdge)).add(state.isFirst() ? Distance.newInstance(0) : graph.getTurnCost(state.getNode(), state.getEdge(), transferEdge));

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
                            Distance alternativeDistance = distance.add(graph.getLength(metric, transferEdge)).add(state.isFirst() ? Distance.newInstance(0) : graph.getTurnCost(state.getNode(), state.getEdge(), transferEdge));

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
            // ----------)(----------->o----------------------------->o------------)(----------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode    traverseEdge    traverseNode
            else {
                noIter++;
                State<OverlayNode, SaraEdge> overlayState = overlayPqueue.extractMin();
                Distance distance = overlayNodeDistanceMap.get(overlayState);
                closedOverlayStates.add(overlayState);

                //get the overlay graph from the level of OverlayNode
                OverlayGraph oGraph = overlayGraph.getPartitions().get(overlayState.getNode().level()).getOverlayGraph();

                //relax neighboring nodes, i.e. exit points in the particular cell + corresponding border edge
                Iterator<OverlayEdge> edges = oGraph.getOutgoingEdges(overlayState.getNode());
                while (edges.hasNext()) {
                    OverlayEdge transferEdge = edges.next();
                    OverlayNode transferNode = oGraph.getOtherNode(transferEdge, overlayState.getNode());
                    State transferState = new State(transferNode, transferEdge);

                    //make a move inside the cell through the shortcut
                    //transfer node is not store anywhere since we are interested in traverse node only
                    Distance alternativeDistance = distance.add(oGraph.getLength(metric, transferEdge));

                    //check connection of forward and reverse run
                    shortestPath = updateShortestPath(shortestPath, reverseClosedOverlayNodes, transferNode, alternativeDistance.getValue(), transferState, 1);

                    //use traverse edge to the next cell
                    OverlayEdge traverseEdge = oGraph.getOutgoingEdges(transferNode).next();//exactly one border edge must exist
                    OverlayNode traverseNode = oGraph.getOtherNode(traverseEdge, transferNode);

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxEntryNode(traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge(), source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State traverseState = new State(levelNode, traverseNode.getColumn().getEdge());
                        if (!closedOverlayStates.contains(traverseState)) {
                            Distance traverseDistance = (overlayNodeDistanceMap.containsKey(traverseState)) ? overlayNodeDistanceMap.get(traverseState) : Distance.newInfinityInstance();
                            Distance newDistance = alternativeDistance.add(graph.getLength(metric, traverseNode.getColumn().getEdge()));

                            if (newDistance.isLowerThan(traverseDistance)) {
                                putOverlayNodeDistance(overlayNodeDistanceMap, overlayPqueue, traverseState, newDistance, closedOverlayNodes, transferState);
                                predecessorMap.put(transferState, overlayState);
                                predecessorMap.put(traverseState, transferState);
                            }
                        }
                    } // routing is going back to L0
                    else {
                        State traverseState = new State(traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge());
                        if (!closedStates.contains(traverseState)) {
                            Distance traverseDistance = (nodeDistanceMap.containsKey(traverseState)) ? nodeDistanceMap.get(traverseState) : Distance.newInfinityInstance();
                            Distance newDistance = alternativeDistance.add(graph.getLength(metric, traverseNode.getColumn().getEdge()));

                            //check connection of forward and reverse run
                            shortestPath = updateShortestPath(shortestPath, reverseClosedSaraNodes, traverseNode.getColumn().getNode(), newDistance.getValue());

                            if (newDistance.isLowerThan(traverseDistance)) {
                                putNodeDistance(nodeDistanceMap, pqueue, traverseState, newDistance, closedSaraNodes);
                                predecessorMap.put(transferState, overlayState);
                                predecessorMap.put(traverseState, transferState);
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
                Iterator<SaraEdge> edges = graph.getIncomingEdges(state.getNode());
                while (edges.hasNext()) {
                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = graph.getOtherNode(transferEdge, state.getNode());

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxExitNode(transferNode, transferEdge, source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State transferState = new State(levelNode, transferEdge);
                        if (!reverseClosedOverlayStates.contains(transferState)) {
                            Distance transferDistance = (reverseOverlayNodeDistanceMap.containsKey(transferState)) ? reverseOverlayNodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(graph.getLength(metric, transferEdge)).add(state.isFirst() ? Distance.newInstance(0) : graph.getTurnCost(state.getNode(), transferEdge, state.getEdge()));

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
                            Distance alternativeDistance = distance.add(graph.getLength(metric, transferEdge)).add(state.isFirst() ? Distance.newInstance(0) : graph.getTurnCost(state.getNode(), transferEdge, state.getEdge()));

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
            // ----------)(----------->o----------------------------->o------------)(----------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode    traverseEdge    traverseNode
            else {
                noIter++;
                State<OverlayNode, SaraEdge> overlayState = reverseOverlayPqueue.extractMin();
                Distance distance = reverseOverlayNodeDistanceMap.get(overlayState);
                reverseClosedOverlayStates.add(overlayState);

                //get the overlay graph from the level of OverlayNode
                OverlayGraph oGraph = overlayGraph.getPartitions().get(overlayState.getNode().level()).getOverlayGraph();

                //relax neighboring nodes, i.e. exit points in the particular cell + corresponding border edge
                Iterator<OverlayEdge> edges = oGraph.getIncomingEdges(overlayState.getNode());
                while (edges.hasNext()) {
                    OverlayEdge transferEdge = edges.next();
                    OverlayNode transferNode = oGraph.getOtherNode(transferEdge, overlayState.getNode());
                    State transferState = new State(transferNode, transferEdge);

                    //make a move inside the cell through the shortcut
                    //transfer node is not store anywhere since we are interested in traverse node only
                    Distance alternativeDistance = distance.add(oGraph.getLength(metric, transferEdge));

                    //check connection of forward and reverse run
                    shortestPath = updateShortestPath(shortestPath, closedOverlayNodes, transferNode, alternativeDistance.getValue(), transferState, -1);

                    //use traverse edge to the next cell
                    OverlayEdge traverseEdge = oGraph.getIncomingEdges(transferNode).next();//exactly one border edge must exist
                    OverlayNode traverseNode = oGraph.getOtherNode(traverseEdge, transferNode);

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxExitNode(traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge(), source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State traverseState = new State(levelNode, traverseNode.getColumn().getEdge());
                        if (!reverseClosedOverlayStates.contains(traverseState)) {
                            Distance traverseDistance = (reverseOverlayNodeDistanceMap.containsKey(traverseState)) ? reverseOverlayNodeDistanceMap.get(traverseState) : Distance.newInfinityInstance();
                            Distance newDistance = alternativeDistance.add(graph.getLength(metric, traverseNode.getColumn().getEdge()));

                            if (newDistance.isLowerThan(traverseDistance)) {
                                putOverlayNodeDistance(reverseOverlayNodeDistanceMap, reverseOverlayPqueue, traverseState, newDistance, reverseClosedOverlayNodes, transferState);
                                reversePredecessorMap.put(transferState, overlayState);
                                reversePredecessorMap.put(traverseState, transferState);
                            }
                        }
                    } // routing is going back to L0
                    else {
                        State traverseState = new State(traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge());
                        if (!reverseClosedStates.contains(traverseState)) {
                            Distance traverseDistance = (reverseNodeDistanceMap.containsKey(traverseState)) ? reverseNodeDistanceMap.get(traverseState) : Distance.newInfinityInstance();
                            Distance newDistance = alternativeDistance.add(graph.getLength(metric, traverseNode.getColumn().getEdge()));

                            //check connection of forward and reverse run
                            shortestPath = updateShortestPath(shortestPath, closedSaraNodes, traverseNode.getColumn().getNode(), newDistance.getValue());

                            if (newDistance.isLowerThan(traverseDistance)) {
                                putNodeDistance(reverseNodeDistanceMap, reversePqueue, traverseState, newDistance, reverseClosedSaraNodes);
                                reversePredecessorMap.put(transferState, overlayState);
                                reversePredecessorMap.put(traverseState, transferState);
                            }
                        }
                    }
                }
            }

        }

        //path unpacking
        return unpacker.unpack(graph, overlayGraph, metric, finalState, predecessorMap);
        //return unpacker.unpack(graph, overlayGraph, metric, reverseFinalState, reversePredecessorMap);
        //return null;
    }

    private void putNodeDistance(Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, State<SaraNode, SaraEdge> node, Distance distance, Map<SaraNode, EdgeDistancePair> closedSaraNodes) {
        pqueue.decreaseKey(node, distance.getValue());
        nodeDistanceMap.put(node, distance);

        //save state for checking the end condition
        EdgeDistancePair tmpSaraNode = closedSaraNodes.get(node.getNode());
        if (tmpSaraNode != null) {
            if (distance.getValue() < tmpSaraNode.getDistance()) {
                closedSaraNodes.put(node.getNode(), new EdgeDistancePair(node, distance.getValue()));
            }
        } else {
            closedSaraNodes.put(node.getNode(), new EdgeDistancePair(node, distance.getValue()));
        }
    }

    private void putOverlayNodeDistance(Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, State<OverlayNode, SaraEdge> overlayState, Distance distance, Map<OverlayNode, EdgeDistancePair> closedOverlayNodes, State intermediateState) {
        overlayPqueue.decreaseKey(overlayState, distance.getValue());
        overlayNodeDistanceMap.put(overlayState, distance);

        //save state for checking the end condition
        EdgeDistancePair tmpOverlayNode = closedOverlayNodes.get(overlayState.getNode());
        if (tmpOverlayNode != null) {
            if (distance.getValue() < tmpOverlayNode.getDistance()) {
                closedOverlayNodes.put(overlayState.getNode(), new EdgeDistancePair(intermediateState, distance.getValue()));
            }
        } else {
            closedOverlayNodes.put(overlayState.getNode(), new EdgeDistancePair(intermediateState, distance.getValue()));
        }
    }

    private double updateShortestPath(double shortestPath, Map<SaraNode, EdgeDistancePair> closedNodes, SaraNode node, double nodeDistance) {
        EdgeDistancePair pair = closedNodes.get(node);
        if (pair != null) {
            double candDist = pair.getDistance() + nodeDistance;
            //TODO: turn restriction
            if (candDist < shortestPath) {
                return candDist;
            }
        }
        return shortestPath;
    }

    private double updateShortestPath(double shortestPath, Map<OverlayNode, EdgeDistancePair> closedNodes, OverlayNode node, double nodeDistance, State<OverlayNode, OverlayEdge> transferState, int direction) {
        EdgeDistancePair pair = closedNodes.get(node);
        if (pair != null) {
            double candDist = pair.getDistance() + nodeDistance;
            if(candDist == 547432.2183971961) {
                System.out.println("JUCHU");
                System.out.println(pair.getDistance() + " + " + nodeDistance);
            }
            if (candDist < shortestPath) {
                return candDist;
            }
        }
        return shortestPath;
    }
}

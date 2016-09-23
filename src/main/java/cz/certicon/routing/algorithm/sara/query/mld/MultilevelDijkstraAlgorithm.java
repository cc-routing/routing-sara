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
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.java8.Optional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class MultilevelDijkstraAlgorithm implements RoutingAlgorithm<SaraNode, SaraEdge> {

    public Optional<Route<SaraNode,SaraEdge>> route(Graph<SaraNode, SaraEdge> graph, OverlayBuilder overlayGraph, Metric metric, SaraNode source, SaraNode destination) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue = new FibonacciHeap<>();
        putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( source, null ), Distance.newInstance( 0 ) );
        return route(graph, overlayGraph, metric, nodeDistanceMap, pqueue, overlayNodeDistanceMap, overlayPqueue, source, destination);
    }

     @Override
    public Optional<Route<SaraNode, SaraEdge>> route(Graph<SaraNode, SaraEdge> graph, Metric metric, SaraNode source, SaraNode destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<SaraNode, SaraEdge>> route(Graph<SaraNode, SaraEdge> graph, Metric metric, SaraEdge source, SaraEdge destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<SaraNode, SaraEdge>> route(Graph<SaraNode, SaraEdge> graph, Metric metric, SaraEdge source, SaraEdge destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // TODO:
    // 1) closedOverlayStates - is it necessary to consider both node and edge? Isn't node enough?
    // 2) unpacker
    // 3) bidirectional MLD
    // 4) should we deal with (distance, closed state, ..) transfer node in overlayGraph?
    private Optional<Route<SaraNode, SaraEdge>> route(Graph<SaraNode, SaraEdge> graph, OverlayBuilder overlayGraph, Metric metric, Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, SaraNode source, SaraNode target) {

        Set<State> closedStates = new HashSet<>();
        Set<State> closedOverlayStates = new HashSet<>();

        while (!pqueue.isEmpty() || !overlayPqueue.isEmpty()) {
            //L0 graph
            // --------------------->o---------------------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode
            if (overlayPqueue.isEmpty() || (!pqueue.isEmpty() && pqueue.minValue() < overlayPqueue.minValue())) {
                State<SaraNode, SaraEdge> state = pqueue.extractMin();
                Distance distance = nodeDistanceMap.get(state);
                closedStates.add(state);

                //end condition - it has to be checked only in L0 graph. Once the target node is closed, its tentative distance cannot be improved.
                if(state.getNode().getId() == target.getId()) {
                    //System.out.println(nodeDistanceMap.get(state));
                    break;
                }

                //relax neighboring nodes
                Iterator<SaraEdge> edges = graph.getOutgoingEdges(state.getNode());
                while (edges.hasNext()) {
                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = graph.getOtherNode(transferEdge, state.getNode());

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxOverlayNode(transferNode, transferEdge, source, target);

                    // upper levels can be used further
                    if (levelNode != null) {
                        State transferState = new State(levelNode, transferEdge);
                        if (!closedOverlayStates.contains(transferState)) {
                            Distance transferDistance = (overlayNodeDistanceMap.containsKey(transferState)) ? overlayNodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(graph.getLength(metric, transferEdge)).add(state.isFirst() ? Distance.newInstance(0) : graph.getTurnCost(state.getNode(), state.getEdge(), transferEdge));
                            if (alternativeDistance.isLowerThan(transferDistance)) {
                                putOverlayNodeDistance(overlayNodeDistanceMap, overlayPqueue, transferState, alternativeDistance,true);
                            }
                        }
                    } // routing is still done in L0
                    else {
                        State transferState = new State(transferNode, transferEdge);
                        if (!closedStates.contains(transferState)) {
                            Distance transferDistance = (nodeDistanceMap.containsKey(transferState)) ? nodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add(graph.getLength(metric, transferEdge)).add(state.isFirst() ? Distance.newInstance(0) : graph.getTurnCost(state.getNode(), state.getEdge(), transferEdge));
                            if (alternativeDistance.isLowerThan(transferDistance)) {
                                putNodeDistance(nodeDistanceMap, pqueue, transferState, alternativeDistance);
                            }
                        }
                    }
                }
            }
            //overlay graph
            // --------------------->o----------------------------->o----------------------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode    traverseEdge    traverseNode
            else {
                State<OverlayNode, SaraEdge> overlayState = overlayPqueue.extractMin();
                Distance distance = overlayNodeDistanceMap.get(overlayState);
                closedOverlayStates.add(overlayState);

                //get the overlay graph from the level of OverlayNode
                OverlayGraph oGraph = overlayGraph.getPartitions().get(overlayState.getNode().level()).getOverlayGraph();

                 //relax neighboring nodes, i.e. exit points in the particular cell
                Iterator<OverlayEdge> edges = oGraph.getOutgoingEdges(overlayState.getNode());
                while (edges.hasNext()) {
                    OverlayEdge transferEdge = edges.next();
                    OverlayNode transferNode = oGraph.getOtherNode(transferEdge, overlayState.getNode());

                    //State transferState = new State(transferNode, transferEdge);
                    //if (!closedOverlayStates.contains(transferState)) {
                        //make a move inside the cell through the shortcut
                        //transfer node is not store anywhere since we are interested in traverse node only

                        //Distance transferDistance = (overlayNodeDistanceMap.containsKey(transferState)) ? overlayNodeDistanceMap.get(transferState) : Distance.newInfinityInstance();
                        Distance alternativeDistance = distance.add(oGraph.getLength(metric, transferEdge));
                        //if (alternativeDistance.isLowerThan(transferDistance)) {
                            //putOverlayNodeDistance(overlayNodeDistanceMap, overlayPqueue, transferState, alternativeDistance,false);

                            //use traverse transferEdge to the next cell
                            OverlayEdge traverseEdge = oGraph.getOutgoingEdges(transferNode).next();
                            OverlayNode traverseNode = oGraph.getOtherNode(traverseEdge, transferNode);

                            //find OverlayNode at maximal level, where three of nodes are still in different cells
                            OverlayNode levelNode = overlayGraph.getMaxOverlayNode(traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge(), source, target);        

                            // upper levels can be used further
                            if (levelNode != null) {
                                State traverseState = new State(traverseNode, traverseNode.getColumn().getEdge());
                                if (!closedOverlayStates.contains(traverseState)) {
                                    Distance traverseDistance = (overlayNodeDistanceMap.containsKey(traverseState)) ? overlayNodeDistanceMap.get(traverseState) : Distance.newInfinityInstance();
                                    Distance newDistance = alternativeDistance.add(graph.getLength(metric, traverseNode.getColumn().getEdge()));
                                    if (newDistance.isLowerThan(traverseDistance)) {
                                        putOverlayNodeDistance(overlayNodeDistanceMap, overlayPqueue, traverseState, newDistance, true);
                                    }
                                }
                            } // routing is going back to L0
                            else {
                                State traverseState = new State(traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge());
                                if (!closedStates.contains(traverseState)) {
                                    Distance traverseDistance = (nodeDistanceMap.containsKey(traverseState)) ? nodeDistanceMap.get(traverseState) : Distance.newInfinityInstance();
                                    Distance newDistance = alternativeDistance.add(graph.getLength(metric, traverseNode.getColumn().getEdge()));
                                    if (newDistance.isLowerThan(traverseDistance)) {
                                        putNodeDistance(nodeDistanceMap, pqueue, traverseState, newDistance);
                                    }
                                }
                            }
                        //}
                    //}

                }
            }
        }

        return null;
    }

    private void putNodeDistance(Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, State<SaraNode, SaraEdge> node, Distance distance) {
        pqueue.decreaseKey(node, distance.getValue());
        nodeDistanceMap.put(node, distance);
    }

    private void putOverlayNodeDistance(Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, State<OverlayNode, SaraEdge> overlayNode, Distance distance, boolean withPqueueUpdate) {
        if(withPqueueUpdate) overlayPqueue.decreaseKey(overlayNode, distance.getValue());
        overlayNodeDistanceMap.put(overlayNode, distance);
    }
}

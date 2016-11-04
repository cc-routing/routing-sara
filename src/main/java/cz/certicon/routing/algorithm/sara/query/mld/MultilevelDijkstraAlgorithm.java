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
import java8.util.Optional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @param <N> node type
 * @param <E> edge type
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 */
public class MultilevelDijkstraAlgorithm<N extends Node, E extends Edge> implements RoutingAlgorithm<N, E> {

    public Optional<Route<N, E>> route( Graph<SaraNode, SaraEdge> graph, OverlayBuilder overlayGraph, Metric metric, SaraNode source, SaraNode destination, RouteUnpacker unpacker ) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue = new FibonacciHeap<>();
        putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( source, null ), Distance.newInstance( 0 ) );
        return route( graph, overlayGraph, metric, nodeDistanceMap, pqueue, overlayNodeDistanceMap, overlayPqueue, source, destination, unpacker );
    }

    @Override
    public Optional<Route<N, E>> route( Graph<N, E> graph, Metric metric, N source, N destination ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route( Graph<N, E> graph, Metric metric, E source, E destination ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<N, E>> route( Graph<N, E> graph, Metric metric, E source, E destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    // TODO:
    // 1) closedOverlayStates - is it necessary to consider both node and edge? Isn't node enough?
    // 2) unpacker
    // 3) bidirectional MLD
    // 4) should we deal with (distance, closed state, ..) transfer node in overlayGraph?
    // 5) should closed states and distances for SaraNode and OverlayNode have separated variables?
    private Optional<Route<N, E>> route( Graph<SaraNode, SaraEdge> graph, OverlayBuilder overlayGraph, Metric metric, Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, SaraNode source, SaraNode target, RouteUnpacker unpacker ) {
        Set<State> closedStates = new HashSet<>();
        Set<State> closedOverlayStates = new HashSet<>();
        Map<State, State> predecessorMap = new HashMap<>();
        State finalState = null;

        while ( !pqueue.isEmpty() || !overlayPqueue.isEmpty() ) {
            //L0 graph
            // --------------------->o---------------------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode
            if ( overlayPqueue.isEmpty() || ( !pqueue.isEmpty() && pqueue.minValue() < overlayPqueue.minValue() ) ) {
                State<SaraNode, SaraEdge> state = pqueue.extractMin();
                Distance distance = nodeDistanceMap.get( state );
                closedStates.add( state );

                //end condition - it has to be checked only in L0 graph. Once the target node is closed, its tentative distance cannot be improved.
                if ( state.getNode().getId() == target.getId() ) {
                    //System.out.println("MLD - final state >>> " + nodeDistanceMap.get(state));
                    finalState = state;
                    break;
                }

                //relax neighboring nodes
                Iterator<SaraEdge> edges = graph.getOutgoingEdges( state.getNode() );
                while ( edges.hasNext() ) {
                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = transferEdge.getOtherNode( state.getNode() );

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxOverlayNode( transferNode, transferEdge, source, target );

                    // upper levels can be used further
                    if ( levelNode != null ) {
                        State transferState = new State( levelNode, transferEdge );
                        if ( !closedOverlayStates.contains( transferState ) ) {
                            Distance transferDistance = ( overlayNodeDistanceMap.containsKey( transferState ) ) ? overlayNodeDistanceMap.get( transferState ) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add( transferEdge.getLength( metric ) ).add( state.isFirst() ? Distance.newInstance( 0 ) : state.getNode().getTurnDistance( state.getEdge(), transferEdge ) );

                            if ( alternativeDistance.isLowerThan( transferDistance ) ) {
                                putOverlayNodeDistance( overlayNodeDistanceMap, overlayPqueue, transferState, alternativeDistance );
                                predecessorMap.put( transferState, state );
                            }
                        }
                    } // routing is still done in L0
                    else {
                        State transferState = new State( transferNode, transferEdge );
                        if ( !closedStates.contains( transferState ) ) {
                            Distance transferDistance = ( nodeDistanceMap.containsKey( transferState ) ) ? nodeDistanceMap.get( transferState ) : Distance.newInfinityInstance();
                            Distance alternativeDistance = distance.add( transferEdge.getLength( metric ) ).add( state.isFirst() ? Distance.newInstance( 0 ) : state.getNode().getTurnDistance( state.getEdge(), transferEdge ) );

                            if ( alternativeDistance.isLowerThan( transferDistance ) ) {
                                putNodeDistance( nodeDistanceMap, pqueue, transferState, alternativeDistance );
                                predecessorMap.put( transferState, state );
                            }
                        }
                    }
                }
            } // overlay graph
            // ----------)(----------->o----------------------------->o------------)(----------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode    traverseEdge    traverseNode
            else {
                State<OverlayNode, SaraEdge> overlayState = overlayPqueue.extractMin();
                Distance distance = overlayNodeDistanceMap.get( overlayState );
                closedOverlayStates.add( overlayState );

                //get the overlay graph from the level of OverlayNode
                OverlayGraph oGraph = overlayGraph.getPartitions().get( overlayState.getNode().level() ).getOverlayGraph();

                //relax neighboring nodes, i.e. exit points in the particular cell + corresponding border edge
                Iterator<OverlayEdge> edges = oGraph.getOutgoingEdges( overlayState.getNode() );
                while ( edges.hasNext() ) {
                    OverlayEdge transferEdge = edges.next();
                    OverlayNode transferNode = transferEdge.getOtherNode( overlayState.getNode() );
                    State transferState = new State( transferNode, transferEdge );

                    //make a move inside the cell through the shortcut
                    //transfer node is not store anywhere since we are interested in traverse node only
                    Distance alternativeDistance = distance.add( transferEdge.getLength( metric ) );

                    //use traverse edge to the next cell
                    OverlayEdge traverseEdge = oGraph.getOutgoingEdges( transferNode ).next();//exactly one border edge must exist
                    OverlayNode traverseNode = transferEdge.getOtherNode( transferNode );

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxOverlayNode( traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge(), source, target );

                    // upper levels can be used further
                    if ( levelNode != null ) {
                        State traverseState = new State( levelNode, traverseNode.getColumn().getEdge() );
                        if ( !closedOverlayStates.contains( traverseState ) ) {
                            Distance traverseDistance = ( overlayNodeDistanceMap.containsKey( traverseState ) ) ? overlayNodeDistanceMap.get( traverseState ) : Distance.newInfinityInstance();
                            Distance newDistance = alternativeDistance.add( traverseNode.getColumn().getEdge().getLength( metric ) );

                            if ( newDistance.isLowerThan( traverseDistance ) ) {
                                putOverlayNodeDistance( overlayNodeDistanceMap, overlayPqueue, traverseState, newDistance );
                                predecessorMap.put( transferState, overlayState );
                                predecessorMap.put( traverseState, transferState );
                            }
                        }
                    } // routing is going back to L0
                    else {
                        State traverseState = new State( traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge() );
                        if ( !closedStates.contains( traverseState ) ) {
                            Distance traverseDistance = ( nodeDistanceMap.containsKey( traverseState ) ) ? nodeDistanceMap.get( traverseState ) : Distance.newInfinityInstance();
                            Distance newDistance = alternativeDistance.add( traverseNode.getColumn().getEdge().getLength( metric ) );

                            if ( newDistance.isLowerThan( traverseDistance ) ) {
                                putNodeDistance( nodeDistanceMap, pqueue, traverseState, newDistance );
                                predecessorMap.put( transferState, overlayState );
                                predecessorMap.put( traverseState, transferState );
                            }
                        }
                    }
                }
            }
        }

        //path unpacking
        return unpacker.unpack( graph, overlayGraph, metric, finalState, predecessorMap );
    }

    private void putNodeDistance( Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, State<SaraNode, SaraEdge> node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }

    private void putOverlayNodeDistance( Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, State<OverlayNode, SaraEdge> overlayNode, Distance distance ) {
        overlayPqueue.decreaseKey( overlayNode, distance.getValue() );
        overlayNodeDistanceMap.put( overlayNode, distance );
    }
}

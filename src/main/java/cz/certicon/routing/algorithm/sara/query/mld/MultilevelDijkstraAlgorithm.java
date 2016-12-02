/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.*;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RoutingPoint;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java8.util.Optional;

/**
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 */
public class MultilevelDijkstraAlgorithm implements RoutingAlgorithm<SaraNode, SaraEdge> {

    private final OverlayBuilder overlayGraph;
    private final RouteUnpacker unpacker;


    public MultilevelDijkstraAlgorithm( OverlayBuilder overlayGraph, RouteUnpacker unpacker ) {
        this.overlayGraph = overlayGraph;
        this.unpacker = unpacker;
    }

    public Optional<Route<SaraNode, SaraEdge>> route( OverlayBuilder overlayGraph, Metric metric, SaraNode source, SaraNode destination, RouteUnpacker unpacker ) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( source, null ), Distance.newInstance( 0 ) );
        return route( metric, nodeDistanceMap, pqueue, source, destination, new NodeEndCondition( destination ) );
    }

    @Override
    public Optional<Route<SaraNode, SaraEdge>> route( Metric metric, SaraNode source, SaraNode destination ) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        ZeroNode sourceNode = overlayGraph.getZeroNode( source );
        ZeroNode destinationNode = overlayGraph.getZeroNode( destination );
        putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( sourceNode, null ), Distance.newInstance( 0 ) );
        EndCondition endCondition = new NodeEndCondition( destinationNode );
        return route( metric, nodeDistanceMap, pqueue, sourceNode, destinationNode, endCondition );
    }

    @Override
    public Optional<Route<SaraNode, SaraEdge>> route( Metric metric, SaraEdge source, SaraEdge destination ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<SaraNode, SaraEdge>> route( Metric metric, SaraEdge source, SaraEdge destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<Route<SaraNode, SaraEdge>> route( Metric metric, RoutingPoint<SaraNode, SaraEdge> source, RoutingPoint<SaraNode, SaraEdge> destination ) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        SaraEdge singleEdgePath = null;
        ZeroNode sourceNode = null;
        ZeroEdge sourceEdge = null;
        if ( !source.isCrossroad() && !source.getEdge().get().getSource().getParent().equals( source.getEdge().get().getTarget().getParent() ) ) {
            sourceNode = overlayGraph.getZeroNode( source.getEdge().get().getTarget() );
            putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( sourceNode, null ), Distance.newInstance( 0 ) );
        } else if ( source.isCrossroad() ) {
            sourceNode = overlayGraph.getZeroNode( source.getNode().get() );
            putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( sourceNode, null ), Distance.newInstance( 0 ) );
//            System.out.println( "source = " + sourceNode );
        } else {
            sourceNode = overlayGraph.getZeroNode( source.getEdge().get().getTarget() );
            for ( SaraEdge e : sourceNode.getEdges() ) {
                if ( Math.abs( e.getId() ) == source.getEdge().get().getId() ) {
                    sourceEdge = (ZeroEdge) e;
                    break;
                }
            }
            if ( source.equals( destination ) ) {
                Distance substract = source.getDistanceToTarget( metric ).orElse( Distance.newZeroDistance() ).subtract( destination.getDistanceToTarget( metric ).orElse( Distance.newZeroDistance() ) );
                if ( source.getEdge().get().isOneWay() ) {
                    // is positive or zero
                    if ( !substract.isNegative() ) {
                        singleEdgePath = source.getEdge().orElse( null );
                    }
                } else {
                    singleEdgePath = source.getEdge().orElse( null );
                }
            }
            putNodeDistance( nodeDistanceMap, pqueue, new State( sourceEdge.getTarget(), sourceEdge ), source.getDistanceToTarget( metric ).orElse( Distance.newZeroDistance() ) );
            if ( !sourceEdge.isOneWay() ) {
                putNodeDistance( nodeDistanceMap, pqueue, new State( sourceEdge.getSource(), sourceEdge ), source.getDistanceToSource( metric ).orElse( Distance.newZeroDistance() ) );
            }
//            System.out.println( "source = " + sourceEdge );
        }
        EndCondition endCondition = null;
        ZeroNode destinationNode = null;
        ZeroEdge destinationEdge = null;
        if ( !destination.isCrossroad() && !destination.getEdge().get().getSource().getParent().equals( destination.getEdge().get().getTarget().getParent() ) ) {
            destinationNode = overlayGraph.getZeroNode( destination.getEdge().get().getSource() );
            endCondition = new NodeEndCondition( destinationNode );
        } else if ( destination.isCrossroad() ) {
            destinationNode = overlayGraph.getZeroNode( destination.getNode().get() );
            endCondition = new NodeEndCondition( destinationNode );
        } else {
            destinationNode = overlayGraph.getZeroNode( destination.getEdge().get().getSource() );
            for ( SaraEdge e : destinationNode.getEdges() ) {
                if ( Math.abs( e.getId() ) == destination.getEdge().get().getId() ) {
                    destinationEdge = (ZeroEdge) e;
                    break;
                }
            }
            endCondition = new EdgeEndCondition( destinationEdge );
        }
//        if ( destination.isCrossroad() ) {
//            System.out.println( "destination = " + destinationNode );
//        } else {
//            System.out.println( "destination = " + destinationEdge );
//        }
        return route( metric, nodeDistanceMap, pqueue, sourceNode, destinationNode, endCondition );
    }

    // TODO:
    // 1) closedOverlayStates - is it necessary to consider both node and edge? Isn't node enough?
    // 2) unpacker
    // 3) bidirectional MLD
    // 4) should we deal with (distance, closed state, ..) transfer node in overlayGraph?
    // 5) should closed states and distances for SaraNode and OverlayNode have separated variables?
    private Optional<Route<SaraNode, SaraEdge>> route( Metric metric, Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, SaraNode source, SaraNode target, EndCondition endCondition ) {
        Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue = new FibonacciHeap<>();
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
                if ( endCondition.shouldTerminate( state ) ) {
                    System.out.println( "MLD - final state >>> " + nodeDistanceMap.get( state ) );
                    finalState = state;
                    break;
                }

                //relax neighboring nodes
                Iterator<SaraEdge> edges = state.getNode().getOutgoingEdges();
                while ( edges.hasNext() ) {
                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = transferEdge.getOtherNode( state.getNode() );

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxEntryNode( transferNode, (ZeroEdge) transferEdge, source, target );

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

                //relax neighboring nodes, i.e. exit points in the particular cell + corresponding border edge
                Iterator<OverlayEdge> edges = overlayState.getNode().getOutgoingEdges();
                while ( edges.hasNext() ) {
                    OverlayEdge transferEdge = edges.next();
                    OverlayNode transferNode = transferEdge.getOtherNode( overlayState.getNode() );
                    State transferState = new State( transferNode, transferEdge );

                    //make a move inside the cell through the shortcut
                    //transfer node is not store anywhere since we are interested in traverse node only
                    Distance alternativeDistance = distance.add( transferEdge.getLength( metric ) );

                    //use traverse edge to the next cell
                    OverlayEdge traverseEdge = transferNode.getOutgoingEdges().next();//exactly one border edge must exist
                    OverlayNode traverseNode = traverseEdge.getOtherNode( transferNode );

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayGraph.getMaxEntryNode( traverseNode.getColumn().getNode(), traverseNode.getColumn().getEdge(), source, target );

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
        return unpacker.unpack( overlayGraph, metric, finalState, predecessorMap );
    }

    private void putNodeDistance( Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, State<SaraNode, SaraEdge> node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }

    private void putOverlayNodeDistance( Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, State<OverlayNode, SaraEdge> overlayNode, Distance distance ) {
        overlayPqueue.decreaseKey( overlayNode, distance.getValue() );
        overlayNodeDistanceMap.put( overlayNode, distance );
    }

    private interface EndCondition {
        boolean shouldTerminate( State<SaraNode, SaraEdge> currentState );
    }

    private static class NodeEndCondition implements EndCondition {
        private final SaraNode finalNode;

        public NodeEndCondition( SaraNode finalNode ) {
            this.finalNode = finalNode;
        }

        @Override
        public boolean shouldTerminate( State<SaraNode, SaraEdge> currentState ) {
            return currentState.getNode().getId() == finalNode.getId();
        }
    }

    private static class EdgeEndCondition implements EndCondition {
        private final SaraEdge finalEdge;

        public EdgeEndCondition( SaraEdge finalEdge ) {
            this.finalEdge = finalEdge;
        }

        @Override
        public boolean shouldTerminate( State<SaraNode, SaraEdge> currentState ) {
//            System.out.println( "should terminate? final edge = " + finalEdge.getId() + ", current edge = " + currentState.getEdge().getId() + ", result = " + ( Math.abs( currentState.getEdge().getId() ) == Math.abs( finalEdge.getId() ) ) );
            return Math.abs( currentState.getEdge().getId() ) == Math.abs( finalEdge.getId() );
        }
    }
}

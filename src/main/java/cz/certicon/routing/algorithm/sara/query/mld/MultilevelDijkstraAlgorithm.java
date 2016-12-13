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
import cz.certicon.routing.model.graph.*;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.values.Distance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cz.certicon.routing.utils.measuring.StatsLogger;
import cz.certicon.routing.utils.measuring.TimeLogger;
import java8.util.Optional;
import java8.util.function.Consumer;

/**
 * Algorithm searches for routes between two points (currently only nodes) based
 * on the multilevel structure
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 */
public class MultilevelDijkstraAlgorithm implements RoutingAlgorithm<SaraNode, SaraEdge> {

    private final OverlayBuilder overlayBuilder;
    private final RouteUnpacker unpacker;

    /**
     * @param overlayBuilder overlay main class
     * @param unpacker       route unpacker
     */
    public MultilevelDijkstraAlgorithm( OverlayBuilder overlayBuilder, RouteUnpacker unpacker ) {
        this.overlayBuilder = overlayBuilder;
        this.unpacker = unpacker;
    }

    /**
     * Returns route between source and target using the given metric
     *
     * @param overlayBuilder overlay main class
     * @param metric         metric
     * @param source         source node
     * @param destination    target node
     * @param unpacker       route unpacker
     * @return optional route (empty if the route was not found)
     */
    public Optional<Route<SaraNode, SaraEdge>> route( OverlayBuilder overlayBuilder, Metric metric, SaraNode source, SaraNode destination, RouteUnpacker unpacker ) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( source, null ), Distance.newInstance( 0 ) );
        return route( metric, nodeDistanceMap, pqueue, source, destination, new NodeEndCondition( destination ) );
    }

    @Override
    public Optional<Route<SaraNode, SaraEdge>> route( Metric metric, SaraNode source, SaraNode destination ) {
        Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<SaraNode, SaraEdge>> pqueue = new FibonacciHeap<>();
        ZeroNode sourceNode = overlayBuilder.getZeroNode( source );
        ZeroNode destinationNode = overlayBuilder.getZeroNode( destination );
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
            sourceNode = overlayBuilder.getZeroNode( source.getEdge().get().getTarget() );
            putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( sourceNode, null ), Distance.newInstance( 0 ) );
        } else if ( source.isCrossroad() ) {
            sourceNode = overlayBuilder.getZeroNode( source.getNode().get() );
            putNodeDistance( nodeDistanceMap, pqueue, new State<SaraNode, SaraEdge>( sourceNode, null ), Distance.newInstance( 0 ) );
        } else {
            sourceNode = overlayBuilder.getZeroNode( source.getEdge().get().getTarget() );
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
        }
        EndCondition endCondition = null;
        ZeroNode destinationNode = null;
        ZeroEdge destinationEdge = null;
        if ( !destination.isCrossroad() && !destination.getEdge().get().getSource().getParent().equals( destination.getEdge().get().getTarget().getParent() ) ) {
            destinationNode = overlayBuilder.getZeroNode( destination.getEdge().get().getSource() );
            endCondition = new NodeEndCondition( destinationNode );
        } else if ( destination.isCrossroad() ) {
            destinationNode = overlayBuilder.getZeroNode( destination.getNode().get() );
            endCondition = new NodeEndCondition( destinationNode );
        } else {
            destinationNode = overlayBuilder.getZeroNode( destination.getEdge().get().getSource() );
            for ( SaraEdge e : destinationNode.getEdges() ) {
                if ( Math.abs( e.getId() ) == destination.getEdge().get().getId() ) {
                    destinationEdge = (ZeroEdge) e;
                    break;
                }
            }
            endCondition = new EdgeEndCondition( destinationEdge );
        }
        return route( metric, nodeDistanceMap, pqueue, sourceNode, destinationNode, endCondition );
    }

    /**
     * Returns route between source and target using the given metric
     *
     * @param metric          metric
     * @param nodeDistanceMap map of tentative distances for L0 graph
     * @param pqueue          queue of L0 nodes to be relaxed
     * @param source          source node
     * @param target          source target
     * @param endCondition    condition for terminating the search
     * @return optional route (empty if the route was not found)
     */
    private Optional<Route<SaraNode, SaraEdge>> route( Metric metric, Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, SaraNode source, SaraNode target, EndCondition endCondition ) {
        Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap = new HashMap<>();
        PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue = new FibonacciHeap<>();
        Set<State> closedStates = new HashSet<>();
        Set<State> closedOverlayStates = new HashSet<>();
        Map<State, State> predecessorMap = new HashMap<>();
        State finalState = null;

        StatsLogger.log( StatsLogger.Statistic.NODES_EXAMINED, StatsLogger.Command.RESET );
        StatsLogger.log( StatsLogger.Statistic.EDGES_RELAXED, StatsLogger.Command.RESET );
        StatsLogger.log( StatsLogger.Statistic.EDGES_VISITED, StatsLogger.Command.RESET );
        TimeLogger.log( TimeLogger.Event.ROUTING, TimeLogger.Command.START );

        while ( !pqueue.isEmpty() || !overlayPqueue.isEmpty() ) {

            StatsLogger.log( StatsLogger.Statistic.NODES_EXAMINED, StatsLogger.Command.INCREMENT );

            //L0 graph
            // --------------------->o---------------------------->o
            // state.getEdge   state.getNode   transferEdge   transferNode
            if ( overlayPqueue.isEmpty() || ( !pqueue.isEmpty() && pqueue.minValue() < overlayPqueue.minValue() ) ) {
                State<SaraNode, SaraEdge> state = pqueue.extractMin();
                Distance distance = nodeDistanceMap.get( state );
                closedStates.add( state );

                //end condition - it has to be checked only in L0 graph. Once the target node is closed, its tentative distance cannot be improved.
                if ( endCondition.shouldTerminate( state ) ) {
//                    System.out.println( "MLD - final state >>> " + nodeDistanceMap.get( state ) );
                    finalState = state;
                    break;
                }

                //relax neighboring nodes
                Iterator<SaraEdge> edges = state.getNode().getOutgoingEdges();
                while ( edges.hasNext() ) {

                    StatsLogger.log( StatsLogger.Statistic.EDGES_VISITED, StatsLogger.Command.INCREMENT );

                    SaraEdge transferEdge = edges.next();
                    SaraNode transferNode = transferEdge.getOtherNode( state.getNode() );

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    Optional<OverlayNode> levelNode = Optional.ofNullable( overlayBuilder.getMaxEntryNode( transferNode, (ZeroEdge) transferEdge, source, target ) );
                    // replace with: levelNode.ifPresentOrElse(
                    //      lvnd -> updateQueue( overlayPqueue, closedOverlayStates, overlayNodeDistanceMap, predecessorMap, metric, state, distance, lvnd, transferEdge )
                    //      , () -> updateQueue( pqueue, closedStates, nodeDistanceMap, predecessorMap, metric, state, distance, transferNode, transferEdge ) );

                    // upper levels can be used further
                    if ( levelNode.isPresent() ) {
                        updateQueue( overlayPqueue, closedOverlayStates, overlayNodeDistanceMap, predecessorMap, metric, state, distance, levelNode.get(), transferEdge );
                    } // routing is still done in L0
                    else {
                        updateQueue( pqueue, closedStates, nodeDistanceMap, predecessorMap, metric, state, distance, transferNode, transferEdge );
                    }
                }
            } // overlay graph
            // ----------)(----------->o--------------------->o-----------)(----------->o
            // state.getEdge   state.getNode   cellEdge   cellNode    borderEdge    borderNode
            else {
                State<OverlayNode, SaraEdge> overlayState = overlayPqueue.extractMin();
                Distance distance = overlayNodeDistanceMap.get( overlayState );
                closedOverlayStates.add( overlayState );

                //relax neighboring nodes, i.e. exit points in the particular cell + corresponding border edge
                Iterator<OverlayEdge> edges = overlayState.getNode().getOutgoingEdges();
                while ( edges.hasNext() ) {

                    StatsLogger.log( StatsLogger.Statistic.EDGES_VISITED, StatsLogger.Command.INCREMENT );

                    OverlayEdge cellEdge = edges.next();
                    OverlayNode cellNode = cellEdge.getOtherNode( overlayState.getNode() );
                    State cellState = new State( cellNode, cellEdge );

                    //make a move inside the cell through the shortcut
                    //cell node is not store anywhere since we are interested in border node only
                    Distance cellDistance = distance.add( cellEdge.getLength( metric ) );

                    //use border edge to the next cell
                    OverlayEdge borderEdge = cellNode.getOutgoingEdges().next();//exactly one border edge must exist
                    OverlayNode borderNode = borderEdge.getOtherNode( cellNode );

                    //find OverlayNode at maximal level, where three of nodes are still in different cells
                    OverlayNode levelNode = overlayBuilder.getMaxEntryNode( borderNode.getLift().getNode(), borderNode.getLift().getEdge(), source, target );

                    // upper levels can be used further
                    if ( levelNode != null ) {
                        State borderState = new State( levelNode, borderNode.getLift().getEdge() );
                        if ( !closedOverlayStates.contains( borderState ) ) {

                            StatsLogger.log( StatsLogger.Statistic.EDGES_RELAXED, StatsLogger.Command.INCREMENT );

                            Distance borderDistance = ( overlayNodeDistanceMap.containsKey( borderState ) ) ? overlayNodeDistanceMap.get( borderState ) : Distance.newInfinityInstance();
                            Distance newDistance = cellDistance.add( borderNode.getLift().getEdge().getLength( metric ) );

                            if ( newDistance.isLowerThan( borderDistance ) ) {
                                putOverlayNodeDistance( overlayNodeDistanceMap, overlayPqueue, borderState, newDistance );
                                predecessorMap.put( cellState, overlayState );
                                predecessorMap.put( borderState, cellState );
                            }
                        }
                    } // routing is going back to L0
                    else {
                        State borderState = new State( borderNode.getLift().getNode(), borderNode.getLift().getEdge() );
                        if ( !closedStates.contains( borderState ) ) {

                            StatsLogger.log( StatsLogger.Statistic.EDGES_RELAXED, StatsLogger.Command.INCREMENT );

                            Distance borderDistance = ( nodeDistanceMap.containsKey( borderState ) ) ? nodeDistanceMap.get( borderState ) : Distance.newInfinityInstance();
                            Distance newDistance = cellDistance.add( borderNode.getLift().getEdge().getLength( metric ) );

                            if ( newDistance.isLowerThan( borderDistance ) ) {
                                putNodeDistance( nodeDistanceMap, pqueue, borderState, newDistance );
                                predecessorMap.put( cellState, overlayState );
                                predecessorMap.put( borderState, cellState );
                            }
                        }
                    }
                }
            }
        }

        TimeLogger.log( TimeLogger.Event.ROUTING, TimeLogger.Command.STOP );
        TimeLogger.log( TimeLogger.Event.ROUTE_BUILDING, TimeLogger.Command.START );

        //path unpacking
        Optional<Route<SaraNode, SaraEdge>> route = unpacker.unpack( overlayBuilder, metric, finalState, predecessorMap );

        TimeLogger.log( TimeLogger.Event.ROUTE_BUILDING, TimeLogger.Command.STOP );

        return route;
    }


    private <T extends Node> void updateQueue(
            PriorityQueue<State<T, SaraEdge>> pqueue,
            Set<State> closedStates,
            Map<State<T, SaraEdge>, Distance> distanceMap,
            Map<State, State> predecessorMap,
            Metric metric,
            State<SaraNode, SaraEdge> currentState,
            Distance currentDistance,
            T otherNode,
            SaraEdge edge ) {

        State transferState = new State( otherNode, edge );
        if ( !closedStates.contains( transferState ) ) {

            StatsLogger.log( StatsLogger.Statistic.EDGES_RELAXED, StatsLogger.Command.INCREMENT );

            Distance transferDistance = ( distanceMap.containsKey( transferState ) ) ? distanceMap.get( transferState ) : Distance.newInfinityInstance();
            Distance alternativeDistance = currentDistance.add( edge.getLength( metric ) ).add( currentState.isFirst() ? Distance.newInstance( 0 ) : currentState.getNode().getTurnDistance( currentState.getEdge(), edge ) );

            if ( alternativeDistance.isLowerThan( transferDistance ) ) {
                pqueue.decreaseKey( transferState, alternativeDistance.getValue() );
                distanceMap.put( transferState, alternativeDistance );
                predecessorMap.put( transferState, currentState );
            }
        }
    }


    /**
     * Puts L0 node to the queue and its tentative distance to the particular
     * map
     *
     * @param nodeDistanceMap map of tentative distances for L0 graph
     * @param pqueue          queue of L0 nodes to be relaxed
     * @param node            L0 state to be put into the given structures
     * @param distance        distance of state
     */
    private void putNodeDistance( Map<State<SaraNode, SaraEdge>, Distance> nodeDistanceMap, PriorityQueue<State<SaraNode, SaraEdge>> pqueue, State<SaraNode, SaraEdge> node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }

    /**
     * Puts L1+ node to the queue and its tentative distance to the particular
     * map
     *
     * @param overlayNodeDistanceMap map of tentative distances for L1+ graph
     * @param overlayPqueue          queue of L1+ nodes to be relaxed
     * @param overlayNode            L1+ state to be put into the given structures
     * @param distance               distance of state
     */
    private void putOverlayNodeDistance( Map<State<OverlayNode, SaraEdge>, Distance> overlayNodeDistanceMap, PriorityQueue<State<OverlayNode, SaraEdge>> overlayPqueue, State<OverlayNode, SaraEdge> overlayNode, Distance distance ) {
        overlayPqueue.decreaseKey( overlayNode, distance.getValue() );
        overlayNodeDistanceMap.put( overlayNode, distance );
    }

    /**
     * Interface for the condition which terminate the search
     */
    private interface EndCondition {

        /**
         * Evaluates whether the search should be terminated or not
         *
         * @param currentState state - {node,edge}
         * @return true if the search should be terminated, false otherwise
         */
        boolean shouldTerminate( State<SaraNode, SaraEdge> currentState );
    }

    /**
     * End condition for nodes
     */
    private static class NodeEndCondition implements EndCondition {

        private final SaraNode finalNode;

        /**
         * @param finalNode node to be checked
         */
        public NodeEndCondition( SaraNode finalNode ) {
            this.finalNode = finalNode;
        }

        @Override
        public boolean shouldTerminate( State<SaraNode, SaraEdge> currentState ) {
            return currentState.getNode().getId() == finalNode.getId();
        }
    }

    /**
     * End condition for edges
     */
    private static class EdgeEndCondition implements EndCondition {

        private final SaraEdge finalEdge;

        /**
         * @param finalEdge edge to be checked
         */
        public EdgeEndCondition( SaraEdge finalEdge ) {
            this.finalEdge = finalEdge;
        }

        @Override
        public boolean shouldTerminate( State<SaraNode, SaraEdge> currentState ) {
            return Math.abs( currentState.getEdge().getId() ) == Math.abs( finalEdge.getId() );
        }
    }
}

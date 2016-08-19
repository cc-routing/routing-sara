/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.model.queue.FibonacciHeap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class DijkstraAlgorithm implements RoutingAlgorithm {

    @Override
    public Route route( Graph graph, Node source, Node destination ) {
        Map<State, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State> pqueue = new FibonacciHeap<>();
        Distance upperBound = Distance.newInfinityInstance();
        putNodeDistance( nodeDistanceMap, pqueue, new State( source, null ), Distance.newInstance( 0 ) );
        return route( graph, nodeDistanceMap, pqueue, upperBound, new NodeEndCondition( destination ), null, null );
    }

    @Override
    public Route route( Graph graph, Edge source, Edge destination ) {
        return route( graph, source, destination, new Distance( 0 ), new Distance( 0 ), new Distance( 0 ), new Distance( 0 ) );
    }

    @Override
    public Route route( Graph graph, Edge source, Edge destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd ) {
        Map<State, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State> pqueue = new FibonacciHeap<>();
        // create upper bound if the edges are equal and mark it
        Distance upperBound = Distance.newInfinityInstance();
        Edge singleEdgePath = null;
        if ( source.equals( destination ) ) {
            Distance substract = toSourceEnd.substract( toDestinationEnd );
            if ( source.isOneway() ) {
                // is positive or zero
                if ( !substract.isNegative() ) {
                    upperBound = substract;
                    singleEdgePath = source;
                }
            } else {
                upperBound = substract.absolute();
                singleEdgePath = source;
            }
        }
        putNodeDistance( nodeDistanceMap, pqueue, new State( source.getTarget(), source ), toSourceEnd );
        if ( !source.isOneway() ) {
            putNodeDistance( nodeDistanceMap, pqueue, new State( source.getSource(), source ), toSourceStart );
        }
        return route( graph, nodeDistanceMap, pqueue, upperBound, new EdgeEndCondition( graph, destination, toDestinationStart, toDestinationEnd ), singleEdgePath, destination );
    }

    private Route route( Graph graph, Map<State, Distance> nodeDistanceMap, PriorityQueue<State> pqueue, Distance upperBound, EndCondition endCondition, Edge singleEdgePath, Edge endEdge ) {
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> closedStates = new HashSet<>();
        State finalState = null;

        while ( !pqueue.isEmpty() ) {
            State state = pqueue.extractMin();
            Distance distance = nodeDistanceMap.get( state );
            closedStates.add( state );
            Pair<State, Distance> updatePair = endCondition.update( finalState, upperBound, state, distance );
            upperBound = updatePair.b;
            finalState = updatePair.a;
            Iterator<Edge> edges = graph.getOutgoingEdges( state.getNode() );
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node targetNode = graph.getOtherNode( edge, state.getNode() );
                State targetState = new State( targetNode, edge );
                if ( !closedStates.contains( targetState ) ) {
                    Distance targetDistance = ( nodeDistanceMap.containsKey( targetState ) ) ? nodeDistanceMap.get( targetState ) : Distance.newInfinityInstance();
                    Distance alternativeDistance = distance.add( edge.getLength() ).add( state.isFirst() ? Distance.newInstance( 0 ) : graph.getTurnCost( state.getNode(), state.getEdge(), edge ) );
                    if ( alternativeDistance.isLowerThan( targetDistance ) ) {
                        putNodeDistance( nodeDistanceMap, pqueue, targetState, alternativeDistance );
                        predecessorMap.put( targetState, state );
                    }
                }
            }
        }
        if ( finalState != null ) {
            Route.RouteBuilder builder = Route.builder();
            State currentState = finalState;
            while ( currentState != null && !currentState.isFirst() ) {
                builder.addAsFirst( currentState.getEdge() );
                currentState = predecessorMap.get( currentState );
            }
            if ( endEdge != null ) {
                builder.addAsLast( endEdge );
            }
            return builder.build();
        } else if ( singleEdgePath != null ) {
            Route.RouteBuilder builder = Route.builder();
            builder.addAsFirst( singleEdgePath );
            return builder.build();
        } else {
            throw new IllegalStateException( "Path not found. Something is wrong." );
        }
    }

    private void putNodeDistance( Map<State, Distance> nodeDistanceMap, PriorityQueue<State> pqueue, State node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }

    private interface EndCondition {

        public Pair<State, Distance> update( State currentFinalState, Distance currentUpperBound, State currentState, Distance currentDistance );

    }

    private static class EdgeEndCondition implements EndCondition {

        private final Graph graph;
        private final Edge destination;
        private final Distance toDestinationStart;
        private final Distance toDestinationEnd;

        public EdgeEndCondition( Graph graph, Edge destination, Distance toDestinationStart, Distance toDestinationEnd ) {
            this.graph = graph;
            this.destination = destination;
            this.toDestinationStart = toDestinationStart;
            this.toDestinationEnd = toDestinationEnd;
        }

        @Override
        public Pair<State, Distance> update( State currentFinalState, Distance currentUpperBound, State currentState, Distance currentDistance ) {
            if ( currentState.getNode().equals( destination.getSource() ) ) {
                Distance completeDistance = currentDistance.add( toDestinationStart ).add( graph.getTurnCost( currentState.getNode(), currentState.getEdge(), destination ) );
                if ( completeDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentState, completeDistance );
                }
            }
            if ( !destination.isOneway() && currentState.getNode().equals( destination.getTarget() ) ) {
                Distance completeDistance = currentDistance.add( toDestinationEnd ).add( graph.getTurnCost( currentState.getNode(), currentState.getEdge(), destination ) );
                if ( completeDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentState, completeDistance );
                }
            }
            return new Pair<>( currentFinalState, currentUpperBound );
        }

    }

    private static class NodeEndCondition implements EndCondition {

        private final Node destination;

        public NodeEndCondition( Node destination ) {
            this.destination = destination;
        }

        @Override
        public Pair<State, Distance> update( State currentFinalState, Distance currentUpperBound, State currentState, Distance currentDistance ) {
            if ( currentState.getNode().equals( destination ) ) {
                if ( currentDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentState, currentDistance );
                }
            }
            return new Pair<>( currentFinalState, currentUpperBound );
        }

    }

}

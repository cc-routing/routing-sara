/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

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
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.utils.java8.Optional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class DijkstraAlgorithm<N extends Node<N,E>, E extends Edge<N,E>> implements RoutingAlgorithm<N, E> {

    @Override
    public Optional<Route<N,E>> route( Graph<N, E> graph, Metric metric, N source, N destination ) {
        Map<State<N, E>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<N, E>> pqueue = new FibonacciHeap<>();
        Distance upperBound = Distance.newInfinityInstance();
        putNodeDistance( nodeDistanceMap, pqueue, new State<N, E>( source, null ), Distance.newInstance( 0 ) );
        return route( graph, metric, nodeDistanceMap, pqueue, upperBound, new NodeEndCondition( destination ), null, null );
    }

    @Override
    public Optional<Route<N,E>> route( Graph<N, E> graph, Metric metric, E source, E destination ) {
        return route( graph, metric, source, destination, new Distance( 0 ), new Distance( 0 ), new Distance( 0 ), new Distance( 0 ) );
    }

    @Override
    public Optional<Route<N,E>> route( Graph<N, E> graph, Metric metric, E source, E destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd ) {
        Map<State<N, E>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<N, E>> pqueue = new FibonacciHeap<>();
        // create upper bound if the edges are equal and mark it
        Distance upperBound = Distance.newInfinityInstance();
        E singleEdgePath = null;
        if ( source.equals( destination ) ) {
            Distance substract = toSourceEnd.substract( toDestinationEnd );
            if ( source.isOneWay() ) {
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
        if ( !source.isOneWay() ) {
            putNodeDistance( nodeDistanceMap, pqueue, new State( source.getSource(), source ), toSourceStart );
        }
        return route( graph, metric, nodeDistanceMap, pqueue, upperBound, new EdgeEndCondition( graph, destination, toDestinationStart, toDestinationEnd ), singleEdgePath, destination );
    }

    private Optional<Route<N,E>> route( Graph<N, E> graph, Metric metric, Map<State<N, E>, Distance> nodeDistanceMap, PriorityQueue<State<N, E>> pqueue, Distance upperBound, EndCondition<N, E> endCondition, E singleEdgePath, E endEdge ) {
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> closedStates = new HashSet<>();
        State finalState = null;

        while ( !pqueue.isEmpty() ) {
            State<N, E> state = pqueue.extractMin();
            Distance distance = nodeDistanceMap.get( state );
            closedStates.add( state );
            Pair<State, Distance> updatePair = endCondition.update( finalState, upperBound, state, distance );
            upperBound = updatePair.b;
            finalState = updatePair.a;
            for ( E edge : graph.getOutgoingEdges( state.getNode() ) ) {
                N targetNode = graph.getOtherNode( edge, state.getNode() );
                State targetState = new State( targetNode, edge );
                if ( !closedStates.contains( targetState ) ) {
                    Distance targetDistance = ( nodeDistanceMap.containsKey( targetState ) ) ? nodeDistanceMap.get( targetState ) : Distance.newInfinityInstance();
                    Distance alternativeDistance = distance.add( graph.getLength( metric, edge ) ).add( state.isFirst() ? Distance.newInstance( 0 ) : graph.getTurnCost( state.getNode(), state.getEdge(), edge ) );
                    if ( alternativeDistance.isLowerThan( targetDistance ) ) {
                        putNodeDistance( nodeDistanceMap, pqueue, targetState, alternativeDistance );
                        predecessorMap.put( targetState, state );
                    }
                }
            }
        }
        if ( finalState != null ) {
            Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
            State<N, E> currentState = finalState;
            while ( currentState != null && !currentState.isFirst() ) {
                builder.addAsFirst( currentState.getEdge() );
                currentState = predecessorMap.get( currentState );
            }
            if ( endEdge != null ) {
                builder.addAsLast( endEdge );
            }
            return Optional.of( builder.build() );
        } else if ( singleEdgePath != null ) {
            Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
            builder.addAsFirst( singleEdgePath );
            return Optional.of( builder.build() );
        } else {
            return Optional.empty();
        }
    }

    private void putNodeDistance( Map<State<N, E>, Distance> nodeDistanceMap, PriorityQueue<State<N, E>> pqueue, State<N, E> node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }

    private interface EndCondition<N extends Node, E extends Edge> {

        public Pair<State<N, E>, Distance> update( State<N, E> currentFinalState, Distance currentUpperBound, State<N, E> currentState, Distance currentDistance );

    }

    private static class EdgeEndCondition<N extends Node, E extends Edge> implements EndCondition<N, E> {

        private final Graph<N, E> graph;
        private final E destination;
        private final Distance toDestinationStart;
        private final Distance toDestinationEnd;

        public EdgeEndCondition( Graph<N, E> graph, E destination, Distance toDestinationStart, Distance toDestinationEnd ) {
            this.graph = graph;
            this.destination = destination;
            this.toDestinationStart = toDestinationStart;
            this.toDestinationEnd = toDestinationEnd;
        }

        @Override
        public Pair<State<N, E>, Distance> update( State<N, E> currentFinalState, Distance currentUpperBound, State<N, E> currentState, Distance currentDistance ) {
            if ( currentState.getNode().equals( destination.getSource() ) ) {
                Distance completeDistance = currentDistance.add( toDestinationStart ).add( graph.getTurnCost( currentState.getNode(), currentState.getEdge(), destination ) );
                if ( completeDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentState, completeDistance );
                }
            }
            if ( !destination.isOneWay() && currentState.getNode().equals( destination.getTarget() ) ) {
                Distance completeDistance = currentDistance.add( toDestinationEnd ).add( graph.getTurnCost( currentState.getNode(), currentState.getEdge(), destination ) );
                if ( completeDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentState, completeDistance );
                }
            }
            return new Pair<>( currentFinalState, currentUpperBound );
        }

    }

    private static class NodeEndCondition<N extends Node, E extends Edge> implements EndCondition<N, E> {

        private final N destination;

        public NodeEndCondition( N destination ) {
            this.destination = destination;
        }

        @Override
        public Pair<State<N, E>, Distance> update( State<N, E> currentFinalState, Distance currentUpperBound, State<N, E> currentState, Distance currentDistance ) {
            if ( currentState.getNode().equals( destination ) ) {
                if ( currentDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentState, currentDistance );
                }
            }
            return new Pair<>( currentFinalState, currentUpperBound );
        }

    }

}

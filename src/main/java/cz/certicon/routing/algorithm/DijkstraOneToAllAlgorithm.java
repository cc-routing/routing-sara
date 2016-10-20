/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.State;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.values.Distance;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java8.util.Optional;

public class DijkstraOneToAllAlgorithm<N extends Node<N, E>, E extends Edge<N, E>> implements OneToAllRoutingAlgorithm<N, E> {

    @Override
    public Map<E, Optional<Route<N, E>>> route( Graph<N, E> graph, Metric metric, E sourceEdge, Direction sourceDirection, Map<E, Direction> targetEdges ) {
        Map<State<N, E>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<N, E>> pqueue = new FibonacciHeap<>();
        putNodeDistance( nodeDistanceMap, pqueue, new State( sourceDirection.equals( Direction.FORWARD ) ? sourceEdge.getTarget() : sourceEdge.getSource(), sourceEdge ), Distance.newInstance( 0 ) );
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> closedStates = new HashSet<>();
        Map<E, State> finalStates = new HashMap<>();
        while ( !pqueue.isEmpty() ) {
            State<N, E> state = pqueue.extractMin();
            Distance distance = nodeDistanceMap.get( state );
            closedStates.add( state );
            if ( isFinal( targetEdges, finalStates, state ) ) {
                finalStates.put( state.getEdge(), state );
            }
            if ( finalStates.size() == targetEdges.size() ) {
                pqueue.clear();
                break;
            }
            for ( E edge : graph.getOutgoingEdges( state.getNode() ) ) {
                N targetNode = graph.getOtherNode( edge, state.getNode() );
                State targetState = new State( targetNode, edge );
                if ( !closedStates.contains( targetState ) ) {
                    Distance targetDistance = ( nodeDistanceMap.containsKey( targetState ) ) ? nodeDistanceMap.get( targetState ) : Distance.newInfinityInstance();
                    Distance alternativeDistance = distance
                            .add( graph.getLength( metric, edge ) )
                            .add( state.isFirst() ? Distance.newInstance( 0 ) : graph.getTurnCost( state.getNode(), state.getEdge(), edge ) );
                    if ( alternativeDistance.isLowerThan( targetDistance ) ) {
                        putNodeDistance( nodeDistanceMap, pqueue, targetState, alternativeDistance );
                        predecessorMap.put( targetState, state );
                    }
                }
            }
        }
        Map<E, Optional<Route<N, E>>> resultMap = new HashMap<>();
        for ( Map.Entry<E, Direction> entry : targetEdges.entrySet() ) {
            Optional<Route<N, E>> optional = Optional.empty();
            if ( finalStates.containsKey( entry.getKey() ) ) {
                State<N, E> currentState = finalStates.get( entry.getKey() );
                Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
                while ( currentState != null && !currentState.isFirst() ) {
                    builder.addAsFirst( currentState.getEdge() );
                    currentState = predecessorMap.get( currentState );
                }
                optional = Optional.of( builder.build() );
            }
            resultMap.put( entry.getKey(), optional );
        }
        return resultMap;
    }

    private boolean isFinal( Map<E, Direction> targetEdges, Map<E, State> finalStates, State<N, E> currentState ) {
        E edge = currentState.getEdge();
        // is it a target edge? Then return
        if ( !targetEdges.containsKey( edge ) ) {
            return false;
        }
        // is the edge already found? Then return
        if ( finalStates.containsKey( edge ) ) {
            return false;
        }
        Direction direction = targetEdges.get( edge );
        N node = currentState.getNode();
        // is the node target of the edge and the wanted direction is forward? Then its ok
        if ( edge.isTarget( node ) && direction.equals( Direction.FORWARD ) ) {
            return true;
        }
        // is the node source of the edge and the wanted direction is backward? Then its ok, otherwise not
        return edge.isSource( node ) && direction.equals( Direction.BACKWARD );
    }

    private void putNodeDistance( Map<State<N, E>, Distance> nodeDistanceMap, PriorityQueue<State<N, E>> pqueue, State<N, E> node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }
}

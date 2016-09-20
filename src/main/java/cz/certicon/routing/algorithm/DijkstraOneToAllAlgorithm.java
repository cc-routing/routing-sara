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

public class DijkstraOneToAllAlgorithm<N extends Node, E extends Edge> implements OneToAllRoutingAlgorithm<N, E> {

    @Override
    public Map<E, Route<N, E>> route( Graph<N, E> graph, Metric metric, E sourceEdge, Direction sourceDirection, Map<E, Direction> targetEdges ) {
        Map<State<N, E>, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<State<N, E>> pqueue = new FibonacciHeap<>();
        pqueue.add( new State( sourceDirection.equals( Direction.FORWARD ) ? sourceEdge.getTarget() : sourceEdge.getSource(), sourceEdge ), 0 );
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> closedStates = new HashSet<>();
        Map<E, State> finalStates = new HashMap<>();
        while ( !pqueue.isEmpty() ) {
            State<N, E> state = pqueue.extractMin();
            Distance distance = nodeDistanceMap.get( state );
            closedStates.add( state );
            for ( E edge : graph.getOutgoingEdges( state.getNode() ) ) {
                if ( targetEdges.containsKey( edge ) && !finalStates.containsKey( edge )
                        && ( ( state.getNode().equals( edge.getSource() ) && targetEdges.get( edge ).equals( Direction.FORWARD ) )
                        || ( state.getNode().equals( edge.getTarget() ) && targetEdges.get( edge ).equals( Direction.BACKWARD ) ) ) ) {
                    finalStates.put( edge, state );
                }
                if ( finalStates.size() == targetEdges.size() ) {
                    pqueue.clear();
                    break;
                }
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
        Map<E, Route<N, E>> resultMap = new HashMap<>();
        for ( Map.Entry<E, State> entry : finalStates.entrySet() ) {
            Route.RouteBuilder<N, E> builder = Route.<N, E>builder();
            State<N, E> currentState = entry.getValue();
            while ( currentState != null && !currentState.isFirst() ) {
                builder.addAsFirst( currentState.getEdge() );
                currentState = predecessorMap.get( currentState );
            }
            builder.addAsLast( entry.getKey() );
            resultMap.put( entry.getKey(), builder.build() );
        }
        return resultMap;
    }

    private void putNodeDistance( Map<State<N, E>, Distance> nodeDistanceMap, PriorityQueue<State<N, E>> pqueue, State<N, E> node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }
}

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
        Map<Node, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<Node> pqueue = new FibonacciHeap<>();
        Distance upperBound = Distance.newInfinityInstance();
        putNodeDistance( nodeDistanceMap, pqueue, source, Distance.newInstance( 0 ) );
        return route( graph, nodeDistanceMap, pqueue, upperBound, new NodeEndCondition( destination ), null );
    }

    @Override
    public Route route( Graph graph, Edge source, Edge destination ) {
        return route( graph, source, destination, new Distance( 0 ), new Distance( 0 ), new Distance( 0 ), new Distance( 0 ) );
    }

    @Override
    public Route route( Graph graph, Edge source, Edge destination, Distance toSourceStart, Distance toSourceEnd, Distance toDestinationStart, Distance toDestinationEnd ) {
        Map<Node, Distance> nodeDistanceMap = new HashMap<>();
        PriorityQueue<Node> pqueue = new FibonacciHeap<>();
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
        putNodeDistance( nodeDistanceMap, pqueue, source.getTarget(), toSourceEnd );
        if ( !source.isOneway() ) {
            putNodeDistance( nodeDistanceMap, pqueue, source.getSource(), toSourceStart );
        }
        return route( graph, nodeDistanceMap, pqueue, upperBound, new EdgeEndCondition( destination, toDestinationStart, toDestinationEnd ), singleEdgePath );
    }

    private Route route( Graph graph, Map<Node, Distance> nodeDistanceMap, PriorityQueue<Node> pqueue, Distance upperBound, EndCondition endCondition, Edge singleEdgePath ) {
        Map<Node, Edge> predecessorMap = new HashMap<>();
        Set<Node> closedNodes = new HashSet<>();
        Node finalNode = null;

        while ( !pqueue.isEmpty() ) {
            Node node = pqueue.extractMin();
            Distance distance = nodeDistanceMap.get( node );
            closedNodes.add( node );
            Pair<Node, Distance> updatePair = endCondition.update( finalNode, upperBound, node, distance );
            upperBound = updatePair.b;
            finalNode = updatePair.a;
            Iterator<Edge> edges = graph.getOutgoingEdges( node );
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node targetNode = graph.getOtherNode( edge, node );
                if ( !closedNodes.contains( targetNode ) ) {
                    Distance targetDistance = ( nodeDistanceMap.containsKey( targetNode ) ) ? nodeDistanceMap.get( targetNode ) : Distance.newInfinityInstance();
                    Distance alternativeDistance = distance.add( edge.getLength() );
                    if ( alternativeDistance.isLowerThan( targetDistance ) ) {
                        putNodeDistance( nodeDistanceMap, pqueue, targetNode, alternativeDistance );
                        predecessorMap.put( targetNode, edge );
                    }
                }
            }
        }
        if ( finalNode != null ) {
            Route.RouteBuilder builder = Route.builder();
            Edge pred = predecessorMap.get( finalNode );
            Node currentNode = finalNode;
            while ( pred != null ) {
                builder.addAsFirst( pred );
                currentNode = graph.getOtherNode( pred, currentNode );
                pred = predecessorMap.get( currentNode );
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

    private void putNodeDistance( Map<Node, Distance> nodeDistanceMap, PriorityQueue<Node> pqueue, Node node, Distance distance ) {
        pqueue.decreaseKey( node, distance.getValue() );
        nodeDistanceMap.put( node, distance );
    }

    private interface EndCondition {

        public Pair<Node, Distance> update( Node currentFinalNode, Distance currentUpperBound, Node currentNode, Distance currentDistance );

    }

    private static class EdgeEndCondition implements EndCondition {

        private final Edge destination;
        private final Distance toDestinationStart;
        private final Distance toDestinationEnd;

        public EdgeEndCondition( Edge destination, Distance toDestinationStart, Distance toDestinationEnd ) {
            this.destination = destination;
            this.toDestinationStart = toDestinationStart;
            this.toDestinationEnd = toDestinationEnd;
        }

        @Override
        public Pair<Node, Distance> update( Node currentFinalNode, Distance currentUpperBound, Node currentNode, Distance currentDistance ) {
            if ( currentNode.equals( destination.getSource() ) ) {
                Distance completeDistance = currentDistance.add( toDestinationStart );
                if ( completeDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentNode, completeDistance );
                }
            }
            if ( !destination.isOneway() && currentNode.equals( destination.getTarget() ) ) {
                Distance completeDistance = currentDistance.add( toDestinationEnd );
                if ( completeDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentNode, completeDistance );
                }
            }
            return new Pair<>( currentFinalNode, currentUpperBound );
        }

    }

    private static class NodeEndCondition implements EndCondition {

        private final Node destination;

        public NodeEndCondition( Node destination ) {
            this.destination = destination;
        }

        @Override
        public Pair<Node, Distance> update( Node currentFinalNode, Distance currentUpperBound, Node currentNode, Distance currentDistance ) {
            if ( currentNode.equals( destination ) ) {
                if ( currentDistance.isLowerThan( currentUpperBound ) ) {
                    return new Pair<>( currentNode, currentDistance );
                }
            }
            return new Pair<>( currentFinalNode, currentUpperBound );
        }

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Distance;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.queue.TIntQueue;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class FordFulkersonMinimalCut implements MinimalCutAlgorithm {

    private static final char OPEN = 1;
    private static final char CLOSED = 2;
    private static final char FRESH = 0;

    @Override
    public MinimalCut compute( Graph graph, Node sourceNode, Node targetNode ) {
        // TODO optimize, change to adjacency lists (currently adjacency table - n^2, wasteful for thin graphs
        // map graph to arrays
        System.out.println( "MINIMAL CUT: mapping graph to arrays" );
        System.out.println( "source = " + sourceNode );
        System.out.println( "target = " + targetNode );
        int nodeCount = graph.getNodesCount();
        int[] predecessors = new int[nodeCount];
        int[] pathFlows = new int[nodeCount];
        int[][] limits = new int[nodeCount][nodeCount];
        int[][] flows = new int[nodeCount][nodeCount];
        TObjectIntMap nodeToIndexMap = new TObjectIntHashMap();
        Node[] indexToNodeArray = new Node[nodeCount];
        Iterator<Node> nodeIterator = graph.getNodes();
        int nodeCounter = 0;
        while ( nodeIterator.hasNext() ) {
            Node n = nodeIterator.next();
            nodeToIndexMap.put( n, nodeCounter );
            indexToNodeArray[nodeCounter] = n;
            nodeCounter++;
        }
        Iterator<Edge> edgeIterator = graph.getEdges();
        while ( edgeIterator.hasNext() ) {
            Edge e = edgeIterator.next();
            int srcIdx = nodeToIndexMap.get( e.getSource() );
            int tgtIdx = nodeToIndexMap.get( e.getTarget() );
            int value = (int) ( Math.round( e.getLength().getValue() ) + 10E-8 );
            limits[srcIdx][tgtIdx] = value;
            if ( !e.isOneway() ) {
                limits[tgtIdx][srcIdx] = value;
            }
        }
//        testPrintTable( limits );

        int maxFlow = 0;
        int source = nodeToIndexMap.get( sourceNode );
        int target = nodeToIndexMap.get( targetNode );
        System.out.println( "While improvement path exists (" + source + " - > " + target + ")" );
        while ( findImprovementPath( predecessors, pathFlows, limits, flows, source, target ) ) {
            maxFlow += pathFlows[target];
            System.out.println( "Increase flow" );
            increaseFlow( predecessors, pathFlows, limits, flows, source, target );
        }
//        testPrintTable( flows );
        System.out.println( "Searching for reachable nodes" );
        // search for visited - find reachable nodes from source (consider only edges with different values from the original graph)
        boolean[] visited = new boolean[nodeCount];
        Stack<Integer> stack = new Stack<>();
        stack.push( source );
        while ( !stack.isEmpty() ) {
            int node = stack.pop();
            visited[node] = true;
            for ( int i = 0; i < nodeCount; i++ ) {
                if ( flows[node][i] != limits[node][i] && !visited[i] ) {
                    stack.push( i );
                }
            }
        }
//        System.out.println( "VISITED" );
//        testPrintArray( visited );
        // for each i,j pair, determine whether it belongs to cut edges (leads from reachagle to unreachable node in the original graph) and find the corresponding edge
        Set<Edge> cutEdge = new HashSet<>();
        for ( int i = 0; i < nodeCount; i++ ) {
            for ( int j = 0; j < nodeCount; j++ ) {
                if ( visited[i] && !visited[j] && limits[i][j] != 0 ) {
                    Node from = indexToNodeArray[i];
                    Node to = indexToNodeArray[j];
                    edgeIterator = from.getEdges();
                    while ( edgeIterator.hasNext() ) {
                        Edge e = edgeIterator.next();
                        Node edgeTarget = graph.getOtherNode( e, from );
                        if ( edgeTarget.equals( to ) ) {
                            cutEdge.add( e );
                        }
                    }
                }
            }
        }
        return new MinimalCut( cutEdge, maxFlow );
    }

    private boolean findImprovementPath( int[] predecessors, int[] pathFlows, int[][] limits, int[][] flows, int source, int target ) {
        if(source == target){
            return false;
        }
        int nodeCount = predecessors.length;
        int node = source;
        Queue<Integer> queue = new LinkedList<>();
        char[] states = new char[nodeCount];
        pathFlows[source] = Integer.MAX_VALUE;
        predecessors[source] = source;
        queue.add( node );
        // while queue is not empty and the target has not been reached
//        System.out.println( "cycle: " + !queue.isEmpty() + " " + ( node != target ) );
        System.out.println( "path: " );
        while ( !queue.isEmpty() && node != target ) {
            // dequeue node from queue and close it
            node = queue.poll();
            System.out.println( node + "," );
            states[node] = CLOSED;
            for ( int i = 0; i < nodeCount; i++ ) {
                // for all outgoing fresh nodes, which have yet to fill the flow limit
//                System.out.println( "#" + i + ": " + ( limits[node][i] != 0 ) + " " + ( states[i] == FRESH ) + " " + ( flows[node][i] < limits[node][i] ) );
                if ( limits[node][i] != 0 && states[i] == FRESH && flows[node][i] < limits[node][i] ) {
//                    System.out.println( "OPENING: " + i );
                    // open them, set predecessor to this
                    states[i] = OPEN;
                    predecessors[i] = node;
                    // set delta to minimum of this delta and the remaining flow capacity to this node
                    pathFlows[i] = ( pathFlows[node] < limits[node][i] - flows[node][i] ) ? pathFlows[node] : limits[node][i] - flows[node][i];
                    queue.add( i );
                }
                // for all incoming fresh nodes, whhich have already been somehow filled
                if ( limits[i][node] != 0 && states[i] == FRESH && 0 < flows[i][node] ) {
//                    System.out.println( "OPENING: " + i );
                    // open them, set predecessor to negative of this (opposite direction)
                    states[i] = OPEN;
                    predecessors[i] = -node;
                    // set delta to minimum of this delta and flow from it to this node
                    pathFlows[i] = ( pathFlows[node] < flows[i][node] ) ? pathFlows[node] : flows[i][node];
                    queue.add( i );
                }
            }
        }
//        System.out.println( "PATH FLOWS" );
//        testPrintArray( pathFlows );
        // return true if the target has been reached
        return node == target;
    }

    private void increaseFlow( int[] predecessors, int[] pathFlows, int[][] limits, int[][] flows, int source, int target ) {
        // increase flow along the improvement path
        // travel from target
        int predecessor = target;
        int node = target;
        int pathFlow = pathFlows[target];
        // while source is not reached
        while ( node != source ) {
            // set node to previous predecessor
            node = predecessor;
            // set predecessor to node's predecessor (without the sign)
            predecessor = Math.abs( predecessors[node] );
            // check the sign, if flow went forward
            if ( predecessors[node] > 0 ) {
                // add target's delta to flow in the forward direction
                flows[predecessor][node] += pathFlow;
            } else {
                // if backward, substract target's delta from flow in backward direction
                flows[node][predecessor] -= pathFlow;
            }
        }
    }

    private void testPrintTable( int[][] table ) {
        System.out.println( "TABLE" );
        for ( int i = 0; i < table.length; i++ ) {
            int[] is = table[i];
            for ( int j = 0; j < is.length; j++ ) {
                int k = is[j];
                System.out.print( k + " " );
            }
            System.out.println( "" );
        }
    }

    private void testPrintArray( int[] array ) {
        System.out.println( "ARRAY" );
        for ( int i = 0; i < array.length; i++ ) {
            int j = array[i];
            System.out.print( j + " " );
        }
        System.out.println( "" );
    }

    private void testPrintArray( boolean[] array ) {
        System.out.println( "ARRAY" );
        for ( int i = 0; i < array.length; i++ ) {
            System.out.print( ( array[i] ? 1 : 0 ) + " " );
        }
        System.out.println( "" );
    }
}

/*
        Node node = source;
        Queue<Node> queue = new LinkedList<>();
        Set<Node> openNodes = new HashSet<>();
        Set<Node> closedNodes = new HashSet<>();
        Map<Node, Node> predecessorMap = new HashMap<>();
        Map<Node, Node> negativePredecessorMap = new HashMap<>();
        Map<Node, Distance> deltaMap = new HashMap<>();
        Map<Edge, Distance> flowMap = new HashMap<>();
        Map<Edge, Distance> limitMap = new HashMap<>();
        Distance zeroDistance = Distance.newInstance( 0 );
        queue.add( source );
        while ( !queue.isEmpty() && !node.equals( target ) ) {
            node = queue.poll();
            closedNodes.add( node );
            Distance delta = deltaMap.get( node );
            Iterator<Edge> edges = node.getEdges();
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node neighbor = graph.getOtherNode( edge, node );
                Distance flow = flowMap.get( edge );
                Distance limit = limitMap.get( edge );
                if ( !openNodes.contains( neighbor ) && !closedNodes.contains( neighbor ) && flow.isLowerThan( limit ) ) {
                    openNodes.add( neighbor );
                    queue.add( neighbor );
                    predecessorMap.put( neighbor, node );
                    Distance remain = limit.substract( flow );
                    deltaMap.put( neighbor, delta.isLowerThan( remain ) ? delta : remain );
                }
            }
            edges = node.getEdges();
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node neighbor = graph.getOtherNode( edge, node );
                Distance flow = flowMap.get( edge );
                Distance limit = limitMap.get( edge );
                if ( !openNodes.contains( neighbor ) && !closedNodes.contains( neighbor ) && flow.isGreaterThan( zeroDistance ) ) {
                    openNodes.add( neighbor );
                    queue.add( neighbor );
                    negativePredecessorMap.put( neighbor, node );
                    deltaMap.put( neighbor, delta.isLowerThan( flow ) ? delta : flow );
                }
            }
        }*/

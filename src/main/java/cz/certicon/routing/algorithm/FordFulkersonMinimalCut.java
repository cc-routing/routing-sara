/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
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
    public <N extends Node, E extends Edge> MinimalCut compute( Graph<N, E> graph, Metric metric, N sourceNode, N targetNode ) {
        // TODO optimize, change to adjacency lists (currently adjacency table - n^2, wasteful for thin graphs
        // map graph to arrays
//        System.out.println( "MINIMAL CUT: mapping graph to arrays: " + graph );
//        System.out.println( "source = " + sourceNode.getId() );
//        System.out.println( "target = " + targetNode.getId() );
        int nodeCount = graph.getNodesCount();
        int[] predecessors = new int[nodeCount];
        int[] pathFlows = new int[nodeCount];
        int[][] limits = new int[nodeCount][nodeCount];
        int[][] flows = new int[nodeCount][nodeCount];
        TObjectIntMap nodeToIndexMap = new TObjectIntHashMap();
        Node[] indexToNodeArray = new Node[nodeCount];
        int nodeCounter = 0;
        for ( Node n : graph.getNodes() ) {
            nodeToIndexMap.put( n, nodeCounter );
            indexToNodeArray[nodeCounter] = n;
            nodeCounter++;
        }
        Distance maxDistance = Distance.newInstance( Integer.MAX_VALUE );
        for ( E e : graph.getEdges() ) {
            int srcIdx = nodeToIndexMap.get( e.getSource() );
            int tgtIdx = nodeToIndexMap.get( e.getTarget() );
            int value = graph.getLength( metric, e ).isGreaterOrEqualTo( maxDistance ) ? Integer.MAX_VALUE : (int) Math.round( graph.getLength( metric, e ).getValue() + 10E-8 );
//            System.out.println( "edge#" + e + ": value = " + value );
            limits[srcIdx][tgtIdx] = value;
            if ( !e.isOneWay() ) {
                limits[tgtIdx][srcIdx] = value;
            }
        }
//        testPrintTable( limits );

        int maxFlow = 0;
        int source = nodeToIndexMap.get( sourceNode );
        int target = nodeToIndexMap.get( targetNode );
//        System.out.println( "While improvement path exists (" + source + " - > " + target + ")" );
        while ( findImprovementPath( predecessors, pathFlows, limits, flows, source, target ) ) {
            maxFlow += pathFlows[target];
//            System.out.println( "Increase flow: " + maxFlow );
            increaseFlow( predecessors, pathFlows, limits, flows, source, target );
        }
//        testPrintTable( flows );
//        System.out.println( "Searching for reachable nodes" );
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
                    N from = (N) indexToNodeArray[i];
                    N to = (N) indexToNodeArray[j];
                    for ( E e : graph.getEdges( from ) ) {
//                        System.out.println( "edge: " + e );
                        Node edgeTarget = graph.getOtherNode( e, from );
                        if ( edgeTarget.equals( to ) ) {
//                            System.out.println( "adding: " + e );
                            cutEdge.add( e );
                        }
                    }
                }
            }
        }
        MinimalCut<Edge> result = new MinimalCut( cutEdge, maxFlow );
//        System.out.println( "RESULT" );
//        for ( Edge cutEdge1 : result.getCutEdges()) {
//            System.out.println( cutEdge1 );
//        }
        return result;
    }

    private boolean findImprovementPath( int[] predecessors, int[] pathFlows, int[][] limits, int[][] flows, int source, int target ) {
        if ( source == target ) {
            return false;
        }
        int nodeCount = predecessors.length;
        int node = source;
        Queue<Integer> queue = new LinkedList<>();
        char[] states = new char[nodeCount];
        pathFlows[source] = Integer.MAX_VALUE;
        predecessors[source] = source + 1;
        queue.add( node );
        // while queue is not empty and the target has not been reached
//        System.out.println( "cycle: " + !queue.isEmpty() + " " + ( node != target ) );
//        System.out.print( "path: " );
        while ( !queue.isEmpty() && node != target ) {
            // dequeue node from queue and close it
            node = queue.poll();
//            System.out.println( "polled: #" + node );
            states[node] = CLOSED;
            for ( int i = 0; i < nodeCount; i++ ) {
                // for all outgoing fresh nodes, which have yet to fill the flow limit
//                System.out.println( "#" + i + ": " + ( limits[node][i] != 0 ) + " " + ( states[i] == FRESH ) + " " + ( flows[node][i] < limits[node][i] ) );
                if ( limits[node][i] != 0 && states[i] == FRESH && flows[node][i] < limits[node][i] ) {
//                    System.out.println( "OPENING+: " + i );
                    // open them, set predecessor to this
                    states[i] = OPEN;
                    predecessors[i] = node + 1;
                    // set delta to minimum of this delta and the remaining flow capacity to this node
                    pathFlows[i] = ( pathFlows[node] < limits[node][i] - flows[node][i] ) ? pathFlows[node] : limits[node][i] - flows[node][i];
//                    System.out.println( "pathFlows[" + i + "] = ( " + pathFlows[node] + " < " + limits[node][i] + " - " + flows[node][i] + " ) ? " + pathFlows[node] + " : " + ( limits[node][i] - flows[node][i] ) + ";" );
                    queue.add( i );
                }
                // for all incoming fresh nodes, which have already been somehow filled
//                System.out.println( "#" + i + ": " + ( limits[i][node] != 0 ) + " " + ( states[i] == FRESH ) + " " + ( 0 < flows[i][node] ) );
                if ( limits[i][node] != 0 && states[i] == FRESH && 0 < flows[i][node] ) {
//                    System.out.println( "OPENING-: " + i );
                    // open them, set predecessor to negative of this (opposite direction)
                    states[i] = OPEN;
                    predecessors[i] = -node - 1;
                    // set delta to minimum of this delta and flow from it to this node
                    pathFlows[i] = ( pathFlows[node] < flows[i][node] ) ? pathFlows[node] : flows[i][node];
//                    System.out.println( "pathFlows[" + i + "] = ( " + pathFlows[node] + " < " + flows[i][node] + " ) ? " + pathFlows[node] + " : " + ( flows[i][node] ) + ";" );
                    queue.add( i );
                }
            }
        }
//        System.out.println( "" );
//        System.out.println( "PATH FLOWS" );
//        testPrintArray( pathFlows );
        // return true if the target has been reached
        return node == target;
    }

    private void increaseFlow( int[] predecessors, int[] pathFlows, int[][] limits, int[][] flows, int source, int target ) {
//        System.out.println( "INCREASE FLOW" );
        // increase flow along the improvement path
        // travel from target
        int predecessor = target;
        int node = target;
        int pathFlow = pathFlows[target];
        // while source is not reached
        while ( node != source ) {
            // set node to previous predecessor
            node = predecessor;
//            System.out.println( "increasing node: #" + node );
            // set predecessor to node's predecessor (without the sign)
            predecessor = Math.abs( predecessors[node] ) - 1;
            // check the sign, if flow went forward
//            System.out.println( "if predecessors[" + node + "] > 0" );
            if ( predecessors[node] > 0 ) {
                // add target's delta to flow in the forward direction
//                System.out.println( "flows[" + predecessor + "][" + node + "] += " + pathFlow );
                flows[predecessor][node] += pathFlow;
            } else {
                // if backward, substract target's delta from flow in backward direction
//                System.out.println( "flows[" + node + "][" + predecessor + "] -= " + pathFlow );
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

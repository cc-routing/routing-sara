/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.assembly;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.preprocessing.NodePair;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.RandomUtils;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GreedyAssembler implements Assembler {

    private final double lowIntervalProbability;
    private final double lowerIntervalLimit;
    private final int maxCellSize;
    private final Random rand;

    /**
     * Constructor
     *
     * @param lowIntervalProbability a
     * @param lowerIntervalLimit b
     * @param maxCellSize r
     */
    public GreedyAssembler( double lowIntervalProbability, double lowerIntervalLimit, int maxCellSize ) {
        this.lowIntervalProbability = lowIntervalProbability;
        this.lowerIntervalLimit = lowerIntervalLimit;
        this.maxCellSize = maxCellSize;
        this.rand = RandomUtils.createRandom();
    }

    @Override
    public <N extends Node, E extends Edge> SaraGraph assemble( Graph<N, E> originalGraph, ContractGraph filteredGraph ) {
        ContractGraph graph = (ContractGraph) filteredGraph.copy();
//        System.out.println( "Assembling..." );
        // find max ids
        long maxNodeId = -1;
        long maxEdgeId = -1;
        Set<ContractNode> nodes = new HashSet<>();
        for ( ContractNode node : graph.getNodes() ) {
            maxNodeId = Math.max( maxNodeId, node.getId() );
            nodes.add( node );
        }
        for ( ContractEdge edge : graph.getEdges() ) {
            maxEdgeId = Math.max( maxEdgeId, edge.getId() );
        }
        // P = {{x,y}; x,y  V, {x,y} e E, s(x)+s(y) < U} // all pairs of adjacent vertices with combined size lower than U  
        // Sort P according to (minimizing): score({x,y}) = r * (w(x,y)/sqrt(s(x))+w(x,y)/sqrt(s(y))), where r is with probability a picked randomly from [0,b] and with probability 1-a picked randomly from [b,1]
        PriorityQueue<NodePair> queue = initQueue( graph );
        ContractNode.MaxIdContainer maxNodeIdContainer = new ContractNode.MaxIdContainer( maxNodeId );
        ContractNode.MaxIdContainer maxEdgeIdContainer = new ContractNode.MaxIdContainer( maxEdgeId );
//        System.out.println( "MAX CELL SIZE = " + maxCellSize );
        // While P is not empty
        while ( !queue.isEmpty() ) {
            // pair = P.pop 
            NodePair pair = queue.extractMin();
//            System.out.println( "popping: " + pair.nodeA.getId() + ", " + pair.nodeB.getId() );
//            System.out.println( "popping detailed: " + pair );
            // Contract pair
            ContractNode source = pair.nodeA;
            nodes.remove( source );
            ContractNode target = pair.nodeB;
            nodes.remove( target );

            // clear pairs before contracting - before losing information about connections
            // - clear old pairs with source
            clearPairs( queue, pair, source );
            // - clear old pairs with target
            clearPairs( queue, pair, target );
            // now contract pair
            ContractNode contractedNode = source.mergeWith( target, maxNodeIdContainer, maxEdgeIdContainer );
            nodes.add( contractedNode );

            // Update score value (with the new r for each iteration) for the adjacent edges (pairs) of the contracted pair (edge) and if the new size s is higher or equal to U, remove pair from P
            // - add new pairs with the contracted node
            addPairs( queue, contractedNode );
        }
        //End While
        //Return partitions, i.e. in which partition each vertex belongs
        // - add all edges to builder
//        UndirectedGraph.UndirectedGraphBuilder builder = UndirectedGraph.builder();
//        Set<ContractEdge> edges = new HashSet<>();
//        for ( ContractNode node : nodes ) {
//            edgeIterator = node.getEdges();
//            while ( edgeIterator.hasNext() ) {
//                edges.add( (ContractEdge) edgeIterator.next() );
//            }
//        }
//        builder.nodes( nodes ).edges( edges );

        SaraGraph saraGraph = new SaraGraph( EnumSet.of( Metric.SIZE, Metric.LENGTH, Metric.TIME ) );
        long cellId = 0;
        for ( ContractNode node : nodes ) {
            Cell cell = new Cell( cellId++ );
            for ( Node origNode : node.getNodes() ) {
                SaraNode saraNode = saraGraph.createNode( origNode.getId(), cell );
                saraNode.setCoordinate( origNode.getCoordinate() );
            }
        }
        for ( E edge : originalGraph.getEdges() ) {
            SaraEdge saraEdge = saraGraph.createEdge( edge.getId(), edge.isOneWay(),
                    saraGraph.getNodeById( edge.getSource().getId() ), saraGraph.getNodeById( edge.getTarget().getId() ),
                    edge.getSourcePosition(), edge.getTargetPosition(),
                    new Pair<>( Metric.LENGTH, edge.getLength( Metric.LENGTH ) ), new Pair<>( Metric.TIME, edge.getLength( Metric.TIME ) ) );
        }
        return saraGraph;
    }

    PriorityQueue<NodePair> initQueue( ContractGraph graph ) {
//        System.out.println( "INIT QUEUE" );
        double ratio = generateR();
        PriorityQueue<NodePair> queue = new FibonacciHeap<>();
        for ( ContractNode node : graph.getNodes() ) {
            for ( ContractEdge edge : node.getEdges() ) {
                ContractNode target = edge.getOtherNode( node );
                NodePair nodePair = new NodePair( node, target, edge );
                if ( !queue.contains( nodePair ) && ( node.getNodes().size() + target.getNodes().size() < maxCellSize ) ) {
//                    System.out.println( "ADDING: " + nodePair );
                    queue.add( nodePair, score( nodePair, ratio ) );
//                    System.out.println( "adding with score[" + score( nodePair, ratio ) + "]: " + nodePair );
                }
            }
        }
        return queue;
    }

    PriorityQueue<NodePair> clearPairs( PriorityQueue<NodePair> queue, NodePair origPair, ContractNode origNode ) {
//        System.out.println( "REMOVING FOR: " + origNode );
        for ( ContractEdge edge : origNode.getEdges() ) {
            ContractNode neighbor = edge.getOtherNode( origNode );
            NodePair nodePair = new NodePair( origNode, neighbor, edge );
            if ( queue.contains( nodePair ) ) {
                queue.remove( nodePair ); // the pair does not have to be contained - it might have higher size than limit (see addPairs condition)
//                System.out.println( "REMOVING: " + nodePair );
            } else {
//                System.out.println( "DOES NOT REMOVE: " + nodePair );
            }
//            if ( !nodePair.equals( origPair ) ) { // WRONG ASSUMPTION, see 
//                if ( !queue.contains( nodePair ) ) {
//                    throw new IllegalStateException( "Queue does not contain pair: " + nodePair );
//                }
//            }
        }
        return queue;
    }

    PriorityQueue<NodePair> addPairs( PriorityQueue<NodePair> queue, ContractNode contractedNode ) {
//        System.out.println( "ADDING FOR: " + contractedNode );
        double ratio = generateR();
        for ( ContractEdge edge : contractedNode.getEdges() ) {
            ContractNode neighbor = edge.getOtherNode( contractedNode );
            NodePair nodePair = new NodePair( contractedNode, neighbor, edge );
            if ( nodePair.nodeA.getNodes().size() + nodePair.nodeB.getNodes().size() < maxCellSize ) {
//                System.out.println( "nodeA[" + nodePair.nodeA.getId() + "->" + nodePair.nodeA.getNodes().size() + "] + nodeB[" + nodePair.nodeB.getId() + "->" + nodePair.nodeB.getNodes().size() + "] < " + maxCellSize );
                double newScore = score( nodePair, ratio );
//                System.out.println( "adding with score[" + newScore + "]: " + nodePair );
                queue.add( nodePair, newScore );
            }
        }
        return queue;
    }

    private double generateR() {
        double lowerBound;
        double upperBound;
        if ( rand.nextDouble() < lowIntervalProbability ) {
            lowerBound = 0;
            upperBound = lowerIntervalLimit;
        } else {
            lowerBound = lowerIntervalLimit;
            upperBound = 1;
        }
        double interval = upperBound - lowerBound;
        return rand.nextDouble() * interval + lowerBound;
    }

    private double score( NodePair nodePair, double ratio ) {
        // TODO return Double.MAX_VALUE - result in order to extract maximal values?
        ContractEdge edge = nodePair.connectingEdge;
//        System.out.println( "score@edge = " + edge );

        double edgeWeight = edge.getLength( Metric.SIZE ).getValue();
        double sourceWeight = nodePair.getSizeA();
        double targetWeight = nodePair.getSizeB();
        return Double.MAX_VALUE - ratio * ( ( edgeWeight / Math.sqrt( sourceWeight ) ) + ( edgeWeight / Math.sqrt( targetWeight ) ) );
    }

}

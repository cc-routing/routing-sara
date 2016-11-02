/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.assembly;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.preprocessing.NodePair;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.basic.MaxIdContainer;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.utils.RandomUtils;
import cz.certicon.routing.utils.measuring.TimeLogger;

import java.util.Random;

/**
 * Greedy algorithm-based assembler. Pseudo-code:
 * P = {{x,y}; x,y  V, {x,y}E, s(x)+s(y) < U} // all pairs of adjacent vertices with combined size lower than U
 * Sort P according to (minimizing): score({x,y}) = r * (w(x,y)s(x)+w(x,y)s(y)), where r is with probability a picked randomly from [0,b] and with probability 1-a picked randomly from [b,1]
 * While P is not empty
 * pair = P.pop
 * Contract pair
 * Update score value (with the new r for each iteration) for the adjacent edges (pairs) of the contracted pair (edge) and if the new size s is higher or equal to U, remove pair from P
 * End While
 * Return partition, i.e. in which cell each vertex belongs
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GreedyAssembler implements Assembler {

    private final double lowIntervalProbability;
    private final double lowerIntervalLimit;
    private long maxCellSize;
    private final Random rand;

    /**
     * Constructor for greedy algorithm-based assembler.
     *
     * @param lowIntervalProbability a
     * @param lowerIntervalLimit     b
     * @param maxCellSize            r
     */
    public GreedyAssembler( double lowIntervalProbability, double lowerIntervalLimit, int maxCellSize ) {
        this.lowIntervalProbability = lowIntervalProbability;
        this.lowerIntervalLimit = lowerIntervalLimit;
        this.maxCellSize = maxCellSize;
        this.rand = RandomUtils.createRandom();
    }

    @Override
    public void setMaxCellSize( int maxCellSize ) {
        this.maxCellSize = maxCellSize;
    }

    @Override
    public ContractGraph assemble( ContractGraph graph ) {
        TimeLogger.log( "ASSEMBLING_ASSEMBLER_CALCULATION_COPY", TimeLogger.Command.CONTINUE );
        graph = (ContractGraph) graph.copy();
        TimeLogger.log( "ASSEMBLING_ASSEMBLER_CALCULATION_COPY", TimeLogger.Command.PAUSE );
//        System.out.println( "Assembling..." );
        // find max ids
        TimeLogger.log( "ASSEMBLING_ASSEMBLER_CALCULATION_MAXID", TimeLogger.Command.CONTINUE );
        long maxNodeId = -1;
        long maxEdgeId = -1;
        for ( ContractNode node : graph.getNodes() ) {
            maxNodeId = Math.max( maxNodeId, node.getId() );
        }
        for ( ContractEdge edge : graph.getEdges() ) {
            maxEdgeId = Math.max( maxEdgeId, edge.getId() );
        }
        TimeLogger.log( "ASSEMBLING_ASSEMBLER_CALCULATION_MAXID", TimeLogger.Command.PAUSE );
        // P = {{x,y}; x,y  V, {x,y} e E, s(x)+s(y) < U} // all pairs of adjacent vertices with combined size lower than U  
        // Sort P according to (minimizing): score({x,y}) = r * (w(x,y)/sqrt(s(x))+w(x,y)/sqrt(s(y))), where r is with probability a picked randomly from [0,b] and with probability 1-a picked randomly from [b,1]        
        TimeLogger.log( "ASSEMBLING_ASSEMBLER_CALCULATION_OTHER", TimeLogger.Command.CONTINUE );
        PriorityQueue<NodePair> queue = initQueue( graph, maxCellSize );
        MaxIdContainer maxNodeIdContainer = new MaxIdContainer( maxNodeId );
        MaxIdContainer maxEdgeIdContainer = new MaxIdContainer( maxEdgeId );
//        System.out.println( "MAX CELL SIZE = " + maxCellSize );
        // While P is not empty
        while ( !queue.isEmpty() ) {
            // pair = P.pop 
            NodePair pair = queue.extractMin();
//            System.out.println( "popping: " + pair.nodeA.getId() + ", " + pair.nodeB.getId() );
//            System.out.println( "popping detailed: " + pair );
            // Contract pair
            ContractNode source = pair.nodeA;
            ContractNode target = pair.nodeB;

            // clear pairs before contracting - before losing information about connections
            // - clear old pairs with source
            clearPairs( queue, pair, source );
            // - clear old pairs with target
            clearPairs( queue, pair, target );
            // now contract pair
            ContractNode contractedNode = source.mergeWith( target, maxNodeIdContainer, maxEdgeIdContainer );

            // Update score value (with the new r for each iteration) for the adjacent edges (pairs) of the contracted pair (edge) and if the new size s is higher or equal to U, remove pair from P
            // - add new pairs with the contracted node
            addPairs( queue, contractedNode, maxCellSize );
        }
        //End While
        //Return partitions, i.e. in which partition each vertex belongs
        // - add all edges to builder
//        UndirectedGraph.UndirectedGraphBuilder builder = UndirectedGraph.builder();
//        Set<ContractEdge> edges = new HashSet<>();
//        for ( ContractNode node : nodes ) {
//            edgeIterator = node.getEdgeIds();
//            while ( edgeIterator.hasNext() ) {
//                edges.add( (ContractEdge) edgeIterator.next() );
//            }
//        }
//        builder.nodes( nodes ).edges( edges );
        TimeLogger.log( "ASSEMBLING_ASSEMBLER_CALCULATION_OTHER", TimeLogger.Command.PAUSE );
        return graph;
    }

    PriorityQueue<NodePair> initQueue( ContractGraph graph, long currentMaxCellSize ) {
//        System.out.println( "INIT QUEUE" );
        double ratio = generateR();
        PriorityQueue<NodePair> queue = new FibonacciHeap<>();
        for ( ContractNode node : graph.getNodes() ) {
            for ( ContractEdge edge : node.getEdges() ) {
                ContractNode target = edge.getOtherNode( node );
                NodePair nodePair = new NodePair( node, target, edge );
                if ( !queue.contains( nodePair ) && ( node.getNodes().size() + target.getNodes().size() < currentMaxCellSize ) ) {
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

    PriorityQueue<NodePair> addPairs( PriorityQueue<NodePair> queue, ContractNode contractedNode, long currentMaxCellSize ) {
//        System.out.println( "ADDING FOR: " + contractedNode );
        double ratio = generateR();
        for ( ContractEdge edge : contractedNode.getEdges() ) {
            ContractNode neighbor = edge.getOtherNode( contractedNode );
            NodePair nodePair = new NodePair( contractedNode, neighbor, edge );
            if ( nodePair.nodeA.getNodes().size() + nodePair.nodeB.getNodes().size() < currentMaxCellSize ) {
//                System.out.println( "nodeA[" + nodePair.nodeA.getId() + "->" + nodePair.nodeA.getNodeIds().size() + "] + nodeB[" + nodePair.nodeB.getId() + "->" + nodePair.nodeB.getNodeIds().size() + "] < " + maxCellSize );
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
        return /*Double.MAX_VALUE*/ -ratio * ( ( edgeWeight / Math.sqrt( sourceWeight ) ) + ( edgeWeight / Math.sqrt( targetWeight ) ) );
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.assembly;

import cz.certicon.routing.model.graph.preprocessing.NodePair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
        this.rand = new Random();
    }

    @Override
    public Graph assemble( FilteredGraph graph ) {
//        System.out.println( "Assembling..." );
        // find max ids
        long maxNodeId = -1;
        long maxEdgeId = -1;
        Set<ContractNode> nodes = new HashSet<>();
        Iterator<Node> nodeIterator = graph.getNodes();
        while ( nodeIterator.hasNext() ) {
            ContractNode node = (ContractNode) nodeIterator.next();
            maxNodeId = Math.max( maxNodeId, node.getId() );
            nodes.add( node );
        }
        Iterator<Edge> edgeIterator = graph.getEdges();
        while ( edgeIterator.hasNext() ) {
            maxEdgeId = Math.max( maxEdgeId, edgeIterator.next().getId() );
        }
        // P = {{x,y}; x,y  V, {x,y} e E, s(x)+s(y) < U} // all pairs of adjacent vertices with combined size lower than U  
        // Sort P according to (minimizing): score({x,y}) = r * (w(x,y)/sqrt(s(x))+w(x,y)/sqrt(s(y))), where r is with probability a picked randomly from [0,b] and with probability 1-a picked randomly from [b,1]
        PriorityQueue<NodePair> queue = initQueue( graph );
        ContractNode.MaxIdContainer maxNodeIdContainer = new ContractNode.MaxIdContainer( maxNodeId );
        ContractNode.MaxIdContainer maxEdgeIdContainer = new ContractNode.MaxIdContainer( maxEdgeId );
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
            ContractNode contractedNode = source.mergeWith( target, maxNodeIdContainer, maxEdgeIdContainer );
            nodes.add( contractedNode );

            // Update score value (with the new r for each iteration) for the adjacent edges (pairs) of the contracted pair (edge) and if the new size s is higher or equal to U, remove pair from P
            // - clear old pairs with source
            clearPairs( queue, pair, source );
            // - clear old pairs with target
            clearPairs( queue, pair, target );
            // - add new pairs with the contracted node
            addPairs( queue, graph, contractedNode );
        }
        //End While
        //Return partitions, i.e. in which partition each vertex belongs
        // - add all edges to builder
        UndirectedGraph.UndirectedGraphBuilder builder = UndirectedGraph.builder();
        Set<ContractEdge> edges = new HashSet<>();
        for ( ContractNode node : nodes ) {
            edgeIterator = node.getEdges();
            while ( edgeIterator.hasNext() ) {
                edges.add( (ContractEdge) edgeIterator.next() );
            }
        }
        builder.nodes( nodes ).edges( edges );
        return new FilteredGraph( builder.build() );
    }

    PriorityQueue<NodePair> initQueue( FilteredGraph graph ) {
//        System.out.println( "INIT QUEUE" );
        double ratio = generateR();
        PriorityQueue<NodePair> queue = new FibonacciHeap<>();
        Iterator<Node> nodeIterator = graph.getNodes();
        while ( nodeIterator.hasNext() ) {
            ContractNode node = (ContractNode) nodeIterator.next();
            Iterator<Edge> edgeIterator = graph.getEdges( node );
            while ( edgeIterator.hasNext() ) {
                ContractEdge edge = (ContractEdge) edgeIterator.next();
                ContractNode target = (ContractNode) graph.getOtherNode( edge, node );
                NodePair nodePair = new NodePair( node, target, edge );
                if ( !queue.contains( nodePair ) ) {
//                    System.out.println( "ADDING: " + nodePair );
                    queue.add( nodePair, score( graph, nodePair, ratio ) );
                }
            }
        }
        return queue;
    }

    PriorityQueue<NodePair> clearPairs( PriorityQueue<NodePair> queue, NodePair origPair, ContractNode origNode ) {
//        System.out.println( "REMOVING FOR: " + origNode );
        Iterator<Edge> edgeIterator = origNode.getEdges();
        while ( edgeIterator.hasNext() ) {
            ContractEdge edge = (ContractEdge) edgeIterator.next();
            ContractNode neighbor = (ContractNode) edge.getOtherNode( origNode );
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

    PriorityQueue<NodePair> addPairs( PriorityQueue<NodePair> queue, FilteredGraph graph, ContractNode contractedNode ) {
//        System.out.println( "ADDING FOR: " + contractedNode );
        double ratio = generateR();
        Iterator<Edge> edgeIterator = contractedNode.getEdges();
        while ( edgeIterator.hasNext() ) {
            ContractEdge edge = (ContractEdge) edgeIterator.next();
            ContractNode neighbor = (ContractNode) edge.getOtherNode( contractedNode );
            NodePair nodePair = new NodePair( contractedNode, neighbor, edge );
            if ( nodePair.nodeA.getNodes().size() + nodePair.nodeB.getNodes().size() < maxCellSize ) {
                double newScore = score( graph, nodePair, ratio );
//                System.out.println( "ADDING: " + nodePair );
                queue.add( nodePair, newScore );
            }
        }
        return queue;
    }

    private double generateR() {
        if ( true ) {
            return 1.0;
        }
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

    private double score( FilteredGraph graph, NodePair nodePair, double ratio ) {
        ContractEdge edge = nodePair.connectingEdge;
//        System.out.println( "score@edge = " + edge );
        double edgeWeight = graph.getEdgeSize( edge );
        double sourceWeight = graph.getNodeSize( nodePair.nodeA );
        double targetWeight = graph.getNodeSize( nodePair.nodeB );
        return ratio * ( ( edgeWeight / Math.sqrt( sourceWeight ) ) + ( edgeWeight / Math.sqrt( targetWeight ) ) );
    }

}

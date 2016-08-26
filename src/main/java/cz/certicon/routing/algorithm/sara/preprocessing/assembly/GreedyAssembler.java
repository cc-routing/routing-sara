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
        long maxNodeId = -1;
        long maxEdgeId = -1;
        Map<NodePair, Edge> nodeEdgeMap = new HashMap<>();
        Map<Node, Set<NodePair>> nodePairMap = new HashMap<>();
        // P = {{x,y}; x,y  V, {x,y} e E, s(x)+s(y) < U} // all pairs of adjacent vertices with combined size lower than U  
        // Sort P according to (minimizing): score({x,y}) = r * (w(x,y)/sqrt(s(x))+w(x,y)/sqrt(s(y))), where r is with probability a picked randomly from [0,b] and with probability 1-a picked randomly from [b,1]
        double ratio = generateR();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<NodePair> queue = new FibonacciHeap<>();
        Set<ContractNode> nodes = new HashSet<>();
        Iterator<Node> nodeIterator = graph.getNodes();
        while ( nodeIterator.hasNext() ) {
            ContractNode node = (ContractNode) nodeIterator.next();
            nodes.add( node );
            maxNodeId = Math.max( maxNodeId, node.getId() );
            visited.add( node );
            Set<NodePair> nodePairSet = new HashSet<>();
            nodePairMap.put( node, nodePairSet );
            Iterator<Edge> edgeIterator = graph.getEdges( node );
            while ( edgeIterator.hasNext() ) {
                Edge edge = edgeIterator.next();
                maxEdgeId = Math.max( maxEdgeId, edge.getId() );
                ContractNode target = (ContractNode) graph.getOtherNode( edge, node );
                if ( !visited.contains( target ) ) {
                    NodePair nodePair = new NodePair( node, target );
                    nodeEdgeMap.put( nodePair, edge );
                    nodePairSet.add( nodePair );
                    queue.add( nodePair, score( graph, nodePair, nodeEdgeMap, ratio ) );
                }
            }
        }
        ContractNode.MaxIdContainer maxNodeIdContainer = new ContractNode.MaxIdContainer( maxNodeId );
        ContractNode.MaxIdContainer maxEdgeIdContainer = new ContractNode.MaxIdContainer( maxEdgeId );
        // While P is not empty
        while ( queue.isEmpty() ) {
            // pair = P.pop
            NodePair pair = queue.extractMin();
            nodeEdgeMap.remove( pair );
            // Contract pair
            ContractNode source = pair.nodeA;
            nodes.remove( source );
            ContractNode target = pair.nodeB;
            nodes.remove( target );
            ContractNode contractedNode = source.mergeWith( target, maxNodeIdContainer, maxEdgeIdContainer, nodeEdgeMap );
            nodes.add( contractedNode );

            // Update score value (with the new r for each iteration) for the adjacent edges (pairs) of the contracted pair (edge) and if the new size s is higher or equal to U, remove pair from P
            for ( NodePair nodePair : nodePairMap.get( source ) ) {
                queue.remove( nodePair );
                if ( nodePair.nodeA.getNodes().size() + nodePair.nodeB.getNodes().size() < maxCellSize ) {
                    ratio = generateR();
                    ContractNode other = nodePair.other( source );
                    NodePair newPair = new NodePair( contractedNode, other );
                    double newScore = score( graph, newPair, nodeEdgeMap, ratio );
                    queue.add( newPair, newScore );
                }
            }
            for ( NodePair nodePair : nodePairMap.get( target ) ) {
                queue.remove( nodePair );
                if ( nodePair.nodeA.getNodes().size() + nodePair.nodeB.getNodes().size() < maxCellSize ) {
                    ratio = generateR();
                    ContractNode other = nodePair.other( target );
                    NodePair newPair = new NodePair( contractedNode, other );
                    double newScore = score( graph, newPair, nodeEdgeMap, ratio );
                    queue.add( newPair, newScore );
                }
            }
        }
        //End While
        //Return partitions, i.e. in which partition each vertex belongs
        UndirectedGraph.UndirectedGraphBuilder builder = UndirectedGraph.builder();
        Set<ContractEdge> edges = new HashSet<>();
        for ( ContractNode node : nodes ) {
            Iterator<Edge> edgeIterator = node.getEdges();
            while ( edgeIterator.hasNext() ) {
                edges.add( (ContractEdge) edgeIterator.next() );
            }
        }
        builder.nodes( nodes ).edges( edges );
        return new FilteredGraph( builder.build() );
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

    private double score( FilteredGraph graph, NodePair nodePair, Map<NodePair, Edge> nodeEdgeMap, double ratio ) {
        Edge edge = nodeEdgeMap.get( nodePair );
        double edgeWeight = graph.getEdgeSize( edge );
        double sourceWeight = graph.getNodeSize( nodePair.nodeA );
        double targetWeight = graph.getNodeSize( nodePair.nodeB );
        return ratio * ( ( edgeWeight / Math.sqrt( sourceWeight ) ) + ( edgeWeight / Math.sqrt( targetWeight ) ) );
    }

}

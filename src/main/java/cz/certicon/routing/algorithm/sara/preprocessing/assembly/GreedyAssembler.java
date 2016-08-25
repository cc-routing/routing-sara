/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.assembly;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GreedyAssembler implements Assembler {

    @Override
    public Graph assemble( FilteredGraph graph ) {
        Map<Node, Map<Node, Edge>> nodeEdgeMap = new HashMap<>();
        Map<Node, Set<NodePair>> nodePairMap = new HashMap<>();
        // P = {{x,y}; x,y  V, {x,y} e E, s(x)+s(y) < U} // all pairs of adjacent vertices with combined size lower than U  
        // Sort P according to (minimizing): score({x,y}) = r * (w(x,y)/sqrt(s(x))+w(x,y)/sqrt(s(y))), where r is with probability a picked randomly from [0,b] and with probability 1-a picked randomly from [b,1]
        double r = generateR();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<NodePair> queue = new FibonacciHeap<>();
        Iterator<Node> nodes = graph.getNodes();
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            visited.add( node );
            Map<Node, Edge> nodeToEdgeMap = new HashMap<>();
            nodeEdgeMap.put( node, nodeToEdgeMap );
            Set<NodePair> nodePairSet = new HashSet<>();
            nodePairMap.put( node, nodePairSet );
            Iterator<Edge> edges = graph.getEdges( node );
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node target = graph.getOtherNode( edge, node );
                nodeToEdgeMap.put( target, edge );
                if ( !visited.contains( target ) ) {
                    NodePair nodePair = new NodePair( node, target );
                    nodePairSet.add( nodePair );
                    queue.add( nodePair, score( graph, node, target, r, nodeEdgeMap ) );
                }
            }
        }
        // While P is not empty
        while ( queue.isEmpty() ) {
            //    pair = P.pop
            NodePair pair = queue.extractMin();
            //    Contract pair
            //    Update score value (with the new r for each iteration) for the adjacent edges (pairs) of the contracted pair (edge) and if the new size s is higher or equal to U, remove pair from P

        }
        //End While
        //Return partitions, i.e. in which partition each vertex belongs
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    private double generateR() {
        return 1.0;
    }

    private double score( FilteredGraph graph, Node source, Node target, double ratio, Map<Node, Map<Node, Edge>> nodeEdgeMap ) {
        Edge edge = nodeEdgeMap.get( source ).get( target );
        double edgeWeight = graph.getEdgeSize( edge );
        double sourceWeight = graph.getNodeSize( source );
        double targetWeight = graph.getNodeSize( target );
        return ratio * ( ( edgeWeight / Math.sqrt( sourceWeight ) ) + ( edgeWeight / Math.sqrt( targetWeight ) ) );
    }

    private static class NodePair {

        private final Node nodeA;
        private final Node nodeB;

        public NodePair( Node nodeA, Node nodeB ) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + Objects.hashCode( this.nodeA ) * Objects.hashCode( this.nodeB );
            return hash;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null ) {
                return false;
            }
            if ( getClass() != obj.getClass() ) {
                return false;
            }
            final NodePair other = (NodePair) obj;
            if ( nodeA.getId() == other.nodeA.getId() ) {
                return nodeB.getId() == other.nodeB.getId();
            }
            if ( nodeA.getId() == other.nodeB.getId() ) {
                return nodeB.getId() == other.nodeA.getId();
            }
            return false;
        }

    }

}

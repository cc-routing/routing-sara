/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class PartitionGraph implements Graph {

    @Getter( AccessLevel.NONE )
    Graph graph;
    @Getter( AccessLevel.NONE )
    Map<Node, Partition> nodeToPartitionMap;
    @Getter( AccessLevel.NONE )
    Map<Partition, Collection<Node>> partitionToNodesMap;

    @Override
    public int getNodesCount() {
        return graph.getNodesCount();
    }

    @Override
    public Iterator<Node> getNodes() {
        return graph.getNodes();
    }

    @Override
    public int getEdgeCount() {
        return graph.getEdgeCount();
    }

    @Override
    public Iterator<Edge> getEdges() {
        return graph.getEdges();
    }

    @Override
    public Iterator<Edge> getIncomingEdges( Node node ) {
        return graph.getIncomingEdges( node );
    }

    @Override
    public Iterator<Edge> getOutgoingEdges( Node node ) {
        return graph.getOutgoingEdges( node );
    }

    @Override
    public Node getSourceNode( Edge edge ) {
        return graph.getSourceNode( edge );
    }

    @Override
    public Node getTargetNode( Edge edge ) {
        return graph.getTargetNode( edge );
    }

    @Override
    public Node getOtherNode( Edge edge, Node node ) {
        return graph.getOtherNode( edge, node );
    }

    @Override
    public Distance getTurnCost( Node node, Edge from, Edge to ) {
        return graph.getTurnCost( node, from, to );
    }

    @Override
    public Iterator<Edge> getEdges( Node node ) {
        return graph.getEdges( node );
    }

    @Override
    public Coordinate getNodeCoordinate( Node node ) {
        return graph.getNodeCoordinate( node );
    }

    public Partition getPartition( Node node ) {
        if ( !nodeToPartitionMap.containsKey( node ) ) {
            throw new IllegalArgumentException( "Unknown node: " + node );
        }
        return nodeToPartitionMap.get( node );
    }

    public Collection<Node> getNodes( Partition partition ) {
        if ( !partitionToNodesMap.containsKey( partition ) ) {
            throw new IllegalArgumentException( "Unknown partition: " + partition );
        }
        return partitionToNodesMap.get( partition );
    }

    public int getPartitionCount() {
        return partitionToNodesMap.keySet().size();
    }

    public Iterator<Partition> getPartitions() {
        return new ImmutableIterator<>( partitionToNodesMap.keySet().iterator() );
    }

    public Graph toPartitionsOnlyGraph() {
        UndirectedGraph.UndirectedGraphBuilder builder = UndirectedGraph.builder();
//        Set<ContractEdge> edges = new HashSet<>();
//        for ( ContractNode node : nodes ) {
//            edgeIterator = node.getEdges();
//            while ( edgeIterator.hasNext() ) {
//                edges.add( (ContractEdge) edgeIterator.next() );
//            }
//        }
//        builder.nodes( nodes ).edges( edges );

        long maxEdgeId = Long.MIN_VALUE;
        Iterator<Edge> edgeIterator = graph.getEdges();
        while ( edgeIterator.hasNext() ) {
            maxEdgeId = Math.max( maxEdgeId, edgeIterator.next().getId() );
        }
        Map<Long, ContractNode> nodeMap = new HashMap<>();
        Map<NodePair, ContractEdge> edgeMap = new HashMap<>();
        for ( Map.Entry<Partition, Collection<Node>> entry : partitionToNodesMap.entrySet() ) {
            ContractNode node = new ContractNode( entry.getKey().getId(), entry.getValue() );
            builder.node( node );
            for ( Node n : node.getNodes() ) {
                Iterator<Edge> edges = n.getEdges();
                while ( edges.hasNext() ) {
                    Edge e = edges.next();
                    Node t = e.getOtherNode( n );
                    Partition targetPartition = getPartition( t );
                    if ( nodeMap.containsKey( targetPartition.getId() ) ) {
                        ContractNode target = nodeMap.get( targetPartition.getId() );
                        NodePair pair = new NodePair( node, target );
                        ContractEdge edge = new ContractEdge( ++maxEdgeId, false, node, target, Distance.newInstance( 1 ), Arrays.asList( e ) );
                        if ( edgeMap.containsKey( pair ) ) {
                            edge = edge.mergeWith( edgeMap.get( pair ), node, target, maxEdgeId );
                        }
                        edgeMap.put( pair, edge );
                    }
                }
            }
            nodeMap.put( entry.getKey().getId(), node );
        }
        builder.edges( edgeMap.values() );
        return builder.build();
    }

    private static class NodePair {

        public final ContractNode nodeA;
        public final ContractNode nodeB;

        public NodePair( @NonNull ContractNode nodeA, @NonNull ContractNode nodeB ) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = (int) ( 37 * hash + nodeA.getId() * nodeB.getId() );
            return hash;
        }

        @Override
        public boolean equals( Object obj ) {
//        System.out.println( "NODEPAIR EQUALS: " + this + " vs " + obj );
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

//    @Override
//    public Graph copy() {
//        Graph graphCopy = graph.copy();
//        Map<Node, Partition> nodeInfoMapCopy = new HashMap<>();
//        Iterator<Node> nodes = graphCopy.getNodes();
//        while ( nodes.hasNext() ) {
//            Node node = nodes.next();
//            nodeInfoMapCopy.put( node, partitionMap.get( node ) );
//        }
//        return new PartitionGraph( (UndirectedGraph) graphCopy, nodeInfoMapCopy );
//    }
}

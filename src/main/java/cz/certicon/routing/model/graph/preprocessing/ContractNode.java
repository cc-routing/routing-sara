/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.AbstractNode;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.utils.collections.CollectionUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ContractNode extends AbstractNode<ContractEdge> {

    private final Collection<Node> nodes;

    public ContractNode( long id, Collection<Node> nodes ) {
        super( id );
        this.nodes = new HashSet<>( nodes );
    }

    public ContractNode mergeWith( Graph<ContractNode, ContractEdge> graph, ContractNode node, MaxIdContainer nodeMaxIdContainer, MaxIdContainer edgeMaxIdContainer ) {
        Set<Node> newNodes = new HashSet<>( this.nodes );
        newNodes.addAll( node.nodes );
        ContractNode contractedNode = new ContractNode( nodeMaxIdContainer.next(), newNodes );
        Map<ContractNode, Set<ContractEdge>> targetMap = new HashMap<>();
//        System.out.println( "iterator edges for: " + this );
        for ( ContractEdge edge : getEdges( graph ) ) {
            ContractNode target = edge.getOtherNode( graph, this );
            if ( !target.equals( node ) ) {
//                System.out.println( "edge = " + edge + ", target = " + target );
                CollectionUtils.getSet( targetMap, target ).add( edge );
            }
        }
        for ( ContractEdge edge : node.getEdges( graph ) ) {
            ContractNode target = edge.getOtherNode( graph, node );
            if ( !target.equals( this ) ) {
//                System.out.println( "edge = " + edge + ", target = " + target );
                CollectionUtils.getSet( targetMap, target ).add( edge );
            }
        }
        for ( Map.Entry<ContractNode, Set<ContractEdge>> entry : targetMap.entrySet() ) {
//            System.out.println( "target map entry = " + entry );
            ContractNode target = entry.getKey();
            Set<ContractEdge> edges = entry.getValue();
            ContractEdge prev = null;
            ContractEdge curr = null;
            for ( ContractEdge edge : edges ) {
                target.removeEdge( edge );
                curr = (ContractEdge) edge;
                if ( prev != null ) {
                    curr = prev.mergeWith( curr, contractedNode, target, edgeMaxIdContainer.next() );
                } else {
                    curr = new ContractEdge( edgeMaxIdContainer.next(), false, contractedNode, target, new HashSet<>( curr.getEdges() ) );
                }
//                System.out.println( "curr=" + curr );
                prev = curr;
            }
            target.addEdge( curr );
            contractedNode.addEdge( curr );
        }
        return contractedNode;
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    public static class MaxIdContainer {

        private long maxId;

        public MaxIdContainer( long maxId ) {
            this.maxId = maxId;
        }

        public long getEdgeMaxId() {
            return maxId;
        }

        public long next() {
            return ++maxId;
        }

    }
}

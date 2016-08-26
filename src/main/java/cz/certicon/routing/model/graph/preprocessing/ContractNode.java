/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
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
public class ContractNode extends Node {

    private final Collection<Node> nodes;

    public ContractNode( long id, Set<Node> nodes ) {
        super( id );
        this.nodes = nodes;
    }

    public ContractNode mergeWith( ContractNode node, MaxIdContainer nodeMaxIdContainer, MaxIdContainer edgeMaxIdContainer, Map<NodePair, Edge> nodeEdgeMap ) {
        Set<Node> newNodes = new HashSet<>( this.nodes );
        newNodes.addAll( node.nodes );
        ContractNode contractedNode = new ContractNode( nodeMaxIdContainer.preIncrement(), newNodes );
        Map<Node, Set<Edge>> targetMap = new HashMap<>();
        Iterator<Edge> thisIterator = getEdges();
        while ( thisIterator.hasNext() ) {
            Edge edge = thisIterator.next();
            Node target = edge.getOtherNode( this );
            CollectionUtils.getSet( targetMap, target ).add( edge );
        }
        Iterator<Edge> otherIterator = node.getEdges();
        while ( otherIterator.hasNext() ) {
            Edge edge = otherIterator.next();
            Node target = edge.getOtherNode( node );
            CollectionUtils.getSet( targetMap, target ).add( edge );
        }
        for ( Map.Entry<Node, Set<Edge>> entry : targetMap.entrySet() ) {
            ContractNode target = (ContractNode) entry.getKey();
            Set<Edge> edges = entry.getValue();
            ContractEdge prev = null;
            ContractEdge curr = null;
            for ( Edge edge : edges ) {
                target.removeEdge( edge );
                curr = (ContractEdge) edge;
                if ( prev != null ) {
                    curr = prev.mergeWith( curr, contractedNode, target, edgeMaxIdContainer.preIncrement() );
                }
                prev = curr;
            }
            target.addEdge( curr );
            contractedNode.addEdge( curr );
            if ( nodeEdgeMap != null ) {
                nodeEdgeMap.put( new NodePair( contractedNode, target ), curr );
            }
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

        public long preIncrement() {
            return ++maxId;
        }

        public long postIncrement() {
            return maxId++;
        }

    }
}

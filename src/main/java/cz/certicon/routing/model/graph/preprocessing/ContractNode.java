/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.basic.IdSupplier;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.AbstractNode;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.utils.StringUtils;
import cz.certicon.routing.utils.collections.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Special implementation of the {@link Node} interface. ContractNode supports contractions - merging of multiple nodes into one. Such ContractNode contains all the original nodes.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ContractNode extends AbstractNode<ContractNode, ContractEdge> {

    private final Collection<Node> nodes;

    /**
     * Constructor
     *
     * @param graph graph containing this node
     * @param id    node id
     * @param nodes original nodes
     */
    ContractNode( Graph<ContractNode, ContractEdge> graph, long id, Collection<? extends Node> nodes ) {
        super( graph, id );
        this.nodes = new HashSet<>( nodes );
    }

    /**
     * Merges (contracts) this node with the given node, the new node gets id supplied by the nodeIdSupploer, all the new edges get ids supplied by the edgeIdSupplier
     *
     * @param node           other node to merge with
     * @param nodeIdSupplier supplier for the new node's id
     * @param edgeIdSupplier supplier for the new edges' ids
     * @return new contracted edge
     */
    public ContractNode mergeWith( ContractNode node, IdSupplier nodeIdSupplier, IdSupplier edgeIdSupplier ) {

//        System.out.println( "N-MERGING: graph = " + graph );
//        System.out.println( "N-MERGE " + this );
//        System.out.println( "N-WITH " + node );
        Set<Node> newNodes = new HashSet<>( this.nodes );
        newNodes.addAll( node.nodes );
        ContractNode contractedNode = ( (ContractGraph) getGraph() ).createNode( nodeIdSupplier.next(), newNodes );
        Map<ContractNode, Set<ContractEdge>> targetMap = new HashMap<>();
        boolean connected = false;
//        System.out.println( "iterator edges for: " + this );
        for ( ContractEdge edge : getEdges() ) {
            ContractNode target = edge.getOtherNode( this );
            if ( !target.equals( node ) ) {
//                System.out.println( "edge = " + edge + ", target = " + target );
                CollectionUtils.getSet( targetMap, target ).add( edge );
            } else {
                connected = true;
            }
        }
        for ( ContractEdge edge : node.getEdges() ) {
            ContractNode target = edge.getOtherNode( node );
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
                curr = edge;
                if ( prev != null ) {
                    curr = prev.mergeWith( curr, contractedNode, target, edgeIdSupplier.next() );
                } else {
                    curr = ( (ContractGraph) getGraph() ).createEdge( edgeIdSupplier.next(), false, contractedNode, target, new HashSet<>( curr.getEdges() ), new Pair<>( Metric.SIZE, edge.getLength( Metric.SIZE ) ) );
                }
//                System.out.println( "curr=" + curr );
                prev = curr;
            }
        }
        getGraph().removeNode( node );
        getGraph().removeNode( this );
//        System.out.println( "N-MERGED NODE " + contractedNode );
//        System.out.println( "N-RESULT: " + graph );
        return contractedNode;
    }

    @Override
    protected String additionalToStringData() {
        return super.additionalToStringData() + ", nodes=" + StringUtils.toArray( nodes );
    }

    /**
     * Returns collection of original nodes contained by this node
     *
     * @return collection of original nodes contained by this node
     */
    public Collection<Node> getNodes() {
        return nodes;
    }

    @Override
    protected ContractNode newInstance( Graph<ContractNode, ContractEdge> newGraph, long id ) {
        return new ContractNode( newGraph, id, new HashSet<>( nodes ) );
    }

}

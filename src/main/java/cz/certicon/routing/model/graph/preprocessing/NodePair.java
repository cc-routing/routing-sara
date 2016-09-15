/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.Graph;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class NodePair {

    public final ContractNode nodeA;
    public final ContractNode nodeB;
    public final ContractEdge connectingEdge;

    public NodePair( Graph<ContractNode, ContractEdge> graph, ContractNode nodeA, ContractNode nodeB, ContractEdge connectingEdge ) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.connectingEdge = connectingEdge;
        if ( ( connectingEdge.getSource().equals( nodeA ) && !connectingEdge.getTarget().equals( nodeB ) ) || ( connectingEdge.getSource().equals( nodeB ) && !connectingEdge.getTarget().equals( nodeA ) ) ) {
            throw new IllegalArgumentException( "Edge does not match the nodes: edge = " + connectingEdge + ", nodeA = " + nodeA + ", nodeB = " + nodeB );
        }
    }

    public ContractNode other( ContractNode node ) {
        if ( nodeA.equals( node ) ) {
            return nodeB;
        }
        if ( nodeB.equals( node ) ) {
            return nodeA;
        }
        throw new IllegalArgumentException( "Unknown node: " + node );
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

    @Override
    public String toString() {
        return "NodePair{" + "nodeA=" + nodeA + ", nodeB=" + nodeB + ", connectingEdge=" + connectingEdge + '}';
    }

}

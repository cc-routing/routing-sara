/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

/**
 * Container for the node pair - two nodes and the edge connecting them.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class NodePair {

    public final ContractNode nodeA;
    public final ContractNode nodeB;
    public final ContractEdge connectingEdge;

    /**
     * Constructor
     *
     * @param nodeA          source/target node
     * @param nodeB          target/source node
     * @param connectingEdge edge connecting nodeA and node B
     */
    public NodePair( ContractNode nodeA, ContractNode nodeB, ContractEdge connectingEdge ) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.connectingEdge = connectingEdge;
        if ( ( connectingEdge.getSource().equals( nodeA ) && !connectingEdge.getTarget().equals( nodeB ) ) || ( connectingEdge.getSource().equals( nodeB ) && !connectingEdge.getTarget().equals( nodeA ) ) ) {
            throw new IllegalArgumentException( "Edge does not match the nodes: edge = " + connectingEdge + ", nodeA = " + nodeA + ", nodeB = " + nodeB );
        }
    }

    /**
     * Returns the other node of this pair
     *
     * @param node not other node
     * @return other node
     */
    public ContractNode other( ContractNode node ) {
        if ( nodeA.equals( node ) ) {
            return nodeB;
        }
        if ( nodeB.equals( node ) ) {
            return nodeA;
        }
        throw new IllegalArgumentException( "Unknown node: " + node );
    }

    /**
     * Returns size (number of nodes) of the nodeA
     *
     * @return size (number of nodes) of the nodeA
     */
    public int getSizeA() {
        return nodeA.getNodes().size();
    }

    /**
     * Returns size (number of nodes) of the nodeB
     *
     * @return size (number of nodes) of the nodeB
     */
    public int getSizeB() {
        return nodeB.getNodes().size();
    }

    /**
     * Returns size of this pair - both nodeA and nodeB
     *
     * @return size of this pair - both nodeA and nodeB
     */
    public int getSize() {
        return getSizeA() + getSizeB();
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

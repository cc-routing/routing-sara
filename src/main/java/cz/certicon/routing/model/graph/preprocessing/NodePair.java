/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import java.util.Objects;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class NodePair {
    
    public final ContractNode nodeA;
    public final ContractNode nodeB;

    public NodePair( ContractNode nodeA, ContractNode nodeB ) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
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

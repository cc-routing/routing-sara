/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.Node;
import java.util.Collection;
import java.util.HashSet;
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

    public ContractNode mergeWith( ContractNode node, long id ) {
        Set<Node> newNodes = new HashSet<>( this.nodes );
        newNodes.addAll( node.nodes );
        return new ContractNode( id, newNodes );
    }

    public Collection<Node> getNodes() {
        return nodes;
    }
}

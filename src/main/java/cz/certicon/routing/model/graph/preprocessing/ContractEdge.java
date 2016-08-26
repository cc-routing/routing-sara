/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Distance;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ContractEdge extends Edge {
    
    private final Collection<Edge> edges;

    public ContractEdge( long id, boolean oneway, Node source, Node target, Distance length, Set<Edge> edges ) {
        super( id, oneway, source, target, length );
        this.edges = edges;
    }

    public ContractEdge mergeWith( ContractEdge edge, long id ) {
        Set<Edge> newEdges = new HashSet<>( this.edges );
        newEdges.addAll( edge.edges );
        return new ContractEdge( id, isOneway(), getSource(), getTarget(), getLength().add( edge.getLength() ), newEdges );
    }

    public Collection<Edge> getEdges() {
        return edges;
    }
    
}

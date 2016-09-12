/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.AbstractEdge;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ContractEdge extends AbstractEdge<ContractNode> {

    private final Collection<Edge> edges;

    public ContractEdge( long id, boolean oneway, ContractNode source, ContractNode target, Collection<Edge> edges ) {
        super( id, oneway, source, target, -1, -1 );
        this.edges = new HashSet<>( edges );
    }

    public ContractEdge mergeWith( ContractEdge edge, ContractNode newSource, ContractNode newTarget, long id ) {
//        if ( ( !getSource().equals( edge.getSource() ) || !getTarget().equals( edge.getTarget() ) ) && ( !getSource().equals( edge.getTarget() ) || !getTarget().equals( edge.getSource() ) ) ) {
//            throw new IllegalArgumentException( "Cannot merge edges: this = " + this + ", other = " + edge );
//        }
        Set<Edge> newEdges = new HashSet<>( this.edges );
        newEdges.addAll( edge.edges );// TODO update metric
        return new ContractEdge( id, false, newSource, newTarget, newEdges );
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    public int calculateWidth( Graph<ContractNode, ContractEdge> graph ) {
        int width = 0;
        for ( Edge edge : edges ) {
            width += edge.isOneWay( graph ) ? 1 : 2;
        }
        return width;
    }

    @Override
    public <E extends Edge> Distance getTurnDistance( Graph<ContractNode, E> graph, TurnTable turnTable, E targetEdge ) {
        return Distance.newInstance( 0 );
    }

}

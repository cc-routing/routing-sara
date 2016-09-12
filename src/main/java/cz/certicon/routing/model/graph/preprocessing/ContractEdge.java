/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.AbstractEdge;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
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

    public ContractEdge mergeWith( Graph<ContractNode, ContractEdge> graph, ContractEdge edge, ContractNode newSource, ContractNode newTarget, long id ) {
//        if ( ( !getSource().equals( edge.getSource() ) || !getTarget().equals( edge.getTarget() ) ) && ( !getSource().equals( edge.getTarget() ) || !getTarget().equals( edge.getSource() ) ) ) {
//            throw new IllegalArgumentException( "Cannot merge edges: this = " + this + ", other = " + edge );
//        }
        System.out.println( "Merging: " + this + " with " + edge );
        Set<Edge> newEdges = new HashSet<>( this.edges );
        newEdges.addAll( edge.edges );
        ContractEdge contractEdge = new ContractEdge( id, false, newSource, newTarget, newEdges );
        graph.setLength( Metric.SIZE, contractEdge, graph.getLength( Metric.SIZE, this ).add( graph.getLength( Metric.SIZE, edge ) ) );
        return contractEdge;
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
    public <E extends Edge> Distance getTurnDistance( Graph<ContractNode, E> graph, ContractNode node, TurnTable turnTable, E targetEdge ) {
        return Distance.newInstance( 0 );
    }

}

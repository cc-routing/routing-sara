/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.AbstractEdge;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.StringUtils;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Special implementation of the {@link Edge} interface. ContractEdge supports contractions - merging of multiple edges into one. Such ContractEdge contains all the original edges.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ContractEdge extends AbstractEdge<ContractNode, ContractEdge> {

    private final Collection<Edge> edges;

    /**
     * Constructor
     *
     * @param graph graph containing this edge
     * @param id    edge id
     * @param edges original edges
     */
    public ContractEdge( Graph<ContractNode, ContractEdge> graph, long id, boolean oneway, ContractNode source, ContractNode target, Collection<? extends Edge> edges ) {
        super( graph, id, oneway, source, target, -1, -1 );
        this.edges = new HashSet<>( edges );
//        System.out.println( "Creating edge: " + this  );
    }


    /**
     * Merges (contracts) this edge with the given edge, the new edge
     *
     * @param edge      other edge to merge with
     * @param newSource new source
     * @param newTarget new target
     * @param id        new id
     * @return new (contracted) edge
     */
    public ContractEdge mergeWith( ContractEdge edge, ContractNode newSource, ContractNode newTarget, long id ) {
//        if ( ( !getSource().equals( edge.getSource() ) || !getTarget().equals( edge.getTarget() ) ) && ( !getSource().equals( edge.getTarget() ) || !getTarget().equals( edge.getSource() ) ) ) {
//            throw new IllegalArgumentException( "Cannot merge edges: this = " + this + ", other = " + edge );
//        }
//        System.out.println( "Merging: " + this + " with " + edge );
//        System.out.println( "E-MERGING: graph = " + graph );
//        System.out.println( "E-MERGE " + this );
//        System.out.println( "E-WITH " + edge );
        Set<Edge> newEdges = new HashSet<>( this.edges );
        newEdges.addAll( edge.edges );
        ContractEdge contractEdge = ( (ContractGraph) getGraph() ).createEdge( id, false, newSource, newTarget, newEdges, new Pair<>( Metric.SIZE, getLength( Metric.SIZE ).add( edge.getLength( Metric.SIZE ) ) ) );
        getGraph().removeEdge( edge );
        getGraph().removeEdge( this );
//        System.out.println( "E-MERGED EDGE " + contractEdge );
//        System.out.println( "E-RESULT: " + graph );
        return contractEdge;
    }

    /**
     * Returns collection of original edges contained by this edge
     *
     * @return collection of original edges contained by this edge
     */
    public Collection<Edge> getEdges() {
        return edges;
    }

    /**
     * Calculates width of this edge - width is sum of all the original edges, where oneway edge has value of 1 and twoway edge has value of 2
     *
     * @return width of this edge
     */
    public int calculateWidth() {
        int width = 0;
        for ( Edge edge : edges ) {
            width += edge.isOneWay() ? 1 : 2;
        }
        return width;
    }

    @Override
    protected String additionalToStringData() {
        return super.additionalToStringData() + ", edges=" + StringUtils.toArray( edges );
    }

    @Override
    public Distance getTurnDistance( ContractNode node, TurnTable turnTable, ContractEdge targetEdge ) {
        return Distance.newInstance( 0 );
    }

    @Override
    protected ContractEdge newInstance( Graph<ContractNode, ContractEdge> newGraph, long id, boolean oneway, ContractNode newSource, ContractNode newTarget, int sourceIndex, int targetIndex ) {
        return new ContractEdge( newGraph, id, oneway, newSource, newTarget, new HashSet<>( edges ) );
    }

}

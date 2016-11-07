/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.values.Distance;

import java.util.Collection;
import java.util.Set;

/**
 * Sara implementation of the {@link Graph} interface used for {@link cz.certicon.routing.algorithm.sara.query.mld.MultilevelDijkstraAlgorithm}
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class SaraGraph extends AbstractUndirectedGraph<SaraNode, SaraEdge> {

    /**
     * Constructor
     */
    public SaraGraph() {
    }

    /**
     * Constructor
     *
     * @param metrics supported metrics
     */
    public SaraGraph( Collection<Metric> metrics ) {
        super( metrics );
    }

    /**
     * Creates and returns new node for the given id and parent
     *
     * @param id     node's id
     * @param parent node's parent
     * @return new node
     */
    public SaraNode createNode( long id, Cell parent ) {
        SaraNode node = new SaraNode( this, id, parent );
        addNode( node );
        return node;
    }

    /**
     * Creates and returns new edge for the given data
     *
     * @param id          edge's id
     * @param oneway      whether the edge is oneway or twoway
     * @param source      edge's source node
     * @param target      edge's target node
     * @param sourceIndex index to source's turn-table
     * @param targetIndex index to target's turn-table
     * @param metrics     collection of distances and metrics
     * @return new edge
     */
    public final SaraEdge createEdge( long id, boolean oneway, SaraNode source, SaraNode target, int sourceIndex, int targetIndex, Collection<Pair<Metric, Distance>> metrics ) {
        SaraEdge edge = new SaraEdge( this, id, oneway, source, target, sourceIndex, targetIndex );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            edge.setLength( metric.a, metric.b );
        }
        return edge;
    }

    /**
     * Creates and returns new edge for the given data
     *
     * @param id          edge's id
     * @param oneway      whether the edge is oneway or twoway
     * @param source      edge's source node
     * @param target      edge's target node
     * @param sourceIndex index to source's turn-table
     * @param targetIndex index to target's turn-table
     * @param metrics     array of distances and metrics
     * @return new edge
     */
    @SafeVarargs
    public final SaraEdge createEdge( long id, boolean oneway, SaraNode source, SaraNode target, int sourceIndex, int targetIndex, Pair<Metric, Distance>... metrics ) {
        SaraEdge edge = new SaraEdge( this, id, oneway, source, target, sourceIndex, targetIndex );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            edge.setLength( metric.a, metric.b );
        }
        return edge;
    }

    @Override
    public Graph<SaraNode, SaraEdge> newInstance( Set<Metric> metrics ) {
        return new SaraGraph( metrics );
    }
}

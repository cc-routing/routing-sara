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
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class SaraGraph extends AbstractUndirectedGraph<SaraNode, SaraEdge> {

    public SaraGraph() {
    }

    public SaraGraph( Collection<Metric> metrics ) {
        super( metrics );
    }

    public Cell getParent( SaraNode node ) {
        return node.getParent();
    }

    public SaraNode createNode( long id, Cell parent ) {
        SaraNode node = new SaraNode( this, id, parent );
        addNode( node );
        return node;
    }

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

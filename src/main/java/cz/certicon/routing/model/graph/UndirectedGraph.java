/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.values.Distance;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class UndirectedGraph extends AbstractUndirectedGraph<SimpleNode, SimpleEdge> {

    public UndirectedGraph() {
        super();
    }

    public UndirectedGraph( Set<Metric> metrics ) {
        super( metrics );
    }

    public SimpleNode createNode( long id ) {
        SimpleNode node = new SimpleNode( this, id );
        addNode( node );
        return node;
    }

    public SimpleEdge createEdge( long id, boolean oneway, SimpleNode source, SimpleNode target, int sourceIndex, int targetIndex, Pair<Metric, Distance>... metrics ) {
        SimpleEdge edge = new SimpleEdge( this, id, oneway, source, target, sourceIndex, targetIndex );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            setLength( metric.a, edge, metric.b );
        }
        return edge;
    }

    @Override
    protected AbstractUndirectedGraph<SimpleNode, SimpleEdge> newInstance( Set<Metric> metrics ) {
        return new UndirectedGraph( metrics );
    }

}

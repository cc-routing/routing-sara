/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

/**
 * Basic implementation of the {@link Edge} interface.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class SimpleEdge extends AbstractEdge<SimpleNode, SimpleEdge> {

    SimpleEdge( Graph<SimpleNode, SimpleEdge> graph, long id, boolean oneway, SimpleNode source, SimpleNode target, int sourceIndex, int targetIndex ) {
        super( graph, id, oneway, source, target, sourceIndex, targetIndex );
    }

    @Override
    protected SimpleEdge newInstance( Graph<SimpleNode, SimpleEdge> newGraph, long id, boolean oneway, SimpleNode newSource, SimpleNode newTarget, int sourceIndex, int targetIndex ) {
        return new SimpleEdge( newGraph, id, oneway, newSource, newTarget, sourceIndex, targetIndex );
    }

}

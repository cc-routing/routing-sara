/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SimpleEdge extends AbstractEdge<SimpleNode, SimpleEdge> {

    SimpleEdge( Graph<SimpleNode, SimpleEdge> graph, long id, boolean oneway, SimpleNode source, SimpleNode target, int sourceIndex, int targetIndex ) {
        super( graph, id, oneway, source, target, sourceIndex, targetIndex );
    }

}

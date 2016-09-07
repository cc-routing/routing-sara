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
public class SimpleEdge extends AbstractEdge<SimpleNode> {

    public SimpleEdge( long id, boolean oneway, SimpleNode source, SimpleNode target, int sourceIndex, int targetIndex ) {
        super( id, oneway, source, target, sourceIndex, targetIndex );
    }
}

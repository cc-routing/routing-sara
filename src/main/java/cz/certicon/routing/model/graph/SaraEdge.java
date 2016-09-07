/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class SaraEdge extends AbstractEdge<SaraNode> {

    public SaraEdge( long id, boolean oneway, SaraNode source, SaraNode target, int sourceIndex, int targetIndex ) {
        super( id, oneway, source, target, sourceIndex, targetIndex );
    }

}

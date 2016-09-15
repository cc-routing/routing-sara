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
public class SaraEdge extends AbstractEdge<SaraNode, SaraEdge> {

    public SaraEdge( Graph<SaraNode, SaraEdge> graph, long id, boolean oneway, SaraNode source, SaraNode target, int sourceIndex, int targetIndex ) {
        super( graph, id, oneway, source, target, sourceIndex, targetIndex );
    }

}

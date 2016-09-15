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
public class SaraNode extends AbstractNode<SaraNode, SaraEdge> {

    private final Cell parent;

    SaraNode( Graph<SaraNode, SaraEdge> graph, long id, Cell parent ) {
        super( graph, id );
        this.parent = parent;
    }

    public Cell getParent() {
        return parent;
    }

}

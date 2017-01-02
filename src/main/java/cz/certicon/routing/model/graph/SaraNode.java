/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

/**
 * {@link Node} implementation for the {@link SaraGraph}.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class SaraNode extends AbstractNode<SaraNode, SaraEdge> implements Parentable {

    private final Cell parent;

    protected SaraNode( Graph<SaraNode, SaraEdge> graph, long id, Cell parent ) {
        super( graph, id );
        this.parent = parent;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Returns parent cell
     *
     * @return parent cell
     */
    @Override
    public Cell getParent() {
        return parent;
    }

    @Override
    protected SaraNode newInstance( Graph<SaraNode, SaraEdge> newGraph, long id ) {
        return new SaraNode( newGraph, id, parent );
    }

}

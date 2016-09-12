/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <N> node type
 */
public abstract class AbstractEdge<N extends Node> implements Edge<N> {

    private final long id;
    private final boolean oneway;
    private final N source;
    private final N target;
    private final int sourceIndex;
    private final int targetIndex;

    public AbstractEdge( long id, boolean oneway, N source, N target, int sourceIndex, int targetIndex ) {
        this.id = id;
        this.oneway = oneway;
        this.source = source;
        this.target = target;
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
    }

    @Override
    public <E extends Edge> N getSource( Graph<N, E> graph ) {
        return source;
    }

    @Override
    public <E extends Edge> N getTarget( Graph<N, E> graph ) {
        return target;
    }

    @Override
    public <E extends Edge> N getOtherNode( Graph<N, E> graph, N node ) {
        if ( node.equals( source ) ) {
            return target;
        } else if ( node.equals( target ) ) {
            return source;
        }
        throw new IllegalArgumentException( "Edge does not contain given node: edge = " + this + ", node = " + node.getId() );
    }

    @Override
    public <E extends Edge> Distance getTurnDistance( Graph<N, E> graph, N node, TurnTable turnTable, E targetEdge ) {
        return turnTable.getCost( getIndex( graph, node, this ), getIndex( graph, node, targetEdge ) );
    }

    @Override
    public <E extends Edge> boolean isOneWay( Graph<N, E> graph ) {
        return oneway;
    }

    public long getId() {
        return id;
    }

    @Override
    public int getSourcePosition() {
        return sourceIndex;
    }

    @Override
    public int getTargetPosition() {
        return targetIndex;
    }

    @Override
    public String toString() {
        return "Edge{id=" + id + ", oneway=" + oneway + ", source=Node{id=" + source.getId() + "}, target=Node{id=" + target.getId() + "}}";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) ( this.id ^ ( this.id >>> 32 ) );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final AbstractEdge<?> other = (AbstractEdge<?>) obj;
        if ( this.id != other.id ) {
            return false;
        }
        return true;
    }

    private <E extends Edge> int getIndex( Graph<N, E> graph, Node node, Edge edge ) {
        int idx;
        if ( edge.getSource( graph ).equals( node ) ) {
            idx = ( (AbstractEdge) edge ).sourceIndex;
        } else if ( edge.getTarget( graph ).equals( node ) ) {
            idx = ( (AbstractEdge) edge ).targetIndex;
        } else {
            throw new IllegalArgumentException( "Edge does not contain given node: edge = " + this + ", node = " + node.getId() );
        }
        return idx;
    }

}

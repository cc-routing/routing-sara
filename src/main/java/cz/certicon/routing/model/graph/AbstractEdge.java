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
 * @param <E> edge type
 */
public abstract class AbstractEdge<N extends Node, E extends Edge> implements Edge<N, E> {

    private final long id;
    private final boolean oneway;
    private final N source;
    private final N target;
    private final int sourceIndex;
    private final int targetIndex;
    private final Graph<N, E> graph;

    public AbstractEdge( Graph<N, E> graph, long id, boolean oneway, N source, N target, int sourceIndex, int targetIndex ) {
        this.graph = graph;
        this.id = id;
        this.oneway = oneway;
        this.source = source;
        this.target = target;
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
    }

    @Override
    public N getSource() {
        return source;
    }

    @Override
    public N getTarget() {
        return target;
    }

    @Override
    public N getOtherNode( N node ) {
        if ( node.equals( source ) ) {
            return target;
        } else if ( node.equals( target ) ) {
            return source;
        }
        throw new IllegalArgumentException( "Edge does not contain given node: edge = " + this + ", node = " + node.getId() );
    }

    @Override
    public Distance getTurnDistance( N node, TurnTable turnTable, E targetEdge ) {
        return turnTable.getCost( getIndex( node, this ), getIndex( node, targetEdge ) );
    }

    @Override
    public boolean isOneWay() {
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
    public Distance getLength( Metric metric ) {
        return graph.getLength( metric, (E) this );
    }

    @Override
    public void setLength( Metric metric, Distance distance ) {
        graph.setLength( metric, (E) this, distance );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", oneway=" + oneway + ", source=Node{id=" + source.getId() + "}, target=Node{id=" + target.getId() + additionalToStringData() + "}}";
    }

    protected String additionalToStringData() {
        return "";
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
        final AbstractEdge<?, ?> other = (AbstractEdge<?, ?>) obj;
        if ( this.id != other.id ) {
            return false;
        }
        return true;
    }

    protected Graph<N, E> getGraph() {
        return graph;
    }

    private int getIndex( Node node, Edge edge ) {
        int idx;
        if ( edge.getSource().equals( node ) ) {
            idx = ( (AbstractEdge) edge ).sourceIndex;
        } else if ( edge.getTarget().equals( node ) ) {
            idx = ( (AbstractEdge) edge ).targetIndex;
        } else {
            throw new IllegalArgumentException( "Edge does not contain given node: edge = " + this + ", node = " + node.getId() );
        }
        return idx;
    }

}

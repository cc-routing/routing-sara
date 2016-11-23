/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;

import java.util.EnumMap;

/**
 * Skeletal implementation of the {@link Edge} interface.
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public abstract class AbstractEdge<N extends Node, E extends Edge> implements Edge<N, E> {

    private final long id;
    private final boolean oneway;
    private N source;
    private N target;
    private int sourceIndex;
    private int targetIndex;
    private final Graph<N, E> graph;
    private final EnumMap<Metric, Distance> distanceMap = new EnumMap<>( Metric.class );
    private boolean locked = false;

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
    public boolean isSource( N node ) {
        return source.equals( node );
    }

    @Override
    public boolean isTarget( N node ) {
        return target.equals( node );
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
        return distanceMap.get( metric );
    }

    @Override
    public void setLength( Metric metric, Distance distance ) {
        checkLock();
        if ( !getGraph().hasMetric( metric ) ) {
            throw new IllegalArgumentException( "Graph does not support metric: " + metric.name() );
        }
        distanceMap.put( metric, distance );
    }

    @Override
    public E copy( Graph<N, E> newGraph, N newSource, N newTarget ) {
        E newInstance = newInstance( newGraph, id, oneway, newSource, newTarget, sourceIndex, targetIndex );
        newSource.addEdge( newInstance );
        newTarget.addEdge( newInstance );
        return newInstance;
    }

    abstract protected E newInstance( Graph<N, E> newGraph, long id, boolean oneway, N newSource, N newTarget, int sourceIndex, int targetIndex );

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", oneway=" + oneway + ", source=Node{id=" + source.getId() + "}, target=Node{id=" + target.getId() + "}, sourceIndex=" + sourceIndex + ", targetIndex=" + targetIndex + additionalToStringData() + "}";
    }

    protected String additionalToStringData() {
        return "";
    }

    @Override
    public void lock() {
        for ( Metric metric : getGraph().getMetrics() ) {
            if ( !distanceMap.containsKey( metric ) ) {
                throw new IllegalStateException( "Unable to lock edge{" + getId() + "}: metrics not filled: missing metric: " + metric.name() );
            }
        }
        this.locked = true;
    }

    private void checkLock() {
        if ( locked ) {
            throw new IllegalStateException( "This object is locked against modification." );
        }
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
        return this.id == other.id;
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

    @Override
    public void setSource(N s, int idx) {
        this.source = s;
        this.sourceIndex = idx;
    }

    @Override
    public void setTarget(N t, int idx) {
        this.target = t;
        this.targetIndex = idx;
    }
}

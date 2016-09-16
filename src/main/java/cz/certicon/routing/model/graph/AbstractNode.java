/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.graph.iterator.IncomingEdgeIterator;
import cz.certicon.routing.model.graph.iterator.OutgoingEdgeIterator;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.StringUtils;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import cz.certicon.routing.utils.collections.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <N> node type
 * @param <E> edge type
 */
public abstract class AbstractNode<N extends Node, E extends Edge> implements Node<N, E> {

    private final long id;
    private TurnTable turnTable;
    private Coordinate coordinate;
    private final ArrayList<E> edges = new ArrayList<>();
    private final Graph<N, E> graph;
    private boolean locked = false;

    public AbstractNode( Graph<N, E> graph, long id ) {
        this.graph = graph;
        this.id = id;
    }

    @Override
    public Distance getTurnDistance( E source, E target ) {
        return source.getTurnDistance( this, turnTable, target );
    }

    @Override
    public Iterator<E> getIncomingEdges() {
        return new IncomingEdgeIterator<>( this, edges );
    }

    @Override
    public Iterator<E> getOutgoingEdges() {
        return new OutgoingEdgeIterator<>( this, edges );
    }

    @Override
    public Iterator<E> getEdges() {
        return new ImmutableIterator<>( edges.iterator() );
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public int getDegree() {
        return edges.size();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void addEdge( E edge ) {
        checkLock();
        edges.add( edge );
    }

    @Override
    public void removeEdge( E edge ) {
        checkLock();
        edges.remove( edge );
    }

    @Override
    public void setTurnTable( TurnTable turnTable ) {
        checkLock();
        this.turnTable = turnTable;
    }

    @Override
    public void setCoordinate( Coordinate coordinate ) {
        checkLock();
        this.coordinate = coordinate;
    }

    @Override
    public void lock() {
        this.locked = true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (int) ( this.id ^ ( this.id >>> 32 ) );
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
        final AbstractNode<?, ?> other = (AbstractNode<?, ?>) obj;
        if ( this.id != other.id ) {
            return false;
        }
        return true;
    }

    private void checkLock() {
        if ( locked ) {
            throw new IllegalStateException( "This object is locked against modification." );
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        List<E> edgeList = new ArrayList<>( edges );
        Collections.sort( edgeList, Identifiable.Comparators.createIdComparator() );
        for ( E e : edgeList ) {
            sb.append( e.getId() ).append( "," );
        }
        StringUtils.replaceLast( sb, !edgeList.isEmpty(), "]" );
        return getClass().getSimpleName() +"{" + "id=" + id + ", turnTable=" + turnTable + ", coordinate=" + coordinate + ", edges=" + sb.toString() + '}';
    }

    protected Graph<N, E> getGraph() {
        return graph;
    }
}

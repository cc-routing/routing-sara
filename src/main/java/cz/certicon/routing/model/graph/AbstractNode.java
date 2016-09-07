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
 * @param <E>
 */
public abstract class AbstractNode<E extends Edge> implements Node<E> {

    private final long id;
    private TurnTable turnTable;
    private Coordinate coordinate;
    private final ArrayList<E> edges = new ArrayList<>();
    private boolean locked = false;

    public AbstractNode( long id ) {
        this.id = id;
    }

    @Override
    public <N extends Node> Distance getTurnDistance( Graph<N, E> graph, E source, E target ) {
        return source.getTurnDistance( graph, turnTable, target );
    }

    @Override
    public <N extends Node> Iterator<E> getIncomingEdges( Graph<N, E> graph ) {
        return new IncomingEdgeIterator<>( graph, (N) this, edges );
    }

    @Override
    public <N extends Node> Iterator<E> getOutgoingEdges( Graph<N, E> graph ) {
        return new OutgoingEdgeIterator<>( graph, (N) this, edges );
    }

    @Override
    public <N extends Node> Iterator<E> getEdges( Graph<N, E> graph ) {
        return new ImmutableIterator<>( edges.iterator() );
    }

    @Override
    public <N extends Node> Coordinate getCoordinate( Graph<N, E> graph ) {
        return coordinate;
    }

    @Override
    public <N extends Node> int getDegree( Graph<N, E> graph ) {
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
        final AbstractNode<?> other = (AbstractNode<?>) obj;
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
        return "AbstractNode{" + "id=" + id + ", turnTable=" + turnTable + ", coordinate=" + coordinate + ", edges=" + sb.toString() + '}';
    }
}

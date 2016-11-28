/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphUtils;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import cz.certicon.routing.utils.collections.Iterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Skeletal implementation of the {@link Graph} interface implementing an undirected graph.
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public abstract class AbstractUndirectedGraph<N extends Node<N, E>, E extends Edge<N, E>> implements Graph<N, E> {

    private final TLongObjectMap<N> nodes;
    private final TLongObjectMap<E> edges;
    private final EnumSet<Metric> metrics;
    private boolean locked = false;
//    @Getter( AccessLevel.NONE )
//    Map<Node, Coordinate> coordinates;

    public AbstractUndirectedGraph() {
        this.nodes = new TLongObjectHashMap<>();
        this.edges = new TLongObjectHashMap<>();
        this.metrics = EnumSet.noneOf( Metric.class );
    }

    public AbstractUndirectedGraph( Collection<Metric> metrics ) {
        this.nodes = new TLongObjectHashMap<>();
        this.edges = new TLongObjectHashMap<>();
        this.metrics = EnumSet.copyOf( metrics );
    }

    @Override
    public int getNodesCount() {
        return nodes.size();
    }

    @Override
    public Iterator<N> getNodes() {
        return new ImmutableIterator<>( nodes.valueCollection().iterator() );
    }

    @Override
    public int getEdgeCount() {
        return edges.size();
    }

    @Override
    public Iterator<E> getEdges() {
        return new ImmutableIterator<>( edges.valueCollection().iterator() );
    }

    //    @Override
//    public Graph copy() {
//        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public N getNodeById( long id ) {
        return nodes.get( id );
    }

    @Override
    public E getEdgeById( long id ) {
        return edges.get( id );
    }

    @Override
    public boolean containsNode( long id ) {
        return nodes.containsKey( id );
    }

    @Override
    public boolean containsEdge( long id ) {
        return edges.containsKey( id );
    }

    @Override
    public void removeNode( N node ) {
        checkLock();
        List<E> removeEdges = new ArrayList<>();
        for ( E edge : node.getEdges() ) {
            removeEdges.add( edge );
        }
        for ( E removeEdge : removeEdges ) {
            removeEdge( removeEdge );
        }
        nodes.remove( node.getId() );
    }

    @Override
    public void removeEdge( E edge ) {
        checkLock();
        edges.remove( edge.getId() );
        edge.getSource().removeEdge( edge );
        edge.getTarget().removeEdge( edge );
    }

    @Override
    public void lock() {
        for ( N node : getNodes() ) {
            node.lock();
        }
        for ( E edge : getEdges() ) {
            edge.lock();
        }
        this.locked = true;
    }

    @Override
    public Set<Metric> getMetrics() {
        return metrics.clone();
    }

    @Override
    public boolean hasMetric( Metric metric ) {
        return metrics.contains( metric );
    }

    @Override
    public Graph<N, E> copy() {
        AbstractUndirectedGraph<N, E> instance = (AbstractUndirectedGraph<N, E>) newInstance( metrics );
        for ( N node : getNodes() ) {
            N newNode = node.copy( instance );
            instance.nodes.put( newNode.getId(), newNode );
        }
        for ( E edge : getEdges() ) {
            E newEdge = edge.copy( instance, instance.getNodeById( edge.getSource().getId() ), instance.getNodeById( edge.getTarget().getId() ) );
            instance.edges.put( newEdge.getId(), newEdge );
            for ( Metric metric : metrics ) {
                newEdge.setLength( metric, edge.getLength( metric ) );
            }
        }
        return instance;
    }

    @Override
    abstract public Graph<N, E> newInstance( Set<Metric> metrics );

    protected void addNode( N node ) {
        checkLock();
        nodes.put( node.getId(), node );
    }

    protected void addEdge( E edge ) {
        checkLock();
        edge.getSource().addEdge( edge );
        edge.getTarget().addEdge( edge );
        edges.put( edge.getId(), edge );
    }

    protected Collection<N> getNodeCollection() {
        return nodes.valueCollection();
    }

    protected Collection<E> getEdgeCollection() {
        return edges.valueCollection();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getClass().getSimpleName() ).append( "{" ).append( "nodes=[" );
        for ( N node : getNodes() ) {
            sb.append( node.getId() ).append( "," );
        }
        if ( !nodes.isEmpty() ) {
            sb.replace( sb.length() - 1, sb.length(), "]" );
        } else {
            sb.append( "]" );
        }
        sb.append( ",edges=[" );
        for ( E edge : getEdges() ) {
            sb.append( edge.getId() ).append( "," );
        }
        if ( !edges.isEmpty() ) {
            sb.replace( sb.length() - 1, sb.length(), "]" );
        } else {
            sb.append( "]" );
        }
        sb.append( ",mapping={" );
        for ( N node : getNodes() ) {
            sb.append( node.getId() ).append( "=>[" );
            for ( E e : node.getEdges() ) {
                sb.append( e.getSource().equals( node ) ? "+" : "-" ).append( e.getId() ).append( "," );
            }
            if ( node.getEdges().hasNext() ) {
                sb.replace( sb.length() - 1, sb.length(), "]" );
            } else {
                sb.append( "]" );
            }
            sb.append( "," );
        }
        if ( !nodes.isEmpty() ) {
            sb.replace( sb.length() - 1, sb.length(), "}" );
        } else {
            sb.append( "}" );
        }
        sb.append( additionalToStringData() );
        sb.append( "}" );
        return sb.toString();
    }

    protected String additionalToStringData() {
        return "";
    }

    private void checkLock() {
        if ( locked ) {
            throw new IllegalStateException( "This object is locked against modification." );
        }
    }
}

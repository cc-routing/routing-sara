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
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <N> node type
 * @param <E> edge type
 */
public class UndirectedGraph<N extends Node, E extends Edge> implements Graph<N, E> {

    private final TLongObjectMap<N> nodes;
    private final TLongObjectMap<E> edges;
    private final Map<Metric, Map<Edge, Distance>> metricMap;
//    @Getter( AccessLevel.NONE )
//    Map<Node, Coordinate> coordinates;

    public UndirectedGraph( Collection<N> nodes, Collection<E> edges, Map<Metric, Map<Edge, Distance>> metricMap ) {
        this.nodes = GraphUtils.toMap( nodes );
        this.edges = GraphUtils.toMap( edges );
        this.metricMap = metricMap;
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

    @Override
    public Iterator<E> getIncomingEdges( N node ) {
        return (Iterator<E>) node.getIncomingEdges( this );
    }

    @Override
    public Iterator<E> getOutgoingEdges( Node node ) {
        return (Iterator<E>) node.getOutgoingEdges( this );
    }

    @Override
    public N getSourceNode( E edge ) {
        return (N) edge.getSource( this );
    }

    @Override
    public N getTargetNode( E edge ) {
        return (N) edge.getTarget( this );
    }

    @Override
    public N getOtherNode( E edge, N node ) {
        return (N) edge.getOtherNode( this, node );
    }

    @Override
    public Distance getTurnCost( N node, E from, E to ) {
        return node.getTurnDistance( this, from, to );
    }

    @Override
    public Iterator<E> getEdges( N node ) {
        return (Iterator<E>) node.getEdges( this );
    }

    @Override
    public Coordinate getNodeCoordinate( N node ) {
//        if ( !nodes.contains( node ) ) {
//            throw new IllegalArgumentException( "Graph does not contain node: " + node );
//        }
        return node.getCoordinate( this );
//        if ( coordinates == null ) {
//            throw new IllegalStateException( "Coordinates not set" );
//        }
//        if ( !coordinates.containsKey( node ) ) {
//            throw new IllegalArgumentException( "Unknown node: " + node );
//        }
//        return coordinates.get( node );
    }

    @Override
    public Distance getLength( Metric metric, E edge ) {
        if ( !metricMap.containsKey( metric ) ) {
            throw new IllegalArgumentException( "Unknown metric: " + metric );
        }
        Map<Edge, Distance> distanceMap = metricMap.get( metric );
        if ( !distanceMap.containsKey( edge ) ) {
            throw new IllegalArgumentException( "Unknown edge: " + edge );
        }
        return distanceMap.get( edge );
    }

    @Override
    public void setLength( Metric metric, E edge, Distance distnace ) {
        if ( !metricMap.containsKey( metric ) ) {
            throw new IllegalArgumentException( "Unknown metric: " + metric );
        }
        Map<Edge, Distance> distanceMap = metricMap.get( metric );
        distanceMap.put( edge, distnace );
    }

//    @Override
//    public Graph copy() {
//        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "UndirectedGraph{" ).append( "nodes=[" );
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
            for ( E e : getEdges( node ) ) {
                sb.append( e.getSource( this ).equals( node ) ? "+" : "-" ).append( e.getId() ).append( "," );
            }
            if ( node.getEdges( this ).hasNext() ) {
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
        sb.append( "}" );
        return sb.toString();
    }

    @Override
    public N getNodeById( long id ) {
        return nodes.get( id );
    }

    @Override
    public E getEdgeById( long id ) {
        return edges.get( id );
    }

    @Override
    public void addNode( N node ) {
        nodes.put( node.getId(), node );
    }

    public void removeNode( N node ) {
        nodes.remove( node.getId() );
    }

    @Override
    public void addEdge( E edge ) {
        edges.put( edge.getId(), edge );
    }

    public void removeEdge( E edge ) {
        edges.remove( edge.getId() );
        for ( Map<Edge, Distance> value : metricMap.values() ) {
            value.remove( edge );
        }
    }

}

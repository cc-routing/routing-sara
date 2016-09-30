/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.Identifiable;
import static cz.certicon.routing.model.Identifiable.Comparators.*;
import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import static cz.certicon.routing.utils.java8.IteratorStreams.*;
import static cz.certicon.routing.utils.java8.Mappers.*;
import java.util.Comparator;
import java.util.Iterator;
import java8.util.function.Function;
import static java8.util.stream.Collectors.*;
import java8.util.stream.StreamSupport;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class ToStringUtils {

    public static <N extends Node, E extends Edge> String toString( Route<N, E> route ) {
        /*
        StringBuilder sb = new StringBuilder();
        sb.append( "Route{source=" ).append( route.getSource().getId() ).append( ",target=" ).append( route.getTarget().getId() ).append( ",edges=[" );
        StreamSupport.
        for ( Edge edge : route.getEdges() ) {
            sb.append( edge.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]}" );
        return sb.toString();
         */
        return "Route{source=" + route.getSource().getId() + ",target=" + route.getTarget().getId() + ",edges=["
                + stream( route.getEdges() ).map( identifiableToString ).collect( joining( "," ) )
                + "]}";
    }

    public static <E extends Edge> String toString( MinimalCut<E> minimalCut ) {
        /*
        List<Edge> sortedEdges = new ArrayList<>( minimalCut.getCutEdges() );
        Collections.sort( sortedEdges, new Comparator<Edge>() {
            @Override
            public int compare( Edge o1, Edge o2 ) {
                return Long.compare( o1.getId(), o2.getId() );
            }
        } );

        StringBuilder sb = new StringBuilder();
        sb.append( "MinimalCut{cut=" ).append( minimalCut.getCutSize() ).append( ",edges=[" );
        for ( Edge edge : sortedEdges ) {
            sb.append( edge.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]}" );
        return sb.toString();
         */
        return "MinimalCut{cut=" + minimalCut.getCutSize() + ",edges=["
                + StreamSupport.stream( minimalCut.getCutEdges() )
                .sorted( createIdComparator() )
                .map( identifiableToString ).collect( joining( "," ) )
                + "]}";
    }

    public static String toString( final ContractNode node ) {
        /*
        StringBuilder sb = new StringBuilder();
        sb.append( node.getClass().getSimpleName() ).append( "{edges={" );
        List<ContractEdge> edges = new ArrayList<>();
        for ( ContractEdge edge : node.getEdges() ) {
            edges.add( edge );
        }
        Collections.sort( edges, new Comparator<ContractEdge>() {
            @Override
            public int compare( ContractEdge o1, ContractEdge o2 ) {
                return Long.compare( o1.getOtherNode( node ).getId(), o2.getOtherNode( node ).getId() );
            }
        } );
        for ( ContractEdge edge : edges ) {
            sb.append( "edge[" ).append( edge.getEdges().size() ).append( "]->node#" ).append( edge.getOtherNode( node ).getId() ).append( "," );
        }
        if ( !edges.isEmpty() ) {
            sb.replace( sb.length() - 1, sb.length(), "" );
        }
        sb.append( "}}" );
        return sb.toString();
         */
        return node.getClass().getSimpleName() + "{edges={"
                + stream( node.getEdges() ).sorted( new Comparator<ContractEdge>() {
            @Override
            public int compare( ContractEdge o1, ContractEdge o2 ) {
                return Long.compare( o1.getOtherNode( node ).getId(), o2.getOtherNode( node ).getId() );
            }
        } ).map( new Function<ContractEdge, String>() {
            @Override
            public String apply( ContractEdge e ) {
                return "edge[" + e.getEdges().size() + "]->node#" + e.getOtherNode( node ).getId();
            }
        } ).collect( joining( "," ) )
                + "}}";
        /*
        return node.getClass().getSimpleName() + "{edges={"
                + IteratorStreams.stream( node.getEdges() )
                .sorted( (e1,e2) -> Long.compare( e1.getOtherNode( node ).getId(), e2.getOtherNode( node ).getId() ) )
                .map( e -> "edge[" + e.getEdges().size() + "]->node#" + e.getOtherNode( node ).getId() )
                .collect( joining( "," ) )
                + "}}";
         */
    }

    public static <I extends Identifiable> String toString( Iterator<I> iterator ) {
        /*
        List<Node> list = CollectionUtils.asList( iterator );
        Collections.sort( list, Identifiable.Comparators.createIdComparator() );
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( Node node : list ) {
            sb.append( node.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
         */
        return "[" + stream( iterator ).sorted( createIdComparator() ).map( identifiableToString ).collect( joining( "," ) ) + "]";
    }
}

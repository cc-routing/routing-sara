/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.model.Identifiable;

import static cz.certicon.routing.model.Identifiable.Comparators.*;

import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.*;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.utils.java8.IteratorStreams;

import static cz.certicon.routing.utils.java8.IteratorStreams.*;

import cz.certicon.routing.utils.java8.Mappers;

import static cz.certicon.routing.utils.java8.Mappers.*;

import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java8.util.J8Arrays;
import java8.util.function.*;
import java8.util.stream.Collectors;

import static java8.util.stream.Collectors.*;

import java8.util.stream.StreamSupport;
import lombok.NonNull;

/**
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class ToStringUtils_Test {

    public static <N extends Node, E extends Edge> String toString( Route<N, E> route ) {
        /*
        StringBuilder sb = new StringBuilder();
        sb.append( "Route{source=" ).append( route.getSource().getId() ).append( ",target=" ).append( route.getTarget().getId() ).append( ",edges=[" );
        StreamSupport.
        for ( Edge edge : route.getEdgeIds() ) {
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
        for ( ContractEdge edge : node.getEdgeIds() ) {
            edges.add( edge );
        }
        Collections.sort( edges, new Comparator<ContractEdge>() {
            @Override
            public int compare( ContractEdge o1, ContractEdge o2 ) {
                return Long.compare( o1.getOtherNode( node ).getId(), o2.getOtherNode( node ).getId() );
            }
        } );
        for ( ContractEdge edge : edges ) {
            sb.append( "edge[" ).append( edge.getEdgeIds().size() ).append( "]->node#" ).append( edge.getOtherNode( node ).getId() ).append( "," );
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
                + IteratorStreams.stream( node.getEdgeIds() )
                .sorted( (e1,e2) -> Long.compare( e1.getOtherNode( node ).getId(), e2.getOtherNode( node ).getId() ) )
                .map( e -> "edge[" + e.getEdgeIds().size() + "]->node#" + e.getOtherNode( node ).getId() )
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

    public static <N extends Node, E extends Edge> String toString( @NonNull Graph<N, E> graph ) {
        String result = graph.getClass().getName() + "{"
                + "nodes=["
                + IteratorStreams.stream( graph.getNodes() )
                .sorted( Identifiable.Comparators.createIdComparator() )
                .map( Mappers.identifiableToString )
                .collect( Collectors.joining( "," ) )
                + "]"
                + ",edges=["
                + IteratorStreams.stream( graph.getEdges() )
                .sorted( Identifiable.Comparators.createIdComparator() )
                .map( new Function<E, String>() {
                    @Override
                    public String apply( E t ) {
                        return t.getId() + "{" + t.getSource().getId() + ( t.isOneWay() ? "" : "<" ) + "->" + t.getTarget().getId() + "}";
                    }
                } ).collect( Collectors.joining( "," ) )
                + "]"
                + "}";
        return result;
    }

    public static <G extends Graph<N, E>, N extends Node, E extends Edge> Graph<N, E> fromString( G graph, String string, NodeCreator<G, N, E> nodeCreator, EdgeCreator<G, N, E> edgeCreator ) {
        String rest = string.substring( graph.getClass().getName().length() );
        Matcher nodeMatcher = Pattern.compile( "nodes=\\[[^\\]]*\\]" ).matcher( rest );
        if ( nodeMatcher.find() ) {
            String nodeArray = nodeMatcher.group( 0 );
            for ( String node : nodeArray.substring( "nodes=[".length(), nodeArray.length() - 1 ).split( "," ) ) {
                if ( !node.isEmpty() ) {
                    long id = Long.parseLong( node );
                    nodeCreator.createNode( graph, id );
                }
            }
        }
        Matcher edgeMatcher = Pattern.compile( "edges=\\[[^\\]]*\\]" ).matcher( rest );
        if ( edgeMatcher.find() ) {
            String edgeArray = edgeMatcher.group( 0 );
            for ( String edge : edgeArray.substring( "edges=[".length(), edgeArray.length() - 1 ).split( "," ) ) {
                if ( !edge.isEmpty() ) {
                    long id = Long.parseLong( edge.substring( 0, edge.indexOf( "{" ) ) );
                    String direction = edge.substring( edge.indexOf( "{" ) + 1, edge.length() - 1 );
                    long targetId = Long.parseLong( direction.substring( direction.lastIndexOf( ">" ) + 1 ) );
                    long sourceId;
                    boolean oneway;
                    if ( direction.contains( "<->" ) ) {
                        oneway = false;
                        sourceId = Long.parseLong( direction.substring( 0, direction.indexOf( "<" ) ) );
                    } else {
                        oneway = true;
                        sourceId = Long.parseLong( direction.substring( 0, direction.indexOf( "-" ) ) );
                    }
                    edgeCreator.createEdge( graph, id, oneway, graph.getNodeById( sourceId ), graph.getNodeById( targetId ) );
                }
            }
        }
        return graph;
    }

    public interface NodeCreator<G extends Graph<N, E>, N extends Node, E extends Edge> {

        N createNode( G graph, long id );
    }

    public interface EdgeCreator<G extends Graph<N, E>, N extends Node, E extends Edge> {

        E createEdge( G graph, long id, boolean oneway, N source, N target );
    }

    public static class UndirectedNodeCreator implements ToStringUtils_Test.NodeCreator<UndirectedGraph, SimpleNode, SimpleEdge> {

        @Override
        public SimpleNode createNode( UndirectedGraph graph, long id ) {
            return graph.createNode( id );
        }

    }

    public static class UndirectedEdgeCreator implements ToStringUtils_Test.EdgeCreator<UndirectedGraph, SimpleNode, SimpleEdge> {

        @Override
        public SimpleEdge createEdge( UndirectedGraph graph, long id, boolean oneway, SimpleNode source, SimpleNode target ) {
            return graph.createEdge( id, oneway, source, target, 0, 0 );
        }

    }


    public static String toString( final OptimizedGraph graph ) {
        return "{nodes=[" +
                J8Arrays.stream( graph.getNodeIds() )
                        .sorted()
                        .mapToObj( new LongFunction<String>() {
                            @Override
                            public String apply( long value ) {
                                return Long.toString( value );
                            }
                        } ).collect( joining( "," ) ) +
                "]" +
                ",edges=[" +
                J8Arrays.stream( graph.getEdgeIds() )
                        .sorted()
                        .mapToObj( new LongFunction<String>() {
                            @Override
                            public String apply( final long value ) {
                                final int e = graph.getEdgeById( value );
                                return value + "{" +
                                        graph.getNodeId( graph.getSource( e ) ) +
                                        ( graph.isOneway( e ) ? "" : "<" ) + "->" +
                                        graph.getNodeId( graph.getTarget( e ) ) +
                                        ";" +
                                        J8Arrays.stream( Metric.values() ).mapToDouble( new ToDoubleFunction<Metric>() {
                                            @Override
                                            public double applyAsDouble( Metric metric ) {
                                                return graph.getLength( e, metric );
                                            }
                                        } ).filter( new DoublePredicate() {
                                            @Override
                                            public boolean test( double len ) {
                                                return len != 0;
                                            }
                                        } ).mapToObj( new DoubleFunction<String>() {
                                            @Override
                                            public String apply( double len ) {
                                                return len + "";
                                            }
                                        } ).collect( Collectors.joining( ":" ) ) +
                                        "" +
                                        "}";
                            }
                        } ).collect( joining( "," ) ) +
                "]}";
    }

    /**
     * @param string
     * @return
     */
    public static OptimizedGraph optimizedGraphFromString( String string ) {
        OptimizedGraph graph = new OptimizedGraph();
        Matcher nodeMatcher = Pattern.compile( "nodes=\\[[^\\]]*\\]" ).matcher( string );
        if ( nodeMatcher.find() ) {
            String nodeArray = nodeMatcher.group( 0 );
            String[] split = nodeArray.substring( "nodes=[".length(), nodeArray.length() - 1 ).split( "," );
            graph.enlargeNodeCapacityBy( split.length );
            for ( String node : split ) {
                if ( !node.isEmpty() ) {
                    long id = Long.parseLong( node );
                    graph.createNode( id );
                }
            }
        }
        Matcher edgeMatcher = Pattern.compile( "edges=\\[[^\\]]*\\]" ).matcher( string );
        if ( edgeMatcher.find() ) {
            String edgeArray = edgeMatcher.group( 0 );
            String[] split = edgeArray.substring( "edges=[".length(), edgeArray.length() - 1 ).split( "," );
            graph.enlargeEdgeCapacityBy( split.length );
            for ( String edge : split ) {
                if ( !edge.isEmpty() ) {
                    long id = Long.parseLong( edge.substring( 0, edge.indexOf( "{" ) ) );
                    String details = edge.substring( edge.indexOf( "{" ) + 1, edge.length() - 1 );
                    String[] detailsArray = details.split( ";" );
                    // direction
                    String direction = detailsArray[0];
                    long targetId = Long.parseLong( direction.substring( direction.lastIndexOf( ">" ) + 1 ) );
                    boolean oneway = !direction.contains( "<->" );
                    long sourceId = Long.parseLong( direction.substring( 0, direction.indexOf( oneway ? "-" : "<" ) ) );
                    int idx = graph.createEdge( id, sourceId, targetId, oneway, 0, 0 ); // FIXME 0,0
                    // lengths
                    String[] lengths = detailsArray[1].split( ":" );
                    for ( int i = 0; i < lengths.length; i++ ) {
                        graph.setLength( idx, Metric.values()[i], Float.parseFloat( lengths[i] ) );
                    }
                }
            }
        }
        return graph;
    }
}

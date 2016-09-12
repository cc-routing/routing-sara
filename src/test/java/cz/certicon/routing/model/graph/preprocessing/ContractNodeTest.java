/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ContractNodeTest {

    public ContractNodeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of mergeWith method, of class ContractNode.
     */
    @Test
    public void testMergeWith() {
        System.out.println( "mergeWith" );
        List<ContractEdge> edges = new ArrayList<>();
        Set<Node> origNodesA = new HashSet<Node>( Arrays.asList( new SimpleNode( -1 ), new SimpleNode( -2 ), new SimpleNode( -3 ) ) );
        Set<Node> origNodesB = new HashSet<Node>( Arrays.asList( new SimpleNode( -4 ), new SimpleNode( -5 ) ) );
        Set<Node> origNodes = new HashSet<Node>( Arrays.asList( new SimpleNode( -6 ) ) );
        ContractNode nodeA = new ContractNode( 0, origNodesA );
        ContractNode nodeB = new ContractNode( 1, origNodesB );
        Set<Edge> origEdges = new HashSet<Edge>( Arrays.asList( new SimpleEdge( -1, false, (SimpleNode) origNodesA.iterator().next(), (SimpleNode) origNodesB.iterator().next(), -1, -1 ) ) );
        ContractEdge connectEdge = new ContractEdge( 0, false, nodeA, nodeB, origEdges );
        nodeA.addEdge( connectEdge );
        nodeB.addEdge( connectEdge );
        ContractNode[] neighbors = new ContractNode[5];
        for ( int i = 0; i < neighbors.length; i++ ) {
            neighbors[i] = new ContractNode( 2 + i, origNodes );
        }
        Map<Metric, Map<Edge, Distance>> metricMap = new EnumMap<>( Metric.class );
        for ( Metric value : Metric.values() ) {
            metricMap.put( value, new HashMap<Edge, Distance>() );
        }
        for ( int i = 0; i < 3; i++ ) {
            ContractNode neighbor = neighbors[i];
            ContractEdge edge = new ContractEdge( i + 1, false, nodeA, neighbor, origEdges );
            nodeA.addEdge( edge );
            neighbor.addEdge( edge );
            edges.add( edge );
            metricMap.get( Metric.SIZE ).put( edge, Distance.newInstance( 1 ) );
        }
        for ( int i = 2; i < 5; i++ ) {
            ContractNode neighbor = neighbors[i];
            ContractEdge edge = new ContractEdge( i + 2, false, nodeB, neighbor, origEdges );
            nodeB.addEdge( edge );
            neighbor.addEdge( edge );
            edges.add( edge );
            metricMap.get( Metric.SIZE ).put( edge, Distance.newInstance( 1 ) );
        }
        ContractNode.MaxIdContainer nodeMaxIdContainer = new ContractNode.MaxIdContainer( 9 );
        ContractNode.MaxIdContainer edgeMaxIdContainer = new ContractNode.MaxIdContainer( 9 );

        Graph<ContractNode, ContractEdge> graph = new UndirectedGraph<>( GraphUtils.toMap( Arrays.asList( nodeA, nodeB ) ), GraphUtils.toMap( edges ), metricMap );

//        System.out.println( "nodes:" );
//        System.out.println( nodeA );
//        System.out.println( nodeB );
//        for ( ContractNode neighbor : neighbors ) {
//            System.out.println( neighbor );
//        }
//        System.out.println( "edges" );
//        System.out.println( connectEdge );
//        for ( ContractEdge edge : edges ) {
//            System.out.println( edge );
//        }
        String expResult = "ContractNode{edges={edge[1]->node#2,edge[1]->node#3,edge[1]->node#4,edge[1]->node#5,edge[1]->node#6}}"; // edge[1]->4 because it is a set and the sets are all the same
        ContractNode result = nodeA.mergeWith( graph, nodeB, nodeMaxIdContainer, edgeMaxIdContainer );
//        System.out.println( result.toString() );
//        System.out.println( toString( result ) );
        assertEquals( expResult, toString( graph, result ) );
    }

    /**
     * Test of getNodes method, of class ContractNode.
     */
    @Test
    public void testGetNodes() {
        System.out.println( "getNodes" );
        Set<Node> origNodes = new HashSet<Node>( Arrays.asList( new SimpleNode( -1 ), new SimpleNode( -2 ), new SimpleNode( -3 ) ) );
        ContractNode instance = new ContractNode( 0, origNodes );
        Collection<Node> expResult = new HashSet<>( origNodes );
        origNodes.clear();
        Collection<Node> result = instance.getNodes();
        assertEquals( expResult, result );
    }

    private String toString( final Graph<ContractNode, ContractEdge> graph, final ContractNode node ) {
        StringBuilder sb = new StringBuilder();
        sb.append( node.getClass().getSimpleName() ).append( "{edges={" );
        List<ContractEdge> edges = new ArrayList<>();
        for ( ContractEdge edge : graph.getEdges( node ) ) {
            edges.add( edge );
        }
        Collections.sort( edges, new EdgeComparator<>( graph, node ) );
        for ( ContractEdge edge : edges ) {
            sb.append( "edge[" ).append( edge.getEdges().size() ).append( "]->node#" ).append( graph.getOtherNode( edge, node ).getId() ).append( "," );
        }
        if ( !edges.isEmpty() ) {
            sb.replace( sb.length() - 1, sb.length(), "" );
        }
        sb.append( "}}" );
        return sb.toString();
    }

    private static class EdgeComparator<N extends Node, E extends Edge> implements Comparator<E> {

        private final Graph<N, E> graph;
        private final N node;

        public EdgeComparator( Graph<N, E> graph, N node ) {
            this.graph = graph;
            this.node = node;
        }

        @Override
        public int compare( E o1, E o2 ) {
            return Long.compare( graph.getOtherNode( o1, node ).getId(), graph.getOtherNode( o2, node ).getId() );
        }

    }
}

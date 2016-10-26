/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphGeneratorUtils;
import cz.certicon.routing.utils.GraphUtils;
import cz.certicon.routing.utils.RandomUtils;
import cz.certicon.routing.utils.ToStringUtils_Test;
import cz.certicon.routing.utils.java8.Mappers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
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
public class FordFulkersonMinimalCutTest {

    private final Graph<Node, Edge> graph;
    private final Map<Long, Node> nodeMap;
    private final Map<Long, Edge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public FordFulkersonMinimalCutTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = createGraph();
    }

    private Graph<Node, Edge> createGraph() {
        return GraphGeneratorUtils.createGraph( EnumSet.of( Metric.LENGTH, Metric.SIZE ), nodeMap, edgeMap, turnTables );
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        RandomUtils.setSeed( 1 );
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compute method, of class FordFulkersonMinimalCut.
     */
    @Test
    public void testCompute() {
        System.out.println( "compute" );
        FordFulkersonMinimalCut instance = new FordFulkersonMinimalCut();
        {
            System.out.println( " - test case #1" );
            Node sourceNode = nodeMap.get( 0L );
            Node targetNode = nodeMap.get( 5L );
            MinimalCut<Edge> expected = new MinimalCut<>( Arrays.asList( edgeMap.get( 4L ), edgeMap.get( 5L ) ), 74 );
            MinimalCut<Edge> result = instance.compute( graph, Metric.LENGTH, sourceNode, targetNode );
            assertEquals(ToStringUtils_Test.toString( expected ), ToStringUtils_Test.toString( result ) );
        }

        {
            System.out.println( " - test case #2" );
            UndirectedGraph g = new UndirectedGraph( EnumSet.of( Metric.LENGTH, Metric.SIZE ) );
            SimpleNode[] nodes = new SimpleNode[8];
            for ( int i = 0; i < nodes.length; i++ ) {
                nodes[i] = g.createNode( i );
            }
            SimpleEdge[] edges = new SimpleEdge[13];
            edges[0] = createEdge( g, 0, 0, 1, 6, nodes );
            edges[1] = createEdge( g, 1, 0, 2, 2, nodes );
            edges[2] = createEdge( g, 2, 0, 3, 2, nodes );
            edges[3] = createEdge( g, 3, 0, 4, 2, nodes );
            edges[4] = createEdge( g, 4, 0, 5, 2, nodes );
            edges[5] = createEdge( g, 5, 0, 7, 2, nodes );
            edges[6] = createEdge( g, 6, 1, 3, 4, nodes );
            edges[7] = createEdge( g, 7, 1, 5, 2, nodes );
            edges[8] = createEdge( g, 8, 2, 4, 2, nodes );
            edges[9] = createEdge( g, 9, 3, 5, 2, nodes );
            edges[10] = createEdge( g, 10, 3, 7, 2, nodes );
            edges[11] = createEdge( g, 11, 4, 6, 2, nodes );
            edges[12] = createEdge( g, 12, 6, 7, 2, nodes );
            MinimalCut<Edge> expected1 = new MinimalCut<>( Arrays.<Edge>asList( edges[0], edges[6], edges[7] ), 12 );
            MinimalCut<Edge> expected2 = new MinimalCut<>( Arrays.<Edge>asList( edges[0], edges[2], edges[4], edges[10] ), 12 );
            MinimalCut<Edge> result = instance.compute( g, Metric.SIZE, nodes[0], nodes[1] );
            if ( !ToStringUtils_Test.toString( result ).equals(ToStringUtils_Test.toString( expected1 ) ) ) {
                assertEquals(ToStringUtils_Test.toString( expected2 ), ToStringUtils_Test.toString( result ) );
            }
        }
        {
            System.out.println( " - test case #3" );
            UndirectedGraph g = new UndirectedGraph( EnumSet.of( Metric.LENGTH, Metric.SIZE ) );
            SimpleNode[] nodes = new SimpleNode[8];
            for ( int i = 0; i < nodes.length; i++ ) {
                nodes[i] = g.createNode( i );
            }
            Edge[] edges = new SimpleEdge[13];
            edges[0] = createEdge( g, 0, 0, 1, 4, nodes );
            edges[1] = createEdge( g, 1, 0, 3, 6, nodes );
            edges[2] = createEdge( g, 2, 0, 4, 6, nodes );
            edges[3] = createEdge( g, 3, 0, 5, 6, nodes );
            edges[4] = createEdge( g, 4, 0, 7, 4, nodes );
            edges[5] = createEdge( g, 5, 1, 2, 2, nodes );
            edges[6] = createEdge( g, 6, 1, 4, 2, nodes );
            edges[7] = createEdge( g, 7, 1, 5, 2, nodes );
            edges[8] = createEdge( g, 8, 1, 6, 2, nodes );
            edges[9] = createEdge( g, 9, 1, 7, 2, nodes );
            edges[10] = createEdge( g, 10, 2, 3, 2, nodes );
            edges[11] = createEdge( g, 11, 2, 4, 2, nodes );
            edges[12] = createEdge( g, 12, 6, 7, 2, nodes );
            MinimalCut<Edge> expected1 = new MinimalCut<>( Arrays.asList( edges[0], edges[5], edges[6], edges[7], edges[4] ), 14 );// 4 or 8+9 or 9+12
            MinimalCut<Edge> expected2 = new MinimalCut<>( Arrays.asList( edges[0], edges[5], edges[6], edges[7], edges[8], edges[9] ), 14 );// 4 or 8+9 or 9+12
            MinimalCut<Edge> expected3 = new MinimalCut<>( Arrays.asList( edges[0], edges[5], edges[6], edges[7], edges[9], edges[12] ), 14 );// 4 or 8+9 or 9+12
            MinimalCut<Edge> result = instance.compute( g, Metric.SIZE, nodes[0], nodes[1] );
            if ( !ToStringUtils_Test.toString( result ).equals(ToStringUtils_Test.toString( expected1 ) ) ) {
                if ( !ToStringUtils_Test.toString( result ).equals(ToStringUtils_Test.toString( expected2 ) ) ) {
                    assertEquals(ToStringUtils_Test.toString( expected3 ), ToStringUtils_Test.toString( result ) );
                }
            }
        }
    }

    private SimpleEdge createEdge( UndirectedGraph g, int id, int source, int target, int length, Node[] nodes ) {
        SimpleEdge edge = g.createEdge( id, false, (SimpleNode) nodes[source], (SimpleNode) nodes[target], -1, -1, new Pair<>( Metric.LENGTH, Distance.newInstance( length ) ), new Pair<>( Metric.SIZE, Distance.newInstance( length ) ) );
        return edge;
    }

}

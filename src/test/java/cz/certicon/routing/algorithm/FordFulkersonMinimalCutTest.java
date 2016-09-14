/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.MinimalCut;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return GraphGeneratorUtils.createGraph( nodeMap, edgeMap, turnTables );
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
            assertEquals( toString( expected ), toString( result ) );
        }

        {
            System.out.println( " - test case #2" );
            Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
            Map<Edge, Distance> distanceMap = new HashMap<>();
            metricMap.put( Metric.SIZE, distanceMap );
            Node[] nodes = new SimpleNode[8];
            for ( int i = 0; i < nodes.length; i++ ) {
                nodes[i] = new SimpleNode( i );
            }
            Edge[] edges = new SimpleEdge[13];
            edges[0] = createEdge( 0, 0, 1, 6, nodes, distanceMap );
            edges[1] = createEdge( 1, 0, 2, 2, nodes, distanceMap );
            edges[2] = createEdge( 2, 0, 3, 2, nodes, distanceMap );
            edges[3] = createEdge( 3, 0, 4, 2, nodes, distanceMap );
            edges[4] = createEdge( 4, 0, 5, 2, nodes, distanceMap );
            edges[5] = createEdge( 5, 0, 7, 2, nodes, distanceMap );
            edges[6] = createEdge( 6, 1, 3, 4, nodes, distanceMap );
            edges[7] = createEdge( 7, 1, 5, 2, nodes, distanceMap );
            edges[8] = createEdge( 8, 2, 4, 2, nodes, distanceMap );
            edges[9] = createEdge( 9, 3, 5, 2, nodes, distanceMap );
            edges[10] = createEdge( 10, 3, 7, 2, nodes, distanceMap );
            edges[11] = createEdge( 11, 4, 6, 2, nodes, distanceMap );
            edges[12] = createEdge( 12, 6, 7, 2, nodes, distanceMap );
            UndirectedGraph<Node, Edge> g = new UndirectedGraph<>( Arrays.asList( nodes ), Arrays.asList( edges ), metricMap );
            MinimalCut<Edge> expected1 = new MinimalCut<>( Arrays.asList( edges[0], edges[6], edges[7] ), 12 );
            MinimalCut<Edge> expected2 = new MinimalCut<>( Arrays.asList( edges[0], edges[2], edges[4], edges[10] ), 12 );
            MinimalCut<Edge> result = instance.compute( g, Metric.SIZE, nodes[0], nodes[1] );
            if ( !toString( result ).equals( toString( expected1 ) ) ) {
                assertEquals( toString( expected2 ), toString( result ) );
            }
        }
        {
            System.out.println( " - test case #3" );
            Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
            Map<Edge, Distance> distanceMap = new HashMap<>();
            metricMap.put( Metric.SIZE, distanceMap );
            Node[] nodes = new SimpleNode[8];
            for ( int i = 0; i < nodes.length; i++ ) {
                nodes[i] = new SimpleNode( i );
            }
            Edge[] edges = new SimpleEdge[13];
            edges[0] = createEdge( 0, 0, 1, 4, nodes, distanceMap );
            edges[1] = createEdge( 1, 0, 3, 6, nodes, distanceMap );
            edges[2] = createEdge( 2, 0, 4, 6, nodes, distanceMap );
            edges[3] = createEdge( 3, 0, 5, 6, nodes, distanceMap );
            edges[4] = createEdge( 4, 0, 7, 4, nodes, distanceMap );
            edges[5] = createEdge( 5, 1, 2, 2, nodes, distanceMap );
            edges[6] = createEdge( 6, 1, 4, 2, nodes, distanceMap );
            edges[7] = createEdge( 7, 1, 5, 2, nodes, distanceMap );
            edges[8] = createEdge( 8, 1, 6, 2, nodes, distanceMap );
            edges[9] = createEdge( 9, 1, 7, 2, nodes, distanceMap );
            edges[10] = createEdge( 10, 2, 3, 2, nodes, distanceMap );
            edges[11] = createEdge( 11, 2, 4, 2, nodes, distanceMap );
            edges[12] = createEdge( 12, 6, 7, 2, nodes, distanceMap );
            UndirectedGraph<Node, Edge> g = new UndirectedGraph<>( Arrays.asList( nodes ), Arrays.asList( edges ), metricMap );
            MinimalCut<Edge> expected1 = new MinimalCut<>( Arrays.asList( edges[0], edges[5], edges[6], edges[7], edges[4] ), 14 );// 4 or 8+9 or 9+12
            MinimalCut<Edge> expected2 = new MinimalCut<>( Arrays.asList( edges[0], edges[5], edges[6], edges[7], edges[8], edges[9] ), 14 );// 4 or 8+9 or 9+12
            MinimalCut<Edge> expected3 = new MinimalCut<>( Arrays.asList( edges[0], edges[5], edges[6], edges[7], edges[9], edges[12] ), 14 );// 4 or 8+9 or 9+12
            MinimalCut<Edge> result = instance.compute( g, Metric.SIZE, nodes[0], nodes[1] );
            if ( !toString( result ).equals( toString( expected1 ) ) ) {
                if ( !toString( result ).equals( toString( expected2 ) ) ) {
                    assertEquals( toString( expected3 ), toString( result ) );
                }
            }
        }
    }

    private SimpleEdge createEdge( int id, int source, int target, int length, Node[] nodes, Map<Edge, Distance> map ) {
        SimpleEdge edge = new SimpleEdge( id, false, (SimpleNode) nodes[source], (SimpleNode) nodes[target], 0, 0 );
        map.put( edge, Distance.newInstance( length ) );
        nodes[source].addEdge( edge );
        nodes[target].addEdge( edge );
        return edge;
    }

    public String toString( MinimalCut<Edge> minimalCut ) {
//        System.out.println( "cut edges:" );
//        for ( Edge cutEdge : minimalCut.getCutEdges()) {
//            System.out.println( cutEdge );
//        }

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
    }

}

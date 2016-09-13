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
        Node sourceNode = nodeMap.get( 0L );
        Node targetNode = nodeMap.get( 5L );
        FordFulkersonMinimalCut instance = new FordFulkersonMinimalCut();
        MinimalCut<Edge> expected = new MinimalCut<>( Arrays.asList( edgeMap.get( 4L ), edgeMap.get( 5L ) ), 74 );
        MinimalCut<Edge> result = instance.compute( graph, Metric.LENGTH, sourceNode, targetNode );
        assertEquals( toString( expected ), toString( result ) );

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
        UndirectedGraph<Node, Edge> g = new UndirectedGraph<>( GraphUtils.toMap( Arrays.asList( nodes ) ), GraphUtils.toMap( Arrays.asList( edges ) ), metricMap );
        MinimalCut<Edge> e = new MinimalCut<>( Arrays.asList( edges[0], edges[6], edges[7] ), 12 );
        MinimalCut<Edge> r = instance.compute( g, Metric.SIZE, nodes[0], nodes[1] );
        assertEquals( toString( e ), toString( r ) );

//        edges[1] = new SimpleEdge( 1, false, nodes[0], nodes[2], 0, 0 );
//        edges[2] = new SimpleEdge( 2, false, nodes[0], nodes[3], 0, 0 );
//        edges[3] = new SimpleEdge( 3, false, nodes[0], nodes[4], 0, 0 );
//        edges[4] = new SimpleEdge( 4, false, nodes[0], nodes[5], 0, 0 );
//        edges[5] = new SimpleEdge( 5, false, nodes[0], nodes[7], 0, 0 );
//        edges[6] = new SimpleEdge( 6, false, nodes[1], nodes[2], 0, 0 );
//        edges[7] = new SimpleEdge( 7, false, nodes[1], nodes[3], 0, 0 );
//        edges[8] = new SimpleEdge( 8, false, nodes[1], nodes[5], 0, 0 );
//        edges[9] = new SimpleEdge( 9, false, nodes[2], nodes[4], 0, 0 );
//        edges[10] = new SimpleEdge( 10, false, nodes[3], nodes[5], 0, 0 );
//        edges[11] = new SimpleEdge( 11, false, nodes[3], nodes[7], 0, 0 );
//        edges[12] = new SimpleEdge( 12, false, nodes[4], nodes[6], 0, 0 );
//        edges[13] = new SimpleEdge( 13, false, nodes[6], nodes[7], 0, 0 );
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

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

    private final Graph<Node,Edge> graph;
    private final Map<Long, Node> nodeMap;
    private final Map<Long, Edge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public FordFulkersonMinimalCutTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = createGraph();
    }

    private Graph<Node,Edge> createGraph() {
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
        MinimalCut expected = new MinimalCut( Arrays.asList( edgeMap.get( 3L ), edgeMap.get( 8L ), edgeMap.get( 9L ) ), 23 );
        MinimalCut result = instance.compute( graph, Metric.SIZE, sourceNode, targetNode );
        assertEquals( toString( expected ), toString( result ) );
    }

    public String toString( MinimalCut minimalCut ) {
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

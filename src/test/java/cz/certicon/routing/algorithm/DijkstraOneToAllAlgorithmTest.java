/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.utils.GraphGeneratorUtils;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class DijkstraOneToAllAlgorithmTest {

    private final Graph<Node, Edge> graph;
    private final Map<Long, Node> nodeMap;
    private final Map<Long, Edge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public DijkstraOneToAllAlgorithmTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = createGraph();
    }

    private Graph<Node, Edge> createGraph() {
        return GraphGeneratorUtils.createGraph( EnumSet.of( Metric.LENGTH ), nodeMap, edgeMap, turnTables );
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
     * Test of route method, of class DijkstraOneToAllAlgorithm.
     */
    @Test
    public void testRoute() {
        System.out.println( "route" );
        DijkstraOneToAllAlgorithm instance = new DijkstraOneToAllAlgorithm();
        Edge sourceEdge = edgeMap.get( 3L );
        OneToAllRoutingAlgorithm.Direction sourceDirection = OneToAllRoutingAlgorithm.Direction.FORWARD;
        Map<Edge, OneToAllRoutingAlgorithm.Direction> targetMap = new HashMap<>();
        targetMap.put( edgeMap.get( 0L ), OneToAllRoutingAlgorithm.Direction.FORWARD );
        targetMap.put( edgeMap.get( 1L ), OneToAllRoutingAlgorithm.Direction.FORWARD );
        targetMap.put( edgeMap.get( 2L ), OneToAllRoutingAlgorithm.Direction.BACKWARD );
        targetMap.put( edgeMap.get( 4L ), OneToAllRoutingAlgorithm.Direction.FORWARD );
        targetMap.put( edgeMap.get( 5L ), OneToAllRoutingAlgorithm.Direction.BACKWARD );
        targetMap.put( edgeMap.get( 6L ), OneToAllRoutingAlgorithm.Direction.FORWARD );
        Map<Edge, Route<Node, Edge>> result = instance.route( graph, Metric.LENGTH, sourceEdge, sourceDirection, targetMap );

        assertEquals( targetMap.size(), result.size() );
        assertTrue( result.containsKey( edgeMap.get( 0L ) ) );
        assertEquals( toString( Route.builder().addAsLast( edgeMap.get( 3L ) ).addAsLast( edgeMap.get( 4L ) ).addAsLast( edgeMap.get( 6L ) ).addAsLast( edgeMap.get( 5L ) ).addAsLast( edgeMap.get( 2L ) ).addAsLast( edgeMap.get( 1L ) ).addAsLast( edgeMap.get( 0L ) ).build() ), toString( result.get( edgeMap.get( 0L ) ) ) );
        assertTrue( result.containsKey( edgeMap.get( 1L ) ) );
        assertEquals( toString( Route.builder().addAsLast( edgeMap.get( 3L ) ).addAsLast( edgeMap.get( 0L ) ).addAsLast( edgeMap.get( 1L ) ).build() ), toString( result.get( edgeMap.get( 1L ) ) ) );
        assertTrue( result.containsKey( edgeMap.get( 2L ) ) );
        assertEquals( toString( Route.builder().addAsLast( edgeMap.get( 3L ) ).addAsLast( edgeMap.get( 4L ) ).addAsLast( edgeMap.get( 6L ) ).addAsLast( edgeMap.get( 5L ) ).addAsLast( edgeMap.get( 2L ) ).build() ), toString( result.get( edgeMap.get( 2L ) ) ) );
        assertTrue( result.containsKey( edgeMap.get( 4L ) ) );
        assertEquals( toString( Route.builder().addAsLast( edgeMap.get( 3L ) ).addAsLast( edgeMap.get( 4L ) ).build() ), toString( result.get( edgeMap.get( 4L ) ) ) );
        assertTrue( result.containsKey( edgeMap.get( 5L ) ) );
        assertEquals( toString( Route.builder().addAsLast( edgeMap.get( 3L ) ).addAsLast( edgeMap.get( 4L ) ).addAsLast( edgeMap.get( 6L ) ).addAsLast( edgeMap.get( 5L ) ).build() ), toString( result.get( edgeMap.get( 5L ) ) ) );
        assertTrue( result.containsKey( edgeMap.get( 6L ) ) );
        assertEquals( toString( Route.builder().addAsLast( edgeMap.get( 3L ) ).addAsLast( edgeMap.get( 4L ) ).addAsLast( edgeMap.get( 6L ) ).build() ), toString( result.get( edgeMap.get( 6L ) ) ) );

        // TODO test turn restrictions somehow - Uturns?
//        Node source = nodeMap.get( 0L );
//        Node destination = nodeMap.get( 3L );
//        DijkstraAlgorithm instance = new DijkstraAlgorithm();
//        Route<Node, Edge> expResult = Route.builder().addAsLast( edgeMap.get( 0L ) ).addAsLast( edgeMap.get( 4L ) ).addAsLast( edgeMap.get( 6L ) ).addAsLast( edgeMap.get( 5L ) ).build();
//        Route<Node, Edge> result = instance.route( graph, Metric.LENGTH, source, destination );
//        assertEquals( toString( expResult ), toString( result ) );
    }

    private static String toString( Route<Node, Edge> route ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Route{source=" ).append( route.getSource().getId() ).append( ",target=" ).append( route.getTarget().getId() ).append( ",edges=[" );
        for ( Edge edge : route.getEdges() ) {
            sb.append( edge.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]}" );
        return sb.toString();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphGeneratorUtils;
import cz.certicon.routing.utils.ToStringUtils_Test;
import cz.certicon.routing.utils.collections.CollectionUtils;
import cz.certicon.routing.utils.collections.Iterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class UndirectedGraphTest {

    private Graph<Node, Edge> graph;
    private Map<Long, Node> nodeMap;
    private Map<Long, Edge> edgeMap;
    private Map<TurnTable, TurnTable> turnTables;
    ToStringUtils_Test.UndirectedNodeCreator nc;
    ToStringUtils_Test.UndirectedEdgeCreator ec;

    public UndirectedGraphTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        nodeMap = new HashMap<>();
        edgeMap = new HashMap<>();
        turnTables = new HashMap<>();
        graph = GraphGeneratorUtils.createGraph( EnumSet.of( Metric.LENGTH ), nodeMap, edgeMap, turnTables );
        nc = new ToStringUtils_Test.UndirectedNodeCreator();
        ec = new ToStringUtils_Test.UndirectedEdgeCreator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getNodesCount method, of class UndirectedGraph.
     */
    @Test
    public void testGetNodesCount() {
        System.out.println( "getNodesCount" );
        Graph instance = graph;
        int expResult = 6;
        int result = instance.getNodesCount();
        assertEquals( expResult, result );
    }

    /**
     * Test of getNodeIds method, of class UndirectedGraph.
     */
    @Test
    public void testGetNodes() {
        System.out.println( "getNodeIds" );
        Graph instance = graph;
        String expResult = "[0,1,2,3,4,5]";
        String result = ToStringUtils_Test.toString( instance.getNodes() );
        assertEquals( expResult, result );
    }

    /**
     * Test of getEdgeCount method, of class UndirectedGraph.
     */
    @Test
    public void testGetEdgeCount() {
        System.out.println( "getEdgeCount" );
        Graph instance = graph;
        int expResult = 7;
        int result = instance.getEdgeCount();
        assertEquals( expResult, result );
    }

    /**
     * Test of getEdgeIds method, of class UndirectedGraph.
     */
    @Test
    public void testGetEdges() {
        System.out.println( "getEdgeIds" );
        Graph instance = graph;
        String expResult = "[0,1,2,3,4,5,6]";
        String result = ToStringUtils_Test.toString( instance.getEdges() );
        assertEquals( expResult, result );
    }

    /**
     * Test of getIncomingEdges method, of class UndirectedGraph.
     */
    @Test
    public void testGetIncomingEdges() {
        System.out.println( "getIncomingEdges" );
        testGetIncomingEdges( 0, "[0,1]" );
        testGetIncomingEdges( 1, "[0,3,4]" );
        testGetIncomingEdges( 2, "[1,2]" );
        testGetIncomingEdges( 3, "[2,5]" );
        testGetIncomingEdges( 4, "[4]" );
        testGetIncomingEdges( 5, "[5,6]" );
    }

    private void testGetIncomingEdges( long nodeId, String expResult ) {
        Node node = nodeMap.get( nodeId );
        String result = ToStringUtils_Test.toString( node.getIncomingEdges() );
        assertEquals( expResult, result );
    }

    /**
     * Test of getOutgoingEdges method, of class UndirectedGraph.
     */
    @Test
    public void testGetOutgoingEdges() {
        System.out.println( "getOutgoingEdges" );
        testGetOutgoingEdges( 0, "[0,1]" );
        testGetOutgoingEdges( 1, "[0,4]" );
        testGetOutgoingEdges( 2, "[1,2]" );
        testGetOutgoingEdges( 3, "[2,3,5]" );
        testGetOutgoingEdges( 4, "[4,6]" );
        testGetOutgoingEdges( 5, "[5]" );
    }

    private void testGetOutgoingEdges( long nodeId, String expResult ) {
        Node node = nodeMap.get( nodeId );
        String result = ToStringUtils_Test.toString( node.getOutgoingEdges() );
        assertEquals( expResult, result );
    }

    /**
     * Test of getSourceNode method, of class UndirectedGraph.
     */
    @Test
    public void testGetSourceNode() {
        System.out.println( "getSourceNode" );
        testGetSourceNodeCompare( 0, 0 );
        testGetSourceNodeCompare( 1, 0 );
        testGetSourceNodeCompare( 2, 2 );
        testGetSourceNodeCompare( 3, 3 );
        testGetSourceNodeCompare( 4, 1 );
        testGetSourceNodeCompare( 5, 3 );
        testGetSourceNodeCompare( 6, 4 );
    }

    private void testGetSourceNodeCompare( long edgeId, long nodeId ) {
        Edge edge = edgeMap.get( edgeId );
        Node expResult = nodeMap.get( nodeId );
        Node result = edge.getSource();
        assertEquals( expResult, result );
    }

    /**
     * Test of getTargetNode method, of class UndirectedGraph.
     */
    @Test
    public void testGetTargetNode() {
        System.out.println( "getTargetNode" );
        testGetTargetNodeCompare( 0, 1 );
        testGetTargetNodeCompare( 1, 2 );
        testGetTargetNodeCompare( 2, 3 );
        testGetTargetNodeCompare( 3, 1 );
        testGetTargetNodeCompare( 4, 4 );
        testGetTargetNodeCompare( 5, 5 );
        testGetTargetNodeCompare( 6, 5 );
    }

    private void testGetTargetNodeCompare( long edgeId, long nodeId ) {
        Edge edge = edgeMap.get( edgeId );
        Node expResult = nodeMap.get( nodeId );
        Node result = edge.getTarget();
        assertEquals( expResult, result );
    }

    /**
     * Test of getOtherNode method, of class UndirectedGraph.
     */
    @Test
    public void testGetOtherNode() {
        System.out.println( "getOtherNode" );
        testGetOtherNodeCompare( 0, 0, 1 );
        testGetOtherNodeCompare( 1, 0, 2 );
        testGetOtherNodeCompare( 2, 2, 3 );
        testGetOtherNodeCompare( 3, 1, 3 );
        testGetOtherNodeCompare( 4, 1, 4 );
        testGetOtherNodeCompare( 5, 3, 5 );
        testGetOtherNodeCompare( 6, 4, 5 );
    }

    private void testGetOtherNodeCompare( long edgeId, long nodeId, long otherNodeId ) {
        {
            Edge edge = edgeMap.get( edgeId );
            Node expResult = nodeMap.get( otherNodeId );
            Node result = edge.getOtherNode( nodeMap.get( nodeId ) );
            assertEquals( expResult, result );
        }
        {
            Edge edge = edgeMap.get( edgeId );
            Node expResult = nodeMap.get( nodeId );
            Node result = edge.getOtherNode( nodeMap.get( otherNodeId ) );
            assertEquals( expResult, result );
        }
    }

    /**
     * Test of getTurnCost method, of class UndirectedGraph.
     */
    @Test
    public void testGetTurnCost() {
        System.out.println( "getTurnCost" );
//        System.out.println( graph.toString() );
        for ( Node<Node, Edge> node : graph.getNodes() ) {
            for ( Edge incoming : node.getIncomingEdges() ) {
                for ( Edge outgoing : node.getOutgoingEdges() ) {
//                    System.out.println( "turn cost at node: " + node.getId() + " from: " + incoming.getId() + " to: " + outgoing.getId() );
                    if ( incoming.equals( outgoing ) || ( node.getId() == 2 && incoming.getId() == 1 && outgoing.getId() == 2 ) /*see GraphGeneratorUtils.java:132*/ ) {
                        assertEquals( Distance.newInfinityInstance(), node.getTurnDistance( incoming, outgoing ) );
                    } else {
                        assertEquals( Distance.newInstance( 0 ), node.getTurnDistance( incoming, outgoing ) );
                    }
                }
            }
        }
    }

    @Test
    public void fromGraph_Returns_EmptyGraph_From_EmptyGraph_() {
        assertGraph( "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[],edges=[]}" );
    }

    @Test
    public void fromGraph_Returns_ThreeNodeGraph_From_ThreeNodeGraph_() {
        assertGraph( "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[1,4,5],edges=[]}" );
    }

    @Test
    public void fromGraph_Returns_ThreeNodeTwoEdgesGraph_From_ThreeNodeTwoEdgesGraph_() {
        assertGraph( "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[1,4,5],edges=[1{5->4},7{1<->5}]}" );
    }

    private void assertGraph( String representation ) {
        UndirectedGraph fromGraph = UndirectedGraph.fromGraph( ToStringUtils_Test.fromString( new UndirectedGraph(), representation, nc, ec ) );
        assertThat( ToStringUtils_Test.toString( fromGraph ), CoreMatchers.equalTo( representation ) );
    }
}

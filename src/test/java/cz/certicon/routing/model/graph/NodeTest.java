/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphGeneratorUtils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
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
public class NodeTest {

    private final Graph<Node, Edge> graph;
    private final Map<Long, Node> nodeMap;
    private final Map<Long, Edge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public NodeTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        this.graph = GraphGeneratorUtils.createGraph(EnumSet.of( Metric.LENGTH), nodeMap, edgeMap, turnTables );
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
        String result = edgeIteratorToString( graph.getIncomingEdges( node ) );
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
        String result = edgeIteratorToString( graph.getOutgoingEdges(node) );
        assertEquals( expResult, result );
    }

    /**
     * Test of getId method, of class Node.
     */
//    @Test
//    public void testGetId() {
//        System.out.println( "getId" );
//        SimpleNode instance = createNode( new ArrayList<SimpleNode>(), 0 );
//        assertEquals( 0, instance.getId() );
//        assertNotEquals( 0, createNode( new ArrayList<SimpleNode>(), 1 ).getId() );
//    }

    /**
     * Test of addEdge method, of class Node.
     */
//    @Test
//    public void testAddEdge_Edge() {
//        System.out.println( "addEdge" );
//        Node node = createNode( new ArrayList<SimpleNode>(), 0 );
//        node.addEdge( createEdge( new ArrayList<SimpleEdge>(), 0, true, node, node, 0, false ) );
//        node.addEdge( createEdge( new ArrayList<SimpleEdge>(), 1, true, node, node, 0, false ) );
//        assertEquals( "[0,1]", edgeIteratorToString( node.getOutgoingEdges() ) );
//    }

    /**
     * Test of addEdge method, of class Node.
     */
//    @Test
//    public void testAddEdge_Edge_int() {
//        System.out.println( "addEdge_int" );
//        SimpleNode node = createNode( new ArrayList<SimpleNode>(), 0 );
//        SimpleEdge a = createEdge( new ArrayList<SimpleEdge>(), 0, true, node, node, 0, false );
//        SimpleEdge b = createEdge( new ArrayList<SimpleEdge>(), 1, true, node, node, 0, false );
//        SimpleEdge c = createEdge( new ArrayList<SimpleEdge>(), 2, true, node, node, 0, false );
//        node.addEdge( a );
//        node.addEdge( b, 5 );
//        node.addEdge( c );
//        assertEquals( "[0,1,2]", edgeIteratorToString( node.getOutgoingEdges() ) ); // iterator skips invalid
//        assertEquals( 0, node.getEdgePosition( a ) );
//        assertEquals( 5, node.getEdgePosition( b ) );
//        assertEquals( 6, node.getEdgePosition( c ) );
//    }

    /**
     * Test of setTurnTable method, of class Node.
     */
    @Test
    public void testSetTurnTable() {
//        System.out.println( "setTurnTable" );
//        TurnTable turnTable = null;
//        Node instance = null;
//        Node expResult = null;
//        Node result = instance.setTurnTable( turnTable );
//        assertEquals( expResult, result );
//        // TODO review the generated test code and remove the default call to fail.
//        fail( "The test case is a prototype." );
    }

    /**
     * Test of getDegree method, of class Node.
     */
//    @Test
//    public void testGetDegree() {
//        System.out.println( "getDegree" );
//        SimpleNode node = createNode( new ArrayList<SimpleNode>(), 0 );
//        node.addEdge( createEdge( new ArrayList<SimpleEdge>(), 0, true, node, node, 0 ) );
//        node.addEdge( createEdge( new ArrayList<SimpleEdge>(), 1, true, node, node, 0 ) );
//        assertEquals( 2, node.getDegree() );
//    }

    /**
     * Test of lock method, of class Node.
     */
//    @Test( expected = IllegalStateException.class )
//    public void testLock() {
//        System.out.println( "lock" );
//        SimpleNode node = createNode( new ArrayList<SimpleNode>(), 0 );
//        node.addEdge( createEdge( new ArrayList<SimpleEdge>(), 0, true, node, node, 0 ) );
//        node.lock();
//        node.addEdge( createEdge( new ArrayList<SimpleEdge>(), 1, true, node, node, 0 ) );
//    }

    private static String edgeIteratorToString( Iterator<Edge> iterator ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        while ( iterator.hasNext() ) {
            sb.append( iterator.next().getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
    }

    private static String nodeIteratorToString( Iterator<SimpleNode> iterator ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        while ( iterator.hasNext() ) {
            sb.append( iterator.next().getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
    }
}

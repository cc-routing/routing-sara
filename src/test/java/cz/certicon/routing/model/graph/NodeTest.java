/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;
import java.util.ArrayList;
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

    private final UndirectedGraph graph;
    private final Map<Long, Node> nodeMap;
    private final Map<Long, Edge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public NodeTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = createGraph();
    }

    private UndirectedGraph createGraph() {
        List<Node> nodes = new ArrayList<>();
        Node a = createNode( nodes, 0 );
        Node b = createNode( nodes, 1 );
        Node c = createNode( nodes, 2 );
        Node d = createNode( nodes, 3 );
        Node e = createNode( nodes, 4 );
        Node f = createNode( nodes, 5 );
        List<Edge> edges = new ArrayList<>();
        Edge ab = createEdge( edges, 0, false, a, b, 120 );
        Edge ac = createEdge( edges, 1, false, a, c, 184 );
        Edge cd = createEdge( edges, 2, false, c, d, 94 );
        Edge db = createEdge( edges, 3, true, d, b, 159 );
        Edge be = createEdge( edges, 4, false, b, e, 36 );
        Edge df = createEdge( edges, 5, false, d, f, 152 );
        Edge ef = createEdge( edges, 6, true, e, f, 38 );
        for ( Node node : nodes ) {
            int size = node.getDegree();
            Distance[][] dtt = new Distance[size][size];
            for ( int i = 0; i < dtt.length; i++ ) {
                for ( int j = 0; j < dtt[i].length; j++ ) {
                    if ( i != j ) {
                        dtt[i][j] = Distance.newInstance( 0 );
                    } else {
                        dtt[i][j] = Distance.newInfinityInstance();
                    }
                }
            }
            TurnTable tt = new TurnTable( dtt );
            if ( !turnTables.containsKey( tt ) ) {
                turnTables.put( tt, tt );
            } else {
                tt = turnTables.get( tt );
            }
            node.setTurnTable( tt );
        }
        for ( Node node : nodes ) {
            node.lock();
        }
        UndirectedGraph g = new UndirectedGraph( nodes, edges );
        return g;
    }

    private Node createNode( List<Node> nodes, long id ) {
//        Distance[][] tt = new Distance[]
        Node node = new Node( id );
        nodes.add( node );
        nodeMap.put( id, node );
        return node;
    }

    private Edge createEdge( List<Edge> edges, long id, boolean oneway, Node source, Node target, double distance ) {
        Edge edge = new Edge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        source.addEdge( edge );
        target.addEdge( edge );
        edgeMap.put( id, edge );
        return edge;
    }

    private Edge createEdge( List<Edge> edges, long id, boolean oneway, Node source, Node target, double distance, boolean addToNode ) {
        Edge edge = new Edge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        if ( addToNode ) {
            source.addEdge( edge );
            target.addEdge( edge );
        }
        edgeMap.put( id, edge );
        return edge;
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
     * Test of getEdgePosition method, of class Node.
     */
    @Test
    public void testGetEdgePosition() {
        System.out.println( "getEdgePosition" );
        Node node = createNode( new ArrayList<Node>(), 0 );
        Edge a = createEdge( new ArrayList<Edge>(), 0, true, node, node, 0 );
        Edge b = createEdge( new ArrayList<Edge>(), 1, true, node, node, 0 );
        System.out.println( edgeIteratorToString( node.getIncomingEdges() ) );
        System.out.println( edgeIteratorToString( node.getOutgoingEdges() ) );
        assertEquals( 0, node.getEdgePosition( a ) );
        assertEquals( 1, node.getEdgePosition( b ) );
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
        String result = edgeIteratorToString( node.getIncomingEdges() );
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
        String result = edgeIteratorToString( node.getOutgoingEdges() );
        assertEquals( expResult, result );
    }

    /**
     * Test of getId method, of class Node.
     */
    @Test
    public void testGetId() {
        System.out.println( "getId" );
        Node instance = createNode( new ArrayList<Node>(), 0 );
        assertEquals( 0, instance.getId() );
        assertNotEquals( 0, createNode( new ArrayList<Node>(), 1 ).getId() );
    }

    /**
     * Test of addEdge method, of class Node.
     */
    @Test
    public void testAddEdge_Edge() {
        System.out.println( "addEdge" );
        Node node = createNode( new ArrayList<Node>(), 0 );
        node.addEdge( createEdge( new ArrayList<Edge>(), 0, true, node, node, 0, false ) );
        node.addEdge( createEdge( new ArrayList<Edge>(), 1, true, node, node, 0, false ) );
        assertEquals( "[0,1]", edgeIteratorToString( node.getOutgoingEdges() ) );
    }

    /**
     * Test of addEdge method, of class Node.
     */
    @Test
    public void testAddEdge_Edge_int() {
        System.out.println( "addEdge_int" );
        Node node = createNode( new ArrayList<Node>(), 0 );
        Edge a = createEdge( new ArrayList<Edge>(), 0, true, node, node, 0, false );
        Edge b = createEdge( new ArrayList<Edge>(), 1, true, node, node, 0, false );
        Edge c = createEdge( new ArrayList<Edge>(), 2, true, node, node, 0, false );
        node.addEdge( a );
        node.addEdge( b, 5 );
        node.addEdge( c );
        assertEquals( "[0,1,2]", edgeIteratorToString( node.getOutgoingEdges() ) ); // iterator skips invalid
        assertEquals( 0, node.getEdgePosition( a ) );
        assertEquals( 5, node.getEdgePosition( b ) );
        assertEquals( 6, node.getEdgePosition( c ) );
    }

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
    @Test
    public void testGetDegree() {
        System.out.println( "getDegree" );
        Node node = createNode( new ArrayList<Node>(), 0 );
        node.addEdge( createEdge( new ArrayList<Edge>(), 0, true, node, node, 0 ) );
        node.addEdge( createEdge( new ArrayList<Edge>(), 1, true, node, node, 0 ) );
        assertEquals( 2, node.getDegree() );
    }

    /**
     * Test of lock method, of class Node.
     */
    @Test( expected = IllegalStateException.class )
    public void testLock() {
        System.out.println( "lock" );
        Node node = createNode( new ArrayList<Node>(), 0 );
        node.addEdge( createEdge( new ArrayList<Edge>(), 0, true, node, node, 0 ) );
        node.lock();
        node.addEdge( createEdge( new ArrayList<Edge>(), 1, true, node, node, 0 ) );
    }

    private static String edgeIteratorToString( Iterator<Edge> iterator ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        while ( iterator.hasNext() ) {
            sb.append( iterator.next().getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
    }

    private static String nodeIteratorToString( Iterator<Node> iterator ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        while ( iterator.hasNext() ) {
            sb.append( iterator.next().getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
    }
}

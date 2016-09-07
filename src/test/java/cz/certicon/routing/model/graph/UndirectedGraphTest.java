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
public class UndirectedGraphTest {

    private final UndirectedGraph graph;
    private final Map<Long, SimpleNode> nodeMap;
    private final Map<Long, SimpleEdge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public UndirectedGraphTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = createGraph();
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

    private UndirectedGraph createGraph() {
        List<SimpleNode> nodes = new ArrayList<>();
        SimpleNode a = createNode( nodes, 0 );
        SimpleNode b = createNode( nodes, 1 );
        SimpleNode c = createNode( nodes, 2 );
        SimpleNode d = createNode( nodes, 3 );
        SimpleNode e = createNode( nodes, 4 );
        SimpleNode f = createNode( nodes, 5 );
        List<SimpleEdge> edges = new ArrayList<>();
        SimpleEdge ab = createEdge( edges, 0, false, a, b, 120 );
        SimpleEdge ac = createEdge( edges, 1, false, a, c, 184 );
        SimpleEdge cd = createEdge( edges, 2, false, c, d, 94 );
        SimpleEdge db = createEdge( edges, 3, true, d, b, 159 );
        SimpleEdge be = createEdge( edges, 4, false, b, e, 36 );
        SimpleEdge df = createEdge( edges, 5, false, d, f, 152 );
        SimpleEdge ef = createEdge( edges, 6, true, e, f, 38 );
        for ( SimpleNode node : nodes ) {
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
        for ( SimpleNode node : nodes ) {
            node.lock();
        }
        UndirectedGraph g = UndirectedGraph.builder().nodes( nodes ).edges( edges ).build();
        return g;
    }

    private SimpleNode createNode( List<SimpleNode> nodes, long id ) {
//        Distance[][] tt = new Distance[]
        SimpleNode node = new SimpleNode( id );
        nodes.add( node );
        nodeMap.put( id, node );
        return node;
    }

    private SimpleEdge createEdge( List<SimpleEdge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, double distance ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        source.addEdge( edge );
        target.addEdge( edge );
        edgeMap.put( id, edge );
        return edge;
    }

    private SimpleEdge createEdge( List<SimpleEdge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, double distance, boolean addToNode ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        if ( addToNode ) {
            source.addEdge( edge );
            target.addEdge( edge );
        }
        edgeMap.put( id, edge );
        return edge;
    }

    /**
     * Test of getNodesCount method, of class UndirectedGraph.
     */
    @Test
    public void testGetNodesCount() {
        System.out.println( "getNodesCount" );
        UndirectedGraph instance = graph;
        int expResult = 6;
        int result = instance.getNodesCount();
        assertEquals( expResult, result );
    }

    /**
     * Test of getNodes method, of class UndirectedGraph.
     */
    @Test
    public void testGetNodes() {
        System.out.println( "getNodes" );
        UndirectedGraph instance = graph;
        String expResult = "[0,1,2,3,4,5]";
        String result = nodeIteratorToString( instance.getNodes() );
        assertEquals( expResult, result );
    }

    /**
     * Test of getEdgeCount method, of class UndirectedGraph.
     */
    @Test
    public void testGetEdgeCount() {
        System.out.println( "getEdgeCount" );
        UndirectedGraph instance = graph;
        int expResult = 7;
        int result = instance.getEdgeCount();
        assertEquals( expResult, result );
    }

    /**
     * Test of getEdges method, of class UndirectedGraph.
     */
    @Test
    public void testGetEdges() {
        System.out.println( "getEdges" );
        UndirectedGraph instance = graph;
        String expResult = "[0,1,2,3,4,5,6]";
        String result = edgeIteratorToString( instance.getEdges() );
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
        SimpleNode node = nodeMap.get( nodeId );
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
        SimpleNode node = nodeMap.get( nodeId );
        String result = edgeIteratorToString( graph.getOutgoingEdges( node ) );
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
        SimpleEdge edge = edgeMap.get( edgeId );
        SimpleNode expResult = nodeMap.get( nodeId );
        SimpleNode result = graph.getSourceNode( edge );
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
        SimpleEdge edge = edgeMap.get( edgeId );
        SimpleNode expResult = nodeMap.get( nodeId );
        SimpleNode result = graph.getTargetNode( edge );
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
            SimpleEdge edge = edgeMap.get( edgeId );
            SimpleNode expResult = nodeMap.get( otherNodeId );
            SimpleNode result = graph.getOtherNode( edge, nodeMap.get( nodeId ) );
            assertEquals( expResult, result );
        }
        {
            SimpleEdge edge = edgeMap.get( edgeId );
            SimpleNode expResult = nodeMap.get( nodeId );
            SimpleNode result = graph.getOtherNode( edge, nodeMap.get( otherNodeId ) );
            assertEquals( expResult, result );
        }
    }

    private static String edgeIteratorToString( Iterator<SimpleEdge> iterator ) {
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

    /**
     * Test of getTurnCost method, of class UndirectedGraph.
     */
    @Test
    public void testGetTurnCost() {
        System.out.println( "getTurnCost" );
        Iterator<SimpleNode> nodes = graph.getNodes();
        while ( nodes.hasNext() ) {
            SimpleNode node = nodes.next();
            Iterator<SimpleEdge> incomingEdges = node.getIncomingEdges();
            while ( incomingEdges.hasNext() ) {
                SimpleEdge incoming = incomingEdges.next();
                Iterator<SimpleEdge> outgoingEdges = node.getOutgoingEdges();
                while ( outgoingEdges.hasNext() ) {
                    SimpleEdge outgoing = outgoingEdges.next();
                    if ( incoming.equals( outgoing ) ) {
                        assertEquals( Distance.newInfinityInstance(), graph.getTurnCost( node, incoming, outgoing ) );
                    } else {
                        assertEquals( Distance.newInstance( 0 ), graph.getTurnCost( node, incoming, outgoing ) );
                    }
                }
            }
        }
    }
}

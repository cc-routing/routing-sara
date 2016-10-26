/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.basic.MaxIdContainer;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphUtils;
import cz.certicon.routing.utils.ToStringUtils_Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
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
        UndirectedGraph g = new UndirectedGraph();
        ContractGraph graph = new ContractGraph( EnumSet.of( Metric.SIZE ) );
        Set<Node> origNodesA = new HashSet<Node>( Arrays.asList( g.createNode( -1 ), g.createNode( -2 ), g.createNode( -3 ) ) );
        Set<Node> origNodesB = new HashSet<Node>( Arrays.asList( g.createNode( -4 ), g.createNode( -5 ) ) );
        Set<Node> origNodes = new HashSet<Node>( Arrays.asList( g.createNode( -6 ) ) );
        ContractNode nodeA = graph.createNode( 0, origNodesA );
        ContractNode nodeB = graph.createNode( 1, origNodesB );
        Set<Edge> origEdges = new HashSet<Edge>( Arrays.asList( g.createEdge( -1, false, (SimpleNode) origNodesA.iterator().next(), (SimpleNode) origNodesB.iterator().next(), -1, -1 ) ) );
        ContractEdge connectEdge = graph.createEdge( 0, false, nodeA, nodeB, origEdges );
        ContractNode[] neighbors = new ContractNode[5];
        for ( int i = 0; i < neighbors.length; i++ ) {
            neighbors[i] = graph.createNode( 2 + i, origNodes );
        }
        for ( int i = 0; i < 3; i++ ) {
            ContractNode neighbor = neighbors[i];
            ContractEdge edge = graph.createEdge( i + 1, false, nodeA, neighbor, origEdges, new Pair<>( Metric.SIZE, Distance.newInstance( 1 ) ) );
        }
        for ( int i = 2; i < 5; i++ ) {
            ContractNode neighbor = neighbors[i];
            ContractEdge edge = graph.createEdge( i + 2, false, nodeB, neighbor, origEdges, new Pair<>( Metric.SIZE, Distance.newInstance( 1 ) ) );
        }
        MaxIdContainer nodeMaxIdContainer = new MaxIdContainer( 9 );
        MaxIdContainer edgeMaxIdContainer = new MaxIdContainer( 9 );

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
//        System.out.println( "nodeA=" + nodeA );
//        System.out.println( "nodeB=" + nodeB );
        System.out.println( graph );
        String expResult = "ContractNode{edges={edge[1]->node#2,edge[1]->node#3,edge[1]->node#4,edge[1]->node#5,edge[1]->node#6}}"; // edge[1]->4 because it is a set and the sets are all the same
        ContractNode result = nodeA.mergeWith( nodeB, nodeMaxIdContainer, edgeMaxIdContainer );
//        System.out.println( expResult );
//        System.out.println( toString( result ) );
//        System.out.println( result.toString() );
        System.out.println( graph );
        assertEquals(expResult, ToStringUtils_Test.toString( result ) );
    }

    /**
     * Test of getNodeIds method, of class ContractNode.
     */
    @Test
    public void testGetNodes() {
        System.out.println( "getNodeIds" );
        UndirectedGraph g = new UndirectedGraph();
        ContractGraph graph = new ContractGraph();
        Set<Node> origNodes = new HashSet<Node>( Arrays.asList( g.createNode( -1 ), g.createNode( -2 ), g.createNode( -3 ) ) );
        ContractNode instance = graph.createNode( 0, origNodes );
        Collection<Node> expResult = new HashSet<>( origNodes );
        origNodes.clear();
        Collection<Node> result = instance.getNodes();
        assertEquals( expResult, result );
    }

}

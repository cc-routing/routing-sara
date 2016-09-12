/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphUtils;
import java.util.Arrays;
import java.util.HashSet;
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
public class NodePairTest {

    public NodePairTest() {
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
     * Test of other method, of class NodePair.
     */
    @Test
    public void testOther() {
        System.out.println( "other" );
        ContractNode nodeA = new ContractNode( 0, new HashSet<Node>() );
        ContractNode nodeB = new ContractNode( 1, new HashSet<Node>() );
        ContractEdge edge = new ContractEdge( 0, false, nodeB, nodeA, new HashSet<Edge>() );
        Graph<ContractNode, ContractEdge> graph = new UndirectedGraph<>( GraphUtils.toMap( Arrays.asList( nodeA, nodeB ) ), GraphUtils.toMap( Arrays.asList( edge ) ), null );
        NodePair instance = new NodePair( graph, nodeA, nodeB, edge );
        assertEquals( nodeA, instance.other( nodeB ) );
        assertEquals( nodeB, instance.other( nodeA ) );
    }

    /**
     * Test of other method, of class NodePair.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testOther_unknown() {
        System.out.println( "other_unknown" );
        ContractNode nodeA = new ContractNode( 0, new HashSet<Node>() );
        ContractNode nodeB = new ContractNode( 1, new HashSet<Node>() );
        ContractNode nodeC = new ContractNode( 2, new HashSet<Node>() );
        ContractEdge edge = new ContractEdge( 0, false, nodeB, nodeA, new HashSet<Edge>() );
        Graph<ContractNode, ContractEdge> graph = new UndirectedGraph<>( GraphUtils.toMap( Arrays.asList( nodeA, nodeB, nodeC ) ), GraphUtils.toMap( Arrays.asList( edge ) ), null );
        NodePair instance = new NodePair( graph, nodeA, nodeB, edge );
        instance.other( nodeC );
    }

    /**
     * Test of hashCode method, of class NodePair.
     */
    @Test
    public void testHashCode() {
        System.out.println( "hashCode" );
        ContractNode nodeA = new ContractNode( 0, new HashSet<Node>() );
        ContractNode nodeB = new ContractNode( 1, new HashSet<Node>() );
        ContractEdge edge = new ContractEdge( 0, false, nodeB, nodeA, new HashSet<Edge>() );
        Graph<ContractNode, ContractEdge> graph = new UndirectedGraph<>( GraphUtils.toMap( Arrays.asList( nodeA, nodeB ) ), GraphUtils.toMap( Arrays.asList( edge ) ), null );
        NodePair instanceA = new NodePair( graph, nodeA, nodeB, edge );
        NodePair instanceB = new NodePair( graph, nodeB, nodeA, edge );
        assertTrue( instanceA.hashCode() == instanceB.hashCode() );
    }

    /**
     * Test of equals method, of class NodePair.
     */
    @Test
    public void testEquals() {
        System.out.println( "equals" );
        ContractNode nodeA = new ContractNode( 0, new HashSet<Node>() );
        ContractNode nodeB = new ContractNode( 1, new HashSet<Node>() );
        ContractEdge edge = new ContractEdge( 0, false, nodeB, nodeA, new HashSet<Edge>() );
        Graph<ContractNode, ContractEdge> graph = new UndirectedGraph<>( GraphUtils.toMap( Arrays.asList( nodeA, nodeB ) ), GraphUtils.toMap( Arrays.asList( edge ) ), null );
        NodePair instanceA = new NodePair( graph, nodeA, nodeB, edge );
        NodePair instanceB = new NodePair( graph, nodeB, nodeA, edge );
        assertTrue( instanceA.equals( instanceB ) );
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.assembly;

import cz.certicon.routing.algorithm.sara.preprocessing.filtering.NaturalCutsFilter;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.graph.preprocessing.NodePair;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.utils.GraphGeneratorUtils;
import cz.certicon.routing.view.GraphStreamPresenter;
import java.util.HashMap;
import java.util.Iterator;
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
public class GreedyAssemblerTest {

    private final UndirectedGraph g;
    private final FilteredGraph graph;
    private final Map<Long, Node> nodeMap;
    private final Map<Long, Edge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;
    private static final int CELL_SIZE = 10;

    public GreedyAssemblerTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        g = GraphGeneratorUtils.generateGridGraph( nodeMap, edgeMap, turnTables, 5, 5 );
        NaturalCutsFilter instance = new NaturalCutsFilter( 1, 4, CELL_SIZE );
        graph = instance.filter( g );
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
     * Test of assemble method, of class GreedyAssembler.
     */
    @Test
    public void testAssemble() {
        System.out.println( "assemble" );
        GreedyAssembler assembler = new GreedyAssembler( 0.5, 0.5, CELL_SIZE );
        Graph assembled = assembler.assemble( graph );

        GraphStreamPresenter presenter = new GraphStreamPresenter();
        presenter.setGraph( assembled );
        presenter.display();
        System.out.println( "Comparison: orig{nodes=" + graph.getNodesCount() + ",edges=" + graph.getEdgeCount() + "}, filtered{nodes=" + assembled.getNodesCount() + ",edges=" + assembled.getEdgeCount() + "}" );

    }

    /**
     * Test of initQueue method, of class GreedyAssembler.
     */
    @Test
    public void testInitQueue() {
        System.out.println( "initQueue" );
        GreedyAssembler instance = new GreedyAssembler( 0.5, 0.5, CELL_SIZE );
        PriorityQueue<NodePair> result = instance.initQueue( graph );
        Iterator<Node> nodes = graph.getNodes();
        while ( nodes.hasNext() ) {
            ContractNode node = (ContractNode) nodes.next();
            Iterator<Edge> edges = node.getEdges();
            while ( edges.hasNext() ) {
                ContractEdge edge = (ContractEdge) edges.next();
                ContractNode target = (ContractNode) edge.getOtherNode( node );
                NodePair pair = new NodePair( node, target, edge );
                assertTrue( result.contains( pair ) );
            }
        }
    }

    /**
     * Test of clearPairs method, of class GreedyAssembler.
     */
    @Test
    public void testClearPairs() {
        System.out.println( "clearPairs" );
        GreedyAssembler instance = new GreedyAssembler( 0.5, 0.5, CELL_SIZE );
        PriorityQueue<NodePair> queue = instance.initQueue( graph );
        ContractNode nodeA = null;
        ContractNode nodeB = null;
        Iterator<Node> nodes = graph.getNodes();
        while ( nodes.hasNext() ) {
            ContractNode node = (ContractNode) nodes.next();
            if ( nodeA == null ) {
                nodeA = node;
            } else if ( nodeB == null ) {
                nodeB = node;
            } else {
                break;
            }
        }
        ContractEdge connectingEdge = null;
        Iterator<Edge> edges = nodeA.getEdges();
        while ( edges.hasNext() ) {
            ContractEdge edge = (ContractEdge) edges.next();
            if ( ( edge.getSource().equals( nodeA ) && edge.getTarget().equals( nodeB ) ) || ( edge.getSource().equals( nodeB ) && edge.getTarget().equals( nodeA ) ) ) {
                connectingEdge = edge;
                break;
            }
        }
        NodePair origPair = new NodePair( nodeA, nodeB, connectingEdge );
        PriorityQueue<NodePair> result = instance.clearPairs( queue, origPair, nodeA );
        nodes = graph.getNodes();
        while ( nodes.hasNext() ) {
            ContractNode node = (ContractNode) nodes.next();
            node.getEdges();
            while ( edges.hasNext() ) {
                ContractEdge edge = (ContractEdge) edges.next();
                ContractNode target = (ContractNode) edge.getOtherNode( node );
                NodePair pair = new NodePair( node, target, edge );
                if ( nodeA.equals( node ) || nodeA.equals( target ) ) {
                    assertFalse( result.contains( pair ) );
                } else {
                    assertTrue( result.contains( pair ) );
                }
            }
        }
        result = instance.clearPairs( queue, origPair, nodeA );
        nodes = graph.getNodes();
        while ( nodes.hasNext() ) {
            ContractNode node = (ContractNode) nodes.next();
            node.getEdges();
            while ( edges.hasNext() ) {
                ContractEdge edge = (ContractEdge) edges.next();
                ContractNode target = (ContractNode) edge.getOtherNode( node );
                NodePair pair = new NodePair( node, target, edge );
                if ( nodeA.equals( node ) || nodeA.equals( target ) || nodeB.equals( node ) || nodeB.equals( target ) ) {
                    assertFalse( result.contains( pair ) );
                } else {
                    assertTrue( result.contains( pair ) );
                }
            }
        }
    }

    /**
     * Test of addPairs method, of class GreedyAssembler.
     */
    @Test
    public void testAddPairs() {
        System.out.println( "addPairs" );
        PriorityQueue<NodePair> queue = null;
        FilteredGraph graph = null;
        ContractNode contractedNode = null;
        GreedyAssembler instance = null;
        PriorityQueue<NodePair> expResult = null;
        PriorityQueue<NodePair> result = instance.addPairs( queue, graph, contractedNode );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    private FilteredGraph filter( Graph graph ) {
        NaturalCutsFilter instance = new NaturalCutsFilter( 1, 4, 40 );
        FilteredGraph result = instance.filter( graph );
        return result;
    }
}

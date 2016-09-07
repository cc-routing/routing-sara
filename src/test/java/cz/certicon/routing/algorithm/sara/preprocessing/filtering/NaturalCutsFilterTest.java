/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.algorithm.sara.preprocessing.assembly.Assembler;
import cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphGeneratorUtils;
import cz.certicon.routing.view.GraphStreamPresenter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
public class NaturalCutsFilterTest {

    private final UndirectedGraph graph;
    private final Map<Long, SimpleNode> nodeMap;
    private final Map<Long, SimpleEdge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public NaturalCutsFilterTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = GraphGeneratorUtils.generateGridGraph( nodeMap, edgeMap, turnTables, 10, 10 );
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
     * Test of filter method, of class NaturalCutsFilter.
     */
    @Test
    public void testFilter() {
        System.out.println( "filter" );
        NaturalCutsFilter instance = new NaturalCutsFilter( 1, 4, 10 );

        Graph originalGraph = GraphGeneratorUtils.generateGridGraph( nodeMap, edgeMap, turnTables, 5, 5 );
        Set<SimpleNode> origNodes = new HashSet<>();
        Iterator<SimpleNode> nodes = originalGraph.getNodes();
        while ( nodes.hasNext() ) {
            origNodes.add( nodes.next() );
        }
//        System.out.println( "orig graph: " + originalGraph );
        Graph g = GraphGeneratorUtils.generateGridGraph( nodeMap, edgeMap, turnTables, 5, 5 );
        Graph filtered = instance.filter( g );
//        System.out.println( "filtered graph: " + filtered );
        nodes = filtered.getNodes();
        while ( nodes.hasNext() ) {
            ContractNode node = (ContractNode) nodes.next();
            for ( SimpleNode n : node.getNodes() ) {
//                System.out.println( "Graph does " + ( ( origNodes.contains( n ) ) ? "" : "NOT " ) + "contain node: node = " + n + ", graph = " + filtered );
                assertTrue( origNodes.contains( n ) );
                origNodes.remove( n );
            }
        }
        if ( !origNodes.isEmpty() ) {
//            System.out.println( "Orignodes conain more nodes[" + origNodes.size() + "]: " + origNodes );
        }
        assertTrue( origNodes.isEmpty() );

        int cellSize = 40;
        FilteredGraph expResult = null;
        FilteredGraph result = instance.filter( graph );
        System.out.println( "Comparison: orig{nodes=" + graph.getNodesCount() + ",edges=" + graph.getEdgeCount() + "}, filtered{nodes=" + result.getNodesCount() + ",edges=" + result.getEdgeCount() + "}" );
//
//        System.out.println( "Press enter to continue..." );
//        new Scanner( System.in ).nextLine();
    }

    private String toString( FilteredGraph graph ) {
        StringBuilder sb = new StringBuilder();
        return graph.toString();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphGeneratorUtils;
import java.util.ArrayList;
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
public class NaturalCutsFilterTest {
    
   private final UndirectedGraph graph;
    private final Map<Long, Node> nodeMap;
    private final Map<Long, Edge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public NaturalCutsFilterTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = GraphGeneratorUtils.generateGridGraph( nodeMap, edgeMap, turnTables, 5, 5);
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
        NaturalCutsFilter instance = new NaturalCutsFilter(0.5, 2, 8);
        FilteredGraph expResult = null;
        FilteredGraph result = instance.filter( graph );
        System.out.println( result );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }
    
    private String toString(FilteredGraph graph){
        StringBuilder sb = new StringBuilder();
        return graph.toString();
    }
}

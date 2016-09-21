/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.utils.GraphUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class RouteTest {

    public RouteTest() {
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
     * Test of builder method, of class Route.
     */
    @Test
    public void testBuilder() {
        System.out.println( "builder" );
        UndirectedGraph graph = new UndirectedGraph();
        SimpleNode a = graph.createNode( 0 );
        SimpleNode b = graph.createNode( 1 );
        SimpleNode c = graph.createNode( 2 );
        SimpleNode d = graph.createNode( 3 );
        SimpleNode e = graph.createNode( 4 );
        SimpleEdge ab = graph.createEdge( 0, false, a, b, 0, 1 );
        SimpleEdge bc = graph.createEdge( 1, false, c, b, 0, 1 );
        SimpleEdge cd = graph.createEdge( 2, false, c, d, 0, 1 );
        SimpleEdge de = graph.createEdge( 3, false, e, d, 0, 1 );

        Route.RouteBuilder builder = Route.builder();

        Route<SimpleNode, SimpleEdge> route = builder.addAsLast( cd ).addAsFirst( bc ).addAsLast( de ).addAsFirst( ab ).build();
        assertEquals( "Route{source=0,target=4,edges=[0,1,2,3]}", toString( route ) );
    }

    /**
     * Test of builder method, of class Route.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testBuilderOneway() {
        System.out.println( "builder_oneway" );
        UndirectedGraph graph = new UndirectedGraph();
        SimpleNode a = graph.createNode( 0 );
        SimpleNode b = graph.createNode( 1 );
        SimpleNode c = graph.createNode( 2 );
        SimpleNode d = graph.createNode( 3 );
        SimpleNode e = graph.createNode( 4 );
        SimpleEdge ab = graph.createEdge( 0, false, a, b, 0, 0 );
        SimpleEdge bc = graph.createEdge( 1, true, c, b, 0, 1 );
        SimpleEdge cd = graph.createEdge( 2, false, c, d, 1, 0 );
        SimpleEdge de = graph.createEdge( 3, false, e, d, 0, 1 );

        Route.RouteBuilder builder = Route.builder();

        Route route = builder.addAsLast( cd ).addAsFirst( bc ).addAsLast( de ).addAsFirst( ab ).build();

//        assertEquals( route.toString(), "Route(edges=[Edge(id=0, oneway=false, source=Node(id=0), target=Node(id=1), length=Distance(value=0.0)), Edge(id=1, oneway=false, source=Node(id=2), target=Node(id=1), length=Distance(value=0.0)), Edge(id=2, oneway=false, source=Node(id=2), target=Node(id=3), length=Distance(value=0.0)), Edge(id=3, oneway=false, source=Node(id=4), target=Node(id=3), length=Distance(value=0.0))], source=Node(id=0), target=Node(id=4))" );
    }

    private static <N extends Node, E extends Edge> String toString( Route<N, E> route ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Route{source=" ).append( route.getSource().getId() ).append( ",target=" ).append( route.getTarget().getId() ).append( ",edges=[" );
        for ( E edge : route.getEdges() ) {
            sb.append( edge.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]}" );
        return sb.toString();
    }

}

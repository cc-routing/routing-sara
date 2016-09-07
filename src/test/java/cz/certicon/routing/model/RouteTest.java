/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.SimpleEdge;
import java.util.ArrayList;
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
        Route.RouteBuilder builder = Route.builder();
        SimpleNode a = new SimpleNode( 0 );
        SimpleNode b = new SimpleNode( 1 );
        SimpleNode c = new SimpleNode( 2 );
        SimpleNode d = new SimpleNode( 3 );
        SimpleNode e = new SimpleNode( 4 );
        SimpleEdge ab = new SimpleEdge( 0, false, a, b, Distance.newInstance( 0 ) );
        SimpleEdge bc = new SimpleEdge( 1, false, c, b, Distance.newInstance( 0 ) );
        SimpleEdge cd = new SimpleEdge( 2, false, c, d, Distance.newInstance( 0 ) );
        SimpleEdge de = new SimpleEdge( 3, false, e, d, Distance.newInstance( 0 ) );

        Route route = builder.addAsLast( cd ).addAsFirst( bc ).addAsLast( de ).addAsFirst( ab ).build();
        assertEquals( "Route{source=0,target=4,edges=[0,1,2,3]}", toString( route ) );
    }

    /**
     * Test of builder method, of class Route.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testBuilderOneway() {
        System.out.println( "builder_oneway" );
        Route.RouteBuilder builder = Route.builder();
        SimpleNode a = new SimpleNode( 0 );
        SimpleNode b = new SimpleNode( 1 );
        SimpleNode c = new SimpleNode( 2 );
        SimpleNode d = new SimpleNode( 3 );
        SimpleNode e = new SimpleNode( 4 );
        SimpleEdge ab = new SimpleEdge( 0, false, a, b, Distance.newInstance( 0 ) );
        SimpleEdge bc = new SimpleEdge( 1, true, c, b, Distance.newInstance( 0 ) );
        SimpleEdge cd = new SimpleEdge( 2, false, c, d, Distance.newInstance( 0 ) );
        SimpleEdge de = new SimpleEdge( 3, false, e, d, Distance.newInstance( 0 ) );

        Route route = builder.addAsLast( cd ).addAsFirst( bc ).addAsLast( de ).addAsFirst( ab ).build();

//        assertEquals( route.toString(), "Route(edges=[Edge(id=0, oneway=false, source=Node(id=0), target=Node(id=1), length=Distance(value=0.0)), Edge(id=1, oneway=false, source=Node(id=2), target=Node(id=1), length=Distance(value=0.0)), Edge(id=2, oneway=false, source=Node(id=2), target=Node(id=3), length=Distance(value=0.0)), Edge(id=3, oneway=false, source=Node(id=4), target=Node(id=3), length=Distance(value=0.0))], source=Node(id=0), target=Node(id=4))" );
    }

    private static String toString( Route route ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Route{source=" ).append( route.getSource().getId() ).append( ",target=" ).append( route.getTarget().getId() ).append( ",edges=[" );
        for ( SimpleEdge edge : route.getEdges() ) {
            sb.append( edge.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]}" );
        return sb.toString();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.Edge;
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
        Node a = new Node( 0 );
        Node b = new Node( 1 );
        Node c = new Node( 2 );
        Node d = new Node( 3 );
        Node e = new Node( 4 );
        Edge ab = new Edge( 0, false, a, b, Distance.newInstance( 0 ) );
        Edge bc = new Edge( 1, false, c, b, Distance.newInstance( 0 ) );
        Edge cd = new Edge( 2, false, c, d, Distance.newInstance( 0 ) );
        Edge de = new Edge( 3, false, e, d, Distance.newInstance( 0 ) );

        Route route = builder.addAsLast( cd ).addAsFirst( bc ).addAsLast( de ).addAsFirst( ab ).build();
        assertEquals( route.toString(), "Route(edges=[Edge(id=0, oneway=false, source=Node(id=0), target=Node(id=1), length=Distance(value=0.0)), Edge(id=1, oneway=false, source=Node(id=2), target=Node(id=1), length=Distance(value=0.0)), Edge(id=2, oneway=false, source=Node(id=2), target=Node(id=3), length=Distance(value=0.0)), Edge(id=3, oneway=false, source=Node(id=4), target=Node(id=3), length=Distance(value=0.0))], source=Node(id=0), target=Node(id=4))" );
    }

    /**
     * Test of builder method, of class Route.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testBuilderOneway() {
        System.out.println( "builder_oneway" );
        Route.RouteBuilder builder = Route.builder();
        Node a = new Node( 0 );
        Node b = new Node( 1 );
        Node c = new Node( 2 );
        Node d = new Node( 3 );
        Node e = new Node( 4 );
        Edge ab = new Edge( 0, false, a, b, Distance.newInstance( 0 ) );
        Edge bc = new Edge( 1, true, c, b, Distance.newInstance( 0 ) );
        Edge cd = new Edge( 2, false, c, d, Distance.newInstance( 0 ) );
        Edge de = new Edge( 3, false, e, d, Distance.newInstance( 0 ) );

        Route route = builder.addAsLast( cd ).addAsFirst( bc ).addAsLast( de ).addAsFirst( ab ).build();

//        assertEquals( route.toString(), "Route(edges=[Edge(id=0, oneway=false, source=Node(id=0), target=Node(id=1), length=Distance(value=0.0)), Edge(id=1, oneway=false, source=Node(id=2), target=Node(id=1), length=Distance(value=0.0)), Edge(id=2, oneway=false, source=Node(id=2), target=Node(id=3), length=Distance(value=0.0)), Edge(id=3, oneway=false, source=Node(id=4), target=Node(id=3), length=Distance(value=0.0))], source=Node(id=0), target=Node(id=4))" );
    }

}

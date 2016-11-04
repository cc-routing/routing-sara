/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.UndirectedGraph;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class TestToStringUtils {

    UndirectedGraph graph;
    ToStringUtils_Test.UndirectedNodeCreator nc;
    ToStringUtils_Test.UndirectedEdgeCreator ec;

    public TestToStringUtils() {
    }

    @Before
    public void setUp() {
        graph = new UndirectedGraph();
        nc = new ToStringUtils_Test.UndirectedNodeCreator();
        ec = new ToStringUtils_Test.UndirectedEdgeCreator();
    }

    @Test
    public void graphToStringReturnsNameOnEmptyGraph() {
        String toString = ToStringUtils_Test.toString( graph );
        assertThat( toString, equalTo( "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[],edges=[]}" ) );
    }

    @Test
    public void graphToStringReturnsCorrectlyOnThreeNodesGraph_different() {
        graph.createNode( 7 );
        graph.createNode( 4 );
        graph.createNode( 5 );
        String toString = ToStringUtils_Test.toString( graph );
        assertThat( toString, equalTo( "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[4,5,7],edges=[]}" ) );
    }

    @Test
    public void graphToStringReturnsCorrectlyOnTwoNodesAndOneEdgeGraph() {
        graph.createNode( 7 );
        graph.createNode( 4 );
        graph.createEdge( 2, true, graph.getNodeById( 7 ), graph.getNodeById( 4 ), 0, 0 );
        String toString = ToStringUtils_Test.toString( graph );
        assertThat( toString, equalTo( "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[4,7],edges=[2{7->4}]}" ) );
    }

    @Test
    public void graphToStringReturnsCorrectlyOnThreeNodesAndOneTwowayAndOneOnewayEdgeGraph() {
        graph.createNode( 7 );
        graph.createNode( 1 );
        graph.createNode( 4 );
        graph.createEdge( 5, false, graph.getNodeById( 7 ), graph.getNodeById( 4 ), 0, 0 );
        graph.createEdge( 2, true, graph.getNodeById( 1 ), graph.getNodeById( 4 ), 0, 0 );
        String toString = ToStringUtils_Test.toString( graph );
        assertThat( toString, equalTo( "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[1,4,7],edges=[2{1->4},5{7<->4}]}" ) );
    }

    @Test
    public void graphFromStringReturnsEmptyGraph() {
        String g = "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[],edges=[]}";
        assertThat( ToStringUtils_Test.toString( ToStringUtils_Test.fromString( new UndirectedGraph(), g, nc, ec ) ), equalTo( g ) );
    }

    @Test
    public void graphFromStringReturnsGraphWithThreeNodes() {
        String g = "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[1,4,5],edges=[]}";
        assertThat( ToStringUtils_Test.toString( ToStringUtils_Test.fromString( new UndirectedGraph(), g, nc, ec ) ), equalTo( g ) );
    }

    @Test
    public void graphFromStringReturnsGraphWithThreeNodesAndTwoEdges() {
        String g = "cz.certicon.routing.model.graph.UndirectedGraph{nodes=[1,4,5],edges=[2{1->4},5{5<->4}]}";
        assertThat( ToStringUtils_Test.toString( ToStringUtils_Test.fromString( new UndirectedGraph(), g, nc, ec ) ), equalTo( g ) );
    }

}

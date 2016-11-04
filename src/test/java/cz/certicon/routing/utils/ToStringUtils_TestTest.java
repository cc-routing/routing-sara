/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.*;
import java8.util.stream.IntStream;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;

/**
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class ToStringUtils_TestTest {

    UndirectedGraph graph;
    ToStringUtils_Test.UndirectedNodeCreator nc;
    ToStringUtils_Test.UndirectedEdgeCreator ec;

    public ToStringUtils_TestTest() {
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

    /**************************************** OPTIMIZED *************************************************/

    @Test
    public void graph_toString_for_empty_graph_returns_empty_graph() throws Exception {
        OptimizedGraph g = new OptimizedGraph( 100, 100 );
        assertThat( ToStringUtils_Test.toString( g ), equalTo( "{nodes=[],edges=[]}" ) );
    }

    @Test
    public void graph_toString_for_1_node_graph_returns_1_node_graph() throws Exception {
        OptimizedGraph g = new OptimizedGraph( 100, 100 );
        g.createNode( 1 );
        assertThat( ToStringUtils_Test.toString( g ), equalTo( "{nodes=[1],edges=[]}" ) );
    }

    @Test
    public void graph_toString_for_3_nodes_graph_returns_3_nodes_graph() throws Exception {
        OptimizedGraph g = new OptimizedGraph( 100, 100 );
        g.createNode( 1 );
        g.createNode( 11 );
        g.createNode( 7 );
        assertThat( ToStringUtils_Test.toString( g ), equalTo( "{nodes=[1,7,11],edges=[]}" ) );
    }

    @Test
    public void graph_toString_for_3_nodes_2_edges_graph_returns_3_nodes_2_edges_graph() throws Exception {
        OptimizedGraph g = new OptimizedGraph( 100, 100 );
        g.createNode( 2 );
        g.createNode( 11 );
        g.createNode( 7 );
        int e1 = g.createEdge( 1, 2, 7, true, 0, 0 );
        g.setLength( e1, Metric.LENGTH, 1 );
        int e2 = g.createEdge( 2, 11, 7, false, 0, 1 );
        g.setLength( e2, Metric.LENGTH, 2 );
        assertThat( ToStringUtils_Test.toString( g ), equalTo( "{nodes=[2,7,11],edges=[1{2->7;1.0},2{11<->7;2.0}]}" ) );
    }


    @Test
    public void graph_fromString_for_emptyGraph_returns_emptyGraph() throws Exception {
        String g = "{nodes=[],edges=[]}";
        assertThat( ToStringUtils_Test.toString( ToStringUtils_Test.optimizedGraphFromString( g ) ), equalTo( g ) );
    }

    @Test
    public void graph_fromString_for_3_nodes_2_edges_returns_3_nodes_2_edges_graph() throws Exception {
        String g = "{nodes=[1,5,7],edges=[2{5->7;1.0:2.0},7{5<->1;2.0:3.0}]}";
        assertThat( ToStringUtils_Test.toString( ToStringUtils_Test.optimizedGraphFromString( g ) ), equalTo( g ) );
    }

}

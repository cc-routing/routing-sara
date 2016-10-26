package cz.certicon.routing.algorithm.sara.optimized.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by blaha on 26.10.2016.
 */
public class OptimizedGraphTest {

    OptimizedGraph graph;

    public OptimizedGraphTest() {
    }

    @Before
    public void setUp() throws Exception {
        graph = new OptimizedGraph( 100, 100 );
    }

    @Test
    public void createNode_for_5_returns_node_0_() throws Exception {
        assertThat( graph.createNode( 5L ), equalTo( 0 ) );
    }

    @Test
    public void after_createNode_5_graph_contains_node_5_() throws Exception {
        graph.createNode( 5 );
        assertThat( graph.containsNodeId( 5 ), equalTo( true ) );
    }

    @Test
    public void empty_graph_does_not_contain_node_5_() throws Exception {
        assertThat( graph.containsNodeId( 5 ), equalTo( false ) );
    }

    @Test
    public void createEdge_for_5_returns_edge_0_() throws Exception {
        graph.createNode( 1 );
        graph.createNode( 2 );
        graph.createNode( 3 );
        assertThat( graph.createEdge( 5, 1, 2, true ), equalTo( 0 ) );
    }

    @Test
    public void after_createEdge_5_graph_contains_edge_5_() throws Exception {
        graph.createNode( 1 );
        graph.createNode( 2 );
        graph.createNode( 3 );
        graph.createEdge( 5, 1, 2, true );
        assertThat( graph.containsEdgeId( 5 ), equalTo( true ) );
    }

    @Test
    public void empty_graph_does_not_contain_edge_5_() throws Exception {
        assertThat( graph.containsEdgeId( 5 ), equalTo( false ) );
    }

    @Test
    public void getEdges_after_adding_2_edges_returns_array_of_two() throws Exception {
        graph.createNode( 1 );
        graph.createNode( 2 );
        graph.createNode( 3 );
        graph.createEdge( 5, 1, 2, true );
        graph.createEdge( 6, 1, 3, true );
        assertThat( graph.getEdgeIds(), either( equalTo( new long[]{ 5, 6 } ) ).or( equalTo( new long[]{ 6, 5 } ) ) );
    }

    @Test
    public void getNodeById_returns_correct_order() throws Exception {
        int n7 = graph.createNode( 7 );
        int n11 = graph.createNode( 11 );
        int n1 = graph.createNode( 1 );
        assertThat( graph.getNodeById( 7L ), equalTo( n7 ) );
        assertThat( graph.getNodeById( 11L ), equalTo( n11 ) );
        assertThat( graph.getNodeById( 1L ), equalTo( n1 ) );
    }

    @Test
    public void getNodeId_returns_correct_id() throws Exception {
        int n7 = graph.createNode( 7 );
        int n11 = graph.createNode( 11 );
        int n1 = graph.createNode( 1 );
        assertThat( graph.getNodeId( n7 ), equalTo( 7L ) );
        assertThat( graph.getNodeId( n11 ), equalTo( 11L ) );
        assertThat( graph.getNodeId( n1 ), equalTo( 1L ) );
    }

    @Test
    public void getSource_returns_correct_source() throws Exception {
        int n1 = graph.createNode( 1 );
        int n7 = graph.createNode( 7 );
        int n5 = graph.createNode( 5 );
        int e1 = graph.createEdge( 1, 5, 7, true );
        assertThat( graph.getSource( e1 ), equalTo( n5 ) );
    }

    @Test
    public void getTarget_returns_correct_target() throws Exception {
        int n1 = graph.createNode( 1 );
        int n7 = graph.createNode( 7 );
        int n5 = graph.createNode( 5 );
        int e1 = graph.createEdge( 1, 5, 7, true );
        assertThat( graph.getTarget( e1 ), equalTo( n7 ) );
    }

    @Test
    public void isOneway_returns_true() throws Exception {
        int n7 = graph.createNode( 7 );
        int n5 = graph.createNode( 5 );
        int e1 = graph.createEdge( 1, 5, 7, true );
        assertThat( graph.isOneway( e1 ), equalTo( true ) );
    }

    @Test
    public void isOneway_returns_false() throws Exception {
        int n7 = graph.createNode( 7 );
        int n5 = graph.createNode( 5 );
        int e1 = graph.createEdge( 1, 5, 7, false );
        assertThat( graph.isOneway( e1 ), equalTo( false ) );
    }
}
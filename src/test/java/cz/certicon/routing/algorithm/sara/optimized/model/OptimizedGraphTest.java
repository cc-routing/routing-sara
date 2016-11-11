package cz.certicon.routing.algorithm.sara.optimized.model;

import cz.certicon.routing.model.graph.Metric;
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
        graph = new OptimizedGraph( 10, 10 );
        graph.createNode( 1 );
        graph.createNode( 2 );
        graph.createNode( 3 );
        graph.createEdge( 1, 1, 2, true, 0, 0 );
        graph.createEdge( 2, 1, 3, true, 1, 0 );
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
    public void after_createEdge_5_graph_contains_edge_5_() throws Exception {
        graph.createEdge( 5, 1, 2, true, 2, 1 );
        assertThat( graph.containsEdgeId( 5 ), equalTo( true ) );
    }

    @Test
    public void empty_graph_does_not_contain_edge_5_() throws Exception {
        assertThat( graph.containsEdgeId( 5 ), equalTo( false ) );
    }

    @Test
    public void getEdges_after_adding_2_edges_returns_array_of_two() throws Exception {
        assertThat( graph.getEdgeIds(), either( equalTo( new long[]{ 2, 1 } ) ).or( equalTo( new long[]{ 1, 2 } ) ) );
    }

    @Test
    public void getNodeById_returns_correct_idx() throws Exception {
        int n7 = graph.createNode( 7 );
        int n11 = graph.createNode( 11 );
        int n5 = graph.createNode( 5 );
        assertThat( graph.getNodeById( 7L ), equalTo( n7 ) );
        assertThat( graph.getNodeById( 11L ), equalTo( n11 ) );
        assertThat( graph.getNodeById( 5L ), equalTo( n5 ) );
    }

    @Test
    public void getNodeId_returns_correct_id() throws Exception {
        int n7 = graph.createNode( 7 );
        int n11 = graph.createNode( 11 );
        int n5 = graph.createNode( 5 );
        assertThat( graph.getNodeId( n7 ), equalTo( 7L ) );
        assertThat( graph.getNodeId( n11 ), equalTo( 11L ) );
        assertThat( graph.getNodeId( n5 ), equalTo( 5L ) );
    }

    @Test
    public void getEdgeId_returns_correct_id() throws Exception {
        int e7 = graph.createEdge( 7, 5, 1, true, 0, 2 );
        int e11 = graph.createEdge( 11, 5, 1, true, 1, 3 );
        int e5 = graph.createEdge( 5, 5, 1, true, 2, 4 );
        assertThat( graph.getEdgeId( e7 ), equalTo( 7L ) );
        assertThat( graph.getEdgeId( e11 ), equalTo( 11L ) );
        assertThat( graph.getEdgeId( e5 ), equalTo( 5L ) );
    }

    @Test
    public void getSource_returns_correct_source() throws Exception {
        int n5 = graph.createNode( 5 );
        int e1 = graph.createEdge( 3, 5, 1, true, 0, 2 );
        assertThat( graph.getSource( e1 ), equalTo( n5 ) );
    }

    @Test
    public void getTarget_returns_correct_target() throws Exception {
        int n7 = graph.createNode( 7 );
        int e1 = graph.createEdge( 3, 1, 7, true, 2, 0 );
        assertThat( graph.getTarget( e1 ), equalTo( n7 ) );
    }

    @Test
    public void isOneway_returns_true() throws Exception {
        int e1 = graph.createEdge( 3, 1, 2, true, 2, 1 );
        assertThat( graph.isOneway( e1 ), equalTo( true ) );
    }

    @Test
    public void isOneway_returns_false() throws Exception {
        int e1 = graph.createEdge( 3, 1, 2, false, 2, 1 );
        assertThat( graph.isOneway( e1 ), equalTo( false ) );
    }

    @Test
    public void getLength_after_setLength_5_returns_5() throws Exception {
        graph.setLength( 1, Metric.LENGTH, 5f );
        assertThat( graph.getDistance( 1, Metric.LENGTH ), equalTo( 5f ) );
    }

    @Test
    public void getOtherNode_for_edge_3_node_1_returns_2_and_the_other_way_around() throws Exception {
        int edge = graph.createEdge( 3, 1, 2, false, 2, 1 );
        int source = graph.getSource( edge );
        int target = graph.getTarget( edge );
        assertThat( graph.getOtherNode( edge, source ), equalTo( target ) );
        assertThat( graph.getOtherNode( edge, target ), equalTo( source ) );
    }

    @Test
    public void getOutgoingEdges_for_node_0_returns_array_0_and_1() throws Exception {
        assertThat( graph.getOutgoingEdges( 0 ), equalTo( new int[]{ 0, 1 } ) );
    }


    @Test
    public void getOutgoingEdges_for_node_1_returns_array_empty() throws Exception {
        assertThat( graph.getOutgoingEdges( 1 ), equalTo( new int[]{} ) );
    }

    @Test
    public void getIncomingEdges_for_node_0_returns_array_empty() throws Exception {
        assertThat( graph.getIncomingEdges( 0 ), equalTo( new int[]{} ) );
    }

    @Test
    public void getIncomingEdges_for_node_1_returns_array_of_0() throws Exception {
        assertThat( graph.getIncomingEdges( 1 ), equalTo( new int[]{ 0 } ) );
    }

    @Test
    public void getIncomingEdges_for_node_2_returns_array_of_1() throws Exception {
        assertThat( graph.getIncomingEdges( 2 ), equalTo( new int[]{ 1 } ) );
    }

    @Test
    public void getTurnDistance_for_1_return_1() throws Exception {
        OptimizedGraph g = new OptimizedGraph( 10, 10 );
        int n1 = g.createNode( 1 );
        int n2 = g.createNode( 2, new float[][]{ { Float.MAX_VALUE, 1.0f }, { 2.0f, Float.MAX_VALUE } } );
        int n3 = g.createNode( 3 );
        int e1 = g.createEdge( 1, 1, 2, false, 0, 0 );
        int e2 = g.createEdge( 2, 2, 3, false, 1, 0 );
        assertThat( g.getTurnDistance( n2, e1, e2 ), equalTo( 1.0f ) );
        assertThat( g.getTurnDistance( n2, e2, e1 ), equalTo( 2.0f ) );
    }
}
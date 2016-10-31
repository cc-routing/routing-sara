package cz.certicon.routing.algorithm.sara.optimized;

import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.algorithm.sara.optimized.model.Route;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.utils.ToStringUtils_Test;
import java8.util.Optional;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by blaha on 26.10.2016.
 */
public class MultilevelDijkstraTest {
    MultilevelDijkstra multilevelDijkstra;
    Metric metric = Metric.LENGTH;

    public MultilevelDijkstraTest() {
    }

    @Before
    public void setUp() throws Exception {
        multilevelDijkstra = new MultilevelDijkstra();

    }

    @Test
    public void from_node_A_to_node_A_returns_empty_route() throws Exception {
        OptimizedGraph graph = ToStringUtils_Test.optimizedGraphFromString( "{nodes=[1],edges=[]}" );
        Optional<Route> routeOptional = multilevelDijkstra.route( graph, graph.getNodeById( 1 ), graph.getNodeById( 1 ), metric );
        assertThat( routeOptional.isPresent(), equalTo( true ) );
        assertThat( routeOptional.get().getEdges(), equalTo( new long[0] ) );
    }

    @Test
    public void from_node_A_to_node_B_returns_AB_route() throws Exception {
        OptimizedGraph graph = new OptimizedGraph( 10, 10 );
        graph.createNode( 1 );
        graph.createNode( 2 );
        graph.createEdge( 1, 1, 2, true, 0, 0 );
        Optional<Route> routeOptional = multilevelDijkstra.route( graph, 1, 2, metric );
        assertThat( routeOptional.isPresent(), equalTo( true ) );
        assertThat( routeOptional.get().getEdges(), equalTo( new long[]{ 1 } ) );
    }
}
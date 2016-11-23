package cz.certicon.routing.algorithm.sara.optimized;

import cz.certicon.routing.algorithm.sara.optimized.data.OptimizedGraphDAO;
import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.algorithm.sara.optimized.model.Route;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.utils.ToStringUtils_Test;
import java8.util.Optional;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class MultilevelDijkstraTest {
    MultilevelDijkstra multilevelDijkstra;
    Metric metric = Metric.LENGTH;
    Properties properties;

    public MultilevelDijkstraTest() {
    }

    @Before
    public void setUp() throws Exception {
        multilevelDijkstra = new MultilevelDijkstra();
        properties = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream( "test.properties" );
        properties.load( in );
        in.close();
    }

//    @Test
//    public void from_node_A_to_node_A_returns_empty_route() throws Exception {
//        OptimizedGraph graph = ToStringUtils_Test.optimizedGraphFromString( "{nodes=[1],edges=[]}" );
//        Optional<Route> routeOptional = multilevelDijkstra.route( graph, graph.getNodeById( 1 ), graph.getNodeById( 1 ), metric );
//        assertThat( routeOptional.isPresent(), equalTo( true ) );
//        assertThat( routeOptional.get().getEdges(), equalTo( new long[]{} ) );
//    }

    @Test
    public void from_node_A_to_node_B_returns_AB_route() throws Exception {
        OptimizedGraph graph = new OptimizedGraph( 10, 10 );
        graph.createNode( 1 );
        graph.createNode( 2 );
        graph.createEdge( 1, 1, 2, true, 0, 0, Metric.LENGTH, 1.0f );
        Optional<Route> routeOptional = multilevelDijkstra.route( graph, 1, 2, metric );
        assertThat( routeOptional.isPresent(), equalTo( true ) );
        assertThat( routeOptional.get().getEdges(), equalTo( new long[]{ 1 } ) );
    }

    @Test
    public void from_node_A_to_node_C_returns_ABC_route() throws Exception {
        OptimizedGraph graph = new OptimizedGraph( 10, 10 );
        graph.createNode( 1, new float[2][2] );
        graph.createNode( 2, new float[2][2] );
        graph.createNode( 3, new float[2][2] );
        graph.createEdge( 1, 1, 2, true, 0, 0, Metric.LENGTH, 1.0f );
        graph.createEdge( 2, 3, 1, true, 0, 1, new Pair<>( Metric.LENGTH, 1.0f ) );
        graph.createEdge( 3, 2, 3, true, 1, 1, new Pair<>( Metric.LENGTH, 1.0f ) );
        Optional<Route> routeOptional = multilevelDijkstra.route( graph, 1, 3, metric );
        assertThat( routeOptional.isPresent(), equalTo( true ) );
        assertThat( routeOptional.get().getEdges(), equalTo( new long[]{ 1, 3 } ) );
    }


    @Test
    public void from_node_A_to_node_C_with_restrictions_returns_AC_route() throws Exception {
        OptimizedGraph graph = new OptimizedGraph( 10, 10 );
        graph.createNode( 1 );
        graph.createNode( 2, new float[][]{ { Float.MAX_VALUE, Float.MAX_VALUE }, { Float.MAX_VALUE, Float.MAX_VALUE } } );
        graph.createNode( 3 );
        graph.createEdge( 1, 1, 2, true, 0, 0, Metric.LENGTH, 1.0f );
        graph.createEdge( 2, 2, 3, true, 1, 0, Metric.LENGTH, 1.0f );
        graph.createEdge( 3, 1, 3, true, 1, 1, new Pair<>( Metric.LENGTH, 100.0f ) );
        Optional<Route> routeOptional = multilevelDijkstra.route( graph, 1, 3, metric );
        assertThat( routeOptional.isPresent(), equalTo( true ) );
        assertThat( routeOptional.get().getEdges(), equalTo( new long[]{ 3 } ) );
    }

    @Test
    public void real_prague_route() throws Exception {
        OptimizedGraphDAO dao = new OptimizedGraphDAO( properties );
        try {
            OptimizedGraph graph = dao.loadGraph();
            Optional<Route> route = multilevelDijkstra.route( graph, 419, 42882, Metric.LENGTH );
            long[] expected = new long[]{ 20468, 6609, 6610, 6584, 6583, 6582, 6581, 52370, 16966 };
            assertThat( route.isPresent(), equalTo( true ) );
            assertThat( Arrays.toString( route.get().getEdges() ), equalTo( Arrays.toString( expected ) ) );
        } catch ( IOException ex ) {
            System.err.println( "WARNING! COULD NOT TEST REAL PRAGUE ROUTE!" );
        }
    }
}
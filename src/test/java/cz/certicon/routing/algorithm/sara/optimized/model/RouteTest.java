package cz.certicon.routing.algorithm.sara.optimized.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class RouteTest {
    @Test
    public void getEdges() throws Exception {
        long[] edges = new long[]{ 1, 2, 3, 4 };
        Route route = new Route( edges );
        assertThat( route.getEdges(), equalTo( edges ) );
    }

    @Test
    public void builder_build_empty_returns_empty() throws Exception {
        Route.Builder builder = Route.builder();
        assertThat( builder.build().getEdges(), equalTo( new long[]{} ) );
    }

    @Test
    public void builder_build_1_edge_returns_1_edge() throws Exception {
        Route.Builder builder = Route.builder();
        builder.edge( 1 );
        assertThat( builder.build().getEdges(), equalTo( new long[]{ 1 } ) );
    }

    @Test
    public void builder_build_1_3_5_edge_returns_1_3_5() throws Exception {
        Route.Builder builder = Route.builder();
        builder.edge( 1 );
        builder.edge( 3 );
        builder.edge( 5 );
        assertThat( builder.build().getEdges(), equalTo( new long[]{ 1, 3, 5 } ) );
    }
    @Test
    public void builder_buildReverse_1_3_5_returns_5_3_1() throws Exception {
        Route.Builder builder = Route.builder();
        builder.edge( 1 );
        builder.edge( 3 );
        builder.edge( 5 );
        assertThat( builder.buildReverse().getEdges(), equalTo( new long[]{ 5, 3, 1 } ) );
    }
}
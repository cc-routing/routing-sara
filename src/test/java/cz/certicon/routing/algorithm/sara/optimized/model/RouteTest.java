package cz.certicon.routing.algorithm.sara.optimized.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by blaha on 31.10.2016.
 */
public class RouteTest {
    @Test
    public void getEdges() throws Exception {
        long[] edges = new long[]{ 1, 2, 3, 4 };
        Route route = new Route( edges );
        assertThat( route.getEdges(), equalTo( edges ) );
    }

}
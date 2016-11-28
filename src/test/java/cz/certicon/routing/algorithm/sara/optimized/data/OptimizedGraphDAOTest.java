package cz.certicon.routing.algorithm.sara.optimized.data;

import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.data.GraphDAO;
import cz.certicon.routing.data.SqliteGraphDAO;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.java8.IteratorStreams;
import cz.certicon.routing.utils.java8.Mappers;
import java8.util.J8Arrays;
import java8.util.function.*;
import java8.util.stream.Collectors;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class OptimizedGraphDAOTest {

    static Graph<Node<Node, Edge>, Edge<Node, Edge>> graph;
    static OptimizedGraph optimizedGraph;
    static Properties properties;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        properties = new Properties();
        InputStream in = OptimizedGraphDAOTest.class.getClassLoader().getResourceAsStream( "test.properties" );
        properties.load( in );
        in.close();
//        properties.setProperty( "driver", "org.sqlite.JDBC" );
//        properties.setProperty( "url", "jdbc:sqlite:C:\\Users\\blaha\\Documents\\NetBeansProjects\\RoutingParser\\routing_sara_prague.sqlite" );
//        properties.setProperty( "spatialite_path", "C:/Routing/Utils/mod_spatialite-4.3.0a-win-amd64/mod_spatialite.dll" );
        GraphDAO graphDAO = new SqliteGraphDAO( properties );
        graph = graphDAO.loadGraph();
    }

    @Before
    public void setUp() throws Exception {
        OptimizedGraphDAO optimizedGraphDAO = new OptimizedGraphDAO( properties );
        optimizedGraph = optimizedGraphDAO.loadGraph();
    }


    @Test
    public void match_nodes() throws Exception {
        assertThat( optimizedGraph.getNodeCount(), equalTo( graph.getNodesCount() ) );
        assertThat( optimizedGraph.getEdgeCount(), equalTo( graph.getEdgeCount() ) );
        for ( Node n : graph.getNodes() ) {
            assertThat( optimizedGraph.containsNodeId( n.getId() ), equalTo( true ) );
            final int nodeIdx = optimizedGraph.getNodeById( n.getId() );
            if(optimizedGraph.getEdges( nodeIdx ).length <= 1){
                continue; // ignore blind streets - turn-tables don't let anything to go through anyway
            }
            long[] outgoingExpected = IteratorStreams.stream( n.getOutgoingEdges() ).mapToLong( Mappers.identifiableToLong ).toArray();
            final int[] edges = optimizedGraph.getEdges( nodeIdx );
            long[] outgoingActual = J8Arrays.stream( edges )
                    .filter( new IntPredicate() {
                        @Override
                        public boolean test( int edgeIdx ) {
                            return !optimizedGraph.isOneway( edgeIdx ) || nodeIdx == optimizedGraph.getSource( edgeIdx );
//                            for ( int i = 0; i < edges.length; i++ ) {
//                                if ( optimizedGraph.getTurnDistance( nodeIdx, edges[i], edgeIdx ) < Float.MAX_VALUE ) {
//                                    return true;
//                                }
//                                if(optimizedGraph.getSource( edgeIdx ) == optimizedGraph.getTarget( edgeIdx )){
//                                    return true; // loop, cannot check...
//                                }
//                            }
//                            return false;
                        }
                    } )
                    .mapToLong( new IntToLongFunction() {
                        @Override
                        public long applyAsLong( int edgeIdx ) {
                            return optimizedGraph.getEdgeId( edgeIdx );
                        }
                    } ).toArray();
            assertThat( n.getId() + Arrays.toString( outgoingActual ), equalTo( n.getId() + Arrays.toString( outgoingExpected ) ) );
        }
    }

    @Test
    public void turn_tables_match() throws Exception {
        for ( Node n : graph.getNodes() ) {
            TurnTable expectedTurnTable = n.getTurnTable();
            int nodeIdx = optimizedGraph.getNodeById( n.getId() );
            float[][] actualTurnTable = optimizedGraph.getTurnTable( nodeIdx );
            assertThat( n.getId() + toString( actualTurnTable ), equalTo( n.getId() + toString( toMatrix( expectedTurnTable ) ) ) );
        }
    }

    private float[][] toMatrix( TurnTable tt ) {
        float[][] matrix = new float[tt.getSize()][tt.getSize()];
        for ( int i = 0; i < tt.getSize(); i++ ) {
            for ( int j = 0; j < tt.getSize(); j++ ) {
                if ( tt.getCost( i, j ).isInfinite() ) {
                    matrix[i][j] = Float.POSITIVE_INFINITY;
                } else {
                    matrix[i][j] = (float) tt.getCost( i, j ).getValue();
                }
            }
        }
        return matrix;
    }

    private String toString( float[][] matrix ) {
        return J8Arrays.stream( matrix ).map( new Function<float[], String>() {
            @Override
            public String apply( float[] floats ) {
                return Arrays.toString( floats );
            }
        } ).collect( Collectors.joining( "," ) );
    }
}
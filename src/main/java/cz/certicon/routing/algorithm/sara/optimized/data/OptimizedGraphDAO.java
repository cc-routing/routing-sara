package cz.certicon.routing.algorithm.sara.optimized.data;

import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.data.basic.database.SimpleDatabase;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.utils.GeometryUtils;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class OptimizedGraphDAO {
    private final SimpleDatabase database;

    /**
     * Constructor
     *
     * @param connectionProperties SQLite database connection properties
     */
    public OptimizedGraphDAO( Properties connectionProperties ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
    }

    /**
     * Loads {@link OptimizedGraph} from the database.
     *
     * @return loaded graph
     * @throws IOException thrown when an IO exception occurs
     */
    public OptimizedGraph loadGraph() throws IOException {
        try {
            ResultSet rs;
            rs = database.read( "SELECT COUNT(*) AS cnt FROM nodes" );
            int nodeCount = 0;
            if ( rs.next() ) {
                nodeCount = rs.getInt( "cnt" );
            }
            rs = database.read( "SELECT COUNT(*) AS cnt FROM edges" );
            int edgeCount = 0;
            if ( rs.next() ) {
                edgeCount = rs.getInt( "cnt" );
            }
            OptimizedGraph graph = new OptimizedGraph( nodeCount, edgeCount );
            // turn tables
            TIntObjectMap<float[][]> turnTables = new TIntObjectHashMap<>();
            rs = database.read( "SELECT * FROM turn_tables" );
            while ( rs.next() ) {
                int id = rs.getInt( "id" );
                int size = rs.getInt( "size" );
                float[][] turnTable = new float[size][size];
                turnTables.put( id, turnTable );
            }
            rs = database.read( "SELECT * FROM turn_table_values" );
            while ( rs.next() ) {
                int id = rs.getInt( "turn_table_id" );
                int rowIdx = rs.getInt( "row_id" );
                int columnIdx = rs.getInt( "column_id" );
                float value = (float) rs.getDouble( "value" );
                float[][] turnTable = turnTables.get( id );
                turnTable[rowIdx][columnIdx] = value;
            }
            // nodes
            rs = database.read( "SELECT * FROM nodes" );
            while ( rs.next() ) {
                graph.createNode( rs.getLong( "id" ), turnTables.get( rs.getInt( "turn_table_id" ) ) );
            }
            // edges
            rs = database.read( "SELECT * FROM edges" );
            while ( rs.next() ) {
                // length in meters, speed in kmph, CAUTION - convert here
                float length = (float) rs.getDouble( "metric_length" );
                double speedFw = rs.getDouble( "metric_speed_forward" );// todo take into consideration direction
                float time = (float) ( length / ( speedFw / 3.6 ) );
                graph.createEdge( rs.getLong( "id" ),
                        rs.getLong( "source" ),
                        rs.getLong( "target" ),
                        rs.getInt( "oneway" ) != 0,
                        rs.getInt( "source_pos" ),
                        rs.getInt( "target_pos" ),
                        Metric.LENGTH, length,
                        Metric.TIME, time
                );
            }
            database.close();
            return graph;
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.data.basic.database.SimpleDatabase;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.CoordinateUtils;
import cz.certicon.routing.utils.GeometryUtils;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SqliteGraphDAO implements GraphDAO {

    private final SimpleDatabase database;

    public SqliteGraphDAO( Properties connectionProperties ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
    }

    @Override
    public void saveGraph( Graph graph ) throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Graph loadGraph() throws IOException {
        try {
            ResultSet rs;
//            rs = database.read( "SELECT COUNT(*) AS nodeCount FROM nodes" );
//            if ( !rs.next() ) {
//                throw new SQLException( "Could not read node count" );
//            }
//            int nodeCount = rs.getInt( "nodeCount" );
//            rs = database.read( "SELECT COUNT(*) AS edgeCount FROM edges" );
//            if ( !rs.next() ) {
//                throw new SQLException( "Could not read edge count" );
//            }
//            int edgeCount = rs.getInt( "edgeCount" );
            // read turn tables
            // TODO add turntables to map, a list of nodes as a value (so that the nodes share turntables)
            TIntObjectMap<MatrixContainer> turnTableMap = new TIntObjectHashMap<>();
            rs = database.read( "SELECT * FROM turn_tables" );
            while ( rs.next() ) {
                turnTableMap.put( rs.getInt( "id" ), new MatrixContainer( rs.getInt( "size" ) ) );
            }
            rs = database.read( "SELECT * FROM turn_table_values" );
            while ( rs.next() ) {
                MatrixContainer matrixContainer = turnTableMap.get( rs.getInt( "turn_table_id" ) );
                matrixContainer.matrix[rs.getInt( "row_id" )][rs.getInt( "column_id" )] = Distance.newInstance( rs.getDouble( "value" ) );
            }
            // read nodes
            TLongObjectMap<SimpleNode> nodeMap = new TLongObjectHashMap<>();
            rs = database.read( "SELECT *, ST_AsText(geom) AS point FROM nodes" );
            while ( rs.next() ) {
                SimpleNode node = new SimpleNode( rs.getLong( "id" ) );
                TurnTable turnTable = new TurnTable( turnTableMap.get( rs.getInt( "turn_table_id" ) ).matrix );
                node.setTurnTable( turnTable );
                node.setCoordinate( GeometryUtils.toCoordinatesFromWktPoint( rs.getString( "point" ) ) );
                nodeMap.put( node.getId(), node );
            }
            // read edges
            TLongObjectMap<Edge> edgeMap = new TLongObjectHashMap<>();
            Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
            for ( Metric value : Metric.values() ) {
                metricMap.put( value, new HashMap<Edge, Distance>() );
            }
            rs = database.read( "SELECT * FROM edges e;" );
            while ( rs.next() ) {
                SimpleEdge edge = new SimpleEdge( rs.getLong( "id" ), rs.getInt( "oneway" ) != 0,
                        nodeMap.get( rs.getLong( "source" ) ),
                        nodeMap.get( rs.getLong( "target" ) ),
                        rs.getInt( "source_pos" ),
                        rs.getInt( "target_pos" ) );
                // length in meters, speed in kmph, CAUTION - convert here
                double length = rs.getDouble( "metric_length" );
                double speedFw = rs.getDouble( "metric_speed_forward" );// todo take into consideration direction
                double time = length / ( speedFw / 3.6 );
                metricMap.get( Metric.LENGTH ).put( edge, Distance.newInstance( length ) );
                metricMap.get( Metric.TIME ).put( edge, Distance.newInstance( time ) );
                edgeMap.put( edge.getId(), edge );
            }
            // lock nodes and build graph
            for ( Object value : nodeMap.values() ) {
                SimpleNode node = (SimpleNode) value;
                node.lock();
            }
            return new UndirectedGraph<>( nodeMap, edgeMap, metricMap );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    private static class MatrixContainer {

        Distance[][] matrix;

        public MatrixContainer( int size ) {
            matrix = new Distance[size][size];
        }

    }

}

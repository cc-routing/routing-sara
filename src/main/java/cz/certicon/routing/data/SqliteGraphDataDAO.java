/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.data.basic.database.SimpleDatabase;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.values.Length;
import cz.certicon.routing.model.values.LengthUnits;
import cz.certicon.routing.model.values.Time;
import cz.certicon.routing.model.values.TimeUnits;
import cz.certicon.routing.utils.GeometryUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class SqliteGraphDataDAO implements GraphDataDao {

    private final SimpleDatabase database;

    public SqliteGraphDataDAO( Properties connectionProperties ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
    }

    @Override
    public NodeData loadNodeData( long nodeId ) throws IOException {
        try {
            ResultSet rs;
            long tableId;
            TLongArrayList cellIds = new TLongArrayList();
            long parent;
            int tableSize;
            Coordinate coordinate;
            Distance[][] matrix;
            rs = database.read( "SELECT n.*, ST_AsText(n.geom) AS point, t.size FROM nodes n JOIN turn_tables t ON n.turn_table_id = t.id WHERE n.id = " + nodeId );
            if ( rs.next() ) {
                tableId = rs.getLong( "turn_table_id" );
                tableSize = rs.getInt( "size" );
                matrix = new Distance[tableSize][tableSize];
                parent = rs.getLong( "cell_id" );
                coordinate = GeometryUtils.toCoordinatesFromWktPoint( rs.getString( "point" ) );
            } else {
                throw new IllegalArgumentException( "Unknown node: " + nodeId );
            }
            while ( parent >= 0 ) {
                cellIds.add( parent );
                rs = database.read( "SELECT * FROM cells WHERE id = " + parent );
                if ( rs.next() ) {
                    parent = rs.getLong( "parent" );
                } else {
                    parent = -1;
                }
            }
            rs = database.read( "SELECT * FROM turn_table_values WHERE turn_table_id = " + tableId );
            while ( rs.next() ) {
                matrix[rs.getInt( "row_id" )][rs.getInt( "column_id" )] = Distance.newInstance( rs.getDouble( "value" ) );
            }
            TLongList incomingEdges = new TLongArrayList();
            rs = database.read( "SELECT id FROM edges WHERE target = " + nodeId );
            while ( rs.next() ) {
                incomingEdges.add( rs.getLong( "id" ) );
            }
            TLongList outgoingEdges = new TLongArrayList();
            rs = database.read( "SELECT id FROM edges WHERE source = " + nodeId );
            while ( rs.next() ) {
                outgoingEdges.add( rs.getLong( "id" ) );
            }
            return new NodeData( coordinate, nodeId, cellIds.toArray(), tableSize, incomingEdges, outgoingEdges, new TurnTable( matrix ) );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public EdgeData loadEdgeData( long edgeId ) throws IOException {
        try {
            ResultSet rs;
            rs = database.read( "SELECT *,ST_AsText(geom) AS edge_geom FROM edges e WHERE id = " + edgeId );
            if ( rs.next() ) {
                long sourceId = rs.getLong( "source" );
                long targetId = rs.getLong( "target" );
                // length in meters, speed in kmph, CAUTION - convert here
                double length = rs.getDouble( "metric_length" );
                double speedFw = rs.getDouble( "metric_speed_forward" );// todo take into consideration direction
                double time = length / ( speedFw / 3.6 );
                List<Coordinate> coordinates = GeometryUtils.toCoordinatesFromWktLinestring( rs.getString( "edge_geom" ) );
                Map<Metric, Distance> distanceMap = new HashMap<>();
                distanceMap.put( Metric.LENGTH, Distance.newInstance( length ) );
                distanceMap.put( Metric.TIME, Distance.newInstance( time ) );
                return new EdgeData( coordinates, edgeId, sourceId, targetId, distanceMap );
            } else {
                throw new IllegalArgumentException( "Unknown edge: " + edgeId );
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

}

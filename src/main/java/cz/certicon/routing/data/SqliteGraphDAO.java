/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.data.basic.database.SimpleDatabase;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.CoordinateUtils;
import cz.certicon.routing.utils.DatabaseUtils;
import cz.certicon.routing.utils.GeometryUtils;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the {@link GraphDAO} interface for accessing SQLite database
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class SqliteGraphDAO implements GraphDAO {

    private static final int BATCH_SIZE = 200;

    private final SimpleDatabase database;
    private final boolean rewrite;

    /**
     * Constructor
     *
     * @param connectionProperties SQLite database connection properties
     */
    public SqliteGraphDAO( Properties connectionProperties ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
        this.rewrite = true;
    }

    /**
     * Constructor
     *
     * @param connectionProperties SQLite database connection properties
     * @param rewrite              rewrite database on save
     */
    public SqliteGraphDAO( Properties connectionProperties, boolean rewrite ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
        this.rewrite = rewrite;
    }

    @Override
    public void saveGraph( Graph graph ) throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveGraph( SaraGraph graph ) throws IOException {
        if ( !DatabaseUtils.columnExists( database, "nodes", "cell_id" ) ) {
            database.write( "ALTER TABLE nodes ADD cell_id INTEGER DEFAULT(-1)" );
        }
        boolean tableExists = DatabaseUtils.tableExists( database, "cells" );
        if ( rewrite && tableExists ) {
            database.write( "DROP TABLE IF EXISTS cells" );
            database.write( "DROP INDEX IF EXISTS `idx_id_cells`" );
        }
        if ( rewrite || !tableExists ) {
            database.write( "CREATE TABLE cells (id INTEGER, parent INTEGER)" );
        }
        try {
            PreparedStatement nodeStatement = database.preparedStatement( "UPDATE nodes SET cell_id = ? WHERE id = ?" );
            PreparedStatement cellStatement = database.preparedStatement( "INSERT INTO cells (id,parent) VALUES (?,?)" );
            int nodeCounter = 0;
            int cellCounter = 0;
            TLongSet set = new TLongHashSet();
            for ( SaraNode node : graph.getNodes() ) {
                Cell cell = node.getParent();
//                System.out.println( "Node#" + node.getId() );
                nodeStatement.setLong( 1, cell.getId() );
                nodeStatement.setLong( 2, node.getId() );
                nodeStatement.addBatch();
//                System.out.println( "Batch added. Nodes count: " + nodeCounter );
                if ( ++nodeCounter % BATCH_SIZE == 0 ) {
                    nodeStatement.executeBatch();
//                    System.out.println( "Done saving: #" + nodeCounter  );
                }
                while ( cell != null ) {
                    if ( set.contains( cell.getId() ) ) { // multiple nodes belong to the same cell! Do not add it again.
                        break;
                    }
                    set.add( cell.getId() );
                    cellStatement.setLong( 1, cell.getId() );
                    cellStatement.setLong( 2, cell.hasParent() ? cell.getParent().getId() : -1 );
                    cellStatement.addBatch();
                    if ( ++cellCounter % BATCH_SIZE == 0 ) {
                        cellStatement.executeBatch();
                    }
                    cell = cell.getParent();
                }
            }
            nodeStatement.executeBatch();
            cellStatement.executeBatch();
            if ( rewrite || !tableExists ) {
                database.write( "CREATE UNIQUE INDEX `idx_id_cells` ON `cells` (`id` DESC)" );
            }
            database.close();
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
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
            UndirectedGraph graph = new UndirectedGraph( EnumSet.of( Metric.LENGTH, Metric.TIME ) );
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
            rs = database.read( "SELECT *, ST_AsText(geom) AS point FROM nodes" );
            while ( rs.next() ) {
                SimpleNode node = graph.createNode( rs.getLong( "id" ) );
                TurnTable turnTable = new TurnTable( turnTableMap.get( rs.getInt( "turn_table_id" ) ).matrix );
                node.setTurnTable( turnTable );
                node.setCoordinate( GeometryUtils.toCoordinatesFromWktPoint( rs.getString( "point" ) ) );
            }
            // read edges
            Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
            for ( Metric value : Metric.values() ) {
                metricMap.put( value, new HashMap<Edge, Distance>() );
            }
            rs = database.read( "SELECT * FROM edges e;" );
            while ( rs.next() ) {
                SimpleNode source = graph.getNodeById( rs.getLong( "source" ) );
                SimpleNode target = graph.getNodeById( rs.getLong( "target" ) );
                // length in meters, speed in kmph, CAUTION - convert here
                double length = rs.getDouble( "metric_length" );
                double speedFw = rs.getDouble( "metric_speed_forward" );// todo take into consideration direction
                double time = length / ( speedFw / 3.6 );
                SimpleEdge edge = graph.createEdge( rs.getLong( "id" ), rs.getInt( "oneway" ) != 0,
                        source,
                        target,
                        rs.getInt( "source_pos" ),
                        rs.getInt( "target_pos" ),
                        new Pair<>( Metric.LENGTH, Distance.newInstance( length ) ), new Pair<>( Metric.TIME, Distance.newInstance( time ) ) );
            }
            // lock nodes and build graph
            graph.lock();
            database.close();
            return graph;
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public SaraGraph loadSaraGraph() throws IOException {
        try {
            ResultSet rs;
            SaraGraph graph = new SaraGraph( EnumSet.of( Metric.LENGTH, Metric.TIME ) );
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
            TLongObjectMap<Cell> cellMap = new TLongObjectHashMap<>();
            rs = database.read( "SELECT *, ST_AsText(geom) AS point FROM nodes" );
            while ( rs.next() ) {
                long cellId = rs.getLong( "cell_id" );
                Cell cell;
                if ( cellMap.containsKey( cellId ) ) {
                    cell = cellMap.get( cellId );
                } else {
                    cell = new Cell( cellId );
                    cellMap.put( cellId, cell );
                }
                SaraNode node = graph.createNode( rs.getLong( "id" ), cell );
                TurnTable turnTable = new TurnTable( turnTableMap.get( rs.getInt( "turn_table_id" ) ).matrix );
                node.setTurnTable( turnTable );
                node.setCoordinate( GeometryUtils.toCoordinatesFromWktPoint( rs.getString( "point" ) ) );
            }
            // read cells            
            rs = database.read( "SELECT * FROM cells c ORDER BY c.id DESC" );
            while ( rs.next() ) {
                long cellId = rs.getLong( "id" );
                long parentId = rs.getLong( "parent" );
                Cell cell;
                if ( cellMap.containsKey( cellId ) ) {
                    cell = cellMap.get( cellId );
                } else {
                    cell = new Cell( cellId );
                    cellMap.put( cellId, cell );
                }
                if ( parentId >= 0 ) {
                    if ( !cellMap.containsKey( parentId ) ) {
                        throw new IllegalStateException( "Map does not contain parent: #" + parentId + " for: #" + cellId );
                    }
                    cell.setParent( cellMap.get( parentId ) );
                    cell.lock();
                } else {
                    cell.lock();
                }
            }
            // read edges
            Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
            metricMap.put( Metric.LENGTH, new HashMap<Edge, Distance>() );
            metricMap.put( Metric.TIME, new HashMap<Edge, Distance>() );
            rs = database.read( "SELECT * FROM edges e;" );
            while ( rs.next() ) {
                SaraNode source = graph.getNodeById( rs.getLong( "source" ) );
                SaraNode target = graph.getNodeById( rs.getLong( "target" ) );
                // length in meters, speed in kmph, CAUTION - convert here
                double length = rs.getDouble( "metric_length" );
                double speedFw = rs.getDouble( "metric_speed_forward" );// todo take into consideration direction
                double time = length / ( speedFw / 3.6 );
                SaraEdge edge = graph.createEdge( rs.getLong( "id" ), rs.getInt( "oneway" ) != 0,
                        source,
                        target,
                        rs.getInt( "source_pos" ),
                        rs.getInt( "target_pos" ),
                        new Pair<>( Metric.LENGTH, Distance.newInstance( length ) ), new Pair<>( Metric.TIME, Distance.newInstance( time ) ) );
            }
            // lock nodes and build graph
            graph.lock();
            database.close();
            return graph;
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

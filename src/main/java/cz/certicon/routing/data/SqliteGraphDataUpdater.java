/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.data.basic.database.SimpleDatabase;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class SqliteGraphDataUpdater implements GraphDataUpdater {

    private static final int BATCH_SIZE = 200;

    private final SimpleDatabase database;

    public SqliteGraphDataUpdater( Properties connectionProperties ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
    }

    @Override
    public void deleteIsolatedAreas( GraphDeleteMessenger graphDeleteMessenger ) throws IOException {
        try {
            PreparedStatement nodePreparedStatement = database.preparedStatement( "DELETE FROM nodes WHERE id = ?" );
            int nodeCounter = 0;
            for ( long nodeId : graphDeleteMessenger.getNodeIds() ) {
//                System.out.println( "Node#" + node.getId() );
                nodePreparedStatement.setLong( 1, nodeId );
                nodePreparedStatement.addBatch();
                if ( ++nodeCounter % BATCH_SIZE == 0 ) {
                    nodePreparedStatement.executeBatch();
                }
            }
            nodePreparedStatement.executeBatch();
            PreparedStatement edgePreparedStatement = database.preparedStatement( "DELETE FROM edges WHERE id = ?" );
            int edgeCounter = 0;
            for ( long edgeId : graphDeleteMessenger.getEdgeIds() ) {
//                System.out.println( "Node#" + node.getId() );
                edgePreparedStatement.setLong( 1, edgeId );
                edgePreparedStatement.addBatch();
                if ( ++edgeCounter % BATCH_SIZE == 0 ) {
                    edgePreparedStatement.executeBatch();
                }
            }
            edgePreparedStatement.executeBatch();
            database.close();
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }

    }

}

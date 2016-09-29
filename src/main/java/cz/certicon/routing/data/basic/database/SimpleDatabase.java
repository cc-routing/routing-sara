/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data.basic.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An extension to the {@link SimpleDatabase}, which is read-only and returns
 * {@link ResultSet} based on the given query (String).
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SimpleDatabase {

    private final AbstractDatabase<ResultSet, String, String, String> database;
    private boolean batch = false;

    private SimpleDatabase( AbstractDatabase<ResultSet, String, String, String> database ) {
        this.database = database;
    }

    public static SimpleDatabase newSqliteDatabase( Properties connectionProperties ) {
        return new SimpleDatabase( new AbstractSqliteDatabase<ResultSet, String, String, String>( connectionProperties ) {
            @Override
            protected ResultSet checkedRead( String in ) throws SQLException, IOException {
                return getStatement().executeQuery( in );
            }

            @Override
            protected void checkedWrite( String out, String additionalData ) throws SQLException, IOException {
                getStatement().execute( out );
            }
        } );
    }

    public static SimpleDatabase newEmbeddedDatabase( Properties connectionProperties ) {
        return new SimpleDatabase( new AbstractEmbeddedDatabase<ResultSet, String, String, String>( connectionProperties ) {
            @Override
            protected ResultSet checkedRead( String in ) throws SQLException, IOException {
                return getStatement().executeQuery( in );
            }

            @Override
            protected void checkedWrite( String out, String additionalData ) throws SQLException, IOException {
                getStatement().execute( out );
            }
        } );
    }

    public static SimpleDatabase newServerDatabase( Properties connectionProperties ) {
        return new SimpleDatabase( new AbstractServerDatabase<ResultSet, String, String, String>( connectionProperties ) {
            @Override
            protected ResultSet checkedRead( String in ) throws SQLException, IOException {
                return getStatement().executeQuery( in );
            }

            @Override
            protected void checkedWrite( String out, String additionalData ) throws SQLException, IOException {
                getStatement().execute( out );
            }
        } );
    }

    /**
     * Returns {@link ResultSet} based on the given query.
     *
     * @param sql SQL query
     * @return result set
     * @throws IOException thrown when an SQL or IO exception appears
     */
    public ResultSet read( String sql ) throws IOException {
        setBatch( false );
        return database.read( sql );
    }

    /**
     * Writes given command
     *
     * @param command SQL command
     * @throws IOException thrown when an SQL or IO exception appears
     */
    public void write( String command ) throws IOException {
        setBatch( false );
        database.write( command, null );
    }

    /**
     * Closes the database connection
     *
     * @throws IOException thrown when an SQL or IO exception appears
     */
    public void close() throws IOException {
        setBatch( false );
        database.close();
    }

    /**
     * Creates PreparedStatement
     *
     * @param sql query
     * @return prepared statement
     * @throws java.io.IOException
     */
    public PreparedStatement preparedStatement( String sql ) throws IOException {
        setBatch( true );
        try {
            return database.getConnection().prepareStatement( sql );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    private void setBatch( boolean on ) throws IOException {
        if(!database.isOpen()){
            database.open();
        }
        try {
            if ( on && !batch ) {
                database.getConnection().setAutoCommit( false );
                batch = true;
            } else if ( !on && batch ) {
                database.getConnection().commit();
                database.getConnection().setAutoCommit( true );
                batch = false;
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

}

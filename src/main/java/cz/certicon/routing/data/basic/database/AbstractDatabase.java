/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data.basic.database;

import cz.certicon.routing.data.basic.Reader;
import cz.certicon.routing.data.basic.Writer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * An abstract implementation of the {@link Reader}/{@link Writer} interfaces
 * for the database access. Encapsulates database access (connection creating),
 * controls the state before reading/writing and opens the connection if
 * necessary.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <In> output of the reader (type to be read)
 * @param <InData> additional data for the reader (if it requires any)
 * @param <Out> type to be written
 * @param <OutData> additional data
 */
public abstract class AbstractDatabase<In, Out, InData, OutData> implements Reader<In, InData>, Writer<Out, OutData> {

    private Statement statement;
    private Connection connection;
    private boolean isOpened = false;
    private Properties connectionProperties;

    public AbstractDatabase( Properties connectionProperties ) {
        this.connectionProperties = connectionProperties;
    }

    @Override
    public void open() throws IOException {
//        if ( connectionProperties == null ) {
//            InputStream in = getClass().getClassLoader().getResourceAsStream( "cz/certicon/routing/data/basic/database/database_connection.properties" );
//            connectionProperties = new Properties();
//            connectionProperties.load( in );
//            in.close();
//        }
        try {
            if ( !isOpened || connection.isClosed() ) {
                connection = createConnection( connectionProperties );
                statement = connection.createStatement();
                isOpened = true;
            }
        } catch ( ClassNotFoundException | SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public In read( InData in ) throws IOException {
        if ( !isOpen() ) {
            open();
        }
        try {
            return checkedRead( in );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void write( Out out, OutData additionalData ) throws IOException {
        if ( !isOpen() ) {
            open();
        }
        try {
            checkedWrite( out, additionalData );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    protected Statement getStatement() {
        return statement;
    }

    protected Connection getConnection() {
        return connection;
    }

    /**
     * Checks the state before reading and opens the source if necessary.
     *
     * @param additionalData additional data (passed)
     * @return read entity
     * @throws SQLException database exception
     * @throws java.io.IOException other IO exception
     */
    abstract protected In checkedRead( InData additionalData ) throws SQLException, IOException;

    /**
     * Checks the state before writing and opens the target if necessary.
     *
     * @param out the entity to be written
     * @param additionalData additional data for the output
     * @throws SQLException database exception
     * @throws java.io.IOException other IO exception
     */
    abstract protected void checkedWrite( Out out, OutData additionalData ) throws SQLException, IOException;

    @Override
    public void close() throws IOException {
        if ( isOpened ) {
            try {
                statement.close();
                connection.close();
            } catch ( SQLException ex ) {
                throw new IOException( ex );
            }
            isOpened = false;
        }
    }

    @Override
    public boolean isOpen() {
        return isOpened;
    }

    /**
     * Create new {@link Connection} based on given connection
     * {@link Properties}
     *
     * @param properties connection data
     * @return an instance of {@link Connection}
     * @throws ClassNotFoundException thrown when the driver is not found
     * @throws SQLException thrown when the connection cannot be established for
     * some reason
     */
    protected abstract Connection createConnection( Properties properties ) throws ClassNotFoundException, SQLException;

}

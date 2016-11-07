/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.data.basic.database.SimpleDatabase;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utilities for database
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class DatabaseUtils {

    /**
     * Returns whether a column having the given column name exists
     *
     * @param database   database access
     * @param tableName  table name
     * @param columnName column name
     * @return true if the column exists, false otherwise
     * @throws IOException thrown when an IO exception occurs
     */
    public static boolean columnExists( SimpleDatabase database, String tableName, String columnName ) throws IOException {
        try {
            ResultSet rs = database.read( "SELECT * FROM " + tableName + " LIMIT 1" );
            if ( !rs.next() ) {
                throw new IllegalStateException( "Database is empty!" );
            } else {
                try {
                    int findColumn = rs.findColumn( columnName );
                    return findColumn >= 0;
                } catch ( SQLException ex ) {
                    return false;
                }
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    /**
     * Returns whether a table having the given table name exists
     *
     * @param database  database access
     * @param tableName table name
     * @return true if the table exists, false otherwise
     * @throws IOException thrown when an IO exception occurs
     */
    public static boolean tableExists( SimpleDatabase database, String tableName ) throws IOException {
        try {
            ResultSet rs = database.read( "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';" );
            return rs.next();
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }
}

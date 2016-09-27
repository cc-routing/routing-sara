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
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class DatabaseUtils {

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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.data.basic.database.SimpleDatabase;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RouteData;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Length;
import cz.certicon.routing.model.values.LengthUnits;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Time;
import cz.certicon.routing.model.values.TimeUnits;
import cz.certicon.routing.utils.GeometryUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SqliteRouteDAO implements RouteDataDAO {

    private final SimpleDatabase database;

    public SqliteRouteDAO( Properties connectionProperties ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
    }

    @Override
    public <N extends Node, E extends Edge> void saveRouteData( Route<N, E> route, RouteData<E> routeData ) throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <N extends Node, E extends Edge> RouteData<E> loadRouteData( Route<N, E> route ) throws IOException {
        Map<E, List<Coordinate>> coordinateMap = new HashMap<>();
        Length length = new Length( LengthUnits.METERS, 0 );
        Time time = new Time( TimeUnits.SECONDS, 0 );
        ResultSet rs;
        N node = route.getSource();
        for ( E edge : route.getEdges() ) {
            try {
                rs = database.read( "SELECT ST_AsText(geom) AS geom FROM edges WHERE id = " + edge.getId() + ";" );
                if ( rs.next() ) {
                    List<Coordinate> coordinates = GeometryUtils.toCoordinatesFromWktLinestring( rs.getString( "geom" ) );
                    if ( node.equals( edge.getTarget() ) ) {
                        Collections.reverse( coordinates );
                        node = (N) edge.getSource();
                    } else {
                        node = (N) edge.getTarget();
                    }
                    coordinateMap.put( edge, coordinates );
                    length.add( new Length( LengthUnits.METERS, (long) edge.getLength( Metric.LENGTH ).getValue() ) );
                    time.add( new Time( TimeUnits.SECONDS, (long) edge.getLength( Metric.TIME ).getValue() ) );
                }
            } catch ( SQLException ex ) {
                throw new IOException( ex );
            }
        }
        return new RouteData<>( coordinateMap, length, time );
    }

}

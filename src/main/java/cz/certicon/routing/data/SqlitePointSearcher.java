package cz.certicon.routing.data;

import cz.certicon.routing.data.basic.database.SimpleDatabase;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class SqlitePointSearcher implements PointSearcher {

    private static final double DISTANCE_INIT = 0.001;
    private static final double DISTANCE_MULTIPLIER = 10;
    private final SimpleDatabase database;

    public SqlitePointSearcher( Properties connectionProperties ) {
        database = SimpleDatabase.newSqliteDatabase( connectionProperties );
    }

    @Override
    public PointSearcher.PointSearchResult findClosestPoint( Coordinate coordinates, Set<Metric> metrics ) throws IOException {
        Map<Metric, Distance> toSourceMap = new EnumMap<>( Metric.class );
        Map<Metric, Distance> toTargetMap = new EnumMap<>( Metric.class );
        final String pointString = "ST_GeomFromText('POINT(" + coordinates.getLongitude() + " " + coordinates.getLatitude() + ")',4326)";
        final String keyDistanceFromStart = "distance_from_start";
        final String keyDistanceToEnd = "distance_to_end";
        try {
            ResultSet rs = database.read( "SELECT n.id FROM nodes n "
                    + "WHERE n.ROWID IN( "
                    + "    SELECT ROWID FROM SpatialIndex "
                    + "    WHERE f_table_name = 'nodes' "
                    + "    AND search_frame = BuildCircleMbr(" + coordinates.getLongitude() + ", " + coordinates.getLatitude() + " , " + DISTANCE_INIT + "  ,4326)"
                    + ") "
                    + "AND ST_Equals("
                    + "    ST_SnapToGrid(" + pointString + ", 0.000001),"
                    + "    ST_SnapToGrid(n.geom, 0.000001)"
                    + ")" );
            if ( rs.next() ) { // for all nodes found
                long edgeId = -1;
                long nodeId = rs.getLong( "id" );
                for ( Metric metric : metrics ) {
                    toSourceMap.put( metric, Distance.newInstance( 0 ) );
                    toTargetMap.put( metric, Distance.newInstance( 0 ) );
                }
                return new PointSearcher.PointSearchResult( edgeId, nodeId, toSourceMap, toTargetMap );
            }
            double distance = DISTANCE_INIT;
            while ( true ) {
                rs = database.read(
                        "SELECT * "
                                + ", ST_Length(ST_Line_Substring(e.geom, 0, ST_Line_Locate_Point(e.geom, x.point)), 1) AS " + keyDistanceFromStart
                                + ", ST_Length(ST_Line_Substring(e.geom, ST_Line_Locate_Point(e.geom, x.point),1), 1) AS " + keyDistanceToEnd + " "
                                + "FROM edges e "
                                + "JOIN (select " + pointString + " AS point) AS x ON 1 = 1 "
                                + "WHERE e.ROWID IN( "
                                + "    SELECT ROWID FROM SpatialIndex "
                                + "    WHERE f_table_name = 'edges' "
                                + "    AND search_frame = BuildCircleMbr(" + coordinates.getLongitude() + ", " + coordinates.getLatitude() + " , " + distance + "  ,4326)"
                                + ")  "
                                + "ORDER BY Distance( e.geom, x.point) "
                                + "LIMIT 1 " );
                if ( rs.next() ) {
                    long edgeId = rs.getLong( "id" );
                    long nodeId = -1;

                    double speed = rs.getDouble( "metric_speed_forward" );
                    double lengthToStart = rs.getDouble( keyDistanceFromStart );
                    double lengthToEnd = rs.getDouble( keyDistanceToEnd );

                    if ( metrics.contains( Metric.LENGTH ) ) {
                        toSourceMap.put( Metric.LENGTH, Distance.newInstance( lengthToStart ) );
                        toTargetMap.put( Metric.LENGTH, Distance.newInstance( lengthToEnd ) );
                    }
                    if ( metrics.contains( Metric.TIME ) ) {
                        double timeToStart = lengthToStart / ( speed / 3.6 );
                        double timeToEnd = lengthToEnd / ( speed / 3.6 );
                        toSourceMap.put( Metric.TIME, Distance.newInstance( timeToStart ) );
                        toTargetMap.put( Metric.TIME, Distance.newInstance( timeToEnd ) );
                    }
                    return new PointSearcher.PointSearchResult( edgeId, nodeId, toSourceMap, toTargetMap );
                }
                distance *= DISTANCE_MULTIPLIER;
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

}

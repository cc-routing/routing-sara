/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.values.CartesianCoords;
import cz.certicon.routing.model.values.Coordinate;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

import static java.lang.Math.*;

import java.util.logging.Logger;

/**
 * Utilities for coordinates
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class CoordinateUtils {

    private static final double EARTH_RADIUS = 6371000;
    public static final double COORDINATE_PRECISION = 10E-5; // 0.11 meter accuracy
    public static final double DISTANCE_PRECISION_METERS = 10E-1; // 0.1 meter accuracy

//    private static final CoordinateReferenceSystem COORDINATE_REFERENCE_SYSTEM;
//    static {
    //        try {
//            COORDINATE_REFERENCE_SYSTEM = org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;
//        } catch ( FactoryException ex ) {
//            throw new IllegalStateException( ex );
//        }
//    }

    /**
     * Calculates the geographical midpoint of the given coordinates.
     *
     * @param iterator iterator of coordinates to be accounted into the
     *                 calculation
     * @return geographical midpoint
     */
    public static <T> Coordinate calculateGeographicMidpoint( Iterator<Coordinate> iterator ) {
        List<CartesianCoords> ccoords = new LinkedList<>();
        while ( iterator.hasNext() ) {
            Coordinate coordinate = iterator.next();
            double lat = toRadians( coordinate.getLatitude() );
            double lon = toRadians( coordinate.getLongitude() );
            ccoords.add( new CartesianCoords(
                    cos( lat ) * cos( lon ),
                    cos( lat ) * sin( lon ),
                    sin( lat )
            ) );
        }
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        for ( CartesianCoords c : ccoords ) {
            sumX += c.getX();
            sumY += c.getY();
            sumZ += c.getZ();
        }
        CartesianCoords mid = new CartesianCoords(
                sumX / ccoords.size(),
                sumY / ccoords.size(),
                sumZ / ccoords.size()
        );
        double lon = atan2( mid.getY(), mid.getX() );
        double hyp = sqrt( mid.getX() * mid.getX() + mid.getY() * mid.getY() );
        double lat = atan2( mid.getZ(), hyp );
        return new Coordinate( toDegrees( lat ), toDegrees( lon ) );
    }

    /**
     * Calculates the geographical midpoint of the given coordinates.
     *
     * @param coordinates list of coordinates to be accounted into the
     *                    calculation
     * @return geographical midpoint
     */
    public static Coordinate calculateGeographicMidpoint( List<Coordinate> coordinates ) {
        return calculateGeographicMidpoint( coordinates.iterator() );
    }

    /**
     * Calculates the geographical distance between two points
     *
     * @param a first point in {@link Coordinate}
     * @param b second point in {@link Coordinate}
     * @return calculated distance in meters
     */
    public static double calculateDistance( Coordinate a, Coordinate b ) {
        return calculateDistance( a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude() );
    }

    /**
     * Calculates the geographical distance between two points
     *
     * @param aLat latitude of point A
     * @param aLon longitude of point A
     * @param bLat latitude of point B
     * @param bLon longitude of point B
     * @return calculated distance in meters
     */
    public static double calculateDistance( double aLat, double aLon, double bLat, double bLon ) {
//        System.out.println( "calcualting distance:" );
//        System.out.println( a );
//        System.out.println( b );
        double aLatRad = toRadians( aLat );
        double aLonRad = toRadians( aLon );
        double bLatRad = toRadians( bLat );
        double bLonRad = toRadians( bLon );
        double result;
        // Pythagoras distance
//        double varX = ( aLatRad - bLatRad ) * cos( ( aLonRad + bLonRad ) / 2 );
//        double varY = ( aLonRad - bLonRad );
//        result = sqrt( varX * varX + varY * varY ) * EARTH_RADIUS;
//        System.out.println( "Pythagoras: " + result );
        // Haversine formula
        double deltaLatRad = toRadians( aLat - bLat );
        double deltaLonRad = toRadians( aLon - bLon );
        double varA = sin( deltaLatRad / 2 ) * sin( deltaLatRad / 2 ) + cos( aLatRad ) * cos( bLatRad ) * sin( deltaLonRad / 2 ) * sin( deltaLonRad / 2 );
        double varC = 2 * atan2( sqrt( varA ), sqrt( 1 - varA ) );
        result = EARTH_RADIUS * varC;

        // JTS
//        GeodeticCalculator geodeticCalculator = new GeodeticCalculator( COORDINATE_REFERENCE_SYSTEM );
//        try {
//            geodeticCalculator.setStartingPosition( JTS.toDirectPosition( new Coordinate( a.getLongitude(), a.getLatitude() ), COORDINATE_REFERENCE_SYSTEM ) );
//            geodeticCalculator.setDestinationPosition( JTS.toDirectPosition( new Coordinate( b.getLongitude(), b.getLatitude() ), COORDINATE_REFERENCE_SYSTEM ) );
//        } catch ( TransformException ex ) {
//            throw new RuntimeException( ex );
//        }
//        result = geodeticCalculator.getOrthodromicDistance();
        return result;
    }

    /**
     * Divides path between two points into list of coordinates.
     *
     * @param start starting point in {@link Coordinate}
     * @param end   target point in {@link Coordinate}
     * @param count amount of required points in the path
     * @return list of {@link Coordinate} for the given path
     */
    public static List<Coordinate> divideCoordinates( Coordinate start, Coordinate end, int count ) {
        List<Coordinate> coords = new LinkedList<>();
        double aLat = start.getLatitude();
        double aLon = start.getLongitude();
        double bLat = end.getLatitude();
        double bLon = end.getLongitude();
        for ( int i = 0; i < count; i++ ) {
            double avgLat = ( ( count - 1 - i ) * aLat + ( i ) * bLat ) / ( count - 1 );
            double avgLon = ( ( count - 1 - i ) * aLon + ( i ) * bLon ) / ( count - 1 );
            coords.add( new Coordinate( avgLat, avgLon ) );
        }
        return coords;
    }

    /**
     * Converts coordinates in WGS84 format into Cartesian coordinates
     *
     * @param coords {@link Coordinate} in WGS84
     * @return {@link CartesianCoords} representation of the given coordinates
     */
    public static CartesianCoords toCartesianFromWGS84( Coordinate coords ) {
        return new CartesianCoords(
                EARTH_RADIUS * Math.cos( coords.getLatitude() ) * Math.cos( coords.getLongitude() ),
                EARTH_RADIUS * Math.cos( coords.getLatitude() ) * Math.sin( coords.getLongitude() ),
                EARTH_RADIUS * Math.sin( coords.getLatitude() )
        );
    }

    /**
     * Converts WGS84 coordinates to point in the given container.
     *
     * @param container an instance of {@link Dimension} for the point to fit in
     *                  (scaled)
     * @param coords    {@link Coordinate} in WGS84
     * @return scaled {@link Point} for the given container based on the given
     * coordinates
     */
    public static Point toPointFromWGS84( Dimension container, Coordinate coords ) {
//        int x = (int) ( ( container.width / 360.0 ) * ( 180 + coords.getLatitude() ) );
//        int y = (int) ( ( container.height / 180.0 ) * ( 90 - coords.getLongitude() ) );
        int x = (int) ( ( container.width / 360.0 ) * ( coords.getLongitude() ) );
        int y = (int) ( ( container.height / 180.0 ) * ( coords.getLatitude() ) );
        return new Point( x, y );
    }

    /**
     * Evaluates equality of the given coordinates with the given precision. For
     * example 1 and 1.99 are equal with precision of 1.0
     *
     * @param a         first coordinate
     * @param b         second coordinate
     * @param precision given precision
     * @return true if the coordinates are equal with the given precision, false
     * otherwise
     */
    public static boolean equals( Coordinate a, Coordinate b, double precision ) {
        return ( DoubleComparator.isEqualTo( a.getLatitude(), b.getLatitude(), precision )
                && DoubleComparator.isEqualTo( a.getLongitude(), b.getLongitude(), precision ) );
    }

    public static List<Coordinate> sortClockwise( Collection<Coordinate> coords ) {
        return sortClockwise( coords.iterator() );
    }

    public static List<Coordinate> sortClockwise( Iterator<Coordinate> iterator ) {
        List<Coordinate> coordinateList = new ArrayList<>();
        while ( iterator.hasNext() ) {
            coordinateList.add( iterator.next() );
        }
        Coordinate center = calculateGeographicMidpoint( coordinateList );
        Collections.sort( coordinateList, new ClockwiseComparator( center ) );
        return coordinateList;
    }

    private static class ClockwiseComparator implements Comparator<Coordinate> {

        private final Coordinate center;

        public ClockwiseComparator( Coordinate center ) {
            this.center = center;
        }

        @Override
        public int compare( Coordinate a, Coordinate b ) {
            if ( CoordinateUtils.equals( a, b, COORDINATE_PRECISION ) ) {
                return 0;
            }
            if ( a.getLongitude() - center.getLongitude() >= 0 && b.getLongitude() - center.getLongitude() < 0 ) {
                return -1;
            }
            if ( a.getLongitude() - center.getLongitude() < 0 && b.getLongitude() - center.getLongitude() >= 0 ) {
                return 1;
            }
            if ( a.getLongitude() - center.getLongitude() == 0 && b.getLongitude() - center.getLongitude() == 0 ) {
                if ( a.getLatitude() - center.getLatitude() >= 0 || b.getLatitude() - center.getLatitude() >= 0 ) {
                    return a.getLatitude() > b.getLatitude() ? -1 : 1;
                }
                return b.getLatitude() > a.getLatitude() ? -1 : 1;
            }

            // compute the cross product of vectors (center -> a) x (center -> b)
            double det = ( a.getLongitude() - center.getLongitude() ) * ( b.getLatitude() - center.getLatitude() ) - ( b.getLongitude() - center.getLongitude() ) * ( a.getLatitude() - center.getLatitude() );
            if ( det < 0 ) {
                return -1;
            }
            if ( det > 0 ) {
                return 1;
            }

            // points a and b are on the same line from the center
            // check which point is closer to the center
            double d1 = ( a.getLongitude() - center.getLongitude() ) * ( a.getLongitude() - center.getLongitude() ) + ( a.getLatitude() - center.getLatitude() ) * ( a.getLatitude() - center.getLatitude() );
            double d2 = ( b.getLongitude() - center.getLongitude() ) * ( b.getLongitude() - center.getLongitude() ) + ( b.getLatitude() - center.getLatitude() ) * ( b.getLatitude() - center.getLatitude() );
            return d1 > d2 ? -1 : 1;
        }
    }

}

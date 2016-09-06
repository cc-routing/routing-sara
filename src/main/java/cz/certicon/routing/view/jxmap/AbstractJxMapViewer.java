/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view.jxmap;

import cz.certicon.routing.model.values.Coordinate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.OSMTileFactoryInfo;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public abstract class AbstractJxMapViewer {

    private final JXMapViewer mapViewer;
    private final JXMapKit mapKit;
    private JFrame frame;
    private DefaultTileFactory tileFactory;
    private final Set<GeoPosition> fitGeoPosition = new HashSet<>();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();

    public AbstractJxMapViewer() {
        this.mapKit = new JXMapKit();
        this.mapViewer = mapKit.getMainMap();
    }

    public void addPolygon( List<GeoPosition> geoPositions ) {
        fitGeoPosition.addAll( geoPositions );
        RoutePainter routePainter = new RoutePainter( geoPositions );
        painters.add( routePainter );
    }

    public void addPolygon( List<GeoPosition> geoPositions, Color color ) {
        fitGeoPosition.addAll( geoPositions );
        RoutePainter routePainter = new RoutePainter( geoPositions );
        routePainter.setColor( color );
        painters.add( routePainter );
    }

    public void addCluster( Collection<GeoPosition> geoPositions, Color color ) {
        fitGeoPosition.addAll( geoPositions );
        ClusterPainter clusterPainter = new ClusterPainter( geoPositions );
        clusterPainter.setColor( color );
        painters.add( clusterPainter );
    }

    public void display() {
        frame = new JFrame( "map" );
        frame.setContentPane( mapViewer );
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        TileFactoryInfo info = new OSMTileFactoryInfo();
        tileFactory = new DefaultTileFactory( info );
        tileFactory.setThreadPoolSize( 8 );
        mapKit.setTileFactory( tileFactory );
        mapViewer.zoomToBestFit( fitGeoPosition, 0.7 );
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>( painters );
        mapViewer.setOverlayPainter( painter );
        frame.setVisible( true );
    }

    public void displayPolygon( List<GeoPosition> geoPositions ) {
        addPolygon( geoPositions );
        display();
    }

    public static List<GeoPosition> toGeoPosition( List<Coordinate> coordinates ) {
        List<GeoPosition> track = new ArrayList<>();
        for ( Coordinate coordinate : coordinates ) {
            track.add( new GeoPosition( coordinate.getLatitude(), coordinate.getLongitude() ) );
        }
        return track;
    }

    public static Collection<GeoPosition> toGeoPosition( Collection<Coordinate> coordinates ) {
        List<GeoPosition> track = new ArrayList<>();
        for ( Coordinate coordinate : coordinates ) {
            track.add( new GeoPosition( coordinate.getLatitude(), coordinate.getLongitude() ) );
        }
        return track;
    }
}

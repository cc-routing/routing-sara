/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view.jxmap;

import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.GeometryUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public abstract class AbstractJxMapViewer {

    private final JXMapViewer mapViewer;
    private final JXMapKit mapKit;
    private JFrame frame;
    private DefaultTileFactory tileFactory;
    private final Set<GeoPosition> fitGeoPosition = new HashSet<>();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();
    private final List<Clickable> clickables = new ArrayList<>();
    private final Map<GeoPosition, Painter<JXMapViewer>> pointPainterMap = new HashMap<>();
    private final Map<List<GeoPosition>, Painter<JXMapViewer>> polygonPainterMap = new HashMap<>();
    private JPanel infoPanel;
    private boolean centering = true;

    public AbstractJxMapViewer() {
        this.mapKit = new JXMapKit();
        this.mapViewer = mapKit.getMainMap();
    }

    public void addPolygon( List<GeoPosition> geoPositions ) {
        addPolygon( geoPositions, RoutePainter.COLOR_DEFAULT );
    }

    public void addPolygon( List<GeoPosition> geoPositions, Color color ) {
        fitGeoPosition.addAll( geoPositions );
        RoutePainter routePainter = new RoutePainter( geoPositions );
        routePainter.setColor( color );
        painters.add( routePainter );
        polygonPainterMap.put( geoPositions, routePainter );
        repaint( centering );
    }

    public void removePolygon( List<GeoPosition> geoPositions ) {
        painters.remove( polygonPainterMap.get( geoPositions ) );
        polygonPainterMap.remove( geoPositions );
        for ( GeoPosition geoPosition : geoPositions ) {
            fitGeoPosition.remove( geoPosition );
        }
        repaint( centering );
    }

    public void addPoint( GeoPosition geoPosition ) {
        addPoint( geoPosition, NodePainter.COLOR_DEFAULT );
    }

    public void addPoint( GeoPosition geoPosition, Color color ) {
        fitGeoPosition.add( geoPosition );
        NodePainter nodePainter = new NodePainter( geoPosition );
        nodePainter.setColor( color );
        painters.add( nodePainter );
        pointPainterMap.put( geoPosition, nodePainter );
        repaint( centering );
    }

    public void removePoint( GeoPosition geoPosition ) {
        painters.remove( pointPainterMap.get( geoPosition ) );
        pointPainterMap.remove( geoPosition );
        fitGeoPosition.remove( geoPosition );
        repaint( centering );
    }

    public void addCluster( Collection<GeoPosition> geoPositions, Color color ) {
        fitGeoPosition.addAll( geoPositions );
        ClusterPainter clusterPainter = new ClusterPainter( geoPositions );
        clusterPainter.setColor( color );
        painters.add( clusterPainter );
        repaint( centering );
    }

    public void register( Clickable clickable ) {
        clickables.add( clickable );
    }

    public void setCentering( boolean centering ) {
        this.centering = centering;
    }

    public void allowInfo( boolean info ) {
        if ( info ) {
            infoPanel.setSize( 400, 600 );
        } else {
            infoPanel.setSize( 0, 600 );
        }
    }

    public void onClick( int x, int y ) {
        for ( Clickable clickable : clickables ) {
            if ( infoPanel != null && contains( clickable.getGeoPositions(), x, y ) ) {
                clickable.display( infoPanel );
                frame.repaint();
                frame.invalidate();
                frame.revalidate();
                frame.validate();
                frame.repaint();
                return;
            }
        }
    }

    public void display() {
        frame = new JFrame( "map" );
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add( mapViewer, BorderLayout.CENTER );
        infoPanel = new JPanel();
        infoPanel.setSize( 0, 600 );
        mainPanel.add( infoPanel, BorderLayout.EAST );
        frame.setContentPane( mainPanel );
        frame.setSize( 1000, 600 );
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        TileFactoryInfo info = new OSMTileFactoryInfo();
        tileFactory = new DefaultTileFactory( info );
        tileFactory.setThreadPoolSize( 8 );
        mapKit.setTileFactory( tileFactory );
        mapViewer.zoomToBestFit( fitGeoPosition, 0.7 );
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>( painters );
        mapViewer.setOverlayPainter( painter );
        mapViewer.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked( MouseEvent e ) {
                onClick( e.getX(), e.getY() );
            }

        } );
        frame.setVisible( true );
        repaint( true );
    }

    public void displayPolygon( List<GeoPosition> geoPositions ) {
        addPolygon( geoPositions );
        display();
    }

    public void repaint( boolean center ) {
        if ( frame != null ) {
            if ( center ) {
                mapViewer.zoomToBestFit( fitGeoPosition, 0.7 );
            }
            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>( painters );
            mapViewer.setOverlayPainter( painter );
        }
    }

    public static List<GeoPosition> toGeoPosition( List<Coordinate> coordinates ) {
        List<GeoPosition> track = new ArrayList<>();
        for ( Coordinate coordinate : coordinates ) {
            track.add( toGeoPosition( coordinate ) );
        }
        return track;
    }

    public static Collection<GeoPosition> toGeoPosition( Collection<Coordinate> coordinates ) {
        List<GeoPosition> track = new ArrayList<>();
        for ( Coordinate coordinate : coordinates ) {
            track.add( toGeoPosition( coordinate ) );
        }
        return track;
    }

    public static GeoPosition toGeoPosition( Coordinate coordinate ) {
        return new GeoPosition( coordinate.getLatitude(), coordinate.getLongitude() );
    }

    protected List<Painter<JXMapViewer>> getPainters() {
        return painters;
    }

    private boolean contains( List<GeoPosition> geoPositions, int x, int y ) {
        Rectangle rectangle = mapViewer.getViewportBounds();
        int lastX = Integer.MAX_VALUE;
        int lastY = Integer.MAX_VALUE;
        for ( GeoPosition coordinate : geoPositions ) {
            Point2D pt = mapViewer.getTileFactory().geoToPixel( coordinate, mapViewer.getZoom() );
            int currX = (int) ( pt.getX() - rectangle.getX() );
            int currY = (int) ( pt.getY() - rectangle.getY() );
            if ( Clickable.Comparison.inRange( currX, currY, x, y ) ) {
                return true;
            }
            if ( lastX != Integer.MAX_VALUE && lastY != Integer.MAX_VALUE && Clickable.Comparison.inLineRange( lastX, lastY, currX, currY, x, y ) ) {
                return true;
            }
            lastX = currX;
            lastY = currY;
        }
        return false;
    }

    public interface Clickable {

        List<GeoPosition> getGeoPositions();

        void display( JPanel panel );

        class Comparison {

            public static boolean inRange( int base, int compared ) {
                return Math.abs( base - compared ) <= PRECISION;
            }

            public static boolean inRange( int baseX, int baseY, int x, int y ) {
//                System.out.println( "comparing: " + baseX + ", " + baseY + " to " + x + ", " + y );
                return inRange( baseX, x ) && inRange( baseY, y );
            }

            public static boolean inLineRange( int ax, int ay, int bx, int by, int x, int y ) {
                return GeometryUtils.pointToLineDistance( ax, ay, bx, by, x, y ) <= PRECISION;
            }
            public static final int PRECISION = 5;
        }

        class Presentation {

            public static void addRow( JPanel panel, String left, String right ) {
                panel.add( new JLabel( " " + left ) );
                panel.add( new JLabel( " " + right ) );
            }

            public static void addRow( JPanel panel, String left, long right ) {
                addRow( panel, left, right + "" );
            }

            public static void addRow( JPanel panel, String left, double right ) {
                addRow( panel, left, right + "" );
            }

            public static void addRow( JPanel panel, String col1, Object col2, Object col3, Object col4 ) {
                panel.add( new JLabel( " " + col1 ) );
                panel.add( new JLabel( " " + col2.toString() ) );
                panel.add( new JLabel( " " + col3.toString() ) );
                panel.add( new JLabel( " " + col4.toString() ) );
            }

            public static String asHtml( Object str ) {
                return "<html>" + str + "</html>";
            }

            public static String asBold( Object str ) {
                return "<html><b>" + str + "</b></html>";
            }
        }
    }

    public interface OnClick {

        void onClick( Object clickedObject );
    }
}

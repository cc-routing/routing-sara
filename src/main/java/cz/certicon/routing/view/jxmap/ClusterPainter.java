/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view.jxmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ClusterPainter implements Painter<JXMapViewer> {

    private Color color = Color.RED;
    private final Collection<GeoPosition> cluster;

    public ClusterPainter( Collection<GeoPosition> cluster ) {
        this.cluster = new HashSet<>( cluster );
    }

    @Override
    public void paint( Graphics2D g, JXMapViewer map, int width, int height ) {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate( -rect.x, -rect.y );

        boolean antiAlias = true;
        if ( antiAlias ) {
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }

//        // do the drawing
//        g.setColor( Color.BLACK );
//        g.setStroke( new BasicStroke( 5 ) );
//
//        drawRoute( g, map );
        // do the drawing again
        g.setColor( color );
        g.setStroke( new BasicStroke( 2 ) );

        for ( GeoPosition geoPosition : cluster ) {
            drawPoint( g, map, geoPosition );
        }

        g.dispose();
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    /**
     * @param g the graphics object
     * @param map the map
     */
    private void drawPoint( Graphics2D g, JXMapViewer map, GeoPosition position ) {
        // convert geo-coordinate to world bitmap pixel
        Point2D pt = map.getTileFactory().geoToPixel( position, map.getZoom() );
        int radius = 5;
        g.drawOval( (int) pt.getX(), (int) pt.getY(), radius, radius );
    }

}

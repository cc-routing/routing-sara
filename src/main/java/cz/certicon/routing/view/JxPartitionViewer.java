/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.view.jxmap.AbstractJxMapViewer;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.ColorUtils;
import cz.certicon.routing.utils.CoordinateUtils;
import cz.certicon.routing.view.jxmap.ClusterPainter;
import cz.certicon.routing.view.jxmap.RoutePainter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class JxPartitionViewer extends AbstractJxMapViewer implements PartitionViewer {

    private ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( 20 );

    @Override
    public void addCutEdges( Graph graph, Collection<Edge> cutEdges ) {
        List<Coordinate> coords = new ArrayList<>();
        for ( Edge cutEdge : cutEdges ) {
            coords.add( edgeMidpoint( graph, cutEdge ) );
        }
        List<Coordinate> sorted = CoordinateUtils.sortClockwise( coords );
        if ( !sorted.isEmpty() ) {
            sorted.add( sorted.get( 0 ) );
        }
        addPolygon( toGeoPosition( sorted ) );
    }

    private Coordinate edgeMidpoint( Graph graph, Edge edge ) {
        Coordinate source = edge.getSource( graph ).getCoordinate( graph );
        Coordinate target = edge.getTarget( graph ).getCoordinate( graph );
        Coordinate midpoint = CoordinateUtils.calculateGeographicMidpoint( Arrays.asList( source, target ) );
        return midpoint;
    }

    @Override
    public void addNodeCluster( Graph graph, Collection<Node> partition ) {
        Color color = colorSupplier.nextColor();
        List<Coordinate> coords = new ArrayList<>();
        for ( Node node : partition ) {
            assert node.getCoordinate( graph ) != null;
            coords.add( node.getCoordinate( graph ) );
        }
        addCluster( toGeoPosition( coords ), color );
//        List<Coordinate> sorted = CoordinateUtils.sortClockwise( coords );
//        if ( !sorted.isEmpty() ) {
//            sorted.add( sorted.get( 0 ) );
//        }
//        addPolygon( toGeoPosition( sorted ), color );
    }

    @Override
    public void addBorderNodes( Graph graph, Collection<Node> borderNodes ) {
        Color color = colorSupplier.nextColor();
        List<Coordinate> coords = new ArrayList<>();
        for ( Node borderNode : borderNodes ) {
            coords.add( borderNode.getCoordinate( graph ) );
        }
        List<Coordinate> sorted = CoordinateUtils.sortClockwise( coords );
        if ( !sorted.isEmpty() ) {
            sorted.add( sorted.get( 0 ) );
        }
        addPolygon( toGeoPosition( sorted ), color );
    }

    @Override
    public void display() {
        super.display();
        Thread repaintThread = new Thread( new Repainter( getPainters() ) );
        repaintThread.setDaemon( true );
        repaintThread.start();
    }

    private static class Repainter implements Runnable {

        private final ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( 20 );
        private final List<Painter<JXMapViewer>> painters;

        public Repainter( List<Painter<JXMapViewer>> painters ) {
            this.painters = painters;
        }

        @Override
        public void run() {
            while ( true ) {
                try {
                    Thread.sleep( 10000 );
                    for ( Painter<JXMapViewer> painter : painters ) {
                        if ( painter instanceof ClusterPainter ) {
                            ( (ClusterPainter) painter ).setColor( colorSupplier.nextColor() );
                        } else if ( painter instanceof RoutePainter ) {
                            ( (RoutePainter) painter ).setColor( colorSupplier.nextColor() );
                        }
                    }
                } catch ( InterruptedException ex ) {
                }
            }
        }

    }
}

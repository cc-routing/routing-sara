/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.data.GraphDataDao;
import cz.certicon.routing.data.RouteDataDAO;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.model.values.TimeUnits;
import cz.certicon.routing.utils.CoordinateUtils;
import cz.certicon.routing.utils.GeometryUtils;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.routing.view.DebugViewer;
import cz.certicon.routing.view.jxmap.AbstractJxMapViewer;
import cz.certicon.routing.view.jxmap.NodePainter;
import cz.certicon.routing.view.jxmap.RoutePainter;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class JxDebugViewer<N extends Node, E extends Edge> extends AbstractJxMapViewer implements DebugViewer {

    private final GraphDataDao graphDataDao;
    private final long delay;
    private final Map<Long, NodeData> nodeDataMap = new HashMap<>();
    private final Map<Long, EdgeData> edgeDataMap = new HashMap<>();
    private boolean stepByInput = false;
    private boolean visible = false;

    public JxDebugViewer( GraphDataDao graphDataDao, Graph<N, E> graph, long delayInMillis ) {
        this.graphDataDao = graphDataDao;
        Graph<N, E> graph1 = graph;
        this.delay = delayInMillis;
    }

    @Override
    public void setStepByInput( boolean stepByInput ) {
        this.stepByInput = stepByInput;
    }

    @Override
    public void blinkEdge( long edgeId ) {
        visible();
        displayEdge( edgeId );
        removeEdge( edgeId );
    }

    @Override
    public void displayNode( long nodeId ) {
        visible();
        try {
            TimeMeasurement time = new TimeMeasurement();
            time.setTimeUnits( TimeUnits.MILLISECONDS );
            time.start();
            NodeData nodeData = new NodeData( graphDataDao.loadNodeData( nodeId ) );
            addPoint( nodeData.getGeoPosition() );
            register( nodeData );
            nodeDataMap.put( nodeId, nodeData );
            nextStep( time.stop() );
        } catch ( IOException ex ) {
            Logger.getLogger( JxDebugViewer.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    @Override
    public void removeNode( long nodeId ) {
        visible();
        NodeData nodeData = nodeDataMap.get( nodeId );
        removePoint( nodeData.getGeoPosition() );
        nodeDataMap.remove( nodeId );
    }

    @Override
    public void displayEdge( long edgeId ) {
        visible();
        try {
            TimeMeasurement time = new TimeMeasurement();
            time.setTimeUnits( TimeUnits.MILLISECONDS );
            time.start();
            EdgeData edgeData = new EdgeData( graphDataDao.loadEdgeData( edgeId ) );
            addPolygon( edgeData.getGeoPositions() );
            register( edgeData );
            edgeDataMap.put( edgeId, edgeData );
            nextStep( time.stop() );
        } catch ( IOException ex ) {
            Logger.getLogger( JxDebugViewer.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    @Override
    public void removeEdge( long edgeId ) {
        visible();
        EdgeData edgeData = edgeDataMap.get( edgeId );
        removePolygon( edgeData.getGeoPositions() );
        edgeDataMap.remove( edgeId );
    }

    @Override
    public void closeEdge( long edgeId ) {
        visible();
        TimeMeasurement time = new TimeMeasurement();
        time.setTimeUnits( TimeUnits.MILLISECONDS );
        time.start();
        EdgeData edgeData = edgeDataMap.get( edgeId );
        removePolygon( edgeData.getGeoPositions() );
        addPolygon( edgeData.getGeoPositions(), Color.BLUE );
        nextStep( time.stop() );
    }

    private void nextStep( long elapsedTime ) {
        if ( stepByInput ) {
            try {
                System.in.read();
            } catch ( IOException ex ) {
                Logger.getLogger( JxDebugViewer.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } else {
            long timeLeft = delay - elapsedTime;
            if ( timeLeft < 0 ) {
                timeLeft = 0;
            }
            try {
                Thread.sleep( timeLeft );
            } catch ( InterruptedException ex ) {
                Logger.getLogger( JxDebugViewer.class.getName() ).log( Level.SEVERE, null, ex );
            }

        }
    }

    private void visible() {
        if ( !visible ) {
            display();
            visible = true;
//            setCentering( false );
        }
    }

    private static class EdgeData implements Clickable {

        private final GraphDataDao.EdgeData edgeData;

        public EdgeData( GraphDataDao.EdgeData edgeData ) {
            this.edgeData = edgeData;
        }

        @Override
        public List<GeoPosition> getGeoPositions() {
            return toGeoPosition( edgeData.getCoordinates() );
        }

        @Override
        public void display( JPanel panel ) {
            panel.removeAll();
            panel.setLayout( new GridLayout( 0, 4 ) );
            Presentation.addRow( panel, "id", edgeData.getId(), "", "" );
            Presentation.addRow( panel, "source", edgeData.getSource(), "", "" );
            Presentation.addRow( panel, "target", edgeData.getTarget(), "", "" );
            for ( Map.Entry<Metric, Distance> entry : edgeData.getDistanceMap().entrySet() ) {
                Presentation.addRow( panel, entry.getKey().name(), entry.getValue().isInfinite() ? -1 : entry.getValue().getValue() );
            }
        }

    }

    private static class NodeData implements Clickable {

        private final GraphDataDao.NodeData nodeData;

        public NodeData( GraphDataDao.NodeData nodeData ) {
            this.nodeData = nodeData;
        }

        @Override
        public List<GeoPosition> getGeoPositions() {
            return Arrays.asList( toGeoPosition( nodeData.getCoordinate() ) );
        }

        @Override
        public void display( JPanel panel ) {
            panel.removeAll();
            panel.setLayout( new GridLayout( 0, 2 ) );
            Presentation.addRow( panel, "id", nodeData.getId() );
            Presentation.addRow( panel, "cell_id", Arrays.toString( nodeData.getCellIds() ) );
            for ( int i = 0; i < nodeData.getEdgeCount(); i++ ) {
                StringBuilder sb = new StringBuilder();
                for ( int j = 0; j < nodeData.getEdgeCount(); j++ ) {
                    Distance cost = nodeData.getTurnTable().getCost( i, j );
                    sb.append( cost.isInfinite() ? "-10" : String.format( "%.02f", cost.getValue() ) ).append( " " );
                }
                Presentation.addRow( panel, "turn_table#" + i, sb.toString() );
            }
            TLongIterator incIt = nodeData.getIncomingEdges().iterator();
            int i = 0;
            while ( incIt.hasNext() ) {
                Presentation.addRow( panel, "incoming#" + ++i, incIt.next() );
            }
            TLongIterator outIt = nodeData.getOutgoingEdges().iterator();
            i = 0;
            while ( outIt.hasNext() ) {
                Presentation.addRow( panel, "outgoing#" + ++i, outIt.next() );
            }
        }

        public GeoPosition getGeoPosition() {
            return toGeoPosition( nodeData.getCoordinate() );
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.view.jxmap.AbstractJxMapViewer;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.Partition;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.ColorUtils;
import cz.certicon.routing.utils.CoordinateUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class JxPartitionViewer extends AbstractJxMapViewer implements PartitionViewer {

    private ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( 20 );

    @Override
    public void addPartition( Graph graph, Collection<Edge> cutEdges ) {
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
        Coordinate source = graph.getNodeCoordinate( edge.getSource() );
        Coordinate target = graph.getNodeCoordinate( edge.getTarget() );
        Coordinate midpoint = CoordinateUtils.calculateGeographicMidpoint( Arrays.asList( source, target ) );
        return midpoint;
    }

    @Override
    public void addPartition( Graph graph, Partition partition ) {
        Color color = colorSupplier.nextColor();
        List<Coordinate> coords = new ArrayList<>();
        for ( Node node : partition.getNodes() ) {
            assert node.getCoordinate() != null;
            coords.add( node.getCoordinate() );
        }
        addCluster( toGeoPosition( coords ), color );
//        List<Coordinate> sorted = CoordinateUtils.sortClockwise( coords );
//        if ( !sorted.isEmpty() ) {
//            sorted.add( sorted.get( 0 ) );
//        }
//        addPolygon( toGeoPosition( sorted ), color );
    }

    @Override
    public void addPartitionNodes( Graph graph, Collection<Node> borderNodes ) {
        Color color = colorSupplier.nextColor();
        List<Coordinate> coords = new ArrayList<>();
        for ( Node borderNode : borderNodes ) {
            coords.add( borderNode.getCoordinate() );
        }
        List<Coordinate> sorted = CoordinateUtils.sortClockwise( coords );
        if ( !sorted.isEmpty() ) {
            sorted.add( sorted.get( 0 ) );
        }
        addPolygon( toGeoPosition( sorted ), color );
    }

}

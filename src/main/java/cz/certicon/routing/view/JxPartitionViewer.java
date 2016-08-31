/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.Partition;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.CoordinateUtils;
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

    @Override
    public void addPartition( Graph graph, Collection<Edge> cutEdges ) {
        List<Coordinate> coordinates = new ArrayList<>();
        Edge first = null;
        for ( Edge cutEdge : cutEdges ) {
            coordinates.add( edgeMidpoint( graph, cutEdge ) );
            if ( first == null ) {
                first = cutEdge;
            }
        }
        if ( first != null ) { // create circle - add first edge as last
            coordinates.add( edgeMidpoint( graph, first ) );
        }
        addPolygon( toGeoPosition( coordinates ) );
    }

    private Coordinate edgeMidpoint( Graph graph, Edge edge ) {
        Coordinate source = graph.getNodeCoordinate( edge.getSource() );
        Coordinate target = graph.getNodeCoordinate( edge.getTarget() );
        Coordinate midpoint = CoordinateUtils.calculateGeographicMidpoint( Arrays.asList( source, target ) );
        return midpoint;
    }

    @Override
    public void addPartition( Graph graph, Partition partition ) {
        List<Coordinate> coords = new ArrayList<>();
        Iterator<Node> nodes = partition.getNodes();
        while(nodes.hasNext()){
            Node node = nodes.next();
            assert node.getCoordinate() != null;
            coords.add( node.getCoordinate() );
        }
        addPolygon( toGeoPosition( CoordinateUtils.sortClockwise( coords ) ) );
    }

}

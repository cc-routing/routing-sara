/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.ColorUtils;
import cz.certicon.routing.view.jxmap.AbstractJxMapViewer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class JxRouteViewer extends AbstractJxMapViewer implements RouteViewer {

    private static final int NUMBER_OF_COLORS = 20;
    private final ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( NUMBER_OF_COLORS );

    @Override
    public <N extends Node, E extends Edge> void addRoute( Route<N, E> route ) {
        List<Coordinate> coords = new ArrayList<>();
        for ( N node : route.getNodes() ) {
            coords.add( node.getCoordinate() );
        }
        addPolygon( toGeoPosition( coords ), colorSupplier.nextColor() );
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.values.Length;
import cz.certicon.routing.model.values.Time;
import cz.certicon.routing.model.values.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 * Representation of the additional route data (such as coordinates and properties like time and length)
 *
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Value
public class RouteData<E extends Edge> {

    @Getter( AccessLevel.NONE )
    Map<E, List<Coordinate>> coordinateMap;
    /**
     * Length of the route. See {@link cz.certicon.routing.model.graph.Metric#LENGTH}
     */
    Length length;
    /**
     * Time of the route. See {@link cz.certicon.routing.model.graph.Metric#TIME}
     */
    Time time;

    /**
     * Returns list of coordiantes for the given edge (its geometry)
     *
     * @param edge edge
     * @return edge's geometry
     */
    public List<Coordinate> getCoordiantes( E edge ) {
        return coordinateMap.get( edge );
    }

    /**
     * Returns list of edges in the route
     *
     * @return list of edges in the route
     */
    public List<E> getEdges() {
        List<E> edges = new ArrayList<>();
        for ( Map.Entry<E, List<Coordinate>> entry : coordinateMap.entrySet() ) {
            edges.add( entry.getKey() );
        }
        return edges;
    }
}

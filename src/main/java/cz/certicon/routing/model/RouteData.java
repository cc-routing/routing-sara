/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.values.Length;
import cz.certicon.routing.model.values.Time;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.values.Coordinate;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <E> edge type
 */
@Value
public class RouteData<E extends Edge> {

    @Getter( AccessLevel.NONE )
    Map<E, List<Coordinate>> coordinateMap;
    Length length;
    Time time;

    public List<Coordinate> getCoordiantes( E edge ) {
        return coordinateMap.get( edge );
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RouteData;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface RouteDataDAO {

    void saveRouteData( Route route, RouteData routeData ) throws IOException;

    RouteData loadRouteData( Route route ) throws IOException;
}

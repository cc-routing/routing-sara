/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.Iterator;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <N> node type
 * @param <E> edge type
 */
public interface Node<N extends Node, E extends Edge> extends Identifiable {
    /**
     * Returns cost of the turn from edge to edge via this node
     *
     * @param source from (turn origin) edge
     * @param target to (turn destination) edge
     * @return cost of the turn
     */
     Distance getTurnDistance( E source, E target );

     Iterator<E> getIncomingEdges();

     Iterator<E> getOutgoingEdges();

     Iterator<E> getEdges();

    /**
     * Returns Coordinate (latitude, longitude) of this node. Might throw
     * IllegalStateException, if the Coordinates are not set
     *
     * @return coordinate
     */
     Coordinate getCoordinate();

     int getDegree();

     void addEdge( E edge );

     void removeEdge( E edge );

     void setTurnTable( TurnTable turnTable );

     TurnTable getTurnTable();

     void setCoordinate( Coordinate coordinate );

     void lock();

     N copy( Graph<N, E> newGraph );
}

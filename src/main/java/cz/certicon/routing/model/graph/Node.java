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
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
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

    /**
     * Returns iterator of edges incoming to this node
     *
     * @return iterator of incoming edges
     */
    Iterator<E> getIncomingEdges();

    /**
     * Returns iterator of edges outgoing from this node
     *
     * @return iterator of outgoing edges
     */
    Iterator<E> getOutgoingEdges();

    /**
     * Returns iterator of edges adjacent to this node
     *
     * @return iterator of node's edges
     */
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

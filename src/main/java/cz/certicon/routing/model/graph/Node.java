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

    /**
     * Returns degree of this node (number of edges connected to this node)
     *
     * @return degree of this node
     */
    int getDegree();

    /**
     * Adds edge to this node
     *
     * @param edge added edge
     */
    void addEdge( E edge );

    /**
     * Removes edge from this node
     *
     * @param edge removed edge
     */
    void removeEdge( E edge );

    /**
     * Sets turn-table
     *
     * @param turnTable turn-table
     */
    void setTurnTable( TurnTable turnTable );

    /**
     * Returns turn-table
     *
     * @return turn-table
     */
    TurnTable getTurnTable();

    /**
     * Sets coordinates
     *
     * @param coordinate coordiantes
     */
    void setCoordinate( Coordinate coordinate );

    /**
     * Locks this node. All the altering operations after lock is called are forbidden.
     */
    void lock();

    /**
     * Creates a copy of this node into the new graph
     *
     * @param newGraph new graph
     * @return copy of this node from the new graph
     */
    N copy( Graph<N, E> newGraph );
}

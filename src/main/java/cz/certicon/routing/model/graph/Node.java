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

    public Distance getTurnDistance( E source, E target );

    public Iterator<E> getIncomingEdges();

    public Iterator<E> getOutgoingEdges();

    public Iterator<E> getEdges();

    public Coordinate getCoordinate();

    public int getDegree();

    public void addEdge( E edge );

    public void removeEdge( E edge );

    public void setTurnTable( TurnTable turnTable );

    public TurnTable getTurnTable();

    public void setCoordinate( Coordinate coordinate );

    public void lock();

    public N copy( Graph<N, E> newGraph );
}

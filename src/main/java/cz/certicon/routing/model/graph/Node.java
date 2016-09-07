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
 * @param <E>
 */
public interface Node<E extends Edge> extends Identifiable {

    public <N extends Node> Distance getTurnDistance( Graph<N, E> graph, E source, E target );

    public <N extends Node> Iterator<E> getIncomingEdges( Graph<N, E> graph );

    public <N extends Node> Iterator<E> getOutgoingEdges( Graph<N, E> graph );

    public <N extends Node> Iterator<E> getEdges( Graph<N, E> graph );

    public <N extends Node> Coordinate getCoordinate( Graph<N, E> graph );

    public <N extends Node> int getDegree( Graph<N, E> graph );

    public void addEdge( E edge );

    public void removeEdge( E edge );

    public void setTurnTable( TurnTable turnTable );

    public void setCoordinate( Coordinate coordinate );

    public void lock();
}

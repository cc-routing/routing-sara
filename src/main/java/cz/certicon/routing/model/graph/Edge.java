/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.values.Distance;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <N> node type
 */
public interface Edge<N extends Node> extends Identifiable {

    public <E extends Edge> N getSource( Graph<N, E> graph );

    public <E extends Edge> N getTarget( Graph<N, E> graph );

    public <E extends Edge> N getOtherNode( Graph<N, E> graph, N node );

    public <E extends Edge> Distance getTurnDistance( Graph<N, E> graph, TurnTable turnTable, E targetEdge );

    public <E extends Edge> boolean isOneWay( Graph<N, E> graph );
    
    public int getSourcePosition();
    
    public int getTargetPosition();
}

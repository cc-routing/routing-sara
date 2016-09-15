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
 * @param <E> edge type
 */
public interface Edge<N extends Node, E extends Edge> extends Identifiable {

    public N getSource();

    public N getTarget();

    public N getOtherNode( N node );

    public Distance getTurnDistance( N node, TurnTable turnTable, E targetEdge );

    public boolean isOneWay();

    public int getSourcePosition();

    public int getTargetPosition();
}

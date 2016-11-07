/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import lombok.Value;

/**
 * Representation of a state used in Dijkstra with turn-tables.
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Value
public class State<N extends Node, E extends Edge> {

    N node;
    E edge;

    /**
     * Returns whether this state is first (in route) or not
     *
     * @return true if this state is first, false otherwise
     */
    public boolean isFirst() {
        return edge == null;
    }
}

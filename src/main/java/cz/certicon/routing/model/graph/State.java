/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 * @param <N> node type
 * @param <E> edge type
 */
@Value
public class State<N extends Node, E extends Edge> {

    N node;
    E edge;
    
    public boolean isFirst(){
        return edge == null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
@EqualsAndHashCode( exclude = { "source", "target" } )
public class Edge {

    long id;
    boolean oneway;
    /**
     * Source
     */
    @NonNull
    Node source;
    /**
     * Target
     */
    @NonNull
    Node target;
    @NonNull
    Distance length;

    @Override
    public String toString() {
        return "Edge{id=" + id + ", oneway=" + oneway + ", length=" + length + ", source=Node{id=" + source.getId() + "}, target=Node{id=" + target.getId() + "}}";
    }
}

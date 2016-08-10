/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;
import lombok.NonNull;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
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
}

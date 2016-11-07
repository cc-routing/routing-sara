/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Edge;

import java.util.Collection;

import lombok.Value;

/**
 * Representation of minimal cut
 *
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Value
public class MinimalCut<E extends Edge> {

    /**
     * Collection of edges in the minimal cut
     */
    Collection<E> cutEdges;
    /**
     * Size of the minimal cut
     */
    double cutSize;
}

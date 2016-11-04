/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;

/**
 * Algorithm computing minimal cut in the given graph from source to target
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface MinimalCutAlgorithm {

    /**
     * Computes minimal cut between source and target in the given graph
     *
     * @param graph  the given graph
     * @param metric the metric to calculate by
     * @param source the source
     * @param target the target
     * @param <N>    node type
     * @param <E>    edge type
     * @return minimal cut
     */
    <N extends Node<N, E>, E extends Edge<N, E>> MinimalCut compute( Graph<N, E> graph, Metric metric, N source, N target );
}

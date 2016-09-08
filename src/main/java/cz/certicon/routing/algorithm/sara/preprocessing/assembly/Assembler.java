/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.assembly;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;

/**
 * Builds partitions using time-consuming techniques on reduced graph. Uses
 * greedy algorithm, local search heuristics, evolutionary algorithm and
 * combinations.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface Assembler {

    <N extends Node, E extends Edge> SaraGraph assemble( Graph<N, E> originalGraph, FilteredGraph graph );
}

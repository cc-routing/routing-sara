/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;

/**
 * Reduces graph size while preserving overall structure.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface Filter {

    /**
     * Filters given graph and creates intermediate areas represented as {@link ContractGraph}
     *
     * @param graph graph to be filtered
     * @param <N>   node type
     * @param <E>   edge type
     * @return filtered {@link ContractGraph}
     */
    <N extends Node<N,E>, E extends Edge<N,E>> ContractGraph filter( Graph<N, E> graph );

    /**
     * Set maximal cell size (U)
     *
     * @param maxCellSize maximal cell size
     */
    void setMaxCellSize( int maxCellSize );
}

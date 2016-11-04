/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.assembly;

import cz.certicon.routing.model.graph.preprocessing.ContractGraph;

/**
 * Builds partitions using time-consuming techniques on reduced graph. May use
 * greedy algorithm, local search heuristics, evolutionary algorithm and
 * combinations.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface Assembler {

    /**
     * Assembles given {@link ContractGraph} into more compact {@link ContractGraph} based on concrete implementation.
     *
     * @param graph graph to assemle
     * @return assembled {@link ContractGraph}
     */
    ContractGraph assemble( ContractGraph graph );

    /**
     * Set maximal cell size (U)
     *
     * @param maxCellSize maximal cell size
     */
    void setMaxCellSize( int maxCellSize );
}

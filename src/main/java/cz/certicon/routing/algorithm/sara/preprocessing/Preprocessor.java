/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing;

import cz.certicon.routing.model.basic.IdSupplier;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.utils.progress.ProgressListener;

/**
 * Partitioning preprocessor interface.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface Preprocessor {

    /**
     * Graph to create partitions from
     *
     * @param graph graph to create partitions from
     * @param input settings for preprocessing
     * @param cellIdSupplier id container for cells (serves as a supplier of possible ids)
     * @param <N> node type
     * @param <E> edge type
     * @return partitioned {@link SaraGraph}
     */
    <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, IdSupplier cellIdSupplier );

    /**
     * Graph to create partitions from
     *
     * @param graph graph to create partitions from
     * @param input settings for preprocessing
     * @param cellIdSupplier id container for cells (serves as a supplier of possible ids)
     * @param progressListener {@link ProgressListener} to report progress
     * @param <N> node type
     * @param <E> edge type
     * @return partitioned {@link SaraGraph}
     */
    <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, IdSupplier cellIdSupplier, ProgressListener progressListener );
}

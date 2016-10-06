/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing;

import cz.certicon.routing.model.basic.MaxIdContainer;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.utils.progress.ProgressListener;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public interface Preprocessor {

    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, MaxIdContainer cellIdContainer );

    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, MaxIdContainer cellIdContainer, ProgressListener progressListener );
}

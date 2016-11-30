/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;

/**
 * interface required for exit or entry edge
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public interface BorderEdge<N extends Node, E extends Edge & BorderEdge<N, E>> {

    /**
     * @return borderData
     */
    BorderData<N, E> getBorder();

    /**
     * sets the border data
     * @param borderData
     */
    void setBorder(BorderData<N, E> borderData);
}

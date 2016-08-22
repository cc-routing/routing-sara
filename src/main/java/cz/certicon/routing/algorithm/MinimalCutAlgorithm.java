/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface MinimalCutAlgorithm {

    public MinimalCut compute( Graph graph, Node source, Node target );
}

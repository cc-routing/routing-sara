/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.model.graph.Graph;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface GraphDAO {

    void saveGraph( Graph graph );

    Graph loadGraph();
}

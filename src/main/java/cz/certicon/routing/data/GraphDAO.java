/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SaraGraph;

import java.io.IOException;

/**
 * An interface defining IO operations for {@link Graph}
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface GraphDAO {

    /**
     * Persist an instance of {@link Graph}
     *
     * @param graph given graph
     * @throws IOException thrown when an IO exception occurs
     */
    void saveGraph( Graph graph ) throws IOException;

    /**
     * Persist an instance of {@link SaraGraph}
     *
     * @param graph given graph
     * @throws IOException thrown when an IO exception occurs
     */
    void saveGraph( SaraGraph graph ) throws IOException;

    /**
     * Load an instance of {@link Graph}
     *
     * @return graph
     * @throws IOException thrown when an IO exception occurs
     */
    Graph loadGraph() throws IOException;

    /**
     * Load an instance of {@link SaraGraph}
     *
     * @return graph
     * @throws IOException thrown when an IO exception occurs
     */
    SaraGraph loadSaraGraph() throws IOException;
}

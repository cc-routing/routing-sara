/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import java.io.IOException;

/**
 * An interface defining method for deletion of all the isolated areas in graph
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface GraphDataUpdater {

    /**
     * Deletes all the nodes and edges contained in the {@link GraphDeleteMessenger}
     *
     * @param graphDeleteMessenger container for node  and edge ids
     * @throws IOException thrown when an IO exception occurs
     */
    void deleteIsolatedAreas( GraphDeleteMessenger graphDeleteMessenger ) throws IOException;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import gnu.trove.list.TLongList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.Value;

/**
 * An interface for IO operations with node and edge data. Used for visualisation and debugging purposes mostly.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface GraphDataDao {

    /**
     * Load data about the node specified by the given id
     *
     * @param nodeId given node id
     * @return node data
     * @throws IOException thrown when an IO exception occurs
     */
    NodeData loadNodeData( long nodeId ) throws IOException;

    /**
     * Load data about the edge specified by the given id
     *
     * @param edgeId given edge id
     * @return edge data
     * @throws IOException thrown when an IO exception occurs
     */
    EdgeData loadEdgeData( long edgeId ) throws IOException;

    /**
     * Messenger for all the necessary node data
     */
    @Value
    class NodeData {

        Coordinate coordinate;
        long id;
        long[] cellIds;
        int edgeCount;
        TLongList incomingEdges;
        TLongList outgoingEdges;
        TurnTable turnTable;
    }

    /**
     * Messenger for all the necessary edge data
     */
    @Value
    class EdgeData {

        List<Coordinate> coordinates;
        long id;
        long source;
        long target;
        Map<Metric, Distance> distanceMap;
    }
}

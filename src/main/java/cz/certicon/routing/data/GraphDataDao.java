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
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public interface GraphDataDao {

    NodeData loadNodeData( long nodeId ) throws IOException;

    EdgeData loadEdgeData( long edgeId ) throws IOException;

    @Value
    public static class NodeData {

        Coordinate coordinate;
        long id;
        long[] cellIds;
        int edgeCount;
        TLongList incomingEdges;
        TLongList outgoingEdges;
        TurnTable turnTable;
    }

    @Value
    public static class EdgeData {

        List<Coordinate> coordinates;
        long id;
        long source;
        long target;
        Map<Metric, Distance> distanceMap;
    }
}

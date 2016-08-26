/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class PartitionGraph implements Graph {

    @Getter( AccessLevel.NONE )
    UndirectedGraph graph;
    @Getter( AccessLevel.NONE )
    Map<Node, Partition> partitionMap;

    @Override
    public int getNodesCount() {
        return graph.getNodesCount();
    }

    @Override
    public Iterator<Node> getNodes() {
        return graph.getNodes();
    }

    @Override
    public int getEdgeCount() {
        return graph.getEdgeCount();
    }

    @Override
    public Iterator<Edge> getEdges() {
        return graph.getEdges();
    }

    @Override
    public Iterator<Edge> getIncomingEdges( Node node ) {
        return graph.getIncomingEdges( node );
    }

    @Override
    public Iterator<Edge> getOutgoingEdges( Node node ) {
        return graph.getOutgoingEdges( node );
    }

    @Override
    public Node getSourceNode( Edge edge ) {
        return graph.getSourceNode( edge );
    }

    @Override
    public Node getTargetNode( Edge edge ) {
        return graph.getTargetNode( edge );
    }

    @Override
    public Node getOtherNode( Edge edge, Node node ) {
        return graph.getOtherNode( edge, node );
    }

    @Override
    public Distance getTurnCost( Node node, Edge from, Edge to ) {
        return graph.getTurnCost( node, from, to );
    }

    @Override
    public Iterator<Edge> getEdges( Node node ) {
        return graph.getEdges( node );
    }

    @Override
    public Coordinate getNodeCoordinate( Node node ) {
        return graph.getNodeCoordinate( node );
    }

    public Partition getPartition( Node node ) {
        if ( !partitionMap.containsKey( node ) ) {
            throw new IllegalArgumentException( "Unknown node: " + node );
        }
        return partitionMap.get( node );
    }
}

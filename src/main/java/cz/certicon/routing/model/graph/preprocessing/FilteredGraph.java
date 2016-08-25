/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import gnu.trove.map.TObjectIntMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class FilteredGraph implements Graph {

    UndirectedGraph graph;
    @Getter( AccessLevel.NONE )
    Map<Node, Set<Node>> nodeMap;
    @Getter( AccessLevel.NONE )
    Map<Edge, Set<Edge>> edgeMap;

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

    public int getNodeSize( Node node ) {
        return nodeMap.get( node ).size();
    }

    public Set<Node> getOrigNodes( Node node ) {
        return nodeMap.get( node );
    }

    public int getEdgeSize( Edge edge ) {
        return edgeMap.get( edge ).size();
    }

    public Set<Edge> getOrigEdges( Edge edge ) {
        return edgeMap.get( edge );
    }

    @Override
    public Coordinate getNodeCoordinate( Node node ) {
        return graph.getNodeCoordinate( node );
    }
}

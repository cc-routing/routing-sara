/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import java.util.HashMap;
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
public class SaraGraph implements Graph {

    private final UndirectedGraph graph;
    @Getter( AccessLevel.NONE )
    private final Map<Node, NodeInfo> nodeInfoMap;

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

    public NodeInfo getNodeInfo( Node node ) {
        if ( !nodeInfoMap.containsKey( node ) ) {
            throw new IllegalArgumentException( "Unknown node: " + node );
        }
        return nodeInfoMap.get( node );
    }

    @Override
    public Iterator<Edge> getEdges( Node node ) {
        return graph.getEdges( node );
    }

    @Override
    public Coordinate getNodeCoordinate( Node node ) {
        return graph.getNodeCoordinate( node );
    }

//    @Override
//    public Graph copy() {
//        Graph graphCopy = graph.copy();
//        Map<Node, NodeInfo> nodeInfoMapCopy = new HashMap<>();
//        Iterator<Node> nodes = graphCopy.getNodes();
//        while ( nodes.hasNext() ) {
//            Node node = nodes.next();
//            nodeInfoMapCopy.put( node, nodeInfoMap.get( node ) );
//        }
//        return new SaraGraph( (UndirectedGraph) graphCopy, nodeInfoMapCopy );
//    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.Iterator;
import java.util.HashMap;
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
    private final Map<SimpleNode, NodeInfo> nodeInfoMap;

    @Override
    public int getNodesCount() {
        return graph.getNodesCount();
    }

    @Override
    public Iterator<SimpleNode> getNodes() {
        return graph.getNodes();
    }

    @Override
    public int getEdgeCount() {
        return graph.getEdgeCount();
    }

    @Override
    public Iterator<SimpleEdge> getEdges() {
        return graph.getEdges();
    }

    @Override
    public Iterator<SimpleEdge> getIncomingEdges( SimpleNode node ) {
        return graph.getIncomingEdges( node );
    }

    @Override
    public Iterator<SimpleEdge> getOutgoingEdges( SimpleNode node ) {
        return graph.getOutgoingEdges( node );
    }

    @Override
    public SimpleNode getSourceNode( SimpleEdge edge ) {
        return graph.getSourceNode( edge );
    }

    @Override
    public SimpleNode getTargetNode( SimpleEdge edge ) {
        return graph.getTargetNode( edge );
    }

    @Override
    public SimpleNode getOtherNode( SimpleEdge edge, SimpleNode node ) {
        return graph.getOtherNode( edge, node );
    }

    @Override
    public Distance getTurnCost( SimpleNode node, SimpleEdge from, SimpleEdge to ) {
        return graph.getTurnCost( node, from, to );
    }

    public NodeInfo getNodeInfo( SimpleNode node ) {
        if ( !nodeInfoMap.containsKey( node ) ) {
            throw new IllegalArgumentException( "Unknown node: " + node );
        }
        return nodeInfoMap.get( node );
    }

    @Override
    public Iterator<SimpleEdge> getEdges( SimpleNode node ) {
        return graph.getEdges( node );
    }

    @Override
    public Coordinate getNodeCoordinate( SimpleNode node ) {
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

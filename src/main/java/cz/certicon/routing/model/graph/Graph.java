/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;
import java.util.Iterator;

/**
 * Definition of graph
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface Graph {

    /**
     * Returns amount of nodes in the graph
     *
     * @return amount of nodes in the graph
     */
    int getNodesCount();

    /**
     * Returns nodes' iterator
     *
     * @return nodes' iterator
     */
    Iterator<Node> getNodes();

    /**
     * Returns amount of edges in the graph
     *
     * @return amount of edges in the graph
     */
    int getEdgeCount();

    /**
     * Returns edges' iterator
     *
     * @return edges' iterator
     */
    Iterator<Edge> getEdges();

    /**
     * Returns iterator of edges adjacent to the given node
     *
     * @param node given node
     * @return iterator of node's edges
     */
    Iterator<Edge> getEdges( Node node );

    /**
     * Returns iterator of edges incoming to the given node
     *
     * @param node given node
     * @return iterator of incoming edges
     */
    Iterator<Edge> getIncomingEdges( Node node );

    /**
     * Returns iterator of edges outgoing from the given node
     *
     * @param node given node
     * @return iterator of outgoing edges
     */
    Iterator<Edge> getOutgoingEdges( Node node );

    /**
     * Returns source node of the given edge
     *
     * @param edge given edge
     * @return source node
     */
    Node getSourceNode( Edge edge );

    /**
     * Returns target node of the given edge
     *
     * @param edge given edge
     * @return target node
     */
    Node getTargetNode( Edge edge );

    /**
     * Returns other node (node on the opposite side from the given node) of the
     * given edge
     *
     * @param edge given edge
     * @param node given node
     * @return the other node
     */
    Node getOtherNode( Edge edge, Node node );

    /**
     * Returns cost of the turn from edge to edge via the given node
     *
     * @param node crossroad node
     * @param from from (turn origin) edge
     * @param to to (turn destination) edge
     * @return cost of the turn
     */
    public Distance getTurnCost( Node node, Edge from, Edge to );
}

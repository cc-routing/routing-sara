/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.UndirectedGraph;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class FilteredGraph extends UndirectedGraph<ContractNode, ContractEdge> {

    public FilteredGraph( Set<ContractNode> nodes, Set<ContractEdge> edges ) {
        super( nodes, edges );
    }

    public int getNodeSize( ContractNode node ) {
        return getOrigNodes( node ).size();
    }

    public Collection<Node> getOrigNodes( ContractNode node ) {
        return node.getNodes();
    }

    public int getEdgeSize( ContractEdge edge ) {
        return getOrigEdges( edge ).size();
    }

    public Collection<Edge> getOrigEdges( ContractEdge edge ) {
        return edge.getEdges();
    }

}

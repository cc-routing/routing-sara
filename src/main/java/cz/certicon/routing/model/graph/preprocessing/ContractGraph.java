/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.AbstractUndirectedGraph;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Distance;
import java.util.Collection;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ContractGraph extends AbstractUndirectedGraph<ContractNode, ContractEdge> {

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

    public ContractNode createNode( long id, Collection<Node> origNodes ) {
        ContractNode node = new ContractNode( this, id, origNodes );
        addNode( node );
        return node;
    }

    public ContractEdge createEdge( long id, boolean oneway, ContractNode source, ContractNode target, Collection<Edge> origEdges, Pair<Metric, Distance>... metrics ) {
        ContractEdge edge = new ContractEdge( this, id, oneway, source, target, origEdges );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            setLength( metric.a, edge, metric.b );
        }
        return edge;
    }

}

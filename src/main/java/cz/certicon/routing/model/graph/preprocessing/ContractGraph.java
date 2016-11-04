/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph.preprocessing;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.AbstractUndirectedGraph;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.StringUtils;

import java.util.Collection;
import java.util.Set;

/**
 * An implementation of {@link Graph}, where nodes and edges can contain other nodes and edges (collections) and they can be merged together (perform so-called contractions)
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ContractGraph extends AbstractUndirectedGraph<ContractNode, ContractEdge> {

    /**
     * Empty constructor
     */
    public ContractGraph() {
    }

    /**
     * Constructor with the given set of metrics. There metrics must be set for each edge before locking the graph.
     *
     * @param metrics metrics
     */
    public ContractGraph( Set<Metric> metrics ) {
        super( metrics );
    }

    /**
     * Creates new node in this graph
     *
     * @param id        id of the node
     * @param origNodes original nodes the node should contain
     * @return new instance of {@link ContractNode}
     */
    public ContractNode createNode( long id, Collection<? extends Node> origNodes ) {
        ContractNode node = new ContractNode( this, id, origNodes );
        addNode( node );
        return node;
    }

    /**
     * Creates new edge in this graph
     *
     * @param id        id of the edge
     * @param oneway    is the edge oneway only?
     * @param source    source node of the edge
     * @param target    target node of the edge
     * @param origEdges original edges the edge should contain
     * @param metrics   metrics with distances
     * @return new instance of {@link ContractEdge}
     */
    @SafeVarargs
    public final ContractEdge createEdge( long id, boolean oneway, ContractNode source, ContractNode target, Collection<? extends Edge> origEdges, Pair<Metric, Distance>... metrics ) {
        ContractEdge edge = new ContractEdge( this, id, oneway, source, target, origEdges );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            edge.setLength( metric.a, metric.b );
        }
        return edge;
    }

    @Override
    protected String additionalToStringData() {
        return super.additionalToStringData() + ", node_origs=" + StringUtils.toArray( getNodeCollection(), new StringUtils.StringExtractor<ContractNode>() {
            @Override
            public String toString( ContractNode item ) {
                return item.getId() + "->" + item.getNodes().size();
            }
        } );
    }

    @Override
    public Graph<ContractNode, ContractEdge> newInstance( Set<Metric> metrics ) {
        return new ContractGraph( metrics );
    }

}

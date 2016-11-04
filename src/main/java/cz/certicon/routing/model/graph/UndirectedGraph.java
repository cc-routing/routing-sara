/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.values.Distance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.NonNull;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class UndirectedGraph extends AbstractUndirectedGraph<SimpleNode, SimpleEdge> {

    public UndirectedGraph() {
        super();
    }

    public UndirectedGraph( Collection<Metric> metrics ) {
        super( metrics );
    }

    public SimpleNode createNode( long id ) {
        SimpleNode node = new SimpleNode( this, id );
        addNode( node );
        return node;
    }

    @SafeVarargs
    public final SimpleEdge createEdge( long id, boolean oneway, @NonNull SimpleNode source, @NonNull SimpleNode target, int sourceIndex, int targetIndex, Pair<Metric, Distance>... metrics ) {
        SimpleEdge edge = new SimpleEdge( this, id, oneway, source, target, sourceIndex, targetIndex );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            edge.setLength( metric.a, metric.b );
        }
        return edge;
    }

    public SimpleEdge createEdge( long id, boolean oneway, @NonNull SimpleNode source, @NonNull SimpleNode target, int sourceIndex, int targetIndex, Collection<Pair<Metric, Distance>> metrics ) {
        SimpleEdge edge = new SimpleEdge( this, id, oneway, source, target, sourceIndex, targetIndex );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            edge.setLength( metric.a, metric.b );
        }
        return edge;
    }

    @Override
    public Graph<SimpleNode, SimpleEdge> newInstance( Set<Metric> metrics ) {
        return new UndirectedGraph( metrics );
    }

    public static <N extends Node, E extends Edge> UndirectedGraph fromGraph( Graph<N, E> graph ) {
        UndirectedGraph undirectedGraph = new UndirectedGraph( graph.getMetrics() );
        for ( N node : graph.getNodes() ) {
            SimpleNode createNode = undirectedGraph.createNode( node.getId() );
            createNode.setCoordinate( node.getCoordinate() );
            createNode.setTurnTable( node.getTurnTable() );
        }
        for ( E edge : graph.getEdges() ) {
            List<Pair<Metric, Distance>> distPairs = new ArrayList<>();
            for ( Metric metric : graph.getMetrics() ) {
                distPairs.add( new Pair<>( metric, edge.getLength( metric ) ) );
            }
            undirectedGraph.createEdge( edge.getId(), edge.isOneWay(),
                    undirectedGraph.getNodeById( edge.getSource().getId() ),
                    undirectedGraph.getNodeById( edge.getTarget().getId() ),
                    edge.getSourcePosition(), edge.getTargetPosition(), distPairs );
        }
        undirectedGraph.lock();
        return undirectedGraph;
    }
}

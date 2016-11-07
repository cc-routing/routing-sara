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
 * Basic undirected implementation of the {@link Graph} interface
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class UndirectedGraph extends AbstractUndirectedGraph<SimpleNode, SimpleEdge> {

    public UndirectedGraph() {
        super();
    }

    public UndirectedGraph( Collection<Metric> metrics ) {
        super( metrics );
    }


    /**
     * Creates and returns new node for the given id
     *
     * @param id node's id
     * @return new node
     */
    public SimpleNode createNode( long id ) {
        SimpleNode node = new SimpleNode( this, id );
        addNode( node );
        return node;
    }

    /**
     * Creates and returns new edge for the given data
     *
     * @param id          edge's id
     * @param oneway      whether the edge is oneway or twoway
     * @param source      edge's source node
     * @param target      edge's target node
     * @param sourceIndex index to source's turn-table
     * @param targetIndex index to target's turn-table
     * @param metrics     array of distances and metrics
     * @return new edge
     */
    @SafeVarargs
    public final SimpleEdge createEdge( long id, boolean oneway, @NonNull SimpleNode source, @NonNull SimpleNode target, int sourceIndex, int targetIndex, Pair<Metric, Distance>... metrics ) {
        SimpleEdge edge = new SimpleEdge( this, id, oneway, source, target, sourceIndex, targetIndex );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            edge.setLength( metric.a, metric.b );
        }
        return edge;
    }

    /**
     * Creates and returns new edge for the given data
     *
     * @param id          edge's id
     * @param oneway      whether the edge is oneway or twoway
     * @param source      edge's source node
     * @param target      edge's target node
     * @param sourceIndex index to source's turn-table
     * @param targetIndex index to target's turn-table
     * @param metrics     collection of distances and metrics
     * @return new edge
     */
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

    /**
     * Creates new instance of [@link {@link UndirectedGraph} from any given {@link Graph}
     *
     * @param graph given graph
     * @param <N>   node type
     * @param <E>   edge type
     * @return new instance of {@link UndirectedGraph}
     */
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

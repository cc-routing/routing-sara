/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data.processor;

import cz.certicon.routing.data.GraphDeleteMessenger;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.utils.collections.Iterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class GraphComponentSearcher {

    public <N extends Node<N, E>, E extends Edge<N, E>> GraphDeleteMessenger findAllButLargest( Graph<N, E> graph ) {
        List<Collection<N>> componentList = new ArrayList<>();
        Set<N> nodes = new HashSet<>();
        for ( N node : graph.getNodes() ) {
            nodes.add( node );
        }
        while ( !nodes.isEmpty() ) {
            N startNode = getAny( nodes );
            Collection<N> component = new ArrayList<>();
            componentList.add( component );
            Queue<N> queue = new LinkedList<>();
            queue.add( startNode );
            while ( !queue.isEmpty() ) {
                N node = queue.poll();
                nodes.remove( node );
                component.add( node );
                for ( E edge : node.getEdges() ) {
                    N target = edge.getOtherNode( node );
                    if ( nodes.contains( target ) ) {
                        queue.add( target );
                    }
                }
            }
        }
        Collection<N> maxComponent = null;
        for ( Collection<N> component : componentList ) {
            if ( maxComponent == null ) {
                maxComponent = component;
            } else if ( maxComponent.size() < component.size() ) {
                maxComponent = component;
            }
        }
        componentList.remove( maxComponent );
        Set<Long> removeNodes = new HashSet<>();
        Set<Long> removeEdges = new HashSet<>();
        for ( Collection<N> collection : componentList ) {
            for ( N node : collection ) {
                removeNodes.add( node.getId() );
                for ( E edge : node.getEdges() ) {
                    removeEdges.add( edge.getId() );
                }
            }
        }
        return new GraphDeleteMessenger( removeNodes, removeEdges );
    }

    private <N extends Node> N getAny( Collection<N> nodes ) {
        if ( nodes.isEmpty() ) {
            throw new NoSuchElementException( "The collection is empty." );
        }
        return nodes.iterator().next();
    }
}

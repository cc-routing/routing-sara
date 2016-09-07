/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.Partition;
import cz.certicon.routing.model.graph.PartitionGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class GraphUtils {

    public static Collection<Collection<SimpleEdge>> getCutEdges( PartitionGraph graph ) {
        Set<Collection<SimpleEdge>> result = new HashSet<>();
        Set<SimpleNode> nodeSet = new HashSet<>();
        Queue<SimpleNode> queue = new LinkedList<>();
        for ( SimpleNode node : graph.getNodes() ) {
            nodeSet.add( node );
        }
        while ( !nodeSet.isEmpty() ) {
            SimpleNode first = nodeSet.iterator().next();
            queue.add( first );
            nodeSet.remove( first );
            List<SimpleEdge> cutEdges = new ArrayList<>();
            while ( !queue.isEmpty() ) {
                SimpleNode node = queue.poll();
                Partition partition = graph.getPartition( node );
                for ( SimpleEdge edge : graph.getEdges( node ) ) {
                    SimpleNode target = graph.getOtherNode( edge, node );
                    if ( partition.equals( graph.getPartition( target ) ) ) {
                        if ( nodeSet.contains( target ) ) {
                            queue.add( target );
                            nodeSet.remove( node );
                        }
                    } else {
                        cutEdges.add( edge );
                    }
                }
            }
            result.add( cutEdges );
        }
        return result;
    }

    public static Collection<Collection<SimpleNode>> getBorderNodes( PartitionGraph graph ) {
        Collection<Collection<SimpleNode>> collections = new ArrayList<>();
        for ( Partition partition : graph.getPartitions() ) {
            Collection<SimpleNode> borderNodes = new ArrayList<>();
            for ( SimpleNode node : partition.getNodes() ) {
                for ( SimpleEdge edge : graph.getEdges( node ) ) {
                    SimpleNode target = graph.getOtherNode( edge, node );
                    if ( !partition.equals( graph.getPartition( target ) ) ) {
                        borderNodes.add( node );
                        break;
                    }
                }
            }
            collections.add( borderNodes );
        }
        return collections;
    }
}

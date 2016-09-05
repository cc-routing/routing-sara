/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
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

    public static Collection<Collection<Edge>> getCutEdges( PartitionGraph graph ) {
        Set<Collection<Edge>> result = new HashSet<>();
        Set<Node> nodeSet = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        for ( Node node : graph.getNodes() ) {
            nodeSet.add( node );
        }
        while ( !nodeSet.isEmpty() ) {
            Node first = nodeSet.iterator().next();
            queue.add( first );
            nodeSet.remove( first );
            List<Edge> cutEdges = new ArrayList<>();
            while ( !queue.isEmpty() ) {
                Node node = queue.poll();
                Partition partition = graph.getPartition( node );
                for ( Edge edge : graph.getEdges( node ) ) {
                    Node target = graph.getOtherNode( edge, node );
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
}

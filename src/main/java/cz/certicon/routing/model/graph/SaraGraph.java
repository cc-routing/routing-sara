/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SaraGraph extends UndirectedGraph<SaraNode, SaraEdge> {

    public SaraGraph( Collection<SaraNode> nodes, Collection<SaraEdge> edges, Map<Metric, Map<Edge, Distance>> metricMap ) {
        super( nodes, edges, metricMap );
    }

    public Cell getParent( SaraNode node ) {
        return node.getParent();
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

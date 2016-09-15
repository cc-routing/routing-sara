/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.values.Distance;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SaraGraph extends AbstractUndirectedGraph<SaraNode, SaraEdge> {

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
    public SaraNode createNode( long id, Cell parent ) {
        SaraNode node = new SaraNode( this, id, parent );
        addNode( node );
        return node;
    }

    public SaraEdge createEdge( long id, boolean oneway, SaraNode source, SaraNode target, int sourceIndex, int targetIndex, Pair<Metric, Distance>... metrics ) {
        SaraEdge edge = new SaraEdge( this, id, oneway, source, target, sourceIndex, targetIndex );
        addEdge( edge );
        for ( Pair<Metric, Distance> metric : metrics ) {
            setLength( metric.a, edge, metric.b );
        }
        return edge;
    }
}

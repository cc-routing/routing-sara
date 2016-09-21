/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.AbstractUndirectedGraph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import java.util.Set;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayGraph extends AbstractUndirectedGraph<OverlayNode, OverlayEdge> {

    public OverlayGraph(OverlayBuilder builder) {
        super(builder.metrics);
    }

    public OverlayNode addNode(OverlayColumn column, BorderNodeMap map, SaraEdge edge) {
        OverlayNode node = new OverlayNode(this, column, map, edge);
        this.addNode(node);
        return node;
    }

    public OverlayEdge addEdge(OverlayNode source, OverlayNode target) {
        OverlayEdge edge = new OverlayEdge(this, this.getEdgeCount(), source, target);
        this.addEdge(edge);
        return edge;
    }

    @Override
    protected AbstractUndirectedGraph<OverlayNode, OverlayEdge> newInstance(Set<Metric> metrics) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

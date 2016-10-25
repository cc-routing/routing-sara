/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.AbstractUndirectedGraph;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import java.util.Set;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayGraph extends AbstractUndirectedGraph<OverlayNode, OverlayEdge> {

    private long maxEdgeId = 0;

    /**
     * adding edges with id is locked
     */
    private boolean lockEdgesById = false;

    public OverlayGraph(OverlayBuilder builder) {
        super(builder.metrics);
    }

    public OverlayNode addNode(OverlayColumn column, BorderNodeMap map, SaraEdge edge) {
        OverlayNode node = new OverlayNode(this, column, map, edge);
        this.addNode(node);
        return node;
    }

    public OverlayNode addNode(long id) {
        OverlayNode node = new OverlayNode(this, id);
        this.addNode(node);
        return node;
    }

    public OverlayEdge addEdge(OverlayNode source, OverlayNode target) {
        this.lockEdgesById = true;
        OverlayEdge edge = new OverlayEdge(this, ++this.maxEdgeId, source, target);
        this.addEdge(edge);
        return edge;
    }

    public OverlayEdge addEdge(long id, OverlayNode source, OverlayNode target) {
        if (this.lockEdgesById) {
            throw new IllegalStateException("add overlay edge by id is locked");
        }
        long absId = Math.abs(id);
        if (absId > this.maxEdgeId) {
            this.maxEdgeId = absId;
        }

        OverlayEdge edge = new OverlayEdge(this, id, source, target);
        this.addEdge(edge);
        return edge;
    }

    public OverlayEdge addCopy(OverlayEdge item) {
        OverlayEdge edge = new OverlayEdge(this, item);
        this.addEdge(edge);
        return edge;
    }

    @Override
    public Graph<OverlayNode, OverlayEdge> newInstance(Set<Metric> metrics) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

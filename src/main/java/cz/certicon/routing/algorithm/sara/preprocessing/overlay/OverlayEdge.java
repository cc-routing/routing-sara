/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.AbstractEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Overlaygraph contains two kind of OverlayEdges: CellEdge - inside cell and
 * BorderEdge between cells.
 *
 * @author Blahoslav Potoček <potocek@merica.cz> Edge for the OverlayGraph.
 *
 */
public class OverlayEdge extends AbstractEdge<OverlayNode, OverlayEdge> implements BorderEdge<OverlayNode, OverlayEdge> {

    /**
     * underlaying border SaraEdge
     */
    @Getter
    private ZeroEdge zeroEdge = null;

    // defined only for border edge
    private OverlayBorder oBorder;

    /**
     * in memory storage of shortcuts in L1 caluclated in L0
     */
    private List<SaraEdge>[] zeroRoutes;

    /**
     * in memory storage of shortcuts in L2+ calculated in N-1
     */
    private List<OverlayEdge>[] overlayRoutes;

    private OverlayEdge(OverlayGraph graph, long id, OverlayNode source, OverlayNode target) {
        super(graph, id, true, source, target, -1, -1);
    }

    public OverlayEdge(OverlayGraph graph, OverlayNode source, OverlayNode target) {
        super(graph, graph.getLayer().getNextEdgeId(), true, source, target, -1, -1);
        if (OverlayBuilder.keepShortcuts) {
            int len = Metric.values().length;
            this.zeroRoutes = new ArrayList[len];
            this.overlayRoutes = new ArrayList[len];
        }
    }

    public OverlayEdge(OverlayGraph graph, OverlayLift lift, OverlayNode source, OverlayNode target) {
        this(graph, lift.getEdgeId(), source, target);

        graph.getLayer().checkEdgeId(this.getId());

        this.zeroEdge = lift.getEdge();
        for (Metric metric : this.getGraph().getMetrics()) {
            Distance distance = lift.getEdge().getLength(metric);
            this.setLength(metric, distance);
        }
    }

    @Override
    public Distance getTurnDistance(OverlayNode node, TurnTable turnTable, OverlayEdge targetEdge) {
        return Distance.newInstance(0);
    }

    @Override
    protected OverlayEdge newInstance(Graph<OverlayNode, OverlayEdge> newGraph, long id, boolean oneway, OverlayNode newSource, OverlayNode newTarget, int sourceIndex, int targetIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BorderData<OverlayNode, OverlayEdge> getBorder() {
        return this.oBorder;
    }

    @Override
    public void setBorder(BorderData<OverlayNode, OverlayEdge> border) {
        this.oBorder = (OverlayBorder) border;
    }

    public OverlayBorder border() {
        return this.oBorder;
    }

    public void setZeroRoute(Metric metric, List<SaraEdge> route) {
        int idx = metric.ordinal();
        this.zeroRoutes[idx] = route;
    }

    public void setOverlayRoute(Metric metric, List<OverlayEdge> route) {
        int idx = metric.ordinal();
        this.overlayRoutes[idx] = route;
    }

    public List<SaraEdge> getZeroRoute(Metric metric) {
        if (this.zeroRoutes == null) {
            return null;
        } else {
            int idx = metric.ordinal();
            return this.zeroRoutes[idx];
        }
    }

    public List<OverlayEdge> getOverlayRoute(Metric metric) {
        if (this.overlayRoutes == null) {
            return null;
        } else {
            int idx = metric.ordinal();
            return this.overlayRoutes[idx];
        }
    }
}

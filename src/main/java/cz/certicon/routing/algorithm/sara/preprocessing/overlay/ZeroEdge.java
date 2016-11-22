/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.values.Coordinate;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class ZeroEdge extends SaraEdge implements BorderEdge<ZeroNode, ZeroEdge> {

    private static int maxUid = 0;

    public final int uid = ++ZeroEdge.maxUid;

    private ZeroBorder zBorder;

    public ZeroEdge(ZeroGraph graph, long id, boolean oneWay, ZeroNode source, ZeroNode target, int sourceIndex, int targetIndex) {
        super(graph, id, oneWay, source, target, sourceIndex, targetIndex);
    }

    public ZeroEdge(ZeroGraph graph, SaraEdge edge) {
        this(graph, edge,
                (ZeroNode) graph.getNodeById(edge.getSource().getId()),
                (ZeroNode) graph.getNodeById(edge.getTarget().getId())
        );
    }

    public ZeroEdge(ZeroGraph graph, SaraEdge edge, ZeroNode source, ZeroNode target) {
        this(graph, edge.getId(), edge.isOneWay(),
                source, target,
                edge.getSourcePosition(), edge.getTargetPosition());
        this.copyMetric(edge);
    }

    public ZeroEdge copyMetric(SaraEdge edge) {
        for (Metric metric : this.getGraph().getMetrics()) {
            this.setLength(metric, edge.getLength(metric));
        }

        return this;
    }

    @Override
    public BorderData<ZeroNode, ZeroEdge> getBorder() {
        return this.zBorder;
    }

    @Override
    public void setBorder(BorderData<ZeroNode, ZeroEdge> border) {
        this.zBorder = (ZeroBorder) border;
    }

    public ZeroBorder border() {
        return this.zBorder;
    }

    public Coordinate getCenter() {
        Coordinate c1 = this.getSource().getCoordinate();
        Coordinate c2 = this.getTarget().getCoordinate();
        double lon = (c1.getLongitude() + c2.getLongitude()) / 2;
        double lat = (c1.getLatitude() + c2.getLatitude()) / 2;
        return new Coordinate(lat, lon);
    }
}

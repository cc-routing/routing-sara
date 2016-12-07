/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.OneToAllRoutingAlgorithm;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java8.util.Optional;
import lombok.Getter;

/**
 * Portion of the graph at level 0.
 * Overlay input - SaraGraph is split by cells at level 1.
 * Each cell at level 1 contains its part of the ZeroGraph.
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class ZeroGraph extends SaraGraph {

    private static Distance stop = Distance.newInfinityInstance();
    private static Distance go = Distance.newInstance(0);
    private static Distance len = Distance.newInstance(1);

    static TurnTable[] turns = new TurnTable[]{
        null,
        new TurnTable(new Distance[][]{{stop}}),
        new TurnTable(new Distance[][]{
            {stop, go},
            {go, stop}
        }),
        new TurnTable(new Distance[][]{
            {stop, go, go},
            {go, stop, go},
            {go, go, stop}
        })
    };

    /**
     * related cell, owner of this part of the graph
     */
    @Getter
    private final OverlayCell cell;

    public ZeroGraph(OverlayCell cell) {
        super(cell.getLayer().getBuilder().getMetrics());
        this.cell = cell;
    }

    public OverlayBuilder getBuilder() {
        return this.cell.getLayer().getBuilder();
    }


    private ZeroNode addNodeCopy(SaraNode node) {
        ZeroNode zNode = (ZeroNode) this.getNodeById(node.getId());
        if (zNode == null) {
            zNode = new ZeroNode(this, node);
            this.addNode(zNode);
        }
        return zNode;
    }

    private ZeroNode addNodeCopy(SaraNode node, long id) {
        ZeroNode zNode = (ZeroNode) this.getNodeById(id);

        if (zNode != null) {
            throw new IllegalStateException("zero node");
        }

        zNode = new ZeroNode(this, node, id);
        this.addNode(zNode);

        return zNode;
    }

    public ZeroEdge addEdgeCopy(SaraEdge edge) {
        ZeroNode s = this.addNodeCopy(edge.getSource());
        ZeroNode t = this.addNodeCopy(edge.getTarget());

        ZeroEdge zEdge = new ZeroEdge(this, edge);
        this.addEdge(zEdge);
        return zEdge;
    }

    public ZeroEdge addExitCopy(SaraEdge edge) {
        ZeroNode s = this.addNodeCopy(edge.getSource());
        ZeroNode t = this.addNodeCopy(edge.getTarget(), -edge.getId());

        ZeroEdge zEdge = new ZeroEdge(this, -edge.getId(), edge.isOneWay(), s, t, edge.getSourcePosition(), 0);
        zEdge.copyMetric(edge);
        this.addEdge(zEdge);

        return zEdge;
    }

    public ZeroEdge addEntryCopy(SaraEdge edge) {
        ZeroNode s = this.addNodeCopy(edge.getSource(), -edge.getId());
        ZeroNode t = this.addNodeCopy(edge.getTarget());

        ZeroEdge zEdge = new ZeroEdge(this, edge.getId(), edge.isOneWay(), s, t, 0, edge.getTargetPosition());
        zEdge.copyMetric(edge);
        this.addEdge(zEdge);

        return zEdge;
    }

    /**
     * calculates shortcuts and assing metrics for CellEdges for level 1
     */
    public void customizeFirstLevel() {

        OverlayCell data = this.cell;

        boolean uTurnsAllowed = true;

        for (Metric metric : this.getMetrics()) {

            for (OverlayNode entryNode : data.getEntryNodes()) {

                Map<SaraEdge, OneToAllRoutingAlgorithm.Direction> targets = new HashMap<>();
                Map<SaraEdge, OverlayEdge> mapper = new HashMap<>();

                OverlayLift begLift = entryNode.getLift();
                ZeroEdge begEdge = begLift.getEdge();

                OneToAllRoutingAlgorithm.Direction begDir = begLift.getDirection();

                for (OverlayEdge cellEdge : entryNode.getOutgoingEdges()) {

                    OverlayNode exitNode = cellEdge.getTarget();
                    OverlayLift endLift = exitNode.getLift();

                    boolean uTurnEdge = begLift.getGroupId() == endLift.getGroupId();

                    if (!uTurnsAllowed && uTurnEdge) {
                        // two-way L0 SaraEdge is split in two L1 OverlayEdges
                        // U-turn in this case is forbidden
                        data.getLayer().forbiddenRoutes++;
                        Distance distance = Distance.newInfinityInstance();
                        cellEdge.setLength(metric, distance);
                    } else {
                        ZeroEdge endEdge = endLift.getEdge();
                        OneToAllRoutingAlgorithm.Direction endDir = endLift.getDirection();
                        targets.put(endEdge, endDir);
                        mapper.put(endEdge, cellEdge);
                    }
                }

                Map<SaraEdge, Optional<Route<SaraNode, SaraEdge>>> routeMap
                        = this.getBuilder().getOneToAll().route(metric, begEdge, begDir, targets);

                for (Map.Entry<SaraEdge, Optional<Route<SaraNode, SaraEdge>>> entry : routeMap.entrySet()) {

                    SaraEdge endEdge = entry.getKey();
                    OverlayEdge cellEdge = mapper.get(endEdge);
                    Optional<Route<SaraNode, SaraEdge>> result = entry.getValue();
                    OneToAllRoutingAlgorithm.Direction endDir = targets.get(endEdge);

                    Distance distance;

                    if (result.isPresent()) {

                        List<SaraEdge> edges = result.get().getEdgeList();
                        distance = this.sumSaraDistance(edges, metric);
                        if (this.getBuilder().isKeepShortcuts()) {
                            cellEdge.setZeroRoute(metric, edges);
                        }
                        data.getLayer().validRoutes++;

                    } else {
                        distance = Distance.newInfinityInstance();
                        data.getLayer().invalidRoutes++;
                    }

                    cellEdge.setLength(metric, distance);
                }
            } // Metric
        }
    }

    /**
     * sum route distance
     *
     * @param edges
     * @param metric
     * @return route distance
     */
    public Distance sumSaraDistance(List<SaraEdge> edges, Metric metric) {
        Distance distance = Distance.newInstance(0);
        for (int idx = 1; idx < edges.size() - 1; idx++) {
            SaraEdge edge = edges.get(idx);
            Distance value = edge.getLength(metric);
            distance = distance.add(value);
        }

        return distance;
    }

    /**
     * maps finds ZeroEdge relatred to the SaraEdge
     * @param saraEdge
     * @return
     */
    public ZeroEdge getZeroEdge(SaraEdge saraEdge) {

        long id = saraEdge.getId();
        SaraEdge zeroEdge = this.getEdgeById(id);
        if (zeroEdge == null) {
            zeroEdge = this.getEdgeById(-id);
        }

        return (ZeroEdge) zeroEdge;
    }
}

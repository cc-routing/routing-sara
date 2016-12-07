/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.OneToAllRoutingAlgorithm;
import cz.certicon.routing.algorithm.OneToAllRoutingAlgorithm.Direction;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.AbstractUndirectedGraph;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Distance;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java8.util.Optional;
import lombok.Getter;

/**
 * Directed OverlayGraph in layers 1-N. Has no turn constrains.
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayGraph extends AbstractUndirectedGraph<OverlayNode, OverlayEdge> {

    @Getter
    private final OverlayCell cell;

    public OverlayGraph(OverlayCell cell) {
        super(cell.getLayer().getBuilder().getMetrics());
        this.cell = cell;
    }

    public OverlayNode addNode(OverlayLift col, OverlayCell cell) {
        OverlayNode node = new OverlayNode(this, col, cell);
        this.addNode(node);
        return node;
    }

    public OverlayEdge addEdge(OverlayNode source, OverlayNode target) {
        OverlayEdge edge = new OverlayEdge(this, source, target);
        this.addEdge(edge);
        return edge;
    }

    public OverLayer getLayer() {
        return this.cell.getLayer();
    }

    public OverlayBuilder getBuilder() {
        return this.getLayer().getBuilder();
    }

    public OverlayEdge addBorder(OverlayLift lift, OverlayCell otherCell) {

        OverlayNode source = null;
        OverlayNode target = null;

        if (lift.isExit()) {
            source = this.addNode(lift, null);
            this.cell.addExitNode(source);

            target = this.addNode(lift, otherCell);

        } else {
            target = this.addNode(lift, null);
            this.cell.addEntryNode(target);

            source = this.addNode(lift, otherCell);
        }

        OverlayEdge edge = new OverlayEdge(this, lift, source, target);
        this.addEdge(edge);

        return edge;
    }

    /**
     * calculates shortcuts and assing metrics for CellEdges for levels 2+
     */
    public void customizeUpperLevel() {

        int p = 2;

        //devel only
        boolean byOne = p == 1;
        boolean byMany = p == 2;

        //OverlayGraph full = this.parent.partitions.get(this.level - 1).overlayGraph;
        for (Metric metric : this.getBuilder().getMetrics()) {

            for (OverlayNode entryNode : this.cell.getEntryNodes()) {

                Map<OverlayEdge, OverlayEdge> mapper = new HashMap<>();
                Map<OverlayEdge, OneToAllRoutingAlgorithm.Direction> targets = new HashMap<>();

                OverlayNode begNode = entryNode.getLowerNode();
                OverlayEdge begEdge = begNode.getIncomingEdges().next();
                Direction begDir = Direction.FORWARD;

                for (OverlayEdge cellEdge : entryNode.getOutgoingEdges()) {

                    OverlayNode exitNode = cellEdge.getTarget();
                    OverlayNode endNode = exitNode.getLowerNode();
                    OverlayEdge endEdge = endNode.getOutgoingEdges().next();

                    Direction endDir = Direction.FORWARD;

                    targets.put(endEdge, endDir);
                    mapper.put(endEdge, cellEdge);

                    if (byOne) {

                        Optional<Route<OverlayNode, OverlayEdge>> result
                                = this.getBuilder().getOneToOne().route(metric, begEdge.getSource(), endEdge.getTarget());

                        this.setResult(metric, cellEdge, result);
                    }
                }

                if (byMany) {

                    Map<OverlayEdge, Optional<Route<OverlayNode, OverlayEdge>>> routeMap
                            = this.getBuilder().getOneToAll().route(metric, begEdge, begDir, targets);

                    for (Map.Entry<OverlayEdge, Optional<Route<OverlayNode, OverlayEdge>>> entry : routeMap.entrySet()) {

                        Optional<Route<OverlayNode, OverlayEdge>> result = entry.getValue();

                        OverlayEdge endEdge = entry.getKey();
                        OverlayEdge cellEdge = mapper.get(endEdge);
                        this.setResult(metric, cellEdge, result);
                    }
                }
            } // Metric
        }
    }

    /**
     * Assigns shortcut to CellEdge
     * @param metric
     * @param cellEdge
     * @param result
     */
    private void setResult(Metric metric, OverlayEdge cellEdge, Optional<Route<OverlayNode, OverlayEdge>> result) {

        Distance distance;

        if (result.isPresent()) {
            Route<OverlayNode, OverlayEdge> route = result.get();
            List<OverlayEdge> edges = route.getEdgeList();
            if (this.getBuilder().isKeepShortcuts()) {
                cellEdge.setOverlayRoute(metric, edges);
            }
            distance = this.sumOverlayDistance(edges, metric);
            this.cell.getLayer().validRoutes++;
        } else {
            distance = Distance.newInfinityInstance();
            this.cell.getLayer().invalidRoutes++;
        }

        //assign calculated shortcut
        cellEdge.setLength(metric, distance);
    }

    /**
     * sums route distance, first and last (border) edges are ignored - will be included
     * by algorithm
     *
     * @param edges
     * @param metric
     * @return route distance
     */
    public Distance sumOverlayDistance(List<OverlayEdge> edges, Metric metric) {
        Distance distance = Distance.newInstance(0);
        for (int idx = 1; idx < edges.size() - 1; idx++) {
            OverlayEdge edge = edges.get(idx);
            Distance value = edge.getLength(metric);
            distance = distance.add(value);
        }

        return distance;
    }

    /**
     * edges inside cell are created
     */
    public void buildCellEdges() {

        for (OverlayNode entry : this.cell.getEntryNodes()) {
            for (OverlayNode exit : this.cell.getExitNodes()) {
                this.addEdge(entry, exit);
            }
        }
    }

    @Override
    public Graph<OverlayNode, OverlayEdge> newInstance(Set<Metric> metrics) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

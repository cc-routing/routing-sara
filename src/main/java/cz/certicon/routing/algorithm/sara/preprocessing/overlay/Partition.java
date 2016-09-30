/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.DijkstraAlgorithm;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.java8.Optional;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz> Represents one level in overlay
 * data. L0 is created only to preserve level-index consistency, L0 partition is
 * not used, it is represented by basic SaraGraph.
 */
public class Partition {

    /**
     * root oblect of the overlay data
     */
    @Getter
    OverlayBuilder parent;

    /**
     * partiton level
     */
    @Getter
    int level;

    /**
     * All cells at this level.
     */
    @Getter
    TLongObjectMap<Cell> cells;

    /**
     * overlay graph at this level
     */
    @Getter
    OverlayGraph overlayGraph;

    public Partition(OverlayBuilder parent, int level) {

        this.parent = parent;
        this.level = level;

        if (level == 0) {
            return;
        }

        this.cells = new TLongObjectHashMap<>();
        this.overlayGraph = new OverlayGraph(parent);
    }

    /**
     * checks whether specified cell is registered in this partiton
     *
     * @param cell
     * @return cell RouteTable
     */
    public CellRouteTable checkCell(Cell cell) {

        long id = cell.getId();

        if (!this.cells.containsKey(id)) {
            CellRouteTable table = new CellRouteTable(this, cell);
            cell.setRouteTable(table);
            this.cells.put(id, cell);
        }

        return cell.getRouteTable();
    }

    /**
     * Builds Sara Sub graphs for L1 cells.
     */
    public void buildCellSubGraphs() {
        for (Cell cell : this.cells.valueCollection()) {
            cell.getRouteTable().graphBuilder.buildSubGraph();
        }
    }

    /**
     * builds overlay graph in this partition.
     */
    public void buildOverlayGraph() {

        if (this.level == 0) {
            return;
        }

        DijkstraAlgorithm router = this.parent.router;
        Distance distance;

        int validRoutes = 0;
        int invalidRoutes = 0;
        int forbiddenUTurns = 0;

        for (Cell cell : this.cells.valueCollection()) {

            CellRouteTable table = cell.getRouteTable();

            // creates edges for each etry-exit matrix in each cell
            for (OverlayNode entryNode : table.entryPoints.values()) {

                for (OverlayNode exitNode : table.exitPoints.values()) {

                    OverlayEdge edge = this.overlayGraph.addEdge(entryNode, exitNode);

                    // apply for all metrics defined in L0 SaraGraph
                    for (Metric metric : this.parent.metrics) {

                        if (this.level == 1) {
                            // L1 distances are calculated from SaraSubGraphs in cells
                            SaraNode saraEntry = entryNode.column.other.node;
                            SaraNode saraExit = exitNode.column.other.node;

                            if (saraEntry.getId() == saraExit.getId()) {
                                // two-way L0 SaraEdge is split in two L1 OverlayEdges
                                // U-turn in this case is forbidden
                                forbiddenUTurns++;
                                distance = Distance.newInfinityInstance();
                            } else {

                                Optional<Route<SaraNode, SaraEdge>> route = table.graphBuilder.route(saraEntry.getId(), saraExit.getId(), metric);

                                if (route.isPresent()) {
                                    distance = table.graphBuilder.sumDistance(route.get().getEdgeList(), metric);
                                    validRoutes++;
                                } else {
                                    distance = Distance.newInfinityInstance();
                                    invalidRoutes++;
                                }
                            }
                        } else {
                            //L2+ distances are calculated from this.L-1 overlay graph
                            OverlayNode overEntry = entryNode.getLowerNode();
                            OverlayNode overExit = exitNode.getLowerNode();
                            Partition lower = this.parent.partitions.get(this.level - 1);
                            Optional<Route<OverlayNode, OverlayEdge>> route = router.route(lower.overlayGraph, metric, overEntry, overExit);
                            if (route.isPresent()) {
                                distance = this.sumDistance(route.get().getEdges(), metric);
                                validRoutes++;
                            } else {
                                distance = Distance.newInfinityInstance();
                                invalidRoutes++;
                            }
                        }

                        edge.setLength(metric, distance);
                    }
                }

            }
        }

        double ratio = (100 * invalidRoutes) / (validRoutes + invalidRoutes);

        String info = String.format("Level=%d,ForbiddenUTurns=%d,  ValidRoutes=%d, InvalidRoutes=%d=%f",
                this.level, forbiddenUTurns, validRoutes, invalidRoutes, ratio);

        System.out.println(info + "%");
    }

    /**
     * sum distance for Overlay route
     *
     * @param route
     * @param map
     * @return route distance
     */
    private Distance sumDistance(Iterable<OverlayEdge> edges, Metric metric) {

        Distance distance = new Distance(0);

        for (OverlayEdge edge : edges) {
            Distance value = edge.getLength(metric);
            distance = distance.add(value);
        }

        return distance;
    }
}

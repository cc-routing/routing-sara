/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.OneToAllRoutingAlgorithm.Direction;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.java8.Optional;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

        int validRoutes = 0;
        int invalidRoutes = 0;
        int forbiddenRoutes = 0;

        for (Cell cell : this.cells.valueCollection()) {

            CellRouteTable table = cell.getRouteTable();

            // creates edges for each etry-exit matrix in each cell
            for (OverlayNode entryNode : table.entryPoints.values()) {
                for (OverlayNode exitNode : table.exitPoints.values()) {
                    this.overlayGraph.addEdge(entryNode, exitNode);
                }
            }

            if (this.level == 1) {
                // L1 distances are calculated from SaraSubGraphs in cells
                SaraGraph subSara = table.graphBuilder.subGraph;

                for (Metric metric : this.parent.metrics) {

                    for (OverlayNode entryNode : table.entryPoints.values()) {

                        Map<SaraEdge, Direction> targets = new HashMap<>();
                        Map<SaraEdge, OverlayEdge> mapper = new HashMap<>();

                        OverlayColumn begCol = entryNode.column.other;
                        SaraEdge begEdge = begCol.edge;
                        long begId = begEdge.getId();
                        begEdge = subSara.getEdgeById(begId);
                        Direction begDir = begCol.getDirection();

                        for (OverlayEdge edge : entryNode.getOutgoingEdges()) {

                            OverlayNode exitNode = edge.getTarget();
                            OverlayColumn endCol = exitNode.column.other;

                            if (begCol.node.getId() == endCol.node.getId()) {
                                // two-way L0 SaraEdge is split in two L1 OverlayEdges
                                // U-turn in this case is forbidden
                                forbiddenRoutes++;
                                Distance distance = Distance.newInfinityInstance();
                                edge.setLength(metric, distance);
                            } else {
                                SaraEdge endEdge = endCol.edge;

                                long endId = endEdge.getId();
                                Direction endDir = endCol.getDirection();
                                endEdge = subSara.getEdgeById(endId);
                                targets.put(endEdge, endDir);
                                mapper.put(endEdge, edge);
                            }
                        }

                        Map<SaraEdge, Optional<Route<SaraNode, SaraEdge>>> routeMap
                                = this.parent.oneToAll.route(subSara, metric, begEdge, begDir, targets);

                        for (Entry<SaraEdge, Optional<Route<SaraNode, SaraEdge>>> entry : routeMap.entrySet()) {
                            SaraEdge endEdge = entry.getKey();
                            OverlayEdge edge = mapper.get(endEdge);
                            Optional<Route<SaraNode, SaraEdge>> result = entry.getValue();

                            Distance distance;

                            if (result.isPresent()) {
                                Route<SaraNode, SaraEdge> route = result.get();
                                List<SaraEdge> edges = route.getEdgeList();
                                distance = table.graphBuilder.sumDistance(edges, metric);
                                validRoutes++;
                            } else {
                                distance = Distance.newInfinityInstance();
                                invalidRoutes++;
                            }

                            edge.setLength(metric, distance);
                        }
                    } // Metric
                } //L1
            } else {
                //L2+
            }

        } //CEll

        System.out.println("L" + this.level + ": valid=" + validRoutes + "; invalid=" + invalidRoutes + " forbidden=" + forbiddenRoutes);

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

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

    @Getter
    long routingCounter = 0;

    int validRoutes = 0;
    int invalidRoutes = 0;
    int forbiddenRoutes = 0;

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
    public void buildSubGraphs() {

        if (this.level == 0) {
            return;
        }

        if (this.level == 1) {
            for (Cell cell : this.cells.valueCollection()) {
                cell.getRouteTable().subSara.buildSubGraph();
            }
        } else {
            for (Cell cell : this.cells.valueCollection()) {
                cell.getRouteTable().subOverlay.buildSubGraph();
            }
        }
    }

    public void buildOverlayGraph() {

        if (this.level == 0) {
            return;
        }

        for (Cell cell : this.cells.valueCollection()) {

            CellRouteTable table = cell.getRouteTable();

            // creates edges for each etry-exit matrix in each cell
            for (OverlayNode entryNode : table.entryPoints.values()) {
                for (OverlayNode exitNode : table.exitPoints.values()) {
                    this.overlayGraph.addEdge(entryNode, exitNode);
                }
            }

            Cell cellParent = cell.getParent();
            if (cellParent != null) {
                CellRouteTable upTable = cellParent.getRouteTable();
                upTable.subOverlay.subCells.add(cell);
            }
        }
    }

    /**
     * builds overlay graph in this partition.
     */
    public void buildCustomization() {

        if (this.level == 0) {
            return;
        }

        if (this.level == 1) {
            for (Cell cell : this.cells.valueCollection()) {
                this.customizeFirstLevel(cell);
            }
        } else {

            this.copyDistances();

            for (Cell cell : this.cells.valueCollection()) {
                this.customizeUpperLevel(cell);
            }
        }

        String info = "L=" + this.level
                + "; cells=" + this.cells.size()
                + "; valid=" + validRoutes
                + "; invalid=" + invalidRoutes
                + "; forbidden=" + forbiddenRoutes;

        System.out.println(info);
    }

    /**
     * copy calculated edge distances from full graph at L(n-1) to cell
     * subGraphs at L(n)
     */
    private void copyDistances() {
        for (Cell cell : this.cells.valueCollection()) {
            cell.getRouteTable().subOverlay.copyDistances();
        }
    }

    /**
     * calculates shortcuts over cell in L1
     *
     * @param cell
     */
    private void customizeFirstLevel(Cell cell) {

        CellRouteTable table = cell.getRouteTable();
        SubSaraBuilder subBuilder = table.subSara;

        // L1 distances are calculated from Sara SubGraphs in cells
        SaraGraph subGraph = subBuilder.subGraph;

        for (Metric metric : this.parent.metrics) {

            for (OverlayNode entryNode : table.entryPoints.values()) {

                Map<SaraEdge, Direction> targets = new HashMap<>();
                Map<SaraEdge, OverlayEdge> mapper = new HashMap<>();

                OverlayColumn begCol = entryNode.column.other;
                SaraEdge begEdge = begCol.edge;
                long begId = begEdge.getId();
                begEdge = subGraph.getEdgeById(begId);
                Direction begDir = begCol.getDirection();

                for (OverlayEdge cellEdge : entryNode.getOutgoingEdges()) {

                    OverlayNode exitNode = cellEdge.getTarget();
                    OverlayColumn endCol = exitNode.column.other;

                    //if (begCol.edge.getId() == endCol.edge.getId()) {
                    if(false) {
                        // two-way L0 SaraEdge is split in two L1 OverlayEdges
                        // U-turn in this case is forbidden
                        forbiddenRoutes++;
                        Distance distance = Distance.newInfinityInstance();
                        cellEdge.setLength(metric, distance);
                    } else {
                        SaraEdge endEdge = endCol.edge;

                        long endId = endEdge.getId();
                        Direction endDir = endCol.getDirection();
                        endEdge = subGraph.getEdgeById(endId);
                        targets.put(endEdge, endDir);
                        mapper.put(endEdge, cellEdge);
                    }
                }

                Map<SaraEdge, Optional<Route<SaraNode, SaraEdge>>> routeMap
                        = this.parent.oneToAll.route(subGraph, metric, begEdge, begDir, targets);

                for (Entry<SaraEdge, Optional<Route<SaraNode, SaraEdge>>> entry : routeMap.entrySet()) {

                    SaraEdge endEdge = entry.getKey();
                    OverlayEdge cellEdge = mapper.get(endEdge);
                    Optional<Route<SaraNode, SaraEdge>> result = entry.getValue();

                    //OverlayNode s = edge.getSource();
                    //OverlayNode t = edge.getTarget();
                    Distance distance;

                    if (result.isPresent()) {
                        Route<SaraNode, SaraEdge> route = result.get();
                        List<SaraEdge> edges = route.getEdgeList();
                        distance = subBuilder.sumDistance(edges, metric);
                        if (OverlayBuilder.keepShortcuts) {
                            cellEdge.saraWay = edges;
                        }
                        validRoutes++;
                    } else {
                        distance = Distance.newInfinityInstance();
                        invalidRoutes++;
                    }

                    cellEdge.setLength(metric, distance);
                }
            } // Metric
        }
    }

    /**
     * calculates shortcuts over cell in L2+
     *
     * @param cell
     */
    private void customizeUpperLevel(Cell cell) {

        CellRouteTable table = cell.getRouteTable();

        SubOverlayBuilder subBuilder = table.subOverlay;
        OverlayGraph subGraph = subBuilder.subGraph;

        //OverlayGraph full = this.parent.partitions.get(this.level - 1).overlayGraph;
        for (Metric metric : this.parent.metrics) {

            for (OverlayNode entryNode : table.entryPoints.values()) {

                Map<OverlayEdge, OverlayEdge> mapper = new HashMap<>();
                Map<OverlayEdge, Direction> targets = new HashMap<>();
                OverlayEdge begEdge = entryNode.getIncomingEdges().next();
                long begId = begEdge.getId();
                begEdge = subGraph.getEdgeById(begId);

                Direction begDir = Direction.FORWARD;

                for (OverlayEdge cellEdge : entryNode.getOutgoingEdges()) {

                    Direction endDir = Direction.FORWARD;
                    OverlayEdge endEdge = cellEdge.getTarget().getOutgoingEdges().next();
                    long endId = endEdge.getId();
                    endEdge = subGraph.getEdgeById(endId);

                    targets.put(endEdge, endDir);
                    mapper.put(endEdge, cellEdge);
                }

                Map<OverlayEdge, Optional<Route<OverlayNode, OverlayEdge>>> routeMap
                        = this.parent.oneToAll.route(subGraph, metric, begEdge, begDir, targets);

                for (Entry<OverlayEdge, Optional<Route<OverlayNode, OverlayEdge>>> entry : routeMap.entrySet()) {

                    Optional<Route<OverlayNode, OverlayEdge>> result = entry.getValue();

                    OverlayEdge endEdge = entry.getKey();
                    OverlayEdge cellEdge = mapper.get(endEdge);

                    Distance distance;

                    if (result.isPresent()) {
                        Route<OverlayNode, OverlayEdge> route = result.get();
                        List<OverlayEdge> edges = route.getEdgeList();
                        if (OverlayBuilder.keepShortcuts) {
                            cellEdge.overWay = edges;
                        }
                        distance = subBuilder.sumDistance(edges, metric);
                        validRoutes++;
                    } else {
                        distance = Distance.newInfinityInstance();
                        invalidRoutes++;
                    }

                    //assign calculated shortcut
                    cellEdge.setLength(metric, distance);
                }
            } // Metric
        }
    }
}

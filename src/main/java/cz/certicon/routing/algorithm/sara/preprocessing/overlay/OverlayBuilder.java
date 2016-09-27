/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.DijkstraAlgorithm;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;

/**
 * Builds Overlay graphs for all levels. Root object of the overlay data.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayBuilder {

    /**
     * collection of overlay partitions for each level. Partition at L0 is void,
     * L0 is represented by basic SaraGraph.
     */
    @Getter
    List<Partition> partitions;

    /**
     * L0 SaraGraph
     */
    @Getter
    SaraGraph graph;

    /**
     * Metrics used in L0 SaraGraph.
     */
    @Getter
    Set<Metric> metrics;

    /**
     * shared instance of the DijkstraAlgorithm.
     */
    @Getter
    DijkstraAlgorithm router;

    /**
     *
     * @param graph L0 SaraGraph
     */
    public OverlayBuilder(SaraGraph graph) {
        this.graph = graph;

        //this.metrics = graph.getMetrics();
        this.metrics = new HashSet<>();
        this.metrics.add(Metric.LENGTH);

        this.partitions = new ArrayList<>();
        this.addPartition();

        this.router = new DijkstraAlgorithm();
    }

    /**
     * adds next level partition.
     *
     * @return
     */
    private Partition addPartition() {
        Partition partition = new Partition(this, this.partitions.size());
        this.partitions.add(partition);
        return partition;
    }

    /**
     * TODO, tmp assumed cells in all nodes have the same count of levels first
     * cell is used to create partitions of all levels
     */
    private void createPartitions() {

        SaraNode first = graph.getNodes().next();
        Cell cell = first.getParent();

        while (cell != null) {
            this.addPartition();
            cell = cell.getParent();
        }
    }

    /**
     * Builds overlay graphs in partitions for all levels.
     */
    public void buildOverlays() {

        this.createPartitions();

        for (SaraEdge edge : graph.getEdges()) {
            this.checkForBorder(edge);
        }

        Partition part1 = this.partitions.get(1);
        part1.buildCellSubGraphs();

        for (Partition partition : this.partitions) {
            partition.buildOverlayGraph();
        }
    }

    /**
     * Checks whether specified edge is a border edge between two cells for all
     * levels.
     *
     * @param graph Basic Sara graph.
     * @param edge Checked edge
     */
    private void checkForBorder(SaraEdge edge) {

        SaraNode source = edge.getSource();
        SaraNode target = edge.getTarget();

        Cell sourceCell = source.getParent();
        Cell targetCell = target.getParent();

        boolean twoWay = !edge.isOneWay();

        OverlayColumn exitColumn1 = null;
        OverlayColumn entryColumn1 = null;

        OverlayColumn exitColumn2 = null;
        OverlayColumn entryColumn2 = null;

        int level = 1;

        while (true) {

            Partition partition = this.partitions.get(level);
            OverlayGraph overGraph = partition.overlayGraph;

            long sourceId = sourceCell.getId();
            long targetId = targetCell.getId();

            if (sourceId == targetId) {

                if (level == 1) {
                    partition.checkCell(sourceCell).graphBuilder.addEdge(edge);
                }

                return;
            }

            if (level == 1) {

                exitColumn1 = OverlayColumn.Create(edge, source, target);
                entryColumn1 = exitColumn1.getOther();

                if (twoWay) {
                    exitColumn2 = OverlayColumn.Create(edge, target, source);
                    entryColumn2 = exitColumn2.getOther();
                }
            }

            CellRouteTable sourceTable = partition.checkCell(sourceCell);
            CellRouteTable targetTable = partition.checkCell(targetCell);
            sourceTable.graphBuilder.addEdge(edge);
            targetTable.graphBuilder.addEdge(edge);

            OverlayNode exitNode1 = overGraph.addNode(exitColumn1, sourceTable.exitPoints, edge);
            OverlayNode entryNode1 = overGraph.addNode(entryColumn1, targetTable.entryPoints, edge);

            overGraph.addEdge(exitNode1, entryNode1);

            if (twoWay) {
                OverlayNode exitNode2 = overGraph.addNode(exitColumn2, targetTable.exitPoints, edge);
                OverlayNode entryNode2 = overGraph.addNode(entryColumn2, sourceTable.entryPoints, edge);
                overGraph.addEdge(exitNode2, entryNode2);
            }

            sourceCell = sourceCell.getParent();
            if (sourceCell == null) {
                return;
            }

            targetCell = targetCell.getParent();
            if (targetCell == null) {
                return;
            }

            level++;
        }
    }

    /**
     * Finds highest level OverlayNode which is not in the same cell with source
     * and target
     *
     * @param node transferNode
     * @param edge transferEdge
     * @param source sourceNode
     * @param target targetNode
     * @return
     */
    public OverlayNode getMaxOverlayNode(SaraNode node, SaraEdge edge, SaraNode source, SaraNode target) {

        BorderNodeMap entryMap = node.getParent().getRouteTable().entryPoints;

        if (!entryMap.containsKey(edge)) {
            //not a border edge
            return null;
        }

        OverlayNode max = null;
        OverlayNode entry = entryMap.get(edge);
        OverlayColumn column = entry.column;
        Cell sourceCell = source.getParent();
        Cell targetCell = target.getParent();

        while (true) {

            if (entry.isMyCell(sourceCell)) {
                return max;
            }

            if (entry.isMyCell(targetCell)) {
                return max;
            }

            if (sourceCell.getId() == targetCell.getId()) {
                return max;
            }

            max = entry;

            sourceCell = sourceCell.getParent();
            if (sourceCell == null) {
                return max;
            }
            targetCell = targetCell.getParent();
            if (targetCell == null) {
                return max;
            }

            if (entry.level() == column.size() - 1) {
                return max;
            }

            entry = entry.getUpperNode();
        }
    }
}

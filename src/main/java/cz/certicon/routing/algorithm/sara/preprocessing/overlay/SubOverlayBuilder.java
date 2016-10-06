/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Distance;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class SubOverlayBuilder extends BaseSubBuilder {

    /**
     * cells at L-1 that form subGraph in this cell at L
     */
    @Getter
    Set<Cell> subCells;

    /**
     * overlay subGraph
     */
    @Getter
    OverlayGraph subGraph;

    /**
     * nodes in full L-graph to become subGraph in this cell
     */
    private Set<OverlayNode> nodes;

    /**
     * edges in full L-graph to become subGraph in this cell
     */
    private Set<OverlayEdge> edges = new HashSet<>();

    public SubOverlayBuilder(CellRouteTable table) {
        super(table);
        this.subCells = new HashSet<>();
    }

    /**
     * builds sub graph from sub cells
     */
    public void buildSubGraph() {

        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
        this.subGraph = new OverlayGraph(this.builder);

        for (Cell cell : this.subCells) {

            CellRouteTable table = cell.getRouteTable();

            for (OverlayNode entryNode : table.entryPoints.values()) {

                for (OverlayEdge edge : entryNode.getIncomingEdges()) {
                    this.addEdge(edge);
                }
            }

            for (OverlayNode exitNode : table.exitPoints.values()) {
                for (OverlayEdge edge : exitNode.getIncomingEdges()) {
                    this.addEdge(edge);
                }

                for (OverlayEdge edge : exitNode.getOutgoingEdges()) {
                    this.addEdge(edge);
                }
            }
        }

        for (OverlayNode node : this.nodes) {
            this.subGraph.addNode(node.getId());
        }

        for (OverlayEdge edge : this.edges) {
            this.subGraph.addCopy(edge);
        }

        this.nodes = null;
        this.edges = null;
    }

    /**
     * collects full graph edges and nodes
     *
     * @param edge in full graph
     */
    private void addEdge(OverlayEdge edge) {

        if (this.edges.contains(edge)) {
            return;
        }
        this.edges.add(edge);
        this.nodes.add(edge.getSource());
        this.nodes.add(edge.getTarget());
    }

    /**
     * sums route distance, first and last edges are ignored - will be included
     * by algorithm
     *
     * @param edges
     * @param metric
     * @return route distance
     */
    public Distance sumDistance(List<OverlayEdge> edges, Metric metric) {
        Distance distance = new Distance(0);
        for (int idx = 1; idx < edges.size() - 1; idx++) {
            OverlayEdge edge = edges.get(idx);
            Distance value = edge.getLength(metric);
            distance = distance.add(value);
        }

        return distance;
    }

    /**
     * copy edge distances from full graph to sub graph
     */
    public void copyDistances() {

        OverlayGraph overGraph = this.builder.partitions.get(this.table.partition.level - 1).overlayGraph;

        for (OverlayEdge destEdge : this.subGraph.getEdges()) {
            if (destEdge.saraEdge == null) {
                // = 'cell' edge (across cell from entry to exit point)
                long id = destEdge.getId();
                OverlayEdge srcEdge = overGraph.getEdgeById(id);
                for (Metric metric : overGraph.getMetrics()) {
                    Distance distance = srcEdge.getLength(metric);
                    destEdge.setLength(metric, distance);
                }
            } else {
                // border edge from cell to cell, distance already copied from SaraEdge
            }
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.Getter;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 * Builder of the Sara SubGraph for the specific Cell. Applicable only for the
 * Cells at L1.
 */
public class SubGraphBuilder {

    /**
     * Related cell.
     */
    final private Cell cell;

    /**
     * Auxiliary collection of sub nodes.
     */
    final private TLongObjectMap<SaraNode> subNodes;

    /**
     * Mapper to edges (1:1 to subEdges) in full graph.
     */
    final private TLongObjectMap<SaraNode> nodeMap;

    /**
     * Sara SubGraph, the result of the build.
     */
    @Getter
    SaraGraph subGraph;

    /**
     * root overlay builder
     */
    OverlayBuilder builder;

    /**
     *
     * @param table
     */
    public SubGraphBuilder(CellRouteTable table) {
        this.builder = table.partition.parent;
        this.cell = table.cell;
        this.subGraph = new SaraGraph(builder.metrics);
        this.subNodes = new TLongObjectHashMap<>();
        this.nodeMap = new TLongObjectHashMap<>();
    }

    /**
     * gets or adds subNode
     *
     * @param node L0 node
     * @return
     */
    private SaraNode checkNode(SaraNode node) {

        long id = node.getId();

        if (this.subNodes.containsKey(id)) {

            return this.subNodes.get(id);

        } else {

            SaraNode subNode = this.subGraph.createNode(id, cell);

            TurnTable turns = node.getTurnTable();
            subNode.setTurnTable(turns);

            this.subNodes.put(id, subNode);
            this.nodeMap.put(id, node);

            return subNode;
        }
    }

    /**
     * adds subEdge derived from edge in L0 sara graph.
     *
     * @param edge edge in the full graph
     */
    public void addEdge(SaraEdge edge) {

        SaraNode source = edge.getSource();
        SaraNode target = edge.getTarget();

        SaraNode subSource = this.checkNode(source);
        SaraNode subTarget = this.checkNode(target);

        int sourceIdx = edge.getSourcePosition();
        int targetIdx = edge.getTargetPosition();
        boolean oneway = edge.isOneWay();

        long id = edge.getId();
        SaraEdge subEdge = this.subGraph.createEdge(id, oneway, subSource, subTarget, sourceIdx, targetIdx);

        for (Metric key : this.builder.metrics) {
            Distance distance = edge.getLength(key);
            subEdge.setLength(key, distance);
        }
    }

    /**
     * builds Sara subGraph (only for cells at L1)
     */
    public void buildSubGraph() {
        //this.subGraph.lock();
    }

    /**
     * finds route in L0 cell sub sara graph = shortcut in L1.
     * @param sourceNodeId
     * @param targetNodeId
     * @param metric
     * @return route
     */
    public Route route(long sourceNodeId, long targetNodeId, Metric metric) {
        SaraNode source = this.subNodes.get(sourceNodeId);
        SaraNode target = this.subNodes.get(targetNodeId);

        Route route = this.builder.router.route(this.subGraph, metric, source, target);
        return route;
    }
}

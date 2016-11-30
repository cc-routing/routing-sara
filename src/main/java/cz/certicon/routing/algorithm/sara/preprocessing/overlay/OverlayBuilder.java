/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.DijkstraAlgorithm;
import cz.certicon.routing.algorithm.DijkstraOneToAllAlgorithm;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import cz.certicon.routing.utils.collections.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Getter;

/**
 * Root object for Overlay and Customization. Container of OverLayers for levels
 * 1-N. Represents entire OverlayGraph.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayBuilder {

    /**
     * devel setup whether to keep calculated shortcuts in memory
     */
    public static boolean keepShortcuts = true;

    /**
     * Collection of Layers
     */
    private List<OverLayer> layers;

    /**
     * layer for level=1
     */
    @Getter
    private OverLayer firstLayer;

    /**
     * highest layer used for routing
     */
    @Getter
    private OverLayer topLayer;

    /**
     * SaraGraph, input data
     */
    @Getter
    private SaraGraph saraGraph;

    /**
     * Used Metrics.
     */
    @Getter
    private Set<Metric> metrics;

    /**
     * one-one DijkstraAlgorithm used to calculate shortcuts
     */
    @Getter
    private DijkstraAlgorithm oneToOne;

    /**
     * one-many DijkstraAlgorithm used to calculate shortcuts
     */
    @Getter
    private DijkstraOneToAllAlgorithm oneToAll;

    /**
     * collection of all borders detected at level 0
     */
    private List<ZeroBorder> zeroBorders = new ArrayList<>();

    /**
     * @param graph SaraGraph is the only input for Overlay + Customization
     */
    public OverlayBuilder(SaraGraph graph) {
        this.saraGraph = graph;

        this.metrics = graph.getMetrics();
//        this.metrics = new HashSet<>();
//        this.metrics.add(Metric.LENGTH);

        this.layers = new ArrayList<>();

        this.oneToOne = new DijkstraAlgorithm();
        this.oneToAll = new DijkstraOneToAllAlgorithm();
    }

    public OverLayer getLayer(int level) {
        return this.layers.get(level - 1);
    }

    public Iterator<OverLayer> getLayers() {
        return new ImmutableIterator<>(this.layers.iterator());
    }

    public int layerCount() {
        return this.layers.size();
    }

    /**
     * Builds layers 1-N and ZeroGraph for level 0 (referenced by cells at level
     * 1.
     */
    public void buildOverlays() {

        if (this.layerCount() > 0) {
            throw new IllegalStateException("overlay is already built");
        }

        this.buildLayers();

        this.runCustomization();

        this.connectLayerGraphs();
    }

    /**
     * Dic/connectcs cells (=regions) in top layer. Only connected (in future
     * downloaded) regions are available for routing.
     *
     * @param connection
     * @param ids
     */
    public void turnTopCells(boolean connection, long... ids) {
        if (ids == null || ids.length == 0) {
            for (OverlayCell cell : this.topLayer.getCells()) {
                cell.setLiftConnection(connection);
            }
        } else {
            for (long id : ids) {
                OverlayCell cell = this.topLayer.getCell(id);
                cell.setLiftConnection(connection);
            }
        }
    }

    /**
     * Maps ZeroEdges route to Sara edge route
     *
     * @param zeroRoute
     * @return sara route
     */
    public List<SaraEdge> mapRoute(List<SaraEdge> zeroRoute) {
        List<SaraEdge> saraRoute = new ArrayList<>();
        for (SaraEdge zeroEdge : zeroRoute) {
            SaraEdge saraEdge = this.getSaraEdge(zeroEdge);
            saraRoute.add(saraEdge);
        }
        return saraRoute;
    }

    /**
     * maps zero edge to sara edge
     *
     * @param zeroEdge
     * @return SaraEdge
     */
    public SaraEdge getSaraEdge(SaraEdge zeroEdge) {
        long id = Math.abs(zeroEdge.getId());
        SaraEdge saraEdge = this.saraGraph.getEdgeById(id);
        return saraEdge;
    }

    /**
     * maps saraNode to zero node
     *
     * @param saraNode
     * @return zero node
     */
    public ZeroNode getZeroNode(SaraNode saraNode) {
        ZeroGraph zeroGraph = this.getZeroGraph(saraNode);
        return (ZeroNode) zeroGraph.getNodeById(saraNode.getId());
    }

    /**
     * maps sara edge to zerp edge
     *
     * @param saraEdge
     * @return zero edge
     */
    public ZeroEdge getZeroEdge(SaraEdge saraEdge) {
        ZeroGraph sourceGraph = this.getZeroGraph(saraEdge.getSource());
        ZeroGraph targetGraph = this.getZeroGraph(saraEdge.getTarget());

        ZeroEdge sourceEdge = sourceGraph.getZeroEdge(saraEdge);

        if (sourceGraph == targetGraph || sourceEdge.getId() > 0) {
            return sourceEdge;
        } else {
            ZeroEdge targetEdge = targetGraph.getZeroEdge(saraEdge);
            return targetEdge;
        }
    }

    /**
     * gets the partial ZeroGraph from L1 cell related to the saraNdoe
     * @param saraNode
     * @return related ZeroGraph
     */
    private ZeroGraph getZeroGraph(SaraNode saraNode) {
        long cellId = saraNode.getParent().getId();
        ZeroGraph zeroGraph = this.firstLayer.getCell(cellId).getZeroGraph();
        return zeroGraph;
    }

    /**
     * adds new layer
     *
     * @return new added layer
     */
    private OverLayer addLayer() {
        OverLayer layer = new OverLayer(this, this.layers.size() + 1);
        this.layers.add(layer);
        return layer;
    }

    /**
     * it is assumed all SaraNodes have same number of parent cells; first
     * cell is used to create required layers
     */
    private void createLayers() {

        SaraNode first = saraGraph.getNodes().next();
        Cell cell = first.getParent();

        while (cell != null) {
            this.addLayer();
            cell = cell.getParent();
        }

        this.firstLayer = this.getLayer(1);

        for (SaraNode node : saraGraph.getNodes()) {
            cell = node.getParent();
            int level = 1;

            while (cell != null) {
                OverLayer layer = this.getLayer(level);
                layer.checkCell(cell);
                cell = cell.getParent();
                level++;
            }
        }
    }

    /**
     * top layers with cellCount==1 are supressed, they are not useful fro routing
     */
    private void removeUnusedLayers() {

        while (true) {
            OverLayer layer = this.getLayer(this.layerCount());
            if (layer.getCells().size() > 1) {
                this.topLayer = layer;
                return;
            } else {
                this.layers.remove(layer.getLevel() - 1);
            }
        }
    }

    /**
     * cells, graphs and other data structures are build for all layers
     */
    private void buildLayers() {
        this.createLayers();

        for (OverLayer layer : this.layers) {
            layer.collectSubCells();
        }

        for (SaraEdge edge : saraGraph.getEdges()) {
            this.checkForBorder(edge);
        }

        for (OverLayer layer : this.layers) {
            layer.separateIsolatedCells();
        }

        this.removeUnusedLayers();

        for (OverLayer layer : this.layers) {
            layer.buildCellEdges();
        }
    }

    /**
     * customization is build, shortcuts are seerched
     */
    private void runCustomization() {

        // sub graphs for customization are formed
        for (OverLayer layer : this.layers) {
            layer.connectSubCells();
        }

        // shortcuts lengths are calculated fro all metrics
        for (OverLayer layer : this.layers) {
            layer.buildCustomization();
        }
    }

    private void connectLayerGraphs() {

        // graph for entire level 0 is connected
        for (ZeroBorder zBorder : this.zeroBorders) {
            zBorder.connect();
        }

        // graphs for each layer 1-N are connected
        for (OverLayer layer : this.layers) {
            layer.setLayerConnection(true);
        }
    }

    /**
     * Checks whether specified edge is a border edge between two cells for all
     * levels. Core method to find all borders.
     *
     * @param edge sara edge to check
     */
    private void checkForBorder(SaraEdge edge) {

        SaraNode sourceNode = edge.getSource();
        SaraNode targetNode = edge.getTarget();

        // cells at level 1 decide whether edge is border or inside a single cell
        long sourceCellId = sourceNode.getParent().getId();
        long targetCellId = targetNode.getParent().getId();

        OverlayCell sourceCell = this.firstLayer.getCell(sourceCellId);
        ZeroGraph sourceGraph = sourceCell.getZeroGraph();

        if (sourceCellId == targetCellId) {
            // no border, edge is in inside cell
            sourceGraph.addEdgeCopy(edge);
            return;
        }

        OverlayCell targetCell = this.firstLayer.getCell(targetCellId);
        ZeroGraph targetGraph = targetCell.getZeroGraph();

        ZeroEdge zExit = sourceGraph.addExitCopy(edge);
        ZeroEdge zEntry = targetGraph.addEntryCopy(edge);

        boolean doubleWay = !edge.isOneWay();

        ZeroBorder zBorder = new ZeroBorder(zExit, zEntry, doubleWay);
        this.zeroBorders.add(zBorder);

        for (OverLayer layer : this.layers) {

            OverlayEdge oExit1 = sourceCell.getOverlayGraph().addBorder(zBorder.getExit1(), targetCell);
            OverlayEdge oEntry1 = targetCell.getOverlayGraph().addBorder(zBorder.getEntry1(), sourceCell);
            OverlayBorder oBorder1 = new OverlayBorder(oExit1, oEntry1);

            if (doubleWay) {
                // double way SaraEdge generates another pair of exit-entry overlay edges
                OverlayEdge oExit2 = targetCell.getOverlayGraph().addBorder(zBorder.getExit2(), sourceCell);
                OverlayEdge oEntry2 = sourceCell.getOverlayGraph().addBorder(zBorder.getEntry2(), targetCell);
                OverlayBorder oBorder2 = new OverlayBorder(oExit2, oEntry2);
            }

            sourceCell = sourceCell.getParent();
            targetCell = targetCell.getParent();

            if (sourceCell == targetCell) {
                return;
            }
        }
    }

    /**
     * debug, tracking only
     */
    public void resetRoutingCounters() {
        for (OverLayer layer : this.layers) {
            layer.routingCounter = 0;
        }
    }

    /**
     * core method for multilevel routing in backward direction
     * @param node current route node
     * @param edge current route edge
     * @param source route source
     * @param target route target
     * @return the highest overlay node available for routing and shortcuts
     */
    public OverlayNode getMaxExitNode(SaraNode node, ZeroEdge edge, SaraNode source, SaraNode target) {

        ZeroBorder border = edge.border();

        if (border == null) {
            return null;
        }

        if (!border.isConnected()) {
            return null;
        }

        OverlayLift lift = null;

        if (edge.getTarget() == node) {
            lift = edge.border().getExit1();
        } else {
            lift = edge.border().getExit2();
        }

        OverlayNode re = lift.getNode(1);

        re = this.checkMaxNode(re, source, target);

        return re;
    }

    /**
     * core method for multilevel routing in forward direction
     * @param node current route node
     * @param edge current route edge
     * @param source route source
     * @param target route target
     * @return the highest overlay node available for routing and shortcuts
     */
    public OverlayNode getMaxEntryNode(SaraNode node, ZeroEdge edge, SaraNode source, SaraNode target) {

        ZeroBorder border = edge.border();

        if (border == null) {
            return null;
        }

        if (!border.isConnected()) {
            return null;
        }

        OverlayLift lift = null;

        if (edge.getTarget() == node) {
            lift = border.getEntry1();
        } else {
            lift = border.getEntry2();
        }

        OverlayNode re = lift.getNode(1);

        re = this.checkMaxNode(re, source, target);

        return re;
    }

    /**
     * auxiliary for tracking
     * @param re
     * @param source
     * @param target
     * @return
     */
    private OverlayNode checkMaxNode(OverlayNode re, SaraNode source, SaraNode target) {

        re = this.getMaxNode(re, source, target);

        if (re != null) {
            int lev = re.getLevel();
            OverLayer layer = this.getLayer(lev);
            layer.routingCounter++;
        }

        return re;
    }

    /**
     * finds highest overlay node available for routing
     * @param node current route node
     * @param source
     * @param target
     * @return overlay node
     */
    private OverlayNode getMaxNode(OverlayNode node, SaraNode source, SaraNode target) {

        OverlayNode max = null;
        Cell sourceCell = source.getParent();
        Cell targetCell = target.getParent();

        int level = 0;

        while (true) {

            level++;

            if (node.isMyCell(sourceCell)) {
                return max;
            }

            if (node.isMyCell(targetCell)) {
                return max;
            }

            if (sourceCell.getId() == targetCell.getId()) {
                return max;
            }

            max = node;

            if (level == this.layers.size()) {
                return max;
            }

            node = node.getUpperNode();

            if (node == null) {
                return max;
            }

            sourceCell = sourceCell.getParent();
            targetCell = targetCell.getParent();
        }
    }
}

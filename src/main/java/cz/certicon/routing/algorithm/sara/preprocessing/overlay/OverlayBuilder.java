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
     * devel setup whether to keep calculated shortcuts in memory
     */
    public static boolean keepShortcuts = false;

    /**
     * collection of overlay partitions for each level. Partition at L0 is void,
     * L0 is represented by basic SaraGraph.
     */
    private List<OverLayer> layers;

    @Getter
    private OverLayer firstLayer;

    @Getter
    private OverLayer topLayer;

    /**
     * L0 SaraGraph
     */
    @Getter
    private SaraGraph saraGraph;

    /**
     * Metrics used in L0 SaraGraph.
     */
    @Getter
    private Set<Metric> metrics;

    /**
     * shared instance of the DijkstraAlgorithm.
     */
    @Getter
    private DijkstraAlgorithm oneToOne;

    @Getter
    private DijkstraOneToAllAlgorithm oneToAll;

    private List<ZeroBorder> zeroBorders = new ArrayList<>();

    /**
     *
     * @param graph L0 SaraGraph
     */
    public OverlayBuilder(SaraGraph graph) {
        this.saraGraph = graph;

        //this.metrics = graph.getMetrics();
        this.metrics = new HashSet<>();
        this.metrics.add(Metric.LENGTH);

        this.layers = new ArrayList<>();

        this.oneToOne = new DijkstraAlgorithm();
        this.oneToAll = new DijkstraOneToAllAlgorithm();
    }

    public ZeroNode getZeroNode(SaraNode node) {
        long cellId = node.getParent().getId();
        ZeroGraph zeroGraph = this.firstLayer.getCell(cellId).getZeroGraph();
        return (ZeroNode) zeroGraph.getNodeById(node.getId());
    }


    public OverLayer getLayer(int level) {
        return this.layers.get(level - 1);
    }

    /**
     * Builds overlay graphs in partitions for all levels.
     */
    public void buildOverlays() {

        if (this.layerCount() > 0) {
            throw new IllegalStateException("overlay is already built");
        }

        this.buildLayers();

        this.runCustomization();

        this.connectLayerGraphs();
    }

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

    public List<SaraEdge> mapRoute(List<SaraEdge> zeroRoute) {
        List<SaraEdge> saraRoute = new ArrayList<>();
        for (SaraEdge zeroEdge : zeroRoute) {
            SaraEdge saraEdge = this.mapEdge(zeroEdge);
            saraRoute.add(saraEdge);
        }
        return saraRoute;
    }

    public SaraEdge mapEdge(SaraEdge zeroEdge) {
        long id = Math.abs(zeroEdge.getId());
        SaraEdge saraEdge = this.saraGraph.getEdgeById(id);
        return saraEdge;
    }

    /**
     * adds next level partition.
     *
     * @return
     */
    private OverLayer addLayer() {
        OverLayer layer = new OverLayer(this, this.layers.size() + 1);
        this.layers.add(layer);
        return layer;
    }

    /**
     * TODO, tmp assumed cells in all nodes have the same count of levels first
     * cell is used to create partitions of all levels
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

    private void runCustomization() {

        for (OverLayer layer : this.layers) {
            layer.connectSubCells();
        }

        for (OverLayer layer : this.layers) {
            layer.buildCustomization();
        }
    }

    private void connectLayerGraphs() {

        for (ZeroBorder zBorder : this.zeroBorders) {
            zBorder.connect();
        }

        for (OverLayer layer : this.layers) {
            layer.setLayerConnection(true);
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

        SaraNode sNode = edge.getSource();
        SaraNode tNode = edge.getTarget();

        long sId = sNode.getParent().getId();
        long tId = tNode.getParent().getId();

        OverlayCell sourceCell = this.firstLayer.getCell(sId);
        ZeroGraph sourceGraph = sourceCell.getZeroGraph();

        if (sId == tId) {
            sourceGraph.addEdgeCopy(edge);
            return;
        }

        OverlayCell targetCell = this.firstLayer.getCell(tId);
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

    public void resetRoutingCounters() {
        for (OverLayer layer : this.layers) {
            layer.routingCounter = 0;
        }
    }

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

    public OverlayNode getMaxOverlayNode(SaraNode node, SaraEdge edge, SaraNode source, SaraNode target) {
        return this.getMaxEntryNode(node, (ZeroEdge) edge, source, target);
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

    private OverlayNode checkMaxNode(OverlayNode re, SaraNode source, SaraNode target) {

        re = this.getMaxNode(re, source, target);

        if (re != null) {
            int lev = re.getLevel();
            OverLayer layer = this.getLayer(lev);
            layer.routingCounter++;
        }

        return re;
    }

    public Iterator<OverLayer> getLayers() {
        return new ImmutableIterator<>(this.layers.iterator());
    }

    public int layerCount() {
        return this.layers.size();
    }

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

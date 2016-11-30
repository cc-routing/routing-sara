/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.values.Coordinate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Container with a part of the overlay graph in one layer. Cells at level 1
 * reference also ZeroGraph from level 0.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayCell {

    private static int maxUid = 0;

    @Getter
    private final long uid;

    /**
     * part of graph for level 0, defined only in cell at level 1
     */
    @Getter
    private ZeroGraph zeroGraph;

    /**
     * part of the overlay graph
     */
    @Getter
    private final OverlayGraph overlayGraph;

    /**
     * related layer, cell owner
     */
    @Getter
    private final OverLayer layer;

    /**
     * related cell genrated by preprocessing partitioning
     */
    @Getter
    private final Cell saraCell;

    /**
     * sub cells in layer N-1
     */
    @Getter
    private final List<OverlayCell> subCells = new ArrayList<>();

    /**
     * collection of entry nodes
     */
    private final DataList<OverlayNode> entryNodes = new DataList<>();

    /**
     * collection of exit nodes
     */
    private final DataList<OverlayNode> exitNodes = new DataList<>();

    /**
     * parent cell in layer N+1
     */
    @Getter
    private OverlayCell parent;

    /**
     * has not etry and exit borders
     */
    @Getter
    private boolean isIsolated = true;

    /**
     * all lifts in all borders of this cell are connected
     */
    @Getter
    private boolean isConnected = true;

    /**
     * @param layer owner layer
     * @param cell related partition cell
     */
    public OverlayCell(OverLayer layer, Cell cell) {
        this.uid = ++OverlayCell.maxUid;
        this.layer = layer;
        this.saraCell = cell;

        this.overlayGraph = new OverlayGraph(this);
        if (layer.getLevel() == 1) {
            this.zeroGraph = new ZeroGraph(this);
        }
    }

    public int getLevel() {
        return this.layer.getLevel();
    }

    public ReadOnlyList<OverlayNode> getExitNodes() {

        return this.exitNodes;
    }

    public ReadOnlyList<OverlayNode> getEntryNodes() {
        return this.entryNodes;
    }

    public void addExitNode(OverlayNode node) {
        this.isIsolated = false;
        this.exitNodes.add(node);
    }

    public void addEntryNode(OverlayNode node) {
        this.isIsolated = false;
        this.entryNodes.add(node);
    }

    public long getId() {
        return this.saraCell.getId();
    }

    public void setParent() {
        long id = saraCell.getParent().getId();
        this.parent = this.layer.getUpperLayer().getCell(id);
        this.parent.subCells.add(this);
    }

    /**
     * Dis/connects lifts for all borders in this cell
     * @param connection
     */
    public void setLiftConnection(boolean connection) {

        this.isConnected = connection;

        for (OverlayNode exit : this.exitNodes) {
            exit.getLift().setConnection(connection);
        }
    }

    /**
     * Dis/connects only borders of this cell
     * @param connection
     */
    public void setLayerConnection(boolean connection) {
        for (OverlayNode exit : this.exitNodes) {
            exit.getOutgoingEdges().next().border().setConnection(connection);
        }
    }

    /**
     * Connects sub cells of this cell
     */
    public void connectSubCells() {

        for (OverlayNode exit : this.exitNodes) {
            OverlayEdge edge = exit.getOutgoingEdges().next();
            OverlayNode other = edge.getTarget();
            if (exit.getCell().parent == other.getCell().parent) {
                OverlayBorder border = edge.border();
                border.connect();
            }
        }
    }

    /**
     * gets center coordinates of all borders, debug only
     * @return
     */
    public List<Coordinate> getBorderPoints() {

        List<Coordinate> re = new ArrayList<>();

        if (this.exitNodes.isEmpty()) {
            return re;
        }

        for (OverlayNode node : this.exitNodes) {
            Coordinate c = node.getLift().getEdge().getCenter();
            re.add(c);
        }

        return re;
    }
}

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
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayCell {

    private static int maxUid = 0;

    @Getter
    private final long uid;

    @Getter
    private ZeroGraph zeroGraph;

    @Getter
    private final OverlayGraph overlayGraph;

    @Getter
    private final OverLayer layer;

    @Getter
    private final Cell saraCell;

    @Getter
    private final List<OverlayCell> subCells = new ArrayList<>();

    private final DataList<OverlayNode> entryNodes = new DataList<>();

    private final DataList<OverlayNode> exitNodes = new DataList<>();

    @Getter
    private OverlayCell parent;

    @Getter
    private boolean isIsolated = true;

    @Getter
    private boolean isConnected = true;

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

    public void setLiftConnection(boolean connection) {

        this.isConnected = connection;

        for (OverlayNode exit : this.exitNodes) {
            exit.getLift().setConnection(connection);
        }
    }

    public void setLayerConnection(boolean connection) {
        for (OverlayNode exit : this.exitNodes) {
            exit.getOutgoingEdges().next().border().setConnection(connection);
        }
    }

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

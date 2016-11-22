/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Cell;
import lombok.Getter;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz> Represents one level in overlay
 * data. L0 is created only to preserve level-index consistency, L0 partition is
 * not used, it is represented by basic SaraGraph.
 */
public class OverLayer {

    /**
     * root oblect of the overlay data
     */
    @Getter
    private final OverlayBuilder builder;

    /**
     * layer level
     */
    @Getter
    private final int level;

    /**
     * All cells at this level.
     */
    private final LongHashMap<OverlayCell> cells = new LongHashMap<>();

    private final LongHashMap<OverlayCell> isolatedCells = new LongHashMap<>();

    private long maxNodeId = 0;
    private long maxEdgeId = 0;
    private boolean lockEdgesById = false;

    /**
     * overlay graph at this level
     */
    long routingCounter = 0;
    int validRoutes = 0;
    int invalidRoutes = 0;
    int forbiddenRoutes = 0;

    public OverLayer(OverlayBuilder builder, int level) {
        this.builder = builder;
        this.level = level;
    }

    public void collectSubCells() {

        if (this.isTopLayer()) {
            return;
        }

        for (OverlayCell cell : this.cells) {
            cell.setParent();
        }
    }

    public OverlayCell getCell(long id) {
        return this.cells.get(id);
    }

    public ReadOnlyLongMap<OverlayCell> getCells() {
        return this.cells;
    }

    /**
     * checks whether specified cell is registered in this partiton
     *
     * @param cell
     * @return cell RouteTable
     */
    public void checkCell(Cell cell) {

        long id = cell.getId();

        if (this.cells.containsKey(id)) {
            return;
        }

        OverlayCell data = new OverlayCell(this, cell);
        this.cells.getMap().put(id, data);

    }

    public void separateIsolatedCells() {

        for (OverlayCell cell : this.cells) {
            if (cell.isIsolated()) {
                this.isolatedCells.add(cell.getId(), cell);
            }
        }

        for (OverlayCell cell : this.isolatedCells) {
            this.cells.getMap().remove(cell.getId());
        }
    }

    public void buildCellEdges() {

        for (OverlayCell cell : this.getCells()) {
            cell.getOverlayGraph().addCellEdges();
        }
    }

    public OverLayer getUpperLayer() {
        return this.builder.getLayer(this.level + 1);
    }

    public void connectSubCells() {

        for (OverlayCell cell : this.cells) {
            cell.connectSubCells();
        }
    }

    public void setLayerConnection(boolean connection) {
        for (OverlayCell cell : this.cells) {
            cell.setLayerConnection(connection);
        }
    }

    public boolean isTopLayer() {
        return this.level == this.builder.layerCount();
    }

    /**
     * builds overlay graph in this partition.
     */
    public void buildCustomization() {

        if (this.level == 1) {
            for (OverlayCell cell : this.getCells()) {
                cell.getZeroGraph().customizeFirstLevel();
            }
        } else {

            for (OverlayCell cell : this.getCells()) {
                cell.getOverlayGraph().customizeUpperLevel();
            }
        }

        String info = "L=" + this.level
                + "; cells=" + this.cells.size() + " + " + this.isolatedCells.size()
                + "; valid=" + validRoutes
                + "; invalid=" + invalidRoutes
                + "; forbidden=" + forbiddenRoutes;

        System.out.println(info);
    }

    public long getNextEdgeId() {
        this.lockEdgesById = true;
        return ++this.maxEdgeId;
    }

    public long getNextNodeId() {
        return ++this.maxNodeId;
    }

    public void checkEdgeId(long id) {

        if (this.lockEdgesById) {
            throw new IllegalStateException("adding OverlayEdge by id is locked");
        }

        long absId = Math.abs(id);

        if (absId > this.maxEdgeId) {
            this.maxEdgeId = absId;
        }
    }

}

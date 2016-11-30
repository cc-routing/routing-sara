/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Cell;
import lombok.Getter;

/**
 * One layer in OverlayGraph. Layers are defined for levels 1-N. OverlayGraph of the
 * layer is "split" into its cells. Cells of the layer reference the relevant
 * portion of the OverlayGraph at the particular level. Level 0 has no object.
 * Cells at layer 1 reference also the related parts of Zero(Sub)Graph from
 * level 0. Level 0 borders are referenced in OverlayBuilder.zeroBorders.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 *
 */
public class OverLayer {

    /**
     * root object in the Overlay
     */
    @Getter
    private final OverlayBuilder builder;

    /**
     * layer level
     */
    @Getter
    private final int level;

    /**
     * Layer cells with borders - contain parts of the graph.
     */
    private final LongHashMap<OverlayCell> cells = new LongHashMap<>();

    /**
     * Cells without borders.
     */
    private final LongHashMap<OverlayCell> isolatedCells = new LongHashMap<>();

    private long maxNodeId = 0;

    private long maxEdgeId = 0;

    private boolean lockEdgesById = false;

    // debug info
    long routingCounter = 0;
    int validRoutes = 0;
    int invalidRoutes = 0;
    int forbiddenRoutes = 0;

    /**
     *
     * @param builder parent object, Overlay root
     * @param level
     */
    public OverLayer(OverlayBuilder builder, int level) {
        this.builder = builder;
        this.level = level;
    }

    /**
     * Builds the tree cell structure among layers, where cells in this layer
     * represent sub cells of cells in the upper layer.
     */
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
     * checks whether partition cell is already registered in this laeyr
     *
     * @param partitionCell cell from preprocessing referenced by {
     * @SaraNode}
     */
    public void checkCell(Cell partitionCell) {

        long id = partitionCell.getId();

        if (this.cells.containsKey(id)) {
            return;
        }

        OverlayCell data = new OverlayCell(this, partitionCell);
        this.cells.getMap().put(id, data);

    }

    /**
     * Cells without border are moved to separate collection.
     */
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

    /**
     * adds OverlayEdges inside cell, matrix entry nodex x exit nodes
     */
    public void buildCellEdges() {

        for (OverlayCell cell : this.getCells()) {
            cell.getOverlayGraph().buildCellEdges();
        }
    }

    public OverLayer getUpperLayer() {
        return this.builder.getLayer(this.level + 1);
    }

    /**
     * Connects subCells (in layer N-1). Used for customization: Shortcuts
     * (CellEdges) in a cell at level N are calculated in graph formed by
     * cell.subCells at level N-1
     */
    public void connectSubCells() {

        for (OverlayCell cell : this.cells) {
            cell.connectSubCells();
        }
    }

    /**
     * sets the connection for all cells in this layer
     *
     * @param connection true: all cells are connected and form graph over
     * entire level false: all cells become disconnected, isolated for routing
     */
    public void setLayerConnection(boolean connection) {
        for (OverlayCell cell : this.cells) {
            cell.setLayerConnection(connection);
        }
    }

    public boolean isTopLayer() {
        return this.level == this.builder.layerCount();
    }

    /**
     * runs cutomization for this layer, using dijkstra searches shortcuts in
     * layer N-1, shortcut length is assign to cellEdges at level N for all
     * metrics
     */
    public void buildCustomization() {

        if (this.level == 1) {
            for (OverlayCell cell : this.getCells()) {
                // cells only at level 1 reference sub Zero Graphs
                cell.getZeroGraph().customizeFirstLevel();
            }
        } else {
            // layers 2-N calculate shortcuts from N-1
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

    long getNextEdgeId() {
        this.lockEdgesById = true;
        return ++this.maxEdgeId;
    }

    long getNextNodeId() {
        return ++this.maxNodeId;
    }

    /**
     * For BorderEdges ids of underlaying Sara/Zero Edges are used CellEdges
     * have higher ids
     *
     * @param id
     */
    void checkEdgeId(long id) {

        if (this.lockEdgesById) {
            throw new IllegalStateException("adding OverlayEdge by id is locked");
        }

        long absId = Math.abs(id);

        if (absId > this.maxEdgeId) {
            this.maxEdgeId = absId;
        }
    }
}

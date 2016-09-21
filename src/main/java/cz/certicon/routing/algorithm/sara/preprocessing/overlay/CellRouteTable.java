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
 * @author Blahoslav Potoček {@literal <potocek@merica.cz>} Table with Entry and
 * Exit points for the specific cell.
 */
public class CellRouteTable {

    /**
     * Map of Entry Points.
     */
    @Getter
    BorderNodeMap entryPoints;

    /**
     * Map of Exit Points.
     */
    @Getter
    BorderNodeMap exitPoints;

    /**
     * Related partition.
     */
    @Getter
    Partition partition;

    /**
     * Related cell.
     */
    @Getter
    Cell cell;

    /**
     * L1 SaraSubGraph builder.
     */
    @Getter
    SubGraphBuilder graphBuilder;

    public CellRouteTable(Partition partition, Cell cell) {
        this.partition = partition;
        this.cell = cell;
        this.entryPoints = new BorderNodeMap(this, true);
        this.exitPoints = new BorderNodeMap(this, false);

        if (partition.level == 1) {
            this.graphBuilder = new SubGraphBuilder(this);
        }
    }
}

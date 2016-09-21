/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.SaraEdge;
import java.util.HashMap;
import lombok.Getter;

/**
 *
 * @author Blahoslav Potoček {@literal <potocek@merica.cz>} HashMap of
 * BorderPoints in OverlayGraph
 */
public class BorderNodeMap extends HashMap<SaraEdge, OverlayNode> {

    /**
     * parent cell routeTable
     */
    @Getter
    CellRouteTable cellTable;

    /**
     * true = this.maps EntryPoints, false= this maps ExitPoints
     */
    @Getter
    boolean isEntry;

    public BorderNodeMap(CellRouteTable cellTable, boolean isEntry) {
        this.cellTable = cellTable;
        this.isEntry = isEntry;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Cell;

/**
 * Base class to build Sara or Overlay SubGraphs.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public abstract class BaseSubBuilder {

    /**
     * root overlay builder
     */
    final protected OverlayBuilder builder;

    /**
     * related route table
     */
    final protected CellRouteTable table;

    /**
     * Related cell.
     */
    final protected Cell cell;

    /**
     *
     * @param table
     */
    public BaseSubBuilder(CellRouteTable table) {
        this.table = table;
        this.cell = table.cell;

        this.builder = table.partition.parent;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.AbstractNode;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.values.Distance;
import lombok.Getter;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz> Node in the OverlayGraph. One
 * instance of the OverlayNode always represens one exitPoint and one
 * entryPouint.
 *
 */
public class OverlayNode extends AbstractNode<OverlayNode, OverlayEdge> {

    /**
     * related borderMap
     */
    @Getter
    BorderNodeMap borderMap;

    /**
     * index of this instance in exit this borderMap
     */
    @Getter
    int borderIndex;

    /**
     * related "vertical" column
     */
    @Getter
    OverlayColumn column;

    /**
     *
     * @param column related overlay column
     * @param map relatedCell entry or exit nodes map
     * @param edge related border edge
     */
    OverlayNode(OverlayGraph graph, OverlayColumn column, BorderNodeMap map, SaraEdge edge) {
        super(graph, graph.getNodesCount() + 1);

        this.column = column;
        this.borderMap = map;
        this.borderIndex = map.size();
        this.borderMap.put(edge, this);
        column.nodes.add(this);
    }

    OverlayNode(OverlayGraph graph, long id) {
        super(graph, id);
    }

    @Override
    public Distance getTurnDistance(OverlayEdge source, OverlayEdge target) {
        return Distance.newInstance(0);
    }

    /**
     *
     * @return partition level
     */
    public int level() {
        return this.borderMap.cellTable.partition.level;
    }

    /**
     * @return related Cell
     */
    public Cell getCell() {
        return this.borderMap.cellTable.cell;
    }

    /**
     * Checks whether specified cell is equal to this node cell
     *
     * @param cell
     * @return true/false for the same cells partitions, null for different
     * cells partitions
     */
    public Boolean isMyCell(Cell cell) {
        Cell myCell = this.getCell();
        if (myCell.getRouteTable().partition == cell.getRouteTable().partition) {
            return cell.getId() == myCell.getId();
        } else {
            return null;
        }
    }

    /**
     *
     * @return "vertical" OverlayNode for upper partition (this.L+1)
     */
    public OverlayNode getUpperNode() {

        int level = this.level();

        if (level < this.column.nodes.size() - 1) {
            return this.column.nodes.get(level + 1);
        } else {
            return null;
        }
    }

    /**
     *
     * @return "vertical" OverlayNode for lower partition (this.L-1) applicable
     * only for L2+, as L0 is represented by SaraGraph
     */
    public OverlayNode getLowerNode() {

        int level = this.level();

        if (level > 1) {
            return this.column.nodes.get(level - 1);
        } else {
            return null;
        }
    }

    @Override
    protected OverlayNode newInstance(Graph<OverlayNode, OverlayEdge> newGraph, long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

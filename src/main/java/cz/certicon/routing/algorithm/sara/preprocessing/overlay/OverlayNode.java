/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.AbstractNode;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Graph;
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
     * related "vertical" column
     */
    @Getter
    private final OverlayLift lift;

    @Getter
    private final OverlayCell cell;

    /**
     *
     * @param lift related overlay column
     * @param map relatedCell entry or exit nodes map
     * @param edge related border edge
     */
    OverlayNode(OverlayGraph graph, OverlayLift lift, OverlayCell cell) {
        super(graph, graph.getLayer().getNextNodeId());

        this.lift = lift;

        if (cell == null) {
            this.cell = graph.getCell();
            lift.addNode(this);
        } else {
            this.cell = cell;
        }
    }

    @Override
    public Distance getTurnDistance(OverlayEdge source, OverlayEdge target) {
        return Distance.newInstance(0);
    }

    /**
     *
     * @return partition level
     */
    public int getLevel() {
        return this.cell.getLayer().getLevel();
    }

    /**
     * Checks whether specified cell is equal to this node cell
     *
     * @param cell
     * @return true/false for the same cells partitions, null for different
     * cells partitions
     */
    public boolean isMyCell(Cell cell) {
        return cell.getId() == this.cell.getId();
        //return cell.uid == this.data.getUid();
    }

    /**
     *
     * @return "vertical" OverlayNode for upper partition (this.L+1)
     */
    public OverlayNode getUpperNode() {

        int level = this.getLevel();

        if (level < this.lift.nodeCount()) {
            return this.lift.getNode(level + 1);
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

        int level = this.getLevel();

        if (level > 1) {
            return this.lift.getNode(level - 1);
        } else {
            return null;
        }
    }

    @Override
    protected OverlayNode newInstance(Graph<OverlayNode, OverlayEdge> newGraph, long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OverlayLift getColumn() {
        return this.lift;
    }
}

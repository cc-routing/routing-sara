/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.OneToAllRoutingAlgorithm.Direction;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * "Vertical link" object in overlay data model. It is shared by OverlayNodes
 * for different levels created over one border SaraEdge in L0.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayLift {

    /**
     * id passed to OverlayNodes, TODO
     */
    @Getter
    private final long groupId;

    @Getter
    private final String key;

    /**
     * related L0 SaraEdge
     */
    @Getter
    private final ZeroEdge edge;

    /**
     * related L0 SaraNode
     */
    @Getter
    private final ZeroNode node;

    @Getter
    private final ZeroNode otherNode;

    private final boolean forward;

    /**
     * false: entryPoint, exitPoint direction corresponds to this.edge
     * source=>target true: entryPoint, exitPoint direction corresponds to
     * this.edge target=>source
     */
    private final boolean exit;

    @Getter
    private final Direction direction;

    /**
     * "vertical" collection of OverlayNodes in a column over this Sara node
     */
    private final List<OverlayNode> nodes = new ArrayList<>();

    /**
     *
     * @param node SaraNode L0
     * @param isEntry column role: true=entry, false=exit;
     */
    public OverlayLift(ZeroEdge edge, boolean isForward, boolean isExit) {

        this.edge = edge;
        this.forward = isForward;
        this.exit = isExit;

        this.groupId = Math.abs(edge.getId());

        String key = this.groupId + "";
        if (this.exit) {
            key += "^Exit^";
        } else {
            key += "^Entry^";
        }

        ZeroNode s = (ZeroNode) edge.getSource();
        ZeroNode t = (ZeroNode) edge.getTarget();

        if (this.forward) {

            key += "1";

            if (this.exit) {
                this.node = s;
                this.otherNode = t;
            } else {
                this.node = t;
                this.otherNode = s;
            }

            this.direction = Direction.FORWARD;

        } else {

            key += "2";

            if (this.exit) {
                this.node = t;
                this.otherNode = s;
            } else {
                this.node = s;
                this.otherNode = t;
            }

            this.direction = Direction.BACKWARD;
        }

        this.key = key;
    }

    public long getEdgeId() {
        if (this.isExit()) {
            return -this.groupId;
        } else {
            return this.groupId;
        }
    }

    public boolean isExit() {
        return this.exit;
    }

    public boolean isForward() {
        return this.forward;
    }

    public OverlayNode getNode(int level) {
        return this.nodes.get(level - 1);
    }

    public void addNode(OverlayNode node) {
        this.nodes.add(node);
        if (node.getLevel() != this.nodeCount()) {
            throw new IllegalStateException("level mismatch in the OverlayLift");
        }
    }

    public int nodeCount() {
        return this.nodes.size();
    }

    public void setConnection(boolean connection) {

        this.edge.border().setConnection(connection);

        for (OverlayNode node : this.nodes) {
            OverlayEdge edge = null;
            if (this.exit) {
                edge = node.getOutgoingEdges().next();
            } else {
                edge = node.getIncomingEdges().next();
            }
            edge.border().setConnection(connection);
        }
    }
}

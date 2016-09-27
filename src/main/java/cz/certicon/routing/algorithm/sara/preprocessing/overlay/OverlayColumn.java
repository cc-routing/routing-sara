/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraNode;
import java.util.ArrayList;
import lombok.Getter;

/**
 * "Vertical link" object in overlay data model. It is shared by OverlayNodes
 * for different levels created over one border SaraEdge in L0.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayColumn extends ArrayList<OverlayNode> {

    /**
     * Related L0 SaraEdge.
     */
    @Getter
    SaraNode node;

    @Getter
    SaraEdge edge;

    /**
     * false: entryPoint, exitPoint direction corresponds to this.edge
     * source=>target true: entryPoint, exitPoint direction corresponds to
     * this.edge target=>source
     */
    @Getter
    boolean isEntry;

    /**
     * other column on the border SaraEdge
     */
    @Getter
    OverlayColumn other;

    /**
     * id passed to OverlayNodes, TODO
     */
    @Getter
    long id;

    /**
     *
     * @param node SaraNode L0
     * @param isEntry column role: true=entry, false=exit;
     */
    private OverlayColumn(SaraEdge edge, SaraNode node, boolean isEntry) {

        this.edge = edge;
        this.node = node;
        this.isEntry = isEntry;
        this.add(null);

        if (this.isEntry) {
            this.id = node.getId();
        } else {
            this.id = -node.getId();
        }
    }

    /**
     * Creates column
     *
     * @param edge border edge
     * @param entry entry node
     * @param exit exit node
     * @return new Exit Column paired with Entry Column
     */
    public static OverlayColumn Create(SaraEdge edge, SaraNode exit, SaraNode entry) {
        OverlayColumn exitColumn = new OverlayColumn(edge, exit, false);
        OverlayColumn entryColumn = new OverlayColumn(edge, entry, true);
        entryColumn.other = exitColumn;
        exitColumn.other = entryColumn;
        return exitColumn;
    }
}

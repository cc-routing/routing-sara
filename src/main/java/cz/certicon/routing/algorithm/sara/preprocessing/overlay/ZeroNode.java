/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;

/**
 * Node from graph at level 0
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class ZeroNode extends SaraNode {

    private static final TurnTable noTurn = new TurnTable(new Distance[][]{{Distance.newInfinityInstance()}});

    public ZeroNode(ZeroGraph graph, long id, Cell cell) {
        super(graph, id, cell);
    }

    public ZeroNode(ZeroGraph graph, SaraNode node) {
        this(graph, node, node.getId());
    }

    public ZeroNode(ZeroGraph graph, SaraNode node, long id) {
        this(graph, id, node.getParent());
        if (id < 0) {
            this.setTurnTable(noTurn);
        } else {
            this.setTurnTable(node.getTurnTable());
        }
        this.setCoordinate(node.getCoordinate());
    }
}

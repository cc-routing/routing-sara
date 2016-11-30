/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import lombok.Getter;

/**
 * Data shared by exit and entry border (parallel) edges.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public abstract class BorderData<N extends Node, E extends Edge & BorderEdge<N, E>> {

    @Getter
    private final long id;

    @Getter
    private final E exitEdge;

    @Getter
    private final E entryEdge;

    /**
     * blind target node at exit edge
     */
    @Getter
    private final N exitTarget;

    /**
     * blind entry source at entry edge
     */
    @Getter
    private final N entrySource;

    private boolean connected = false;

    /**
     * @param exit  exit parallel edge
     * @param entry entry parallel edge
     */
    public BorderData(E exit, E entry) {

        if ((exit.getId() < 0) && (Math.abs(exit.getId()) == entry.getId())) {
            this.id = entry.getId();
        } else {
            throw new IllegalStateException("invalid border id");
        }

        this.exitEdge = exit;
        this.entryEdge = entry;
        this.exitTarget = (N) exit.getTarget();
        this.entrySource = (N) entry.getSource();
        exit.setBorder(this);
        entry.setBorder(this);
    }

    /**
     * true: exit and entry edges are parallel, border is available for routing
     * false: exit and entry edges are ednded by blind nodes, border is not available for routing
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * connects exit and entry parallel edges
     */
    public void connect() {

        if (this.connected) {
            return;
        }

        exitEdge.setTarget(entryEdge.getTarget(), entryEdge.getTargetPosition());
        entryEdge.setSource(exitEdge.getSource(), exitEdge.getSourcePosition());

        this.connected = true;
    }

    /**
     * disconectes exit and entry parallel edges
     */
    public void disconnect() {
        if (!this.connected) {
            return;
        }

        this.exitEdge.setTarget(this.exitTarget, 0);
        this.entryEdge.setSource(this.entrySource, 0);
        this.connected = false;
    }

    public void setConnection(boolean connection) {
        if (connection) {
            this.connect();
        } else {
            this.disconnect();
        }
    }
}

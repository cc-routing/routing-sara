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
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class BorderData<N extends Node, E extends Edge & BorderEdge<N, E>> {

    @Getter
    private final long id;

    @Getter
    private final E exitEdge;

    @Getter
    private final E entryEdge;

    @Getter
    private final N exitTarget;

    @Getter
    private final N entrySource;

    private boolean connected = false;

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

    public boolean isConnected() {
        return this.connected;
    }

    public void connect() {

        if (this.connected) {
            return;
        }

        E e1 = this.exitEdge;
        E e2 = this.entryEdge;

        N ss = (N) e1.getSource();
        N tt = (N) e2.getTarget();

        e1.setTarget(tt, e2.getTargetPosition());
        e2.setSource(ss, e1.getSourcePosition());

        this.connected = true;
    }

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

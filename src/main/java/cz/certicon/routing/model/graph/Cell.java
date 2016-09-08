/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class Cell implements Identifiable {

    private final long id;
    private Cell parent = null;
    private boolean locked = false;

    public Cell( long id ) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public Cell getParent() {
        return parent;
    }

    public void setParent( Cell parent ) {
        checkLock();
        this.parent = parent;
    }

    public void lock() {
        locked = true;
    }

    private void checkLock() {
        if ( locked ) {
            throw new IllegalStateException( "This object is locked against modification." );
        }
    }
}

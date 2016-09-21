/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.algorithm.sara.preprocessing.overlay.CellRouteTable;
import cz.certicon.routing.model.Identifiable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class Cell implements Identifiable {

    private final long id;
    private Cell parent = null;
    private boolean locked = false;

    @Getter
    @Setter
    CellRouteTable routeTable;

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

    public boolean hasParent() {
        return parent != null;
    }

    public void lock() {
        locked = true;
    }

    private void checkLock() {
        if ( locked ) {
            throw new IllegalStateException( "This object is locked against modification." );
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) ( this.id ^ ( this.id >>> 32 ) );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Cell other = (Cell) obj;
        if ( this.id != other.id ) {
            return false;
        }
        return true;
    }

}

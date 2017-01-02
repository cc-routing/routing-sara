/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayCell;
import cz.certicon.routing.model.Identifiable;
import lombok.Getter;
import lombok.Setter;

/**
 * Cell used for hierarchical sara graph.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Cell implements Identifiable, Parentable {

    private final long id;
    private Cell parent = null;
    private boolean locked = false;

    /**
     * Constructor
     *
     * @param id cell id
     */
    public Cell( long id ) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    /**
     * Returns parent of this cell
     *
     * @return parent of this cell
     */
    @Override
    public Cell getParent() {
        return parent;
    }

    /**
     * Sets parent to this cell
     *
     * @param parent parent
     */
    public void setParent( Cell parent ) {
        checkLock();
        this.parent = parent;
    }

    /**
     * Returns whether this cell has a parent
     *
     * @return true if this cell has a parent, false otherwise
     */
    @Override
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Locks this cell against modifications.
     */
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
        return this.id == other.id;
    }

}

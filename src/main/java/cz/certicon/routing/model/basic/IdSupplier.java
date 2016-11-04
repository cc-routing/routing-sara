/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.basic;

/**
 * Id supplier class. Stores current maximal id. Generates new id. Must be initialized properly in order to work (provide max used id).
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class IdSupplier {

    private long maxId;

    /**
     * Create new instance. Provide current maximum, or else it might generate duplicate ids
     *
     * @param maxId current maximum
     */
    public IdSupplier( long maxId ) {
        this.maxId = maxId;
    }

    /**
     * Retrieves current id (last)
     *
     * @return current id
     */
    public long getCurrent() {
        return maxId;
    }

    /**
     * Retrieves new id
     *
     * @return new id
     */
    public long next() {
        return ++maxId;
    }

}

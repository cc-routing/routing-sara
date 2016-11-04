/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.basic;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class IdSupplier {

    private long maxId;

    public IdSupplier( long maxId ) {
        this.maxId = maxId;
    }

    public long getCurrent() {
        return maxId;
    }

    public long next() {
        return ++maxId;
    }

}

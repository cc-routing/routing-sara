/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.basic;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class MaxIdContainer {
    
    private long maxId;

    public MaxIdContainer( long maxId ) {
        this.maxId = maxId;
    }

    public long getCurrent() {
        return maxId;
    }

    public long next() {
        return ++maxId;
    }
    
}

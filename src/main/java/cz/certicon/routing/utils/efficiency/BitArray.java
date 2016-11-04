/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.efficiency;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface BitArray {

    void init( int size );

    void set( int index, boolean value );

    boolean get( int index );
    
    int size();

    void clear();
}

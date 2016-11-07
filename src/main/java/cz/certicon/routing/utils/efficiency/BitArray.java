/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.efficiency;

/**
 * Interface for bit array support
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface BitArray {

    /**
     * Initializes the bit array for the given size
     *
     * @param size number of elements
     */
    void init( int size );

    /**
     * Sets value on the given index
     *
     * @param index given index
     * @param value given value
     */
    void set( int index, boolean value );

    /**
     * Returns value on the given index
     *
     * @param index given index
     * @return value on the given index
     */
    boolean get( int index );

    /**
     * Returns size of this array
     *
     * @return size of this array
     */
    int size();

    /**
     * Clears this array (resets everything to false). You don't have to call init after clear.
     */
    void clear();
}

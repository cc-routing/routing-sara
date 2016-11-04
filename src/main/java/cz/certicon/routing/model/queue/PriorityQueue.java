/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.queue;

/**
 *
 * The root interface for priority queues.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <T> element type
 */
public interface PriorityQueue<T> {

    /**
     * Extract element with the minimal distance. Should be as fast as possible.
     *
     * @return element with the minimal distance
     */
    T extractMin();

    /**
     * Adds element to the structure.
     *
     * @param element element to be added
     * @param value value to be associated with the node
     */
    void add( T element, double value );

    /**
     * Removes element from the structure.
     *
     * @param element element to be removed
     */
    void remove( T element );

    /**
     * Notifies the structure about distance change (invoking so called
     * decrease-key operation). Adds element if it does not already exist in the queue.
     *
     * @param node node to change
     * @param value value to be associated with the node
     */
    void decreaseKey( T node, double value );

    /**
     * Wipes out the data from this structure.
     */
    void clear();

    /**
     * Returns true or false whether this structure contains elements or not.
     *
     * @return boolean value
     */
    boolean isEmpty();

    /**
     * Returns amount of elements left in the structure.
     *
     * @return integer value
     */
    int size();

    /**
     * Returns true if the structure contains the given element.
     *
     * @param element element whose presence is to be tested
     * @return true if this structure contains the specified element
     */
    boolean contains( T element );

    /**
     * Returns element with the minimal value. Does not remove it.
     *
     * @return element with the minimal value
     */
    T findMin();

    /**
     * Returns the minimal value in this structure
     *
     * @return the minimal value
     */
    double minValue();
}

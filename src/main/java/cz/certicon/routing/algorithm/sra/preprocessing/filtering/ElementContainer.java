/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sra.preprocessing.filtering;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
interface ElementContainer<E> extends Iterable<E> {

    void add( E element );

    void addAll( Collection<E> elements );

    void addAll( Iterator<E> elements );

    boolean isContained( E element );

    void remove( E element );

    E any();
    
    boolean isEmpty();

    void clear();
}

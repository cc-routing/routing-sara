/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
class SetElementContainer<E> implements ElementContainer<E> {

    private final Set<E> elements = new HashSet<>();

    @Override
    public void add( E element ) {
        elements.add( element );
    }

    @Override
    public void addAll( Collection<E> elements ) {
        this.elements.addAll( elements );
    }

    @Override
    public boolean contains( E element ) {
        return elements.contains( element );
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public void addAll( Iterator<E> elementsIterator ) {
        while ( elementsIterator.hasNext() ) {
            add( elementsIterator.next() );
        }
    }

    @Override
    public void remove( E element ) {
        elements.remove( element );
    }

    @Override
    public E any() {
        Iterator<E> iterator = elements.iterator();
        if ( iterator.hasNext() ) {
            return iterator.next();
        } else {
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }
}

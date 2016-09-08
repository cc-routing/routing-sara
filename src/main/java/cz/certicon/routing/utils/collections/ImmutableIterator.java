/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.collections;

import cz.certicon.routing.model.graph.Node;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <T>
 */
public class ImmutableIterator<T> implements Iterator<T> {

    private final java.util.Iterator<T> iterator;

    public ImmutableIterator( ArrayIterator<T> iterator ) {
        this.iterator = iterator;
    }

    public ImmutableIterator( java.util.Iterator<T> iterator ) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return this;
    }

}

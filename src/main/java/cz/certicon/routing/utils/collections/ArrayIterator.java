/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.collections;

/**
 * @param <T>
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class ArrayIterator<T> implements Iterator<T> {

    private final T[] array;
    private int position = -1;

    public ArrayIterator( T[] array ) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return position + 1 < array.length;
    }

    @Override
    public T next() {
        return array[++position];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException( "Remove not supported" );
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return this;
    }
}

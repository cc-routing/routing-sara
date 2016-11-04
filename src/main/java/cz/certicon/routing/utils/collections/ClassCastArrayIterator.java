/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.collections;

/**
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class ClassCastArrayIterator<T> implements Iterator<T> {

    private final ClassCaster<T> classCaster;
    private final ArrayIterator<Object> iterator;

    public ClassCastArrayIterator( Object[] array, ClassCaster<T> classCaster ) {
        this.iterator = new ArrayIterator<>( array );
        this.classCaster = classCaster;
    }

    @Override
    public T next() {
        return classCaster.cast( iterator.next() );
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return this;
    }

    public interface ClassCaster<T> {

        T cast( Object o );
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException( "Remove not supported" );
    }
}

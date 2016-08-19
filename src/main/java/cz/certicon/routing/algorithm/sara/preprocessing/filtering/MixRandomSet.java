/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <E> element type
 */
class MixRandomSet<E> extends AbstractSet<E> implements RandomSet<E> {

    private final List<E> dta;
    private final Map<E, Integer> idx;

    public MixRandomSet() {
        dta = new ArrayList<>();
        idx = new HashMap<>();
    }

    public MixRandomSet( int initialCapacity ) {
        dta = new ArrayList<>( initialCapacity );
        idx = new HashMap<>( initialCapacity );
    }

    public MixRandomSet( Collection<E> items ) {
        dta = new ArrayList<>( items.size() );
        idx = new HashMap<>( items.size() );
        for ( E item : items ) {
            idx.put( item, dta.size() );
            dta.add( item );
        }
    }

    @Override
    public boolean add( E item ) {
        if ( idx.containsKey( item ) ) {
            return false;
        }
        idx.put( item, dta.size() );
        dta.add( item );
        return true;
    }

    /**
     * Override element at position <code>id</code> with last element.
     *
     * @param id
     */
    public E removeAt( int id ) {
        if ( id >= dta.size() ) {
            return null;
        }
        E res = dta.get( id );
        idx.remove( res );
        E last = dta.remove( dta.size() - 1 );
        // skip filling the hole if last is removed
        if ( id < dta.size() ) {
            idx.put( last, id );
            dta.set( id, last );
        }
        return res;
    }

    @Override
    public boolean remove( Object item ) {
        @SuppressWarnings( value = "element-type-mismatch" )
        Integer id = idx.get( item );
        if ( id == null ) {
            return false;
        }
        removeAt( id );
        return true;
    }

    public E get( int i ) {
        return dta.get( i );
    }

    public E pollRandom( Random rnd ) {
        if ( dta.isEmpty() ) {
            return null;
        }
        int id = rnd.nextInt( dta.size() );
        return removeAt( id );
    }

    @Override
    public int size() {
        return dta.size();
    }

    @Override
    public Iterator<E> iterator() {
        return dta.iterator();
    }

}

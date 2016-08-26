/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for frequent operations with collections.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class CollectionUtils {

    /**
     * Returns list from a map referenced by the given key. If the list is not
     * present under the given key, a new list is created, inserted into the map
     * and returned. In other words, it ensures retrieval of a list for the
     * given key.
     *
     * @param <Key> key type of the map
     * @param <Value> value type of the list (which acts as a value in the map)
     * @param map key-value map, where value is of type {@link List}
     * @param node key
     * @return non-null {@link List} for the given key
     */
    public static <Key, Value> List<Value> getList( Map<Key, List<Value>> map, Key node ) {
        List<Value> list = map.get( node );
        if ( list == null ) {
            list = new ArrayList<>();
            map.put( node, list );
        }
        return list;
    }

    /**
     * Returns set from a map referenced by the given key. If the set is not
     * present under the given key, a new list is created, inserted into the map
     * and returned. In other words, it ensures retrieval of a set for the given
     * key.
     *
     * @param <Key> key type of the map
     * @param <Value> value type of the set (which acts as a value in the map)
     * @param map key-value map, where value is of type {@link Set}
     * @param node key
     * @return non-null {@link Set} for the given key
     */
    public static <Key, Value> Set<Value> getSet( Map<Key, Set<Value>> map, Key node ) {
        Set<Value> set = map.get( node );
        if ( set == null ) {
            set = new HashSet<>();
            map.put( node, set );
        }
        return set;
    }

    /**
     * Converts {@link List} of Integers into an int array
     *
     * @param list list to be converted
     * @return int[] representation
     */
    public static int[] toIntArray( List<Integer> list ) {
        int[] array = new int[list.size()];
        for ( int i = 0; i < list.size(); i++ ) {
            array[i] = list.get( i );
        }
        return array;
    }

    /**
     * Converts parameters into a set
     *
     * @param <T> set element type
     * @param values elements
     * @return set containing the given elements
     */
    public static <T> Set<T> asSet( T... values ) {
        return new HashSet<>( Arrays.asList( values ) );
    }

    /**
     * Converts parameters into a list
     *
     * @param <T> list element type
     * @param values elements
     * @return list containing the given elements
     */
    public static <T> List<T> asList( T... values ) {
        return Arrays.asList( values );
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.graph.SimpleNode;

import java.util.Collection;

/**
 * String utilities
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class StringUtils {

    /**
     * Replaces last character in the given {@link StringBuilder} with the replacement based on the given condition
     *
     * @param sb              given {@link StringBuilder}
     * @param conditionResult condition for replacement
     * @param replacement     replacement
     * @return {@link StringBuilder} containing the replacement, should the condition result in true, original {@link StringBuilder} otherwise
     */
    public static StringBuilder replaceLast( StringBuilder sb, boolean conditionResult, String replacement ) {
        if ( conditionResult ) {
            sb.replace( sb.length() - 1, sb.length(), replacement );
        } else {
            sb.append( replacement );
        }
        return sb;
    }

    /**
     * Prints this collection as an array using {@link Identifiable#getId()} method on each element, e.g. [el1,el2,el3,...]
     *
     * @param items items
     * @param <I>   element type
     * @return string in form of [elem1,elem2,elem3,...]
     */
    public static <I extends Identifiable> String toArray( Collection<I> items ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( I item : items ) {
            sb.append( item.getId() ).append( "," );
        }
        return replaceLast( sb, !items.isEmpty(), "]" ).toString();
    }

    /**
     * Prints this collection as an array using the given {@link StringExtractor} on each element, e.g. [el1,el2,el3,...]
     *
     * @param items     items
     * @param extractor string extractor
     * @param <T>       element type
     * @return string in form of [elem1,elem2,elem3,...]
     */
    public static <T> String toArray( Collection<T> items, StringExtractor<T> extractor ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( T item : items ) {
            sb.append( extractor.toString( item ) ).append( "," );
        }
        return replaceLast( sb, !items.isEmpty(), "]" ).toString();
    }

    /**
     * Extractor mapping given item to string
     *
     * @param <T> element type
     */
    public interface StringExtractor<T> {

        /**
         * Maps item to string
         *
         * @param item item
         * @return string representation
         */
        String toString( T item );
    }
}

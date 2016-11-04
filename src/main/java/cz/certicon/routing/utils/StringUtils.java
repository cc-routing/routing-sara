/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.Identifiable;
import java.util.Collection;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class StringUtils {

    public static StringBuilder replaceLast( StringBuilder sb, boolean conditionResult, String replacement ) {
        if ( conditionResult ) {
            sb.replace( sb.length() - 1, sb.length(), replacement );
        } else {
            sb.append( replacement );
        }
        return sb;
    }

    public static <I extends Identifiable> String toArray( Collection<I> items ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( I item : items ) {
            sb.append( item.getId() ).append( "," );
        }
        return replaceLast( sb, !items.isEmpty(), "]" ).toString();
    }

    public static <T> String toArray( Collection<T> items, StringExtractor<T> extractor ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( T item : items ) {
            sb.append( extractor.toString( item ) ).append( "," );
        }
        return replaceLast( sb, !items.isEmpty(), "]" ).toString();
    }

    public interface StringExtractor<T> {

        String toString( T item );
    }
}

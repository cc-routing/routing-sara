/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.java8;

import java.util.Iterator;

import java8.util.Spliterator;
import java8.util.Spliterators;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

/**
 * Stream support for iterators.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class IteratorStreams {

    /**
     * Creates stream for the given iterator. The iterator becomes used after this.
     *
     * @param iterator given iterator
     * @param <T>      element type
     * @return stream for the given iterator
     */
    public static <T> Stream<T> stream( Iterator<T> iterator ) {
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( iterator, Spliterator.ORDERED ), false );
    }
}

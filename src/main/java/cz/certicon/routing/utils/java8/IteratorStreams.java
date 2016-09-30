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
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class IteratorStreams {

    public static <T> Stream<T> stream( Iterator<T> iterator ) {
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( iterator, Spliterator.ORDERED ), false );
    }
}

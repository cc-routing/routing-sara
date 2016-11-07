/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.collections;

/**
 * Iterable iterator. Do not re-use. Convenience class for enhanced for-each loop. Once the for-each loop supports iterators, this class becomes deprecated.
 *
 * @param <T> type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface Iterator<T> extends java.util.Iterator<T>, Iterable<T> {

}

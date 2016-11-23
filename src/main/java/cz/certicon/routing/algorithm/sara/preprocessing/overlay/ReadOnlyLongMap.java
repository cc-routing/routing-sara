/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public interface ReadOnlyLongMap<T> extends Iterable<T> {
    T get(long key);

    boolean containsKey(long key);

    int size();
}

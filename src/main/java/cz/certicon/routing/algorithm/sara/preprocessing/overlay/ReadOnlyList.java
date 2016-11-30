/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

/**
 * Readonly List interface
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public interface ReadOnlyList<T> extends Iterable<T> {
    T get(int index);

    int size();
}

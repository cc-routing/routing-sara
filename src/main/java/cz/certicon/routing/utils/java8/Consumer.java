/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.java8;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <T>
 * @deprecated use {@link java8.util.function.Consumer} instead
 */
public interface Consumer<T> {

    void accept( T t );
}

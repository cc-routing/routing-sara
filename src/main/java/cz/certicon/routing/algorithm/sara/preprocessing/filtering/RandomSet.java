/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import java.util.Random;
import java.util.Set;

/**
 * {@link Set} extension with random element polling.
 *
 * @param <E> element type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
interface RandomSet<E> extends Set<E> {

    /**
     * Returns random element (given {@link Random})
     *
     * @param rnd {@link Random} source of randomness
     * @return random element
     */
    E pollRandom( Random rnd );
}

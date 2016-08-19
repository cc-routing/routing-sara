/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import java.util.Random;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <E> element type
 */
interface RandomSet<E> extends Set<E> {

    public E removeAt( int id );

    public E get( int i );

    public E pollRandom( Random rnd );
}

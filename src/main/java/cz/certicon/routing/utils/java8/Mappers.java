/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.java8;

import cz.certicon.routing.model.Identifiable;
import java8.util.function.Function;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class Mappers {

    public static final Function<Object, String> objectToString = new Function<Object, String>() {
        @Override
        public String apply( Object t ) {
            return t.toString();
        }
    };

    public static final Function<Identifiable, String> identifiableToString = new Function<Identifiable, String>() {
        @Override
        public String apply( Identifiable t ) {
            return Long.toString( t.getId() );
        }
    };
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.java8;

import cz.certicon.routing.model.Identifiable;
import java8.util.function.Function;

/**
 * Container for frequently used mappers
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Mappers {

    /**
     * Maps {@link Object} to {@link String} via {@link Object#toString()}
     */
    public static final Function<Object, String> objectToString = new Function<Object, String>() {
        @Override
        public String apply( Object t ) {
            return t.toString();
        }
    };

    /**
     * Maps {@link Identifiable} to {@link String} via id to string conversion
     */
    public static final Function<Identifiable, String> identifiableToString = new Function<Identifiable, String>() {
        @Override
        public String apply( Identifiable t ) {
            return Long.toString( t.getId() );
        }
    };
}

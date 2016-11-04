/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import java.util.Comparator;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface Identifiable {

    long getId();

    class Comparators {

        public static Comparator<Identifiable> createIdComparator() {
            return new Comparator<Identifiable>() {
                @Override
                public int compare( Identifiable o1, Identifiable o2 ) {
                    return Long.compare( o1.getId(), o2.getId() );
                }
            };
        }

        public static Comparator<Identifiable> createIdComparatorDesc() {
            return new Comparator<Identifiable>() {
                @Override
                public int compare( Identifiable o1, Identifiable o2 ) {
                    return Long.compare( o2.getId(), o1.getId() );
                }
            };
        }
    }
}

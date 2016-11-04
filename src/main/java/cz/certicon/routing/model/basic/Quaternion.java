/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.basic;

import java.util.Objects;

/**
 * A generic container class for four objects. Based on the implementations of the contained object, it support hashCode, equals and toString (via delegation and combination).
 *
 * @param <A> class of the first object
 * @param <B> class of the second object
 * @param <C> class of the third object
 * @param <D> class of the fourth object
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Quaternion<A, B, C, D> extends Trinity<A, B, C> {

    public final D d;

    /**
     * Constructor
     *
     * @param a object a
     * @param b object b
     * @param c object c
     * @param d object d
     */
    public Quaternion( A a, B b, C c, D d ) {
        super( a, b, c );
        this.d = d;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + d.hashCode();
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Quaternion<?, ?, ?, ?> other = (Quaternion<?, ?, ?, ?>) obj;
        return Objects.equals( this.d, other.d );
    }

    @Override
    public String toString() {
        return "Quaternion{" + "a=" + a + "b=" + b + "c=" + c + "d=" + d + '}';
    }

}

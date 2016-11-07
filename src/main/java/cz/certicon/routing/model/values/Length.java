/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.values;

import cz.certicon.routing.utils.DoubleComparator;

import java.util.Objects;

/**
 * Class representing the length of something. It supports unit conversion,
 * formatting and so on.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Length implements Number<Length> {

    private final LengthUnits lengthUnits;
    private final long nanometers;

    /**
     * Creates an instance via an amount and units
     *
     * @param lengthUnits the length units in which the length is provided
     * @param length      actual value
     */
    public Length( LengthUnits lengthUnits, long length ) {
        this.lengthUnits = lengthUnits;
        this.nanometers = lengthUnits.toNano( length );
    }

    /**
     * Returns current units
     *
     * @return current units
     */
    public LengthUnits getLengthUnits() {
        return lengthUnits;
    }

    /**
     * Returns length in nanometers
     *
     * @return length in nanometers
     */
    public long getNanometers() {
        return nanometers;
    }

    /**
     * Returns length in the current units
     *
     * @return length in the current units
     */
    public long getValue() {
        return lengthUnits.fromNano( nanometers );
    }

    /**
     * Returns length in the provided units
     *
     * @param lengthUnits provided units
     * @return length in the provided units
     */
    public long getValue( LengthUnits lengthUnits ) {
        return lengthUnits.fromNano( nanometers );
    }

    /**
     * Returns string representing the length in the provided units
     *
     * @param lengthUnits provided units
     * @return string in format "%d %s", length, unit
     */
    public String toString( LengthUnits lengthUnits ) {
        return getValue( lengthUnits ) + " " + lengthUnits.getUnit();
    }

    /**
     * Returns string representation of the current units
     *
     * @return string representation of the current units
     */
    public String getUnit() {
        return lengthUnits.getUnit();
    }

    @Override
    public String toString() {
        return "" + Length.this.getValue();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode( this.lengthUnits );
        hash = 97 * hash + (int) ( this.nanometers ^ ( this.nanometers >>> 32 ) );
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
        final Length other = (Length) obj;
        if ( this.nanometers != other.nanometers ) {
            return false;
        }
        return this.lengthUnits == other.lengthUnits;
    }


    public int compareTo( Length other ) {
        return Long.compare( getValue(), other.getValue() );
    }

    @Override
    public boolean isGreaterThan( Length other ) {
        return compareTo( other ) > 0;
    }

    @Override
    public boolean isGreaterOrEqualTo( Length other ) {
        return compareTo( other ) >= 0;
    }

    @Override
    public boolean isLowerThan( Length other ) {
        return compareTo( other ) < 0;
    }

    @Override
    public boolean isLowerOrEqualTo( Length other ) {
        return compareTo( other ) <= 0;
    }

    @Override
    public boolean isEqualTo( Length other ) {
        return compareTo( other ) == 0;
    }

    @Override
    public int compareTo( Number<Length> o ) {
        return compareTo( o.identity() );
    }

    @Override
    public boolean isPositive() {
        return nanometers > 0;
    }

    @Override
    public boolean isNegative() {
        return nanometers < 0;
    }

    @Override
    public Length absolute() {
        return new Length( lengthUnits, lengthUnits.fromNano( Math.abs( nanometers ) ) );
    }

    @Override
    public Length add( Length other ) {
        return new Length( lengthUnits, lengthUnits.fromNano( nanometers + other.nanometers ) );
    }

    @Override
    public Length subtract( Length other ) {
        return new Length( lengthUnits, lengthUnits.fromNano( nanometers - other.nanometers ) );
    }

    @Override
    public Length divide( Length other ) {
        return new Length( lengthUnits, lengthUnits.fromNano( nanometers / other.nanometers ) );
    }

    @Override
    public Length multiply( Length other ) {
        return new Length( lengthUnits, lengthUnits.fromNano( nanometers * other.nanometers ) );
    }

    @Override
    public Length identity() {
        return this;
    }
}

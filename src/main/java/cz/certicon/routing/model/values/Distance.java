/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.values;

import cz.certicon.routing.utils.DoubleComparator;
import lombok.Value;

/**
 * Representation of distance implementing the {@link Number} interface.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Value
public class Distance implements Number<Distance> {

    double value;
    private static final double EPS = 10E-10;

    private static final Distance DISTANCE_ZERO = new Distance( 0 );
    private static final Distance DISTANCE_ONE = new Distance( 1 );
    private static final Distance DISTANCE_TWO = new Distance( 2 );
    private static final Distance DISTANCE_INFINITY = new Distance( Double.MAX_VALUE );

    /**
     * Returns instance of {@link Distance} representing the provided double value. Returned instance does not have to be unique - 0,1,2 and infinity instances for example are premade.
     *
     * @param dist distance as double value
     * @return new instnace of {@link Distance}
     */
    public static Distance newInstance( double dist ) {
        if ( DoubleComparator.isEqualTo( dist, 0, EPS ) ) {
            return DISTANCE_ZERO;
        }
        if ( DoubleComparator.isEqualTo( dist, Double.MAX_VALUE, EPS ) || dist == Double.POSITIVE_INFINITY ) {
            return DISTANCE_INFINITY;
        }
        if ( DoubleComparator.isEqualTo( dist, 1, EPS ) ) {
            return DISTANCE_ONE;
        }
        if ( DoubleComparator.isEqualTo( dist, 2, EPS ) ) {
            return DISTANCE_TWO;
        }
        return new Distance( dist );
    }

    /**
     * Returns infinity instance
     *
     * @return infinity instance
     */
    public static Distance newInfinityInstance() {
        return DISTANCE_INFINITY;
    }

    public static Distance newZeroDistance() {
        return DISTANCE_ZERO;
    }

    /**
     * Private constructor. Use newInstnace instead.
     *
     * @param dist double value
     */
    private Distance( double dist ) {
        this.value = dist;
    }

    public int compareTo( Distance other ) {
        return DoubleComparator.compare( value, other.value, EPS );
    }

    @Override
    public boolean isGreaterThan( Distance other ) {
        return compareTo( other ) > 0;
    }

    @Override
    public boolean isGreaterOrEqualTo( Distance other ) {
        return compareTo( other ) >= 0;
    }

    @Override
    public boolean isLowerThan( Distance other ) {
        return compareTo( other ) < 0;
    }

    @Override
    public boolean isLowerOrEqualTo( Distance other ) {
        return compareTo( other ) <= 0;
    }

    @Override
    public boolean isEqualTo( Distance other ) {
        return compareTo( other ) == 0;
    }

    @Override
    public int compareTo( Number<Distance> o ) {
        return compareTo( o.identity() );
    }

    /**
     * Returns whether this number is infinite or not
     *
     * @return true if this number is infinite, false otherwise
     */
    public boolean isInfinite() {
        return isGreaterOrEqualTo( DISTANCE_INFINITY );
    }

    @Override
    public Distance add( Distance other ) {
        return new Distance( value + other.value );
    }

    @Override
    public Distance subtract( Distance other ) {
        return new Distance( value - other.value );
    }

    @Override
    public Distance divide( Distance other ) {
        return new Distance( value / other.value );
    }

    @Override
    public Distance multiply( Distance other ) {
        return new Distance( value * other.value );
    }

    @Override
    public Distance identity() {
        return this;
    }

    @Override
    public boolean isPositive() {
        return DoubleComparator.isGreaterThan( value, 0, EPS );
    }

    @Override
    public boolean isNegative() {
        return DoubleComparator.isLowerThan( value, 0, EPS );
    }

    @Override
    public Distance absolute() {
        if ( isNegative() ) {
            return new Distance( Math.abs( value ) );
        }
        return this;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.values;

import cz.certicon.routing.utils.DoubleComparator;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class Distance implements Number<Distance> {

    double value;
    private static final double EPS = 10E-10;

    private static final Distance DISTANCE_ZERO = new Distance( 0 );
    private static final Distance DISTANCE_ONE = new Distance( 1 );
    private static final Distance DISTANCE_TWO = new Distance( 2 );
    private static final Distance DISTANCE_INFINITY = new Distance( Double.MAX_VALUE );

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

    public static Distance newInfinityInstance() {
        return DISTANCE_INFINITY;
    }

    public Distance( double dist ) {
        this.value = dist;
    }

    public boolean isGreaterThan( Distance other ) {
        return compareTo( other ) > 0;
    }

    public boolean isGreaterOrEqualTo( Distance other ) {
        return compareTo( other ) >= 0;
    }

    public boolean isLowerThan( Distance other ) {
        return compareTo( other ) < 0;
    }

    public boolean isLowerOrEqualTo( Distance other ) {
        return compareTo( other ) <= 0;
    }

    public boolean isEqualTo( Distance other ) {
        return compareTo( other ) == 0;
    }

    public int compareTo( Distance other ) {
        return DoubleComparator.compare( value, other.value, EPS );
    }

    public boolean isInfinite() {
        return isGreaterOrEqualTo( DISTANCE_INFINITY );
    }

    @Override
    public Distance add( Distance other ) {
        return new Distance( value + other.value );
    }

    @Override
    public Distance substract( Distance other ) {
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.values;

import java.util.Objects;

/**
 * Class representation of time. Supports conversion, formatting, etc.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class Time implements Number<Time> {

    private final TimeUnits timeUnits;
    private final long nanoseconds;

    /**
     * Creates an instance via an amount and units
     *
     * @param timeUnits time units in which the length is provided
     * @param time actual value
     */
    public Time( TimeUnits timeUnits, long time ) {
        this.timeUnits = timeUnits;
        this.nanoseconds = timeUnits.toNano( time );
    }

    /**
     * Returns current units
     *
     * @return current units
     */
    public TimeUnits getTimeUnits() {
        return timeUnits;
    }

    /**
     * Returns time in nanoseconds
     *
     * @return time in nanoseconds
     */
    public long getNanoseconds() {
        return nanoseconds;
    }

    /**
     * Returns time in the current units
     *
     * @return time in the current units
     */
    public long getTime() {
        return timeUnits.fromNano( nanoseconds );
    }

    /**
     * Returns time in the provided units
     *
     * @param timeUnits provided units
     * @return time in the provided units
     */
    public long getTime( TimeUnits timeUnits ) {
        return timeUnits.fromNano( nanoseconds );
    }

    /**
     * Returns string representation of the current units
     *
     * @return string representation of the current units
     */
    public String getUnit() {
        return timeUnits.getUnit();
    }

    /**
     * Divides this time by a provided divisor. Returns new instance.
     *
     * @param divisor number for the time to be divided by
     * @return new instance of {@link Time} as a result of division of this time
     * by the divisor
     */
    public Time divide( long divisor ) {
        return new Time( timeUnits, getTime() / divisor );
    }

    @Override
    public String toString() {
        return "" + getTime() + " " + timeUnits.getUnit();
    }

    /**
     * Returns string representing the time in the provided units
     *
     * @param timeUnits provided units
     * @return string in format "%d %s", time, unit
     */
    public String toString( TimeUnits timeUnits ) {
        return getTime( timeUnits ) + " " + timeUnits.getUnit();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode( this.timeUnits );
        hash = 71 * hash + (int) ( this.nanoseconds ^ ( this.nanoseconds >>> 32 ) );
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
        final Time other = (Time) obj;
        if ( this.nanoseconds != other.nanoseconds ) {
            return false;
        }
        if ( this.timeUnits != other.timeUnits ) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isPositive() {
        return nanoseconds > 0;
    }

    @Override
    public boolean isNegative() {
        return nanoseconds < 0;
    }

    @Override
    public Time absolute() {
        return new Time( timeUnits, timeUnits.fromNano( Math.abs( nanoseconds ) ) );
    }

    public Time add( Time other ) {
        return new Time( timeUnits, timeUnits.fromNano( nanoseconds + other.nanoseconds ) );
    }

    @Override
    public Time substract( Time other ) {
        return new Time( timeUnits, timeUnits.fromNano( nanoseconds - other.nanoseconds ) );
    }

    @Override
    public Time divide( Time other ) {
        return new Time( timeUnits, timeUnits.fromNano( nanoseconds / other.nanoseconds ) );
    }

    @Override
    public Time multiply( Time other ) {
        return new Time( timeUnits, timeUnits.fromNano( nanoseconds * other.nanoseconds ) );
    }

}

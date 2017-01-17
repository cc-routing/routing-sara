/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.values;

import cz.certicon.routing.utils.StringUtils;

import java.util.Objects;
import java.util.Set;

/**
 * Class representation of time. Supports conversion, formatting, etc.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Time implements Number<Time> {

    private final TimeUnits timeUnits;
    private final long nanoseconds;

    /**
     * Creates an instance via an amount and units
     *
     * @param timeUnits time units in which the length is provided
     * @param time      actual value
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
    public long getValue() {
        return timeUnits.fromNano( nanoseconds );
    }

    /**
     * Returns time in the provided units
     *
     * @param timeUnits provided units
     * @return time in the provided units
     */
    public long getValue( TimeUnits timeUnits ) {
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
        return new Time( timeUnits, Time.this.getValue() / divisor );
    }

    @Override
    public String toString() {
        return "" + Time.this.getValue() + " " + timeUnits.getUnit();
    }

    /**
     * Returns string representing the time in the provided units
     *
     * @param timeUnits provided units
     * @return string in format "%d %s", time, unit
     */
    public String toString( TimeUnits timeUnits ) {
        return getValue( timeUnits ) + " " + timeUnits.getUnit();
    }

    /**
     * Returns string representing the time passed contained in this object, using the requested units
     *
     * @param timeUnitsSet set of requested units
     * @return formatted string, e.g. 3 years, 4 months, 2 days, 10 hours, 55 minutes, 53 seconds
     */
    public String toString(Set<TimeUnits> timeUnitsSet){
        StringBuilder sb = new StringBuilder();
        long nanos = nanoseconds;
        TimeUnits[] timeUnits = TimeUnits.values();
        for(int i = timeUnits.length - 1; i >= 0; i--){
            if(timeUnitsSet.contains( timeUnits[i] )){
                long count = timeUnits[i].fromNano( nanos );
                sb.append( count ).append( " " ).append( timeUnits[i].getUnit() ).append( ", " );
                nanos -= timeUnits[i].toNano( count );
            }
        }
        StringUtils.replaceLast( sb, sb.length() > 0, "" );
        StringUtils.replaceLast( sb, sb.length() > 0, "." );
        return sb.toString();
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
        return this.timeUnits == other.timeUnits;
    }


    public int compareTo( Time other ) {
        return Long.compare( getValue(), other.getValue() );
    }

    @Override
    public boolean isGreaterThan( Time other ) {
        return compareTo( other ) > 0;
    }

    @Override
    public boolean isGreaterOrEqualTo( Time other ) {
        return compareTo( other ) >= 0;
    }

    @Override
    public boolean isLowerThan( Time other ) {
        return compareTo( other ) < 0;
    }

    @Override
    public boolean isLowerOrEqualTo( Time other ) {
        return compareTo( other ) <= 0;
    }

    @Override
    public boolean isEqualTo( Time other ) {
        return compareTo( other ) == 0;
    }

    @Override
    public int compareTo( Number<Time> o ) {
        return compareTo( o.identity() );
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
    public Time subtract( Time other ) {
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

    @Override
    public Time identity() {
        return this;
    }
}

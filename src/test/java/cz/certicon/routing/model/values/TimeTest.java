package cz.certicon.routing.model.values;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class TimeTest {

    private Time time;

    @Before
    public void setUp() throws Exception {
        time = new Time( TimeUnits.NANOSECONDS,
                TimeUnits.YEARS.toNano( 1 )
                        + TimeUnits.MONTHS.toNano( 2 )
                        + TimeUnits.DAYS.toNano( 3 )
                        + TimeUnits.HOURS.toNano( 4 )
                        + TimeUnits.MINUTES.toNano( 5 )
                        + TimeUnits.SECONDS.toNano( 6 )
                        + TimeUnits.MILLISECONDS.toNano( 7 )
                        + TimeUnits.MICROSECONDS.toNano( 8 )
                        + TimeUnits.NANOSECONDS.toNano( 9 ) );
    }

    @Test
    public void toString_returns_years_and_microseconds() throws Exception {
        long nanos = time.getNanoseconds();
        long years = TimeUnits.YEARS.fromNano( nanos );
        long microseconds = TimeUnits.MICROSECONDS.fromNano( nanos - TimeUnits.YEARS.toNano( years ) );
        String expected = years + " years, " + microseconds + " mcs.";
        assertThat( time.toString( EnumSet.of( TimeUnits.YEARS, TimeUnits.MICROSECONDS ) ), equalTo( expected ) );
    }


    @Test
    public void toString_returns_months_and_hours() throws Exception {
        String expected = "14 months, 76 h.";
        assertThat( time.toString( EnumSet.of( TimeUnits.MONTHS, TimeUnits.HOURS ) ), equalTo( expected ) );
    }
}
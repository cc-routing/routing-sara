/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.measuring;

import cz.certicon.routing.model.values.TimeUnits;
import cz.certicon.routing.model.values.Time;

import java.util.HashMap;
import java.util.Map;

/**
 * Logging class for execution times. See
 * {@link #getTimeMeasurement(cz.certicon.routing.utils.measuring.TimeLogger.Event) getTimeMeasurement(Event)}
 * method for elapsed time retrieval.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class TimeLogger {

    private static final boolean ALLOW_LOGS = true;
    private static final Map<Event, TimeMeasurement> TIME_MAP = new HashMap<>();
    private static final Map<String, TimeMeasurement> TIME_STRING_MAP = new HashMap<>();
    private static TimeUnits timeUnits = TimeUnits.MILLISECONDS;

    /**
     * Set time units globally =&gt; all the timers will return values in these
     * units
     *
     * @param timeUnits given time units
     */
    public static void setTimeUnits( TimeUnits timeUnits ) {
        if ( ALLOW_LOGS ) {
            TimeLogger.timeUnits = timeUnits;
            for ( TimeMeasurement value : TIME_MAP.values() ) {
                value.setTimeUnits( timeUnits );
            }
            for ( TimeMeasurement value : TIME_STRING_MAP.values() ) {
                value.setTimeUnits( timeUnits );
            }
        }
    }

    /**
     * Log given {@link Command} for given {@link Event} type
     *
     * @param event   given event type
     * @param command given command
     */
    public static void log( Event event, Command command ) {
        if ( ALLOW_LOGS ) {
            command.execute( getTimeMeasurement( event ) );
        }
    }

    /**
     * Log given {@link Command} for given {@link Event} type
     *
     * @param eventName given event name
     * @param command   given command
     */
    public static void log( String eventName, Command command ) {
        if ( ALLOW_LOGS ) {
            command.execute( getTimeMeasurement( eventName ) );
        }
    }

    /**
     * Returns {@link TimeMeasurement} object for the given event. Extract
     * elapsed {@link Time} via
     * {@link TimeMeasurement#getTime() timeMeasurement.getTime()}
     *
     * @param event given event
     * @return time measurement object
     */
    public static TimeMeasurement getTimeMeasurement( Event event ) {
        if ( ALLOW_LOGS ) {
            TimeMeasurement time = TIME_MAP.get( event );
            if ( time == null ) {
                time = new TimeMeasurement();
                time.setTimeUnits( timeUnits );
                TIME_MAP.put( event, time );
            }
            return time;
        } else {
            throw new IllegalStateException( "Logs not allowed" );
        }
    }

    /**
     * Returns {@link TimeMeasurement} object for the given event. Extract
     * elapsed {@link Time} via
     * {@link TimeMeasurement#getTime() timeMeasurement.getTime()}
     *
     * @param eventName given event name
     * @return time measurement object
     */
    public static TimeMeasurement getTimeMeasurement( String eventName ) {
        TimeMeasurement time = TIME_STRING_MAP.get( eventName );
        if ( time == null ) {
            time = new TimeMeasurement();
            time.setTimeUnits( timeUnits );
            TIME_STRING_MAP.put( eventName, time );
        }
        return time;
    }

    /**
     * Enumeration for event types
     */
    public static enum Event {
        ASSEMBLING,
        FILTERING,
        /**
         * Preprocessing
         */
        PREPROCESSING,
        /**
         * Loading graph
         */
        GRAPH_LOADING,
        /**
         * Loading preprocessed data
         */
        PREPROCESSED_LOADING,
        /**
         * Searching for nodes
         */
        NODE_SEARCHING,
        /**
         * Routing
         */
        ROUTING,
        /**
         * Building route
         */
        ROUTE_BUILDING,
        /**
         * Loading path data
         */
        PATH_LOADING;
    }

    /**
     * Interface for commands as well as wrapping class for predefined commands.
     */
    public static enum Command {
        /**
         * Start given time measurement
         */
        START {
            @Override
            void execute( TimeMeasurement timeMeasurement ) {
                timeMeasurement.start();
            }
        },
        /**
         * Stop given time measurement
         */
        STOP {
            @Override
            void execute( TimeMeasurement timeMeasurement ) {
                timeMeasurement.stop();
            }
        },
        /**
         * Pause given time measurement
         */
        PAUSE {
            @Override
            void execute( TimeMeasurement timeMeasurement ) {
                timeMeasurement.pause();
            }
        },
        /**
         * Continue given time measurement
         */
        CONTINUE {
            @Override
            void execute( TimeMeasurement timeMeasurement ) {
                timeMeasurement.continue_();
            }
        };

        abstract void execute( TimeMeasurement timeMeasurement );
    }
}

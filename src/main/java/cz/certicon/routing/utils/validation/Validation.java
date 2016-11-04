/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.validation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Validation {

    public static void validateThat( Validator validator ) {
        String validate = validator.validate();
        if ( validate != null ) {
            throw new IllegalArgumentException( validate );
        }
    }

    public static Validator not( Validator validator ) {
        return new Validator( validator.validate() == null, "Not: " + validator.getErrorMessage() );
    }

    public static <T> Validator notNull( T actual ) {
        return new Validator( actual != null, "Value is null: " + actual );
    }

    public static <T extends Comparable<T>> Validator greaterThan( T actual, T value ) {
        return new Validator( actual.compareTo( value ) == 1, "Not greater than: " + actual + ", " + value );
    }

    public static <T extends Comparable<T>> Validator greaterOrEqualTo( T actual, T value ) {
        return new Validator( actual.compareTo( value ) >= 0, "Not greater or equal to: " + actual + ", " + value );
    }

    public static <T extends Comparable<T>> Validator equalTo( T actual, T value ) {
        return new Validator( actual.compareTo( value ) == 0, "Not equal to: " + actual + ", " + value );
    }

    public static <T extends Comparable<T>> Validator smallerThan( T actual, T value ) {
        return new Validator( actual.compareTo( value ) == -1, "Not smaller than: " + actual + ", " + value );
    }

    public static <T extends Comparable<T>> Validator smallerOrEqualTo( T actual, T value ) {
        return new Validator( actual.compareTo( value ) <= 0, "Not smaller or equal to: " + actual + ", " + value );
    }

    public static <T> Validator greaterThan( T actual, T value, Comparator<T> comparator ) {
        return new Validator( comparator.compare( actual, value ) == 1, "Not greater than: " + actual + ", " + value );
    }

    public static <T> Validator greaterOrEqualTo( T actual, T value, Comparator<T> comparator ) {
        return new Validator( comparator.compare( actual, value ) >= 0, "Not greater or equal to: " + actual + ", " + value );
    }

    public static <T> Validator equalTo( T actual, T value, Comparator<T> comparator ) {
        return new Validator( comparator.compare( actual, value ) == 0, "Not equal to: " + actual + ", " + value );
    }

    public static <T> Validator smallerThan( T actual, T value, Comparator<T> comparator ) {
        return new Validator( comparator.compare( actual, value ) == -1, "Not smaller than: " + actual + ", " + value );
    }

    public static <T> Validator smallerOrEqualTo( T actual, T value, Comparator<T> comparator ) {
        return new Validator( comparator.compare( actual, value ) <= 0, "Not smaller or equal to: " + actual + ", " + value );
    }

    public static Validator greaterThan( long actual, long value ) {
        return new Validator( actual > value, "Not greater than: " + actual + ", " + value );
    }

    public static Validator greaterOrEqualTo( long actual, long value ) {
        return new Validator( actual >= value, "Not greater or equal to: " + actual + ", " + value );
    }

    public static Validator equalTo( long actual, long value ) {
        return new Validator( actual == value, "Not equal to: " + actual + ", " + value );
    }

    public static Validator smallerThan( long actual, long value ) {
        return new Validator( actual < value, "Not smaller than: " + actual + ", " + value );
    }

    public static Validator smallerOrEqualTo( long actual, long value ) {
        return new Validator( actual <= value, "Not smaller or equal to: " + actual + ", " + value );
    }

    public static Validator greaterThan( double actual, double value ) {
        return new Validator( actual > value, "Not greater than: " + actual + ", " + value );
    }

    public static Validator greaterOrEqualTo( double actual, double value ) {
        return new Validator( actual >= value, "Not greater or equal to: " + actual + ", " + value );
    }

    public static Validator equalTo( double actual, double value ) {
        return new Validator( actual == value, "Not equal to: " + actual + ", " + value );
    }

    public static Validator smallerThan( double actual, double value ) {
        return new Validator( actual < value, "Not smaller than: " + actual + ", " + value );
    }

    public static Validator smallerOrEqualTo( double actual, double value ) {
        return new Validator( actual <= value, "Not smaller or equal to: " + actual + ", " + value );
    }

    public static Validator valid() {
        return new Validator( true, null );
    }

    public static Validator invalid() {
        return invalid( "Invalid" );
    }

    public static Validator invalid( String message ) {
        return new Validator( false, message );
    }

    public static class Validator {

        private final List<Validator> andValidators = new ArrayList<>();
        private final List<Validator> orValiditors = new ArrayList<>();
        private final Map<Validator, String> parameterNameMap = new HashMap<>();
        private final String errorMessage;

        public Validator( boolean result, String errorMessage ) {
            if ( result ) {
                this.errorMessage = null;
            } else {
                this.errorMessage = errorMessage;
            }
        }

        public Validator and( Validator validator ) {
            if ( !orValiditors.isEmpty() ) {
                throw new UnsupportedOperationException( "Do not combine 'and' and 'or' on the same level." );
            }
            andValidators.add( validator );
            return this;
        }

        public Validator and( String parameterName, Validator validator ) {
            parameterNameMap.put( validator, parameterName );
            return and( validator );
        }

        public Validator or( Validator validator ) {
            if ( !andValidators.isEmpty() ) {
                throw new UnsupportedOperationException( "Do not combine 'and' and 'or' on the same level." );
            }
            orValiditors.add( validator );
            return this;
        }

        public Validator or( String parameterName, Validator validator ) {
            parameterNameMap.put( validator, parameterName );
            return or( validator );
        }

        protected List<Validator> getAndValidators() {
            return andValidators;
        }

        protected List<Validator> getOrValidators() {
            return orValiditors;
        }

        public String validate() {
            String message = getErrorMessage();
            if ( !andValidators.isEmpty() ) {
                for ( Validator andValidator : andValidators ) {
                    if ( andValidator.validate() != null ) {
                        String parameterName = parameterNameMap.get( andValidator );
                        return ( parameterName == null ? "" : parameterName + " - " ) + andValidator.validate();
                    }
                }
            } else if ( !orValiditors.isEmpty() ) {
                for ( Validator orValiditor : orValiditors ) {
                    if ( orValiditor.validate() == null ) {
                        return null;
                    }
                }
            }
            return message;
        }

        public String getErrorMessage() {
            String message = errorMessage;
            return message;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode( this.errorMessage );
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
            final Validator other = (Validator) obj;
            if ( !Objects.equals( this.errorMessage, other.errorMessage ) ) {
                return false;
            }
            if ( !Objects.equals( this.andValidators, other.andValidators ) ) {
                return false;
            }
            if ( !Objects.equals( this.orValiditors, other.orValiditors ) ) {
                return false;
            }
            return Objects.equals( this.parameterNameMap, other.parameterNameMap );
        }

    }
}

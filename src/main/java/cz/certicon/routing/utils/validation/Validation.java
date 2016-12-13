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
 * Validation class for input parameters. use {@link #validateThat(ValidatorExpression)} as a start of validation and {@link #valid()} as a start of expression.
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Validation {

    /**
     * Validates given validatorExpression
     *
     * @param validatorExpression given validatorExpression
     */
    public static void validateThat( ValidatorExpression validatorExpression ) {
        String validate = validatorExpression.validate();
        if ( validate != null ) {
            throw new IllegalArgumentException( validate );
        }
    }

    /**
     * Validates given validatorExpression
     *
     * @param parameterName       parameter to be displayed should the validation fail
     * @param validatorExpression given validatorExpression
     */
    public static void validateThat( String parameterName, ValidatorExpression validatorExpression ) {
        String validate = validatorExpression.validate();
        if ( validate != null ) {
            throw new IllegalArgumentException( ( parameterName == null ? "" : parameterName + " - " ) + validate );
        }
    }

    /**
     * Negates given expression
     *
     * @param validatorExpression given expression
     * @return negated expression
     */
    public static ValidatorExpression not( ValidatorExpression validatorExpression ) {
        return new ValidatorExpression( validatorExpression.validate() == null, "Not: " + validatorExpression.getErrorMessage() );
    }

    /**
     * The actual is not null
     *
     * @param actual the actual
     * @param <T>    actual type
     * @return expression that the actual is not null
     */
    public static <T> ValidatorExpression notNull( T actual ) {
        return new ValidatorExpression( actual != null, "Value is null: " + actual );
    }

    /**
     * The actual is greater than the value
     *
     * @param actual actual
     * @param value  value
     * @param <T>    type
     * @return expression that the actual is greater than the value
     */
    public static <T extends Comparable<T>> ValidatorExpression greaterThan( T actual, T value ) {
        return new ValidatorExpression( actual.compareTo( value ) == 1, "Not greater than: " + actual + ", " + value );
    }

    /**
     * The actual is greater or equal to the value
     *
     * @param actual actual
     * @param value  value
     * @param <T>    type
     * @return expression that the actual is greater or equal to the value
     */
    public static <T extends Comparable<T>> ValidatorExpression greaterOrEqualTo( T actual, T value ) {
        return new ValidatorExpression( actual.compareTo( value ) >= 0, "Not greater or equal to: " + actual + ", " + value );
    }

    /**
     * The actual is equal to the value
     *
     * @param actual actual
     * @param value  value
     * @param <T>    type
     * @return expression that the actual is equal to the value
     */
    public static <T extends Comparable<T>> ValidatorExpression equalTo( T actual, T value ) {
        return new ValidatorExpression( actual.compareTo( value ) == 0, "Not equal to: " + actual + ", " + value );
    }

    /**
     * The actual is smaller than the value
     *
     * @param actual actual
     * @param value  value
     * @param <T>    type
     * @return expression that the actual is smaller than the value
     */
    public static <T extends Comparable<T>> ValidatorExpression smallerThan( T actual, T value ) {
        return new ValidatorExpression( actual.compareTo( value ) == -1, "Not smaller than: " + actual + ", " + value );
    }

    /**
     * The actual is smaller or equal to the value
     *
     * @param actual actual
     * @param value  value
     * @param <T>    type
     * @return expression that the actual is smaller or equal to the value
     */
    public static <T extends Comparable<T>> ValidatorExpression smallerOrEqualTo( T actual, T value ) {
        return new ValidatorExpression( actual.compareTo( value ) <= 0, "Not smaller or equal to: " + actual + ", " + value );
    }

    /**
     * The actual is greater than the value using the given comparator
     *
     * @param actual     actual
     * @param value      value
     * @param comparator comparator
     * @param <T>        type
     * @return expression that the actual is greater than the value using the given comparator
     */
    public static <T> ValidatorExpression greaterThan( T actual, T value, Comparator<T> comparator ) {
        return new ValidatorExpression( comparator.compare( actual, value ) == 1, "Not greater than: " + actual + ", " + value );
    }

    /**
     * The actual is greater or equal to the value using the given comparator
     *
     * @param actual     actual
     * @param value      value
     * @param comparator comparator
     * @param <T>        type
     * @return expression that the actual is greater or equal to the value using the given comparator
     */
    public static <T> ValidatorExpression greaterOrEqualTo( T actual, T value, Comparator<T> comparator ) {
        return new ValidatorExpression( comparator.compare( actual, value ) >= 0, "Not greater or equal to: " + actual + ", " + value );
    }

    /**
     * The actual is equal to the value using the given comparator
     *
     * @param actual     actual
     * @param value      value
     * @param comparator comparator
     * @param <T>        type
     * @return expression that the actual is equal to the value using the given comparator
     */
    public static <T> ValidatorExpression equalTo( T actual, T value, Comparator<T> comparator ) {
        return new ValidatorExpression( comparator.compare( actual, value ) == 0, "Not equal to: " + actual + ", " + value );
    }

    /**
     * The actual is smaller than the value using the given comparator
     *
     * @param actual     actual
     * @param value      value
     * @param comparator comparator
     * @param <T>        type
     * @return expression that the actual is smaller than the value using the given comparator
     */
    public static <T> ValidatorExpression smallerThan( T actual, T value, Comparator<T> comparator ) {
        return new ValidatorExpression( comparator.compare( actual, value ) == -1, "Not smaller than: " + actual + ", " + value );
    }

    /**
     * The actual is smaller or equal to the value using the given comparator
     *
     * @param actual     actual
     * @param value      value
     * @param comparator comparator
     * @param <T>        type
     * @return expression that the actual is smaller or equal to the value using the given comparator
     */
    public static <T> ValidatorExpression smallerOrEqualTo( T actual, T value, Comparator<T> comparator ) {
        return new ValidatorExpression( comparator.compare( actual, value ) <= 0, "Not smaller or equal to: " + actual + ", " + value );
    }

    /**
     * The actual is greater than the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is greater than the value
     */
    public static ValidatorExpression greaterThan( long actual, long value ) {
        return new ValidatorExpression( actual > value, "Not greater than: " + actual + ", " + value );
    }

    /**
     * The actual is greater or equal to the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is greater or equal to the value
     */
    public static ValidatorExpression greaterOrEqualTo( long actual, long value ) {
        return new ValidatorExpression( actual >= value, "Not greater or equal to: " + actual + ", " + value );
    }

    /**
     * The actual is equal to the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is equal to the value
     */
    public static ValidatorExpression equalTo( long actual, long value ) {
        return new ValidatorExpression( actual == value, "Not equal to: " + actual + ", " + value );
    }

    /**
     * The actual is smaller than the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is smaller than the value
     */
    public static ValidatorExpression smallerThan( long actual, long value ) {
        return new ValidatorExpression( actual < value, "Not smaller than: " + actual + ", " + value );
    }

    /**
     * The actual is smaller or equal to the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is smaller or equal to the value
     */
    public static ValidatorExpression smallerOrEqualTo( long actual, long value ) {
        return new ValidatorExpression( actual <= value, "Not smaller or equal to: " + actual + ", " + value );
    }

    /**
     * The actual is greater than the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is greater than the value
     */
    public static ValidatorExpression greaterThan( double actual, double value ) {
        return new ValidatorExpression( actual > value, "Not greater than: " + actual + ", " + value );
    }

    /**
     * The actual is greater or equal to the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is greater or equal to the value
     */
    public static ValidatorExpression greaterOrEqualTo( double actual, double value ) {
        return new ValidatorExpression( actual >= value, "Not greater or equal to: " + actual + ", " + value );
    }

    /**
     * The actual is equal to the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is equal to the value
     */
    public static ValidatorExpression equalTo( double actual, double value ) {
        return new ValidatorExpression( actual == value, "Not equal to: " + actual + ", " + value );
    }

    /**
     * The actual is smaller than the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is smaller than the value
     */
    public static ValidatorExpression smallerThan( double actual, double value ) {
        return new ValidatorExpression( actual < value, "Not smaller than: " + actual + ", " + value );
    }

    /**
     * The actual is smaller or equal to the value
     *
     * @param actual actual
     * @param value  value
     * @return expression that the actual is smaller or equal to the value
     */
    public static ValidatorExpression smallerOrEqualTo( double actual, double value ) {
        return new ValidatorExpression( actual <= value, "Not smaller or equal to: " + actual + ", " + value );
    }

    /**
     * Returns first valid expression in order to start a more complex expression
     *
     * @return valid expression
     */
    public static ValidatorExpression valid() {
        return new ValidatorExpression( true, null );
    }

    /**
     * Returns first invalid expression in order to start a more complex expression
     *
     * @return invalid expression
     */
    public static ValidatorExpression invalid() {
        return invalid( "Invalid" );
    }

    /**
     * Returns first invalid expression in order to start a more complex expression (using the given invalid message)
     *
     * @param message invalid message
     * @return invalid expression
     */
    public static ValidatorExpression invalid( String message ) {
        return new ValidatorExpression( false, message );
    }

    /**
     * Class representing a validator expression
     */
    public static class ValidatorExpression {

        private final List<ValidatorExpression> andValidatorExpressions = new ArrayList<>();
        private final List<ValidatorExpression> orValiditors = new ArrayList<>();
        private final Map<ValidatorExpression, String> parameterNameMap = new HashMap<>();
        private final String errorMessage;

        /**
         * Constructor
         *
         * @param result       result of this expression
         * @param errorMessage error message, should the expression fail
         */
        public ValidatorExpression( boolean result, String errorMessage ) {
            if ( result ) {
                this.errorMessage = null;
            } else {
                this.errorMessage = errorMessage;
            }
        }

        /**
         * Returns AND of this and the other expression
         *
         * @param validatorExpression the other expression
         * @return AND of this and the other expression
         */
        public ValidatorExpression and( ValidatorExpression validatorExpression ) {
            if ( !orValiditors.isEmpty() ) {
                throw new UnsupportedOperationException( "Do not combine 'and' and 'or' on the same level." );
            }
            andValidatorExpressions.add( validatorExpression );
            return this;
        }

        /**
         * Returns AND of this and the other expression
         *
         * @param parameterName       parameter to be displayed should the validation fail
         * @param validatorExpression the other expression
         * @return AND of this and the other expression
         */
        public ValidatorExpression and( String parameterName, ValidatorExpression validatorExpression ) {
            parameterNameMap.put( validatorExpression, parameterName );
            return and( validatorExpression );
        }

        /**
         * Returns OR of this and the other expression
         *
         * @param validatorExpression the other expression
         * @return OR of this and the other expression
         */
        public ValidatorExpression or( ValidatorExpression validatorExpression ) {
            if ( !andValidatorExpressions.isEmpty() ) {
                throw new UnsupportedOperationException( "Do not combine 'and' and 'or' on the same level." );
            }
            orValiditors.add( validatorExpression );
            return this;
        }

        /**
         * Returns AND of this and the other expression
         *
         * @param parameterName       parameter to be displayed should the validation fail
         * @param validatorExpression the other expression
         * @return AND of this and the other expression
         */
        public ValidatorExpression or( String parameterName, ValidatorExpression validatorExpression ) {
            parameterNameMap.put( validatorExpression, parameterName );
            return or( validatorExpression );
        }

        protected List<ValidatorExpression> getAndValidatorExpressions() {
            return andValidatorExpressions;
        }

        protected List<ValidatorExpression> getOrValidators() {
            return orValiditors;
        }

        /**
         * Validates the expression (recursively including its subexpressions) and returns error message or null if no error is present and the expression is valid
         *
         * @return error message or null if the expression is valid
         */
        public String validate() {
            String message = getErrorMessage();
            if ( !andValidatorExpressions.isEmpty() ) {
                for ( ValidatorExpression andValidatorExpression : andValidatorExpressions ) {
                    if ( andValidatorExpression.validate() != null ) {
                        String parameterName = parameterNameMap.get( andValidatorExpression );
                        return ( parameterName == null ? "" : parameterName + " - " ) + andValidatorExpression.validate();
                    }
                }
            } else if ( !orValiditors.isEmpty() ) {
                for ( ValidatorExpression orValiditor : orValiditors ) {
                    if ( orValiditor.validate() == null ) {
                        return null;
                    }
                }
            }
            return message;
        }

        /**
         * Returns the error message
         *
         * @return the error message
         */
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
            final ValidatorExpression other = (ValidatorExpression) obj;
            if ( !Objects.equals( this.errorMessage, other.errorMessage ) ) {
                return false;
            }
            if ( !Objects.equals( this.andValidatorExpressions, other.andValidatorExpressions ) ) {
                return false;
            }
            if ( !Objects.equals( this.orValiditors, other.orValiditors ) ) {
                return false;
            }
            return Objects.equals( this.parameterNameMap, other.parameterNameMap );
        }

    }
}

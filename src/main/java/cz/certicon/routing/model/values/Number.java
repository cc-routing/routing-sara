/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.values;

/**
 * Interface representing a number. Implements {@link Comparable}.
 *
 * @param <T> number type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface Number<T extends Number> extends Comparable<Number<T>> {

    /**
     * Returns whether this number is greater than the other
     *
     * @param other the other number
     * @return true if this number is greater than the other, false otherwise
     */
    boolean isGreaterThan( T other );

    /**
     * Returns whether this number is greater or equal to the other
     *
     * @param other the other number
     * @return true if this number is greater or equal to the other, false otherwise
     */
    boolean isGreaterOrEqualTo( T other );

    /**
     * Returns whether this number is lower than the other
     *
     * @param other the other number
     * @return true if this number is lower than the other, false otherwise
     */
    boolean isLowerThan( T other );

    /**
     * Returns whether this number is lower or equal to the other
     *
     * @param other the other number
     * @return true if this number is lower or equal to the other, false otherwise
     */
    boolean isLowerOrEqualTo( T other );

    /**
     * Returns whether this number is equal t the other
     *
     * @param other the other number
     * @return true if this number is equal to the other, false otherwise
     */
    boolean isEqualTo( T other );

    /**
     * Returns whether this number is positive
     *
     * @return true if this number is positive, false otherwise
     */
    boolean isPositive();

    /**
     * Returns whether this number is negative
     *
     * @return true if this number is negative, false otherwise
     */
    boolean isNegative();

    /**
     * Returns absolute version of this number
     *
     * @return absolute version of this number
     */
    T absolute();

    /**
     * Returns result of the addition of this and the other number
     *
     * @param other the other number
     * @return result of the addition of this and the other number
     */
    T add( T other );

    /**
     * Returns result of the subtraction of the other number from this number
     *
     * @param other the other number
     * @return result of the subtraction of the other number from this number
     */
    T subtract( T other );

    /**
     * Returns result of the division of this number by the other number
     *
     * @param other the other number
     * @return result of the division of this number by the other number
     */
    T divide( T other );

    /**
     * Returns result of the multiplication of this and the other number
     *
     * @param other the other number
     * @return result of the multiplication of this and the other number
     */
    T multiply( T other );

    /**
     * Returns this number
     *
     * @return this number
     */
    T identity();
}

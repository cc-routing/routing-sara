/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.values;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 * @param <T> number type
 */
public interface Number<T extends Number> {

    boolean isPositive();

    boolean isNegative();
    
    T absolute();

    T add( T other );

    T substract( T other );

    T divide( T other );

    T multiply( T other );
}

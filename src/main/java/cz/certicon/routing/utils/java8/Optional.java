/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.java8;

import java.util.NoSuchElementException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 * @param <T>
 */
public class Optional<T> {

    private final T value;

    private Optional( T value ) {
        this.value = value;
    }

    public static <T> Optional<T> empty() {
        return new Optional<>( null );
    }

    public static <T> Optional<T> of( T value ) {
        return new Optional<>( value );
    }

    public static <T> Optional<T> ofNullable( T value ) {
        if ( value == null ) {
            return empty();
        } else {
            return of( value );
        }
    }

    public T get() {
        if ( value == null ) {
            throw new NoSuchElementException( "The value is not present." );
        }
        return value;
    }

    public void ifPresent( Consumer<? super T> consumer ) {
        if ( isPresent() ) {
            consumer.accept( value );
        }
    }

    public boolean isPresent() {
        return value != null;
    }

    public T orElse( T other ) {
        if ( isPresent() ) {
            return value;
        } else {
            return other;
        }
    }

    public T orElseGet( Supplier<? extends T> other ) {
        return orElse( other.get() );
    }

    public <X extends Throwable> T orElseThrow( Supplier<? extends X> exceptionSupplier ) throws X {
        if ( isPresent() ) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

}

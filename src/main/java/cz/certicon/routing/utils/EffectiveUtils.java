/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import java.util.Arrays;

/**
 * Utility class offering operations which require higher efficiency (execution
 * optimizations)
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class EffectiveUtils {

    /**
     * Fill the array with the given value (all the elements of the array will
     * be equal to the given value after this operation is performed).
     *
     * @param array array to be edited
     * @param value value to be spread all over the array
     */
    public static void fillArray( int[] array, int value ) {
        int len = array.length;
        if ( len > 0 ) {
            array[0] = value;
        }
        for ( int i = 1; i < len; i += i ) {
            System.arraycopy( array, 0, array, i, ( ( len - i ) < i ) ? ( len - i ) : i );
        }
    }

    /**
     * Fill the array with the given value (all the elements of the array will
     * be equal to the given value after this operation is performed).
     *
     * @param array array to be edited
     * @param value value to be spread all over the array
     */
    public static void fillArray( double[] array, double value ) {
        int len = array.length;
        if ( len > 0 ) {
            array[0] = value;
        }
        for ( int i = 1; i < len; i += i ) {
            System.arraycopy( array, 0, array, i, ( ( len - i ) < i ) ? ( len - i ) : i );
        }
    }

    /**
     * Fill the array with the given value (all the elements of the array will
     * be equal to the given value after this operation is performed).
     *
     * @param array array to be edited
     * @param value value to be spread all over the array
     */
    public static void fillArray( float[] array, float value ) {
        int len = array.length;
        if ( len > 0 ) {
            array[0] = value;
        }
        for ( int i = 1; i < len; i += i ) {
            System.arraycopy( array, 0, array, i, ( ( len - i ) < i ) ? ( len - i ) : i );
        }
    }

    /**
     * Fill the array with the given value (all the elements of the array will
     * be equal to the given value after this operation is performed).
     *
     * @param array array to be edited
     * @param value value to be spread all over the array
     */
    public static void fillArray( boolean[] array, boolean value ) {
        int len = array.length;
        if ( len > 0 ) {
            array[0] = value;
        }
        for ( int i = 1; i < len; i += i ) {
            System.arraycopy( array, 0, array, i, ( ( len - i ) < i ) ? ( len - i ) : i );
        }
    }

    /**
     * Fill the array with the given value (all the elements of the array will
     * be equal to the given value after this operation is performed).
     *
     * @param <T>   array and value type
     * @param array array to be edited
     * @param value value to be spread all over the array
     */
    public static <T> void fillArray( T[] array, T value ) {
        int len = array.length;
        if ( len > 0 ) {
            array[0] = value;
        }
        for ( int i = 1; i < len; i += i ) {
            System.arraycopy( array, 0, array, i, ( ( len - i ) < i ) ? ( len - i ) : i );
        }
    }

    /**
     * Copy values from source array to target array
     *
     * @param source source array
     * @param target target (destination) array
     */
    public static void copyArray( int[] source, int[] target ) {
        System.arraycopy( source, 0, target, 0, Math.min( source.length, target.length ) );
    }

    /**
     * Copy values from source array to target array
     *
     * @param source source array
     * @param target target (destination) array
     */
    public static void copyArray( long[] source, long[] target ) {
        System.arraycopy( source, 0, target, 0, Math.min( source.length, target.length ) );
    }

    /**
     * Copy values from source array to target array
     *
     * @param source source array
     * @param target target (destination) array
     */
    public static void copyArray( double[] source, double[] target ) {
        System.arraycopy( source, 0, target, 0, Math.min( source.length, target.length ) );
    }

    /**
     * Copy values from source array to target array
     *
     * @param source source array
     * @param target target (destination) array
     */
    public static void copyArray( boolean[] source, boolean[] target ) {
        System.arraycopy( source, 0, target, 0, Math.min( source.length, target.length ) );
    }

    /**
     * Copy values from source array to target array
     *
     * @param source source array
     * @param target target (destination) array
     */
    public static void copyArray( float[] source, float[] target ) {
        System.arraycopy( source, 0, target, 0, Math.min( source.length, target.length ) );
    }


    /**
     * Copy values from source array to target array
     *
     * @param source source array
     * @param target target (destination) array
     */
    public static void copyArray( float[][] source, float[][] target ) {
        for ( int i = 0; i < source.length; i++ ) {
            copyArray( source[i], target[i] );
        }
    }


    /**
     * Enlarges the orig array by the sizeBy
     *
     * @param orig   original array
     * @param sizeBy size by which the array should be enlarged
     * @return new array containing values from orig and having size of orig.length + sizeBy
     */
    public static long[] enlarge( long[] orig, int sizeBy ) {
        long[] newArray = new long[orig.length + sizeBy];
        copyArray( orig, newArray );
        return newArray;
    }

    /**
     * Enlarges the orig array by the sizeBy
     *
     * @param orig   original array
     * @param sizeBy size by which the array should be enlarged
     * @return new array containing values from orig and having size of orig.length + sizeBy
     */
    public static int[] enlarge( int[] orig, int sizeBy ) {
        int[] newArray = new int[orig.length + sizeBy];
        copyArray( orig, newArray );
        return newArray;
    }

    /**
     * Enlarges the orig array by the sizeBy
     *
     * @param orig   original array
     * @param sizeBy size by which the array should be enlarged
     * @return new array containing values from orig and having size of orig.length + sizeBy
     */
    public static boolean[] enlarge( boolean[] orig, int sizeBy ) {
        boolean[] newArray = new boolean[orig.length + sizeBy];
        copyArray( orig, newArray );
        return newArray;
    }

    /**
     * Enlarges the orig array by the sizeBy
     *
     * @param orig   original array
     * @param sizeBy size by which the array should be enlarged
     * @return new array containing values from orig and having size of orig.length + sizeBy
     */
    public static float[] enlarge( float[] orig, int sizeBy ) {
        float[] newArray = new float[orig.length + sizeBy];
        copyArray( orig, newArray );
        return newArray;
    }

    /**
     * Enlarges the orig array by the sizeBy
     *
     * @param orig   original array
     * @param sizeBy size by which the array should be enlarged
     * @return new array containing values from orig and having size of orig.length + sizeBy
     */
    public static double[] enlarge( double[] orig, int sizeBy ) {
        double[] newArray = new double[orig.length + sizeBy];
        copyArray( orig, newArray );
        return newArray;
    }

    /**
     * Enlarges the orig array by the sizeBy
     *
     * @param orig   original array
     * @param sizeBy size by which the array should be enlarged
     * @return new array containing values from orig and having size of orig.length + sizeBy
     */
    public static int[][] enlarge( int[][] orig, int sizeBy ) {
        int[][] newArray = Arrays.copyOf( orig, orig.length + sizeBy );
        return newArray;
    }

    /**
     * Enlarges the orig array by the sizeBy
     *
     * @param orig   original array
     * @param sizeBy size by which the array should be enlarged
     * @return new array containing values from orig and having size of orig.length + sizeBy
     */
    public static float[][][] enlarge( float[][][] orig, int sizeBy ) {
        float[][][] newArray = Arrays.copyOf( orig, orig.length + sizeBy );
        return newArray;
    }
}

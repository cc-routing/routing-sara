/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import java.util.Random;

/**
 * Utilities for random. Call {@link #setSeed(long)} to control all the randomness in this library
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class RandomUtils {

    private static long seed = -1;

    /**
     * Return new {@link Random}
     *
     * @return new {@link Random}
     */
    public static Random createRandom() {
        if ( seed > 0 ) {
            return new Random( seed );
        }
        return new Random();
    }

    /**
     * Sets seed. Affects all the randoms created by {@link #createRandom()} after this method is called
     *
     * @param seed new seed
     */
    public static void setSeed( long seed ) {
        System.err.println( "WATCH OUT! SEED IS SET! RANDOMNESS IS COMPROMISED!" );
        RandomUtils.seed = seed;
    }
}

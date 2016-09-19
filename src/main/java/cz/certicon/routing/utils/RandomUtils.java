/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import java.util.Random;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class RandomUtils {

    private static long seed = -1;

    public static Random createRandom() {
        if ( seed > 0 ) {
            return new Random( seed );
        }
        return new Random();
    }

    public static void setSeed( long seed ) {
        System.err.println( "WATCH OUT! SEED IS SET! RANDOMNESS IS COMPROMISED!" );
        RandomUtils.seed = seed;
    }
}

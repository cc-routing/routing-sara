/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class Level {

    int level;

    private Level( int level ) {
        this.level = level;
    }

    private static final TIntObjectMap<Level> LEVEL_MAP;

    static {
        LEVEL_MAP = new TIntObjectHashMap<>();
        LEVEL_MAP.put( 0, new Level( 0 ) );
    }

    public static Level newInstance( int level ) { // TODO sycnhronize properly, see singleton sycnhronization
        if ( LEVEL_MAP.containsKey( level ) ) {
            return LEVEL_MAP.get( level );
        } else {
            Level instance = new Level( level );
            LEVEL_MAP.put( level, instance );
            return instance;
        }
    }
}

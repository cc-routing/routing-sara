/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class NodeInfo {

    @Getter( AccessLevel.NONE )
    Set<Level> relevantLevels;
    @Getter( AccessLevel.NONE )
    Map<Level, Cell> cellsAtLevels;

    public boolean isBorder( Level level ) {
        return relevantLevels.contains( level );
    }

    public Cell getCell( Level level ) {
        if ( !cellsAtLevels.containsKey( level ) ) {
            throw new IllegalArgumentException( "Unknown level: " + level );
        }
        return cellsAtLevels.get( level );
    }
}

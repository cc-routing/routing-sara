/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.values.Distance;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class TurnTable {

    @Getter( AccessLevel.NONE )
    Distance[][] turnCosts;

    public Distance getCost( int from, int to ) {
        return turnCosts[from][to];
    }
}

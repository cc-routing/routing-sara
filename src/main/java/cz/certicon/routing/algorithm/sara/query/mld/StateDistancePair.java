/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.model.graph.State;
import lombok.Value;

/**
 * Structure for storing state{node,edge} together with the criterion
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 */
@Value
public class StateDistancePair {

    State state;
    double distance;
}

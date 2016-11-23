/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.query.mld;

import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.State;
import lombok.Value;

/**
 *
 * @author Roman Vaclavik {@literal <vaclavik@merica.cz>}
 */
@Value
public class EdgeDistancePair {

    State state;
    double distance;
}

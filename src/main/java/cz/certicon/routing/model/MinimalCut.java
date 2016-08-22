/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Edge;
import java.util.Collection;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class MinimalCut {
    Collection<Edge> cutEdges;
    double cutSize;
}

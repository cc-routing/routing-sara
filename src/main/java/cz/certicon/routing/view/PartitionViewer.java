/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import java.util.Collection;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface PartitionViewer {

    public <E extends Edge> void addCutEdges( Collection<E> cutEdges );

    public <N extends Node> void addBorderNodes( Collection<N> borderNodes );

    public <N extends Node> void addNodeCluster( Collection<N> partition );

    public void display();
}

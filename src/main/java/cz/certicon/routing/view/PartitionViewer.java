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
    
    void setNumberOfColors( int numberOfColors );

    <E extends Edge> void addCutEdges( Collection<E> cutEdges );

    <N extends Node> void addBorderNodes( Collection<N> borderNodes );

    <N extends Node> void addNodeCluster( Collection<N> partition );

    void display();
}

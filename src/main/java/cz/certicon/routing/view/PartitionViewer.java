/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.Partition;
import java.util.Collection;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface PartitionViewer {

    public void addPartition( Graph graph, Collection<SimpleEdge> cutEdges );

    public void addPartitionNodes( Graph graph, Collection<SimpleNode> borderNodes );

    public void addPartition( Graph graph, Partition partition );

    public void display();
}

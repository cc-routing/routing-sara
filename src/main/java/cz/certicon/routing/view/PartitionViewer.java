/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import java.util.Collection;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface PartitionViewer {

    public void addCutEdges( Graph graph, Collection<Edge> cutEdges );

    public void addBorderNodes( Graph graph, Collection<Node> borderNodes );

    public void addNodeCluster( Graph graph, Collection<Node> partition );

    public void display();
}

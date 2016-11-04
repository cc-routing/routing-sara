/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.utils.collections.CollectionUtils;
import cz.certicon.routing.view.GraphStreamPresenter;
import cz.certicon.routing.view.JxPartitionViewer;
import cz.certicon.routing.view.PartitionViewer;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class DisplayUtils {

    public static void display( SaraGraph graph ) {
        int layers = 1;
        Cell cell = graph.getNodes().next().getParent();
        while ( cell != null ) {
//            System.out.println( "- Layer #" + layers + ", " + "parenting: 1 to " + layers  );
            PartitionViewer viewer = new JxPartitionViewer();
            Map<Cell, List<Node>> cellMap = new HashMap<>();
            for ( SaraNode node : graph.getNodes() ) {
                Cell parent = node.getParent();
                for ( int i = 1; i < layers; i++ ) {
                    parent = parent.getParent();
                }
                CollectionUtils.getList( cellMap, parent ).add( node );
            }
//            System.out.println( "- Adding #" + cellMap.size() + " clusters..." );
            viewer.setNumberOfColors( cellMap.size() );
            for ( List<Node> value : cellMap.values() ) {
                viewer.addNodeCluster( value );
            }
//            System.out.println( "- Displaying" );
            viewer.display();
            layers++;
            cell = cell.getParent();
        }
    }

    public static <N extends Node, E extends Edge> void display( Graph<N, E> graph, Collection<Collection<N>> nodeGroups ) {
        UndirectedGraph g = new UndirectedGraph();
        for ( Collection<N> nodeGroup : nodeGroups ) {
            for ( N n : nodeGroup ) {
                SimpleNode node = g.createNode( n.getId() );
            }
        }
        for ( E e : graph.getEdges() ) {
            Node s = e.getSource();
            Node t = e.getTarget();
            if ( !g.containsEdge( e.getId() ) && g.containsNode( s.getId() ) && g.containsNode( t.getId() ) ) {
                SimpleNode source = g.getNodeById( s.getId() );
                SimpleNode target = g.getNodeById( t.getId() );
                SimpleEdge edge = g.createEdge( e.getId(), false, source, target, 0, 0, new Pair<>( Metric.SIZE, e.getLength( Metric.SIZE ) ) );
                source.addEdge( edge );
                target.addEdge( edge );
            }
        }
        GraphStreamPresenter presenter = new GraphStreamPresenter();
        presenter.setGraph( g );
        int cnt = 0;
        ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( nodeGroups.size() );
        for ( Collection<N> nodeGroup : nodeGroups ) {
            Color color = colorSupplier.nextColor();
            System.out.println( "color #" + ++cnt + ": " + color );
            for ( N n : nodeGroup ) {
                presenter.setNodeColor( n.getId(), color );
            }
        }
        presenter.display();
    }

    public static <N extends Node, E extends Edge> void display( ContractGraph graph ) {
        PartitionViewer viewer = new JxPartitionViewer();
        for ( ContractNode node : graph.getNodes() ) {
            viewer.addNodeCluster( node.getNodes() );
        }
        viewer.display();
    }

}

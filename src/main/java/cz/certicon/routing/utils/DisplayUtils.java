/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.algorithm.sara.preprocessing.filtering.ElementContainer;
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
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.CollectionUtils;
import cz.certicon.routing.view.GraphStreamPresenter;
import cz.certicon.routing.view.JxPartitionViewer;
import cz.certicon.routing.view.PartitionViewer;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.awt.Color;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class DisplayUtils {

    public static void display( SaraGraph graph ) {
        PartitionViewer viewer = new JxPartitionViewer();
        Map<Cell, List<Node>> cellMap = new HashMap<>();
        for ( SaraNode node : graph.getNodes() ) {
            Cell parent = node.getParent();
            CollectionUtils.getList( cellMap, parent ).add( node );
        }
        for ( List<Node> value : cellMap.values() ) {
            viewer.addNodeCluster( value );
        }
        viewer.display();
    }

    public static <N extends Node, E extends Edge> void display( Graph<N, E> graph, Collection<ElementContainer<N>> nodeGroups ) {
        UndirectedGraph g = new UndirectedGraph();
        for ( ElementContainer<N> nodeGroup : nodeGroups ) {
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
        for ( ElementContainer<N> nodeGroup : nodeGroups ) {
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

//    public static void displayConnectedPartitions( SaraGraph graph ) {
//        GraphStreamPresenter presenter = new GraphStreamPresenter();
//        Graph assembled = graph.toPartitionsOnlyGraph();
//        presenter.setGraph( assembled );
//        ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( assembled.getNodesCount() );
//        Iterator<SimpleNode> nodes = assembled.getNodes();
//        while ( nodes.hasNext() ) {
//            SimpleNode node = nodes.next();
//            Color c = colorSupplier.nextColor();
//            presenter.setNodeColor( node.getId(), c );
//        }
//        presenter.display();
//    }
//    public static void display( PartitionGraph graph ) {
//        ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( graph.getPartitionCount() );
//        GraphStreamPresenter presenter = new GraphStreamPresenter();
//        presenter.setGraph( graph );
//        Iterator<Partition> partitions = graph.getPartitions();
//        while ( partitions.hasNext() ) {
//            Partition partition = partitions.next();
//            Color c = colorSupplier.nextColor();
//            Iterator<SimpleNode> nodes = partition.getNodes();
//            while ( nodes.hasNext() ) {
//                SimpleNode node = nodes.next();
//                presenter.setNodeColor( node.getId(), c );
//            }
//        }
//        presenter.display();
//    }
//    public static void displayAll( PartitionGraph graph ) {
//        Map<Long, Color> colorMap = new HashMap<>();
//        GraphStreamPresenter presenter = new GraphStreamPresenter();
//        Graph assembled = graph.toPartitionsOnlyGraph();
//        presenter.setGraph( assembled );
//        ColorUtils.ColorSupplier colorSupplier = ColorUtils.createColorSupplier( assembled.getNodesCount() );
//        Iterator<SimpleNode> nodes = assembled.getNodes();
//        while ( nodes.hasNext() ) {
//            SimpleNode node = nodes.next();
//            Color c = colorSupplier.nextColor();
//            colorMap.put( node.getId(), c );
//            presenter.setNodeColor( node.getId(), c );
//        }
//        presenter.display();
//        presenter = new GraphStreamPresenter();
//        presenter.setGraph( graph );
//        Iterator<Partition> partitions = graph.getPartitions();
//        while ( partitions.hasNext() ) {
//            Partition partition = partitions.next();
//            Color c = colorMap.get( partition.getId() );
//            nodes = partition.getNodes();
//            while ( nodes.hasNext() ) {
//                SimpleNode node = nodes.next();
//                presenter.setNodeColor( node.getId(), c );
//            }
//        }
//        presenter.display();
//    }
//    public static void displayMap( PartitionGraph graph ) {
//        PartitionViewer viewer = new JxPartitionViewer();
////        System.out.println( "Obtaining cut-edges" );
////        Collection<Collection<Edge>> cutEdgesCollection = GraphUtils.getCutEdges( graph );
////        System.out.println( "Done obtaining cut-edges" );
////        int counter = 0;
////        for ( Collection<Edge> collection : cutEdgesCollection ) {
////            viewer.addPartition( graph, collection );
////            counter += collection.size();
////            if(counter > 1000){
////                break;
////            }
////        }
////        int counter = 0;
////        for ( Partition partition : graph.getPartitions() ) {
////            viewer.addCutEdges( graph, partition );
////            counter += 1;
////            if ( counter > 20 ) {
////                break;
////            }
//        }
////        Collection<Collection<Node>> borderNodesCollection = GraphUtils.getBorderNodes( graph );
////        int counter = 0;
////        for ( Collection<Node> collection : borderNodesCollection ) {
////            viewer.addPartitionNodes( graph, collection );
////            counter += 1;
////            if ( counter > 1000 ) {
////                break;
////            }
////        }
//        viewer.display();
//    }
}

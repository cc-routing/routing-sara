/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.view.GraphStreamPresenter;
import cz.certicon.routing.view.JxPartitionViewer;
import cz.certicon.routing.view.PartitionViewer;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class DisplayUtils {

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

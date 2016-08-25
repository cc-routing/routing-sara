/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

/**
 * An implementation of {@link GraphPresenter} using a GraphStream library.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GraphStreamPresenter implements GraphPresenter {

    private static final int MOVE = 15;
    private static final int NUM_COLORS = 20;
//    private final List<Color> colorList;
    private boolean displayNodes = true;
    private Graph displayGraph;
    private Map<Long, Node> nodeMap;
    private Map<Long, Edge> edgeMap;
//    private int colorCounter = 0;

    public GraphStreamPresenter() {
//        this.colorList = new ArrayList<>();
//        float interval = 360 / ( NUM_COLORS );
//        for ( float x = 0; x < 360; x += interval ) {
//            Color c = Color.getHSBColor( x / 360, 1, 1 );
//            colorList.add( c );
//        }
    }

    @Override
    public void displayGraph( cz.certicon.routing.model.graph.Graph graph ) {
        nodeMap = new HashMap<>();
        displayGraph = new org.graphstream.graph.implementations.MultiGraph( "graph-id" );
//        displayGraph.addAttribute( "ui.stylesheet", "edge {"
//                + "shape: line;"
//                + "fill-color: " + toCssRgb( Color.yellow ) + ";"
//                + "arrow-shape: arrow;"
//                + "arrow-size: 8px, 4px;"
//                + "}" );
        Iterator<cz.certicon.routing.model.graph.Node> nodesIterator;
        nodesIterator = graph.getNodes();
        while ( nodesIterator.hasNext() ) {
            cz.certicon.routing.model.graph.Node node = nodesIterator.next();
//            System.out.println( "adding node #" + node.getId() );
            Node n = displayGraph.addNode( "" + node.getId() );
            nodeMap.put( node.getId(), n );
            if ( displayNodes ) {
                n.setAttribute( "ui.label", "" + node.getId() );
            }
        }
        Iterator<cz.certicon.routing.model.graph.Edge> edgeIterator = graph.getEdges();
        while ( edgeIterator.hasNext() ) {
            cz.certicon.routing.model.graph.Edge edge = edgeIterator.next();
//            System.out.println( "adding edge #" + edge.getId() );
            Edge addEdge = displayGraph.addEdge( edge.getId() + "", edge.getSource().getId() + "", edge.getTarget().getId() + "", true );
        }
        Viewer viewer = displayGraph.display( true );
        View view = viewer.getDefaultView();

        ZoomListener zoomListener = new ZoomListener( view.getCamera() );
        viewer.getDefaultView().addMouseWheelListener( zoomListener );
        viewer.getDefaultView().addMouseMotionListener( zoomListener );
    }

    public void setGraph( cz.certicon.routing.model.graph.Graph graph ) {
        nodeMap = new HashMap<>();
        displayGraph = new org.graphstream.graph.implementations.MultiGraph( "graph-id" );
//        displayGraph.addAttribute( "ui.stylesheet", "edge {"
//                //+ "shape: line;"
//                //+ "fill-color: #222;"
//                + "arrow-shape: arrow;"
//                + "arrow-size: 8px, 4px;"
//                + "}" );
        Iterator<cz.certicon.routing.model.graph.Node> nodesIterator;
        nodesIterator = graph.getNodes();
        while ( nodesIterator.hasNext() ) {
            cz.certicon.routing.model.graph.Node node = nodesIterator.next();
//            System.out.println( "adding node #" + node.getId() );
            Node n = displayGraph.addNode( "" + node.getId() );
            nodeMap.put( node.getId(), n );
            if ( displayNodes ) {
                n.setAttribute( "ui.label", "" + node.getId() );
            }
        }
        Iterator<cz.certicon.routing.model.graph.Edge> edgeIterator = graph.getEdges();
        while ( edgeIterator.hasNext() ) {
            cz.certicon.routing.model.graph.Edge edge = edgeIterator.next();
//            System.out.println( "adding edge #" + edge.getId() );
            Edge addEdge = displayGraph.addEdge( edge.getId() + "", edge.getSource().getId() + "", edge.getTarget().getId() + "", true );
        }
    }

    public void display() {
        Viewer viewer = displayGraph.display( true );
        View view = viewer.getDefaultView();

        ZoomListener zoomListener = new ZoomListener( view.getCamera() );
        viewer.getDefaultView().addMouseWheelListener( zoomListener );
        viewer.getDefaultView().addMouseMotionListener( zoomListener );
    }

    public void setNodeColor( long id, Color color ) {
        String fillColor = "fill-color: " + toCssRgb( color ) + ";";
        if ( !nodeMap.containsKey( id ) ) {
            throw new IllegalArgumentException( "Could not find node: " + id );
        }
        nodeMap.get( id ).addAttribute( "ui.style", fillColor );
    }

    public void setEdgeColor( long id, Color color ) {
//        System.out.println( "setting edge: " + id + ", edge = " + edgeMap.get( id ) );
        String fillColor = "fill-color: " + toCssRgb( color ) + ";";
        if ( !edgeMap.containsKey( id ) ) {
            throw new IllegalArgumentException( "Could not find edge: " + id );
        }
        edgeMap.get( id ).addAttribute( "ui.style", fillColor );
        edgeMap.get( id ).addAttribute( "ui.color", color );
    }

    public void removeEdge( long id ) {
        System.out.println( "removed: " + displayGraph.removeEdge( id + "" ) );
    }

//    private Color nextColor() {
//        return colorList.get( colorCounter++ % colorList.size() );
//    }
    private String toCssRgb( Color color ) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }
}

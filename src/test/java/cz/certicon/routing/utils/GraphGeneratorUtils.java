/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GraphGeneratorUtils {

    private static final int GRID_MAX_SIZE = 100;
    private static final int GRID_EDGE_SIZE = 1;

    public static UndirectedGraph generateGridGraph( Map<Long, Node> nodeMap, Map<Long, Edge> edgeMap, Map<TurnTable, TurnTable> turnTables, int rows, int columns ) {
        if ( rows > GRID_MAX_SIZE || columns > GRID_MAX_SIZE ) {
            throw new IllegalArgumentException( "It's a quick test! Size over " + GRID_MAX_SIZE + " is just too much: rows = " + rows + ", columns = " + columns );
        }
        int multiplier = GRID_MAX_SIZE;
        while(rows < multiplier / 10 && columns < multiplier / 10){
            multiplier /= 10;
        }
        List<Node> nodes = new ArrayList<>();
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < columns; j++ ) {
                createNode( nodeMap, nodes, i * multiplier + j );
            }
        }
        List<Edge> edges = new ArrayList<>();
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < columns; j++ ) {
                Node target = nodeMap.get( (long) i * multiplier + j );
                if ( i > 0 ) {
                    Node source = nodeMap.get( (long) ( i - 1 ) * multiplier + j );
                    createEdge( edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, GRID_EDGE_SIZE );
                }
                if ( j > 0 ) {
                    Node source = nodeMap.get( (long) i * multiplier + ( j - 1 ) );
                    createEdge( edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, GRID_EDGE_SIZE );
                }
            }
        }
        for ( Node node : nodes ) {
            int size = node.getDegree();
            Distance[][] dtt = new Distance[size][size];
            for ( int i = 0; i < dtt.length; i++ ) {
                for ( int j = 0; j < dtt[i].length; j++ ) {
                    if ( i != j ) {
                        dtt[i][j] = Distance.newInstance( 0 );
                    } else {
                        dtt[i][j] = Distance.newInfinityInstance();
                    }
                }
            }
            TurnTable tt = new TurnTable( dtt );
            if ( !turnTables.containsKey( tt ) ) {
                turnTables.put( tt, tt );
            } else {
                tt = turnTables.get( tt );
            }
            node.setTurnTable( tt );
        }
        for ( Node node : nodes ) {
            node.lock();
        }
        UndirectedGraph g = UndirectedGraph.builder().nodes( nodes ).edges( edges ).build();
        return g;
    }

    public static UndirectedGraph createGraph( Map<Long, Node> nodeMap, Map<Long, Edge> edgeMap, Map<TurnTable, TurnTable> turnTables ) {
        List<Node> nodes = new ArrayList<>();
        Node a = createNode( nodeMap, nodes, 0 );
        Node b = createNode( nodeMap, nodes, 1 );
        Node c = createNode( nodeMap, nodes, 2 );
        Node d = createNode( nodeMap, nodes, 3 );
        Node e = createNode( nodeMap, nodes, 4 );
        Node f = createNode( nodeMap, nodes, 5 );
        List<Edge> edges = new ArrayList<>();
        Edge ab = createEdge( edgeMap, edges, 0, false, a, b, 120 );
        Edge ac = createEdge( edgeMap, edges, 1, false, a, c, 184 );
        Edge cd = createEdge( edgeMap, edges, 2, false, c, d, 94 );
        Edge db = createEdge( edgeMap, edges, 3, true, d, b, 159 );
        Edge be = createEdge( edgeMap, edges, 4, false, b, e, 36 );
        Edge df = createEdge( edgeMap, edges, 5, false, d, f, 152 );
        Edge ef = createEdge( edgeMap, edges, 6, true, e, f, 38 );
        for ( Node node : nodes ) {
            int size = node.getDegree();
            Distance[][] dtt = new Distance[size][size];
            for ( int i = 0; i < dtt.length; i++ ) {
                for ( int j = 0; j < dtt[i].length; j++ ) {
                    if ( i != j ) {
                        dtt[i][j] = Distance.newInstance( 0 );
                    } else {
                        dtt[i][j] = Distance.newInfinityInstance();
                    }
                }
            }
            if ( node.getId() == 2 ) {
                dtt[0][1] = Distance.newInfinityInstance();
            }

            TurnTable tt = new TurnTable( dtt );
            if ( !turnTables.containsKey( tt ) ) {
                turnTables.put( tt, tt );
            } else {
                tt = turnTables.get( tt );
            }
            node.setTurnTable( tt );
        }
        for ( Node node : nodes ) {
            node.lock();
        }
        UndirectedGraph g = UndirectedGraph.builder().nodes( nodes ).edges( edges ).build();
        return g;
    }

    private static Node createNode( Map<Long, Node> nodeMap, List<Node> nodes, long id ) {
//        Distance[][] tt = new Distance[]
        Node node = new Node( id );
        nodes.add( node );
        nodeMap.put( id, node );
        return node;
    }

    private static Edge createEdge( Map<Long, Edge> edgeMap, List<Edge> edges, long id, boolean oneway, Node source, Node target, double distance ) {
        Edge edge = new Edge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        source.addEdge( edge );
        target.addEdge( edge );
        edgeMap.put( id, edge );
        return edge;
    }

    private static Edge createEdge( Map<Long, Edge> edgeMap, List<Edge> edges, long id, boolean oneway, Node source, Node target, double distance, boolean addToNode ) {
        Edge edge = new Edge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        if ( addToNode ) {
            source.addEdge( edge );
            target.addEdge( edge );
        }
        edgeMap.put( id, edge );
        return edge;
    }
}

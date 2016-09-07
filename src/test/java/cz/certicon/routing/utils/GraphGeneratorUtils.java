/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
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

    public static UndirectedGraph generateGridGraph( Map<Long, SimpleNode> nodeMap, Map<Long, SimpleEdge> edgeMap, Map<TurnTable, TurnTable> turnTables, int rows, int columns ) {
        if ( rows > GRID_MAX_SIZE || columns > GRID_MAX_SIZE ) {
            throw new IllegalArgumentException( "It's a quick test! Size over " + GRID_MAX_SIZE + " is just too much: rows = " + rows + ", columns = " + columns );
        }
        int multiplier = GRID_MAX_SIZE;
        while(rows < multiplier / 10 && columns < multiplier / 10){
            multiplier /= 10;
        }
        List<SimpleNode> nodes = new ArrayList<>();
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < columns; j++ ) {
                createNode( nodeMap, nodes, i * multiplier + j );
            }
        }
        List<SimpleEdge> edges = new ArrayList<>();
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < columns; j++ ) {
                SimpleNode target = nodeMap.get( (long) i * multiplier + j );
                if ( i > 0 ) {
                    SimpleNode source = nodeMap.get( (long) ( i - 1 ) * multiplier + j );
                    createEdge( edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, GRID_EDGE_SIZE );
                }
                if ( j > 0 ) {
                    SimpleNode source = nodeMap.get( (long) i * multiplier + ( j - 1 ) );
                    createEdge( edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, GRID_EDGE_SIZE );
                }
            }
        }
        for ( SimpleNode node : nodes ) {
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
        for ( SimpleNode node : nodes ) {
            node.lock();
        }
        UndirectedGraph g = UndirectedGraph.builder().nodes( nodes ).edges( edges ).build();
        return g;
    }

    public static UndirectedGraph createGraph( Map<Long, SimpleNode> nodeMap, Map<Long, SimpleEdge> edgeMap, Map<TurnTable, TurnTable> turnTables ) {
        List<SimpleNode> nodes = new ArrayList<>();
        SimpleNode a = createNode( nodeMap, nodes, 0 );
        SimpleNode b = createNode( nodeMap, nodes, 1 );
        SimpleNode c = createNode( nodeMap, nodes, 2 );
        SimpleNode d = createNode( nodeMap, nodes, 3 );
        SimpleNode e = createNode( nodeMap, nodes, 4 );
        SimpleNode f = createNode( nodeMap, nodes, 5 );
        List<SimpleEdge> edges = new ArrayList<>();
        SimpleEdge ab = createEdge( edgeMap, edges, 0, false, a, b, 120 );
        SimpleEdge ac = createEdge( edgeMap, edges, 1, false, a, c, 184 );
        SimpleEdge cd = createEdge( edgeMap, edges, 2, false, c, d, 94 );
        SimpleEdge db = createEdge( edgeMap, edges, 3, true, d, b, 159 );
        SimpleEdge be = createEdge( edgeMap, edges, 4, false, b, e, 36 );
        SimpleEdge df = createEdge( edgeMap, edges, 5, false, d, f, 152 );
        SimpleEdge ef = createEdge( edgeMap, edges, 6, true, e, f, 38 );
        for ( SimpleNode node : nodes ) {
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
        for ( SimpleNode node : nodes ) {
            node.lock();
        }
        UndirectedGraph g = UndirectedGraph.builder().nodes( nodes ).edges( edges ).build();
        return g;
    }

    private static SimpleNode createNode( Map<Long, SimpleNode> nodeMap, List<SimpleNode> nodes, long id ) {
//        Distance[][] tt = new Distance[]
        SimpleNode node = new SimpleNode( id );
        nodes.add( node );
        nodeMap.put( id, node );
        return node;
    }

    private static SimpleEdge createEdge( Map<Long, SimpleEdge> edgeMap, List<SimpleEdge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, double distance ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        source.addEdge( edge );
        target.addEdge( edge );
        edgeMap.put( id, edge );
        return edge;
    }

    private static SimpleEdge createEdge( Map<Long, SimpleEdge> edgeMap, List<SimpleEdge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, double distance, boolean addToNode ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        if ( addToNode ) {
            source.addEdge( edge );
            target.addEdge( edge );
        }
        edgeMap.put( id, edge );
        return edge;
    }
}

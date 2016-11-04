/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.values.Distance;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class GraphGeneratorUtils {

    private static final int GRID_MAX_SIZE = 100;
    private static final int GRID_EDGE_SIZE = 1;

    public static UndirectedGraph generateGridGraph( Set<Metric> metrics, Map<Long, Node> nodeMap, Map<Long, Edge> edgeMap, Map<TurnTable, TurnTable> turnTables, int rows, int columns ) {
        if ( rows > GRID_MAX_SIZE || columns > GRID_MAX_SIZE ) {
            throw new IllegalArgumentException( "It's a quick test! Size over " + GRID_MAX_SIZE + " is just too much: rows = " + rows + ", columns = " + columns );
        }
        UndirectedGraph graph = new UndirectedGraph( metrics );
        int multiplier = GRID_MAX_SIZE;
        while ( rows < multiplier / 10 && columns < multiplier / 10 ) {
            multiplier /= 10;
        }
        List<Node> nodes = new ArrayList<>();
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < columns; j++ ) {
                createNode( graph, nodeMap, nodes, i * multiplier + j );
            }
        }
        List<Edge> edges = new ArrayList<>();
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < columns; j++ ) {
                SimpleNode target = (SimpleNode) nodeMap.get( (long) i * multiplier + j );
//                System.out.println( "target=#" + target.getId() );
//                int targetIndex = ( i < rows - 1 ? 1 : 0 ) + ( j < columns - 1 ? 1 : 0 );
                int targetIndex = 0;
                if ( i > 0 ) {
//                    int sourceIndex = ( 0 < i - 1 ? 1 : 0 ) + ( 0 < j ? 1 : 0 );
                    int sourceIndex = ( 0 < i - 1 ? 1 : 0 ) + ( 0 < j ? 1 : 0 ) + ( j < columns - 1 ? 1 : 0 );
                    SimpleNode source = (SimpleNode) nodeMap.get( (long) ( i - 1 ) * multiplier + j );
//                    System.out.println( "source(top)=#" + source.getId() );
//                    System.out.println( "targetIndex = " + targetIndex + ", sourceIndex = " + sourceIndex );
                    createEdge( graph, edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, sourceIndex, targetIndex, GRID_EDGE_SIZE );
                    targetIndex++;
                }
                if ( j > 0 ) {
                    int sourceIndex = ( 0 < i ? 1 : 0 ) + ( 0 < j - 1 ? 1 : 0 );
                    SimpleNode source = (SimpleNode) nodeMap.get( (long) i * multiplier + ( j - 1 ) );
//                    System.out.println( "source(left)=#" + source.getId() );
//                    System.out.println( "targetIndex = " + targetIndex + ", sourceIndex = " + sourceIndex );
                    createEdge( graph, edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, sourceIndex, targetIndex, GRID_EDGE_SIZE );
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
        return graph;
    }

    public static Graph createGraph( Set<Metric> metrics, Map<Long, Node> nodeMap, Map<Long, Edge> edgeMap, Map<TurnTable, TurnTable> turnTables ) {
        UndirectedGraph g = new UndirectedGraph( metrics );
        List<Node> nodes = new ArrayList<>();
        SimpleNode a = createNode( g, nodeMap, nodes, 0 );
        SimpleNode b = createNode( g, nodeMap, nodes, 1 );
        SimpleNode c = createNode( g, nodeMap, nodes, 2 );
        SimpleNode d = createNode( g, nodeMap, nodes, 3 );
        SimpleNode e = createNode( g, nodeMap, nodes, 4 );
        SimpleNode f = createNode( g, nodeMap, nodes, 5 );
        List<Edge> edges = new ArrayList<>();
        SimpleEdge ab = createEdge( g, edgeMap, edges, 0, false, a, b, 0, 0, 120 );
        SimpleEdge ac = createEdge( g, edgeMap, edges, 1, false, a, c, 1, 0, 184 );
        SimpleEdge cd = createEdge( g, edgeMap, edges, 2, false, c, d, 1, 0, 94 );
        SimpleEdge db = createEdge( g, edgeMap, edges, 3, true, d, b, 1, 1, 159 );
        SimpleEdge be = createEdge( g, edgeMap, edges, 4, false, b, e, 2, 0, 36 );
        SimpleEdge df = createEdge( g, edgeMap, edges, 5, false, d, f, 2, 0, 38 );
        SimpleEdge ef = createEdge( g, edgeMap, edges, 6, true, e, f, 1, 1, 152 );
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
        return g;
    }

    public static Graph<ContractNode, ContractEdge> toContractGraph( Graph<Node, Edge> graph ) {
        ContractGraph g = new ContractGraph();
        for ( Node node : graph.getNodes() ) {
            g.createNode( node.getId(), Arrays.asList( node ) );
        }
        for ( Edge edge : graph.getEdges() ) {
            ContractEdge newEdge = g.createEdge( edge.getId(), edge.isOneWay(),
                    g.getNodeById( edge.getSource().getId() ), g.getNodeById( edge.getTarget().getId() ),
                    Arrays.asList( edge ),
                    new Pair<>( Metric.LENGTH, edge.getLength( Metric.LENGTH ) ), new Pair<>( Metric.TIME, edge.getLength( Metric.TIME ) ), new Pair<>( Metric.SIZE, edge.getLength( Metric.SIZE ) ) );
        }
        for ( ContractEdge edge : g.getEdges() ) {
            edge.setLength( Metric.SIZE, Distance.newInstance( edge.calculateWidth() ) );
        }
        return g;
    }

    private static SimpleNode createNode( UndirectedGraph graph, Map<Long, Node> nodeMap, List<Node> nodes, long id ) {
//        Distance[][] tt = new Distance[]
        SimpleNode node = graph.createNode( id );
        nodes.add( node );
        nodeMap.put( id, node );
        return node;
    }

    private static SimpleEdge createEdge( UndirectedGraph graph, Map<Long, Edge> edgeMap, List<Edge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, int sourcePos, int targetPos, double distance ) {
        SimpleEdge edge = graph.createEdge( id, oneway, source, target, sourcePos, targetPos );
        edges.add( edge );
        edgeMap.put( id, edge );
        Distance dist = Distance.newInstance( distance );
        for ( Metric metric : graph.getMetrics() ) {
            edge.setLength( metric, dist );
        }
        return edge;
    }
}

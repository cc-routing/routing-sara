/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

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
import cz.certicon.routing.model.values.Distance;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GraphGeneratorUtils {

    private static final int GRID_MAX_SIZE = 100;
    private static final int GRID_EDGE_SIZE = 1;

    public static Graph<Node, Edge> generateGridGraph( Map<Long, Node> nodeMap, Map<Long, Edge> edgeMap, Map<TurnTable, TurnTable> turnTables, int rows, int columns ) {
        if ( rows > GRID_MAX_SIZE || columns > GRID_MAX_SIZE ) {
            throw new IllegalArgumentException( "It's a quick test! Size over " + GRID_MAX_SIZE + " is just too much: rows = " + rows + ", columns = " + columns );
        }
        Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
        metricMap.put( Metric.SIZE, new HashMap<Edge, Distance>() );
        int multiplier = GRID_MAX_SIZE;
        while ( rows < multiplier / 10 && columns < multiplier / 10 ) {
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
                SimpleNode target = (SimpleNode) nodeMap.get( (long) i * multiplier + j );
                int targetIndex = ( i < rows - 1 ? 1 : 0 ) + ( j < columns - 1 ? 1 : 0 );
                if ( i > 0 ) {
                    int sourceIndex = ( 0 < i - 1 ? 1 : 0 ) + ( 0 < j ? 1 : 0 );
                    SimpleNode source = (SimpleNode) nodeMap.get( (long) ( i - 1 ) * multiplier + j );
                    createEdge( edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, sourceIndex, targetIndex, GRID_EDGE_SIZE, metricMap );
                    targetIndex++;
                }
                if ( j > 0 ) {
                    int sourceIndex = ( 0 < i ? 1 : 0 ) + ( 0 < j - 1 ? 1 : 0 );
                    SimpleNode source = (SimpleNode) nodeMap.get( (long) i * multiplier + ( j - 1 ) );
                    createEdge( edgeMap, edges, source.getId() * multiplier * multiplier + target.getId(), false, source, target, sourceIndex, targetIndex, GRID_EDGE_SIZE, metricMap );
                }
            }
        }
        UndirectedGraph g = new UndirectedGraph( GraphUtils.toMap( nodes ), GraphUtils.toMap( edges ), metricMap );
        for ( Node node : nodes ) {
            int size = node.getDegree( g );
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
        return g;
    }

    public static Graph createGraph( Map<Long, Node> nodeMap, Map<Long, Edge> edgeMap, Map<TurnTable, TurnTable> turnTables ) {
        Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
        metricMap.put( Metric.SIZE, new HashMap<Edge, Distance>() );
        metricMap.put( Metric.LENGTH, new HashMap<Edge, Distance>() );
        metricMap.put( Metric.TIME, new HashMap<Edge, Distance>() );
        List<Node> nodes = new ArrayList<>();
        SimpleNode a = createNode( nodeMap, nodes, 0 );
        SimpleNode b = createNode( nodeMap, nodes, 1 );
        SimpleNode c = createNode( nodeMap, nodes, 2 );
        SimpleNode d = createNode( nodeMap, nodes, 3 );
        SimpleNode e = createNode( nodeMap, nodes, 4 );
        SimpleNode f = createNode( nodeMap, nodes, 5 );
        List<Edge> edges = new ArrayList<>();
        SimpleEdge ab = createEdge( edgeMap, edges, 0, false, a, b, 0, 0, 120, metricMap );
        SimpleEdge ac = createEdge( edgeMap, edges, 1, false, a, c, 1, 0, 184, metricMap );
        SimpleEdge cd = createEdge( edgeMap, edges, 2, false, c, d, 1, 0, 94, metricMap );
        SimpleEdge db = createEdge( edgeMap, edges, 3, true, d, b, 1, 1, 159, metricMap );
        SimpleEdge be = createEdge( edgeMap, edges, 4, false, b, e, 2, 0, 36, metricMap );
        SimpleEdge df = createEdge( edgeMap, edges, 5, false, d, f, 2, 0, 152, metricMap );
        SimpleEdge ef = createEdge( edgeMap, edges, 6, true, e, f, 1, 1, 38, metricMap );
        UndirectedGraph g = new UndirectedGraph( GraphUtils.toMap( nodes ), GraphUtils.toMap( edges ), metricMap );
        for ( Node node : nodes ) {
            int size = node.getDegree( g );
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
        TLongObjectMap<ContractNode> nodeMap = new TLongObjectHashMap<>();
        for ( Node node : graph.getNodes() ) {
            nodeMap.put( node.getId(), new ContractNode( node.getId(), Arrays.asList( node ) ) );
        }
        Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
        Map<Edge, Distance> distanceMap = new HashMap<>();
        metricMap.put( Metric.LENGTH, distanceMap );
        TLongObjectMap<ContractEdge> edgeMap = new TLongObjectHashMap<>();
        for ( Edge edge : graph.getEdges() ) {
            ContractEdge newEdge = new ContractEdge( edge.getId(), edge.isOneWay( graph ),
                    nodeMap.get( edge.getSource( graph ).getId() ), nodeMap.get( edge.getTarget( graph ).getId() ),
                    Arrays.asList( edge ) );
            edgeMap.put( edge.getId(), newEdge );
            distanceMap.put( newEdge, graph.getLength( Metric.LENGTH, edge ) );
        }
        return new UndirectedGraph<>( nodeMap, edgeMap, metricMap );
    }

    private static SimpleNode createNode( Map<Long, Node> nodeMap, List<Node> nodes, long id ) {
//        Distance[][] tt = new Distance[]
        SimpleNode node = new SimpleNode( id );
        nodes.add( node );
        nodeMap.put( id, node );
        return node;
    }

    private static SimpleEdge createEdge( Map<Long, Edge> edgeMap, List<Edge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, int sourcePos, int targetPos, double distance, Map<Metric, Map<Edge, Distance>> metricMap ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, sourcePos, targetPos );
        edges.add( edge );
        source.addEdge( edge );
        target.addEdge( edge );
        edgeMap.put( id, edge );
        metricMap.get( Metric.LENGTH ).put( edge, Distance.newInstance( distance ) );
        return edge;
    }

    private static SimpleEdge createEdge( Map<Long, Edge> edgeMap, List<Edge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, int sourcePos, int targetPos, double distance, Map<Metric, Map<Edge, Distance>> metricMap, boolean addToNode ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, sourcePos, targetPos );
        edges.add( edge );
        if ( addToNode ) {
            source.addEdge( edge );
            target.addEdge( edge );
        }
        edgeMap.put( id, edge );
        metricMap.get( Metric.LENGTH ).put( edge, Distance.newInstance( distance ) );
        return edge;
    }
}

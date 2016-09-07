/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm;

import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.values.Distance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class FordFulkersonMinimalCutTest {

    private final UndirectedGraph graph;
    private final Map<Long, SimpleNode> nodeMap;
    private final Map<Long, SimpleEdge> edgeMap;
    private final Map<TurnTable, TurnTable> turnTables;

    public FordFulkersonMinimalCutTest() {
        this.nodeMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.turnTables = new HashMap<>();
        graph = createGraph();
    }

    private UndirectedGraph createGraph() {
        List<SimpleNode> nodes = new ArrayList<>();
        SimpleNode a = createNode( nodes, 0 );
        SimpleNode b = createNode( nodes, 1 );
        SimpleNode c = createNode( nodes, 2 );
        SimpleNode d = createNode( nodes, 3 );
        SimpleNode e = createNode( nodes, 4 );
        SimpleNode f = createNode( nodes, 5 );
        List<SimpleEdge> edges = new ArrayList<>();
        SimpleEdge ab = createEdge( edges, 0, true, a, b, 16 );
        SimpleEdge ac = createEdge( edges, 1, true, a, c, 13 );
        SimpleEdge bc = createEdge( edges, 2, true, b, c, 10 );
        SimpleEdge bd = createEdge( edges, 3, true, b, d, 12 );
        SimpleEdge cb = createEdge( edges, 4, true, c, b, 4 );
        SimpleEdge ce = createEdge( edges, 5, true, c, e, 14 );
        SimpleEdge dc = createEdge( edges, 6, true, d, c, 9 );
        SimpleEdge df = createEdge( edges, 7, true, d, f, 20 );
        SimpleEdge ed = createEdge( edges, 8, true, e, d, 7 );
        SimpleEdge ef = createEdge( edges, 9, true, e, f, 4 );
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

    private SimpleNode createNode( List<SimpleNode> nodes, long id ) {
//        Distance[][] tt = new Distance[]
        SimpleNode node = new SimpleNode( id );
        nodes.add( node );
        nodeMap.put( id, node );
        return node;
    }

    private SimpleEdge createEdge( List<SimpleEdge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, double distance ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        source.addEdge( edge );
        target.addEdge( edge );
        edgeMap.put( id, edge );
        return edge;
    }

    private SimpleEdge createEdge( List<SimpleEdge> edges, long id, boolean oneway, SimpleNode source, SimpleNode target, double distance, boolean addToNode ) {
        SimpleEdge edge = new SimpleEdge( id, oneway, source, target, Distance.newInstance( distance ) );
        edges.add( edge );
        if ( addToNode ) {
            source.addEdge( edge );
            target.addEdge( edge );
        }
        edgeMap.put( id, edge );
        return edge;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compute method, of class FordFulkersonMinimalCut.
     */
    @Test
    public void testCompute() {
        System.out.println( "compute" );
        SimpleNode sourceNode = nodeMap.get( 0L );
        SimpleNode targetNode = nodeMap.get( 5L );
        FordFulkersonMinimalCut instance = new FordFulkersonMinimalCut();
        MinimalCut expected = new MinimalCut( Arrays.asList( edgeMap.get( 3L ), edgeMap.get( 8L ), edgeMap.get( 9L ) ), 23 );
        MinimalCut result = instance.compute( graph, sourceNode, targetNode );
        assertEquals( toString( expected ), toString( result ) );
    }

    public String toString( MinimalCut minimalCut ) {
        List<SimpleEdge> sortedEdges = new ArrayList<>( minimalCut.getCutEdges() );
        Collections.sort(sortedEdges, new Comparator<SimpleEdge>() {
            @Override
            public int compare( SimpleEdge o1, SimpleEdge o2 ) {
                return Long.compare( o1.getId(), o2.getId() );
            }
        } );

        StringBuilder sb = new StringBuilder();
        sb.append( "MinimalCut{cut=" ).append( minimalCut.getCutSize() ).append( ",edges=[" );
        for ( SimpleEdge edge : sortedEdges ) {
            sb.append( edge.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]}" );
        return sb.toString();
    }

}

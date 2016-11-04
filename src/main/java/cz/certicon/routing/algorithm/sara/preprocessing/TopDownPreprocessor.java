/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing;

import cz.certicon.routing.algorithm.sara.preprocessing.assembly.Assembler;
import cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler;
import cz.certicon.routing.algorithm.sara.preprocessing.filtering.Filter;
import cz.certicon.routing.algorithm.sara.preprocessing.filtering.NaturalCutsFilter;
import cz.certicon.routing.model.basic.IdSupplier;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.utils.java8.IteratorStreams;
import cz.certicon.routing.utils.progress.EmptyProgressListener;
import cz.certicon.routing.utils.progress.ProgressListener;

import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import java8.util.Optional;
import java8.util.function.ToIntFunction;

/**
 * Implementation of the {@link Preprocessor} interface using top-down preprocessing strategy.
 * Such strategy filters the bottom layer and assembles it into large areas. Then for each such area
 * it filters it and assembles into smaller areas, recursively, building it from the top.
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class TopDownPreprocessor implements Preprocessor {

    private static final double FILTER_TO_ASSEMBLY_RATIO = 100;

    @Override
    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, IdSupplier cellIdSupplier ) {
        return preprocess( graph, input, cellIdSupplier, new EmptyProgressListener() );
    }

    @Override
    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, IdSupplier cellIdSupplier, ProgressListener progressListener ) {
        // perform preprocessing on the whole area
        List<Pair<UndirectedGraph, Cell>> graphs = new LinkedList<>();
        Filter filter = new NaturalCutsFilter( input.getCellRatio(), 1 / input.getCoreRatio(), input.getCellSizes()[0] );
        Assembler assembler = new GreedyAssembler( input.getLowIntervalProbability(), input.getLowIntervalLimit(), input.getCellSizes()[0] );
        graphs.add( new Pair<>( UndirectedGraph.fromGraph( graph ), new Cell( cellIdSupplier.next() ) ) );
        for ( int layer = 0; layer < input.getNumberOfLayers(); layer++ ) {
            List<Pair<UndirectedGraph, Cell>> newGraphs = new LinkedList<>();
            for ( Pair<UndirectedGraph, Cell> pair : graphs ) {
                UndirectedGraph g = pair.a;
                // filter
                filter.setMaxCellSize( input.getCellSizes()[input.getNumberOfLayers() - layer - 1] );
                ContractGraph filteredGraph = filter.filter( g );
                // assembly large areas
                assembler.setMaxCellSize( input.getCellSizes()[input.getNumberOfLayers() - layer - 1] );
                Optional<ContractGraph> assembled = Optional.empty();
                long bestEdgeCount = Long.MAX_VALUE;
                for ( int run = 0; run < input.getNumberOfAssemblyRuns(); run++ ) {
                    ContractGraph assembledTmp = assembler.assemble( filteredGraph );
                    long edgeCount = IteratorStreams.stream( assembledTmp.getEdges() )
                            .mapToInt( new ToIntFunction<ContractEdge>() {
                                @Override
                                public int applyAsInt( ContractEdge value ) {
                                    return value.calculateWidth();
                                }
                            } )
                            .sum();
                    if ( edgeCount < bestEdgeCount ) {
                        assembled = Optional.of( assembledTmp );
                        bestEdgeCount = edgeCount;
                    }
//                progressListener.nextStep();
                }
                // foreach assembled area create subgraph - foreach node in assembled contractGraph
                ContractGraph assembledAreas = assembled.get();
                for ( ContractNode node : assembledAreas.getNodes() ) {
                    Collection<Node> nodes = node.getNodes();
                    // create subgraph
                    UndirectedGraph newGraph = new UndirectedGraph();
                    // foreach node add node
                    for ( Node n : nodes ) {
                        SimpleNode newNode = newGraph.createNode( n.getId() );
                        newNode.setCoordinate( n.getCoordinate() );
                        newNode.setTurnTable( n.getTurnTable() );
                    }
                    // foreach edge add edge if it connects already present nodes
                    for ( Node n : nodes ) {
                        cz.certicon.routing.utils.collections.Iterator edges = n.getEdges();
                        while ( edges.hasNext() ) {
                            Edge e = (Edge) edges.next();
                            if ( newGraph.containsNode( e.getSource().getId() ) && newGraph.containsNode( e.getTarget().getId() ) ) {
                                newGraph.createEdge( e.getId(), e.isOneWay(), newGraph.getNodeById( e.getSource().getId() ), newGraph.getNodeById( e.getTarget().getId() ), e.getSourcePosition(), e.getTargetPosition() );
                            }
                        }
                    }
                    Cell newCell = new Cell( cellIdSupplier.next() );
                    newCell.setParent( pair.b );
                    newGraphs.add( new Pair<>( newGraph, newCell ) );
                }
                // repeat for N layers    
            }
            graphs = newGraphs;
        }
        // build sara graph
        SaraGraph saraGraph = new SaraGraph( EnumSet.of( Metric.LENGTH, Metric.TIME ) );
        for ( Pair<UndirectedGraph, Cell> pair : graphs ) {
            UndirectedGraph g = pair.a;
            for ( SimpleNode node : g.getNodes() ) {
                SaraNode saraNode = saraGraph.createNode( node.getId(), pair.b );
                saraNode.setCoordinate( node.getCoordinate() );
                saraNode.setTurnTable( node.getTurnTable() );
            }
        }
        for ( E edge : graph.getEdges() ) {
            SaraEdge saraEdge = saraGraph.createEdge( edge.getId(), edge.isOneWay(),
                    saraGraph.getNodeById( edge.getSource().getId() ), saraGraph.getNodeById( edge.getTarget().getId() ),
                    edge.getSourcePosition(), edge.getTargetPosition(),
                    new Pair<>( Metric.LENGTH, edge.getLength( Metric.LENGTH ) ), new Pair<>( Metric.TIME, edge.getLength( Metric.TIME ) ) );
        }
        return saraGraph;
    }

    private int pow( int a, int b ) {
        if ( b == 0 ) {
            return 1;
        }
        if ( b == 1 ) {
            return a;
        }
        if ( b % 2 == 0 ) {
            return pow( a * a, b / 2 ); //even a=(a^2)^b/2
        } else {
            return a * pow( a * a, b / 2 ); //odd  a=a*(a^2)^b/2
        }
    }

}

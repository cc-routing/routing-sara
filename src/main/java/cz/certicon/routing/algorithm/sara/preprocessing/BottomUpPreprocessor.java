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
import cz.certicon.routing.model.basic.MaxIdContainer;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Cell;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.values.Time;
import cz.certicon.routing.model.values.TimeUnits;
import cz.certicon.routing.utils.java8.IteratorStreams;
import cz.certicon.routing.utils.measuring.TimeLogger;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.routing.utils.progress.EmptyProgressListener;
import cz.certicon.routing.utils.progress.ProgressListener;
import java.util.EnumSet;
import java8.util.function.Predicate;
import java8.util.function.ToIntFunction;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class BottomUpPreprocessor implements Preprocessor {

    private static final double FILTER_TO_ASSEMBLY_RATIO = 100;

    @Override
    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, MaxIdContainer cellIdContainer ) {
        return preprocess( graph, input, cellIdContainer, new EmptyProgressListener() );
    }

    @Override
    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, MaxIdContainer cellIdContainer, ProgressListener progressListener ) {
        double filterRatio = FILTER_TO_ASSEMBLY_RATIO / ( FILTER_TO_ASSEMBLY_RATIO + input.getNumberOfAssemblyRuns() * input.getNumberOfLayers() );
        progressListener.init( 1, filterRatio );
        TimeLogger.log( TimeLogger.Event.FILTERING, TimeLogger.Command.START );
        Filter filter = new NaturalCutsFilter( input.getCellRatio(), 1 / input.getCoreRatio(), input.getCellSizes()[0] );
        ContractGraph filteredGraph = filter.filter( graph );
        TimeLogger.log( TimeLogger.Event.FILTERING, TimeLogger.Command.STOP );
        progressListener.nextStep();
        int assemblySize = input.getNumberOfAssemblyRuns() * input.getNumberOfLayers();
        progressListener.init( assemblySize, 1.0 - filterRatio );
        TimeLogger.log( TimeLogger.Event.ASSEMBLING, TimeLogger.Command.START );
        Assembler assembler = new GreedyAssembler( input.getLowIntervalProbability(), input.getLowIntervalLimit(), input.getCellSizes()[0] );

        // for i < layers
        // - assemble graph
        // - create sara graph
        // - assign parents
        SaraGraph saraGraph = null;
        for ( int i = 0; i < input.getNumberOfLayers(); i++ ) {
            assembler.setMaxCellSize( input.getCellSizes()[i] );
            ContractGraph assembled = null;
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
                    assembled = assembledTmp;
                    bestEdgeCount = edgeCount;
                }
                progressListener.nextStep();
            }
            if ( saraGraph == null ) {
                saraGraph = new SaraGraph( EnumSet.of( Metric.LENGTH, Metric.TIME ) );
                for ( ContractNode node : assembled.getNodes() ) {
                    Cell cell = new Cell( cellIdContainer.next() );
//                    System.out.println( "putting: " + node.getId() + " -> " + cell.getId() + ", nodes=[" + StreamSupport.stream( node.getNodeIds() ).map( Mappers.identifiableToString ).collect( Collectors.joining( "," ) ) + "]" );
                    for ( Node origNode : node.getNodes() ) {
                        SaraNode saraNode = saraGraph.createNode( origNode.getId(), cell );
                        saraNode.setCoordinate( origNode.getCoordinate() );
                        saraNode.setTurnTable( origNode.getTurnTable() );
                    }
                }
                for ( E edge : graph.getEdges() ) {
                    SaraEdge saraEdge = saraGraph.createEdge( edge.getId(), edge.isOneWay(),
                            saraGraph.getNodeById( edge.getSource().getId() ), saraGraph.getNodeById( edge.getTarget().getId() ),
                            edge.getSourcePosition(), edge.getTargetPosition(),
                            new Pair<>( Metric.LENGTH, edge.getLength( Metric.LENGTH ) ), new Pair<>( Metric.TIME, edge.getLength( Metric.TIME ) ) );
                }
            } else {
                for ( ContractNode node : assembled.getNodes() ) {
                    Cell cell = new Cell( cellIdContainer.next() );
//                    System.out.println( "putting&getting: " + node.getId() + " -> " + cell.getId() + ", nodes=[" + StreamSupport.stream( node.getNodeIds() ).map( Mappers.identifiableToString ).collect( Collectors.joining( "," ) ) + "]" );
                    for ( Node n : node.getNodes() ) {
                        Cell parent = saraGraph.getNodeById( n.getId() ).getParent();
                        for ( int j = 1; j < i; j++ ) {
                            parent = parent.getParent();
                        }
                        if ( !parent.hasParent() ) {
                            parent.setParent( cell );
                            parent.lock();
                        }
                    }
                }
            }
            filteredGraph = assembled;
        }
        TimeLogger.log( TimeLogger.Event.ASSEMBLING, TimeLogger.Command.STOP );
        return saraGraph;
    }
}

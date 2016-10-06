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
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.utils.java8.IteratorStreams;
import cz.certicon.routing.utils.measuring.TimeLogger;
import cz.certicon.routing.utils.progress.EmptyProgressListener;
import cz.certicon.routing.utils.progress.ProgressListener;
import java.util.EnumSet;
import java8.util.function.Predicate;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class BottomUpPreprocessor implements Preprocessor {

    private static final double FILTER_TO_ASSEMBLY_RATIO = 1000;

    @Override
    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, MaxIdContainer cellIdContainer ) {
        return preprocess( graph, input, cellIdContainer, new EmptyProgressListener() );
    }

    @Override
    public <N extends Node, E extends Edge> SaraGraph preprocess( Graph<N, E> graph, PreprocessingInput input, MaxIdContainer cellIdContainer, ProgressListener progressListener ) {
        double filterRatio = FILTER_TO_ASSEMBLY_RATIO / ( FILTER_TO_ASSEMBLY_RATIO + input.getNumberOfAssemblyRuns() * input.getNumberOfLayers() );
        progressListener.init( 1, filterRatio );
        TimeLogger.log( TimeLogger.Event.FILTERING, TimeLogger.Command.START );
        Filter filter = new NaturalCutsFilter( input.getCellRatio(), 1 / input.getCoreRatio(), input.getCellSize() );
        ContractGraph filteredGraph = filter.filter( graph );
        TimeLogger.log( TimeLogger.Event.FILTERING, TimeLogger.Command.STOP );
        progressListener.nextStep();
        int assemblySize = input.getNumberOfAssemblyRuns() * input.getNumberOfLayers();
        progressListener.init( assemblySize, 1.0 - filterRatio );
        TimeLogger.log( TimeLogger.Event.ASSEMBLING, TimeLogger.Command.START );
        Assembler assembler = new GreedyAssembler( input.getLowIntervalProbability(), input.getLowIntervalLimit(), input.getCellSize() );

        SaraGraph bestResult = null;
        long bestEdgeCount = Long.MAX_VALUE;
        for ( int run = 0; run < input.getNumberOfAssemblyRuns(); run++ ) {

            int currentCellSize = input.getCellSize();
            // for i < layers
            // - assemble graph
            // - create sara graph
            // - assign parents
            SaraGraph saraGraph = null;
            for ( int i = 0; i < input.getNumberOfLayers(); i++ ) {
                ContractGraph assembled = assembler.assemble( filteredGraph );
                if ( saraGraph == null ) {
                    saraGraph = new SaraGraph( EnumSet.of( Metric.LENGTH, Metric.TIME ) );
                    for ( ContractNode node : assembled.getNodes() ) {
                        Cell cell = new Cell( cellIdContainer.next() );
//                    System.out.println( "putting: " + node.getId() + " -> " + cell.getId() + ", nodes=[" + StreamSupport.stream( node.getNodes() ).map( Mappers.identifiableToString ).collect( Collectors.joining( "," ) ) + "]" );
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
//                    System.out.println( "putting&getting: " + node.getId() + " -> " + cell.getId() + ", nodes=[" + StreamSupport.stream( node.getNodes() ).map( Mappers.identifiableToString ).collect( Collectors.joining( "," ) ) + "]" );
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
                currentCellSize *= input.getCellSize();
                assembler.setMaxCellSize( currentCellSize );
                progressListener.nextStep();
            }
            long edgeCount = IteratorStreams.stream( saraGraph.getEdges() ).filter( new Predicate<SaraEdge>() {
                @Override
                public boolean test( SaraEdge edge ) {
                    return !edge.getSource().getParent().equals( edge.getTarget().getParent() );
                }
            } ).count();
            if ( edgeCount < bestEdgeCount ) {
                bestResult = saraGraph;
                bestEdgeCount = edgeCount;
            }
        }
        TimeLogger.log( TimeLogger.Event.ASSEMBLING, TimeLogger.Command.STOP );
        return bestResult;
    }

//    @Override
//    public <N extends Node, E extends Edge> ContractGraph assemble( Graph<N, E> originalGraph, ContractGraph filteredGraph, MaxIdContainer cellId, int layers ) {
//        if ( layers < 1 ) {
//            throw new IllegalArgumentException( "Number of layers must be positive!" );
//        }
//        ContractGraph graph = filteredGraph;
//        long currentCellSize = maxCellSize;
//        // for i < layers
//        // - assemble graph
//        // - create sara graph
//        // - assign parents
//        SaraGraph saraGraph = null;
//        for ( int i = 0; i < layers; i++ ) {
//            ContractGraph assembled = recursiveAssemble( graph, currentCellSize );
//            if ( saraGraph == null ) {
//                saraGraph = new SaraGraph( EnumSet.of( Metric.LENGTH, Metric.TIME ) );
//                for ( ContractNode node : assembled.getNodes() ) {
//                    Cell cell = new Cell( cellId.next() );
////                    System.out.println( "putting: " + node.getId() + " -> " + cell.getId() + ", nodes=[" + StreamSupport.stream( node.getNodes() ).map( Mappers.identifiableToString ).collect( Collectors.joining( "," ) ) + "]" );
//                    for ( Node origNode : node.getNodes() ) {
//                        SaraNode saraNode = saraGraph.createNode( origNode.getId(), cell );
//                        saraNode.setCoordinate( origNode.getCoordinate() );
//                        saraNode.setTurnTable( origNode.getTurnTable() );
//                    }
//                }
//                for ( E edge : originalGraph.getEdges() ) {
//                    SaraEdge saraEdge = saraGraph.createEdge( edge.getId(), edge.isOneWay(),
//                            saraGraph.getNodeById( edge.getSource().getId() ), saraGraph.getNodeById( edge.getTarget().getId() ),
//                            edge.getSourcePosition(), edge.getTargetPosition(),
//                            new Pair<>( Metric.LENGTH, edge.getLength( Metric.LENGTH ) ), new Pair<>( Metric.TIME, edge.getLength( Metric.TIME ) ) );
//                }
//            } else {
//                for ( ContractNode node : assembled.getNodes() ) {
//                    Cell cell = new Cell( cellId.next() );
////                    System.out.println( "putting&getting: " + node.getId() + " -> " + cell.getId() + ", nodes=[" + StreamSupport.stream( node.getNodes() ).map( Mappers.identifiableToString ).collect( Collectors.joining( "," ) ) + "]" );
//                    for ( Node n : node.getNodes() ) {
//                        Cell parent = saraGraph.getNodeById( n.getId() ).getParent();
//                        for ( int j = 1; j < i; j++ ) {
//                            parent = parent.getParent();
//                        }
//                        if ( !parent.hasParent() ) {
//                            parent.setParent( cell );
//                            parent.lock();
//                        }
//                    }
//                }
//
//            }
//            graph = assembled;
//            currentCellSize *= maxCellSize;
//        }
//        return saraGraph;
//    }
//
//    @Override
//    public <N extends Node, E extends Edge> ContractGraph assemble( Graph<N, E> originalGraph, ContractGraph filteredGraph, MaxIdContainer cellId ) {
//        ContractGraph graph = recursiveAssemble( filteredGraph, maxCellSize );
//        SaraGraph saraGraph = new SaraGraph( EnumSet.of( Metric.LENGTH, Metric.TIME ) );
//        for ( ContractNode node : graph.getNodes() ) {
//            Cell cell = new Cell( cellId.next() );
//            for ( Node origNode : node.getNodes() ) {
//                SaraNode saraNode = saraGraph.createNode( origNode.getId(), cell );
//                saraNode.setCoordinate( origNode.getCoordinate() );
//                saraNode.setTurnTable( origNode.getTurnTable() );
//            }
//        }
//        for ( E edge : originalGraph.getEdges() ) {
//            SaraEdge saraEdge = saraGraph.createEdge( edge.getId(), edge.isOneWay(),
//                    saraGraph.getNodeById( edge.getSource().getId() ), saraGraph.getNodeById( edge.getTarget().getId() ),
//                    edge.getSourcePosition(), edge.getTargetPosition(),
//                    new Pair<>( Metric.LENGTH, edge.getLength( Metric.LENGTH ) ), new Pair<>( Metric.TIME, edge.getLength( Metric.TIME ) ) );
//        }
//        return saraGraph;
//    }
}

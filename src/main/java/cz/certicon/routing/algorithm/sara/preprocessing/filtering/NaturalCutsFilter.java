/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.model.graph.preprocessing.ContractEdge;
import cz.certicon.routing.algorithm.FordFulkersonMinimalCut;
import cz.certicon.routing.algorithm.MinimalCutAlgorithm;
import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.GraphUtils;
import cz.certicon.routing.utils.ToStringUtils;
import cz.certicon.routing.utils.collections.CollectionUtils;
import cz.certicon.routing.view.GraphStreamPresenter;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Implementation of the {@link Filter} interface. Uses Natural cuts technique
 * for reducing the graph size and dividing it into separate components
 *
 * C = {} //set of cut edges ***************************************************
 * Repeat O times **************************************************************
 * *** Q = {x; x e V} //set of vertices to go through **************************
 * *** Sort Q randomly *********************************************************
 * *** While Q is not empty ****************************************************
 * *** v = Q.first *************************************************************
 * *** T = {x; x e V, x forms BFS tree from root v until s(T) &lt; alpha*U} ****
 * *** R = {x; x V\T, y e T, {x,y} e E} // ring ********************************
 * *** J = {x; x T, x was added to T before s(T) &gt;= U/f} // core ***********
 * *** Temporarily contract J to single vertex s *******************************
 * *** Temporarily contract R to single vertex t *******************************
 * *** Find min s-t cut MC //set of cut edges dividing s from t ****************
 * *** C.uniqueAdd(MC) *********************************************************
 * *** Remove all vertices J from Q ********************************************
 * End While *******************************************************************
 * End Repeat ******************************************************************
 *
 * Contract each component from GC=(V,E\C) on the origin graph G ***************
 * Return G ********************************************************************
 *
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class NaturalCutsFilter implements Filter {

    private static final int NODE_INIT_SIZE = 1;
    private static final int EDGE_INIT_SIZE = 1;

    @Wither
    private final double cellRatio; // alpha
    @Wither
    private final double coreRatioInverse; // f
    @Wither
    private final int maxCellSize; // U

    /**
     * Creates new instance
     *
     * @param cellRatio portion of U (max cell size) defining the size of
     * fragment, alpha, 0 &lt;= alpha &lt;= 1
     * @param coreRatioInverse divisor defining the core size, core size =
     * alpha*U/f, this is f
     * @param maxCellSize maximal size of a fragment, U
     */
    public NaturalCutsFilter( double cellRatio, double coreRatioInverse, int maxCellSize ) {
        this.cellRatio = cellRatio;
        this.coreRatioInverse = coreRatioInverse;
        this.maxCellSize = maxCellSize;
    }

    @Override
    public <N extends Node, E extends Edge> FilteredGraph filter( Graph<N, E> graph ) {
//        GraphStreamPresenter presenter = new GraphStreamPresenter();
//        presenter.displayGraph( graph );
        // NOTE: contraction
        // - foreach node
        // -- remove node
        // -- preserve paths (create edges between all the neighbors)
//        System.out.println( "started filtering" );
        ElementContainer<E> cutEdges = getCutEdges( graph );
//        System.out.println( "cut edges obtained: " + cutEdges.size() );
//        presenter = new GraphStreamPresenter();
//        presenter.setGraph( graph );
//        for ( Edge cutEdge : cutEdges ) {
//            presenter.setEdgeColor( cutEdge.getId(), Color.red );
//        }
//        presenter.display();
//        if ( true == true ) {
//            try {
//                Thread.sleep( 1000000 );
//            } catch ( InterruptedException ex ) {
//                Logger.getLogger( NaturalCutsFilter.class.getName() ).log( Level.SEVERE, null, ex );
//            }
//        }
        // TODO must handle properly empty cutEdges
        // split graph into regions bounded by the cut edges
//        System.out.println( "splitting graph" );

        SplitGraphMessenger<N, E> splitGraphResult = splitGraph( graph, cutEdges );
//        System.out.println( "graph splitted" );
//        presenter = new GraphStreamPresenter();
//        presenter.setGraph( graph );
//        for ( Edge cutEdge : cutEdges ) {
//            presenter.removeEdge( cutEdge.getId() );
//        }
//        presenter.display();
//        System.out.println( "Press enter to continue..." );
//        new Scanner( System.in ).nextLine();
        // build new filtered graph
//        System.out.println( "building filtered graph" );
        FilteredGraph filteredGraph = buildFilteredGraph( splitGraphResult.getFragmentOrigNodes(), splitGraphResult.getOrigEdgesMapList() );
//        System.out.println( "graph built" );

//        presenter = new GraphStreamPresenter();
//        presenter.displayGraph( filteredGraph );
//        presenter = new GraphStreamPresenter();
//        presenter.displayGraph( graph );
        // nodeSizes // splitGraph.fragmentSizeMap
        return filteredGraph;
    }

    private <N extends Node, E extends Edge> ElementContainer<E> getCutEdges( Graph<N, E> graph ) {
        // init structures
        ElementContainer<E> cutEdges = new SetElementContainer<>();
        ElementContainer<N> coreNodes = new SetElementContainer<>();
        ElementContainer<N> ringNodes = new SetElementContainer<>();
        ElementContainer<N> treeNodes = new SetElementContainer<>();
        NodeSizeContainer<N> nodeSizeContainer = new MapNodeSizeContainer<>();
        NodeSizeContainer<N> nodeOrderContainer = new MapNodeSizeContainer<>();
        Queue<N> nodeQueue = new LinkedList<>();
        RandomSet<N> randomNodes = new MixRandomSet<>( graph.getNodesCount() );
        // TODO need structure which allows random pick and fast element removal (target element)
        // conside using set and converting to array or iterator for random pick - how many random picks???
        Iterator<N> nodeIterator = graph.getNodes();
        while ( nodeIterator.hasNext() ) {
            N node = nodeIterator.next();
            // TODO remove condition - only for test (remove randomness)
//            if ( node.getId() == 0 ) {
            randomNodes.add( node );
//            }
            nodeSizeContainer.put( node, NODE_INIT_SIZE );
        }
        Random random = new Random();
        // until there are no nodes left
//        System.out.println( "starting cycle" );
        while ( !randomNodes.isEmpty() ) {
            // pick a node (=center) at random (a node that does not belong to any core)
            N center = randomNodes.pollRandom( random );
//            System.out.println( "picking: " + center.getId() );
            // create tree T via BFS from center at maximal size of cellRatio * maxCellSize, where size is a sum of tree's nodes' sizes
            nodeQueue.add( center );
            // NOTE: what is node size? 1 at the beginning, then sum of contracted nodes inside this node
            int sum = 0;
//            System.out.println( "creating tree: core size = " + ( cellRatio * maxCellSize / coreRatioInverse ) + ", tree size = " + ( cellRatio * maxCellSize ) );
            while ( !nodeQueue.isEmpty() ) {
                N node = nodeQueue.poll();
                int size = nodeSizeContainer.getSize( node );
                // all the nodes added to tree before it reached cellRatio * maxCellSize / coreRatioInverse form a "core"
                if ( sum + size <= cellRatio * maxCellSize / coreRatioInverse ) {
                    sum += size;
                    coreNodes.add( node );
                    // other nodes in the tree form the area where minimal cut will be performed
                } else if ( sum + size <= cellRatio * maxCellSize ) {
                    sum += size;
                    treeNodes.add( node );
                } else {
                    // direct neighors to the tree form a "ring"
                    ringNodes.add( node );
//                    nodeQueue.clear();
                    continue;
                }
                Iterator<E> edges = graph.getEdges( node );
                while ( edges.hasNext() ) {
                    E edge = edges.next();
                    N target = graph.getOtherNode( edge, node );
                    if ( !coreNodes.contains( target ) && !ringNodes.contains( target ) ) {
                        nodeQueue.add( target );
                    }
                }
            }
//            System.out.println( "adding minimal cut" );
            // mark edges from minimal cut as "cut edges"
            cutEdges.addAll( (Collection<E>) minimalCut( graph, treeNodes, coreNodes, ringNodes ) );
//            System.out.println( "minimal cut done" );
            // remove all core nodes from the queue
            for ( N coreNode : coreNodes ) {
                randomNodes.remove( coreNode );
            }
            treeNodes.clear();
            coreNodes.clear();
            ringNodes.clear();
        }
        return cutEdges;
    }

    private <N extends Node, E extends Edge> Map<N, Set<E>> contractNode( Graph<N, E> graph, ElementContainer<N> nodeGroup, N node ) {
        /*
         * Pseudocontract - selects only border nodes and creates evaluated edges, ignores inner nodes
         * TODO Correct - should contract the usual way and save information about inner edges per each shortcut, also should be able to unpack this shortcut and find the actual cut edge
         * contraction: while(hasNeighbor(node)){ merge(node, neighbor));} 
         */
//        System.out.println( "contracting nodes: " + ToStringUtils.toString( nodeGroup ) );
        Map<N, Set<E>> targets = new HashMap<>();
        for ( N singleNode : nodeGroup ) {
            Iterator<E> edges = singleNode.getEdges( graph );
            while ( edges.hasNext() ) {
                E edge = edges.next();
                N target = graph.getOtherNode( edge, singleNode );
                if ( !nodeGroup.contains( target ) ) {
                    Set<E> list = CollectionUtils.getSet( targets, target );
                    list.add( edge );
                }
            }
        }
        return targets;
        /*
        long maxNodeId = 0;
        Iterator<Node> nodes = graph.getNodes();
        while ( nodes.hasNext() ) {
            maxNodeId = Math.max( maxNodeId, nodes.next().getId() );
        }
        Node newNode = new Node( maxNodeId + 1 );
        long maxEdgeId = 0;
        Iterator<Edge> edges = graph.getEdges();
        while ( edges.hasNext() ) {
            maxEdgeId = Math.max( maxEdgeId, edges.next().getId() );
        }
        for ( Map.Entry<Node, List<Edge>> entry : targets.entrySet() ) {
            Edge edge = new ContractEdge( ++maxEdgeId, false, newNode, entry.getKey(), Distance.newInstance( entry.getValue().size() ), entry.getValue() );
            newNode.addEdge( edge );
        }
        return newNode;
         */
 /*
        Node newNode = new Node( node.getId() + 1 );

        Iterator<Edge> edges = node.getEdges();
        while ( edges.hasNext() ) {
            Edge edge = edges.next();
            Node target = graph.getOtherNode( edge, node );
            // contract target with node - select its neighbors, think about it and shit, then convert it into loop with stack
        }
        return contractNode( graph, node );*/
    }

    /**
     * Performs s-t minimal cut and returns cut edges
     *
     * @param graph graph
     * @param treeNodes tree nodes without the core nodes
     * @param coreNodes core nodes of the tree
     * @param ringNodes ring nodes
     * @return cut edges
     */
    private <N extends Node, E extends Edge> Collection<Edge> minimalCut( Graph<N, E> graph, ElementContainer<N> treeNodes, ElementContainer<N> coreNodes, ElementContainer<N> ringNodes ) {
        // contract core into a single node s
        /* // SEE contractNode
        ElementContainer<Node> contractedNodes = new SetElementContainer<>();
        Queue<Node> nodeQueue = new LinkedList<>();
        nodeQueue.add( coreNodes.any() );
        Node contracted = new Node( graph.getNodesCount() );
        Stack<Node> stack = new Stack<>();
        stack.push( coreNodes.any() );
        while ( !stack.isEmpty() ) {

        }
        while ( !nodeQueue.isEmpty() ) {
            Node node = nodeQueue.poll();
            Iterator<Edge> edges = graph.getEdges( node );
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node target = graph.getOtherNode( edge, node );
                Iterator<Edge> es2 = graph.getEdges( target );
                while ( es2.hasNext() ) {
                    Edge e2 = es2.next();
                    Node t2 = graph.getOtherNode( e2, target );
                    // add edges
                    // contract
                }
                // contract
            }
        }
         */

//        GraphStreamPresenter presenter = new GraphStreamPresenter();
//        presenter.displayGraph( graph );
//        for ( Node node : coreNodes ) {
//            presenter.setNodeColor( node.getId(), Color.red );
//        }
//        for ( Node node : treeNodes ) {
//            presenter.setNodeColor( node.getId(), Color.green );
//        }
//        for ( Node node : ringNodes ) {
//            presenter.setNodeColor( node.getId(), Color.blue );
//        }
//        System.out.println( "minimal cut" );
        Map<N, Set<E>> coreMap = contractNode( graph, coreNodes, coreNodes.any() );
//        System.out.println( "core map: " );
//        testPrintContractMap( coreMap );
        // contract ring into a single node t
        Map<N, Set<E>> ringMap = contractNode( graph, ringNodes, ringNodes.any() );
        // remove nodes from outside the area (egdes going outside the ring, not inside)
        Set<Map.Entry<N, Set<E>>> entrySet = ringMap.entrySet();
        Iterator<Map.Entry<N, Set<E>>> entrySetIterator = entrySet.iterator();
        while ( entrySetIterator.hasNext() ) {
            Map.Entry<N, Set<E>> entry = entrySetIterator.next();
            if ( !coreNodes.contains( entry.getKey() ) && !treeNodes.contains( entry.getKey() ) ) {
                entrySetIterator.remove();
            }
        }
//        System.out.println( "ring map: " );
//        testPrintContractMap( ringMap );
//        System.out.println( "nodes contracted" );
        // build a new graph (temporary)
        // copy tree into new graph
        Map<Long, ContractNode> nodeMap = new HashMap<>();
        Map<Long, ContractEdge> edgeMap = new HashMap<>();
        for ( N treeNode : treeNodes ) {
            Set<Node> set = new HashSet<>();
            set.add( treeNode );
            ContractNode newNode = new ContractNode( treeNode.getId(), CollectionUtils.asSet( (Node) treeNode ) );
            nodeMap.put( newNode.getId(), newNode );
            Iterator<E> edgeIterator = treeNode.getEdges( graph );
            while ( edgeIterator.hasNext() ) {
                E e = edgeIterator.next();
                N t = graph.getOtherNode( e, treeNode );
                if ( nodeMap.containsKey( t.getId() ) ) {
                    ContractNode newTargetNode = nodeMap.get( t.getId() );
                    ContractEdge newEdge = new ContractEdge( e.getId(), false, newNode, newTargetNode, CollectionUtils.asSet( (Edge) e ) );
                    edgeMap.put( newEdge.getId(), newEdge );
                    newNode.addEdge( newEdge );
                    newTargetNode.addEdge( newEdge );
                }
            }
        }
        // find new ids
        long maxEdgeId = 0;
        Iterator<E> graphEdges = graph.getEdges();
        while ( graphEdges.hasNext() ) {
            maxEdgeId = Math.max( maxEdgeId, graphEdges.next().getId() );
        }
        long maxNodeId = 0;
        Iterator<N> graphNodes = graph.getNodes();
        while ( graphNodes.hasNext() ) {
            maxNodeId = Math.max( maxNodeId, graphNodes.next().getId() );
        }
        // add contracted nodes
        ContractNode coreNode = new ContractNode( ++maxNodeId, new HashSet<Node>() );
        nodeMap.put( coreNode.getId(), coreNode );
        for ( Map.Entry<N, Set<E>> entry : coreMap.entrySet() ) {
            if ( nodeMap.containsKey( entry.getKey().getId() ) ) {
                ContractNode newTargetNode = nodeMap.get( entry.getKey().getId() );
                ContractEdge edge = new ContractEdge( coreNode.getId() * 100 + newTargetNode.getId(), false, coreNode, newTargetNode, (Collection<Edge>) entry.getValue() );
                coreNode.addEdge( edge );
                newTargetNode.addEdge( edge );
                edgeMap.put( edge.getId(), edge );
            }
        }
        ContractNode ringNode = new ContractNode( ++maxNodeId, new HashSet<Node>() );
        nodeMap.put( ringNode.getId(), ringNode );
        for ( Map.Entry<N, Set<E>> entry : ringMap.entrySet() ) {
            if ( nodeMap.containsKey( entry.getKey().getId() ) ) {
                ContractNode newTargetNode = nodeMap.get( entry.getKey().getId() );
                ContractEdge edge = new ContractEdge( ringNode.getId() * 100 + newTargetNode.getId(), false, ringNode, newTargetNode, (Collection<Edge>) entry.getValue() );
                ringNode.addEdge( edge );
                newTargetNode.addEdge( edge );
                edgeMap.put( edge.getId(), edge );
            }
//            else if ( coreNodes.contains( entry.getKey() ) ) {
//                Node newTargetNode = coreNode;
//                List<Edge> list = entry.getValue();
//                List<Edge> otherList = coreMap.get( entry.getKey() );
//                if ( list.size() > otherList.size() ) {
//                    list = otherList;
//                }
//                Edge edge = new ContractEdge( ringNode.getId() * 100 + newTargetNode.getId(), false, ringNode, newTargetNode, Distance.newInstance( list.size() ), list );
//                ringNode.addEdge( edge );
//                newTargetNode.addEdge( edge );
//                edgeMap.put( edge.getId(), edge );
//            }
        }
        Set<E> coreToRingEdges = new HashSet<>();
        for ( Map.Entry<N, Set<E>> entry : coreMap.entrySet() ) {
            if ( ringNodes.contains( entry.getKey() ) ) {
                coreToRingEdges.addAll( entry.getValue() );
            }
        }
        Set<E> ringToCoreEdges = new HashSet<>();
        for ( Map.Entry<N, Set<E>> entry : ringMap.entrySet() ) {
            if ( coreNodes.contains( entry.getKey() ) ) {
                ringToCoreEdges.addAll( entry.getValue() );
            }
        }
        Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
        Map<Edge, Distance> distanceMap = new HashMap<>();
        metricMap.put( Metric.SIZE, distanceMap );
        if ( coreToRingEdges.size() <= ringToCoreEdges.size() ) {
            ContractEdge edge = new ContractEdge( coreNode.getId() * 100 + ringNode.getId(), false, coreNode, ringNode, (Collection<Edge>) coreToRingEdges );
            ringNode.addEdge( edge );
            coreNode.addEdge( edge );
            edgeMap.put( edge.getId(), edge );
        } else {
            ContractEdge edge = new ContractEdge( ringNode.getId() * 100 + coreNode.getId(), false, ringNode, coreNode, (Collection<Edge>) ringToCoreEdges );
            ringNode.addEdge( edge );
            coreNode.addEdge( edge );
            edgeMap.put( edge.getId(), edge );
        }

//        Map<Node, Node> visitedNodes = new HashMap<>();
//        Queue<Node> nodeQueue = new LinkedList<>();
//        for ( Node node : coreMap.keySet() ) {
//            nodeQueue.add( node );
//        }
//        for ( Node node : ringMap.keySet() ) {
//            nodeQueue.add( node );
//        }
//        // add all the nodes into builder
//        System.out.println( "adding nodes to builder" );
//        while ( !nodeQueue.isEmpty() ) {
//            Node node = nodeQueue.poll();
//            Iterator<Edge> edges = node.getEdges();
//            Node newNode = new Node( node.getId() );
//            newNode.setCoordinate( node.getCoordinate() );
//            visitedNodes.put( newNode, newNode );
//            builder.node( newNode );
//            while ( edges.hasNext() ) {
//                Edge edge = edges.next();
//                Node target = graph.getOtherNode( edge, node );
//                if ( visitedNodes.containsKey( target ) ) {
//                    Node actualTarget = visitedNodes.get( target );
//                    Edge e = new ContractEdge( edge.getId(), false, newNode, actualTarget, Distance.newInstance( EDGE_INIT_SIZE ), Arrays.asList( edge ) );
//                    actualTarget.addEdge( e );
//                    newNode.addEdge( e );
//                    builder.edge( e );
//                } else if ( treeNodes.contains( target ) ) {
//                    nodeQueue.add( target );
//                }
//            }
//        }
//        System.out.println( "searching for ids" );
//        System.out.println( "adding connections to groups" );
//        // add connection to core node
//        Node coreNode = new Node( ++maxNodeId );
//        builder.node( coreNode );
//        for ( Map.Entry<Node, List<Edge>> entry : coreMap.entrySet() ) {
//            Node actualTarget = visitedNodes.get( entry.getKey() );
//            Edge edge = new ContractEdge( ++maxEdgeId, false, coreNode, actualTarget, Distance.newInstance( entry.getValue().size() ), entry.getValue() );
//            actualTarget.addEdge( edge );
//            coreNode.addEdge( edge );
//            builder.edge( edge );
//        }
//        // add connection to ring node
//        Node ringNode = new Node( ++maxNodeId );
//        builder.node( ringNode );
//        for ( Map.Entry<Node, List<Edge>> entry : ringMap.entrySet() ) {
//            Node actualTarget = visitedNodes.get( entry.getKey() );
//            Edge edge = new ContractEdge( ++maxEdgeId, false, ringNode, actualTarget, Distance.newInstance( entry.getValue().size() ), entry.getValue() );
//            actualTarget.addEdge( edge );
//            ringNode.addEdge( edge );
//            builder.edge( edge );
//        }
        Graph tmpGraph = new UndirectedGraph( GraphUtils.toMap( nodeMap.values() ), GraphUtils.toMap( edgeMap.values() ), metricMap );
        for ( ContractEdge value : edgeMap.values() ) {
            distanceMap.put( value, Distance.newInstance( value.calculateWidth( tmpGraph ) ) );
        }
//        System.out.println( "temporary graph: " + tmpGraph );
//        presenter = new GraphStreamPresenter();
//        presenter.displayGraph( tmpGraph );
//        for ( Node node : treeNodes ) {
//            presenter.setNodeColor( node.getId(), Color.green );
//        }
//        presenter.setNodeColor( coreNode.getId(), Color.red );
//        presenter.setNodeColor( ringNode.getId(), Color.blue );
//        System.out.println( "performing s-t cuts" );
        // build temporary graph
        // perform s-t minimal cut algorithm between them (on the tree)
        MinimalCutAlgorithm minimalCutAlgorithm = new FordFulkersonMinimalCut();
        // TODO graph must contain source and target
        MinimalCut<ContractEdge> cut = minimalCutAlgorithm.compute( tmpGraph, Metric.SIZE, coreNode, ringNode );
//        System.out.println( "returning result: " + cut );
//        for ( Edge cutEdge : cut.getCutEdges() ) {
//            presenter.setEdgeColor( cutEdge.getId(), Color.red );
//        }

        // map minimal cut back to original edges
        Set<Edge> cutEdges = new HashSet<>();
        for ( ContractEdge cutEdge : cut.getCutEdges() ) {
            ContractEdge e = cutEdge;
            cutEdges.addAll( e.getEdges() );
        }
//        Set<Edge> cutEdges = new HashSet<>();
//        for ( Node node : coreNodes ) {
//            Iterator<Edge> edges = node.getEdges();
//            while ( edges.hasNext() ) {
//                Edge edge = edges.next();
//                Node target = graph.getOtherNode( edge, node );
//                if ( ringNodes.contains( target ) ) {
//                    cutEdges.add( edge );
//                }
//            }
//        }
        return cutEdges;
    }

    private <N extends Node, E extends Edge> SplitGraphMessenger splitGraph( Graph<N, E> graph, ElementContainer<E> cutEdges ) {
//        System.out.println( "Splitting graph" );
        Queue<N> nodeQueue = new LinkedList<>();
//        System.out.println( "initializing" );
        NodeSizeContainer nodeSizeContainer = new MapNodeSizeContainer();
        for ( N node : graph.getNodes() ) {
            nodeSizeContainer.put( node, NODE_INIT_SIZE );
        }
        // contract each region (connected component) into a single node calles "fragment", preserve connections (without duplicates)
        // - determine fragments
//        System.out.println( "determine fragments" );
        int fragment = 0;
        TObjectIntMap<N> fragmentMap = new TObjectIntHashMap<>();
        List<N> fragmentCenterMap = new ArrayList<>();
        List<Set<N>> fragmentOrigNodes = new ArrayList<>();
        ElementContainer<N> nodeContainer = new SetElementContainer<>();
        nodeContainer.addAll( graph.getNodes() );
        TIntList sizeMap = new TIntArrayList();
        // -- foreach node
        while ( !nodeContainer.isEmpty() ) {
            fragmentOrigNodes.add( new HashSet<N>() );
            N center = nodeContainer.any();
            fragmentCenterMap.add( center );
            nodeQueue.add( center );
            int sum = 0;
//            System.out.println( "search connected component for node: " + center.getId() );
            // -- search connected component
            while ( !nodeQueue.isEmpty() ) {
                N node = nodeQueue.poll();
                // -- mark each reached node as a part of current fragment
                fragmentMap.put( node, fragment );
                fragmentOrigNodes.get( fragment ).add( node );
                sum += nodeSizeContainer.getSize( node );
                nodeContainer.remove( node );
                // -- repeat for all its neighbors, do not consider cut edges
                for ( E edge : graph.getEdges( node ) ) {
                    if ( !cutEdges.contains( edge ) ) {
                        N target = graph.getOtherNode( edge, node );
                        if ( !fragmentMap.containsKey( target ) ) {
//                            System.out.println( "adding to queue: #" + target.getId() );
                            nodeQueue.add( target );
                        }
                    }
                }
            }
//            System.out.println( fragment + "->[" + fragmentOrigNodes.get( fragment ) + "]" );
            sizeMap.add( sum );
//            System.out.println( "size = " + sum );
            fragment++;
        }
        // - determine neighboring areas
        TIntSet[] fragmentNeighbors = new TIntSet[fragment];
        List<Map<Integer, Set<E>>> origEdgesMapList = new ArrayList<>();
        // -- foreach fragment
//        System.out.println( "determine neighbors" );
        for ( int i = 0; i < fragment; i++ ) {
            origEdgesMapList.add( new HashMap<Integer, Set<E>>() );
            fragmentNeighbors[i] = new TIntHashSet();
            N center = fragmentCenterMap.get( i );
            nodeQueue.add( center );
            // -- find all neighbors
            ElementContainer<N> visitedNodes = new SetElementContainer<>();
            while ( !nodeQueue.isEmpty() ) {
                N node = nodeQueue.poll();
                visitedNodes.add( node );
                // -- for all neighbors, if they are connected via regular edge, repeat for them, if via cut edge, add fragment neighbor
                for ( E edge : graph.getEdges( node ) ) {
                    N target = graph.getOtherNode( edge, node );
                    if ( !cutEdges.contains( edge ) ) { // continue searching
                        if ( !visitedNodes.contains( target ) ) {
                            nodeQueue.add( target );
                        }
                    } else { // add fragment neighbor
//                        System.out.println( "adding neighbor: " + node.getId() + "[" + fragmentMap.get( node ) + "] -> " + target.getId() + "[" + fragmentMap.get( target ) + "]" );
                        CollectionUtils.getSet( origEdgesMapList.get( i ), fragmentMap.get( target ) ).add( edge );
                        fragmentNeighbors[i].add( fragmentMap.get( target ) );
                    }
                }
            }
        }
        return new SplitGraphMessenger<>( fragmentOrigNodes, origEdgesMapList );
    }

    private <N extends Node, E extends Edge> FilteredGraph buildFilteredGraph( List<Set<N>> fragmentOrigNodes, List<Map<Integer, Set<E>>> origEdgesMapList ) {
//        Map<Node, Set<Node>> origNodes = new HashMap<>();
//        Map<Edge, Set<Edge>> origEdges = new HashMap<>();
        List<ContractNode> nodes = new ArrayList<>();
        List<ContractEdge> edges = new ArrayList<>();
        int nodeCounter = 0;
        for ( Set<N> fragmentOrigNode : fragmentOrigNodes ) {
            ContractNode node = new ContractNode( nodeCounter++, (Collection<Node>) fragmentOrigNode );
//            origNodes.put( node, fragmentOrigNode );
            nodes.add( node );
        }
        int fragmentCounter = 0;
        int edgeCounter = 0;
        Map<Metric, Map<Edge, Distance>> metricMap = new HashMap<>();
        Map<Edge, Distance> distanceMap = new HashMap();
        metricMap.put( Metric.SIZE, distanceMap );
        for ( Map<Integer, Set<E>> map : origEdgesMapList ) {
//            System.out.println( "map..." );
            for ( Map.Entry<Integer, Set<E>> entry : map.entrySet() ) {
//                System.out.println( "entry..." );
                int targetFragment = entry.getKey();
//                System.out.println( targetFragment  + " < " + fragmentCounter + " ?" );
                if ( targetFragment < fragmentCounter ) {
                    ContractNode source = nodes.get( fragmentCounter );
                    ContractNode target = nodes.get( targetFragment );
//                    System.out.println( "edge: #" + source.getId() + " -> #" + target.getId() );
                    ContractEdge edge = new ContractEdge( edgeCounter++, false, source, target, (Collection<Edge>) entry.getValue() );
                    distanceMap.put( edge, Distance.newInstance( entry.getValue().size() ) );
                    source.addEdge( edge );
                    target.addEdge( edge );
//                    origEdges.put( edge, new HashSet<>( entry.getValue() ) );
                    edges.add( edge );
                }
            }
            fragmentCounter++;
        }
//        for ( Node node : nodes ) {
//            node.lock();
//        }

        return new FilteredGraph( GraphUtils.toMap( nodes ), GraphUtils.toMap( edges ), metricMap );

//        List<Node> nodes = new ArrayList<>();
//        TObjectIntMap<Node> nodeSizeMap = new TObjectIntHashMap<>();
//        for ( int i = 0; i < fragmentNeighbors.length; i++ ) {
//            Node node = new Node( i );
//            nodes.add( node );
//            nodeSizeMap.put( node, fragmentSizeMap[i] );
//        }
//        int edgeCounter = 0;
//        List<Edge> edges = new ArrayList<>();
//        for ( int i = 0; i < fragmentNeighbors.length; i++ ) {
//            TIntSet fragmentNeighbor = fragmentNeighbors[i];
//            TIntIterator iterator = fragmentNeighbor.iterator();
//            while ( iterator.hasNext() ) {
//                int neighbor = iterator.next();
//                if ( neighbor > i ) { // has not been added yet
//                    Edge edge = new Edge( edgeCounter++, false, nodes.get( i ), nodes.get( neighbor ), Distance.newInstance( 1.0 ) );
//                    nodes.get( i ).addEdge( edge );
//                    nodes.get( neighbor ).addEdge( edge );
//                    edges.add( edge );
//                }
//            }
//            nodes.get( i ).lock();
//        }
//        return new FilteredGraph( UndirectedGraph.builder().nodes( nodes ).edges( edges ).build(), nodeSizeMap );
    }

//    private int getEdgeInitSize( SimpleEdge edge ) {
//        if ( edge.isOneWay() ) {
//            return EDGE_INIT_SIZE;
//        } else {
//            return 2 * EDGE_INIT_SIZE;
//        }
//    }
    @Value
    private static class SplitGraphMessenger<N extends Node, E extends Edge> {

        List<Set<N>> fragmentOrigNodes;
        List<Map<Integer, Set<E>>> origEdgesMapList;
    }

    private static void testPrintContractMap( Map<SimpleNode, Set<SimpleEdge>> contractedMap ) {
        System.out.println( "CONTRACTED MAP" );
        for ( Map.Entry<SimpleNode, Set<SimpleEdge>> entry : contractedMap.entrySet() ) {
            System.out.println( entry.getKey().getId() + " => " + toStringEdges( entry.getValue() ) );
        }
    }

    private static String toStringEdges( Collection<SimpleEdge> edges ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( SimpleEdge edge : edges ) {
            sb.append( edge.getId() ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
    }
}

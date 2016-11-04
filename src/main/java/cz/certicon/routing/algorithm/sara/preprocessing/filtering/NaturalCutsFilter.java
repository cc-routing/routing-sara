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
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.preprocessing.ContractNode;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
import cz.certicon.routing.model.basic.IdSupplier;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.RandomUtils;
import cz.certicon.routing.utils.collections.CollectionUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;

/**
 * Implementation of the {@link Filter} interface. Uses Natural cuts technique
 * for reducing the graph size and dividing it into separate components
 * <p>
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
 * <p>
 * Contract each component from GC=(V,E\C) on the origin graph G ***************
 * Return G ********************************************************************
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
    @NonFinal
    private int maxCellSize; // U

    /**
     * Creates new instance
     *
     * @param cellRatio        portion of U (max cell size) defining the size of
     *                         fragment, alpha, 0 &lt;= alpha &lt;= 1
     * @param coreRatioInverse divisor defining the core size, core size =
     *                         alpha*U/f, this is f
     * @param maxCellSize      maximal size of a fragment, U
     */
    public NaturalCutsFilter( double cellRatio, double coreRatioInverse, int maxCellSize ) {
        this.cellRatio = cellRatio;
        this.coreRatioInverse = coreRatioInverse;
        this.maxCellSize = maxCellSize;
    }

    @Override
    public <N extends Node<N, E>, E extends Edge<N, E>> ContractGraph filter( Graph<N, E> graph ) {
//        GraphStreamPresenter presenter = new GraphStreamPresenter();
//        presenter.displayGraph( graph );
        // NOTE: contraction
        // - foreach node
        // -- remove node
        // -- preserve paths (create edges between all the neighbors)
//        System.out.println( "started filtering" );
        Set<E> cutEdges = getCutEdges( graph );
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
        ContractGraph filteredGraph = buildFilteredGraph( splitGraphResult.getFragmentOrigNodes(), splitGraphResult.getOrigEdgesMapList() );
//        System.out.println( "graph built" );

//        presenter = new GraphStreamPresenter();
//        presenter.displayGraph( filteredGraph );
//        presenter = new GraphStreamPresenter();
//        presenter.displayGraph( graph );
        // nodeSizes // splitGraph.fragmentSizeMap
        return filteredGraph;
    }

    private <N extends Node<N, E>, E extends Edge<N, E>> Set<E> getCutEdges( Graph<N, E> graph ) {
        // init structures
        Set<E> cutEdges = new HashSet<>();
        Set<N> coreNodes = new HashSet<>();
        Set<N> ringNodes = new HashSet<>();
        Set<N> treeNodes = new HashSet<>();
        TObjectIntMap<N> nodeSizeContainer = new TObjectIntHashMap<>();
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
        Random random = RandomUtils.createRandom();
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
                int size = nodeSizeContainer.get( node );
                // all the nodes added to tree before it reached cellRatio * maxCellSize / coreRatioInverse form a "core"
                if ( sum + size <= cellRatio * maxCellSize / coreRatioInverse ) {
                    sum += size;
                    coreNodes.add( node );
//                    System.out.println( "adding core node: " + node );
                    // other nodes in the tree form the area where minimal cut will be performed
                } else if ( sum + size <= cellRatio * maxCellSize ) {
                    sum += size;
                    treeNodes.add( node );
//                    System.out.println( "adding tree node: " + node );
                } else {
                    // direct neighors to the tree form a "ring"
//                    System.out.println( "adding ring node: " + node );
                    ringNodes.add( node );
//                    nodeQueue.clear();
                    continue;
                }
                for ( E edge : node.getEdges(  ) ) {
                    N target = edge.getOtherNode( node );
//                        System.out.println( "neighbor = " + edge );
                    if ( !coreNodes.contains( target ) && !ringNodes.contains( target ) && !treeNodes.contains( target ) ) {
                        nodeQueue.add( target );
                    }
                }
            }
//            System.out.println( "adding minimal cut" );
            // mark edges from minimal cut as "cut edges"
            cutEdges.addAll( minimalCut( graph, treeNodes, coreNodes, ringNodes ) );
//            System.out.println( "core nodes: " + coreNodes.size() );
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

    private <N extends Node> ContractNode contractNode( Graph<ContractNode, ContractEdge> graph, Set<N> nodeGroup, IdSupplier nodeIdSupplier, IdSupplier edgeIdSupplier ) {
        // TODO must contract only neighbors! while nodeGroup notEmpty contractnode with first neighbor
        Set<ContractNode> nodeSet = new HashSet<>();
        for ( N n : nodeGroup ) {
            nodeSet.add( graph.getNodeById( n.getId() ) );
        }
        ContractNode node = null;
        while ( !nodeSet.isEmpty() ) {
            if ( node == null ) {
                node = nodeSet.iterator().next();
                nodeSet.remove( node );
            } else {
                boolean foundNeighbor = false;
                for ( ContractEdge edge : node.getEdges(  ) ) {
                    ContractNode target = edge.getOtherNode( node );
                    if ( nodeSet.contains( target ) ) {
                        node = node.mergeWith( target, nodeIdSupplier, edgeIdSupplier );
                        nodeSet.remove( target );
                        foundNeighbor = true;
                        break;
                    }
                }
                if ( !foundNeighbor ) {
                    ContractNode target = nodeSet.iterator().next();
                    node = node.mergeWith( target, nodeIdSupplier, edgeIdSupplier );
                    nodeSet.remove( target );
                }
            }
        }
        return node;
    }

    private <N extends Node<N, E>, E extends Edge<N, E>> void fillMap( ContractGraph graph, Set<N> container ) {
        for ( N treeNode : container ) {
            ContractNode node = graph.createNode( treeNode.getId(), Arrays.asList( (Node) treeNode ) );
        }
    }

    private <N extends Node> void fillMap( ContractGraph graph, Set<N> treeNodes, Set<N> coreNodes, Set<N> ringNodes, Set<N> container ) {
        for ( N node : container ) {
            for ( Object e : node.getEdges() ) {
                Edge edge = (Edge) e;
                N otherNode = (N) edge.getOtherNode( node );
                if ( !graph.containsEdge( edge.getId() ) && ( treeNodes.contains( otherNode ) || coreNodes.contains( otherNode ) || ringNodes.contains( otherNode ) ) ) {
                    ContractNode source = graph.getNodeById( node.getId() );
                    ContractNode target = graph.getNodeById( otherNode.getId() );
                    ContractEdge contractEdge = graph.createEdge( edge.getId(), false, source, target, Arrays.asList( (Edge) edge ) );
                }
            }
        }
    }

    /**
     * Performs s-t minimal cut and returns cut edges
     *
     * @param graph     graph
     * @param treeNodes tree nodes without the core nodes
     * @param coreNodes core nodes of the tree
     * @param ringNodes ring nodes
     * @return cut edges
     */
    private <N extends Node<N, E>, E extends Edge<N, E>> Collection<E> minimalCut( Graph<N, E> graph, Set<N> treeNodes, Set<N> coreNodes, Set<N> ringNodes ) {
        // create a temporary graph
        ContractGraph tmpGraph = new ContractGraph( EnumSet.of( Metric.SIZE ) );
        fillMap( tmpGraph, treeNodes );
        fillMap( tmpGraph, coreNodes );
        fillMap( tmpGraph, ringNodes );
        fillMap( tmpGraph, treeNodes, coreNodes, ringNodes, treeNodes );
        fillMap( tmpGraph, treeNodes, coreNodes, ringNodes, coreNodes );
        fillMap( tmpGraph, treeNodes, coreNodes, ringNodes, ringNodes );
        for ( ContractEdge edge : tmpGraph.getEdges() ) {
            edge.setLength( Metric.SIZE, Distance.newInstance( edge.calculateWidth() ) );
        }
//        System.out.println( "tmpgraph [BEFORE]: " + tmpGraph );
//        DisplayUtils.display( graph, Arrays.asList( treeNodes, coreNodes, ringNodes ) );
        // contract core nodes and ring nodes
        // - find new ids
        long maxEdgeId = 0;
        Iterator<E> graphEdges = graph.getEdges();
        while ( graphEdges.hasNext() ) {
            maxEdgeId = Math.max( maxEdgeId, graphEdges.next().getId() );
        }
        IdSupplier edgeIdSupplier = new IdSupplier( maxEdgeId );
        long maxNodeId = 0;
        Iterator<N> graphNodes = graph.getNodes();
        while ( graphNodes.hasNext() ) {
            maxNodeId = Math.max( maxNodeId, graphNodes.next().getId() );
        }
        IdSupplier nodeIdSupplier = new IdSupplier( maxNodeId );
        // - contract core
        ContractNode core = contractNode( tmpGraph, coreNodes, nodeIdSupplier, edgeIdSupplier );
        // - contract ring
        ContractNode ring = contractNode( tmpGraph, ringNodes, nodeIdSupplier, edgeIdSupplier );
        // perform minimal cut from core to ring
        MinimalCutAlgorithm minimalCutAlgorithm = new FordFulkersonMinimalCut();
        MinimalCut<ContractEdge> cut = minimalCutAlgorithm.compute( tmpGraph, Metric.SIZE, core, ring );
        // map result back to the original graph
        Set<E> cutEdges = new HashSet<>();
        for ( ContractEdge cutEdge : cut.getCutEdges() ) {
            ContractEdge e = cutEdge;
            for ( Edge ed : e.getEdges() ) {
                cutEdges.add( (E) ed );
            }
        }
        // return cut edges
        return cutEdges;
    }

    private <N extends Node<N, E>, E extends Edge<N, E>> SplitGraphMessenger splitGraph( Graph<N, E> graph, Set<E> cutEdges ) {
//        System.out.println( "Splitting graph" );
        Queue<N> nodeQueue = new LinkedList<>();
//        System.out.println( "initializing" );
        TObjectIntMap<N> nodeSizeContainer = new TObjectIntHashMap<>();
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
        Set<N> nodeContainer = new HashSet<>();
        for ( N node : graph.getNodes() ) {
            nodeContainer.add( node );
        }
        TIntList sizeMap = new TIntArrayList();
        // -- foreach node
        while ( !nodeContainer.isEmpty() ) {
            fragmentOrigNodes.add( new HashSet<N>() );
            N center = nodeContainer.iterator().next();
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
                sum += nodeSizeContainer.get( node );
                nodeContainer.remove( node );
                // -- repeat for all its neighbors, do not consider cut edges
                for ( E edge : node.getEdges(  ) ) {
                    if ( !cutEdges.contains( edge ) ) {
                        N target = edge.getOtherNode( node );
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
            Set<N> visitedNodes = new HashSet<>();
            while ( !nodeQueue.isEmpty() ) {
                N node = nodeQueue.poll();
                visitedNodes.add( node );
                // -- for all neighbors, if they are connected via regular edge, repeat for them, if via cut edge, add fragment neighbor
                for ( E edge : node.getEdges(  ) ) {
                    N target = edge.getOtherNode( node );
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

    private <N extends Node<N, E>, E extends Edge<N, E>> ContractGraph buildFilteredGraph( List<Set<N>> fragmentOrigNodes, List<Map<Integer, Set<E>>> origEdgesMapList ) {
//        Map<Node, Set<Node>> origNodes = new HashMap<>();
//        Map<Edge, Set<Edge>> origEdges = new HashMap<>();
        List<ContractNode> nodes = new ArrayList<>();
        List<ContractEdge> edges = new ArrayList<>();
        ContractGraph graph = new ContractGraph( EnumSet.of( Metric.SIZE ) );
        int nodeCounter = 0;
        for ( Set<N> fragmentOrigNode : fragmentOrigNodes ) {
            ContractNode node = graph.createNode( nodeCounter++, fragmentOrigNode );
            nodes.add( node );
        }
        int fragmentCounter = 0;
        int edgeCounter = 0;
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
                    ContractEdge edge = graph.createEdge( edgeCounter++, false, source, target, entry.getValue(),
                            new Pair<>( Metric.SIZE, Distance.newInstance( entry.getValue().size() ) ) );
//                    origEdges.put( edge, new HashSet<>( entry.getValue() ) );
                }
            }
            fragmentCounter++;
        }
        return graph;
//        for ( Node node : nodes ) {
//            node.lock();
//        }

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

    @Override
    public void setMaxCellSize( int maxCellSize ) {
        this.maxCellSize = maxCellSize;
    }

    //    private int getEdgeInitSize( SimpleEdge edge ) {
//        if ( edge.isOneWay() ) {
//            return EDGE_INIT_SIZE;
//        } else {
//            return 2 * EDGE_INIT_SIZE;
//        }
//    }
    private static class SplitGraphMessenger<N extends Node<N, E>, E extends Edge<N, E>> {

        private final List<Set<N>> fragmentOrigNodes;
        private final List<Map<Integer, Set<E>>> origEdgesMapList;

        public SplitGraphMessenger( List<Set<N>> fragmentOrigNodes, List<Map<Integer, Set<E>>> origEdgesMapList ) {
            this.fragmentOrigNodes = fragmentOrigNodes;
            this.origEdgesMapList = origEdgesMapList;
        }

        public List<Set<N>> getFragmentOrigNodes() {
            return fragmentOrigNodes;
        }

        public List<Map<Integer, Set<E>>> getOrigEdgesMapList() {
            return origEdgesMapList;
        }

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

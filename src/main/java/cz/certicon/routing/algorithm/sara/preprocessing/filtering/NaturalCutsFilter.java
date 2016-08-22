/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.algorithm.FordFulkersonMinimalCut;
import cz.certicon.routing.algorithm.MinimalCutAlgorithm;
import cz.certicon.routing.model.MinimalCut;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.CollectionUtils;
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
import java.util.Set;
import java.util.Stack;
import lombok.Value;
import lombok.experimental.Wither;

/**
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
    public FilteredGraph filter( Graph graph ) {
        // NOTE: contraction
        // - foreach node
        // -- remove node
        // -- preserve paths (create edges between all the neighbors)
        ElementContainer<Edge> cutEdges = getCutEdges( graph );
        // split graph into regions bounded by the cut edges
        SplitGraphMessenger splitGraphResult = splitGraph( graph, cutEdges );
        // build new filtered graph
        FilteredGraph filteredGraph = buildFilteredGraph( splitGraphResult.getFragmentNeighbors(), splitGraphResult.getFragmentSizeMap() );
        // nodeSizes // splitGraph.fragmentSizeMap
        return filteredGraph;
    }

    private ElementContainer<Edge> getCutEdges( Graph graph ) {
        // init structures
        ElementContainer<Edge> cutEdges = new SetElementContainer<>();
        ElementContainer<Node> coreNodes = new SetElementContainer<>();
        ElementContainer<Node> ringNodes = new SetElementContainer<>();
        ElementContainer<Node> treeNodes = new SetElementContainer<>();
        NodeSizeContainer nodeSizeContainer = new MapNodeSizeContainer();
        NodeSizeContainer nodeOrderContainer = new MapNodeSizeContainer();
        Queue<Node> nodeQueue = new LinkedList<>();
        RandomSet<Node> randomNodes = new MixRandomSet<>( graph.getNodesCount() );
        // TODO need structure which allows random pick and fast element removal (target element)
        // conside using set and converting to array or iterator for random pick - how many random picks???
        Iterator<Node> nodeIterator = graph.getNodes();
        while ( nodeIterator.hasNext() ) {
            Node node = nodeIterator.next();
            randomNodes.add( node );
            nodeSizeContainer.put( node, NODE_INIT_SIZE );
        }
        Random random = new Random();
        // until there are no nodes left
        while ( !randomNodes.isEmpty() ) {
            // pick a node (=center) at random (a node that does not belong to any core)
            Node center = randomNodes.pollRandom( random );
            // create tree T via BFS from center at maximal size of cellRatio * maxCellSize, where size is a sum of tree's nodes' sizes
            nodeQueue.add( center );
            // NOTE: what is node size? 1 at the beginning, then sum of contracted nodes inside this node
            int sum = nodeSizeContainer.getSize( center );
            while ( !nodeQueue.isEmpty() ) {
                Node node = nodeQueue.poll();
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
                Iterator<Edge> edges = graph.getEdges( node );
                while ( edges.hasNext() ) {
                    Edge edge = edges.next();
                    Node target = graph.getOtherNode( edge, node );
                    if ( !coreNodes.contains( target ) && !ringNodes.contains( target ) ) {
                        nodeQueue.add( target );
                    }
                }
            }
            // mark edges from minimal cut as "cut edges"
            cutEdges.addAll( minimalCut( graph, treeNodes, coreNodes, ringNodes ) );
            // remove all core nodes from the queue
            for ( Node coreNode : coreNodes ) {
                randomNodes.remove( coreNode );
            }
            coreNodes.clear();
            ringNodes.clear();
        }
        return cutEdges;
    }

    private Map<Node, List<Edge>> contractNode( Graph graph, ElementContainer<Node> nodeGroup, Node node ) {
        /*
         * Pseudocontract - selects only border nodes and creates evaluated edges, ignores inner nodes
         * TODO Correct - should contract the usual way and save information about inner edges per each shortcut, also should be able to unpack this shortcut and find the actual cut edge
         */
        Map<Node, List<Edge>> targets = new HashMap<>();
        for ( Node singleNode : nodeGroup ) {
            Iterator<Edge> edges = singleNode.getEdges();
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node target = graph.getOtherNode( edge, singleNode );
                if ( !nodeGroup.contains( target ) ) {
                    List<Edge> list = CollectionUtils.getList( targets, target );
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
    private Collection<Edge> minimalCut( Graph graph, ElementContainer<Node> treeNodes, ElementContainer<Node> coreNodes, ElementContainer<Node> ringNodes ) {
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
        Map<Node, List<Edge>> coreMap = contractNode( graph, coreNodes, coreNodes.any() );
        // contract ring into a single node t
        Map<Node, List<Edge>> ringMap = contractNode( graph, ringNodes, ringNodes.any() );
        // build a new graph (temporary)
        UndirectedGraph.UndirectedGraphBuilder builder = UndirectedGraph.builder();
        Map<Node, Node> visitedNodes = new HashMap<>();
        Queue<Node> nodeQueue = new LinkedList<>();
        for ( Node node : coreMap.keySet() ) {
            nodeQueue.add( node );
        }
        // add all the nodes into builder
        while ( !nodeQueue.isEmpty() ) {
            Node node = nodeQueue.poll();
            Iterator<Edge> edges = node.getEdges();
            Node newNode = new Node( node.getId() );
            newNode.setCoordinate( node.getCoordinate() );
            visitedNodes.put( newNode, newNode );
            builder.node( newNode );
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node target = graph.getOtherNode( edge, node );
                if ( visitedNodes.containsKey( target ) ) {
                    Node actualTarget = visitedNodes.get( target );
                    Edge e = new ContractEdge( edge.getId(), false, newNode, actualTarget, Distance.newInstance( EDGE_INIT_SIZE ), Arrays.asList( edge ) );
                    actualTarget.addEdge( e );
                    newNode.addEdge( e );
                    builder.edge( e );
                } else if ( treeNodes.contains( target ) ) {
                    nodeQueue.add( target );
                }
            }
        }
        // find new ids
        long maxNodeId = 0;
        Iterator<Node> graphNodes = graph.getNodes();
        while ( graphNodes.hasNext() ) {
            maxNodeId = Math.max( maxNodeId, graphNodes.next().getId() );
        }
        long maxEdgeId = 0;
        Iterator<Edge> graphEdges = graph.getEdges();
        while ( graphEdges.hasNext() ) {
            maxEdgeId = Math.max( maxEdgeId, graphEdges.next().getId() );
        }
        // add connection to core node
        Node coreNode = new Node( ++maxNodeId );
        for ( Map.Entry<Node, List<Edge>> entry : coreMap.entrySet() ) {
            Node actualTarget = visitedNodes.get( entry.getKey() );
            Edge edge = new ContractEdge( ++maxEdgeId, false, coreNode, actualTarget, Distance.newInstance( entry.getValue().size() ), entry.getValue() );
            actualTarget.addEdge( edge );
            coreNode.addEdge( edge );
            builder.edge( edge );
        }
        // add connection to ring node
        Node ringNode = new Node( ++maxNodeId );
        for ( Map.Entry<Node, List<Edge>> entry : ringMap.entrySet() ) {
            Node actualTarget = visitedNodes.get( entry.getKey() );
            Edge edge = new ContractEdge( ++maxEdgeId, false, ringNode, actualTarget, Distance.newInstance( entry.getValue().size() ), entry.getValue() );
            actualTarget.addEdge( edge );
            ringNode.addEdge( edge );
            builder.edge( edge );
        }
        // perform s-t minimal cut algorithm between them (on the tree)
        MinimalCutAlgorithm minimalCutAlgorithm = new FordFulkersonMinimalCut();
        MinimalCut cut = minimalCutAlgorithm.compute( graph, coreNode, ringNode );
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
        return cut.getCutEdges();
    }

    private SplitGraphMessenger splitGraph( Graph graph, ElementContainer<Edge> cutEdges ) {
        Queue<Node> nodeQueue = new LinkedList<>();
        NodeSizeContainer nodeSizeContainer = new MapNodeSizeContainer();
        Iterator<Node> nodeIterator = graph.getNodes();
        while ( nodeIterator.hasNext() ) {
            nodeSizeContainer.put( nodeIterator.next(), NODE_INIT_SIZE );
        }
        // contract each region (connected component) into a single node calles "fragment", preserve connections (without duplicates)
        // - determine fragments
        int fragment = 0;
        TObjectIntMap<Node> fragmentMap = new TObjectIntHashMap<>();
        TIntObjectMap<Node> fragmentCenterMap = new TIntObjectHashMap<>();
        ElementContainer<Node> nodeContainer = new SetElementContainer<>();
        nodeContainer.addAll( graph.getNodes() );
        TIntList sizeMap = new TIntArrayList();
        // -- foreach node
        while ( !nodeContainer.isEmpty() ) {
            Node center = nodeContainer.any();
            fragmentCenterMap.put( fragment, center );
            nodeQueue.add( center );
            int sum = 0;
            // -- search connected component
            while ( !nodeQueue.isEmpty() ) {
                Node node = nodeQueue.poll();
                // -- mark each reached node as a part of current fragment
                fragmentMap.put( node, fragment );
                sum += nodeSizeContainer.getSize( node );
                nodeContainer.remove( node );
                Iterator<Edge> edges = graph.getEdges( node );
                // -- repeat for all its neighbors, do not consider cut edges
                while ( edges.hasNext() ) {
                    Edge edge = edges.next();
                    if ( !cutEdges.contains( edge ) ) {
                        Node target = graph.getOtherNode( edge, node );
                        nodeQueue.add( target );
                    }
                }
            }
            sizeMap.add( sum );
            fragment++;
        }
        // - determine neighboring areas
        TIntSet[] fragmentNeighbors = new TIntSet[fragment];
        // -- foreach fragment
        for ( int i = 0; i < fragment; i++ ) {
            fragmentNeighbors[i] = new TIntHashSet();
            Node center = fragmentCenterMap.get( i );
            nodeQueue.add( center );
            // -- find all neighbors
            while ( !nodeQueue.isEmpty() ) {
                Node node = nodeQueue.poll();
                Iterator<Edge> edges = graph.getEdges( node );
                // -- for all neighbors, if they are connected via regular edge, repeat for them, if via cut edge, add fragment neighbor
                while ( edges.hasNext() ) {
                    Edge edge = edges.next();
                    Node target = graph.getOtherNode( edge, node );
                    if ( !cutEdges.contains( edge ) ) { // continue searching
                        nodeQueue.add( target );
                    } else { // add fragment neighbor
                        fragmentNeighbors[i].add( fragmentMap.get( target ) );
                    }
                }
            }
        }
        return new SplitGraphMessenger( fragmentNeighbors, sizeMap.toArray() );
    }

    private FilteredGraph buildFilteredGraph( TIntSet[] fragmentNeighbors, int[] fragmentSizeMap ) {
        List<Node> nodes = new ArrayList<>();
        TObjectIntMap<Node> nodeSizeMap = new TObjectIntHashMap<>();
        for ( int i = 0; i < fragmentNeighbors.length; i++ ) {
            Node node = new Node( i );
            nodes.add( node );
            nodeSizeMap.put( node, fragmentSizeMap[i] );
        }
        int edgeCounter = 0;
        List<Edge> edges = new ArrayList<>();
        for ( int i = 0; i < fragmentNeighbors.length; i++ ) {
            TIntSet fragmentNeighbor = fragmentNeighbors[i];
            TIntIterator iterator = fragmentNeighbor.iterator();
            while ( iterator.hasNext() ) {
                int neighbor = iterator.next();
                if ( neighbor > i ) { // has not been added yet
                    Edge edge = new Edge( edgeCounter++, false, nodes.get( i ), nodes.get( neighbor ), Distance.newInstance( 1.0 ) );
                    nodes.get( i ).addEdge( edge );
                    nodes.get( neighbor ).addEdge( edge );
                    edges.add( edge );
                }
            }
            nodes.get( i ).lock();
        }
        return new FilteredGraph( UndirectedGraph.builder().nodes( nodes ).edges( edges ).build(), nodeSizeMap );
    }

    @Value
    private static class SplitGraphMessenger {

        TIntSet[] fragmentNeighbors;
        int[] fragmentSizeMap;
    }

    private static class ContractEdge extends Edge {

        private final List<Edge> edges;

        public ContractEdge( long id, boolean oneway, Node source, Node target, Distance length, List<Edge> edges ) {
            super( id, oneway, source, target, length );
            this.edges = edges;
        }

        public ContractEdge mergeWith( ContractEdge edge, long id ) {
            List<Edge> newEdges = new ArrayList<>( this.edges );
            newEdges.addAll( edge.edges );
            return new ContractEdge( id, isOneway(), getSource(), getTarget(), getLength().add( edge.getLength() ), newEdges );
        }

    }
}

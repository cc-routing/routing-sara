/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.UndirectedGraph;
import cz.certicon.routing.model.graph.preprocessing.FilteredGraph;
import cz.certicon.routing.model.values.Distance;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import lombok.Value;
import lombok.experimental.Wither;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class NaturalCutsFilter implements Filter {

    private static final int NODE_INIT_SIZE = 1;

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
        NodeSizeContainer nodeSizeContainer = new MapNodeSizeContainer();
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
                    // other nodes in the tree form a "ring"
                } else if ( sum + size <= cellRatio * maxCellSize ) {
                    sum += size;
                    ringNodes.add( node );
                } else {
                    nodeQueue.clear();
                    break;
                }
                Iterator<Edge> edges = graph.getEdges( node );
                while ( edges.hasNext() ) {
                    Edge edge = edges.next();
                    Node target = graph.getOtherNode( edge, node );
                    if ( !coreNodes.isContained( target ) && !ringNodes.isContained( target ) ) {
                        nodeQueue.add( target );
                    }
                }
            }
            // contract core into a single node s
            // contract ring into a single node t
            // perform s-t minimal cut algorithm between them
            // mark edges from minimal cut as "cut edges"
            cutEdges.addAll( minimalCut( graph, coreNodes, ringNodes ) );
            // remove all core nodes from the queue
            for ( Node coreNode : coreNodes ) {
                randomNodes.remove( coreNode );
            }
            coreNodes.clear();
            ringNodes.clear();
        }
        return cutEdges;
    }

    private Collection<Edge> minimalCut( Graph graph, ElementContainer<Node> coreNodes, ElementContainer<Node> ringNodes ) {
        Set<Edge> cutEdges = new HashSet<>();
        for ( Node coreNode : coreNodes ) {
            Iterator<Edge> edges = coreNode.getEdges();
            while ( edges.hasNext() ) {
                Edge edge = edges.next();
                Node target = graph.getOtherNode( edge, coreNode );
                if ( ringNodes.isContained( target ) ) {
                    cutEdges.add( edge );
                }
            }
        }
        return cutEdges;
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
                    if ( !cutEdges.isContained( edge ) ) {
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
                    if ( !cutEdges.isContained( edge ) ) { // continue searching
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
}

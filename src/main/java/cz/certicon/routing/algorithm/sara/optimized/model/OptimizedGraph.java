package cz.certicon.routing.algorithm.sara.optimized.model;

import cz.certicon.routing.utils.EffectiveUtils;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;
import lombok.ToString;

/**
 * Created by blaha on 26.10.2016.
 */
@ToString
public class OptimizedGraph {

    private final TLongIntMap nodeMap = new TLongIntHashMap();
    private final TLongIntMap edgeMap = new TLongIntHashMap();

    private long[] nodeIds = new long[0];

    private int[] sources = new int[0];
    private int[] targets = new int[0];
    private boolean[] oneways = new boolean[0];

    public OptimizedGraph() {
    }

    public OptimizedGraph( int nodeCount, int edgeCount ) {
        enlargeNodeCapacityBy( nodeCount );
        enlargeEdgeCapacityBy( edgeCount );
    }

    public final void enlargeNodeCapacityBy( int size ) {
        nodeIds = enlarge( nodeIds, size );
    }

    public final void enlargeEdgeCapacityBy( int size ) {
        sources = enlarge( sources, size );
        targets = enlarge( targets, size );
        oneways = enlarge( oneways, size );
    }

    public int createNode( long id ) {
        int idx = nodeMap.size();
        nodeIds[idx] = id;
        nodeMap.put( id, idx );
        return idx;
    }

    public int createEdge( long id, long source, long target, boolean oneway ) {
        int idx = edgeMap.size();
        edgeMap.put( id, idx );
        sources[idx] = getNodeById( source );
        targets[idx] = getNodeById( target );
        oneways[idx] = oneway;
        return idx;
    }

    public boolean containsNodeId( long id ) {
        return nodeMap.containsKey( id );
    }

    public int getNodeCount() {
        return nodeMap.size();
    }

    public long[] getNodeIds() {
        return nodeMap.keys( new long[nodeMap.size()] );
    }

    public boolean containsEdgeId( long id ) {
        return edgeMap.containsKey( id );
    }

    public int getEdgeCount() {
        return edgeMap.size();
    }

    public long[] getEdgeIds() {
        return edgeMap.keys( new long[edgeMap.size()] );
    }

    public int getSource( int edgeIdx ) {
        return sources[edgeIdx];
    }

    public int getTarget( int edgeIdx ) {
        return targets[edgeIdx];
    }

    public int getNodeById( long id ) {
        return nodeMap.get( id );
    }

    public int getEdgeById( long id ) {
        return edgeMap.get( id );
    }

    public long getNodeId( int idx ) {
        return nodeIds[idx];
    }

    public boolean isOneway( int edgeIdx ) {
        return oneways[edgeIdx];
    }

    private static long[] enlarge( long[] orig, int size ) {
        long[] newArray = new long[orig.length + size];
        EffectiveUtils.copyArray( orig, newArray );
        return newArray;
    }

    private static int[] enlarge( int[] orig, int size ) {
        int[] newArray = new int[orig.length + size];
        EffectiveUtils.copyArray( orig, newArray );
        return newArray;
    }

    private static boolean[] enlarge( boolean[] orig, int size ) {
        boolean[] newArray = new boolean[orig.length + size];
        EffectiveUtils.copyArray( orig, newArray );
        return newArray;
    }
}

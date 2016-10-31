package cz.certicon.routing.algorithm.sara.optimized.model;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Metric;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;
import lombok.ToString;

import java.util.*;

import static cz.certicon.routing.utils.EffectiveUtils.*;

/**
 * Created by blaha on 26.10.2016.
 */
@ToString
public class OptimizedGraph {

    private final TLongIntMap nodeMap = new TLongIntHashMap();
    private final TLongIntMap edgeMap = new TLongIntHashMap();

    private long[] nodeIds = new long[0];
    private long[] edgeIds = new long[0];

    private int[] sources = new int[0];
    private int[] targets = new int[0];
    private boolean[] oneways = new boolean[0];
    private Map<Metric, float[]> lengths = new EnumMap<Metric, float[]>( Metric.class ) {{
        for ( Metric metric : Metric.values() ) {
            put( metric, new float[0] );
        }
    }};
    private int[][] outgoingEdges = new int[0][0];
    private int[][] incomingEdges = new int[0][0];

    public OptimizedGraph() {
    }

    public OptimizedGraph( int nodeCount, int edgeCount ) {
        enlargeNodeCapacityBy( nodeCount );
        enlargeEdgeCapacityBy( edgeCount );
    }

    public final void enlargeNodeCapacityBy( int size ) {
        nodeIds = enlarge( nodeIds, size );
        outgoingEdges = enlarge( outgoingEdges, size );
        incomingEdges = enlarge( incomingEdges, size );
    }

    public final void enlargeEdgeCapacityBy( int size ) {
        edgeIds = enlarge( edgeIds, size );
        sources = enlarge( sources, size );
        targets = enlarge( targets, size );
        oneways = enlarge( oneways, size );
        for ( Metric metric :
                Metric.values() ) {
            lengths.put( metric, enlarge( lengths.get( metric ), size ) );
        }
    }

    public int createNode( long id ) {
        int idx = nodeMap.size();
        nodeIds[idx] = id;
        nodeMap.put( id, idx );
        outgoingEdges[idx] = new int[0];
        incomingEdges[idx] = new int[0];
        return idx;
    }

    public int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx, Pair<Metric, Double>... distances ) {
        List<Pair<Metric, Double>> distanceList = new ArrayList<>();
        for ( Pair<Metric, Double> pair : distances ) {
            distanceList.add( pair );
        }
        return createEdge( id, source, target, oneway, sourceTableIdx, targetTableIdx, distanceList );
    }

    public int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx, Collection<Pair<Metric, Double>> distances ) {
        int idx = edgeMap.size();
        edgeIds[idx] = id;
        edgeMap.put( id, idx );
        int sourceIdx = getNodeById( source );
        sources[idx] = sourceIdx;
        int targetIdx = getNodeById( target );
        targets[idx] = targetIdx;
        oneways[idx] = oneway;
        outgoingEdges[sourceIdx] = enlargeToIndex( outgoingEdges[sourceIdx], sourceTableIdx );
        outgoingEdges[sourceIdx][sourceTableIdx] = idx;
        incomingEdges[targetIdx] = enlargeToIndex( incomingEdges[targetIdx], targetTableIdx );
        incomingEdges[targetIdx][targetTableIdx] = idx;
        for ( Pair<Metric, Double> pair : distances ) {
            lengths.get( pair.a )[idx] = pair.b.floatValue();
        }
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

    public long getNodeId( int nodeIdx ) {
        return nodeIds[nodeIdx];
    }

    public long getEdgeId( int edgeIdx ) {
        return edgeIds[edgeIdx];
    }

    public boolean isOneway( int edgeIdx ) {
        return oneways[edgeIdx];
    }

    public void setLength( int edgeIdx, Metric metric, float length ) {
        lengths.get( metric )[edgeIdx] = length;
    }

    public float getLength( int edgeIdx, Metric metric ) {
        return lengths.get( metric )[edgeIdx];
    }

    public int[] getOutgoingEdges( int nodeIdx ) {
        return outgoingEdges[nodeIdx];
    }

    public int[] getIncomingEdges( int nodeIdx ) {
        return incomingEdges[nodeIdx];
    }

    public int getOtherNode( int edgeIdx, int nodeIdx ) {
        int source = sources[edgeIdx];
        return source != nodeIdx ? source : targets[edgeIdx];
    }

    private static int[] enlargeToIndex( int[] array, int index ) {
        return array.length <= index ? enlarge( array, index - array.length + 1 ) : array;
    }

}

package cz.certicon.routing.algorithm.sara.optimized.model;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.utils.EffectiveUtils;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;
import lombok.ToString;

import java.util.*;

import static cz.certicon.routing.utils.EffectiveUtils.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
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
    private int[][] edges = new int[0][0];

    private int[] sourceTableIndices = new int[0];
    private int[] targetTableIndices = new int[0];

    private float[][][] turnTables = new float[0][0][0];

    public OptimizedGraph() {
    }

    public OptimizedGraph( int nodeCount, int edgeCount ) {
        enlargeNodeCapacityBy( nodeCount );
        enlargeEdgeCapacityBy( edgeCount );
    }

    public final void enlargeNodeCapacityBy( int size ) {
        nodeIds = enlarge( nodeIds, size );
        edges = enlarge( edges, size );
        turnTables = enlarge( turnTables, size );
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
        sourceTableIndices = enlarge( sourceTableIndices, size );
        targetTableIndices = enlarge( targetTableIndices, size );
    }

    public int createNode( long id ) {
        return createNode( id, null );
    }

    public int createNode( long id, float[][] turnTable ) {
        int idx = nodeMap.size();
        nodeIds[idx] = id;
        nodeMap.put( id, idx );
        edges[idx] = new int[0];
        turnTables[idx] = turnTable;
        return idx;
    }

    /*********************** OPTIMIZATION CREATE_EDGE METHODS ***********************/

    public int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx ) {
        return createEdge( id, source, target, oneway, sourceTableIdx, targetTableIdx, null );
    }

    public int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx, Metric metric1, float distance1 ) {
        int idx = createEdge( id, source, target, oneway, sourceTableIdx, targetTableIdx );
        lengths.get( metric1 )[idx] = distance1;
        return idx;
    }

    public int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx, Metric metric1, float distance1, Metric metric2, float distance2 ) {
        int idx = createEdge( id, source, target, oneway, sourceTableIdx, targetTableIdx );
        lengths.get( metric1 )[idx] = distance1;
        lengths.get( metric2 )[idx] = distance2;
        return idx;
    }

    public int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx, Metric metric1, float distance1, Metric metric2, float distance2, Metric metric3, float distance3 ) {
        int idx = createEdge( id, source, target, oneway, sourceTableIdx, targetTableIdx );
        lengths.get( metric1 )[idx] = distance1;
        lengths.get( metric2 )[idx] = distance2;
        lengths.get( metric3 )[idx] = distance3;
        return idx;
    }

    @SafeVarargs
    public final int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx, Pair<Metric, Float> firstDistance, Pair<Metric, Float>... distances ) {
        List<Pair<Metric, Float>> distanceList = new ArrayList<>();
        distanceList.add( firstDistance );
        for ( Pair<Metric, Float> pair : distances ) {
            distanceList.add( pair );
        }
        return createEdge( id, source, target, oneway, sourceTableIdx, targetTableIdx, distanceList );
    }

    /*********************** END OF OPTIMIZATION CREATE_EDGE METHODS ***********************/

    public int createEdge( long id, long source, long target, boolean oneway, int sourceTableIdx, int targetTableIdx, Collection<Pair<Metric, Float>> distances ) {
        int idx = edgeMap.size();
        edgeIds[idx] = id;
        edgeMap.put( id, idx );
        int sourceIdx = getNodeById( source );
        sources[idx] = sourceIdx;
        int targetIdx = getNodeById( target );
        targets[idx] = targetIdx;
        oneways[idx] = oneway;
        edges[sourceIdx] = enlargeToIndex( edges[sourceIdx], sourceTableIdx );
        edges[sourceIdx][sourceTableIdx] = idx;
        edges[targetIdx] = enlargeToIndex( edges[targetIdx], targetTableIdx );
        edges[targetIdx][targetTableIdx] = idx;
//        if ( oneway && turnTables[targetIdx] != null ) {
//            float[][] newTable = new float[turnTables[targetIdx].length][turnTables[targetIdx].length];
//            EffectiveUtils.copyArray( turnTables[targetIdx], newTable );
//            for ( int i = 0; i < newTable.length; i++ ) {
//                newTable[i][targetTableIdx] = Float.POSITIVE_INFINITY;
//            }
//            turnTables[targetIdx] = newTable;
//            System.out.println( "new tables created: " + ++tblChangeCounter );
//        }
        sourceTableIndices[idx] = sourceTableIdx;
        targetTableIndices[idx] = targetTableIdx;
        if ( distances != null ) {
            for ( Pair<Metric, Float> pair : distances ) {
                lengths.get( pair.a )[idx] = pair.b;
            }
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
        if ( nodeMap.containsKey( id ) ) {
            return nodeMap.get( id );
        } else {
            throw new IllegalArgumentException( "Unknown node id: " + id );
        }
    }

    public int getEdgeById( long id ) {
        if ( edgeMap.containsKey( id ) ) {
            return edgeMap.get( id );
        } else {
            throw new IllegalArgumentException( "Unknown edge id: " + id );
        }
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

    public float getDistance( int edgeIdx, Metric metric ) {
        return lengths.get( metric )[edgeIdx];
    }

    public int[] getEdges( int nodeIdx ) {
        return edges[nodeIdx];
    }

    public int getOtherNode( int edgeIdx, int nodeIdx ) {
        int source = sources[edgeIdx];
        return source != nodeIdx ? source : targets[edgeIdx];
    }

    private static int[] enlargeToIndex( int[] array, int index ) {
        return array.length <= index ? enlarge( array, index - array.length + 1 ) : array;
    }

    public float getTurnDistance( int nodeIdx, int edgeFromIdx, int edgeToIdx ) {
        if ( turnTables[nodeIdx] == null ) {
            return 0;
        }
        if ( edgeFromIdx < 0 ) {
            // if oneway and i'm the target, then return infinity, otherwise return 0
            return ( oneways[edgeToIdx] && targets[edgeToIdx] == nodeIdx ) ? Float.MAX_VALUE : 0;
        }
        // sourceTableIndices contains index of the edge on its source, both enter and exit
        int sourceIdx = ( nodeIdx == sources[edgeFromIdx] ) ? sourceTableIndices[edgeFromIdx] : targetTableIndices[edgeFromIdx];
        int targetIdx = ( nodeIdx == sources[edgeToIdx] ) ? sourceTableIndices[edgeToIdx] : targetTableIndices[edgeToIdx];
        return turnTables[nodeIdx][sourceIdx][targetIdx];
    }

    public float[][] getTurnTable( int nodeIdx ) {
        return turnTables[nodeIdx];
    }
}

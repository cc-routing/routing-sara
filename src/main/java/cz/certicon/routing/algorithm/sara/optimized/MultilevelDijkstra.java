package cz.certicon.routing.algorithm.sara.optimized;

import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.algorithm.sara.optimized.model.Route;
import cz.certicon.routing.algorithm.sara.optimized.model.State;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import cz.certicon.routing.utils.EffectiveUtils;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java8.util.Optional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by blaha on 26.10.2016.
 */
public class MultilevelDijkstra {

    public Optional<Route> route( OptimizedGraph graph, long source, long target, Metric metric ) {
        int sourceIdx = graph.getNodeById( source );
        int targetIdx = graph.getNodeById( target );
        PriorityQueue<State> queue = new FibonacciHeap<>();
        TObjectDoubleMap<State> distanceMap = new TObjectDoubleHashMap<>();
        Map<State, State> predecessorMap = new HashMap<>();
        Set<State> closed = new HashSet<>();
        {
            State initState = new State( sourceIdx, -1 );
            queue.add( initState, 0 );
            distanceMap.put( initState, 0 );
        }
        State finalState = null;
        double finalDistance = Double.MAX_VALUE;
        while ( !queue.isEmpty() ) {
            State state = queue.extractMin();
            closed.add( state );
            double distance = distanceMap.get( state );
            if ( distance > finalDistance ) {
                queue.clear();
                break;
            }
            if ( state.node == targetIdx && distance < finalDistance ) {
                finalState = state;
                finalDistance = distance;
            }
            for ( int edge : graph.getOutgoingEdges( state.node ) ) {
                int targetNode = graph.getOtherNode( edge, state.node );
                State targetState = new State( targetNode, edge );
                if ( !closed.contains( targetState ) ) {
                    double currentDistance = distanceMap.containsKey( targetState ) ? distanceMap.get( targetState ) : Double.MAX_VALUE;
                    double alternativeDistance = distance + graph.getLength( edge, metric ) + ( state.isFirst() ? 0 : graph.getTurnDistance( state.node, state.edge, edge ) );
                    if ( alternativeDistance < currentDistance ) {
                        distanceMap.put( targetState, alternativeDistance );
                        queue.decreaseKey( targetState, alternativeDistance );
                        predecessorMap.put( targetState, state );
                    }
                }
            }
        }
        if ( finalState != null ) {
            Route.Builder builder = Route.builder();
            State state = finalState;
            while ( predecessorMap.containsKey( state ) && !state.isFirst() ) {
                builder.edge( graph.getEdgeId( state.edge ) );
                state = predecessorMap.get( state );
            }
            return Optional.of( builder.buildReverse() );
        }
        return Optional.empty();
    }
}

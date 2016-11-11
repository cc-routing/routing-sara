package cz.certicon.routing.algorithm.sara.optimized;

import cz.certicon.routing.algorithm.sara.optimized.model.OptimizedGraph;
import cz.certicon.routing.algorithm.sara.optimized.model.Route;
import cz.certicon.routing.algorithm.sara.optimized.model.State;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.queue.FibonacciHeap;
import cz.certicon.routing.model.queue.PriorityQueue;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java8.util.Optional;
import java8.util.function.Supplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by blaha on 26.10.2016.
 */
public class MultilevelDijkstra {

    private final Supplier<PriorityQueue<State>> queueSupplier;

    public MultilevelDijkstra() {
        this.queueSupplier = new Supplier<PriorityQueue<State>>() {
            @Override
            public PriorityQueue<State> get() {
                return new FibonacciHeap<>();
            }
        };
    }

    public MultilevelDijkstra( Supplier<PriorityQueue<State>> queueSupplier ) {
        this.queueSupplier = queueSupplier;
    }

    public Optional<Route> route( OptimizedGraph graph, long source, long target, Metric metric ) {
        int sourceIdx = graph.getNodeById( source );
        int targetIdx = graph.getNodeById( target );
        PriorityQueue<State> queue = queueSupplier.get();
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
            for ( int edge : graph.getEdges( state.node ) ) {
                int targetNode = graph.getOtherNode( edge, state.node );
                State targetState = new State( targetNode, edge );

                // obtain level node - node at maximal level, where source, targetNode and target are in different cells
                // if level node is found
                // - state = level node and edge
                // - travel in the given level
                // => this leads to setting level and traveling in this level
                // => what is level node?
                // otherwise
                // - travel in current level


                if ( !closed.contains( targetState ) ) {
                    double currentDistance = distanceMap.containsKey( targetState ) ? distanceMap.get( targetState ) : Double.MAX_VALUE;
                    double alternativeDistance = distance + graph.getDistance( edge, metric ) + graph.getTurnDistance( state.node, state.edge, edge );
                    if ( alternativeDistance < currentDistance ) {
                        distanceMap.put( targetState, alternativeDistance );
                        queue.decreaseKey( targetState, alternativeDistance );
                        predecessorMap.put( targetState, state );
                    }
                }
            }

            // **overlay queue**
            // graph = getGraph( level )
            // foreach edge
            // - obtain level node (see  above)
            // - if level is found
            // - - travel in level
            // - otherwise
            // - - travel in L0


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

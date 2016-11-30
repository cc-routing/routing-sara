package cz.certicon.routing.model;

import cz.certicon.routing.data.PointSearcher;
import cz.certicon.routing.model.basic.Trinity;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Distance;
import java8.util.Optional;
import lombok.NonNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class RoutingPoint<N extends Node<N, E>, E extends Edge<N, E>> {
    private final N node;
    private final E edge;
    private final Map<Metric, Distance> toSourceDistanceMap;
    private final Map<Metric, Distance> toTargetDistanceMap;

    private RoutingPoint( N node, E edge, Map<Metric, Distance> toSourceDistanceMap, Map<Metric, Distance> toTargetDistanceMap ) {
        this.node = node;
        this.edge = edge;
        this.toSourceDistanceMap = toSourceDistanceMap;
        this.toTargetDistanceMap = toTargetDistanceMap;
    }

    public static <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> of( @NonNull N node ) {
        return new RoutingPoint<>( node, null, new EnumMap<Metric, Distance>( Metric.class ), new EnumMap<Metric, Distance>( Metric.class ) );
    }

    public static <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> of( @NonNull E edge, @NonNull final Metric metric, @NonNull final Distance toSourceDistance, @NonNull final Distance toTargetDistance ) {
        return new RoutingPoint<>( null, edge, new EnumMap<Metric, Distance>( Metric.class ) {{
            put( metric, toSourceDistance );
        }}, new HashMap<Metric, Distance>() {{
            put( metric, toTargetDistance );
        }} );
    }


    public static <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> of( @NonNull E edge, final Metric metric1, @NonNull final Distance toSourceDistance1, @NonNull final Distance toTargetDistance1, @NonNull final Metric metric2, @NonNull final Distance toSourceDistance2, @NonNull final Distance toTargetDistance2 ) {
        return new RoutingPoint<>( null, edge, new EnumMap<Metric, Distance>( Metric.class ) {{
            put( metric1, toSourceDistance1 );
            put( metric2, toSourceDistance2 );
        }}, new HashMap<Metric, Distance>() {{
            put( metric1, toTargetDistance1 );
            put( metric2, toTargetDistance2 );
        }} );
    }

    public static <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> of( @NonNull E edge, final Metric metric1, @NonNull final Distance toSourceDistance1, @NonNull final Distance toTargetDistance1, @NonNull final Metric metric2, @NonNull final Distance toSourceDistance2, @NonNull final Distance toTargetDistance2, @NonNull final Metric metric3, @NonNull final Distance toSourceDistance3, @NonNull final Distance toTargetDistance3 ) {
        return new RoutingPoint<>( null, edge, new EnumMap<Metric, Distance>( Metric.class ) {{
            put( metric1, toSourceDistance1 );
            put( metric2, toSourceDistance2 );
            put( metric3, toSourceDistance3 );
        }}, new HashMap<Metric, Distance>() {{
            put( metric1, toTargetDistance1 );
            put( metric2, toTargetDistance2 );
            put( metric3, toTargetDistance3 );
        }} );
    }

    public static <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> of( @NonNull E edge, final Trinity<Metric, Distance, Distance>... metricStartEndTrinity ) {
        Map<Metric, Distance> toSourceDistanceMap = new EnumMap<>( Metric.class );
        Map<Metric, Distance> toTargetDistanceMap = new EnumMap<>( Metric.class );
        for ( Trinity<Metric, Distance, Distance> trinity : metricStartEndTrinity ) {
            toSourceDistanceMap.put( trinity.a, trinity.b );
            toTargetDistanceMap.put( trinity.a, trinity.c );
        }
        return new RoutingPoint<>( null, edge, toSourceDistanceMap, toTargetDistanceMap );
    }

    public static <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> of( @NonNull PointSearcher.PointSearchResult pointSearchResult, Set<Metric> metrics, @NonNull Graph<N, E> graph ) {
        if ( pointSearchResult.isCrossroad() ) {
            return of( graph.getNodeById( pointSearchResult.getNodeId() ) );
        }
        Map<Metric, Distance> toSourceDistanceMap = new EnumMap<>( Metric.class );
        Map<Metric, Distance> toTargetDistanceMap = new EnumMap<>( Metric.class );
        for ( Metric metric : metrics ) {
            toSourceDistanceMap.put( metric, pointSearchResult.getDistanceToSource( metric ) );
            toTargetDistanceMap.put( metric, pointSearchResult.getDistanceToTarget( metric ) );
        }
        return new RoutingPoint<>( null, graph.getEdgeById( pointSearchResult.getEdgeId() ), toSourceDistanceMap, toTargetDistanceMap );
    }


    public static <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> of( @NonNull E edge, Map<Metric, Distance> toSourceDistanceMap, @NonNull Map<Metric, Distance> toTargetDistanceMap ) {
        return new RoutingPoint<>( null, edge, new EnumMap<>( toSourceDistanceMap ), new EnumMap<>( toTargetDistanceMap ) );
    }

    public Optional<N> getNode() {
        return Optional.ofNullable( node );
    }

    public Optional<E> getEdge() {
        return Optional.ofNullable( edge );
    }

    public Optional<Distance> getDistanceToSource( Metric metric ) {
        return Optional.ofNullable( toSourceDistanceMap.get( metric ) );
    }

    public Optional<Distance> getDistanceToTarget( Metric metric ) {
        return Optional.ofNullable( toTargetDistanceMap.get( metric ) );
    }

    public boolean isCrossroad() {
        return node != null;
    }
}

package cz.certicon.routing.data;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface PointSearcher {

    /**
     * Based on given coordinates find the closest nodes and actual distances
     * (in kilometers) to them
     *
     * @param coordinates a geographical point specifying the approximate
     * location
     * @return an instance of {@link PointSearchResult} representing either a crossroad or an edge with distances to the closest point on the edge (from both source and target)
     * @throws java.io.IOException thrown when an error occurs while searching
     */
    PointSearchResult findClosestPoint( Coordinate coordinates, Set<Metric> metrics ) throws IOException;

    @Value
    class PointSearchResult {
        long edgeId;
        long nodeId;
        @Getter( AccessLevel.NONE)
        Map<Metric,Distance> distanceToSource;
        @Getter( AccessLevel.NONE)
        Map<Metric,Distance> distanceToTarget;

        public boolean isCrossroad(){
            return edgeId < 0;
        }

        public Distance getDistanceToSource(Metric metric){
            return distanceToSource.get( metric );
        }

        public Distance getDistanceToTarget(Metric metric){
            return distanceToTarget.get( metric );
        }
    }
}

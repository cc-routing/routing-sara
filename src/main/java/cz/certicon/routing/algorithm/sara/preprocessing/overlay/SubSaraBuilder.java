/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraEdge;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.SaraNode;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.model.values.Distance;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.List;

import java8.util.Optional;
import lombok.Getter;

/**
 * @author Blahoslav Potoček <potocek@merica.cz>
 *         Builder of the Sara SubGraph for the specific Cell. Applicable only for the
 *         Cells at L1.
 */
public class SubSaraBuilder extends BaseSubBuilder {

    /**
     * Auxiliary collection of sub nodes.
     */
    final private TLongObjectMap<SaraNode> subNodes;

    /**
     * Sara SubGraph, the result of the build.
     */
    @Getter
    SaraGraph subGraph;

    /**
     * @param table
     */
    public SubSaraBuilder( CellRouteTable table ) {
        super( table );

        this.subGraph = new SaraGraph( builder.metrics );
        this.subNodes = new TLongObjectHashMap<>();
    }

    /**
     * gets or adds subNode
     *
     * @param node L0 node
     * @return
     */
    private SaraNode checkNode( long edgeId, SaraNode node ) {

        long id = 0;

        if ( node.getParent() == this.cell ) {
            id = node.getId();
        } else {
            id = -edgeId;
        }

        if ( this.subNodes.containsKey( id ) ) {
            return this.subNodes.get( id );
        } else {

            SaraNode subNode = this.subGraph.createNode( id, node.getParent() );

            TurnTable turns = node.getTurnTable();
            subNode.setTurnTable( turns );

            Coordinate coord = node.getCoordinate();
            subNode.setCoordinate( coord );

            this.subNodes.put( id, subNode );

            return subNode;
        }
    }

    /**
     * adds subEdge derived from edge in L0 sara graph.
     *
     * @param edge edge in the full graph
     */
    public void addEdge( SaraEdge edge ) {

        long id = edge.getId();

        SaraNode source = edge.getSource();
        SaraNode target = edge.getTarget();

        SaraNode subSource = this.checkNode( id, source );
        SaraNode subTarget = this.checkNode( id, target );

        int sourceIdx = edge.getSourcePosition();
        int targetIdx = edge.getTargetPosition();
        boolean oneway = edge.isOneWay();

        SaraEdge subEdge = this.subGraph.createEdge( id, oneway, subSource, subTarget, sourceIdx, targetIdx );

        for ( Metric key : this.builder.metrics ) {
            Distance distance = edge.getLength( key );
            subEdge.setLength( key, distance );
        }
    }

    /**
     * builds Sara subGraph (only for cells at L1)
     */
    public void buildSubGraph() {
        // this.subGraph.lock(); TODO?
    }

    /**
     * finds route in L0 cell sub sara graph = shortcut in L1.
     *
     * @param sourceNodeId
     * @param targetNodeId
     * @param metric
     * @return route distance
     */
    public Optional<Route<SaraNode, SaraEdge>> route( long sourceNodeId, long targetNodeId, Metric metric ) {
        SaraNode source = this.subNodes.get( sourceNodeId );
        SaraNode target = this.subNodes.get( targetNodeId );

        Optional<Route<SaraNode, SaraEdge>> route = this.builder.oneToOne.route( this.subGraph, metric, source, target );

        return route;
    }

    /**
     * sum route distance
     *
     * @param edges
     * @param metric
     * @return route distance
     */
    public Distance sumDistance( List<SaraEdge> edges, Metric metric ) {
        Distance distance = Distance.newInstance( 0 );
        for ( int idx = 1; idx < edges.size() - 1; idx++ ) {
            SaraEdge edge = edges.get( idx );
            Distance value = edge.getLength( metric );
            distance = distance.add( value );
        }

        return distance;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.AbstractEdge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.TurnTable;
import cz.certicon.routing.model.values.Distance;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz> Edge for the OverlayGraph.
 * Always directed, no turn constrains.
 */
public class OverlayEdge extends AbstractEdge<OverlayNode, OverlayEdge> {

    public OverlayEdge(OverlayGraph graph, long id, OverlayNode source, OverlayNode target) {

        super(graph, id, true, source, target, -1, -1);
    }

    @Override
    public Distance getTurnDistance(OverlayNode node, TurnTable turnTable, OverlayEdge targetEdge) {
        return Distance.newInstance(0);
    }

    @Override
    protected OverlayEdge newInstance(Graph<OverlayNode, OverlayEdge> newGraph, long id, boolean oneway, OverlayNode newSource, OverlayNode newTarget, int sourceIndex, int targetIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

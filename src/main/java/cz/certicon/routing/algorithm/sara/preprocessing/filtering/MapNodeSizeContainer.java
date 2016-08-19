/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.model.graph.Node;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
class MapNodeSizeContainer implements NodeSizeContainer {

    private final TObjectIntMap<Node> map = new TObjectIntHashMap<>();

    @Override
    public void put( Node node, int size ) {
        map.put( node, size );
    }

    @Override
    public int getSize( Node node ) {
        return map.get( node );
    }

    @Override
    public void clear() {
        map.clear();
    }

}

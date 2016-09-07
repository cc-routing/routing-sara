/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.filtering;

import cz.certicon.routing.model.graph.Node;


/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
interface NodeSizeContainer<N extends Node> {

    void put( N node, int size );

    int getSize( N node );
    
    void clear();
}

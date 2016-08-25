/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.algorithm.sara.preprocessing.filtering.ElementContainer;
import cz.certicon.routing.model.graph.Node;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ToStringUtils {

    public static String toString( ElementContainer<Node> nodeContainer ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "{" );
        for ( Node node : nodeContainer ) {
            sb.append( node.getId() ).append( "," );
        }
        if ( sb.length() > 1 ) {
            sb.replace( sb.length() - 1, sb.length(), "}" );
        } else {
            sb.append( "}" );
        }
        return sb.toString();
    }
}

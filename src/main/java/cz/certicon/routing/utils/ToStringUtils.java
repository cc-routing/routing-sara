/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.graph.SimpleNode;

import java.util.Collection;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ToStringUtils {

    public static String toString( Collection<SimpleNode> nodeContainer ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "{" );
        for ( SimpleNode node : nodeContainer ) {
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

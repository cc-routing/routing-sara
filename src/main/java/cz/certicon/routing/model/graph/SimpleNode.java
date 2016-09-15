/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SimpleNode extends AbstractNode<SimpleNode,SimpleEdge> {

    SimpleNode( Graph<SimpleNode, SimpleEdge> graph, long id ) {
        super( graph, id );
    }

    private static String toString( List<SimpleEdge> list ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( int i = 0; i < list.size(); i++ ) {
            SimpleEdge edge = list.get( i );
            sb.append( edge != null ? edge.getId() : "null" ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
    }

    public static Comparator<SimpleNode> getIdComparator() {
        return new Comparator<SimpleNode>() {
            @Override
            public int compare( SimpleNode o1, SimpleNode o2 ) {
                return Long.compare( o1.getId(), o2.getId() );
            }
        };
    }
}

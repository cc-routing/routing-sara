/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.values.Distance;
import java.util.Comparator;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
@NonFinal
@EqualsAndHashCode( exclude = { "source", "target" } )
public class Edge implements Identifiable, Cloneable {

    long id;
    boolean oneway;
    /**
     * Source
     */
    @NonNull
    @Wither
    Node source;
    /**
     * Target
     */
    @NonNull
    @Wither
    Node target;
    @NonNull
    @Wither
    Distance length;

    public Node getOtherNode( Node node ) {
        if ( node.equals( source ) ) {
            return target;
        } else if ( node.equals( target ) ) {
            return source;
        }
        throw new IllegalArgumentException( "Edge does not contain given node: edge = " + this + ", node = " + node.getId() );
    }

    @Override
    public String toString() {
        return "Edge{id=" + id + ", oneway=" + oneway + ", length=" + length + ", source=Node{id=" + source.getId() + "}, target=Node{id=" + target.getId() + "}}";
    }

    public static Comparator<Edge> getIdComparator() {
        return new Comparator<Edge>() {
            @Override
            public int compare( Edge o1, Edge o2 ) {
                return Long.compare( o1.getId(), o2.getId() );
            }
        };
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
@ToString( exclude = "edges" )
public class Node {

    long id;
    @NonNull
    @Getter( AccessLevel.NONE )
    List<Edge> edges;

    public Iterator<Edge> getIncomingEdges() {
        throw new UnsupportedOperationException( "Not implemented yet." );
    }

    public Iterator<Edge> getOutgoingEdges() {
        throw new UnsupportedOperationException( "Not implemented yet." );
    }
}

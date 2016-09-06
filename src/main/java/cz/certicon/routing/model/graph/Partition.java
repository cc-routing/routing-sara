/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import cz.certicon.routing.utils.collections.Iterator;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
@EqualsAndHashCode( exclude = "nodes" )
public class Partition implements Identifiable {

    long id;

    @Getter( AccessLevel.NONE )
    Collection<Node> nodes;

    public Iterator<Node> getNodes() {
        return new ImmutableIterator<>( nodes.iterator() );
    }

    public int getNodesCount() {
        return nodes.size();
    }

}

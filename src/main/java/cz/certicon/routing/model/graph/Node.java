/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
@ToString( exclude = { "edges", "edgePositionMap" } )
@EqualsAndHashCode( exclude = { "edges", "edgePositionMap" } )
public class Node {

    long id;
    @NonNull
    @Getter( AccessLevel.NONE )
    ArrayList<Edge> edges;
    @NonNull
    @Getter( AccessLevel.NONE )
    Map<Edge, Integer> edgePositionMap;

    public Node( long id, ArrayList<Edge> edges ) {
        this.id = id;
        this.edges = edges;
        this.edgePositionMap = new HashMap<>();
        for ( int i = 0; i < edges.size(); i++ ) {
            edgePositionMap.put( edges.get( i ), i );
        }
    }

    public int getEdgePosition( Edge edge ) {
        return edgePositionMap.get( edge );
    }

    public Iterator<Edge> getIncomingEdges() {
        return new IncomingEdgeIterator( this );
    }

    public Iterator<Edge> getOutgoingEdges() {
        return new OutgoingEdgeIterator( this );
    }

    private abstract class FilteringEdgeIterator implements Iterator<Edge> {

        private final int last;
        private final Node node;
        private int position = -1;
        private int nextPosition = -1;

        public FilteringEdgeIterator( Node node ) {
            this.node = node;
            int tmpLast = -1;
            for ( int i = edges.size() - 1; i >= 0; i-- ) {
                Edge edge = edges.get( i );
                if ( isValid( node, edge ) ) {
                    tmpLast = i;
                    break;
                }
            }
            this.last = tmpLast;
        }

        @Override
        public boolean hasNext() {
            if ( position + 1 > last ) {
                return false;
            }
            if ( nextPosition < 0 ) {
                int tmpPosition = position + 1;
                Edge edge = edges.get( tmpPosition );
                while ( tmpPosition < edges.size() - 1 && !isValid( node, edge ) ) {
                    tmpPosition++;
                    edge = edges.get( tmpPosition );
                }
                if ( tmpPosition < edges.size() ) {
                    nextPosition = tmpPosition;
                    return true;
                } else {
                    position = tmpPosition;
                    return false;
                }
            } else {
                return true;
            }
        }

        @Override
        public Edge next() {
            if ( hasNext() ) {
                position = nextPosition;
                this.nextPosition = -1;
                return edges.get( position );
            } else {
                throw new IllegalStateException( "No more egdes: call hasNext before asking for next edge!" );
            }
        }

        abstract boolean isValid( Node node, Edge edge );

    }

    private class OutgoingEdgeIterator extends FilteringEdgeIterator {

        public OutgoingEdgeIterator( Node node ) {
            super( node );
        }

        @Override
        boolean isValid( Node node, Edge edge ) {
            return !edge.isOneway() || edge.getSource().equals( node );
        }

    }

    private class IncomingEdgeIterator extends FilteringEdgeIterator {

        public IncomingEdgeIterator( Node node ) {
            super( node );
        }

        @Override
        boolean isValid( Node node, Edge edge ) {
            return !edge.isOneway() || edge.getTarget().equals( node );
        }

    }
}

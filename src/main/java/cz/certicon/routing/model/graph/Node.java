/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.graph;

import cz.certicon.routing.model.Identifiable;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.NonFinal;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@EqualsAndHashCode( exclude = { "turnTable", "locked", "edges", "edgePositionMap" } )
public class Node implements Identifiable {

    @Getter
    private final long id;
    @NonFinal
    @Getter
    private TurnTable turnTable;
    @Getter
    private Coordinate coordinate;
//    @Wither
    private final ArrayList<Edge> edges;
    private final Map<Edge, Integer> edgePositionMap;
    private boolean locked = false;
    private boolean invalid = false;

    public Node( long id ) {
        this.id = id;
        this.edgePositionMap = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    public Node addEdge( @NonNull Edge edge ) {
        checkLock();
        if ( !edgePositionMap.containsKey( edge ) ) {
//            System.out.println( "adding edge: " + edge.getId() + " to " + toString( edges ) );
            edges.add( edge );
            edgePositionMap.put( edge, edges.size() - 1 );
//            System.out.println( "result: " + toString( edges ) );
        }
        return this;
    }

    public Node removeEdge( @NonNull Edge edge ) {
        checkLock();
        if ( !edgePositionMap.containsKey( edge ) ) {
            throw new IllegalArgumentException( "Cennot remove edge (does not exist): " + edge );
        }
        edges.remove( edge );
        edgePositionMap.remove( edge );
        invalid = true;
        return this;
    }

    public Node addEdge( @NonNull Edge edge, int position ) {
        checkLock();
        if ( !edgePositionMap.containsKey( edge ) ) {
//            System.out.println( "adding edge: " + edge.getId() + ", [" + position + "] to " + toString( edges ) );
            while ( edges.size() <= position ) {
                edges.add( null );
            }
            edges.set( position, edge );
            edgePositionMap.put( edge, position );
//            System.out.println( "result: " + toString( edges ) );
        }
        return this;
    }

    private void validatePositions() {
        if ( invalid ) {
            edgePositionMap.clear();
            for ( int i = 0; i < edges.size(); i++ ) {
                edgePositionMap.put( edges.get( i ), i );
            }
            invalid = false;
        }
    }

    public Node setTurnTable( TurnTable turnTable ) {
        checkLock();
        this.turnTable = turnTable;
        return this;
    }

    public Node setCoordinate( Coordinate coordinate ) {
        checkLock();
        this.coordinate = coordinate;
        return this;
    }

    public int getEdgePosition( Edge edge ) {
        validatePositions();
        if ( !edgePositionMap.containsKey( edge ) ) {
            StringBuilder sb = new StringBuilder();
            for ( Map.Entry<Edge, Integer> e : edgePositionMap.entrySet() ) {
                if ( edge.equals( e.getKey() ) ) {
                    sb.append( "contains=" ).append( edgePositionMap.containsKey( edge ) );
                    sb.append( "THIS_ONE_EQUALS:" );
                }
                sb.append( e.getKey() ).append( "=>" ).append( e.getValue() ).append( "," );
            }
            throw new IllegalArgumentException( "Unknown edge: " + edge + ", \n\tknown edges: " + sb );
        }
        return edgePositionMap.get( edge );
    }

    public Iterator<Edge> getIncomingEdges() {
        return new IncomingEdgeIterator( this );
    }

    public Iterator<Edge> getOutgoingEdges() {
        return new OutgoingEdgeIterator( this );
    }

    public Iterator<Edge> getEdges() {
        return new ImmutableIterator<>( edges.iterator() );
    }

    public int getDegree() {
        return edges.size();
    }

    private void checkLock() {
        if ( locked ) {
            throw new IllegalStateException( "This object is locked against modification." );
        }
    }

    public synchronized void lock() {
        this.locked = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for ( Edge edge : edges ) {
            sb.append( edge.getId() ).append( "," );
        }
        if ( sb.length() > 1 ) {
            sb.replace( sb.length() - 1, sb.length(), "}" );
        } else {
            sb.append( "}" );
        }
        String edgesString = sb.toString();
        sb = new StringBuilder();
        for ( Edge edge : edges ) {
            long neighborId = edge.getOtherNode( this ).getId();
            sb.append( neighborId ).append( "," );
        }
        if ( sb.length() > 1 ) {
            sb.replace( sb.length() - 1, sb.length(), "}" );
        } else {
            sb.append( "}" );
        }
        String neighborsString = sb.toString();
        sb = new StringBuilder();
        sb.append( "{" );
        validatePositions();
        for ( Map.Entry<Edge, Integer> entry : edgePositionMap.entrySet() ) {
            sb.append( entry.getKey().getId() ).append( "=>" ).append( entry.getValue() ).append( "," );
        }
        if ( sb.length() > 1 ) {
            sb.replace( sb.length() - 1, sb.length(), "}" );
        } else {
            sb.append( "}" );
        }
        String edgesMapString = sb.toString();
        return "Node{id=" + id + ", locked=" + locked + ", coordinate = " + coordinate + ", edges=" + edgesString + ", neighbors=" + neighborsString + ", turnTable=" + turnTable + ", edgePositionMap=" + edgesMapString + "}";
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
            return edge != null && ( !edge.isOneway() || edge.getSource().equals( node ) );
        }

    }

    private class IncomingEdgeIterator extends FilteringEdgeIterator {

        public IncomingEdgeIterator( Node node ) {
            super( node );
        }

        @Override
        boolean isValid( Node node, Edge edge ) {
            return edge != null && ( !edge.isOneway() || edge.getTarget().equals( node ) );
        }

    }

    private static String toString( List<Edge> list ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for ( int i = 0; i < list.size(); i++ ) {
            Edge edge = list.get( i );
            sb.append( edge != null ? edge.getId() : "null" ).append( "," );
        }
        sb.replace( sb.length() - 1, sb.length(), "]" );
        return sb.toString();
    }

    public static Comparator<Node> getIdComparator() {
        return new Comparator<Node>() {
            @Override
            public int compare( Node o1, Node o2 ) {
                return Long.compare( o1.getId(), o2.getId() );
            }
        };
    }
}

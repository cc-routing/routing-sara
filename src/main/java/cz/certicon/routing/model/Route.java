/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import cz.certicon.routing.utils.collections.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <N>
 * @param <E>
 */
@Value
public class Route<N extends Node, E extends Edge> {

    @Getter( AccessLevel.NONE )
    List<E> edges;
    N source;
    N target;

    public List<E> getEdgeList() {
        return new ArrayList<>( edges );
    }

    public Iterator<E> getEdges() {
        return new ImmutableIterator<>( edges.iterator() );
    }

    public Iterator<N> getNodes() {
        return new Iterator<N>() {
            private final java.util.Iterator<E> edgeIterator = edges.iterator();
            private N current = source;

            @Override
            public boolean hasNext() {
                return !current.equals( target );
            }

            @Override
            public N next() {
                E next = edgeIterator.next();
                current = (N) next.getOtherNode( current );
                return current;
            }

            @Override
            public java.util.Iterator<N> iterator() {
                return this;
            }
        };
    }

    public static <N extends Node, E extends Edge> RouteBuilder builder() {
        return new RouteBuilder<>();
    }

    public static class RouteBuilder<N extends Node, E extends Edge> {

        private Node source;
        private Node target;
        private final LinkedList<E> edges = new LinkedList<>();
        private boolean uturn = false;
        private boolean fixedStart = false;

        public RouteBuilder() {
        }

        public RouteBuilder setSource( N source ) {
            this.source = source;
            this.target = source;
            fixedStart = true;
            return this;
        }

        public RouteBuilder setTarget( N target ) {
            this.source = target;
            this.target = target;
            fixedStart = true;
            return this;
        }

        public RouteBuilder addAsLast( E edge ) {
            if ( source == null ) {
                source = edge.getSource();
                target = edge.getTarget();
                if ( edge.isOneWay() ) {
                    fixedStart = true;
                }
            } else if ( fixedStart ) {
                if ( target.equals( edge.getSource() ) ) {
                    target = edge.getTarget();
                } else if ( target.equals( edge.getTarget() ) && !edge.isOneWay() ) {
                    target = edge.getSource();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current target = " + target + ", edge = " + edge );
                }
            } else if ( uturn ) {
                if ( target.equals( edge.getSource() ) ) {
                    source = target;
                    target = edge.getTarget();
                } else if ( target.equals( edge.getTarget() ) && !edge.isOneWay() ) {
                    source = target;
                    target = edge.getSource();
                } else if ( source.equals( edge.getSource() ) ) {
                    target = edge.getTarget();
                } else if ( source.equals( edge.getTarget() ) && !edge.isOneWay() ) {
                    target = edge.getSource();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current target = " + target + ", edge = " + edge );
                }
                fixedStart = true;
                uturn = false;
            } else if ( ( target.equals( edge.getSource() ) && source.equals( edge.getTarget() ) ) || ( !edge.isOneWay() && target.equals( edge.getTarget() ) && source.equals( edge.getSource() ) ) ) {
                uturn = true;
            } else if ( target.equals( edge.getSource() ) ) {
                target = edge.getTarget();
                fixedStart = true;
            } else if ( target.equals( edge.getTarget() ) && !edge.isOneWay() ) {
                target = edge.getSource();
                fixedStart = true;
            } else if ( source.equals( edge.getSource() ) ) {
                source = target;
                target = edge.getTarget();
                fixedStart = true;
            } else if ( source.equals( edge.getTarget() ) && !edge.isOneWay() ) {
                source = target;
                target = edge.getSource();
                fixedStart = true;
            } else {
                throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
            }
            edges.addLast( edge );
            return this;
        }

        public RouteBuilder addAsFirst( E edge ) {
            if ( source == null ) {
                source = edge.getSource();
                target = edge.getTarget();
                if ( edge.isOneWay() ) {
                    fixedStart = true;
                }
            } else if ( fixedStart ) {
                if ( source.equals( edge.getTarget() ) ) {
                    source = edge.getSource();
                } else if ( source.equals( edge.getSource() ) && !edge.isOneWay() ) {
                    source = edge.getTarget();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
                }
            } else if ( uturn ) {
                if ( source.equals( edge.getTarget() ) ) {
                    target = source;
                    source = edge.getSource();
                } else if ( source.equals( edge.getSource() ) && !edge.isOneWay() ) {
                    target = source;
                    source = edge.getTarget();
                } else if ( target.equals( edge.getTarget() ) ) {
                    source = edge.getSource();
                } else if ( target.equals( edge.getSource() ) && !edge.isOneWay() ) {
                    source = edge.getTarget();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
                }
                fixedStart = true;
                uturn = false;
            } else if ( ( source.equals( edge.getTarget() ) && target.equals( edge.getSource() ) ) || ( !edge.isOneWay() && source.equals( edge.getSource() ) && target.equals( edge.getTarget() ) ) ) {
                uturn = true;
            } else if ( source.equals( edge.getTarget() ) ) {
                source = edge.getSource();
                fixedStart = true;
            } else if ( source.equals( edge.getSource() ) && !edge.isOneWay() ) {
                source = edge.getTarget();
                fixedStart = true;
            } else if ( target.equals( edge.getTarget() ) ) {
                target = source;
                source = edge.getSource();
                fixedStart = true;
            } else if ( target.equals( edge.getSource() ) && !edge.isOneWay() ) {
                target = source;
                source = edge.getTarget();
                fixedStart = true;
            } else {
                throw new IllegalArgumentException( "Unable to connect edges: current target = " + target + ", edge = " + edge );
            }
            edges.addFirst( edge );
            return this;
        }

        public Route build() {
            return new Route( new ArrayList<>( edges ), source, target );
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.SimpleNode;
import cz.certicon.routing.model.graph.SimpleEdge;
import cz.certicon.routing.model.values.Distance;
import cz.certicon.routing.utils.collections.ImmutableIterator;
import cz.certicon.routing.utils.collections.Iterator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 * Representation of route (result of the {@link cz.certicon.routing.algorithm.RoutingAlgorithm})
 *
 * @param <N> node type
 * @param <E> edge type
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Value
public class Route<N extends Node, E extends Edge> {

    @Getter( AccessLevel.NONE )
    List<E> edges;
    N source;
    N target;

    /**
     * Returns list of edges in the route
     *
     * @return list of edges in the route
     */
    public List<E> getEdgeList() {
        return new ArrayList<>( edges );
    }

    /**
     * Returns iterator for the list of edges in the route
     *
     * @return iterator for the list of edges in the route
     */
    public Iterator<E> getEdges() {
        return new ImmutableIterator<>( edges.iterator() );
    }

    /**
     * Returns iterator for the list of nodes in the route
     *
     * @return iterator for the list of nodes in the route
     */
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

            @Override
            public void remove() {
                throw new UnsupportedOperationException( "Remove not supported" );
            }
        };
    }

    /**
     * Calculates and returns length of this route for the given metric
     *
     * @param metric given metric
     * @return length of this route for the given metric
     */
    public Distance calculateDistance( Metric metric ) {
        Distance dist = Distance.newInstance( 0 );
        for ( E edge : edges ) {
            dist = dist.add( edge.getLength( metric ) );
        }
        return dist;
    }

    /**
     * Returns builder
     *
     * @param <N> node type
     * @param <E> edge type
     * @return builder
     */
    public static <N extends Node<N, E>, E extends Edge<N, E>> RouteBuilder builder() {
        return new RouteBuilder<>();
    }

    /**
     * {@link Route} builder.
     *
     * @param <N> node type
     * @param <E> edge type
     */
    public static class RouteBuilder<N extends Node, E extends Edge> {

        private Node source;
        private Node target;
        private final LinkedList<E> edges = new LinkedList<>();
        private boolean uturn = false;
        private boolean fixedStart = false;

        /**
         * Constructor
         */
        public RouteBuilder() {
        }

        /**
         * Sets source of the route
         *
         * @param source source of the route
         * @return this builder
         */
        public RouteBuilder setSource( N source ) {
            this.source = source;
            this.target = source;
            fixedStart = true;
            return this;
        }

        /**
         * Sets target of the route
         *
         * @param target target of the route
         * @return this builder
         */
        public RouteBuilder setTarget( N target ) {
            this.source = target;
            this.target = target;
            fixedStart = true;
            return this;
        }

        /**
         * Adds edge as last (in the sequence)
         *
         * @param edge edge
         * @return this builder
         */
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

        /**
         * Adds edge as first (in the sequence)
         *
         * @param edge edge
         * @return this builder
         */
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

        /**
         * Builds the route
         *
         * @return built route
         */
        public Route<N, E> build() {
            return new Route( new ArrayList<>( edges ), source, target );
        }
    }
}

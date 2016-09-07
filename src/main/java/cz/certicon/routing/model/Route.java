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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <N>
 * @param <E>
 */
@Value
public class Route<N extends Node, E extends Edge> {

    List<E> edges;
    N source;
    N target;

    public static <N extends Node, E extends Edge> RouteBuilder builder( Graph<N, E> graph ) {
        return new RouteBuilder<>( graph );
    }

    public static class RouteBuilder<N extends Node, E extends Edge> {

        private final Graph<N, E> graph;
        private Node source;
        private Node target;
        private final LinkedList<E> edges = new LinkedList<>();
        private boolean uturn = false;
        private boolean fixedStart = false;

        public RouteBuilder( Graph<N, E> graph ) {
            this.graph = graph;
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
                source = edge.getSource( graph );
                target = edge.getTarget( graph );
                if ( edge.isOneWay( graph ) ) {
                    fixedStart = true;
                }
            } else if ( fixedStart ) {
                if ( target.equals( edge.getSource( graph ) ) ) {
                    target = edge.getTarget( graph );
                } else if ( target.equals( edge.getTarget( graph ) ) && !edge.isOneWay( graph ) ) {
                    target = edge.getSource( graph );
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current target = " + target + ", edge = " + edge );
                }
            } else if ( uturn ) {
                if ( target.equals( edge.getSource( graph ) ) ) {
                    source = target;
                    target = edge.getTarget( graph );
                } else if ( target.equals( edge.getTarget( graph ) ) && !edge.isOneWay( graph ) ) {
                    source = target;
                    target = edge.getSource( graph );
                } else if ( source.equals( edge.getSource( graph ) ) ) {
                    target = edge.getTarget( graph );
                } else if ( source.equals( edge.getTarget( graph ) ) && !edge.isOneWay( graph ) ) {
                    target = edge.getSource( graph );
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current target = " + target + ", edge = " + edge );
                }
                fixedStart = true;
                uturn = false;
            } else if ( ( target.equals( edge.getSource( graph ) ) && source.equals( edge.getTarget( graph ) ) ) || ( !edge.isOneWay( graph ) && target.equals( edge.getTarget( graph ) ) && source.equals( edge.getSource( graph ) ) ) ) {
                uturn = true;
            } else if ( target.equals( edge.getSource( graph ) ) ) {
                target = edge.getTarget( graph );
                fixedStart = true;
            } else if ( target.equals( edge.getTarget( graph ) ) && !edge.isOneWay( graph ) ) {
                target = edge.getSource( graph );
                fixedStart = true;
            } else if ( source.equals( edge.getSource( graph ) ) ) {
                source = target;
                target = edge.getTarget( graph );
                fixedStart = true;
            } else if ( source.equals( edge.getTarget( graph ) ) && !edge.isOneWay( graph ) ) {
                source = target;
                target = edge.getSource( graph );
                fixedStart = true;
            } else {
                throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
            }
            edges.addLast( edge );
            return this;
        }

        public RouteBuilder addAsFirst( E edge ) {
            if ( source == null ) {
                source = edge.getSource( graph );
                target = edge.getTarget( graph );
                if ( edge.isOneWay( graph ) ) {
                    fixedStart = true;
                }
            } else if ( fixedStart ) {
                if ( source.equals( edge.getTarget( graph ) ) ) {
                    source = edge.getSource( graph );
                } else if ( source.equals( edge.getSource( graph ) ) && !edge.isOneWay( graph ) ) {
                    source = edge.getTarget( graph );
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
                }
            } else if ( uturn ) {
                if ( source.equals( edge.getTarget( graph ) ) ) {
                    target = source;
                    source = edge.getSource( graph );
                } else if ( source.equals( edge.getSource( graph ) ) && !edge.isOneWay( graph ) ) {
                    target = source;
                    source = edge.getTarget( graph );
                } else if ( target.equals( edge.getTarget( graph ) ) ) {
                    source = edge.getSource( graph );
                } else if ( target.equals( edge.getSource( graph ) ) && !edge.isOneWay( graph ) ) {
                    source = edge.getTarget( graph );
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
                }
                fixedStart = true;
                uturn = false;
            } else if ( ( source.equals( edge.getTarget( graph ) ) && target.equals( edge.getSource( graph ) ) ) || ( !edge.isOneWay( graph ) && source.equals( edge.getSource( graph ) ) && target.equals( edge.getTarget( graph ) ) ) ) {
                uturn = true;
            } else if ( source.equals( edge.getTarget( graph ) ) ) {
                source = edge.getSource( graph );
                fixedStart = true;
            } else if ( source.equals( edge.getSource( graph ) ) && !edge.isOneWay( graph ) ) {
                source = edge.getTarget( graph );
                fixedStart = true;
            } else if ( target.equals( edge.getTarget( graph ) ) ) {
                target = source;
                source = edge.getSource( graph );
                fixedStart = true;
            } else if ( target.equals( edge.getSource( graph ) ) && !edge.isOneWay( graph ) ) {
                target = source;
                source = edge.getTarget( graph );
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

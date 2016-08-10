/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model;

import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.graph.Edge;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class Route {

    List<Edge> edges;
    Node source;
    Node target;

    public static RouteBuilder builder() {
        return new RouteBuilder();
    }

    public static class RouteBuilder {

        private Node source;
        private Node target;
        private final LinkedList<Edge> edges = new LinkedList<>();
        private boolean uturn = false;
        private boolean fixedStart = false;

        public RouteBuilder setSource( Node source ) {
            this.source = source;
            this.target = source;
            fixedStart = true;
            return this;
        }

        public RouteBuilder setTarget( Node target ) {
            this.source = target;
            this.target = target;
            fixedStart = true;
            return this;
        }

        public RouteBuilder addAsLast( Edge edge ) {
            if ( source == null ) {
                source = edge.getSource();
                target = edge.getTarget();
                if ( edge.isOneway() ) {
                    fixedStart = true;
                }
            } else if ( fixedStart ) {
                if ( target.equals( edge.getSource() ) ) {
                    target = edge.getTarget();
                } else if ( target.equals( edge.getTarget() ) && !edge.isOneway() ) {
                    target = edge.getSource();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current target = " + target + ", edge = " + edge );
                }
            } else if ( uturn ) {
                if ( target.equals( edge.getSource() ) ) {
                    source = target;
                    target = edge.getTarget();
                } else if ( target.equals( edge.getTarget() ) && !edge.isOneway() ) {
                    source = target;
                    target = edge.getSource();
                } else if ( source.equals( edge.getSource() ) ) {
                    target = edge.getTarget();
                } else if ( source.equals( edge.getTarget() ) && !edge.isOneway() ) {
                    target = edge.getSource();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current target = " + target + ", edge = " + edge );
                }
                fixedStart = true;
                uturn = false;
            } else if ( ( target.equals( edge.getSource() ) && source.equals( edge.getTarget() ) ) || ( !edge.isOneway() && target.equals( edge.getTarget() ) && source.equals( edge.getSource() ) ) ) {
                uturn = true;
            } else if ( target.equals( edge.getSource() ) ) {
                target = edge.getTarget();
                fixedStart = true;
            } else if ( target.equals( edge.getTarget() ) && !edge.isOneway() ) {
                target = edge.getSource();
                fixedStart = true;
            } else if ( source.equals( edge.getSource() ) ) {
                source = target;
                target = edge.getTarget();
                fixedStart = true;
            } else if ( source.equals( edge.getTarget() ) && !edge.isOneway() ) {
                source = target;
                target = edge.getSource();
                fixedStart = true;
            } else {
                throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
            }
            edges.addLast( edge );
            return this;
        }

        public RouteBuilder addAsFirst( Edge edge ) {
            if ( source == null ) {
                source = edge.getSource();
                target = edge.getTarget();
                if ( edge.isOneway() ) {
                    fixedStart = true;
                }
            } else if ( fixedStart ) {
                if ( source.equals( edge.getTarget() ) ) {
                    source = edge.getSource();
                } else if ( source.equals( edge.getSource() ) && !edge.isOneway() ) {
                    source = edge.getTarget();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
                }
            } else if ( uturn ) {
                if ( source.equals( edge.getTarget() ) ) {
                    target = source;
                    source = edge.getSource();
                } else if ( source.equals( edge.getSource() ) && !edge.isOneway() ) {
                    target = source;
                    source = edge.getTarget();
                } else if ( target.equals( edge.getTarget() ) ) {
                    source = edge.getSource();
                } else if ( target.equals( edge.getSource() ) && !edge.isOneway() ) {
                    source = edge.getTarget();
                } else {
                    throw new IllegalArgumentException( "Unable to connect edges: current source = " + source + ", edge = " + edge );
                }
                fixedStart = true;
                uturn = false;
            } else if ( ( source.equals( edge.getTarget() ) && target.equals( edge.getSource() ) ) || ( !edge.isOneway() && source.equals( edge.getSource() ) && target.equals( edge.getTarget() ) ) ) {
                uturn = true;
            } else if ( source.equals( edge.getTarget() ) ) {
                source = edge.getSource();
                fixedStart = true;
            } else if ( source.equals( edge.getSource() ) && !edge.isOneway() ) {
                source = edge.getTarget();
                fixedStart = true;
            } else if ( target.equals( edge.getTarget() ) ) {
                target = source;
                source = edge.getSource();
                fixedStart = true;
            } else if ( target.equals( edge.getSource() ) && !edge.isOneway() ) {
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

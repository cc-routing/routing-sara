/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.view;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface DebugViewer {

    void setStepByInput( boolean stepByInput );

    void blinkEdge( long edgeId );

    void displayEdge( long edgeId );

    void removeEdge( long edgeId );

    void closeEdge( long edgeId );

    void displayNode( long nodeId );

    void removeNode( long nodeId );
}

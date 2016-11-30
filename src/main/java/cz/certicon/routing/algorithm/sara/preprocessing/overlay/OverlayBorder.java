/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

/**
 * BorderData shared by Overlay exit and entry parallel border edges.
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayBorder extends BorderData<OverlayNode, OverlayEdge> {
    public OverlayBorder(OverlayEdge exit, OverlayEdge entry) {
        super(exit, entry);
    }
}

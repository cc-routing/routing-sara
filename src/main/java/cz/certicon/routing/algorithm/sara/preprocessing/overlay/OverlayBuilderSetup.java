/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.model.graph.Metric;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Input setup for the OverlayBuilder
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
@Getter
@Setter
public class OverlayBuilderSetup {

    /**
     * whether calculated shortcuts are kept in memory; true required for
     * {@link cz.certicon.routing.algorithm.sara.query.mld.MLDFullMemoryRouteUnpacker}
     */
    boolean keepSortcuts = true;

    /**
     * optional metrics to use in overlay; used namely for debug, otherweise
     * keep null and metrics provided by input SaraGraph are used by default
     */
    Set<Metric> metric;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.sara.preprocessing.PreprocessingInput;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;

/**
 * Input setup for the OverlayCreator
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
@Getter
@Setter
public class OverlayCreatorSetup {

    public OverlayCreatorSetup() {
        this.builderSetup = new OverlayBuilderSetup();
    }

    /**
     * setup for OverlayBuilder
     */
    private final OverlayBuilderSetup builderSetup;

    /**
     * Optional properties namely for debug; by default null and project
     * resource properties are loaded
     */
    Properties daoProperties;

    /**
     * setup for partition preprocessing, if null preprocessing is skipped and
     * loaded from database
     */
    PreprocessingInput preprocessingInput;

    /**
     * optional random seed in partition preprocessing
     * keep -1 to skip
     */
    long randomSeed = -1;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing;

import lombok.Value;
import lombok.experimental.Wither;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
@Value
@Wither
public class PreprocessingInput {

    int cellSize;
    double cellRatio;
    double coreRatio;
    double lowIntervalProbability;
    double lowIntervalLimit;
    int numberOfAssemblyRuns;
    int numberOfLayers;

    public PreprocessingInput( int cellSize, double cellRatio, double coreRatio, double lowIntervalProbability, double lowIntervalLimit, int numberOfAssemblyRuns, int numberOfLayers ) {
        // validate input
        this.cellSize = cellSize;
        this.cellRatio = cellRatio;
        this.coreRatio = coreRatio;
        this.lowIntervalProbability = lowIntervalProbability;
        this.lowIntervalLimit = lowIntervalLimit;
        this.numberOfAssemblyRuns = numberOfAssemblyRuns;
        this.numberOfLayers = numberOfLayers;
    }

}

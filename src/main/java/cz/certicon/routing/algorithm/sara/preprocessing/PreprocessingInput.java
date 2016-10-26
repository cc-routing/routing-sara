/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing;

import lombok.Value;
import lombok.experimental.Wither;

import static cz.certicon.routing.utils.validation.Validation.*;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
@Value
@Wither
public class PreprocessingInput {

    int cellSizes[];
    double cellRatio;
    double coreRatio;
    double lowIntervalProbability;
    double lowIntervalLimit;
    int numberOfAssemblyRuns;
    int numberOfLayers;

    public PreprocessingInput( int cellSize, double cellRatio, double coreRatio, double lowIntervalProbability, double lowIntervalLimit, int numberOfAssemblyRuns, int numberOfLayers ) {
        // validate input
        validateThat( valid()
                .and( "cellSize", greaterThan( cellSize, 0 ) )
                .and( "cellRatio", greaterThan( cellRatio, 0 ).and( smallerOrEqualTo( cellRatio, 1 ) ) )
                .and( "coreRatio", greaterThan( coreRatio, 0 ).and( smallerOrEqualTo( coreRatio, 1 ) ) )
                .and( "lowIntervalProbability", greaterOrEqualTo( lowIntervalProbability, 0 ).and( smallerOrEqualTo( lowIntervalProbability, 1 ) ) )
                .and( "lowIntervalLimit", greaterThan( lowIntervalLimit, 0 ).and( smallerThan( lowIntervalLimit, 1 ) ) )
                .and( "numberOfAssemblyRuns", greaterThan( numberOfAssemblyRuns, 0 ) )
                .and( "numberOfLayers", greaterThan( numberOfLayers, 0 ) )
        );
        this.cellSizes = new int[numberOfLayers];
        for ( int i = 0; i < cellSizes.length; i++ ) {
            cellSizes[i] = ( i > 0 ? cellSizes[i - 1] : 1 ) * cellSize;
        }
        this.cellRatio = cellRatio;
        this.coreRatio = coreRatio;
        this.lowIntervalProbability = lowIntervalProbability;
        this.lowIntervalLimit = lowIntervalLimit;
        this.numberOfAssemblyRuns = numberOfAssemblyRuns;
        this.numberOfLayers = numberOfLayers;
    }

    public PreprocessingInput( int[] cellSizes, double cellRatio, double coreRatio, double lowIntervalProbability, double lowIntervalLimit, int numberOfAssemblyRuns, int numberOfLayers ) {
        // validate input
        validateThat( valid()
                .and( "cellSizes", equalTo( cellSizes.length, numberOfLayers ) )
                .and( "cellRatio", greaterThan( cellRatio, 0 ).and( smallerOrEqualTo( cellRatio, 1 ) ) )
                .and( "coreRatio", greaterThan( coreRatio, 0 ).and( smallerOrEqualTo( coreRatio, 1 ) ) )
                .and( "lowIntervalProbability", greaterOrEqualTo( lowIntervalProbability, 0 ).and( smallerOrEqualTo( lowIntervalProbability, 1 ) ) )
                .and( "lowIntervalLimit", greaterThan( lowIntervalLimit, 0 ).and( smallerThan( lowIntervalLimit, 1 ) ) )
                .and( "numberOfAssemblyRuns", greaterThan( numberOfAssemblyRuns, 0 ) )
                .and( "numberOfLayers", greaterThan( numberOfLayers, 0 ) )
        );
        for ( int i = 0; i < cellSizes.length; i++ ) {
            validateThat( valid().and( "cellSize[" + cellSizes[i] + "]", greaterThan( cellSizes[i], 0 ) ) );
        }
        this.cellSizes = cellSizes;
        this.cellRatio = cellRatio;
        this.coreRatio = coreRatio;
        this.lowIntervalProbability = lowIntervalProbability;
        this.lowIntervalLimit = lowIntervalLimit;
        this.numberOfAssemblyRuns = numberOfAssemblyRuns;
        this.numberOfLayers = numberOfLayers;
    }

    public PreprocessingInput withCellSize( int cellSize ) {
        return new PreprocessingInput( cellSize, cellRatio, coreRatio, lowIntervalProbability, lowIntervalLimit, numberOfAssemblyRuns, numberOfLayers );
    }

}

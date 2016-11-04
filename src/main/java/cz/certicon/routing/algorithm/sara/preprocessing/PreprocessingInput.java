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
 * Settings for the preprocessor unit.
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

    /**
     * Constructor
     *
     * @param cellSize               maximal size of cell, must be a positive number
     * @param cellRatio              defines portion of a cell to create a fragment during the filtering phase, must be a number from interval (0,1]
     * @param coreRatio              defines portion of a fragment to create its core during the filtering phase, must be a number from interval (0,1]
     * @param lowIntervalProbability probability of selecting the lower interval when generating r for the score function, see {@link cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler} for more details, must be a number from interval [0,1]
     * @param lowIntervalLimit       defines the lower interval by its upper bound (e.g. lowIntervalLimit = 0.4, then the lower interval is [0,0.4]) when generating r for the score function, see {@link cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler} for more details, must be a number from interval [0,1]
     * @param numberOfAssemblyRuns   number N of assembly runs per each layer, N assembly runs are performed and the best one is chosen based on its amount of cut edges, must be a positive number
     * @param numberOfLayers         number of layers the result should have, must be a positive number
     */
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

    /**
     * Constructor
     *
     * @param cellSizes              maximal size of cell for each layer, each cell size must be a positive number, length of the array must match the number of layers
     * @param cellRatio              defines portion of a cell to create a fragment during the filtering phase, must be a number from interval (0,1]
     * @param coreRatio              defines portion of a fragment to create its core during the filtering phase, must be a number from interval (0,1]
     * @param lowIntervalProbability probability of selecting the lower interval when generating r for the score function, see {@link cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler} for more details, must be a number from interval [0,1]
     * @param lowIntervalLimit       defines the lower interval by its upper bound (e.g. lowIntervalLimit = 0.4, then the lower interval is [0,0.4]) when generating r for the score function, see {@link cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler} for more details, must be a number from interval [0,1]
     * @param numberOfAssemblyRuns   number N of assembly runs per each layer, N assembly runs are performed and the best one is chosen based on its amount of cut edges, must be a positive number
     * @param numberOfLayers         number of layers the result should have, must be a positive number
     */
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
        for ( int cellSize : cellSizes ) {
            validateThat( valid().and( "cellSize[" + cellSize + "]", greaterThan( cellSize, 0 ) ) );
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

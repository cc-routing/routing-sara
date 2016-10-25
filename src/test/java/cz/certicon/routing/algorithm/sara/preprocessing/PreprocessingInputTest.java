/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing;

import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
@RunWith( Parameterized.class )
public class PreprocessingInputTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList( new Object[][]{
            { false, -1, 0.1, 0.1, 0.1, 0.1, 1, 1 },
            { false, 1, 0.0, 0.1, 0.1, 0.1, 1, 1 },
            { false, 1, 1.1, 0.1, 0.1, 0.1, 1, 1 },
            { false, 1, 0.1, 0.0, 0.1, 0.1, 1, 1 },
            { false, 1, 0.1, 1.1, 0.1, 0.1, 1, 1 },
            { false, 1, 0.1, 0.1, -0.1, 0.1, 1, 1 },
            { false, 1, 0.1, 0.1, 1.1, 0.1, 1, 1 },
            { false, 1, 0.1, 0.1, 0.1, 0.0, 1, 1 },
            { false, 1, 0.1, 0.1, 0.1, 1.0, 1, 1 },
            { false, 1, 0.1, 0.1, 0.1, 0.1, -1, 1 },
            { false, 1, 0.1, 0.1, 0.1, 0.1, 1, -1 },
            { true, 1, 0.1, 0.1, 0.1, 0.1, 1, 1 }
        } );
    }

    final boolean isValid;
    final int cellSize;
    final double cellRatio;
    final double coreRatio;
    final double lowIntervalProbability;
    final double lowIntervalLimit;
    final int numberOfAssemblyRuns;
    final int numberOfLayers;

    public PreprocessingInputTest( boolean isValid, int cellSize, double cellRatio, double coreRatio, double lowIntervalProbability, double lowIntervalLimit, int numberOfAssemblyRuns, int numberOfLayers ) {
        this.isValid = isValid;
        this.cellSize = cellSize;
        this.cellRatio = cellRatio;
        this.coreRatio = coreRatio;
        this.lowIntervalProbability = lowIntervalProbability;
        this.lowIntervalLimit = lowIntervalLimit;
        this.numberOfAssemblyRuns = numberOfAssemblyRuns;
        this.numberOfLayers = numberOfLayers;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test( expected = IllegalArgumentException.class )
    public void testInvalid() {
        if ( !isValid ) {
            PreprocessingInput instance = new PreprocessingInput( cellSize, cellRatio, coreRatio, lowIntervalProbability, lowIntervalLimit, numberOfAssemblyRuns, numberOfLayers );
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Test
    public void testValid() {
        if ( isValid ) {
            PreprocessingInput instance = new PreprocessingInput( cellSize, cellRatio, coreRatio, lowIntervalProbability, lowIntervalLimit, numberOfAssemblyRuns, numberOfLayers );
        }

    }
}

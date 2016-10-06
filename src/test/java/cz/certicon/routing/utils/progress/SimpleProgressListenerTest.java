/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils.progress;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
public class SimpleProgressListenerTest {

    public SimpleProgressListenerTest() {
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

    /**
     * Test of getNumOfUpdates method, of class SimpleProgressListener.
     */
    @Test
    public void testGetNumOfUpdates() {
        System.out.println( "getNumOfUpdates" );
        SimpleProgressListener instance = new SimpleProgressListener() {
            @Override
            public void onProgressUpdate( double done ) {
                // do nothing
            }
        };
        instance.setNumOfUpdates( 10 );
        int expResult = 10;
        int result = instance.getNumOfUpdates();
        assertEquals( expResult, result );
    }

    /**
     * Test of setNumOfUpdates method, of class SimpleProgressListener.
     */
    @Test
    public void testSetNumOfUpdates() {
        System.out.println( "setNumOfUpdates" );
        SimpleProgressListener instance = new SimpleProgressListener() {
            @Override
            public void onProgressUpdate( double done ) {
                // do nothing
            }
        };
        instance.setNumOfUpdates( 10 );
        int expResult = 10;
        int result = instance.getNumOfUpdates();
        assertEquals( expResult, result );
    }

    /**
     * Test of nextStep method, of class SimpleProgressListener.
     */
    @Test
    public void testNextStep() {
        System.out.println( "nextStep" );
    }

    /**
     * Test of init method, of class SimpleProgressListener.
     */
    @Test
    public void testInit() {
        System.out.println( "init" );
        SimpleProgressListener instance = new SimpleProgressListener( 10 ) {
            @Override
            public void onProgressUpdate( double done ) {
                // do nothing
            }
        };
        instance.init( 1, 0.5 );
        assertTrue( instance.nextStep() );
        instance.init( 10, 0.5 );
        for ( int i = 0; i < 5; i++ ) {
            assertFalse( instance.nextStep() );
            assertTrue( instance.nextStep() );
        }
    }

}

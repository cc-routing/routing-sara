package cz.certicon.routing.utils;

import org.junit.Test;

import static cz.certicon.routing.utils.EffectiveUtils.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by blaha on 31.10.2016.
 */
public class EffectiveUtilsTest {

    @Test
    public void fillArray_int_for_1_returns_array_of_1() throws Exception {
        int value = 1;
        int[] result = new int[5];
        int[] expected = new int[]{ value, value, value, value, value };
        fillArray( result, value );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void fillArray_double_for_half_returns_array_of_halfs() throws Exception {
        double value = 0.5;
        double[] result = new double[5];
        double[] expected = new double[]{ value, value, value, value, value };
        fillArray( result, value );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void fillArray_float_for_half_returns_array_of_halfs() throws Exception {
        float value = 0.5f;
        float[] result = new float[5];
        float[] expected = new float[]{ value, value, value, value, value };
        fillArray( result, value );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void fillArray_double_for_true_returns_array_of_trues() throws Exception {
        boolean value = true;
        boolean[] result = new boolean[5];
        boolean[] expected = new boolean[]{ value, value, value, value, value };
        fillArray( result, value );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void fillArray_double_for_String_returns_array_of_Strings() throws Exception {
        String value = "nothing";
        String[] result = new String[5];
        String[] expected = new String[]{ value, value, value, value, value };
        fillArray( result, value );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void copyArray_int_copies_source_into_target() throws Exception {
        int value = 1;
        int[] result = new int[5];
        int[] expected = new int[]{ value, value, value, value, value };
        copyArray( expected, result );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void copyArray_long_copies_source_into_target() throws Exception {
        long value = 1;
        long[] result = new long[5];
        long[] expected = new long[]{ value, value, value, value, value };
        copyArray( expected, result );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void copyArray_double_copies_source_into_target() throws Exception {
        double value = 0.5;
        double[] result = new double[5];
        double[] expected = new double[]{ value, value, value, value, value };
        copyArray( expected, result );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void copyArray_boolean_copies_source_into_target() throws Exception {
        boolean value = true;
        boolean[] result = new boolean[5];
        boolean[] expected = new boolean[]{ value, value, value, value, value };
        copyArray( expected, result );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void copyArray_float_copies_source_into_target() throws Exception {
        float value = 0.5f;
        float[] result = new float[5];
        float[] expected = new float[]{ value, value, value, value, value };
        copyArray( expected, result );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void enlarge_long_enlarges_to_twice_size() throws Exception {
        long value = 1;
        long def = 0;
        long[] input = new long[]{ value, value, value, value, value };
        long[] expected = new long[]{ value, value, value, value, value, def, def, def, def, def };
        long[] result = enlarge( input, input.length );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void enlarge_int_enlarges_to_twice_size() throws Exception {
        int value = 1;
        int def = 0;
        int[] input = new int[]{ value, value, value, value, value };
        int[] expected = new int[]{ value, value, value, value, value, def, def, def, def, def };
        int[] result = enlarge( input, input.length );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void enlarge_boolean_enlarges_to_twice_size() throws Exception {
        boolean value = true;
        boolean def = false;
        boolean[] input = new boolean[]{ value, value, value, value, value };
        boolean[] expected = new boolean[]{ value, value, value, value, value, def, def, def, def, def };
        boolean[] result = enlarge( input, input.length );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void enlarge_float_enlarges_to_twice_size() throws Exception {
        float value = 0.5f;
        float def = 0;
        float[] input = new float[]{ value, value, value, value, value };
        float[] expected = new float[]{ value, value, value, value, value, def, def, def, def, def };
        float[] result = enlarge( input, input.length );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void enlarge_double_enlarges_to_twice_size() throws Exception {
        double value = 0.5;
        double def = 0;
        double[] input = new double[]{ value, value, value, value, value };
        double[] expected = new double[]{ value, value, value, value, value, def, def, def, def, def };
        double[] result = enlarge( input, input.length );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void enlarge_int_2d_enlarges_to_twice_size() throws Exception {
        int value = 1;
        int[] def = null;
        int[][] input = new int[][]{ { value, value }, { value, value, value }, { value, value }, { value, value, value }, { value, value } };
        int[][] expected = new int[][]{ { value, value }, { value, value, value }, { value, value }, { value, value, value }, { value, value }, def, def, def, def, def };
        int[][] result = enlarge( input, input.length );
        assertThat( result, equalTo( expected ) );
    }

    @Test
    public void enlarge_float_3d_enlarges_to_twice_size() throws Exception {
        float value = 1.0f;
        float[][] def = null;
        float[][][] input = new float[][][]{ { { value, value }, { value, value, value }, { value, value }, { value, value, value }, { value, value } }, { { value } } };
        float[][][] expected = new float[][][]{ { { value, value }, { value, value, value }, { value, value }, { value, value, value }, { value, value } }, { { value } }, def, def };
        float[][][] result = enlarge( input, input.length );
        assertThat( result, equalTo( expected ) );
    }

}
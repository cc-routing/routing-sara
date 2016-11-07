/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Color utilities, e.g. {@link ColorSupplier}
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ColorUtils {
    private static final int MAX_COLORS = 40;

    /**
     * Returns new color supplier with the given number of colors. Maximal number of colors is {@value #MAX_COLORS}
     *
     * @param numberOfColors number of colors
     * @return new color supplier with the given number of colors
     */
    public static ColorSupplier createColorSupplier( int numberOfColors ) {
        if ( numberOfColors > MAX_COLORS ) {
            numberOfColors = MAX_COLORS;
        }
        return new ColorSupplier( numberOfColors );
    }

    /**
     * Color supplier class. Create via {@link ColorUtils#createColorSupplier(int)}
     */
    public static class ColorSupplier {

        private final List<Color> colorList = new ArrayList<>();
        private int colorCounter = 0;

        ColorSupplier( int numberOfColors ) {
            float interval = 360 / ( numberOfColors );
            for ( float x = 0; x < 360; x += interval ) {
                Color c = Color.getHSBColor( x / 360, 1, 1 );
                colorList.add( c );
            }
        }

        /**
         * Shuffles the colors
         */
        public void shuffle() {
            Collections.shuffle( colorList );
        }

        /**
         * Returns next color in line
         *
         * @return next color in line
         */
        public Color nextColor() {
            return colorList.get( colorCounter++ % colorList.size() );
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class StringUtils {

    public static StringBuilder replaceLast( StringBuilder sb, boolean conditionResult, String replacement ) {
        if ( conditionResult ) {
            sb.replace( sb.length() - 1, sb.length(), replacement );
        } else {
            sb.append( replacement );
        }
        return sb;
    }
}

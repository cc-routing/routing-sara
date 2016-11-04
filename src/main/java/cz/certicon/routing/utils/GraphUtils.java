/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.utils;

import cz.certicon.routing.model.Identifiable;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.Collection;

/**
 *
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class GraphUtils {

    public static <I extends Identifiable> TLongObjectMap<I> toMap( Collection<I> identifiables ) {
        TLongObjectMap<I> map = new TLongObjectHashMap<>();
        for ( I identifiable : identifiables ) {
            map.put( identifiable.getId(), identifiable );
        }
        return map;
    }

//    public static SaraNode toSaraNode(Node node){
//        return new SaraNode(node.getId(), node);
//    }
}

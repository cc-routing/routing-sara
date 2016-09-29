/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data;

import java.util.Collection;
import lombok.Builder;
import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@gmail.com>}
 */
@Value
public class GraphDeleteMessenger {
    Collection<Long> nodeIds;
    Collection<Long> edgeIds;
}

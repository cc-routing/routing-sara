/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.Iterator;
import lombok.Getter;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class LongHashMap<T> implements ReadOnlyLongMap<T> {

    @Getter
    private final TLongObjectMap<T> map = new TLongObjectHashMap<T>();

    public void add(long key, T value) {
        if (this.map.containsKey(key)) {
            throw new IllegalStateException("key is already present: " + key);
        } else {
            this.map.put(key, value);
        }
    }

    @Override
    public T get(long key) {
        return this.map.get(key);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return this.map.valueCollection().iterator();
    }

    @Override
    public boolean containsKey(long key) {
        return this.map.containsKey(key);
    }
}

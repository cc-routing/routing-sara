/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.model.queue;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.util.FibonacciHeapNode;

/**
 * Implementation of the {@link PriorityQueue} via jgrapht's library - the
 * Fibonacci heap implementation
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <T> element type
 */
public class FibonacciHeap<T> implements PriorityQueue<T> {

    private final Map<T, FibonacciHeapNode<T>> nodeMap;
    private final org.jgrapht.util.FibonacciHeap<T> fibonacciHeap;

    public FibonacciHeap() {
        this.fibonacciHeap = new org.jgrapht.util.FibonacciHeap<>();
        this.nodeMap = new HashMap<>();
    }

    @Override
    public T extractMin() {
        FibonacciHeapNode<T> min = fibonacciHeap.removeMin();
        nodeMap.remove( min.getData() );
        return min.getData();
    }

    @Override
    public void add( T element, double value ) {
//        System.out.println( "Adding " + node + " with value " + value );
        FibonacciHeapNode<T> n = new FibonacciHeapNode<>( element );
        nodeMap.put( element, n );
        fibonacciHeap.insert( n, value );
    }

    @Override
    public void remove( T element ) {
        FibonacciHeapNode<T> n = nodeMap.get( element );
        nodeMap.remove( element );
        fibonacciHeap.delete( n );
    }

    @Override
    public void decreaseKey( T element, double value ) {
//        System.out.println( "Changing " + node + " to value " + value );
        FibonacciHeapNode<T> n = nodeMap.get( element );
        if ( n == null ) {
            add( element, value );
        } else if ( value < n.getKey() ) {
            fibonacciHeap.decreaseKey( n, value );
        } else if ( value > n.getKey() ) {
            remove( element );
            add( element, value );
        }
    }

    @Override
    public void clear() {
        nodeMap.clear();
        fibonacciHeap.clear();
    }

    @Override
    public boolean isEmpty() {
        return fibonacciHeap.isEmpty();
    }

    @Override
    public int size() {
        return fibonacciHeap.size();
    }

    @Override
    public boolean contains( T element ) {
        return nodeMap.containsKey( element );
    }

    @Override
    public T findMin() {
        if ( !fibonacciHeap.isEmpty() ) {
            return fibonacciHeap.min().getData();
        } else {
            return null;
        }
    }

    @Override
    public double minValue() {
        if ( !fibonacciHeap.isEmpty() ) {
            return fibonacciHeap.min().getKey();
        } else {
            return Double.MAX_VALUE;
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sra.preprocessing.filtering;

import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Node;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import lombok.Value;
import lombok.experimental.Wither;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class NaturalCutsFilter implements Filter {

    @Wither
    private final double cellRatio; // alpha
    @Wither
    private final double coreRatioInverse; // f
    @Wither
    private final int maxCellSize; // U

    /**
     * Creates new instance
     *
     * @param cellRatio portion of U (max cell size) defining the size of
     * fragment, alpha, 0 &lt;= alpha &lt;= 1
     * @param coreRatioInverse divisor defining the core size, core size =
     * alpha*U/f, this is f
     * @param maxCellSize maximal size of a fragment, U
     */
    public NaturalCutsFilter( double cellRatio, double coreRatioInverse, int maxCellSize ) {
        this.cellRatio = cellRatio;
        this.coreRatioInverse = coreRatioInverse;
        this.maxCellSize = maxCellSize;
    }

    @Override
    public Graph filter( Graph graph ) {
        // TODO need structure which allows random pick and fast element removal (target element)
        // conside using set and converting to array or iterator for random pick - how many random picks???
        RandomSet<Node> nodes = new RandomSet<>( graph.getNodesCount() );
        Iterator<Node> nodeIterator = graph.getNodes();
        while ( nodeIterator.hasNext() ) {
            nodes.add( nodeIterator.next() );
        }

        Random random = new Random();
        // until there are no nodes left
        while ( !nodes.isEmpty() ) {
            // pick a node (=center) at random (a node that does not belong to any core)
            Node center = nodes.pollRandom( random );
            // create tree T via BFS from center at maximal size of cellRatio * maxCellSize, where size is a sum of tree's nodes' sizes
            // TODO what is node size? 1 at the beginning, then sum of contracted nodes inside this node
            // all the nodes added to tree before it reached cellRatio * maxCellSize / coreRatioInverse form a "core"
            // other nodes in the tree form a "ring"
            // contract core into a single node s
            // contract ring into a single node t
            // perform s-t minimal cut algorithm between them
            // CONTRACTION
            // - foreach node
            // -- remove node
            // -- preserve paths (create edges between all the neighbors)
        }

        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

//    private static class RandomSet<E> {
//
//        private final Set<E> set;
//
//        public RandomSet() {
//            set = new HashSet<>();
//        }
//
//        public RandomSet( int initialCapacity ) {
//            set = new HashSet<>( initialCapacity );
//
//        }
//
//        public void add( E element ) {
//            set.add( element );
//        }
//
//        public void remove( E element ) {
//            set.remove( element );
//        }
//
//        public E random() {
//
//        }
//    }
    private static class RandomSet<E> extends AbstractSet<E> {

        private final List<E> dta;
        private final Map<E, Integer> idx;

        public RandomSet() {
            dta = new ArrayList<>();
            idx = new HashMap<>();
        }

        public RandomSet( int initialCapacity ) {
            dta = new ArrayList<>( initialCapacity );
            idx = new HashMap<>( initialCapacity );
        }

        public RandomSet( Collection<E> items ) {
            dta = new ArrayList<>( items.size() );
            idx = new HashMap<>( items.size() );
            for ( E item : items ) {
                idx.put( item, dta.size() );
                dta.add( item );
            }
        }

        @Override
        public boolean add( E item ) {
            if ( idx.containsKey( item ) ) {
                return false;
            }
            idx.put( item, dta.size() );
            dta.add( item );
            return true;
        }

        /**
         * Override element at position <code>id</code> with last element.
         *
         * @param id
         */
        public E removeAt( int id ) {
            if ( id >= dta.size() ) {
                return null;
            }
            E res = dta.get( id );
            idx.remove( res );
            E last = dta.remove( dta.size() - 1 );
            // skip filling the hole if last is removed
            if ( id < dta.size() ) {
                idx.put( last, id );
                dta.set( id, last );
            }
            return res;
        }

        @Override
        public boolean remove( Object item ) {
            @SuppressWarnings( value = "element-type-mismatch" )
            Integer id = idx.get( item );
            if ( id == null ) {
                return false;
            }
            removeAt( id );
            return true;
        }

        public E get( int i ) {
            return dta.get( i );
        }

        public E pollRandom( Random rnd ) {
            if ( dta.isEmpty() ) {
                return null;
            }
            int id = rnd.nextInt( dta.size() );
            return removeAt( id );
        }

        @Override
        public int size() {
            return dta.size();
        }

        @Override
        public Iterator<E> iterator() {
            return dta.iterator();
        }
    }
}

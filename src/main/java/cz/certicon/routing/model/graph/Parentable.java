package cz.certicon.routing.model.graph;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface Parentable {

    boolean hasParent();

    Cell getParent();
}

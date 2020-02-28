package graphs;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Amanda
 *
 * @param <T>
 */
public class ListGraph<T> implements Graph<T>, Serializable {
	private static final long serialVersionUID = -5526103659696008461L;
	private Map<T, List<Edge<T>>> nodes = new HashMap<T, List<Edge<T>>>();
	
	/**
	 * Adderar en nod till nodlistan.
	 * @param node
	 */
    public void add(T node) {
    	if (nodes.containsKey(node))
    		throw new IllegalArgumentException("The node already exists.");
    	nodes.put(node, new ArrayList<Edge<T>>());
    }

    /**
	 * Skapar en förbindelse mellan två noder.
	 * @param connectionType
	 * @param weight
	 * @param from
	 * @param to
	 */
    public void connect(String connectionType, int weight, T from, T to) {
    	if (getEdgeBetween(from, to) != null)
    		throw new IllegalStateException("The nodes already have a connection.");
    	List<Edge<T>> fromEdges = nodes.get(from);
    	List<Edge<T>> toEdges = nodes.get(to);
    	fromEdges.add(new Edge<T>(connectionType, weight, to, from));
    	toEdges.add(new Edge<T>(connectionType, weight, from, to));
    }
    
    /**
	 * Tar bort en förbindelse mellan två noder.
	 * @param from
	 * @param to
	 */
    public void disconnect(T from, T to) {
    	if (!nodes.keySet().contains(from) || !nodes.keySet().contains(to))
    		throw new NoSuchElementException("Platsen finns inte!");
    	nodes.get(from).remove(getEdgeBetween(from, to));
    	nodes.get(to).remove(getEdgeBetween(from, to));
    }
    
    /**
	 * Tar bort en nod från nodlistan.
	 * @param node
	 */
    public void remove(T node) {
    	nodes.remove(node);
    }

    /**
	 * Sätter vikten på en förbindelse.
	 * @param from
	 * @param to
	 * @param weight
	 */
    public void setConnectionWeight(T from, T to, int weight) {
    	Edge<T> edgeFrom = getEdgeBetween(from, to);
    	Edge<T> edgeTo = getEdgeBetween(to, from);
    	checkEdge(edgeFrom);
    	checkEdge(edgeTo);
    	edgeFrom.setWeight(weight);
    	edgeTo.setWeight(weight);
    }

    /**
	 * Returnerar en kopia av nodlistan.
	 * @return
	 */
    public List<T> getNodes() {
    	return new ArrayList<T>(nodes.keySet());
    }

    /**
	 * Returnerar en lista med alla förbindelser från en nod.
	 * @param node
	 * @return
	 */
    public List<Edge<T>> getEdgesFrom(T node) {
    	if (nodes.containsKey(node)) {
    		return new ArrayList<Edge<T>>(nodes.get(node));
    	}
    	return null;
    }
    
    /**
     * Kontrollerar om förbindelsen finns.
     * @param edge
     */
    private void checkEdge(Edge<T> edge) {
    	if (edge == null)
    		throw new NoSuchElementException("Förbindelsen finns inte!");
    }

    /**
	 * Returnerar förbindelsen som går mellan två noder.
	 * @param from
	 * @param to
	 * @return
	 */
    public Edge<T> getEdgeBetween(T from, T to) {
    	try {
    		for (Edge<T> e : nodes.get(from))
        		if (e.getDestination() == to)
        			return e;
    		return null;
    	} catch (NullPointerException npe) {
    		return null;
    	}
    }

    /**
	 * Returnerar namnet på noden och listan med förbindelser från noden.
	 * @return
	 */
    public String toString() {
        for (Map.Entry<T, List<Edge<T>>> entry : nodes.entrySet()) {
        	return entry.getKey() + " " + entry.getValue();
        }
        
        return null;
    }
}

package graphs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphMethods {
	/**
	 * Gör en djupet först sökning.
	 * @param listGraph
	 * @param where
	 * @param visited
	 */
	private static <T> void depthFirstSearch(Graph<T> listGraph, T where, Set<T> visited) {
		visited.add(where);
		List<Edge<T>> edgeList = listGraph.getEdgesFrom(where);
		if (edgeList != null) {
			for (Edge<T> edge : edgeList) {
	    		if (!visited.contains(edge.getDestination()))
	    			depthFirstSearch(listGraph, edge.getDestination(), visited);
	    	}
		}
	}
	
	/**
	 * Kontrollerar om det finns en väg mellan noderna.
	 * @param listGraph
	 * @param startNode
	 * @param endNode
	 * @return
	 */
    public static <T> boolean pathExists(Graph<T> listGraph, T startNode, T endNode){
    	Set<T> visited = new HashSet<T>();
    	depthFirstSearch(listGraph, startNode, visited);
        return visited.contains(endNode);
    }
    
    /**
     * Letar efter den snabbaste vägen mellan två noder och returnerar den.
     * @param listGraph
     * @param startNode
     * @param endNode
     * @return
     */
    public static <T> ArrayList<Edge<T>> getFastestPath(Graph<T> listGraph, T startNode, T endNode) {
    	if (!pathExists(listGraph, startNode, endNode)) { return null; }
    	Map<T, Integer> distance = new HashMap<T, Integer>();
    	Map<T, T> fromNode = new HashMap<T, T>();
    	Map<T, Boolean> visited = new HashMap<T, Boolean>();
    	for (T node : listGraph.getNodes()) {
    		distance.put(node, Integer.MAX_VALUE);
    		fromNode.put(node, null);
    		visited.put(node, false);
    	}
    	distance.put(startNode, 0);
    	visited.put(startNode, true);
    	T where = startNode;
    	while (visited.get(endNode) == false) {
    		for (Edge<T> edge : listGraph.getEdgesFrom(where)) {
    			if (distance.get(where) + edge.getWeight() < distance.get(edge.getDestination())) {
    				distance.put(edge.getDestination(), distance.get(where) + edge.getWeight());
    				fromNode.put(edge.getDestination(), where);
    			}
    		}
    		int maxDistance = Integer.MAX_VALUE;
    		for (T node : listGraph.getNodes()) {
    			if (!visited.get(node) && distance.get(node) < maxDistance) {
    				maxDistance = distance.get(node);
    				where = node;
    			}
    		}
    		visited.put(where, true);
    	}
    	ArrayList<Edge<T>> fastestPath = new ArrayList<Edge<T>>();
    	T to = endNode;
    	T from = fromNode.get(to);
    	while (from != null) {
    		fastestPath.add(listGraph.getEdgeBetween(from, to));
    		to = from;
    		from = fromNode.get(from);
    	}
    	Collections.reverse(fastestPath);
		return fastestPath;
    }
}

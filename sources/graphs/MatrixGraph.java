package graphs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MatrixGraph<T> implements Graph<T>{
	private Edge<T>[][] graph;
	private Map<T, Integer> index = new HashMap<T, Integer>();
	
	
	public MatrixGraph(int initCap) {
	}

	@Override
	public void add(T node) {
		index.put(node, index.size());
	}

	@Override
	public void connect(String connectionType, int weight, T from, T to) {
		int fromNr = index.get(from);
		int toNr = index.get(to);
		Edge<T> edge = new Edge<T>(connectionType, weight, to, from);
		graph[fromNr][toNr] = edge;
	}

	@Override
	public void disconnect(T from, T to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(T node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConnectionWeight(T from, T to, int weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<T> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Edge<T>> getEdgesFrom(T from) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge<T> getEdgeBetween(T from, T to) {
		// TODO Auto-generated method stub
		return null;
	}

}

package graphs;
import java.util.List;

public interface Graph<T> {
	/**
	 * 
	 * @param node
	 */
	void add(T node);
	
	/**
	 * 
	 * @param connectionType
	 * @param weight
	 * @param from
	 * @param to
	 */
	void connect(String connectionType, int weight, T from, T to);
	
	/**
	 * 
	 * @param from
	 * @param to
	 */
	void disconnect(T from, T to);
	
	/**
	 * 
	 * @param node
	 */
	void remove(T node);
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param weight
	 */
	void setConnectionWeight(T from, T to, int weight);
	
	/**
	 * 
	 * @return
	 */
	List<T> getNodes();
	
	/**
	 * 
	 * @param from
	 * @return
	 */
	List<Edge<T>> getEdgesFrom(T from);
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	Edge<T> getEdgeBetween(T from, T to);
	
	/**
	 * 
	 * @return
	 */
	String toString();
}

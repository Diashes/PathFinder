package graphs;
import java.io.Serializable;

public class Edge<T> implements Serializable {
	private static final long serialVersionUID = -1452610288524532986L;
	private String edgeName;
	private int weight;
	private T destination;
	private T origin;
	
	Edge(String edgeName, int weight, T destination, T origin) {
		if (weight < 0)
			throw new IllegalArgumentException("Tiden är negativ!");
		this.edgeName = edgeName;
		this.weight = weight;
		this.destination = destination;
		this.origin = origin;
	}
	
    public T getDestination() { return destination; }
    
    public String getEdgeName() { return edgeName; }

    public int getWeight() { return weight; }

    public void setWeight(int weight) { this.weight = weight; }
    
    public String toString() { return edgeName + " från " + origin + " till " + destination + " tar " + weight + " minuter."; }
}

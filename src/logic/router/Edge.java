package logic.router;

/**
 * 
 * @author vsutskever
 * 
 */
public class Edge {
	
	private Node node;
	private int cost;	//time

	
	public Edge(Node nodeTo, int cost) {
		this.node = nodeTo;
		this.cost = cost;
	}


	
	public Node getNode() {
		return this.node;
	}


	
	public int getCost() {
		return this.cost;
	}
	
}
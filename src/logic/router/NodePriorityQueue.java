package logic.router;

import java.util.Collection;
import java.util.PriorityQueue;
/**
 * 
 * @author vsutskever
 * 
 */
public class NodePriorityQueue  {
	
	
	private PriorityQueue<Node> pQueue = new PriorityQueue<Node>();
	
	
	public NodePriorityQueue() {
		
	}
	
	
	
	public void add(Node n) {
		this.pQueue.add(n);
	}
	
	
	
	public void add(Collection<Node> nodeCollection) {
		this.pQueue.addAll(nodeCollection);
	}

	
	
	public Boolean hasMore() {
		return !this.pQueue.isEmpty();
	}
	
	
	
	public Node remove() {
		return this.pQueue.remove();	//rimuove in testa
	}
	
	
	// Removes desired graph node, then inserts into appropriate slot
	public void updateNodeDistance(Node n) {
		this.pQueue.remove(n);
		this.pQueue.add(n);
	}
	
	
        
}
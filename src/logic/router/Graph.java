package logic.router;

import java.util.ArrayList;

/**
 * 
 * @author vsutskever
 * Collections of functinos 
 */
public class Graph {
	
	private Node startNode;
	private Node destinationNode;
	private ArrayList<Node> allNodes = new ArrayList<Node>();
	private ArrayList<Integer> visitedNodes = new ArrayList<Integer>(); 


	public Graph(Node startNode, Node destinationNode) {
		this.startNode = startNode;
		this.destinationNode = destinationNode;
		this.discoverGraph();
	}


	
	public Node getStartNode() {
		return this.startNode;
	}
	
	
	public Node getDestinationNode() {
		return this.destinationNode;
	}


	
	public ArrayList<Node> getAllNodes() {
		return this.allNodes;
	}
	
	
	// discover all GraphNode and store them in allNodes list
	private void discoverGraph() {
		this.allNodes.add(this.startNode);
		addToVisited(this.startNode);
		
		for (Edge e: this.startNode.getOutGoingEdges()) {
			if (!isVisited(e.getNode())) {
				bfs(e.getNode());
			}
		}
		
		this.allNodes.add(this.destinationNode);	// l'ultimo elemento della lista deve essere il destinationNode
	}
	
	
	
	// Breath First Search traversal of the Graph
	private void bfs(Node n) {
		addToVisited(n);
		
		if (n.getId() == -1)
			return;
		
		this.allNodes.add(n);
		for (Edge e: n.getOutGoingEdges()) {
			if (!isVisited(e.getNode())) { 
				bfs(e.getNode());
			}
		}
	}
	
	
	
	private boolean isVisited(Node n) {
		return visitedNodes.contains(n.getId());
	}


	
	private void addToVisited(Node n) {
		this.visitedNodes.add(n.getId());
	}

	

        
}
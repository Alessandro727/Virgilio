package logic.router;

import java.util.ArrayList;
import java.util.List;

import model.User;


public class Router_Dijkstra extends Router {
	
	private final int INFINITY = Integer.MAX_VALUE;
	private List<Route> routeList; 
	
	private NodePriorityQueue priorityQ = new NodePriorityQueue();

	public Router_Dijkstra(Graph graph, User user, int maxTime, int maxWayPoints) {
		super(graph, user, maxTime, maxWayPoints);
		this.priorityQ.add(this.graph.getAllNodes());
	}
	

	@Override
	public void initializeNodeDistances() {
		for(Node n: this.graph.getAllNodes())
			n.setDistance(INFINITY);
		this.graph.getStartNode().setDistance(INFINITY);
	}

	
	@Override
	public void execute() {
		while (this.priorityQ.hasMore()) {
			Node n = this.priorityQ.remove();	// prende il primo Nodo della lista
			if (n.getId() == 0) {
				n.setDistance(0);	// serve per rimettere a zero la distanza del nodo start
			}
			for (Edge e: n.getOutGoingEdges()) {
				Node adjNode = e.getNode();
				/////
				if (!n.pathIsValid(adjNode.getId()))
					continue;
				/////
				int newPossiblePathCost = e.getCost() + n.getDistance();
				if (newPossiblePathCost < adjNode.getDistance() && newPossiblePathCost <= this.maxTime) {
					adjNode.setDistance(newPossiblePathCost);
					this.priorityQ.updateNodeDistance(adjNode);
					adjNode.setPrev(n);					
				}
			}
		}
	}

	
	@Override
	public List<Route> getTopKRoutes(int k) {
		Route route = new Route();
		Node curr = this.graph.getDestinationNode();
		route.add(curr);
		
		Node prev;
				
		while(curr.getPrev() != null) {
			prev = curr.getPrev();
			route.add(0, prev);
			if (prev.getId() == 0)
				break;
			else
				curr = prev;
		}
		
		if (route.getSize() >= 3) {
			List<Route> topKRoutes = new ArrayList<Route>();
			//route.calculateScore(user);
			topKRoutes.add(route);
			return topKRoutes;
		}			
		else
			return null;
	}
	
	@Override
	public List<Route> getRouteList() {
		
		return this.routeList;
	}
	

}

package logic.router;

import java.util.ArrayList;
import java.util.List;



import model.User;

public class Router_DijkstraMax extends Router {

	private final int MINUS_INFINITY = Integer.MIN_VALUE;
	
	public Router_DijkstraMax(Graph graph, User user, int maxTime, int maxWayPoints) {
		super(graph, user, maxTime, maxWayPoints);
	}

	@Override
	public void initializeNodeDistances() {
		for(Node n: this.graph.getAllNodes())
			n.setDistance(MINUS_INFINITY);
		this.graph.getStartNode().setDistance(0);
	}

	
	@Override
	public void execute() {
		Node startNode = this.graph.getStartNode();
		int maxPath = getLongestPathFromStart(startNode);
		System.out.println(maxPath);
	}
	
	
	public int getLongestPathFromStart(Node start) {
		int dist;
		int max = 0;		
		for(Edge e: start.getOutGoingEdges()) {
			Node adjNode = e.getNode();
			adjNode.setVisited(true);
			dist = e.getCost() + getLongestPathFromNode(adjNode, this.maxTime-e.getCost());
			if (dist > max && dist <= this.maxTime) {
				max = dist;
				upadatePrevList(adjNode);
				adjNode.setPrev(start);
			}
			adjNode.setVisited(false);
		}		
		return max;
	}
	
	
	
	public int getLongestPathFromNode(Node n, int time) {
		int dist;
		int max = 0;
		n.setVisited(true);
		for(Edge e: n.getOutGoingEdges()) {
			Node adjNode = e.getNode();			
			if (!adjNode.getVisited()) {
				dist = e.getCost() + getLongestPathFromNode(adjNode, time);
				if (dist > max && dist <= time) {
					max = dist;			
					n.setSucc(adjNode);
				}
			}
		}		
		n.setVisited(false);
		return max;
	}
	
	
	
	public void upadatePrevList(Node n) {
		Node curr = n;
		Node succ;
		while(curr.getSucc() != null) {
			succ = curr.getSucc();
			succ.setPrev(curr);
			curr = succ;
		}		
	}

	
	@Override
	public List<Route> getTopKRoutes(int k) {
		Route route = new Route();
		Node curr = this.graph.getStartNode();
		route.add(curr);
		
		Node succ;
				
		while(curr.getSucc() != null) {
			succ = curr.getSucc();
			route.add(succ);
			if (succ.getId() == -1)
				break;
			else
				curr = succ;
		}
		
		if (route.getSize() >= 3) {
			List<Route> topKRoutes = new ArrayList<Route>();
			topKRoutes.add(route);
			return topKRoutes;
		}			
		else
			return null;
	}

}

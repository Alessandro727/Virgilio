package logic.router;

import java.util.ArrayList;

import model.Venue;

public class Node implements Comparable<Node> {
	
	private Long id;
	private Venue venue;
	private ArrayList<Edge> outGoingEdges = new ArrayList<Edge>();
	private Integer distance;
	private int popularity;
	private boolean visited;
	private Node prev;
	private Node succ;
	private int costToSucc;
	
	// Constructor
	public Node(Venue venue) {
		this.id = venue.getId();
		this.venue = venue;
		this.outGoingEdges = new ArrayList<Edge>();
		this.popularity = venue.getCheckinsNumber();
		this.visited = false;		
	}
	
	
	
	public Long getId() {
		return this.id;
	}
	
	
	public Venue getVenue() {
		return this.venue;
	}
	
	
	public ArrayList<Edge> getOutGoingEdges() {
		return this.outGoingEdges;
	}
	
	
	public void AddOutgoingEdge(Node node, int cost) {
		this.outGoingEdges.add(new Edge(node, cost));
	}   
	
	
	public void SetOutgoingEdge(ArrayList<Edge> outGoingEdges) {
		this.outGoingEdges = outGoingEdges;
	}
	
	
	public Integer getDistance() {
		return this.distance;
	}

		
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
	public boolean getVisited() {
		return this.visited;
	}

		
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	
	public int getPopularity() {
		return this.popularity;
	}
	
	
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	
	
	
	public int getMrt() {
		return this.venue.getMacro_category().getMrt();
	}
	
	
	public Node getPrev() {
		return this.prev;
	}


	public void setPrev(Node prev) {
		this.prev = prev;
	}
	
	
	public Node getSucc() {
		return this.succ;
	}


	public void setSucc(Node succ) {
		this.succ = succ;
	}
	
	
	public int getCostToSucc() {
		return costToSucc;
	}



	public void setCostToSucc(int costToSucc) {
		this.costToSucc = costToSucc;
	}



	@Override
	public int compareTo(Node n) {
		//return this.distance.compareTo(n.getDistance());
		return n.getDistance().compareTo(this.distance);
	}
	
	
	public boolean pathIsValid(Long long1) {
		boolean isValid = true;
		Node prev = this.getPrev();
		while(prev != null && isValid) {
			if (prev.getId() == long1)
				isValid = false;
			prev = prev.getPrev();
		}
		return isValid;
	}

}
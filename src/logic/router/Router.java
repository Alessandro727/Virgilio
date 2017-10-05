package logic.router;

import java.util.List;

import model.User;

public abstract class Router {
	
	
	protected Graph graph;
	protected User user;
	protected int maxTime;
	protected int maxVenues;
	
	
	public Router(Graph graph, User user, int maxTime, int maxWayPoints) {
		this.graph = graph;
		this.user = user;
		this.maxTime = maxTime;
		if (maxWayPoints >= 8)
			this.maxVenues = 10;	// 8 waypoints + start + end
		else
			this.maxVenues = maxWayPoints + 2;	
		this.initializeNodeDistances();				
	}
	
	
	public int getMaxTime() {
		return this.maxTime;
	}
	
	
	public int getMaxVenues() {
		return this.maxVenues;
	}
	
	
	public abstract void initializeNodeDistances();
	
	
	/**
	 * execute the route inference
	 */
	public abstract void execute();
	
	
	public abstract List<Route> getTopKRoutes(int k);

}

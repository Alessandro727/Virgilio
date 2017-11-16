package logic.router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.User;


public class Router_Default extends Router {

	private final int INFINITY = Integer.MAX_VALUE;

	private List<String> visited;
	private List<Route> routeList;
	private Node lastInserted;
	private boolean includeFood;

	public Router_Default(Graph graph, User user, int maxTime, int maxWayPoints, boolean includeFood) {
		super(graph, user, maxTime, maxWayPoints);
		this.visited = new ArrayList<String>();
		this.routeList = new ArrayList<Route>();		
		this.includeFood = includeFood;
	}



	@Override
	public void initializeNodeDistances() {
		Node startNode = this.graph.getStartNode();
		for(Edge e: startNode.getOutGoingEdges()) {
			e.getNode().setDistance(e.getCost());
		}

		this.graph.getStartNode().setDistance(0);
		this.graph.getDestinationNode().setDistance(INFINITY);		
	}



	@Override
	public void execute() {
		Route start_end = new Route();
		Node start = this.graph.getStartNode();
		Node end = this.graph.getDestinationNode();


		if (this.includeFood) {
			Node foodNode = null;
			for(Node n: this.graph.getAllNodes())
				if(n.getId() > 0 && n.getVenue().getMacro_category().getId() == 5) {

					foodNode = new Node(n.getVenue());
					foodNode.SetOutgoingEdge(n.getOutGoingEdges());
					this.graph.getAllNodes().remove(n);
					foodNode.setSucc(end);
					for (Edge e: foodNode.getOutGoingEdges()) {
						if (e.getNode().getId() == end.getId()) {
							foodNode.setCostToSucc(e.getCost());
							break;
						}
					}
					start.setSucc(foodNode);
					for (Edge e: start.getOutGoingEdges()) {
						if (e.getNode().getId() == foodNode.getId()) {
							start.setCostToSucc(e.getCost());
							break;
						}
					}
					start_end.add(start);
					start_end.add(foodNode);
					start_end.add(end);
					break;
				}
		} else {
			start.setSucc(end);
			start.setCostToSucc(INFINITY);
			start_end.add(start);
			start_end.add(end);
		}


		Route candidateList = new Route();
		Node newNode;
		for(Node n: this.graph.getAllNodes()) {
			if (n.getId() != 0 && n.getId() != -1) {
				newNode = new Node(n.getVenue());
				newNode.SetOutgoingEdge(n.getOutGoingEdges());
				candidateList.add(newNode);
			}			
		}

		Route cloneStart_end;
		Route cloneCandidateList;		
		Node n;
		for (int i=0; i<candidateList.getSize(); i++) {
			cloneStart_end = start_end.cloneRoute(true);
			cloneCandidateList = candidateList.cloneRoute(false);
			n = cloneCandidateList.remove(i);
			cloneCandidateList.add(0, n);
			routeEnhancement(cloneStart_end, cloneCandidateList, true);
		}
	}




	public void routeEnhancement(Route route, Route candidateList, boolean firstCall) {
		if (candidateList.getSize() > 0) {
			if(!firstCall)
				sortCandidateList(candidateList, this.lastInserted);
			//sortCandidateList(candidateList, tour.get(tour.size()-2).getId());
			Node n;
			while ((candidateList.getSize() > 0) && (route.getSize() < this.maxVenues)) {
				n = candidateList.remove(0);
				Route newRoute = tryToInsertNewCandidate(route, n);
				if (newRoute != null)
					routeEnhancement(newRoute, candidateList, false);
			}
		}
		addToRouteList(route);
	}






	/**
	 * Inserisce, se possibile il nodo n in route, nella posizione che d� il minor tempo di percorrenza totale 
	 * 
	 * @param route
	 * @param n
	 * @return	una nuova route, se � ammissibile, null altrimenti 
	 */
	public Route tryToInsertNewCandidate(Route route, Node n) {
		int time = Integer.MAX_VALUE;
		int timeTemp;

		int position = -1;

		for(int i=1; i<route.getSize(); i++) {			
			route.addNode(i, n);			
			timeTemp = route.calculateRouteTime();
			route.removeNode(i);

			if (timeTemp <= this.maxTime && timeTemp <= time) {
				time = timeTemp;
				position = i;
			}
		}		

		if (position != -1) {
			this.lastInserted = new Node(n.getVenue()); //da levare se si ordina in base al penultimo elemento
			Route clonedRoute = route.cloneRoute(true);
			Node clonedNode = new Node(n.getVenue());
			clonedRoute.addNode(position, clonedNode);			
			return clonedRoute;
		}
		else
			return null;
	}





	/**
	 * Ordina in base a popolarit� del nodo e distanza dall'ultimo nodo inserito
	 * 
	 * @param candidateList	una lista di nodi candidati
	 * @param lastInserted	l'ultimo nodo inserito in route
	 */
	public void sortCandidateList(Route candidateList, Node lastInserted) {
		if (candidateList.getSize() <= 1)
			return;

		Map<Double, Route> map = new TreeMap<Double, Route>(Collections.reverseOrder());
		double alfa = 0.5;	// peso tappe del tour
		double beta = 0.01;	// peso distanza dall'ultimo nodo inserito				
		double score;

		for (Edge e: lastInserted.getOutGoingEdges()) {
			Node n = e.getNode();
			for (Node ncl: candidateList.getNodes()) {
				if (n.getId() == ncl.getId()) {
					score = (n.getPopularity()*0.1*alfa) - (e.getCost()*0.01*beta);
					if (map.containsKey(score)) {
						map.get(score).add(ncl);
					} else {
						Route route = new Route();
						route.add(ncl);
						map.put(score, route);
					}						
					candidateList.remove(ncl);
					break;
				}
			}
		}

		// se � rimasta qualche candidata...
		if (candidateList.getSize() > 0) {
			Route route = new Route();
			while (candidateList.getSize() > 0) {
				route.add(candidateList.getNode(0));
				candidateList.remove(0);
			}
			map.put(Double.MIN_VALUE, route);
		}

		// ricreo la candidateList, ma in maniera ordinata
		for(double d: map.keySet())
			for (Node n: map.get(d).getNodes())
				candidateList.add(n);		
	}






	/**
	 * Ordina in base a popolarit� del nodo, distanza dall'ultimo nodo inserito e peso utenti-tappe
	 * 
	 * @param candidateList	una lista di nodi candidati
	 * @param lastInserted	l'ultimo nodo inserito in route
	 */
	public void sortCandidateList2(Route candidateList, Node lastInserted) {
		if (candidateList.getSize() <= 1)
			return;

		if (user.getUsername().equals("guest")) {
			sortCandidateList(candidateList, lastInserted);
			return;
		}

		Map<Double, Route> map = new TreeMap<Double, Route>(Collections.reverseOrder());
		double alfa = 0.5;	// peso tappe del tour
		double beta = 0.4;	// peso distanza dall'ultimo nodo inserito
		double gamma = 2;	// peso pesi utente-venue
		double score;

		double AB;	// A�B
		double A;	// A^2
		double B;	// B^2


		for (Edge e: lastInserted.getOutGoingEdges()) {
			Node n = e.getNode();
			for (Node ncl: candidateList.getNodes()) {
				if (n.getId() == ncl.getId()) {
					AB = 0;
					A = 0;
					B = 0;	
					for (int i=1; i<user.getWeigths().length; i++) {
						AB += (user.getWeigth(i)) * (n.getVenue().getMacro_category().getWeight(i));
						A += Math.pow(user.getWeigth(i), 2);
						B += Math.pow(n.getVenue().getMacro_category().getWeight(i), 2);
					}
					score = (n.getPopularity()*0.1*alfa) - (e.getCost()*0.01*beta) + (( AB / Math.sqrt(A*B) )*gamma);
					if (map.containsKey(score)) {
						map.get(score).add(ncl);
					} else {
						Route route = new Route();
						route.add(ncl);
						map.put(score, route);
					}						
					candidateList.remove(ncl);
					break;
				}
			}
		}

		// se � rimasta qualche candidata...
		if (candidateList.getSize() > 0) {
			Route route = new Route();
			while (candidateList.getSize() > 0) {
				route.add(candidateList.getNode(0));
				candidateList.remove(0);
			}
			map.put(Double.MIN_VALUE, route);
		}

		// ricreo la candidateList, ma in maniera ordinata
		for(double d: map.keySet())
			for (Node n: map.get(d).getNodes())
				candidateList.add(n);		
	}






	/**
	 * Ritorna le top k routes
	 * 
	 * @param k		il numero di top route da ritornare	 * 
	 */
	@Override
	public List<Route> getTopKRoutes(int k) {
		Map<Double, List<Route>> map = this.sortRouteList();
		List<Route> topKRoutes = new ArrayList<Route>();

		if (map != null) {			

			int addedRoutes = 0;

			for(double d: map.keySet()) {
				if (addedRoutes >= k)
					break;
				for (Route route: map.get(d)) {
					if (addedRoutes >= k)
						break;
					if (!containsSameElementsOrIsSubset(route, topKRoutes)) {
						topKRoutes.add(route);
						addedRoutes++;
					}
				}			
			}
		}


		return topKRoutes;
	}



	public boolean containsSameElementsOrIsSubset(Route route, List<Route> list) {
		boolean containsSameElementsOrIsSubset = false;
		int i = 0;
		Route r;

		while (!containsSameElementsOrIsSubset && i<list.size()) {
			r = list.get(i);			
			if (route.getSize() == r.getSize()) {
				boolean sameElements = true;
				int j=0;
				for (Node n: route.getNodes()) {
					if (!r.containsId(n.getId())) {
						j++;
						if(j>3)	{
							sameElements = false;
							break;
						}
					}
				}
				if (sameElements)
					containsSameElementsOrIsSubset = true;
			}
			else {
				if (route.getSize() < r.getSize()) {
					boolean subset = true;
					for (Node n: route.getNodes()) {
						if (!r.containsId(n.getId())) {
							subset = false;
							break;
						}
					}
					if (subset)
						containsSameElementsOrIsSubset = true;
				}
			}
			i++;
		}

		return containsSameElementsOrIsSubset;
	}





	public void addToRouteList(Route route) {		
		String s = "";
		for(Node n: route.getNodes())
			s += n.getId();			

		if (!this.visited.contains(s)) {			
			this.visited.add(s);			
			if (route.calculateRouteTime() <= this.maxTime) {
				route.calculateScore(user);
				this.routeList.add(route);
			}

		}
	}






	/**
	 * @return	una mappa con i tour presenti nella lista tourList, ordinati tramite la funzione getScore
	 */
	public Map<Double, List<Route>> sortRouteList() {
		Map<Double, List<Route>> map = new TreeMap<Double, List<Route>>(Collections.reverseOrder());
		double score;
		List<Route> list;

		for (Route route: this.routeList) {
			score = route.getScore();
			if (map.containsKey(score)) {
				list = map.get(score);
				list.add(route);
				map.put(score, list);
			} else {
				list = new ArrayList<Route>();
				list.add(route);
				map.put(score, list);
			}
		}

		if (map.size() > 0)
			return map;
		else 
			return null;
	}	






}

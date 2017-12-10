package logic.router;

import java.util.ArrayList;
import java.util.List;

import com.flickr4java.flickr.groups.Category;

import model.Checkin;
import model.User;
import model.Venue;


public class Route {
	
	private List<Node> route;
	private double score;
	
	public Route() {
		this.setRoute(new ArrayList<Node>());
	}

	
	public List<Node> getRoute() {
		return this.route;
	}

	public void setRoute(List<Node> route) {
		this.route = route;
	}
	
	
	public double getScore() {
		return this.score;
	}
	
	
	public void setScore(double score) {
		this.score = score;
	}
	
	
	public Node getNode(int position) {
		return this.route.get(position);
	}
	
	public List<Node> getNodes() {
		return this.route;
	}
	
	
	public void add(Node n) {
		this.route.add(n);
	}
	
	public void add(int position, Node n) {
		this.route.add(position, n);
	}
	
	
	public Node remove(int position) {
		return this.route.remove(position);
	}
	
	
	public void remove(Node n) {
		this.route.remove(n);
	} 
	
	public int getSize() {
		return this.route.size();
	}
	
	
	public boolean containsId(long long1) {
		boolean contains = false;
		for(Node n: route)
			if (n.getId()==long1) {
				contains = true;
				break;
			}
		return contains;
	}
	
	
	/**
	 * Inserisce il nodo n alla posizione i (quelli dopo scalano)
	 * 
	 * @param i		la posizione in cui inserire il nodo n
	 * @param n		il nodo da inserire
	 */	
	public void addNode(int i, Node n) {
		route.get(i-1).setSucc(n);
		for(Edge e: route.get(i-1).getOutGoingEdges()) {
			if (e.getNode().getId() == n.getId()) {
				route.get(i-1).setCostToSucc(e.getCost());
				break;
			}
		}
		
		n.setSucc(route.get(i));
		for(Edge e: n.getOutGoingEdges()) {
			if (e.getNode().getId() == route.get(i).getId()) {
				n.setCostToSucc(e.getCost());
				break;
			}
		}
		route.add(i ,n);
	}
	
	
	
	/**
	 * Rimuove il nodo che si trova alla posizione i (quelli prima scalano)
	 * 
	 * @param i		l'indice del nodo da rimuovere
	 */
	public void removeNode(int i) {
		route.get(i-1).setSucc(route.get(i+1));
		
		if (route.size() == 3)
			route.get(i-1).setCostToSucc(Integer.MAX_VALUE);
		else {
			for(Edge e: route.get(i-1).getOutGoingEdges()) {
				if (e.getNode().getId() == route.get(i+1).getId()) {
					route.get(i-1).setCostToSucc(e.getCost());
					break;
				}
			}
		}
		
		route.remove(i);
	}
	
	
	
	/**
	 * Copia la lista in una nuova lista e la ritorna
	 * 
	 * @param cloneSucc		dice se va copiata anche la propriet� setSucc
	 * @return	un clone di route
	 */
	public Route cloneRoute(boolean cloneSucc) {
		Route clone = new Route();
		
		Node curr, succ = null;
		
		if (cloneSucc) {
			
			System.out.println("ROUTE: "+route);
			
			curr = new Node(route.get(0).getVenue());
			curr.setCostToSucc(route.get(0).getCostToSucc());
			curr.SetOutgoingEdge(route.get(0).getOutGoingEdges());
			succ = new Node(route.get(1).getVenue());
			succ.setCostToSucc(route.get(1).getCostToSucc());
			succ.SetOutgoingEdge(route.get(1).getOutGoingEdges());
			curr.setSucc(succ);			
			clone.add(curr);
			
			for(int i=2; i<route.size(); i++) {
				curr = succ;
				
				succ = new Node(route.get(i).getVenue());
				succ.setCostToSucc(route.get(i).getCostToSucc());
				succ.SetOutgoingEdge(route.get(i).getOutGoingEdges());
				
				curr.setSucc(succ);				
				clone.add(curr);
			}	
			clone.add(succ);
			/*for(int i=route.size()-1; i>0; i--) {
				curr = new Node(route.get(i).getVenue());
				curr.SetOutgoingEdge(route.get(i).getOutGoingEdges());
				
				prev = new Node(route.get(i-1).getVenue());
				prev.SetOutgoingEdge(route.get(i-1).getOutGoingEdges());
				
				prev.setSucc(curr);
				prev.setCostToSucc(route.get(i-1).getCostToSucc());
				
				clone.add(0, curr);
			}	
			clone.add(0, prev);*/
		}
		else {
			for (Node n: route) {
				curr = new Node(n.getVenue());
				curr.SetOutgoingEdge(n.getOutGoingEdges());
				clone.add(curr);
			}
		}					
		
		return clone;
	}
	
	
	
	
	/**
	 * @return il tempo di percorrenza della route
	 */
	public int calculateRouteTime() {
		int time = 0;
		
		for (Node n: route) {
			time += n.getMrt();
			if (n.getSucc() != null)
				time += n.getCostToSucc();
		}				
		
		return time;
	}
	
	
	
	/**
	 * Crea un lista di Venue a partire da una lista di Node
	 * 
	 * @return		una lista di Venue
	 */
	public List<Venue> getVenueList() {
		List<Venue> venueList = new ArrayList<Venue>();
		for (Node n: route)
			venueList.add(n.getVenue());
		return venueList;
	}
	
	
	
	
	/**
	 * @return	lo score della route
	 */
	public void calculateScore(User user) {
		double alfa = 0.4;	// peso popolarit� del route
		double beta = 0.2;	// peso distanza totale del route
		double gamma = 0.05;	// peso numero tappe presenti
		double delta = 2;	// peso pesi utente-venue
		double teta = 5;	// peso amicizie
		
		double scoreAlfa = 0;
		double scoreBeta = 0;
		double scoreGamma = this.getSize();
		double scoreDelta = 0;
		double scoreTeta = 0;
		
		/*
		 * CosSim(A,B) = (A�B) / sqrt((A^2)(B^2)) 
		 */
		
		double AB;	// A�B
		double A = 0;	// A^2
		double B;	// B^2
		
		for (int i=1; i<user.getWeigths().length; i++) {
			A += Math.pow(user.getWeigth(i), 2);			
		}
		
		for (Node n: this.getNodes()) {
			scoreAlfa += n.getPopularity();
			if (n.getSucc() != null)
				scoreBeta += n.getCostToSucc();
			
			if (n.getId() != 0 && n.getId() != -1) {
				AB = 0;			
				B = 0;		
				for (int i=1; i<user.getWeigths().length; i++) {
					AB += (user.getWeigth(i)) * (n.getVenue().getMacro_category().getWeight(i));				
					B += Math.pow(n.getVenue().getMacro_category().getWeight(i), 2);
				}
				scoreDelta += ( AB / Math.sqrt(A*B) );
			}
			
			for(int id: user.getFriends()) {
				for (Checkin c: n.getVenue().getCheckins()) {
					if (c.getUser_id() == id) {
						scoreTeta += 1;
					}
				}
			}
		}
		
		scoreAlfa = scoreAlfa * 0.3;	// normalizzo
		scoreBeta = scoreBeta * 0.01;	// normalizzo
		scoreGamma = scoreGamma * 0.05;	// normalizzo
		scoreTeta = scoreTeta * 0.5;	// normalizzo
		
		double score = (alfa*scoreAlfa)-(beta*scoreBeta)+(gamma*scoreGamma)+(delta*scoreDelta)+(teta*scoreTeta);
		this.setScore(score);
		
	}
	
	
	
	/**
	 * @return	lo score della route senza l'informazione sociale
	 */
	public void calculateScoreWithoutSocial(User user) {
		double alfa = 0.4;	// peso popolarit� del route
		double beta = 0.2;	// peso distanza totale del route
		double gamma = 0.5;	// peso numero tappe presenti
		double delta = 2;	// peso pesi utente-venue
		double diversity = 2;
		
		double scoreAlfa = 0;
		double scoreBeta = 0;
		double scoreGamma = this.getSize();
		double scoreDelta = 0;
		
		/*
		 * CosSim(A,B) = (A�B) / sqrt((A^2)(B^2)) 
		 */
		
		double AB;	// A�B
		double A = 0;	// A^2
		double B;	// B^2
		
		for (int i=1; i<user.getWeigths().length; i++) {
			A += Math.pow(user.getWeigth(i), 2);			
		}
		
		for (Node n: this.getNodes()) {
			scoreAlfa += n.getPopularity();
			if (n.getSucc() != null)
				scoreBeta += n.getCostToSucc();
			
			if (n.getId() != 0 && n.getId() != -1) {
				AB = 0;			
				B = 0;		
				for (int i=1; i<user.getWeigths().length; i++) {
					AB += (user.getWeigth(i)) * (n.getVenue().getMacro_category().getWeight(i));				
					B += Math.pow(n.getVenue().getMacro_category().getWeight(i), 2);
				}
				scoreDelta += ( AB / Math.sqrt(A*B) );
			}			
			
		}
		List<String> category = new ArrayList<>();
		for (Node node : this.getNodes())	{
			if(!category.contains(node.getVenue().getCategory_fq()))
				category.add(node.getVenue().getCategory_fq());
		}
		
		scoreAlfa = scoreAlfa * 0.3;	// normalizzo
		scoreBeta = scoreBeta * 0.01;	// normalizzo
		scoreGamma = scoreGamma * 0.05;	// normalizzo
		
		double score = (alfa*scoreAlfa)-(beta*scoreBeta)+(gamma*scoreGamma)+(delta*scoreDelta)+(diversity*category.size());
		this.setScore(score);
		
	}

}

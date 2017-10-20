package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import logic.LatLngSquare;
//import logic.VenueSearcher;
import logic.router.Route;
import logic.router.Router_Default;
import logic.router.Graph;
import logic.router.Node;
import logic.router.Router;
import logic.router.JenaManagerForPlace;
import model.Context;
import model.MacroCategory;
import model.Scenario;
import model.User;
import model.Venue;
import socialAndServices.Google;
import util.Utilities;

/**
 * Servlet implementation class FindTopKPopularRoutes
 */
@WebServlet("/FindTopKPopularRoutes")
public class FindTopKPopularRoutes extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindTopKPopularRoutes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User)session.getAttribute("user");
	
		String prossimaPagina = null;
		String start = request.getParameter("txtStart");
		String end = request.getParameter("txtEnd");
		String mode = request.getParameter("ddlMode");
		int availableTime = Integer.parseInt(request.getParameter("ddlHours"))*60;	// hours to minutes
		availableTime += Integer.parseInt(request.getParameter("ddlMinutes"));		// minutes
		int maxWayPoints = Integer.parseInt(request.getParameter("txtMaxWayPoints"));
		
		
		
		String[] categories = request.getParameterValues("cbCategories");
		
		
		for (int i=0; i<categories.length; i++)	{
			System.out.println(categories[i]);
		}
		
	
		Google google = new Google();
		
		MacroCategory mc = new MacroCategory();
		mc.setId(12);	// 12 = id macro categoria fittizia
		mc.setMacro_category_fq("Macro Categoria Fittizia");
		mc.setMrt(0);
		
		Venue startVenue = google.getCoordinatesFromAddress(start);
		startVenue.setId((long) 0);	// 0 is the id of the source node of Router algorithm
		startVenue.setMacro_category(mc);
		
		Venue endVenue = google.getCoordinatesFromAddress(end);
		
		endVenue.setId((long) -1);		// -1 is the id of the destination node of Router algorithm
		endVenue.setMacro_category(mc);
		
		List<Venue> venuesInTheSquare = null;
		List<Route> topKroute = null;
		
		if (startVenue.getStatus().equals("OK") && endVenue.getStatus().equals("OK")) {
			venuesInTheSquare = new ArrayList<Venue>();
			venuesInTheSquare.add(startVenue);
			venuesInTheSquare.add(endVenue);
			
			Context context = new Context();
			context.setMode(mode);
			context.setCity("");
			context.setStart(start);
			context.setEnd(end);
			context.setTime(availableTime);
			context.setSunny(Utilities.isSunny(startVenue, availableTime));
			Scenario scenario = new Scenario(0, context);
			for (int i=0; i<categories.length; i++)	{
				if (categories[i].equals("5"))	
					scenario.setFood(true);
			}
			
			// TODO ricerca dei posti dai LOD invece che con getVenuesWithContextAndCategories(30, categories);
			
			double lat = middlePoint(startVenue.getLatitude(),endVenue.getLatitude());
			
			double lng = middlePoint(startVenue.getLongitude(),endVenue.getLongitude());
		
			
			venuesInTheSquare = JenaManagerForPlace.retrivePlacesNodes(lat, lng, 0.1, categories);
			
			
			
//			LatLngSquare llSquare = new LatLngSquare(venuesInTheSquare);
//			VenueSearcher searcher = new VenueSearcher(llSquare, scenario);
//			venuesInTheSquare = searcher.getVenuesWithContextAndCategories(30, categories);
			venuesInTheSquare.add(0, startVenue);
			venuesInTheSquare.add(endVenue);	
						
		
			for (Venue venue : venuesInTheSquare) {
				System.out.println(venue.getName_fq());
			}
			
			topKroute = runDijkstraAlgorithm(venuesInTheSquare,
											mode,
											availableTime,
											maxWayPoints,
											user,
											scenario.getFood(),
											google);
			int size = topKroute.size();
			if (size >= 2) {
				prossimaPagina = "/findTopKPopularRoutes3.jsp";
				request.setAttribute("topKroute", topKroute);
				request.setAttribute("mode", mode);
			} else {
				if (size == 1) {
					prossimaPagina = "/findTopKPopularRoutes2.jsp";
					request.setAttribute("topKroute", topKroute);
					request.setAttribute("mode", mode);
				} else {	// size = 0
					prossimaPagina = "/findTopKPopularRoutes1.jsp";
					request.setAttribute("error", "no venues found or time too short");
				}
			}						
		} 
		else {
			prossimaPagina = "/findTopKPopularRoutes1.jsp";
			if (!startVenue.getStatus().equals("OK"))
				request.setAttribute("error", "Start address: " + startVenue.getStatus());
			if (!endVenue.getStatus().equals("OK"))
				request.setAttribute("error", "End address: " + endVenue.getStatus());
		}		
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}

	private double middlePoint(String v1, String v2) {
		double middlePoint = Double.valueOf(v1)+Double.valueOf(v2);
		return middlePoint/2.0;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	
	
	public static List<Route> runDijkstraAlgorithm(List<Venue> venuesInTheSquare, String mode, int availableTime, int maxWayPoints, User user, boolean food, Google google) {
		
		List<Node> nodeList = new ArrayList<Node>();
		for(Venue v: venuesInTheSquare) {
			nodeList.add(new Node(v));
		}
		
		System.out.println("creo i nodi");
		int time;

		for(Node n1: nodeList) {
			for(Node n2: nodeList) {
				if ((n1.getId()!=-1) && (n2.getId()!=0) && (n1.getId() != n2.getId())) {
					time = google.getTimeBetweenTwoPoints(n1.getVenue(), n2.getVenue(), mode);	//minutes
					if ((time != -1) && !(n1.getId()==0 && n2.getId()==-1))
						n1.AddOutgoingEdge(n2, time);
				}
			}
		}
		/*for(int i=0; i<nodeList.size(); i++) {
			for(int j=0; j<nodeList.size(); j++) {
				if ((nodeList.get(i).getId()!=-1) && (nodeList.get(j).getId()!=0) && (nodeList.get(i).getId() != nodeList.get(j).getId())) {
					time = google.getTimeBetweenTwoPoints(nodeList.get(i).getVenue(), nodeList.get(j).getVenue(), mode);	//minutes
					if ((time != -1) && !(nodeList.get(i).getId()==0 && nodeList.get(j).getId()==-1)) {
						nodeList.get(i).AddOutgoingEdge(nodeList.get(j), time);
						System.out.println("nodeList.get(" + i + ").AddOutgoingEdge(nodeList.get(" + j + "), " + time + ");" );
					}
				}
			}
		}*/
		System.out.println("nodi creati...eseguo router_default");
		Graph graph = new Graph(nodeList.get(0), nodeList.get(nodeList.size()-1));
        Router router = new Router_Default(graph, user, availableTime, maxWayPoints, food);
        router.execute();
        System.out.println("router_default eseguito...eseguo getTopKRoutes");
        List<Route> topKRoutes = router.getTopKRoutes(5);	
        System.out.println("getTopKRoutes eseguito");
        
        if (topKRoutes.size() == 0) {
        	time = google.getTimeBetweenTwoPoints(venuesInTheSquare.get(0), venuesInTheSquare.get(venuesInTheSquare.size()-1), mode);
        	if (time <= availableTime) {
        		Route route = new Route();
        		route.add(new Node(venuesInTheSquare.get(0)));
        		route.add(new Node(venuesInTheSquare.get(venuesInTheSquare.size()-1)));
        		topKRoutes.add(route);        		
        	}        		
        }
                
        
        return topKRoutes;
	}
	
}

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

import postgres.PersistenceException;
import postgres.ScenarioPostgres;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.entities.CompactVenue;
import socialAndServices.Foursquare;
import socialAndServices.Google;
import util.CreateNodes;
import logic.LatLngSquare;
import logic.VenueSearcher;
import logic.router.Graph;
import logic.router.Node;
import logic.router.Route;
import logic.router.Router;
import logic.router.Router_Default;
import logic.router.Router_DefaultWithoutSocial;
import logic.router.Router_Dijkstra;
import model.Context;
import model.MacroCategory;
import model.Scenario;
import model.User;
import model.Venue;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User)session.getAttribute("user");
		Scenario scenario = (Scenario)session.getAttribute("scenario");
		scenario.setFood(Boolean.parseBoolean(request.getParameter("rbFood")));
		try {
			ScenarioPostgres.persistScenario(scenario);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		Context context = scenario.getContext();
		if (context.getMode().equals("a piedi"))
			context.setMode("walking");
		else
			context.setMode("driving");
		
		Google google = new Google();
		
		MacroCategory mc = new MacroCategory();
		mc.setId(0);	// 0 = id fittizio (non corrisponde a nessuna)
		mc.setMrt(0);
		
		Venue startVenue = google.getCoordinatesFromAddress(context.getStart() + ", " + context.getCity());
		startVenue.setId(0);	// 0 is the id of the source node of Router algorithm
		startVenue.setName_fq(context.getStart());
		startVenue.setMacro_category(mc);
		
		Venue endVenue = google.getCoordinatesFromAddress(context.getEnd() + ", " + context.getCity());
		endVenue.setId(-1);		// -1 is the id of the destination node of Router algorithm
		endVenue.setName_fq(context.getEnd());
		endVenue.setMacro_category(mc);
		
		List<Venue> venuesStartEnd = new ArrayList<Venue>();
		venuesStartEnd.add(startVenue);
		venuesStartEnd.add(endVenue);
		LatLngSquare llSquare = new LatLngSquare(venuesStartEnd);
		VenueSearcher searcher = new VenueSearcher(llSquare, scenario);
		
		List<Venue> venuesWithoutContext = searcher.getVenuesWithoutContext(30);
		venuesWithoutContext.add(0, startVenue);
		venuesWithoutContext.add(endVenue);
		
		List<Venue> venuesWithContext = searcher.getVenuesWithContext(25);
		venuesWithContext.add(0, startVenue);
		venuesWithContext.add(endVenue);
		
		System.out.println("CREO GRAFO SENZA CONTESTO");
		//Graph graphWithoutContext = createGraph(venuesWithoutContext, context.getMode(), google);
		Graph graphWithoutContext = CreateNodes.getGrafoSenzaContesto(venuesWithoutContext, context.getId());
		System.out.println("GRAFO SENZA CONTESTO CREATO");
		Graph graphWithContext = createGraph(venuesWithContext, context.getMode(), google);
		
		List<Route> severalRoutes = new ArrayList<Route>();
		severalRoutes.add(runDijkstraAlgorithm(venuesWithoutContext, context, user, graphWithoutContext));
		severalRoutes.add(runRouterDefaultAlgorithmWithoutContextAndSocial(venuesWithoutContext, scenario, user, graphWithoutContext));
		severalRoutes.add(runRouterDefaultAlgorithmWithoutSocial(venuesWithContext, scenario, user, graphWithContext));
		severalRoutes.add(runRouterDefaultAlgorithm(venuesWithContext, scenario, user, graphWithContext));
		
		
		request.setAttribute("severalRoutes", severalRoutes);
		request.setAttribute("mode", context.getMode());
		request.setAttribute("scenario", scenario);
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher("/test2.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	
	
	
	public static Graph createGraph(List<Venue> venues, String mode, Google google) {
		List<Node> nodeList = new ArrayList<Node>();
		for(Venue v: venues) {
			nodeList.add(new Node(v));
		}
		
		System.out.println("creo i nodi");
		int time;
		/*for(Node n1: nodeList) {
			for(Node n2: nodeList) {
				if ((n1.getId()!=-1) && (n2.getId()!=0) && (n1.getId() != n2.getId())) {
					time = google.getTimeBetweenTwoPoints(n1.getVenue(), n2.getVenue(), mode);	//minutes
					if ((time != -1) && !(n1.getId()==0 && n2.getId()==-1))
						n1.AddOutgoingEdge(n2, time);
				}
			}
		}*/
		for(int i=0; i<nodeList.size(); i++) {
			for(int j=0; j<nodeList.size(); j++) {
				if ((nodeList.get(i).getId()!=-1) && (nodeList.get(j).getId()!=0) && (nodeList.get(i).getId() != nodeList.get(j).getId())) {
					time = google.getTimeBetweenTwoPoints(nodeList.get(i).getVenue(), nodeList.get(j).getVenue(), mode);	//minutes
					if ((time != -1) && !(nodeList.get(i).getId()==0 && nodeList.get(j).getId()==-1)) {
						nodeList.get(i).AddOutgoingEdge(nodeList.get(j), time);
						System.out.println("nodeList.get(" + i + ").AddOutgoingEdge(nodeList.get(" + j + "), " + time + ");" );
					}
				}
			}
		}
		System.out.println("nodi creati...eseguo router_default");
		Graph graph = new Graph(nodeList.get(0), nodeList.get(nodeList.size()-1));
		
		return graph;
	}
	
	
	
	
	
	// algoritmo dijkstra, senza contesto n� personalizzazione
	public static Route runDijkstraAlgorithm(List<Venue> venues, Context context, User user, Graph graph) {
		Router router = new Router_Dijkstra(graph, user, context.getTime(), 8);
        router.execute();
        System.out.println("router_dijkstra eseguito...eseguo getTopKRoutes");
        List<Route> topKRoutes = router.getTopKRoutes(1);	// la prima route
        System.out.println("getTopKRoutes eseguito");
        
        if (topKRoutes.size() == 0) {
        	Route route = new Route();
        	route.add(new Node(venues.get(0)));
        	route.add(new Node(venues.get(venues.size()-1)));
        	topKRoutes.add(route);        		
        }
        
        CompactVenue cv;
        try {
            for (Venue v: topKRoutes.get(0).getVenueList())
            	if (v.getFoursquare_id() == null) {
            		cv = Foursquare.searchSingleVenueMatch(v);
            		if (cv != null)
            			v.setFoursquare_id(cv.getId());
            	}
        } catch (FoursquareApiException e) {
			e.printStackTrace();
		}        
        
        return topKRoutes.get(0);
	}
	
	
	
	// algoritmo router_default, senza contesto n� sociale
	public static Route runRouterDefaultAlgorithmWithoutContextAndSocial(List<Venue> venues, Scenario scenario, User user, Graph graph) {
		Router router = new Router_DefaultWithoutSocial(graph, user, scenario.getContext().getTime(), 8, scenario.getFood());		
        router.execute();
        System.out.println("router_default senza contesto n� sociale eseguito...eseguo getTopKRoutes");
        List<Route> topKRoutes = router.getTopKRoutes(1);	// la prima route
        System.out.println("getTopKRoutes eseguito");
        
        if (topKRoutes.size() == 0) {
        	Route route = new Route();
        	route.add(new Node(venues.get(0)));
        	route.add(new Node(venues.get(venues.size()-1)));
        	topKRoutes.add(route);        		
        }
        
        CompactVenue cv;
        try {
            for (Venue v: topKRoutes.get(0).getVenueList())
            	if (v.getFoursquare_id() == null) {
            		cv = Foursquare.searchSingleVenueMatch(v);
            		if (cv != null)
            			v.setFoursquare_id(cv.getId());
            	}
        } catch (FoursquareApiException e) {
			e.printStackTrace();
		}        
        
        return topKRoutes.get(0);
	}
	

	
	// algoritmo router_default, con contesto e personalizzazione, senza sociale
	public static Route runRouterDefaultAlgorithmWithoutSocial(List<Venue> venues, Scenario scenario, User user, Graph graph) {
		Router router = new Router_DefaultWithoutSocial(graph, user, scenario.getContext().getTime(), 8, scenario.getFood());
        router.execute();
        System.out.println("router_default senza sociale eseguito...eseguo getTopKRoutes");
        List<Route> topKRoutes = router.getTopKRoutes(1);	// la prima route
        System.out.println("getTopKRoutes eseguito");
        
        if (topKRoutes.size() == 0) {
        	Route route = new Route();
        	route.add(new Node(venues.get(0)));
        	route.add(new Node(venues.get(venues.size()-1)));
        	topKRoutes.add(route);        		
        }
        
        CompactVenue cv;
        try {
            for (Venue v: topKRoutes.get(0).getVenueList())
            	if (v.getFoursquare_id() == null) {
            		cv = Foursquare.searchSingleVenueMatch(v);
            		if (cv != null)
            			v.setFoursquare_id(cv.getId());
            	}
        } catch (FoursquareApiException e) {
			e.printStackTrace();
		}        
        
        return topKRoutes.get(0);
	}
	
	
	// algoritmo router_default, con contesto, personalizzazione e sociale	
	public static Route runRouterDefaultAlgorithm(List<Venue> venues, Scenario scenario, User user, Graph graph) {
		Router router = new Router_Default(graph, user, scenario.getContext().getTime(), 8, scenario.getFood());
        router.execute();
        System.out.println("router_default eseguito...eseguo getTopKRoutes");
        List<Route> topKRoutes = router.getTopKRoutes(1);	// la prima route
        System.out.println("getTopKRoutes eseguito");
        
        if (topKRoutes.size() == 0) {
        	Route route = new Route();
        	route.add(new Node(venues.get(0)));
        	route.add(new Node(venues.get(venues.size()-1)));
        	topKRoutes.add(route);        		
        }
        
        CompactVenue cv;
        try {
            for (Venue v: topKRoutes.get(0).getVenueList())
            	if (v.getFoursquare_id() == null) {
            		cv = Foursquare.searchSingleVenueMatch(v);
            		if (cv != null)
            			v.setFoursquare_id(cv.getId());
            	}
        } catch (FoursquareApiException e) {
			e.printStackTrace();
		}        
        
        return topKRoutes.get(0);
	}
	
	
}

package servlet;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import logic.router.Route;
import logic.router.Router_Default;
import logic.Filter;
import logic.router.FoursquareManagerForPlace;
import logic.router.Graph;
import logic.router.JenaManagerForBook;
import logic.router.JenaManagerForMovies;
import logic.router.Node;
import logic.router.Router;
import logic.router.JenaManagerForPlace;
import logic.router.JenaManagerForTraks;
import model.Book;
import model.MacroCategory;
import model.Movie;
import model.Object;
import model.Singer;
import model.User;
import model.Venue;
import postgres.CheckinPostgres;
import postgres.PersistenceException;
import postgres.UserPostgres;
import postgres.VenuePostgres;
import scala.Tuple2;

import socialAndServices.Google;
import util.JsonReader;
import util.KMeans;
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


		Tuple2<List<String>, List<Long>> categoriesAndSameUsers = userCategoryAndSameUsers(user);


		List<String> categories = categoriesAndSameUsers._1();
		List<Long> similarUsers = categoriesAndSameUsers._2();

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

			double lat = Utilities.middlePoint(startVenue.getLatitude(),endVenue.getLatitude());

			double lng = Utilities.middlePoint(startVenue.getLongitude(),endVenue.getLongitude());

			boolean food = setCategoryWithContextAndCheckFood(categories, startVenue, availableTime);

			//
//			try {
//				venuesInTheSquare = JenaManagerForPlace.retriveNodes(lat, lng, 0.1, categories);
//			} catch (PersistenceException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

			String ll = String.valueOf(lat)+","+String.valueOf(lng);
									
			venuesInTheSquare = FoursquareManagerForPlace.searchVenues(ll, categories);



			System.out.println("FINALVENUELIST =  "+venuesInTheSquare.size());



			addBookMovieSingerForUserAndPlaceWithoutContext(lat, lng, session);

			System.out.println("FINALVENUELIST =  "+venuesInTheSquare.size());


			List<Venue> finalVenuesList = Filter.filterVenueWithRecommendationAlgorithm(venuesInTheSquare, similarUsers, user, lat, lng, maxWayPoints);



			System.out.println("FINALVENUELIST =  "+finalVenuesList.size());


			finalVenuesList = JsonReader.filterClosedVenue(finalVenuesList, categories);

			System.out.println("FINALVENUELIST =  "+finalVenuesList.size());

			finalVenuesList = finalVenuesList.subList(1, 15);

			Filter.filterFoodVenues(finalVenuesList);

			Collections.shuffle(finalVenuesList);




			finalVenuesList.add(0, startVenue);
			finalVenuesList.add(endVenue);	




			for (Venue venue : finalVenuesList) {
				System.out.println(venue.getName_fq());
			}

			topKroute = runDijkstraAlgorithm(finalVenuesList,
					mode,
					availableTime,
					maxWayPoints,
					user,
					food,
					google);
			int size = topKroute.size();

			if (size >= 2) {
				prossimaPagina = "/routes.jsp";
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
		session.setAttribute("topKroute", topKroute);
		session.setAttribute("mode", mode);
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
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
		System.out.println("NUMERO DI ROTTE PRIMA DELLA TOP K: "+router.getRouteList().size());
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

	public static void makeNodes(List<Venue> venuesInTheSquare, String mode, Google google)	{
		List<Node> nodeList = new ArrayList<Node>();
		for(Venue v: venuesInTheSquare) {
			nodeList.add(new Node(v));
		}
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
	}


	public static Tuple2<List<String>, List<Long>> userCategoryAndSameUsers(User user)	{


		Map<Long, Integer> mapUserCluster = new HashMap<>();
		double[][] centroidCluster= new double[55][10];

		long idUser = 0;
		try {
			System.out.println(user.getUsername());

			idUser = UserPostgres.retriveUserIdByUsername(user.getUsername());
		} catch (PersistenceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			mapUserCluster = KMeans.clusterResult();
			centroidCluster = KMeans.getFirstCentroids();
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> categories = new ArrayList<>();
		String[] temp = new String[1];
		int numCluster = mapUserCluster.get(idUser-1);

		for (int j=0; j<centroidCluster[numCluster].length; j++)	{
			if(centroidCluster[numCluster][j]==1)	{
				temp[0] = String.valueOf(j+1);
				categories.add(temp[0]);
			}		
		}

		List<Long> usersSameCluster = new ArrayList<>();

		for (Entry<Long, Integer> entry : mapUserCluster.entrySet()) {
			if (Objects.equals(numCluster, entry.getValue())) {
				usersSameCluster.add(entry.getKey());
			}
		}

		Tuple2<List<String>, List<Long>> tupla = new Tuple2<List<String>, List<Long>>(categories,usersSameCluster);
		return tupla;

	}


	public static void addBookMovieSingerForUserAndPlaceWithoutContext(double lat, double lng, HttpSession session) throws IOException	{

		JenaManagerForBook jBook = new JenaManagerForBook();
		JenaManagerForMovies jMovies = new JenaManagerForMovies();
		JenaManagerForTraks jTrack = new JenaManagerForTraks();

		Map<Long, Object> userBooks = new HashMap<Long, Object>();
		Map<Long, Object> userMovies = new HashMap<Long, Object>();
		Map<Long, Object> userTracks = new HashMap<Long, Object>();

		userBooks = jBook.retriveNodes(lat, lng, 0.2);
		userMovies = jMovies.retriveNodes(lat, lng, 0.2);
		userTracks = jTrack.retriveNodes(lat, lng, 0.2);

		Book book =  (Book) Book.weightedChoice(userBooks);
		Movie movie = (Movie) Movie.weightedChoice(userMovies);
		Singer singer = (Singer) Singer.weightedChoice(userTracks);


		String linkBook = JsonReader.getBookImage(book.getISBN());

		while (linkBook.equals("not found"))	{
			book =  (Book) Book.weightedChoice(userBooks);
			linkBook = JsonReader.getBookImage(book.getISBN());
		}

		String textBook = "https://www.bookfinder.com/search/?author=&title=&lang=en&isbn="+book.getISBN()+"&new_used=*&destination=it&currency=EUR&mode=basic&st=sr&ac=qr";

		String linkMovie = movie.getExternalLink();
		String posterUrl = movie.getImage();

		String singerCover = singer.getImage();
		String singerAlbum = singer.getExternalLink();

		String singerName = singer.getName();


		session.setAttribute("linkBook", linkBook);
		session.setAttribute("posterUrl", posterUrl);
		session.setAttribute("textBook", textBook);
		session.setAttribute("linkMovie", linkMovie);
		session.setAttribute("singerCover", singerCover);
		session.setAttribute("singerAlbum", singerAlbum);
		session.setAttribute("singerName", singerName);
	}

	public static synchronized boolean setCategoryWithContextAndCheckFood(List<String> categories, Venue startVenue, int availableTime)	{

		boolean food = false;

		System.out.println("\n");
		System.out.println("\n");
		System.out.println("\n");
		for (String string : categories) {
			System.out.println("CATEGORIE: "+string);	
		}

		System.out.println("\n");
		System.out.println("\n");
		System.out.println("\n");

		if (categories.contains("6"))	
			food=true;


		if (categories.contains("6") && categories.size()==1)	{
			categories.add("2");
			categories.add("1");
			categories.add("5");
		}

		if (!Utilities.isSunny(startVenue, availableTime)	)	{
			for (Iterator<String> it = categories.iterator(); it.hasNext();) {
				String cat = it.next();
				if (cat.equals("3") || cat.equals("5") || cat.equals("10"))	{
					it.remove();

				}
			}
		}
		if (!Utilities.isSunny(startVenue, availableTime)	)	{
			if(!categories.contains("1")) {
				categories.add("1");
			}
			if(!categories.contains("2")) {
				categories.add("2");
			}
		}

		if (categories.size()==0)	{
			categories.add("7");
			categories.add("1");
			categories.add("4");
		}

		if (Utilities.isFoodTime(String.valueOf(startVenue.getLatitude()), String.valueOf(startVenue.getLongitude())) && !categories.contains("6"))	{

			categories.add("6");
			food=true;
		}

		if (Utilities.isNightTime(String.valueOf(startVenue.getLatitude()), String.valueOf(startVenue.getLongitude())) && !categories.contains("6"))	{

			categories.add("8");
		}
		
		System.out.println("\n");
		System.out.println("\n");
		System.out.println("\n");
		for (String string : categories) {
			System.out.println("CATEGORIE: "+string);	
		}

		System.out.println("\n");
		System.out.println("\n");
		System.out.println("\n");


		return food;

	}

	



}

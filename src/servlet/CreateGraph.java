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

import logic.LatLngSquare;
import logic.VenueSearcher;
import model.Context;
import model.Scenario;
import model.Venue;
import socialAndServices.Google;

/**
 * Servlet implementation class CreateGraph
 */
@WebServlet("/CreateGraph")
public class CreateGraph extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateGraph() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String prossimaPagina = null;
		String start = request.getParameter("txtStart");
		String end = request.getParameter("txtEnd");
				
		Google google = new Google();
		
		Venue startVenue = google.getCoordinatesFromAddress(start);
		startVenue.setId(0);	// 0 is the id of the source node of Router algorithm
		
		Venue endVenue = google.getCoordinatesFromAddress(end);
		endVenue.setId(-1);		// -1 is the id of the destination node of Router algorithm
		
		List<Venue> venuesInTheSquare = null;
		
		if (startVenue.getStatus().equals("OK") && endVenue.getStatus().equals("OK")) {
			venuesInTheSquare = new ArrayList<Venue>();
			venuesInTheSquare.add(startVenue);
			venuesInTheSquare.add(endVenue);
			
			Context context = new Context();
			context.setStart(start);
			context.setEnd(end);
			context.setCity("");
			context.setSunny(true);
			Scenario scenario = new Scenario(0, context);
			scenario.setFood(true);
			LatLngSquare llSquare = new LatLngSquare(venuesInTheSquare);
			VenueSearcher searcher = new VenueSearcher(llSquare, scenario);
			venuesInTheSquare = searcher.getVenuesWithoutContext(40);
			venuesInTheSquare.add(0, startVenue);
			venuesInTheSquare.add(endVenue);				
			if (venuesInTheSquare.size() >= 2) {
				prossimaPagina = "/createGraphWithoutPolyline.jsp";
				request.setAttribute("venuesInTheSquare", venuesInTheSquare);
				request.setAttribute("startVenue", startVenue);
				request.setAttribute("endVenue", endVenue);
			} else {
				prossimaPagina = "/createGraph.jsp";
				request.setAttribute("error", "no venues found or time too short");				
			}						
		} else {
			prossimaPagina = "/createGraph.jsp";
			if (!startVenue.getStatus().equals("OK"))
				request.setAttribute("error", "Start address: " + startVenue.getStatus());
			if (!endVenue.getStatus().equals("OK"))
				request.setAttribute("error", "End address: " + endVenue.getStatus());
		}		
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

package servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import postgres.CheckinPostgres;
import postgres.PersistenceException;
import logic.LatLngSquare;
import logic.VenueSearcher;
import model.Venue;
import socialAndServices.Google;

/**
 * Servlet implementation class Index
 */
@WebServlet("/FindPOIsInTheSquare")
public class FindPOIsInTheSquare extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindPOIsInTheSquare() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String prossimaPagina = null;
		String start = request.getParameter("txtStart");;
		String end = request.getParameter("txtEnd");

		Google google = new Google();
		
		Venue startVenue = google.getCoordinatesFromAddress(start);
		Venue endVenue = google.getCoordinatesFromAddress(end);
		List<Venue> addressedVenues = null;
		List<Venue> venuesInTheSquare = null;
		VenueSearcher searcher;
		
		if (startVenue.getStatus().equals("OK") && endVenue.getStatus().equals("OK")) {
			addressedVenues = new LinkedList<Venue>();
			addressedVenues.add(startVenue);
			addressedVenues.add(endVenue);
			LatLngSquare llSquare = new LatLngSquare(addressedVenues);
			searcher = new VenueSearcher(llSquare, null);
			venuesInTheSquare = searcher.getAllVenuesInTheSquare();	
			if (venuesInTheSquare != null) {
				try {
					for (Venue v: venuesInTheSquare)
						CheckinPostgres.getCheckinsNumbersByVenueId(v);
				} catch (PersistenceException e) {
					e.printStackTrace();
				}
				prossimaPagina = "/findPOIsInTheSquare2.jsp";
				request.setAttribute("startVenue", startVenue);
				request.setAttribute("endVenue", endVenue);
				request.setAttribute("venuesInTheSquare", venuesInTheSquare);							
				request.setAttribute("llSquare", llSquare);
			} else {
				prossimaPagina = "/findPOIsInTheSquare1.jsp";
				request.setAttribute("error", "no venues found");
			}
		} else {
			prossimaPagina = "/findPOIsInTheSquare1.jsp";
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
		this.doGet(request, response);
	}

}

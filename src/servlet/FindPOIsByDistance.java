package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import postgres.PersistenceException;
import postgres.VenuePostgres;
import model.Venue;
import socialAndServices.Google;

/**
 * Servlet implementation class FindPOIsByDistance
 */
@WebServlet("/FindPOIsByDistance")
public class FindPOIsByDistance extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindPOIsByDistance() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String prossimaPagina = null;
		String address = request.getParameter("txtAddress");
		int km = Integer.parseInt(request.getParameter("txtKm"));
		List<Venue> venues = null;
		//Map<Double, Venue> venuesMap = null;
		
		Google google = new Google();
		
		Venue addressedVenue = google.getCoordinatesFromAddress(address);
		if (addressedVenue.getStatus().equals("OK")) {
			double lat = Double.parseDouble(addressedVenue.getLatitude());
			double lng = Double.parseDouble(addressedVenue.getLongitude());
			try {
				venues = VenuePostgres.RetrieveVenuesByDistance(km, lat, lng);
				//venuesMap = GeoLocation.orderVenuesByDistance(addressedVenue.getLatitude(), addressedVenue.getLongitude(), venues, 15);
			} catch (PersistenceException e) {
				e.printStackTrace();
			}
			
			if (venues != null) {
				prossimaPagina = "/findPOIsByDistance2.jsp";
				request.setAttribute("addressedVenue", addressedVenue);
				request.setAttribute("venues", venues);			
			} else {
				prossimaPagina = "/findPOIsByDistance1.jsp";
				request.setAttribute("error", "no venues near the address");
			}
		} else {
			prossimaPagina = "/findPOIsByDistance1.jsp";
			request.setAttribute("error", addressedVenue.getCategory_fq());
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

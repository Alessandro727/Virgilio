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

import logic.router.Route;
import model.User;
import postgres.CheckinPostgres;
import postgres.PersistenceException;

@WebServlet("/Routes")
public class Routes extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Routes() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User)session.getAttribute("user");
		String routeNumber = request.getParameter("ddlRoutes").split("Route n° ")[1];
		List<Route> topKroute = (List<Route>) session.getAttribute("topKroute");
		String mode = (String)session.getAttribute("mode");
		request.setAttribute("mode", mode);
		List<Route> finalRoute = new ArrayList<>();
		int i = Integer.parseInt(routeNumber);
		finalRoute.add(topKroute.get(i-1));
		try {
			CheckinPostgres.persistCheckins(finalRoute,user);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		session.setAttribute("finalRoute", finalRoute);
		ServletContext application  = getServletContext();
		RequestDispatcher requestDispatcher = application.getRequestDispatcher("/finalRoute.jsp");
	    requestDispatcher.forward(request, response);
		
	}
	
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);		
	}



}

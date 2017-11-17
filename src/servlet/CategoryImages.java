package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;
import model.Venue;
import postgres.PersistenceException;
import postgres.UserPostgres;
import socialAndServices.Google;

@WebServlet("/CategoryImages")
public class CategoryImages extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CategoryImages() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		User user = (User) request.getAttribute("user"); 
		String[] arrayData = request.getParameterValues("hiddenArray");
		
		for (int i=0; i<arrayData.length;i++)
			System.out.println(arrayData[i]);
		
		double[] weights = new double[]{10};
		
		
		
//		String prossimaPagina = "";
//
//		String username = request.getParameter("txtUsername");
//		String password = request.getParameter("txtPassword");
//		String gender = request.getParameter("rdGender");
//		int age = Integer.valueOf(request.getParameter("txtAge"));
//		String role = request.getParameter("ddlRole");
//		String residence = request.getParameter("residence");
//		System.out.println(residence);
//		Google google = new Google();
//		Venue venue = google.getCoordinatesFromAddress(residence);
//
//		String residenceLat = venue.getLatitude();
//		String residenceLong = venue.getLongitude();
//		User user = new User();
//
//		try {
//			if (UserPostgres.RetriveUsername(username)) {
//				prossimaPagina = "/register1.jsp";
//				request.setAttribute("error", "User already exists");
//			}
//			else {
//				//				String gender = request.getParameter("rdGender");
//				//				int age = Integer.parseInt(request.getParameter("txtAge"));
//				//				String role = request.getParameter("ddlRole");
//
//
//				user.setUsername(username);
//				user.setPassword(password);
//				//				user.setWeight(1, Double.valueOf(request.getParameter("txtMuseum")));
//				//				user.setWeight(2, Double.valueOf(request.getParameter("txtChurch")));
//				//				user.setWeight(3, Double.valueOf(request.getParameter("txtHistory")));
//				//				user.setWeight(4, Double.valueOf(request.getParameter("txtArts")));
//				//				user.setWeight(5, Double.valueOf(request.getParameter("txtOutdoors")));
//				//				user.setWeight(6, Double.valueOf(request.getParameter("txtFood")));
//				//				user.setWeight(7, Double.valueOf(request.getParameter("txtEntertainment")));
//				//				user.setWeight(8, Double.valueOf(request.getParameter("txtNightlife")));
//				//				user.setWeight(9, Double.valueOf(request.getParameter("txtShop")));
//				//				user.setWeight(10, Double.valueOf(request.getParameter("txtAthletics")));
//				user.setGender(gender);
//				user.setAge(age);
//				user.setRole(role);
//				user.setResidenceLat(residenceLat);
//				user.setResidenceLong(residenceLong);
//
//
//				//				user.setId(retrieveMostSmilarUser(user));
//
//				//				UserPostgres.persistUser(user);
//				prossimaPagina = "/register2.jsp";
//				request.setAttribute("user", user);
//			}
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//		}	
//
//		System.out.println(prossimaPagina);
//
//		for(int i=0; i<user.getWeigths().length; i++)	{
//			System.out.println(user.getWeigth(i));
//		}
//
//		ServletContext application  = getServletContext();
//		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
//		rd.forward(request, response);
	}


}

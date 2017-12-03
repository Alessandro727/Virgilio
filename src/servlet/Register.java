package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import postgres.CheckinPostgres;
import postgres.PersistenceException;
import postgres.UserPostgres;
import socialAndServices.Google;
import model.User;
import model.Venue;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final double INFINITY = Integer.MAX_VALUE;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
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
		String prossimaPagina = "";
		HttpSession session = request.getSession(false);
		String username = request.getParameter("txtUsername");
		String password = request.getParameter("txtPassword");
		String gender = request.getParameter("rdGender");
		int age = Integer.valueOf(request.getParameter("txtAge"));
		String role = request.getParameter("ddlRole");
		String residence = request.getParameter("residence");
		System.out.println(residence);
		Google google = new Google();
		Venue venue = google.getCoordinatesFromAddress(residence);
		
		String residenceLat = venue.getLatitude();
		String residenceLong = venue.getLongitude();
		User user = new User();
		
		try {
			if (UserPostgres.RetriveUsername(username)) {
				prossimaPagina = "/register1.jsp";
				request.setAttribute("error", "User already exists");
			}
			else {
//				String gender = request.getParameter("rdGender");
//				int age = Integer.parseInt(request.getParameter("txtAge"));
//				String role = request.getParameter("ddlRole");
				
				
				user.setUsername(username);
				user.setPassword(password);
//				user.setWeight(1, Double.valueOf(request.getParameter("txtMuseum")));
//				user.setWeight(2, Double.valueOf(request.getParameter("txtChurch")));
//				user.setWeight(3, Double.valueOf(request.getParameter("txtHistory")));
//				user.setWeight(4, Double.valueOf(request.getParameter("txtArts")));
//				user.setWeight(5, Double.valueOf(request.getParameter("txtOutdoors")));
//				user.setWeight(6, Double.valueOf(request.getParameter("txtFood")));
//				user.setWeight(7, Double.valueOf(request.getParameter("txtEntertainment")));
//				user.setWeight(8, Double.valueOf(request.getParameter("txtNightlife")));
//				user.setWeight(9, Double.valueOf(request.getParameter("txtShop")));
//				user.setWeight(10, Double.valueOf(request.getParameter("txtAthletics")));
				user.setGender(gender);
				user.setAge(age);
				user.setRole(role);
				user.setResidenceLat(residenceLat);
				user.setResidenceLong(residenceLong);
				
				
//				user.setId(retrieveMostSmilarUser(user));
				
//				UserPostgres.persistUser(user);
				prossimaPagina = "/register2.jsp";
				session.setAttribute("user", user);
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}	
		
		System.out.println(prossimaPagina);
		
		for(int i=0; i<user.getWeigths().length; i++)	{
			System.out.println(user.getWeigth(i));
		}
		
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
		try {
			System.out.println(mostTenSimilarUser(user));
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public long retrieveMostSmilarUser(User user) throws PersistenceException {
		List<User> users = UserPostgres.getAllUsers();
		System.out.println(users.size());
		long id = -1;
		int numCheckins = 0;
		
		double AB;		// A�B
		double A = 0;	// A
		double B;		// B^2
		double sim = 0;
		double simTemp;
		
		for (int i=1; i<user.getWeigths().length; i++) {
			A += Math.pow(user.getWeigth(i), 2);
		}
		
		for (User u: users) {
			AB = 0;
			B = 0;			
			for (int i=1; i<user.getWeigths().length; i++) {
				AB += (user.getWeigth(i)) * (u.getWeigth(i));
				B += Math.pow(u.getWeigth(i), 2);
			}
			simTemp = ( AB / Math.sqrt(A*B) );
			if (simTemp > sim) {
				sim = simTemp;
				id = CheckinPostgres.getUserIdByUsernameAndPassword(u.getUsername(),u.getPassword());
				System.out.println("ID primo if: "+id);
				numCheckins = CheckinPostgres.getNumCheckinsByUser(id);
			} else {
				if (simTemp == sim) {
					int ck = CheckinPostgres.getNumCheckinsByUser(CheckinPostgres.getUserIdByUsernameAndPassword(u.getUsername(), u.getPassword()));
					if (ck > numCheckins) {						
						id = CheckinPostgres.getUserIdByUsernameAndPassword(u.getUsername(), u.getPassword());
						System.out.println("ID secondo if: "+id);
						numCheckins = ck;
					}
				}
			}
		}
		System.out.println("ID: "+id);
		return id;
	}
	
	
	public static List<Long> mostTenSimilarUser(User user) throws PersistenceException	{
		List<User> users = UserPostgres.getAllUsers();
		List<Long> tenUserId = new ArrayList<>();
		System.out.println(users.size());
//		long id = -1;
		double sim = INFINITY;
		double simTemp;
		
		for (User u: users)	{
			
			double diffCoppiaUser = 0;
			
			for(int i=1; i<user.getWeigths().length; i++)	{
				
				diffCoppiaUser +=  Math.pow((u.getWeigth(i)-user.getWeigth(i)), 2);
			}
			
			simTemp = Math.pow(diffCoppiaUser, 0.5);
			
			if (simTemp<=sim) {
				
				sim = simTemp;
				if (tenUserId.size()<10)	{
					tenUserId.add(u.getId());
				}
				else {
					deleteLessSimilarUser(tenUserId);
					tenUserId.add(u.getId());
				}
			}
			
			
		}
		
		
		return tenUserId;
		
	}
	
	
	private static void deleteLessSimilarUser(List<Long> tenUser) {
		Collections.sort(tenUser);
		tenUser.remove(0);
		
		
		
	}

	public static void main(String[] args) throws PersistenceException {
	
		
		User user = new User();
		
		user.setId(3439);
		user.setWeight(1, 0.4);
		user.setWeight(2, 0.6);
		user.setWeight(3, 0.2);
		user.setWeight(4, 0.3);
		user.setWeight(5, 0.8);
		user.setWeight(6, 0.8);
		user.setWeight(7, 0.5);
		user.setWeight(8, 0.5);
		user.setWeight(9, 0.6);
		user.setWeight(10, 0.4);
		
		
		System.out.println(mostTenSimilarUser(user));
		
	}


	
	
	
	
	
	
	
	
	
	
	
}

package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

import postgres.DataSource;
import postgres.PersistenceException;




@WebServlet("/Evaluation")
public class Evaluation extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Evaluation() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		int size = Integer.valueOf(request.getParameter("venuesNum"));
		int quest1 = Integer.valueOf(request.getParameter("recommender_rating"));
		int quest2 = Integer.valueOf(request.getParameter("novelty"));
		int quest3 = Integer.valueOf(request.getParameter("serendipity"));
		int quest4 = Integer.valueOf(request.getParameter("diversity"));
		int quest5 = Integer.valueOf(request.getParameter("obvious"));
		int quest6 = Integer.valueOf(request.getParameter("venues"));

		try {
			persistEvaluation(quest1,quest2,quest3,quest4,quest5,quest6,size);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher("/findTopKPopularRoutes1.jsp");
		rd.forward(request, response);

	}


	public static void persistEvaluation(int quest1, int quest2, int quest3, int quest4, int quest5, int quest6, int size) throws PersistenceException {

		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = datasource.getConnection();
			String insert = "insert into evaluation (quest1, quest2, quest3, quest4, quest5, quest6, numVenue) values (?, ?, ?, ?, ?, ?, ?)";
			System.out.println(insert);
			statement = connection.prepareStatement(insert);
			statement.setInt(1, quest1);
			statement.setInt(2, quest2);
			statement.setInt(3, quest3);
			statement.setInt(4, quest4);
			statement.setInt(5, quest5);
			statement.setInt(6, quest6);
			statement.setInt(7, size);
			
			

			statement.executeUpdate();						
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			throw e;
		} finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws PersistenceException	{
		persistEvaluation(4, 4, 5, 5, 5, 3, 3);
	}





}

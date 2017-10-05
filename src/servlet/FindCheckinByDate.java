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
import postgres.UserPostgres;
import model.Checkin;

/**
 * Servlet implementation class CercaDateCheckinsUtente
 */
@WebServlet("/FindCheckinByDate")
public class FindCheckinByDate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindCheckinByDate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String prossimaPagina = null;
		int userID = Integer.parseInt(request.getParameter("userID"));
		List<Checkin> checkins = null;
		try {
			checkins = UserPostgres.RetrieveCheckinsByUser(userID);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (checkins != null) {
			prossimaPagina = "/findCheckinByDate2.jsp";
			request.setAttribute("checkins", checkins);
		}
		else
			prossimaPagina = "/findCheckinByDate1.jsp";
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
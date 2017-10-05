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
import postgres.PersistenceException;
import postgres.UserPostgres;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
		String prossimaPagina = null;
		
		User user;
		HttpSession session = request.getSession(false);
		try {
			if (request.getParameter("btnLogin").equals("Login as a guest")) {
				user = new User();
				user.setUsername("guest");
				user.setPassword("guest");
				
				if (session != null)
					session.invalidate();
				session = request.getSession();
				session.setAttribute("user", user);
				prossimaPagina = "/findTopKPopularRoutes1.jsp";
			}
			else {
				String username = request.getParameter("txtUsername");
				String password = request.getParameter("txtPassword");
				user = UserPostgres.RetrieveUserByUsernameAndPassword(username, password);
				if (user != null) {
					UserPostgres.RetrieveFriends(user);
					
					if (session != null)
					session.invalidate();
					session = request.getSession();
					session.setAttribute("user", user);
					prossimaPagina = "/findTopKPopularRoutes1.jsp";
				} else {
					prossimaPagina = "/login.jsp";
					request.setAttribute("error", "Failed login: wrong username or password");
				}
			}
		} catch (PersistenceException e) {			
			e.printStackTrace();
		}			
		
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}

}

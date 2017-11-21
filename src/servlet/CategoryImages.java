package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.User;

import postgres.PersistenceException;
import postgres.UserPostgres;


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

		String[] categories = arrayData[0].split(",");


		double[] weights = new double[]{10};

		for (int i=0; i<categories.length;i++)	{
			System.out.println(categories[i]);
			switch(categories[i]) {
			case "museum1": case "museum2": case "museum3": 	weights[0]=weights[0]+0.3; break;		

			case "chiesa1": case "chiesa2": case "chiesa3": 	weights[1]=weights[1]+0.3; break;

			case "monument1": case "monument2": case "monument3": 	weights[2]=weights[2]+0.3; break;	

			case "art2": case "art3": 	weights[3]=weights[3]+0.3; break;

			case "outdoors1": case "outdoors2": case "outdoors3": 	weights[4]=weights[4]+0.3; break;	

			case "food1": case "food3": 	weights[5]=weights[5]+0.3; break;

			case "entertaiment1": case "entertaiment2": case "entertaiment3": 	weights[6]=weights[6]+0.3; break;

			case "night1": case "night3": 	weights[7]=weights[7]+0.3; break;

			case "shop1": case "shop2": case "shop3": 	weights[8]=weights[8]+0.3; break;

			case "sport1": case "sport3": 	weights[9]=weights[9]+0.3; break;

			case "sport2": weights[9]=weights[9]+0.3; weights[6]=weights[6]+0.1; break;

			case "art1": weights[3]=weights[3]+0.3; weights[2]=weights[2]+0.1; break;

			case "night2": weights[7]=weights[7]+0.3; weights[6]=weights[6]+0.1; break;

			case "food2": weights[5]=weights[5]+0.3; weights[7]=weights[7]+0.1; break;

			default: break;
			}
		}

		user.setWeight(1, weights[0]);
		user.setWeight(2, weights[1]);
		user.setWeight(3, weights[2]);
		user.setWeight(4, weights[3]);
		user.setWeight(5, weights[4]);
		user.setWeight(6, weights[5]);
		user.setWeight(7, weights[6]);
		user.setWeight(8, weights[7]);
		user.setWeight(9, weights[8]);
		user.setWeight(10, weights[9]);
		
		try {
			UserPostgres.persistUser(user);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("user", user);

		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher("/findTopKPopularRoutes1.jsp");
		rd.forward(request, response);

	}


}

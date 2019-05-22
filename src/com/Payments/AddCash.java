package com.Payments;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.login.facade.LoginFacade;
import com.login.vo.LoginVO;

/**
 * Servlet implementation class AddCash
 */

public class AddCash extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddCash() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String regID = request.getHeader("Authorization");
		if (null == regID) {
			regID = request.getParameter("regID");
		}
		LoginVO loginVO = new LoginFacade().validateRegID(regID);
		if (null != loginVO && null != loginVO.getEmailID()) {
			response.sendRedirect("https://idonotremember-app.appspot.com/AddCash?appName=sosServices&email="+loginVO.getEmailID()+"&amount="+request.getParameter("amount"));
		}else {
			response.getWriter().print("Something unusual happened. Please try again later.");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}

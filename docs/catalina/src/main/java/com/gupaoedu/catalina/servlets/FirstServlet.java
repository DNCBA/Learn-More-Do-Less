package com.gupaoedu.catalina.servlets;

import com.gupaoedu.catalina.http.GPRequest;
import com.gupaoedu.catalina.http.GPResponse;
import com.gupaoedu.catalina.http.GPServlet;

public class FirstServlet extends GPServlet {

	
	@Override
	public void doGet(GPRequest request, GPResponse response) {
		doPost(request, response);
	}

	
	@Override
	public void doPost(GPRequest request, GPResponse response) {
		String param = "name";  
	    String str = request.getParameter(param);  
	    response.write(param + ":" + str,200);
	}
	
}

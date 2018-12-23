package com.gupaoedu.catalina.servlets;

import com.alibaba.fastjson.JSON;
import com.gupaoedu.catalina.http.GPRequest;
import com.gupaoedu.catalina.http.GPResponse;
import com.gupaoedu.catalina.http.GPServlet;

public class SecondServlet extends GPServlet {

	@Override
	public void doGet(GPRequest request, GPResponse response) {
		doPost(request, response);
	}
	
	@Override
	public void doPost(GPRequest request, GPResponse response) {
	    String str = JSON.toJSONString(request.getParameters(),true);  
	    response.write(str,200);
	}
	
}

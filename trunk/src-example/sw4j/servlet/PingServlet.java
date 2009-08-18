/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/

package sw4j.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sw4j.task.rdf.RDFSYNTAX;
import sw4j.util.ToolSafe;



/**
 * Web Interface for PmlValidator.
 *  
 * @author Li
 *
 */
public class PingServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3392604472787612392L;

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
	{
    	doProcessQuery(request,response);
	}
	
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
	{
    	doProcessQuery(request,response);
	}
    
    
    public void doProcessQuery(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
    	PingService svc = new PingService();
    	
    	// parse input parameters
		svc.szURL =  request.getParameter("url");
		String view =  request.getParameter("view");
		svc.rdfsyntax = RDFSYNTAX.parseRdfSyntax(view);
		if (!ToolSafe.isEmpty(request) && !ToolSafe.isEmpty(request.getRequestURI()))
			svc.requestURI =  request.getRequestURI() ;

		svc.bValidateRDF = false;
		String temp = (String) request.getParameter("vrdf");
		svc.bValidateRDF =ToolSafe.isEqual(temp, "yes");

        DataServeletResponse ret = svc.run();
       	ret.output(response);        

    }
   
}

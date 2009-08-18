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
public class SparqlServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3392604472787612392L;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
	{
    	doProcessQuery(request,response);
	}
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
	{
    	doProcessQuery(request,response);
	}
    
    
    public void doProcessQuery(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
    	SparqlService svc = new SparqlService();
    	
    	// parse input parameters
    	svc.queryURL =  (String) request.getParameter("queryURL");
    	svc.queryText =  (String) request.getParameter("queryText");
		
		

		String view =  request.getParameter("view");
		svc.rdfsyntax = RDFSYNTAX.parseRdfSyntax(view);

		svc.bUsePellet = false;
		String usePellet= (String) request.getParameter("usePellet");
		svc.bUsePellet =ToolSafe.isEqual(usePellet, "yes");
		
		if (!ToolSafe.isEmpty(request) && !ToolSafe.isEmpty(request.getRequestURI()))
			svc.requestURI =  request.getRequestURI() ;
		
        DataServeletResponse ret = svc.run();
       	ret.output(response);  
    }
   
}

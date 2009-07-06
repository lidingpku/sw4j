/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/

package sw4j.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sw4j.util.ToolSafe;



/**
 * Web Interface for PmlValidator.
 *  
 * @author Li
 *
 */
public class DiffServlet extends HttpServlet {

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
    	DiffService svc = new DiffService();
    	
    	// parse input parameters
		String szTemp;
		szTemp = request.getParameter("prev");
		if (!ToolSafe.isEmpty(szTemp))
			svc.szPrev =szTemp;

		szTemp = request.getParameter("cur");
		if (!ToolSafe.isEmpty(szTemp))
			svc.szCur =szTemp;
		
		szTemp = request.getParameter("output");
		if (!ToolSafe.isEmpty(szTemp))
			svc.output =szTemp;

		szTemp = request.getParameter("rss_title");
		if (!ToolSafe.isEmpty(szTemp))
			svc.rss_title =szTemp;

		szTemp = request.getParameter("rss_url");
		if (!ToolSafe.isEmpty(szTemp))
			svc.rss_url =szTemp;

		szTemp = request.getParameter("root_type_uri");
		if (!ToolSafe.isEmpty(szTemp))
			svc.root_type_uri =szTemp;


		if (!ToolSafe.isEmpty(request) && !ToolSafe.isEmpty(request.getRequestURI()))
			svc.requestURI =  request.getRequestURI() ;

		// compute model = cur - prev
        DataServeletResponse ret = svc.run();
       	ret.output(response);        

    }
   
}

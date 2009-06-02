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
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.rdf.ToolJena;


import com.hp.hpl.jena.rdf.model.Model;



/**
 * Web Interface for PmlValidator.
 *  
 * @author Li
 *
 */
public class SparqlJenaServlet extends HttpServlet {

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
    	// parse input parameters
		String queryURL =  (String) request.getParameter("queryURL");
		String queryText =  (String) request.getParameter("queryText");
		
		boolean bEmptyURL = ToolSafe.isEmpty(queryURL);
		boolean bEmptyText = ToolSafe.isEmpty(queryText);
		

		String view =  (String) request.getParameter("view");
		String rdfsyntax = RDFSYNTAX.parseRdfSyntax(view);

		boolean bUsePellet = false;
		String usePellet= (String) request.getParameter("usePellet");
		bUsePellet =ToolSafe.isEqual(usePellet, "yes");
		

        if (bEmptyURL && bEmptyText){
        	DataServeletResponse ret = DataServeletResponse.createResponse("None of URL or Text are set, please set only one.", false,request, rdfsyntax);
        	ret.output(response);
        	return;
        }
        
        if (!bEmptyURL && !bEmptyText){
        	DataServeletResponse ret = DataServeletResponse.createResponse("Both URL and Text are set, please set only one.",false,request, rdfsyntax);
        	ret.output(response);
    		return;
        }
        
        if (!bEmptyURL){
    		//load query text from query URL
    		try {
				queryText = ToolIO.pipeUrlToString(queryURL);
			} catch (Sw4jException e) {
	        	DataServeletResponse ret = DataServeletResponse.createResponse(e.getMessage(),false,request, rdfsyntax);
	        	ret.output(response);

				return;
			}
    		bEmptyText = ToolSafe.isEmpty(queryText);
    		if (bEmptyText){
            	DataServeletResponse ret = DataServeletResponse.createResponse("Cannot find sparql query from the specified URL.",false,request, rdfsyntax);
            	ret.output(response);
        		return;
    		}
        }
        
        //assert (bEmptyText==false)

        // run sparql and return result
        Object results = ToolJena.sparql_exec(queryText, bUsePellet);
        if (ToolSafe.isEmpty(results)){
        	DataServeletResponse ret = DataServeletResponse.createResponse("empty result.", false, request, rdfsyntax);
        	ret.output(response);
        	return;
        }else if (results instanceof Model){
        	DataServeletResponse ret = DataServeletResponse.createResponse((Model)results, rdfsyntax);
        	ret.output(response);
        	return;        	
        }else{
        	DataServeletResponse ret = DataServeletResponse.createResponse(results.toString(), true, request, rdfsyntax);
        	ret.output(response);
        	return;        	        	
        }
    }
   
}

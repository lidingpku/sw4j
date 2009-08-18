package sw4j.app.servlet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.servlet.common.DataServletResponse;
import sw4j.rdf.util.AgentSparql;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;

public class SparqlService {
	
	final public static String AGENT = "TW SPARQL Service. http://onto.rpi.edu/sw4j/sparql.html";

	public String queryURL;
	public String queryText;
	public String rdfsyntax;
	public boolean bUsePellet;
	public String requestURI;
	
	public DataServletResponse run(){
		boolean bEmptyURL = ToolSafe.isEmpty(queryURL);
		boolean bEmptyText = ToolSafe.isEmpty(queryText);
	
       if (bEmptyURL && bEmptyText){
        	DataServletResponse ret = DataServletResponse.createResponse("None of URL or Text are set, please set only one.", false,requestURI, AGENT,  rdfsyntax);
        	return ret;
        }
        
        if (!bEmptyURL && !bEmptyText){
        	DataServletResponse ret = DataServletResponse.createResponse("Both URL and Text are set, please set only one.",false,requestURI, AGENT,  rdfsyntax);
        	return ret;
        }
        
        if (!bEmptyURL){
    		//load query text from query URL
    		try {
				queryText = ToolIO.pipeUrlToString(queryURL);
			} catch (Sw4jException e) {
	        	DataServletResponse ret = DataServletResponse.createResponse(e.getMessage(),false,requestURI, AGENT,  rdfsyntax);
	        	return ret;
			}
    		bEmptyText = ToolSafe.isEmpty(queryText);
    		if (bEmptyText){
            	DataServletResponse ret = DataServletResponse.createResponse("Cannot find sparql query from the specified URL.",false,requestURI, AGENT,  rdfsyntax);
            	return ret;
    		}
        }
        
        //assert (bEmptyText==false)

        // run sparql and return result
        Object results = new AgentSparql().exec(queryText, rdfsyntax);
        if (ToolSafe.isEmpty(results)){
        	DataServletResponse ret = DataServletResponse.createResponse("empty result.", false, requestURI, AGENT,  rdfsyntax);
        	return ret;
        }else if (results instanceof Model){
        	DataServletResponse ret = DataServletResponse.createResponse((Model)results, null, rdfsyntax);
        	return ret;
        }else{
        	DataServletResponse ret = DataServletResponse.createResponse(results.toString(), true, requestURI, AGENT,  rdfsyntax);
        	return ret;
        }
	}
}

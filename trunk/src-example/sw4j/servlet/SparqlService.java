package sw4j.servlet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.rdf.ToolJena;

public class SparqlService {
	
	final public static String AGENT = "TW SPARQL Service. http://onto.rpi.edu/sw4j/sparql.html";

	public String queryURL;
	public String queryText;
	public String rdfsyntax;
	public boolean bUsePellet;
	public String requestURI;
	
	public DataServeletResponse run(){
		boolean bEmptyURL = ToolSafe.isEmpty(queryURL);
		boolean bEmptyText = ToolSafe.isEmpty(queryText);
	
       if (bEmptyURL && bEmptyText){
        	DataServeletResponse ret = DataServeletResponse.createResponse("None of URL or Text are set, please set only one.", false,requestURI, AGENT,  rdfsyntax);
        	return ret;
        }
        
        if (!bEmptyURL && !bEmptyText){
        	DataServeletResponse ret = DataServeletResponse.createResponse("Both URL and Text are set, please set only one.",false,requestURI, AGENT,  rdfsyntax);
        	return ret;
        }
        
        if (!bEmptyURL){
    		//load query text from query URL
    		try {
				queryText = ToolIO.pipeUrlToString(queryURL);
			} catch (Sw4jException e) {
	        	DataServeletResponse ret = DataServeletResponse.createResponse(e.getMessage(),false,requestURI, AGENT,  rdfsyntax);
	        	return ret;
			}
    		bEmptyText = ToolSafe.isEmpty(queryText);
    		if (bEmptyText){
            	DataServeletResponse ret = DataServeletResponse.createResponse("Cannot find sparql query from the specified URL.",false,requestURI, AGENT,  rdfsyntax);
            	return ret;
    		}
        }
        
        //assert (bEmptyText==false)

        // run sparql and return result
        Object results = ToolJena.sparql_exec(queryText, bUsePellet, rdfsyntax);
        if (ToolSafe.isEmpty(results)){
        	DataServeletResponse ret = DataServeletResponse.createResponse("empty result.", false, requestURI, AGENT,  rdfsyntax);
        	return ret;
        }else if (results instanceof Model){
        	DataServeletResponse ret = DataServeletResponse.createResponse((Model)results, null, rdfsyntax);
        	return ret;
        }else{
        	DataServeletResponse ret = DataServeletResponse.createResponse(results.toString(), true, requestURI, AGENT,  rdfsyntax);
        	return ret;
        }
	}
}

package sw4j.app.servlet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.pellet.AgentSparqlPellet;
import sw4j.rdf.util.AgentSparql;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;

public class SparqlService extends AbstractService{
	@Override
	public String getName() {
		return "TW SPARQL Service. http://onto.rpi.edu/sw4j/sparql.html";
	}

	public static final String PARAM_QUERY_URL = "queryURL".toLowerCase();
	public static final String PARAM_QUERY_TEXT = "queryText".toLowerCase();
	public static final String PARAM_USE_PELLET = "usePellet".toLowerCase();
	   	
   	
	
	public DataServletResponse run(){
		String queryURL =this.getUriParam(PARAM_QUERY_URL);
		String queryText =this.params.getAsString(PARAM_QUERY_TEXT);
		boolean bUsePellet =this.getBoolParam(PARAM_USE_PELLET);
		String output = this.params.getAsString(AbstractService.PARAM_OUTPUT);

		
		boolean bEmptyURL = ToolSafe.isEmpty(queryURL);
		boolean bEmptyText = ToolSafe.isEmpty(queryText);
	
       if (bEmptyURL && bEmptyText){
        	DataServletResponse ret = DataServletResponse.createResponse("None of URL or Text are set, please set only one.", false,this);
        	return ret;
        }
        
        if (!bEmptyURL && !bEmptyText){
        	DataServletResponse ret = DataServletResponse.createResponse("Both URL and Text are set, please set only one.",false,this);
        	return ret;
        }
        
        if (!bEmptyURL){
    		//load query text from query URL
    		try {
				queryText = ToolIO.pipeUrlToString(queryURL);
			} catch (Sw4jException e) {
	        	DataServletResponse ret = DataServletResponse.createResponse(e.getMessage(),false,this);
	        	return ret;
			}
    		bEmptyText = ToolSafe.isEmpty(queryText);
    		if (bEmptyText){
            	DataServletResponse ret = DataServletResponse.createResponse("Cannot find sparql query from the specified URL.",false,this);
            	return ret;
    		}
        }
        
        //assert (bEmptyText==false)

        // run sparql and return result
        Object results;
        if (bUsePellet)
        	results = new AgentSparqlPellet(true).exec(queryText, output);
        else
        	results = new AgentSparql().exec(queryText, output);
        
        if (ToolSafe.isEmpty(results)){
        	DataServletResponse ret = DataServletResponse.createResponse("empty result.", false, this);
        	return ret;
        }else if (results instanceof Model){
        	DataServletResponse ret = DataServletResponse.createResponse((Model)results, null, output);
        	return ret;
        }else{
        	DataServletResponse ret = DataServletResponse.createResponse(results.toString(), true, this);
        	return ret;
        }
	}

}

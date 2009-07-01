package sw4j.servlet;

import com.hp.hpl.jena.rdf.model.Model;
import sw4j.task.rdf.RDFSYNTAX;
import sw4j.task.util.AgentModelManager;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;
import sw4j.util.rdf.ToolJena;

public class DiffService {

	final public static String AGENT = "TW Diff Service. http://onto.rpi.edu/sw4j/diff.html";
	
	private String rdfsyntax = RDFSYNTAX.RDFXML;
	public String requestURI;

	public String szPrev;
	public String szCur;
	
	

	public DataServeletResponse run(){
        if (ToolSafe.isEmpty(szPrev)){
        	DataServeletResponse ret = DataServeletResponse.createResponse("URL for previous dataset not set.", false, requestURI,AGENT , rdfsyntax);
        	return ret;
        }
        if (ToolSafe.isEmpty(szCur)){
        	DataServeletResponse ret = DataServeletResponse.createResponse("URL for current dataset not set.", false, requestURI,AGENT , rdfsyntax);
        	return ret;
        }

		Model model_prev;
		try {
			model_prev = AgentModelManager.get().loadModel(szPrev);
	        if (ToolSafe.isEmpty(model_prev)){
	        	DataServeletResponse ret = DataServeletResponse.createResponse("cannot load previous dataset.", false, requestURI,AGENT , rdfsyntax);
	        	return ret;
	        }

	        Model model_cur  = AgentModelManager.get().loadModel(szCur);
	        if (ToolSafe.isEmpty(model_cur)){
	        	DataServeletResponse ret = DataServeletResponse.createResponse("cannot load current dataset.", false, requestURI,AGENT , rdfsyntax);
	        	return ret;
	        }
	        
	        Model model_diff = ToolJena.model_diff(model_cur, model_prev);
			DataServeletResponse  ret = DataServeletResponse.createResponse(model_diff, null, rdfsyntax);
	        return ret; 
		} catch (Sw4jException e) {
        	DataServeletResponse ret = DataServeletResponse.createResponse(e.getLocalizedMessage(), false, requestURI,AGENT , rdfsyntax);
        	return ret;
		}

	}
}

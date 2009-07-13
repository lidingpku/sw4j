package sw4j.servlet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RSS;

import sw4j.task.rdf.RDFSYNTAX;
import sw4j.task.rdf.TaskDiff;
import sw4j.task.util.AgentModelManager;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;

public class DiffService {

	final public static String AGENT = "TW Diff Service. http://onto.rpi.edu/sw4j/diff.html";
	
	private String rdfsyntax = RDFSYNTAX.RDFXML;
	public String requestURI;

	public String szPrev;
	public String szCur;
	public String root_type_uri=RSS.item.getURI();
	public String xmlbase = "http://tw.rpi.edu/diff";
	public String rss_title="DIFF default";

	public String output=TaskDiff.DIFF_RDF;
	
	

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
			//load model
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
	        
	        TaskDiff diff = TaskDiff.create(model_cur, model_prev, root_type_uri, szCur, szPrev, xmlbase);

	        if (TaskDiff.DIFF_RSS.equals(this.output)){
		        return  DataServeletResponse.createResponse(diff.getOutputRss(rss_title, RSS.title, RSS.link), null, RDFSYNTAX.RDFXML_ABBREV);
	        }else if (TaskDiff.DIFF_RDF.equals(this.output)){
	        	return DataServeletResponse.createResponse(diff.getOutputRdfDiff(), null, RDFSYNTAX.RDFXML);	        	
	        }else if (TaskDiff.DIFF_RDF_DEL.equals(this.output)){
	        	return DataServeletResponse.createResponse(diff.m_model_del, null, RDFSYNTAX.RDFXML_ABBREV);	        	
	        }else{
	        	return DataServeletResponse.createResponse(diff.m_model_add, null, RDFSYNTAX.RDFXML_ABBREV);
	        }
		} catch (Sw4jException e) {
        	DataServeletResponse ret = DataServeletResponse.createResponse(e.getLocalizedMessage(), false, requestURI,AGENT , rdfsyntax);
        	return ret;
		}

	}
}

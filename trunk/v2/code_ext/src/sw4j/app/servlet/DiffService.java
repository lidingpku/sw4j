package sw4j.app.servlet;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RSS;

import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.load.TaskDiff;
import sw4j.util.ToolSafe;

public class DiffService extends AbstractService{

	@Override
	public String getName() {
		return "TW SemDiff Service. http://onto.rpi.edu/sw4j/diff.html";
	}

	
	public static final String PARAM_PREV = "prev";
	public static final String PARAM_CUR = "cur";
	public static final String PARAM_RSS_TITLE = "rss_title";
	public static final String PARAM_XMLBASE = "xmlbase";
	public static final String PARAM_ROOT_TYPE_URI = "root_type_uri";
	public static final String PARAM_RSS_LINK_PROP = "rss_link_prop";
	public static final String PARAM_RSS_TITLE_PROP = "rss_title_prop";


	public DiffService(){
		this.params.put(PARAM_OUTPUT, TaskDiff.DIFF_RDF);
		this.params.put(PARAM_XMLBASE, "http://tw.rpi.edu/diff");
		this.params.put(PARAM_RSS_TITLE, "DIFF default");
		this.params.put(PARAM_RSS_LINK_PROP, RSS.link.getURI());
		this.params.put(PARAM_RSS_TITLE_PROP, RSS.title.getURI());
	}


	
	@Override
	public DataServletResponse run(){
		String szPrev =this.getUriParam(PARAM_PREV);
		String szCur =this.getUriParam(PARAM_CUR);
		String root_type_uri =this.getUriParam(PARAM_ROOT_TYPE_URI);
		Logger.getLogger(this.getClass()).info(root_type_uri);
		String xmlbase  =this.getUriParam(PARAM_XMLBASE);
		String rss_title  =this.params.getAsString(PARAM_RSS_TITLE);
		String rss_link_prop =this.getUriParam(PARAM_RSS_LINK_PROP);
		String rss_title_prop  =this.getUriParam(PARAM_RSS_TITLE_PROP);
		String output =this.params.getAsString(PARAM_OUTPUT);

		
		if (ToolSafe.isEmpty(szPrev)){
        	DataServletResponse ret = DataServletResponse.createResponse("URL for previous dataset not set.", false, this);
        	return ret;
        }
        if (ToolSafe.isEmpty(szCur)){
        	DataServletResponse ret = DataServletResponse.createResponse("URL for current dataset not set.", false, this);
        	return ret;
        }

		//load model
		Model model_prev = ModelFactory.createDefaultModel();
		model_prev.read(szPrev);
        if (ToolSafe.isEmpty(model_prev)||model_prev.size()==0){
        	DataServletResponse ret = DataServletResponse.createResponse("cannot load previous dataset or empty dataset.", false, this);
        	return ret;
        }

		Model model_cur = ModelFactory.createDefaultModel();
		model_cur.read(szCur);
        if (ToolSafe.isEmpty(model_cur)||model_cur.size()==0){
        	DataServletResponse ret = DataServletResponse.createResponse("cannot load current dataset or empty dataset.", false,this);
        	return ret;
        }
        
        TaskDiff diff = TaskDiff.create(model_cur, model_prev, root_type_uri, szCur, szPrev, xmlbase);

        if (TaskDiff.DIFF_RSS.equals(output)){
	        return  DataServletResponse.createResponse(diff.getOutputRss(rss_title, rss_title_prop, rss_link_prop), null, RDFSYNTAX.RDFXML_ABBREV);
        }else if (TaskDiff.DIFF_RDF.equals(output)){
        	return DataServletResponse.createResponse(diff.getOutputRdfDiff(), null, RDFSYNTAX.RDFXML);	        	
        }else if (TaskDiff.DIFF_RDF_DEL.equals(output)){
        	return DataServletResponse.createResponse(diff.m_model_del, null, RDFSYNTAX.RDFXML_ABBREV);	        	
        }else{
        	return DataServletResponse.createResponse(diff.m_model_add, null, RDFSYNTAX.RDFXML_ABBREV);
	    }


	}








}

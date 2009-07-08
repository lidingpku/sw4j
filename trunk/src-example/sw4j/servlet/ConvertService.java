package sw4j.servlet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.task.util.AgentModelLoader;
import sw4j.util.ToolSafe;

public class ConvertService {

	final public static String AGENT = "TW Converter Service. http://onto.rpi.edu/sw4j/ping.html";
	
	public String szURL;
	public String rdfsyntax;
	public String requestURI;
	public boolean bValidateRDF;

	public DataServeletResponse run(){
		boolean bEmptyURL = ToolSafe.isEmpty(szURL);

        if (bEmptyURL ){
        	DataServeletResponse ret = DataServeletResponse.createResponse("URL not set.", false, requestURI,AGENT , rdfsyntax);
        	return ret;
        }
        
        Model m;
		AgentModelLoader aml = new AgentModelLoader(szURL);
		m = aml.getModelData();
		if (null!=m)
			return DataServeletResponse.createResponse(m, null, rdfsyntax);
		else
			return DataServeletResponse.createResponse("cannot load. "  , false, requestURI, AGENT, rdfsyntax);
		
	}
}

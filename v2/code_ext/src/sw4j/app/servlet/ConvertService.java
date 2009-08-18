package sw4j.app.servlet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.servlet.common.DataServletResponse;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.util.ToolSafe;

public class ConvertService {

	final public static String AGENT = "TW Converter Service. http://onto.rpi.edu/sw4j/convert.html";
	
	public String szURL;
	public String rdfsyntax;
	public String requestURI;
	public boolean bValidateRDF;

	public DataServletResponse run(){
		boolean bEmptyURL = ToolSafe.isEmpty(szURL);

        if (bEmptyURL ){
        	DataServletResponse ret = DataServletResponse.createResponse("URL not set.", false, requestURI,AGENT , rdfsyntax);
        	return ret;
        }
        
        Model m;
		AgentModelLoader aml = new AgentModelLoader(szURL);
		m = aml.getModelData();
		if (null!=m)
			return DataServletResponse.createResponse(m, null, rdfsyntax);
		else
			return DataServletResponse.createResponse("cannot load. "  , false, requestURI, AGENT, rdfsyntax);
		
	}
}

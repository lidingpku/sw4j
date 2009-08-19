package sw4j.app.servlet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.util.ToolSafe;

public class ConvertService extends AbstractService{

	public ConvertService(){
		//init default value
		this.params.put(AbstractService.PARAM_OUTPUT, RDFSYNTAX.N3);
	}
	public DataServletResponse run(){
		String szURL = this.params.getAsString(PARAM_URL);
		String output = this.params.getAsString(AbstractService.PARAM_OUTPUT);

		boolean bEmptyURL = ToolSafe.isEmpty(szURL);

        if (bEmptyURL ){
        	DataServletResponse ret = DataServletResponse.createResponse("URL not set.", false, this);
        	return ret;
        }
        
        Model m;
		AgentModelLoader aml = new AgentModelLoader(szURL);
		m = aml.getModelData();
		if (null!=m)
			return DataServletResponse.createResponse(m, null, output);
		else
			return DataServletResponse.createResponse("cannot load. "  , false, this);
		
	}

	@Override
	public String getName() {
		return "TW Converter Service. http://onto.rpi.edu/sw4j/convert.html";
	}

}

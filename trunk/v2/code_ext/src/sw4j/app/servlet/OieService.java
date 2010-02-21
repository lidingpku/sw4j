package sw4j.app.servlet;

import org.apache.log4j.Logger;
import sw4j.app.oie.DataEvaluationConfig;
import sw4j.app.oie.DefaultOwlInstanceDataChecker;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.task.common.DataTaskResult;
import sw4j.util.ToolSafe;

public class OieService extends AbstractService{
	@Override
	public String getName() {
		return "OWL Instance Evaluation Service";
	}

	public static final String PARAM_inputUrl = "inputUrl";
	public static final String PARAM_inputText = "inputText";
	public static final String PARAM_inputMethod = "inputMethod";
	public static final String PARAM_inputTextRdfSyntax = "inputTextRdfSyntax";
	   	
	

	
	static Logger getLogger(){
		return Logger.getLogger(OieService.class);
	}
	
	public DataServletResponse run(){

	   try {   
			//String output = this.params.getAsString(AbstractService.PARAM_OUTPUT);
			
	        String inputUrl =  this.getUriParam(PARAM_inputUrl);
			String inputText=  this.getUriParam(PARAM_inputText);
//			String inputMethod = this.params.getAsString(PARAM_inputMethod);
			String inputTextRdfSyntax = this.params.getAsString(PARAM_inputTextRdfSyntax);
        	
		

	        // parse evaluation options
			
			DataEvaluationConfig vc = new DataEvaluationConfig();
			//debug if (!ToolCommon.isEmpty(inputUrl))
			if (!ToolSafe.isEmpty(this.params))
				vc = new DataEvaluationConfig(this.params.getAllFieldName());
	        
	        //validate RDF document
	        DefaultOwlInstanceDataChecker validator = new DefaultOwlInstanceDataChecker();
	        DataTaskResult der =null;
        	if (!ToolSafe.isEmpty(inputUrl)){
        		// process URL
        		der = validator.inspect(inputUrl, null, vc);
        	}else {
        		// process text
           		if ("RX".equals(inputTextRdfSyntax))
           			inputTextRdfSyntax ="RDF/XML";
           		else if ("NT".equals(inputTextRdfSyntax))
           			inputTextRdfSyntax ="N-TRIPLE";
           		else if ("N3".equals(inputTextRdfSyntax))
           			inputTextRdfSyntax ="N3";
           		else 
           			inputTextRdfSyntax ="RDF/XML";
           		
           		der = validator.inspectText(inputText, null, inputTextRdfSyntax,vc);
        	}
        		
        	DataServletResponse ret = DataServletResponse.createResponse(der.toHtml(), true, this);
        	return ret;
        	
        }catch (Exception e){
        	DataServletResponse ret = DataServletResponse.createResponse("encounter exceptions."+ e.getMessage(), false, this);
        	return ret;
        }
  	}

}

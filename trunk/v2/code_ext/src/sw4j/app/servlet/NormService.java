package sw4j.app.servlet;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import sw4j.app.pml.PMLDS;
import sw4j.app.pml.PMLR;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.pellet.ToolPellet;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.ToolJena;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

public class NormService extends AbstractService{

	@Override
	public String getName() {
		return "TW Sign Service. http://onto.rpi.edu/sw4j/sign.html";
	}

	
//	public static final String PARAM_XMLBASE = "xmlbase";
	public static final String PARAM_TYPE_URI = "type_uri";
	public static final String PARAM_TP_URI = "tp_uri";

	public static final String PARAM_VALUE_OPTION_SIGN = "sign";
	public static final String PARAM_VALUE_OPTION_UNSIGN = "unsign";
	public static final String PARAM_VALUE_OPTION_DLIST = "dlist";
	public static final String PARAM_VALUE_OPTION_TP = "tp";
	public static final String PARAM_VALUE_OPTION_DEDUCT_OWL = "deduct_owl";
	public static final String PARAM_VALUE_OPTION_DEDUCT_RDFS = "deduct_rdfs";

	public NormService(){
		this.params.put(PARAM_OUTPUT, RDFSYNTAX.RDFXML);
//		this.params.put(PARAM_XMLBASE, "http://tw.rpi.edu/sign/");
	}


	
	@Override
	public DataServletResponse run(){
		String szUrl =this.getUriParam(PARAM_URL);
		String type_uri =this.getUriParam(PARAM_TYPE_URI);
		String szOption =this.getNormalizedParam(PARAM_OPTION);
		Logger.getLogger(this.getClass()).info(type_uri);
//		String xmlbase  =this.getUriParam(PARAM_XMLBASE);

		
		if (ToolSafe.isEmpty(szUrl)){
        	DataServletResponse ret = DataServletResponse.createResponse("URL for dataset not set.", false, this);
        	return ret;
        }

		if (ToolSafe.isEmpty(szOption)){
        	DataServletResponse ret = DataServletResponse.createResponse("Option not set.", false, this);
        	return ret;
        }

		//load model
		Model model= ModelFactory.createDefaultModel();
		model.read(szUrl);
        if (ToolSafe.isEmpty(model)||model.size()==0){
        	DataServletResponse ret = DataServletResponse.createResponse("cannot load dataset or empty dataset.", false, this);
        	return ret;
        }
		
        //parse option
        szOption = szOption.toLowerCase();
        Set<String> options = ToolString.explode(",",szOption);

        //sign or unsign
        if (options.contains(PARAM_VALUE_OPTION_SIGN)){
			model = ToolJena.model_signBlankNode(model, DataQname.extractNamespaceUrl(szUrl)+"#");
        }else if (options.contains(PARAM_VALUE_OPTION_UNSIGN)){
        	Iterator<String> iter = ToolString.explode(",",type_uri).iterator();
            while (iter.hasNext()){
            	String a_type_uri = iter.next();
            	model = ToolJena.model_unsignBlankNode(model, a_type_uri);
            }
        }
        
        //decouple recursive lists use pmlr:hasPart
        if (options.contains(PARAM_VALUE_OPTION_DLIST)){
        	ToolJena.model_update_List2Map(model, RDF.first, RDF.rest, PMLR.hasPart, false);
        	ToolJena.model_update_List2Map(model, PMLDS.first, PMLDS.rest, PMLR.hasPart, false);
        	model.setNsPrefix(PMLR.class.getSimpleName().toLowerCase(), PMLR.getURI());
        }
        
		String tp_uri =this.getUriParam(PARAM_TP_URI);
        if (options.contains(PARAM_VALUE_OPTION_TP) && !ToolSafe.isEmpty(tp_uri)){
        	ToolJena.model_add_transtive(model, model.createProperty(tp_uri));
        }

        if (options.contains(PARAM_VALUE_OPTION_DEDUCT_RDFS)){
			model =ToolJena.model_createDeductiveClosure(model);
        }else if (options.contains(PARAM_VALUE_OPTION_DEDUCT_OWL)){
        	try {
				model =ToolPellet.model_createDeductiveClosure(model);
			} catch (Sw4jException e) {
	        	DataServletResponse ret = DataServletResponse.createResponse(e.getLocalizedMessage(), false, this);
	        	return ret;
			}
        }
        
        return DataServletResponse.createResponse(model, null, RDFSYNTAX.RDFXML);	        	


	}








}

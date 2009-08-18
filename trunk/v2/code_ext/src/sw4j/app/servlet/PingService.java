package sw4j.app.servlet;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RSS;

import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.app.servlet.vocabulary.RM;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.load.TaskParseRdf;
import sw4j.task.load.TaskLoad;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

public class PingService extends AbstractService {

	@Override
	public String getName() {
		return "TW Ping Service. http://onto.rpi.edu/sw4j/ping.html";
	}

	@Override
	public String[] getParamNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static final String PARAM_VALIDATE_RDF = "vrdf";


	@Override
	public DataServletResponse run(){
		String szURL = this.params.getAsString(PARAM_URL);
		String rdfsyntax = this.getRdfSyntax();
		String requestURI = this.context.getAsString(HTTP_REQUEST_URI);

		boolean bEmptyURL = ToolSafe.isEmpty(szURL);

        if (bEmptyURL ){
        	DataServletResponse ret = DataServletResponse.createResponse("URL not set.", false, requestURI,getName() , rdfsyntax);
        	return ret;
        }
        
        TaskLoad tl = TaskLoad.load(szURL);
		
        DataServletResponse ret;
		if (tl.isLoadSucceed()){
			Resource item = null;
        	ret = DataServletResponse.createResponse("alive", true, requestURI, getName(), rdfsyntax);
        	if (null!=ret.m_root){
        		item= ret.m_root.getModel().createResource();
        		item.addProperty(RDF.type, FOAF.Document);
        		if (0<tl.getLastmodified())
        			item.addProperty(DCTerms.modified, ToolString.formatXMLDateTime(tl.getLastmodified()));
        		item.addProperty(RSS.link, szURL);
        		if (!ToolSafe.isEmpty(tl.getMimetype()))
        			item.addLiteral(DC.format, tl.getMimetype());
        		
        		ret.m_root.addProperty(DC.relation, item);
        		ret.m_root.getModel().setNsPrefix(DCTerms.class.getSimpleName().toLowerCase(), DCTerms.getURI());
        	}else{
    			ret.m_sz_content += String.format("\nURL: %s ", szURL);
    			ret.m_sz_content += String.format("\nAgent: %s ", getName());
        		if (0<tl.getLastmodified())
        			ret.m_sz_content += String.format("\nLast modified? %s ", ToolString.formatXMLDateTime(tl.getLastmodified()));
        	}
        	
        	if (this.getBoolParam(PARAM_VALIDATE_RDF)){
        		TaskParseRdf tp = TaskParseRdf.parse(tl);
        		boolean isRDF =false;
        		if (null!=tp && tp.hasModel()){
        			isRDF = true;
    				if (null!=item){
						item.addLiteral(RM.isRDF, true);
		        		item.addProperty(RDF.type, RM.SemanticWebDocument);

						if (RDFSYNTAX.RDFXML.equals(tp.getRdfSyntax())){
    						item.addProperty(RM.hasRDFSyntax, RM.RDFXML);
    					}else if (RDFSYNTAX.N3.equals(tp.getRdfSyntax())){
    						item.addProperty(RM.hasRDFSyntax, RM.N3);
    					}else if (RDFSYNTAX.NT.equals(tp.getRdfSyntax())){
    						item.addProperty(RM.hasRDFSyntax, RM.NTriples);
    					}else if (RDFSYNTAX.TURTLE.equals(tp.getRdfSyntax())){
    						item.addProperty(RM.hasRDFSyntax, RM.Turtle);
    					}else if (RDFSYNTAX.RDFA.equals(tp.getRdfSyntax())){
    						item.addProperty(RM.hasRDFSyntax, RM.RDFa);
    					}
    					
						item.addLiteral(RM.hasCntTriple, tp.getModel().size());
						item.addProperty(RM.hasMd5sum, tl.getMd5sum());
    				}
    			}else{
    				if (null!=item){
						item.addLiteral(RM.isRDF, false);
    				}        				
    			}
        	
            	if (null!=ret.m_root){
            		ret.m_root.getModel().setNsPrefix(RM.class.getSimpleName().toLowerCase(), RM.getURI());
            	}else{
            		ret.m_sz_content += String.format("\nIs RDF? %s ", isRDF);
            	}
        	
        	}
        	
		}else{
        	ret = DataServletResponse.createResponse("offline", true, requestURI, getName(), rdfsyntax);
		}
		
		return ret;
	}


}

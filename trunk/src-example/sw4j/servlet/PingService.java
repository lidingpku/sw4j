package sw4j.servlet;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RSS;

import sw4j.servlet.vocabulary.RM;
import sw4j.task.load.TaskLoad;
import sw4j.task.rdf.RDFSYNTAX;
import sw4j.task.rdf.TaskParseRdf;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

public class PingService {

	final public static String AGENT = "TW Ping Service. http://onto.rpi.edu/sw4j/ping.html";
	
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
        
        TaskLoad tl = TaskLoad.load(szURL);
		
        DataServeletResponse ret;
		if (tl.isLoadSucceed()){
			Resource item = null;
        	ret = DataServeletResponse.createResponse("alive", true, requestURI, AGENT, rdfsyntax);
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
    			ret.m_sz_content += String.format("\nAgent: %s ", AGENT);
        		if (0<tl.getLastmodified())
        			ret.m_sz_content += String.format("\nLast modified? %s ", ToolString.formatXMLDateTime(tl.getLastmodified()));
        	}
        	
        	if (bValidateRDF){
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
        	ret = DataServeletResponse.createResponse("offline", true, requestURI, AGENT, rdfsyntax);
		}
		
		return ret;
	}
}

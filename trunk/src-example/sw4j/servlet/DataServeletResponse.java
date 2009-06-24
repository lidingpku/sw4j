package sw4j.servlet;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import sw4j.servlet.vocabulary.RESPONSE;
import sw4j.task.rdf.RDFSYNTAX;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;
import sw4j.util.rdf.ToolJena;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataServeletResponse {
	
	String m_sz_content = "";
	String m_sz_mime_type = RDFSYNTAX.MIME_TEXT_PLAIN;
	String m_sz_rdf_syntax = null;
	Model m_model_content=null;
	Resource m_root = null;


	public static DataServeletResponse createResponse(String msg, boolean isSucceed, String requestURI, String creator, String rdfsyntax){
		rdfsyntax = RDFSYNTAX.parseRdfSyntax(rdfsyntax);
		if (ToolSafe.isEmpty(rdfsyntax)){
			DataServeletResponse response = new DataServeletResponse();
			response.m_sz_mime_type = RDFSYNTAX.MIME_TEXT_PLAIN;
			response.m_sz_rdf_syntax = null;
			response.m_sz_content = msg;
			response.m_model_content =null;
			response.m_root =null;

			return response;
		}else{
			Model m = ModelFactory.createDefaultModel();
			Resource root = m.createResource();
			root.addProperty(RDF.type, RESPONSE.ServiceResponse);
			//root.addProperty(RDF.type, RSS.item);
			root.addProperty(DC.date, ToolString.formatXMLDateTime(System.currentTimeMillis()));
			root.addProperty(DC.creator, creator );
			if (!ToolSafe.isEmpty(msg))
				root.addProperty(RESPONSE.hasMsg, msg);
			
			if (!ToolSafe.isEmpty(requestURI) )
				root.addProperty(DC.contributor, requestURI );
			
			if (isSucceed){
				root.addProperty(RESPONSE.hasResult, RESPONSE.succeed);				
			}else{
				root.addProperty(RESPONSE.hasResult, RESPONSE.failed);								
			}
			
			m.setNsPrefix(RESPONSE.class.getSimpleName().toLowerCase(), RESPONSE.getURI());
			return createResponse(m, root, rdfsyntax);
		}
	}

	public  static DataServeletResponse createResponse(Model m, Resource root, String rdfsyntax){
		rdfsyntax = RDFSYNTAX.parseRdfSyntax(rdfsyntax);
		if (ToolSafe.isEmpty(rdfsyntax))
			rdfsyntax = RDFSYNTAX.RDFXML;
		
		DataServeletResponse response = new DataServeletResponse();
		response.m_sz_rdf_syntax = rdfsyntax;
		response.m_sz_mime_type = RDFSYNTAX.getMimeType(rdfsyntax, null);
		response.m_model_content = m;
		response.m_sz_content =null;
		response.m_root =root;
		
		return response;
	}	
		
		

	public String getMimeType(){
		return RDFSYNTAX.getMimeType(m_sz_rdf_syntax, m_sz_mime_type);
	}
	
	public String getContent(){
		if (!ToolSafe.isEmpty(this.m_sz_content))
			return this.m_sz_content;
		else if (!ToolSafe.isEmpty(m_model_content))
			return  ToolJena.printModelToString(m_model_content, m_sz_rdf_syntax);
		else
			return "";
	}
	

	public void output(HttpServletResponse response) throws IOException{
        PrintWriter out = response.getWriter();
    	response.setContentType(getMimeType());
        output(out);
		out.close();
	}
	public void output(PrintWriter out) throws IOException{
		if (!ToolSafe.isEmpty(m_sz_content)){
			String header =
				"# date: "+ ToolString.formatXMLDateTime(System.currentTimeMillis())+"\n";
			
			out.println(header);
			out.println(getContent());
		}else{
			out.println(getContent());
		}
		out.flush();
	}
}

package sw4j.servlet;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sw4j.task.rdf.RDFSYNTAX;
import sw4j.util.ToolSafe;
import sw4j.util.rdf.ToolJena;
import vocabulary.RESPONSE;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataServeletResponse {
	
	String m_sz_content = "";
	String m_sz_mime_type = RDFSYNTAX.MIME_TEXT_PLAIN;
	String m_sz_rdf_syntax = null;


	public static DataServeletResponse createResponse(String msg, boolean isSucceed, HttpServletRequest request, String rdfsyntax){
		rdfsyntax = RDFSYNTAX.parseRdfSyntax(rdfsyntax);
		if (ToolSafe.isEmpty(rdfsyntax)){
			DataServeletResponse response = new DataServeletResponse();
			response.m_sz_mime_type = RDFSYNTAX.MIME_TEXT_PLAIN;
			response.m_sz_rdf_syntax = null;
			response.m_sz_content = msg;
			
			return response;
		}else{
			Model m = ModelFactory.createDefaultModel();
			Resource root = m.createResource();
			root.addProperty(RDF.type, RESPONSE.ServiceResponse);
			if (!ToolSafe.isEmpty(msg))
				root.addProperty(RESPONSE.hasMsg, msg);
			
			if (!ToolSafe.isEmpty(request) && !ToolSafe.isEmpty(request.getRequestURI()))
				root.addProperty(DC.creator, request.getRequestURI() );
			
			if (isSucceed){
				root.addProperty(RESPONSE.hasResult, RESPONSE.succeed);				
			}else{
				root.addProperty(RESPONSE.hasResult, RESPONSE.failed);								
			}
			
			return createResponse(m, rdfsyntax);
		}
	}

	public static DataServeletResponse createResponse(Model m, String rdfsyntax){
		rdfsyntax = RDFSYNTAX.parseRdfSyntax(rdfsyntax);
		if (ToolSafe.isEmpty(rdfsyntax))
			rdfsyntax = RDFSYNTAX.RDFXML;
		
		DataServeletResponse response = new DataServeletResponse();
		response.m_sz_rdf_syntax = rdfsyntax;
		response.m_sz_mime_type = RDFSYNTAX.getMimeType(rdfsyntax, null);
		response.m_sz_content = ToolJena.printModelToString(m, rdfsyntax);
		
		return response;
	}	
		
		

	public String getMimeType(){
		return RDFSYNTAX.getMimeType(m_sz_rdf_syntax, m_sz_mime_type);
	}
	
	public String getContent(){
		return m_sz_content;
	}
	
	public void output(HttpServletResponse response) throws IOException{
        PrintWriter out = response.getWriter();
    	response.setContentType(getMimeType());
		out.print(getContent());
		out.close();
	}
}

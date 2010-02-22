package sw4j.app.servlet.common;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import sw4j.app.servlet.common.DataServletResponse;
import sw4j.app.servlet.vocabulary.RESPONSE;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.ToolJena;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataServletResponse {
	
	public String m_sz_content = "";
	String m_sz_mime_type = RDFSYNTAX.MIME_TEXT_PLAIN;
	String m_sz_rdf_syntax = null;
	public Model m_model_content=null;
	public Resource m_root = null;
	boolean m_bIsSucceed = false;
	public boolean isSucceed(){
		return m_bIsSucceed;
	}

	public static DataServletResponse createResponse(String msg, boolean isSucceed, AbstractService service){
		return createResponse( msg,  isSucceed,  service.context.getAsString(AbstractService.HTTP_REQUEST_URI),  service.getName(),  service.params.getAsString(AbstractService.PARAM_OUTPUT));
	}

	public static DataServletResponse createHtmlRedirectResponse(String homepageUrl){
		String html = String.format("<meta HTTP-EQUIV=\"REFRESH\" content=\"0; url=http://www.yourdomain.com/index.html\">",homepageUrl);
		return createResponse( html,  false,  null,  null, RDFSYNTAX.MIME_TEXT_HTML);
	}

	public static DataServletResponse createResponse(String msg, boolean isSucceed, String requestURI, String creator, String rdfsyntax){
		rdfsyntax = RDFSYNTAX.parseSyntax(rdfsyntax);
		
		if (ToolSafe.isEmpty(rdfsyntax)){
			DataServletResponse response = new DataServletResponse();
			response.m_sz_mime_type = RDFSYNTAX.MIME_TEXT_PLAIN;
			response.m_sz_rdf_syntax = null;
			response.m_sz_content = msg;
			response.m_model_content =null;
			response.m_root =null;
			response.m_bIsSucceed = isSucceed;

			return response;
		}else if (RDFSYNTAX.MIME_TEXT_HTML.equalsIgnoreCase(rdfsyntax)){
			DataServletResponse response = new DataServletResponse();
			response.m_sz_mime_type = RDFSYNTAX.MIME_TEXT_HTML;
			response.m_sz_rdf_syntax = null;
			response.m_sz_content = msg;
			response.m_model_content =null;
			response.m_root =null;
			response.m_bIsSucceed = isSucceed;

			return response;

		}else if (RDFSYNTAX.SPARQL_XML.equalsIgnoreCase(rdfsyntax)){
			DataServletResponse response = new DataServletResponse();
			response.m_sz_mime_type = RDFSYNTAX.MIME_SPARQL_XML;
			response.m_sz_rdf_syntax =RDFSYNTAX.SPARQL_XML;
			response.m_sz_content = msg;
			response.m_model_content =null;
			response.m_root =null;
			response.m_bIsSucceed = isSucceed;

			return response;			
		}else if (RDFSYNTAX.SPARQL_JSON.equalsIgnoreCase(rdfsyntax)){
			DataServletResponse response = new DataServletResponse();
			response.m_sz_mime_type = RDFSYNTAX.MIME_SPARQL_JSON;
			response.m_sz_rdf_syntax =RDFSYNTAX.SPARQL_JSON;
			response.m_sz_content = msg;
			response.m_model_content =null;
			response.m_root =null;
			response.m_bIsSucceed = isSucceed;

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
			DataServletResponse response = createResponse(m, root, rdfsyntax);

			response.m_bIsSucceed = isSucceed;
			return response;			
		}
	}

	public  static DataServletResponse createResponse(Model m, Resource root, String rdfsyntax){
		rdfsyntax = RDFSYNTAX.parseSyntaxRdf(rdfsyntax);
		if (ToolSafe.isEmpty(rdfsyntax))
			rdfsyntax = RDFSYNTAX.RDFXML;
		
		DataServletResponse response = new DataServletResponse();
		response.m_sz_rdf_syntax = rdfsyntax;
		response.m_sz_mime_type = RDFSYNTAX.getMimeType(rdfsyntax, null);
		response.m_model_content = m;
		response.m_sz_content =null;
		response.m_root =root;
		response.m_bIsSucceed = true;
		
		return response;
	}	
		
		

	public String getMimeType(){
		return RDFSYNTAX.getMimeType(m_sz_rdf_syntax, m_sz_mime_type);
	}
	
	public String getContent(){
		if (!ToolSafe.isEmpty(this.m_sz_content))
			return this.m_sz_content;
		else if (!ToolSafe.isEmpty(m_model_content)){
			String szTemp =  ToolJena.printModelToString(m_model_content, m_sz_rdf_syntax);
			String [] content = ToolString.section_split(szTemp,"<channel","</channel>");
			if (null==content)
				return szTemp;
			
			String ret = content[0] +content[2];
			int index = ret.indexOf("<item");
			if (index<0)
				return szTemp;
			else
				return ret.substring(0,index)+content[1]+ret.substring(index);
		}else
			return "";
	}
	

	public void output(HttpServletResponse response) throws IOException{
        PrintWriter out = response.getWriter();
    	response.setContentType(getMimeType());
        output(out);
        out.flush();
		out.close();
	}
	public void output(PrintWriter out) throws IOException{
		if (!ToolSafe.isEmpty(m_sz_content)){
			//String header = "# date: "+ ToolString.formatXMLDateTime(System.currentTimeMillis())+"\n";
			
			//out.println(header);
			out.println(getContent());
		}else{
			out.println(getContent());
		}
		out.flush();
	}
}

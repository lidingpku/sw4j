/**
MIT License

Copyright (c) 2009 

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */
package sw4j.rdf.load;

/**
 * detect format of a document, e.g. html, xml, rdf/xml.
 * 
 *  @author: Li Ding
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import sw4j.util.ToolSafe;
import sw4j.util.web.ToolWeb;

import com.ibm.icu.util.StringTokenizer;


public class ToolMarkupDetector {
	public static boolean debug = false;
	
	public final static int FORMAT_TEXT = 0;
    public final static int FORMAT_XML = 1;
    public final static int FORMAT_HTML = 2;
    public final static int FORMAT_RDFXML = 3;
    public final static int FORMAT_HTML_EMBED_RDFXML = 4;
    public final static int FORMAT_TEXT_N3= 5;
    public final static int FORMAT_TEXT_NT= 6;
    public final static int FORMAT_MARKUP_NO_CONTENT= 7;
    public final static int FORMAT_TEXT_NO_CONTENT= 8;
    public final static int FORMAT_TEXT_EMBED_RDFXML = 9;
    public final static int FORMAT_TEXT_RDFA = 10;
    public final static int FORMAT_TEXT_GRDDL = 11;
    
    public final static String [][] FORMAT_NAME= new String [][]{
    	{"text", ""},
		{"xml",""},
		{"html",""},
		{"rdf_xml", RDFSYNTAX.RDFXML},
		{"html_embed_rdf_xml",RDFSYNTAX.RDFXML},
		{"text_n3",RDFSYNTAX.N3},
		{"text_nt",RDFSYNTAX.NT},
		{"markup_empty",""},
		{"text_empty",""},
		{"text_embed_rdf_xml",RDFSYNTAX.RDFXML},
		{"text_rdfa",RDFSYNTAX.RDFA},
    };

    public static String getJenaSyntaxByFormat(int iFormat){
    	return FORMAT_NAME[iFormat][1];
    }

    /**
     *  guess the format of a document (e.g. html, xml, RDF/XML, n3, ntriples.)
     *  using content information and pre-asserted expectation 
     * 
     * @param szText
     * @param expected_ret
     * @return
     */
	public static int guessFormat(String szText, int expected_ret){
		int ret = FORMAT_TEXT;
		
		//to avoid heap outofmemory problem, we skip guessing type of large files 
		if (szText.length()>200000){
			int len = szText.length();
			szText=szText.substring(0,100000)+ szText.substring(len -100000, len);
			//return FORMAT_RDFXML; // we use RDF/XML as default for very large files
		}
		
		
		String szNormalizedMarkup = normalizeHtml(szText).toLowerCase();
		// if this is an empty document after removing all comments, and it is expected to be RDF/XML, then it can be RDF/XML
		if (ToolSafe.isEmpty(szNormalizedMarkup)){
			if (FORMAT_RDFXML == expected_ret)
				return FORMAT_RDFXML;
			else
				return FORMAT_MARKUP_NO_CONTENT;
		}
		
		
		if (guessXML(szText) )
			ret = FORMAT_XML;

		if (guessHTML(szNormalizedMarkup) && FORMAT_TEXT==ret)
			ret = FORMAT_HTML;
		
		if (guessRDFXML(szText.toLowerCase())){
			switch (ret){
				case FORMAT_XML: ret = FORMAT_RDFXML; break;
				case FORMAT_HTML: ret = FORMAT_HTML_EMBED_RDFXML; break;
				case FORMAT_TEXT: ret = FORMAT_TEXT_EMBED_RDFXML; break;
			}
		}
		
		// if this is an empty xml, and it is expected to be RDF/XML, then it can be RDF/XML
		if (FORMAT_XML==ret && FORMAT_RDFXML == expected_ret)
			return FORMAT_RDFXML;
		
		if ( FORMAT_TEXT==ret){
			if (FORMAT_TEXT_N3 == expected_ret)
				return FORMAT_TEXT_N3;
			else if (FORMAT_TEXT_NT == expected_ret)
				return FORMAT_TEXT_NT;
			else{
				String szNormalizedText = normalizeN3(szText);
				if (ToolSafe.isEmpty(szNormalizedText)){
						ret = FORMAT_TEXT_NO_CONTENT;
				}else{
					if (guessNTriples(szNormalizedText)){
						ret = FORMAT_TEXT_NT;
					}else if (guessN3(szNormalizedText)){
						ret = FORMAT_TEXT_N3;
					}
				}
			}
		}

		if (FORMAT_TEXT != expected_ret){
			if (ret !=expected_ret){
				//getLogger().info("INFO: guessed format does not matche the expected format, use expected format")
				ret = expected_ret;
			}
		}else{
			// the expected ret is not set yet, so we can use ret safely
		}
		return ret;
	}
	/**
	 * guess document format using pre-asserted info 
	 * 
	 * @param szRdfSyntax	user specified RDF syntax
	 * @param szMimetype	mime type detected from http 
	 * @param szURL		the url  or file name of the document
	 * @return
	 */
	public static int guessFormat(String szRdfSyntax, String szMimetype, String szRawURL, String szXmlBase){
		// first use user supplied rdf syntax
		if (!ToolSafe.isEmpty(szRdfSyntax)){
			if (RDFSYNTAX.RDFXML.equals(szRdfSyntax)){
				return FORMAT_RDFXML;
			}else if (RDFSYNTAX.N3.equals(szRdfSyntax)){
				return FORMAT_TEXT_N3;
			}else if (RDFSYNTAX.NT.equals(szRdfSyntax)){
				return FORMAT_TEXT_NT;
			}else if (RDFSYNTAX.TURTLE.equals(szRdfSyntax)){
				return FORMAT_TEXT_N3;
			}
		}
		
		// then try mimetype 
		if (!ToolSafe.isEmpty(szMimetype)){
			int ret = guessFormatByMimetype(szMimetype);
			if (FORMAT_TEXT != ret)
				return ret;
		}
		
		// finally try file extension
		int ret =  guessFormatByFileExtension(szRawURL);
		if (FORMAT_TEXT != ret)
			return ret;
		
		if (!ToolSafe.isEmpty(szXmlBase)){
			ret =  guessFormatByFileExtension(szXmlBase);
		}
		
		return ret;
	}
	
	private  static int guessFormatByFileExtension(String szFileName){
		if (!ToolSafe.isEmpty(szFileName)){
			
			String szTemp = szFileName.toLowerCase();
			
			if (szTemp.endsWith("_nt"))
				return FORMAT_TEXT_NT;
			
			int index = szTemp.lastIndexOf(".");
			if (index>0 ){
				szTemp = szTemp.substring(Math.min(szTemp.length(), index+1));
				if ("rdf".equals(szTemp))
					return FORMAT_RDFXML;
				
				if ("owl".equals(szTemp))
					return FORMAT_RDFXML;
				
				if ("n3".equals(szTemp))
					return FORMAT_TEXT_N3;

				if ("ttl".equals(szTemp))
					return FORMAT_TEXT_N3;

				if ("nt".equals(szTemp))
					return FORMAT_TEXT_NT;

				if ("pdf".equals(szTemp))
					return FORMAT_TEXT_EMBED_RDFXML;
			}
		}
		
		return FORMAT_TEXT;
	}

	/**
	 * http://www.w3.org/2008/01/rdf-media-types
	 * 
	 * @param szMimeType
	 * @return
	 */
	private static int guessFormatByMimetype(String szMimeType){
		if (!ToolSafe.isEmpty(szMimeType)){
			
			String szTemp = szMimeType.toLowerCase();
			if ("application/rdf+xml".equals(szTemp))
				return FORMAT_RDFXML;
			
			if ("text/n3".equals(szTemp))
				return FORMAT_TEXT_N3;
			
			if ("text/turtle".equals(szTemp))
				return FORMAT_TEXT_N3;

			if ("application/x-turtle".equals(szTemp))
				return FORMAT_TEXT_N3;
		}
		
		return FORMAT_TEXT;
	}	
    private static boolean guessXML(String szText) {
    	
    	String szTemp = szText;//ToolWeb.removeHtmlComment(szText).toLowerCase();
    	if (szTemp.startsWith("<?xml"))
    		return true;

    	if (szTemp.startsWith("<!DOCTYPE xml"))
    		return true;

    	StringTokenizer st = new StringTokenizer(szTemp);
    	if (st.hasMoreTokens()){
    		String first = st.nextToken();
    		if (first.equals("<rdf:rdf"))
    			return true;
    		if (first.equals("<rdf"))
    			return true;
    	}

    	//if (szTemp.indexOf("xmlns:")>0)
    	//	return true;


    	return false;
    }

    private static boolean guessRDFXML(String szText) {
    	String szTemp = szText;//szText.toLowerCase();
    	if ((szTemp.indexOf("http://www.w3.org/1999/02/22-rdf-syntax-ns")>0)&&(szTemp.indexOf("<rdf:rdf")>=0))
    		return true;
    	
    	if ((szTemp.indexOf("http://www.w3.org/1999/02/22-rdf-syntax-ns")>0)&&(szTemp.indexOf("<rdf")>=0))
    		return true;

    	if ((szTemp.indexOf("http://www.w3.org/1999/02/22-rdf-syntax-ns")>0)&&(szTemp.indexOf(" xmlns:rdf")>=0)&& (szTemp.indexOf("<pre>")<0))
    		return true;

    	if ((szTemp.indexOf("http://www.w3.org/1999/02/22-rdf-syntax-ns")>0)&&(szTemp.indexOf("\nxmlns:rdf")>=0))
    		return true;
    	//    	if ((szTemp.indexOf("http://www.w3.org/1999/02/22-rdf-syntax-ns")>0)&&(szTemp.indexOf("xmlns:rdf")>=0))
//    		return true;

    	return false;
    }

    private static boolean guessHTML(String szText) {
    	String szTemp = szText;//.toLowerCase();
    	if (szTemp.indexOf("</html>")>=0)
    		return true;
    
    	if (szTemp.indexOf("<html ")>=0)
    		return true;

    	if (szTemp.indexOf("<!doctype html")>=0)
    		return true;
    	
    	if (guessXML(szText)){
    		return true;
    	}
    	
    	return false;
    }
    
    public static String normalizeHtml(String szText){
    	return ToolWeb.removeHtmlComment(szText);
    }
    
    public static String normalizeN3(String szText){
    	
    	BufferedReader reader = new BufferedReader(new StringReader(szText));
    	String ret ="";
    	String line;
    	int max_line =10;
    	try {
			while (null!=(line=reader.readLine())){
				line = line.trim();
				if (ToolSafe.isEmpty(line))
					continue;

				if (line.startsWith("#"))
					continue;
				
				int index = line.indexOf(" #");
				if (index>0){
					line = line.substring(0,index); 
				}

				ret += line+"\n";
				
				max_line --;
				if (max_line<0)
					break;
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret.replaceAll(".\n"," .\n");
    }

    private static boolean guessN3(String szText) {
    	String szTemp = szText.toLowerCase();
    	BufferedReader reader = new BufferedReader(new StringReader(szTemp));
    	String line;
    	try {
			while (null!=(line=reader.readLine())){
				if (line.indexOf("<http://www.w3.org/1999/02/22-rdf-syntax-ns#>")>=0)
					return true;
				
				if (line.indexOf("<http://www.w3.org/2002/07/owl#>")>=0)
					return true;

				if (line.indexOf("<http://www.w3.org/2000/01/rdf-schema#>")>=0)
					return true;

				if (line.startsWith("@prefix")){
			    	StringTokenizer st = new StringTokenizer(line);
	        		if (!st.hasMoreTokens())
	        			continue;
			    	String prefix = st.nextToken();
			    		
	        		if (!st.hasMoreTokens())
	        			continue;
	        		String qname = st.nextToken();

	        		if (!st.hasMoreTokens())
	        			continue;
	        		String uri = st.nextToken();

	        		if (!st.hasMoreTokens())
	        			continue;
	        		String period= st.nextToken();
	        		
	        		if (!".".equals(period))
	        			continue;

	        		if (!"@prefix".equals(prefix))
	        			continue;

	        		if (!isNTripleURI(uri))
	        			continue;
	        		
	        		if (!isN3Qname(qname))
	        			continue;
					
	        		return true;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    		
    	return false;
    }
    
    private static boolean guessNTriples(String szText) {
    	String szTemp = szText;

    	BufferedReader reader = new BufferedReader(new StringReader(szTemp));
    	String line;
    	int cnt=10;
    	try {
			while (null!=(line=reader.readLine()) && cnt>0){
				cnt--;
				
				StringTokenizer st = new StringTokenizer(line);
		    	if (debug)
		    		System.out.println(line);
				
	    		if (!st.hasMoreTokens())
	    			return false;
	    		String s = st.nextToken();
	    		
	    		if (!st.hasMoreTokens())
	    			return false;
	    		String p = st.nextToken();
	    		
	    		if (!st.hasMoreTokens())
	    			return false;
	    		String o = st.nextToken();
	    		
	    		if (o.startsWith("\"")){
	    			
		    		String temp = o; 
		    		do{
		    			if (temp.endsWith("\"") && o.length()>1){
		    				break;
		    			}else if (temp.indexOf("\"^^<")>=0){
		    				break;
		    			}else if (temp.indexOf("\"@")>=0){
		    				break;
		    			}else{
				    		if (!st.hasMoreTokens())
				    			return false;
				    		
				    		temp = st.nextToken();
		    			}
	    				if (!o.equals(temp))
	    					o+=" "+temp;
		    		}while (true);
	    		}
	    		
	    		if (!st.hasMoreTokens())
	    			return false;
		    	String	period = st.nextToken();

	    		
	    		if (!".".equals(period))
	    			return false;
	    			
	    		if (!isNTripleURI(s))
	    			return false;

	    		if (!isNTripleURI(p))
	    			return false;
	    		
	    		if (!isNTripleURI(o)&&!isNTripleLiteral(o))
	    			return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    	
		return true;
    }
    
    private static boolean isNTripleURI (String szWord){
    	if (szWord.startsWith("_:"))
    		return true;
    	if (szWord.matches("<[^>]*>"))
    		return true;
    	if (szWord.matches("(([a-zA-Z_][a-zA-Z0-9_]*)?:)?([a-zA-Z_][a-zA-Z0-9_]*)?"))
    		return true;
    	
    	return false;
    }
    
    private static boolean isNTripleLiteral(String szWord){
    	szWord = szWord.trim();
    	int index =0;
    	index = szWord.lastIndexOf("\"^^<");
    	String first = null;
    	String rest = null;
    	if (index>0){
    		index = index+1;
    		first = szWord.substring(0,index);
    		rest = szWord.substring(index+2);
    		
    		return isNTripleLiteral(first) && isNTripleURI(rest);
    	}else {
    		index = szWord.lastIndexOf("\"@");
    		if (index>0){
        		index = index+1;
	    		first = szWord.substring(0,index);
	    		rest = szWord.substring(index);
	    		return isNTripleLiteral(first);
    		}else{
    	    	if (szWord.startsWith("\"") && szWord.endsWith("\""))
    	    		return true;
    	    	else
    	    		return false;
    		}
    	}
    }

    private static boolean isN3Qname(String szWord){
    	if (szWord.matches("(([a-zA-Z_][a-zA-Z0-9_]*)?:)?([a-zA-Z_][a-zA-Z0-9_]*)?"))
    		return true;
    	
    	return false;
    }


}

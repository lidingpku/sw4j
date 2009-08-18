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
 * use jena to parse RDF documents, including embedded RDF/XML documents
 * @author Li Ding
 */

import java.io.*;

import org.apache.log4j.Logger;

import sw4j.util.ToolSafe;
import sw4j.util.ToolURI;

import com.hp.hpl.jena.rdf.model.*;
//import com.hp.hpl.jena.shared.JenaException;
/**
 * @author Li Ding
 *
 * This document is based on W3C RDF/XML validator (by Jeremy Carroll, HP Lab)
 * some modifications are made to extend its capability.
 * The parser has pass all .rdf test files in RDF Test (W3C 2004/2)
 * This parser is running in strict-mode, to make it flexible, we need to change some property.
 * 
 * TODO List: 
 *  (1) [9:42 PM 3/7/2005] N3 files are not well handled by Jena parser, 
 * 		I will investigate this issue in the future. 
 *  http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/animal-result.n3
 	http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/builtins.n3
 	http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/etc5-proof.n3
	http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/gedcom-relations-result.n3
	http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/graph.proof.n3
	http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/lists-query.n3
	http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/owl-rules.n3
	http://www.w3.org/2000/10/rdf-tests/rdfcore/euler/rdf-result.n3
 */
public class ToolParseRdfJena {

	public static boolean debug = false;
	private static Logger getLogger(){
		return Logger.getLogger(ToolParseRdfJena.class);
	}

	public static void parse(String szText, String szURL, String szRdfSyntax, TaskParseRdf output){
		if (debug)
			System.out.println("ToolParseRdfJena");
		
		if (ToolSafe.isEmpty(szText)){
			return;
		}
		
		Reader input = new StringReader(szText);
		
		parse(input, szURL, szRdfSyntax, output);
	}
	
	public static void parse(Reader input, String szURL, String szRdfSyntax, TaskParseRdf output){
		if (debug)
			System.out.println("ToolParseRdfJena");
		
		if (ToolSafe.isEmpty(input)){
			return;
		}
		
		
		// assume rdf syntax has already been determined
		if (ToolSafe.isEmpty(szRdfSyntax)){
			return;
		}
		
		if (ToolSafe.isEmpty(output)){
			return;
		}
		
		// assume that comment lines has been appropriately processed

		ToolMyErrorHandler oMyErrorHandler = new ToolMyErrorHandler(output.getReport(), getLogger());
		Model m =null;
		try {
			 m = ModelFactory.createDefaultModel();
			
			RDFReader reader;
			reader = m.getReader(szRdfSyntax);
			
			// this code is removed because jena can parse more rdf data from pdf file
			//
			if (szRdfSyntax.equalsIgnoreCase(RDFSYNTAX.RDFXML))
				reader.setProperty("error-mode","strict");
			
			reader.setErrorHandler(oMyErrorHandler);
			
			// we need to first remove comments lines to tolerate RDF/XML in bad XML format, e.g. the first chars are not '<?xml'.
			String szRuntimeXmlbase =szURL;
			if (ToolSafe.isEmpty(szRuntimeXmlbase)){
				szRuntimeXmlbase = ToolURI.DEFAULT_XMLBASE;
			}
			
			reader.read(m, input , szRuntimeXmlbase);
			
			output.setRuntimeXmlbase(szRuntimeXmlbase);
			output.addModel(m);
			output.setRdfSyntax(szRdfSyntax);

		//}catch (JenaException e){
		//	if (debug)
		//		e.printStackTrace();
			/*
			String abs;
			String msg = e.getMessage();
			if (null==msg){
				abs = msg="n/a";
			}else{			
				//updated by jiao 4/3/2008			
			
				//case1: prolog
				msg =msg.toLowerCase();
				String szTest1, szTest2;
				szTest1 = "content is not allowed in prolog";
				boolean b1 =  (msg.indexOf(szTest1)>=0);
				
				//case2: Syntax error
				szTest1 = "the element type";
				szTest2	= "must be terminated by the matching end-tag";
				boolean b2 = (msg.indexOf(szTest1)>=0 && msg.indexOf(szTest2)>=0);		
			
				if (b1){
					abs = "Can not parse RDF triples from the input document. prolog is expected at the beginning of RDF/XML documents.";
				}else if (b2){	
					abs = "RDF syntax error found in the parsing process.";					
				}else {
					abs = msg;				
				}
			}
			
			//getLogger().info("failed parsing RDF data from text or file");
			throw new SwutilException (SwutilException.ERROR_CANNOT_LOAD_RDF_TRIPLE, abs, msg);
			*/
		}catch (Exception e) {
			if (debug)
				e.printStackTrace();
			/*
			String abs;
			String msg = e.getMessage();
			if (null==msg){
				abs = msg="n/a";
			}else{	
				//updated by jiao 4/3/2008
				
				//case1: invalid URI
				msg =msg.toLowerCase();
				String szTest1;
				szTest1 = "not found: http:";
				boolean b1 =  (msg.indexOf(szTest1)>=0);
				
				if (b1){
					abs = "Can not load RDF triples from the specified URI.";					
				}else {
					abs = msg;
				}
				
			}			
			throw new SwutilException (SwutilException.ERROR_CANNOT_LOAD_RDF_TRIPLE, abs, msg);
			*/
		}
		
	}	

	
/*

	// parse results
    public final static String IS_XML = "isXml";
	
	public final static String RDF_VALIDATE = "rdf_validate";
	public final static int RDF_PARSE_NA = 0;
	public final static int RDF_PARSE_NOT_PURE = 1;
	public final static int RDF_PARSE_NONE = 2;
	public final static int RDF_PARSE_EMBED_FATAL_ERROR = 11;
	public final static int RDF_PARSE_EMBED_ERROR = 12;
	public final static int RDF_PARSE_EMBED_WARNING = 13;
	public final static int RDF_PARSE_EMBED_STRICT = 15;
	public final static int RDF_PARSE_FATAL_ERROR = 21;
	public final static int RDF_PARSE_ERROR = 22;
	public final static int RDF_PARSE_WARNING = 23;
	public final static int RDF_PARSE_STRICT = 25;
	private int m_rdf_parse_state = RDF_PARSE_NA;
    public int getRdfParseState(){
    	validateRDF();
    	isEmbeddedRDF();
    	return this.m_rdf_parse_state;
    }

    private Model m_model = null;
    public Model getModel(){return m_model;}
    public boolean hasModel(){return null!=m_model;} 

    private String m_warnings = null;
    public String getWarningString(){
    	if (null==m_warnings|| m_warnings.length()==0)
    		return null;
    	else
    		return m_warnings;
    } 
    private String m_errors= null;
    public String getErrorString(){
    	if (null==m_errors || m_errors.length()==0)
    		return null;
    	else
    		return m_errors;
    } 
	

	public static final String RDF_ENCODING_RDFXML ="RX";
	public static final String RDF_ENCODING_NT ="NT";
	public static final String RDF_ENCODING_N3 ="N3";
	public static final String RDF_ENCODING_RDF_EMBED ="RE";
	private String m_rdf_encoding = null;
	public String getRDFEncoding(){
		if (m_rdf_encoding==null){
			validateRDF();
			isEmbeddedRDF();
		}
		return m_rdf_encoding;
	};

    public void read(){
    	validateRDF();
		isEmbeddedRDF();
    }
	
	// internal variables 
	private String m_szURL;
	private String m_szText;
	private int m_option;
	
	private static boolean debug = false;

	public String getContent(){
		return m_szText;
	}
	
	public static RdfParser create(String szURL){
		DataCrawlerTask task = SimpleCrawler.crawl(szURL);
		
		if (null==task)
			return null;
		
		RdfParser reader = RdfParser.create(task.getContent(),szURL, task.getContentFormat());
		return reader;
	}
	
	public static RdfParser create(String szText, String szURL, int option){
		if (null==szText)
			return null;
		
		RdfParser reader = new RdfParser();
		reader.m_szText = szText;
		reader.m_szURL = szURL;
		reader.m_option = option;
		
		return reader;
	}
	
	

    public boolean isXML(){
		try {
			// check cached result first, avoid recompute 
			String temp = this.getEntry( IS_XML );
			if (null!= temp)
				return ToolSimpleFormatter.parseBooleanEntry(temp);

			// otherwise, first time check
			
			this.put( IS_XML, false );

			// check XML
			if (m_option != DataCrawlerTask.FORMAT_XML)
				return false;
			
			// dom1 is the best tools
            if (isXMLdom1(m_szText)){
    			this.put( IS_XML, true );
				return true;
			}
		}catch (Exception e){
			if (debug)
				e.printStackTrace();
		}
		return false;
	}     	
    
    
    public boolean isStrictRDF(){
    	validateRDF();
    	return (this.m_rdf_parse_state== RDF_PARSE_STRICT);
    }
    
    public boolean isWarningRDF(){
    	validateRDF();
    	return (this.m_rdf_parse_state== RDF_PARSE_WARNING);
    }

    public boolean isNoneRDF(){
    	isEmbeddedRDF();
    	return (this.m_rdf_parse_state <= RDF_PARSE_NONE);
    }

    private void validateRDF(){
		// check cached result first, avoid recompute 
    	if (this.m_rdf_parse_state!=RDF_PARSE_NA)
    		return;

    	if (DataCrawlerTask.FORMAT_HTML_EMBED== m_option){
			this.m_rdf_parse_state= RDF_PARSE_NOT_PURE;
    		return; //this is an obvious embedded SWD, skip checking
    	}
    	
		String input = removeCommentLines(m_szText);
		
		String [][] ary_test;
		switch (m_option){
		case DataCrawlerTask.FORMAT_XML:
			//ary_test = new String [][]{{"RDF/XML",RDF_ENCODING_RDFXML}, {"N3",RDF_ENCODING_N3}, {"N-TRIPLE",RDF_ENCODING_NT}};
			ary_test = new String [][]{{"RDF/XML",RDF_ENCODING_RDFXML}};
			break;
		default:  //RESULT_TEXT
			if (m_szURL.endsWith(".nt")){
				ary_test = new String [][]{{"N-TRIPLE",RDF_ENCODING_NT}, {"N3",RDF_ENCODING_N3}, {"RDF/XML",RDF_ENCODING_RDFXML}};
			}else{
				ary_test = new String [][]{{"N3",RDF_ENCODING_N3}, {"N-TRIPLE",RDF_ENCODING_NT},{"RDF/XML",RDF_ENCODING_RDFXML}};
			}
		}
		
		for (int i =0; i<ary_test.length; i++){
			if (validateRDF(input, m_szURL, ary_test[i][0], ary_test[i][1]))
	    		return;
		}

		this.m_rdf_parse_state= RDF_PARSE_NOT_PURE;
    }    		
	
    public boolean isEmbeddedRDF(){
    	validateRDF();
    	
    	if (m_rdf_parse_state > RDF_PARSE_NOT_PURE){
    		return m_rdf_parse_state == RDF_PARSE_EMBED_STRICT;
    	}else
    		m_rdf_parse_state = RDF_PARSE_NONE;
    	
    	
		// extract embeded RDF
		int indexBegin = m_szText.indexOf("<rdf:RDF");
		if (indexBegin<0)
			return false;
		String endlabel = "</rdf:RDF>";
		int indexEnd = m_szText.indexOf(endlabel, indexBegin);
		if (indexEnd<0)
			return false;
    	
		String temp = m_szText.substring(indexBegin, indexEnd+endlabel.length());

		validateRDF(temp, m_szURL, "RDF/XML", RDF_ENCODING_RDF_EMBED);
		
		switch (m_rdf_parse_state){
			case RDF_PARSE_STRICT: m_rdf_parse_state = RDF_PARSE_EMBED_STRICT; break;
			case RDF_PARSE_WARNING: m_rdf_parse_state = RDF_PARSE_EMBED_WARNING; break;
			case RDF_PARSE_ERROR: m_rdf_parse_state = RDF_PARSE_EMBED_ERROR; break;
		}
		return m_rdf_parse_state == RDF_PARSE_EMBED_STRICT;
    }	
	
	
		
	
    // check XML
    private static boolean isXMLdom1(String szText) {
    	try {
			DOMParser parser = new DOMParser();
	        //parser.setFeature("http://xml.org/sax/features/validation", true);

			PrintWriter out = new PrintWriter(System.out);
			ToolSaxErrorHandler oMyErrorHandler = new ToolSaxErrorHandler(out, !debug);
	        parser.setErrorHandler(oMyErrorHandler);
	        parser.parse(new InputSource(new StringReader(szText)));
	        
	       // System.out.println(oMyErrorHandler.bFailed);
	        
	        return true;
    	}catch(SAXException e){
    		if (debug)
    			e.printStackTrace();
    	}catch(IOException e){
    		e.printStackTrace();
    	}catch (Exception e){
    		e.printStackTrace();	
    	}
    	return false;
    }
    
    
    private static boolean isXMLdom2(String szText, String szCharset){
    	try {
			DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			ToolSaxErrorHandler oMyErrorHandler = new ToolSaxErrorHandler(new PrintWriter(System.out), !debug);
			docBuilder.setErrorHandler( oMyErrorHandler );
			
			org.w3c.dom.Document document = docBuilder.parse(new ByteArrayInputStream(szText.getBytes(szCharset)));
			
			// determine content encoding
			//org.apache.xerces.dom.DeferredDocumentImpl impl = (org.apache.xerces.dom.DeferredDocumentImpl)document;
			//System.out.println(impl.getInputEncoding());  //this output is not correct
			//System.out.println(impl.getXmlEncoding());
			//m_xml_encoding = impl.getXmlEncoding();
			
			//in.close();
			
			//if (!oMyErrorHandler.bFailed){
			//System.out.println("isValidating:"+docBuilder.isValidating());
	        return null!= document;    	
    	}catch(ParserConfigurationException e){
    		e.printStackTrace();
    	}catch(SAXException e){
    		if (debug)
    			e.printStackTrace();
    	}catch(IOException e){
    		e.printStackTrace();
    	}catch (Exception e){
    	
    	}
    	return false;
    }
   


    private String removeCommentLines(String text){
    	String first ="";
    	String rest = text;
    	while (true) {
    		int index1=rest.indexOf("<!--");
    		if (index1<0)
    			break;
    		int index2=rest.indexOf("-->",index1);
    		if (index2<0)
    			break;
    		first += rest.substring(0,index1);
    		rest = rest.substring(index2+3);
    	}
    	first += rest;
    	return first.trim();
    	// the following regular expression code takes too much time
    	// so we get rid of them 
    	//
    	//String temp = text.replaceAll("<!--[^-]+[-[^-]+]*[--[^>]+]*-->","");
    	//temp = temp.trim();
    	//return temp;
    }
    
    
	private boolean validateRDF(String szText, String szURL, String parseType, String rdf_syntax){
		ToolSaxErrorHandler oMyErrorHandler = new ToolSaxErrorHandler(new PrintWriter(System.out), false);
		// create model
		Model m = ModelFactory.createDefaultModel();
		try {
			szURL.toLowerCase();
	
	
			RDFReader reader;
			reader = m.getReader(parseType);
			
			// set reader's syntax check to stric
			if (parseType.equalsIgnoreCase("RDF/XML"))
				reader.setProperty("error-mode","strict");
			//reader.setProperty("WARN_ENCODING_MISMATCH","EM_IGNORE");
			
			// set reader's error handler, i.e. do not print any error message
			reader.setErrorHandler(oMyErrorHandler);
			
			
			// we need to first remove commnets lines to tolerate RDF/XML in bad XML format, e.g. the first chars are not '<?xml'.
			reader.read(m, new StringReader( szText ) , szURL);
			
			if (m.size()==0)
				return false; 	//empty model;

	        if (debug){
		        PrintWriter out = new PrintWriter(System.out);
				printErrorMessages(out, oMyErrorHandler);
				out.flush();
	        }
			
			// check if parsed
			if (oMyErrorHandler.bHasFatalError){
				
				//this.m_rdf_parse_state = RDF_PARSE_ERROR; // TODO should be FATAL ERROR,  we will update it in the next version.
				//this.m_rdf_encoding = rdf_syntax;
				//this.m_warnings = oMyErrorHandler.warnings;
				//this.m_errors = oMyErrorHandler.errors+" | "+oMyErrorHandler.fatalErrors;
				//this.m_model = m;
				
				//return true;
			}else if (oMyErrorHandler.bHasError){
					if ( rdf_syntax.equals(RDF_ENCODING_RDFXML)
						|| (oMyErrorHandler.errors.indexOf("N3toRDF: All statements are asserted - no formulae in RDF")>0)
						){
					this.m_rdf_parse_state = RDF_PARSE_ERROR;
					this.m_rdf_encoding = rdf_syntax;
					this.m_warnings = oMyErrorHandler.warnings;
					this.m_errors = oMyErrorHandler.errors;
					this.m_model = m;
					
					return true;
				}
			}else if (oMyErrorHandler.bHasWarning){
				this.m_rdf_parse_state = RDF_PARSE_WARNING;
				this.m_rdf_encoding = rdf_syntax;
				this.m_warnings = oMyErrorHandler.warnings;
				this.m_model = m;
				
				return true;
			}else	{//error/warning free
				this.m_rdf_parse_state = RDF_PARSE_STRICT;
				this.m_rdf_encoding = rdf_syntax;
				this.m_model = m;
				
				return true;
			}
				
			

			//m.write(System.out, "RDF/XML");
        }catch (Exception e) {
            if (m.size()>0){
            	System.out.println(this.m_szURL);
            	System.out.println(parseType);
				RDFWriter w = m.getWriter("N-TRIPLE");
				w.write(m,System.out, szURL);
            	e.printStackTrace();
            }
            
        }

        if (this.m_rdf_parse_state != RDF_PARSE_NONE)
        	this.m_rdf_parse_state = RDF_PARSE_NOT_PURE;
        return false;
	}

	
	   private static void printErrorMessages(PrintWriter out, ToolSaxErrorHandler eh)
	    {
	        try {
	            String s;
	            boolean c = true;

	            out.println("<h2><a name='messages' id='messages'>" +
	                        "Validation Results</a></h2>");

	            s = eh.getFatalErrors();
	            if (s != null && s.length() >= 1) {
	                out.println("<h3>Fatal Error Messages</h3>" + s);
	                c = false;
	            }

	            s = eh.getErrors();
	            if (s != null && s.length() >= 1) {
	                out.println("<h3>Error Messages</h3>" + s);
	                c = false;
	            }

	            s = eh.getWarnings();
	            if (s != null && s.length() >= 1) {
	                out.println("<h3>Warning Messages</h3>" + s);
	                c = false;
	            }

	            if (c){
	              	
	            	out.println("<p>Your RDF document validated successfully.</p>");
	            }

	            /*the following should not happen anymore, as we use ARP2 which supports datatypes, but leave it there for now, just in case*/
	/*            s = eh.getDatatypeErrors();
	            if (s != null && s.length() >= 2){//2 to prevent an arrayindexoutofbounds exception
	                out.println("<h3>Note about datatypes</h3>");
	                out.println("<p>Datatypes are used on lines "+s.substring(0,s.length()-2)+". This RDF feature is not yet supported by the RDF Validator. Literals are treated as untyped.</p>");
	            }
	
	        } catch (Exception e) {
	            System.err.println( ": Error printing error messages.");
	        }
	    }



    

    /*
     * Search the given string for substring "key"
     * and if it is found, replace it with string "replacement"
     *
     *@param input the input string
     *@param key the string to search for
     *@param replacement the string to replace all occurences of "key"
     *@return if no substitutions are done, input is returned; otherwise
     * a new string is returned.
     
    /*    public static String replaceString(String input, String key,  String replacement) 
    {
        try {
            RE re = new RE(key);
            return re.subst(input, replacement);
        } catch (RESyntaxException e) {
            return input;
        }
    }


    
     
    
	private static void testXMLParserOnce(String szURL){
		DataCrawlerTask task = SimpleCrawler.crawl(szURL);
		
		if(!task.isDownloadSuccess())
			return;
		
		
		boolean b1 = RdfParser.isXMLdom1(task.getContent());
		

		boolean b2 = RdfParser.isXMLdom2(task.getContent(), task.getContentCharset());
		
		boolean b3 = true;//ReaderRDF.isXMLdom3(szURL);

		System.out.println(szURL);
		System.out.println("[1:"+b1+",2:"+b2+",3:"+b3+"]");
	}
	
	
	// reference online XML validator [Richard] http://www.hcrc.ed.ac.uk/~richard/xml-check.html.
	private static void testXMLParser(){
		// dom1,  dom2,  richard
		String [] aryURL = new String []{
			// 	[ true,  exception ]
			 //"http://blogspace.com/swhack/chatlogs/2002-01-15.rdf",
			
			// [ true, true ]  [FALSE]
			//  error message(Richard):  unexpected encoding shift-jis by 
			// comments: [Richard] can not handle multi language
			"http://morpheus.cs.umbc.edu/swoogle/service/cache.php?swoogleid=262574&view=ori",
			
			
			//  [false, true]  [TRUE] 
			//  error message(dom1): The prefix "syn" for element "syn:updatePeriod" is not bound.
			//  comments:  I believe in dom1 (false)  
			"http://morpheus.cs.umbc.edu/swoogle/service/cache.php?swoogleid=2220&view=ori",
			// same situation,  prefix not bounded
			"http://morpheus.cs.umbc.edu/swoogle/service/cache.php?swoogleid=109419&view=ori",
			"http://morpheus.cs.umbc.edu/swoogle/service/cache.php?swoogleid=260144&view=ori",
			"http://www.csee.umbc.edu/~dingli1/article51649028.xml",
			
		};
		 
		 for (int i=0; i<aryURL.length; i++){
		 	String szURL = aryURL[i];
			testXMLParserOnce(szURL);
		 }
	}    

		 
	
		debug = true;
		
		 for (int i=0; i<aryURL.length; i++){
		 	String szURL = aryURL[i].trim();
			System.out.println("["+i+"]"+szURL);
		 	
			DataCrawlerTask task = SimpleCrawler.crawl(szURL);
			
			if(!task.isDownloadSuccess())
				return;

			// test save to file
			try {
				System.out.println("http:"+task.getFileLength());
				String fn="temp\\rdftest\\"+i;
				ToolIO.pipeStringToFile(task.getContent(), fn);
				System.out.println("saved:"+ new File(fn).length());
			} catch (SwutilException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// test XML/ stric-RDF/ warning-RDF /embededRDF checker
			RdfParser reader = RdfParser.create(task.getContent(), szURL, task.getContentFormat());
			if (null!=reader){
				HashMap result = new HashMap();
				result.put("isXML",""+reader.isXML());
				result.put("isStrictRDF",""+reader.isStrictRDF());
				result.put("isWarningRDF",""+reader.isWarningRDF());
				result.put("isEmbeddedRDF",""+reader.isEmbeddedRDF());
				System.out.println(result);
				reader.print();
				
				task.print();
			}else{
				System.out.println("FATAL ERROR");
				task.print();
			}
			
			System.out.println("--------------------");
		 }
	}    	
	
	
	
	
	public static void test(){
		testXMLParser();
		testRDFParser();
	}
	
	public static void main(String[] args) {
		debug=true;
		checkCrawlURL();
		//testXMLParser();
	}
	*/
}

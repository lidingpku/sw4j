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
 * store RDF parse results
 * 
 *  @author: Li Ding
 */
/**
 * 
 */



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import sw4j.rdf.util.ToolJena;
import sw4j.task.common.AbstractTaskDesc;
import sw4j.task.load.TaskLoad;
import sw4j.util.Sw4jMessage;
import sw4j.util.ToolSafe;
import sw4j.util.web.ToolWeb;

public class TaskParseRdf extends AbstractTaskDesc {
	////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////

	////////////////////////////////////////////////
	// SwutilEvaluationTask
	////////////////////////////////////////////////
	public static final String ERROR_SUMMARY_1 = "issues found when parsing RDF using jena";
	public static final String ERROR_SUMMARY_2 = "unexpected issues found when parsing RDF using jena";
	public static final String ERROR_SUMMARY_3 = "cannot find xsl file when parsing RDFA using xslt translation";
	public static final String ERROR_SUMMARY_4 = "issues found when parsing RDFA using xslt translation";
	public static final String ERROR_SUMMARY_5 = "RDF parser need non empty content from the input";

	public static final String REPORT_TITLE ="RDF parsing and validation";
	public static final String REPORT_DESC ="This service checks if there exists any syntax errors in instance data";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}

	////////////////////////////////////////////////
	// state
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////
	// input
	////////////////////////////////////////////////
    private int m_nGuessFormat = ToolMarkupDetector.FORMAT_TEXT;
    public void setGuessFormat(int nGuessFormat){
    	m_nGuessFormat = nGuessFormat;
    }
    public int getGuessFormat(){
    	return m_nGuessFormat;
    }
    public String getGuessFormatString(){
    	return ToolMarkupDetector.FORMAT_NAME[m_nGuessFormat][0];
    }
	
	
    private String m_szRdfSyntax = null;
    public void setRdfSyntax(String szRdfSyntax){
    	m_szRdfSyntax = szRdfSyntax;
    }
    public String getRdfSyntax(){
    	return m_szRdfSyntax;
    }

	////////////////////////////////////////////////
	// output
	////////////////////////////////////////////////
	private Model m_model =null;

	public void addModel(Model m){
		if (null== m_model)
			m_model = ModelFactory.createDefaultModel();
		ToolJena.update_copy(m_model,m);
	}
	
	public boolean hasModel(){
		return null!=m_model && m_model.size()>0;
	}
    
	public Model getModel(){
		return m_model;
	}

	public void setModel(Model m){
		m_model = null;
		addModel(m);
	}

	String m_szRuntimeXmlbase = null;
	public void setRuntimeXmlbase(String szRuntimeXmlbase) {
		m_szRuntimeXmlbase = szRuntimeXmlbase;
	}
	
	public String getRuntimeXmlbase(){
		return m_szRuntimeXmlbase;
	}

	public static TaskParseRdf parse(TaskLoad input){
		if (null==input)
			return null;
	
		if (!input.isLoadSucceed())
			return null;
	
		
		TaskParseRdf output = new TaskParseRdf();
		
		if ( ToolSafe.isEmpty(input.getContent())){
			Integer error_level =Sw4jMessage.STATE_FATAL;
			String error_summary = TaskParseRdf.ERROR_SUMMARY_5;
			String error_details = null;
			String error_creator = TaskParseRdf.class.getSimpleName();
	
			output.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true);
			
	    	return output;
		}
		
		
		String szText = input.getContent();
		
		int iExpectedFormat = ToolMarkupDetector.guessFormat(input.getRdfSyntax(), input.getMimetype(), input.getRawUrl(), input.getXmlBase());
		int iFormat = ToolMarkupDetector.guessFormat(szText,iExpectedFormat);
		String szJenaSyntax = ToolMarkupDetector.getJenaSyntaxByFormat(iFormat);
		output.setGuessFormat(iFormat);
	
		// try to use the guessed format to parse
		HashSet<String> setTriedParse= new HashSet<String>();
		
		switch (iFormat){
		case ToolMarkupDetector.FORMAT_HTML: 
		case ToolMarkupDetector.FORMAT_XML: 
			if (ToolParserXml.validate(szText)){
				output.setGuessFormat(ToolMarkupDetector.FORMAT_XML);
	
				ToolParseRdfaXslt.parse(input, output);
				setTriedParse.add(""+ToolMarkupDetector.FORMAT_TEXT_RDFA);
				if (output.hasModel()){
			    	 output.setGuessFormat(ToolMarkupDetector.FORMAT_TEXT_RDFA);
			    	 return output;
				}
			}else{
				//if it is not xml, then it can only be html or text
				output.setGuessFormat(ToolMarkupDetector.FORMAT_HTML);
			}			
			return output;
			//break;
		case ToolMarkupDetector.FORMAT_HTML_EMBED_RDFXML: 
		case ToolMarkupDetector.FORMAT_TEXT_EMBED_RDFXML: 
			ArrayList<String> rdf_data = ToolWeb.extractMarkup(szText, "<rdf:RDF", "</rdf:RDF>");
			Iterator<String> iter = rdf_data.iterator();
			while (iter.hasNext()){
				String szRdfxmlText = iter.next();
				ToolParseRdfJena.parse(szRdfxmlText, input.getXmlBase(), szJenaSyntax, output);
				setTriedParse.add(""+ToolMarkupDetector.FORMAT_TEXT_EMBED_RDFXML);
				setTriedParse.add(""+ToolMarkupDetector.FORMAT_HTML_EMBED_RDFXML);
			}
			if (output.hasModel()){
				return output;
			}else{
		    //	 output.setGuessFormat(ToolMarkupDetector.FORMAT_TEXT);
			}
			break;
		case ToolMarkupDetector.FORMAT_RDFXML: 
		case ToolMarkupDetector.FORMAT_TEXT_N3: 
		case ToolMarkupDetector.FORMAT_TEXT_NT:
			setTriedParse.add(""+iFormat);
			if (tryParseJena(input, output, iFormat))
				return output;
			break;
		default:
				
		}
		
		// in case guessed format does not work, try all parsers one by one
		if (!setTriedParse.contains(""+ToolMarkupDetector.FORMAT_RDFXML) && input.getRdfSyntax()!=null){
			if (tryParseJena(input, output, ToolMarkupDetector.FORMAT_RDFXML))
				return output;
		}
		if (!setTriedParse.contains(""+ToolMarkupDetector.FORMAT_HTML_EMBED_RDFXML)){
			ArrayList<String> rdf_data = ToolWeb.extractMarkup(szText, "<rdf:RDF", "</rdf:RDF>");
			Iterator<String> iter = rdf_data.iterator();
			while (iter.hasNext()){
				String szRdfxmlText = iter.next();
				ToolParseRdfJena.parse(szRdfxmlText, input.getXmlBase(), szJenaSyntax, output);
				setTriedParse.add(""+ToolMarkupDetector.FORMAT_TEXT_EMBED_RDFXML);
				setTriedParse.add(""+ToolMarkupDetector.FORMAT_HTML_EMBED_RDFXML);
				
		    	output.setGuessFormat(ToolMarkupDetector.FORMAT_HTML_EMBED_RDFXML);
			}
			if (rdf_data.size()>0){
				return output;
			}
		}
	
		if (!setTriedParse.contains(""+ToolMarkupDetector.FORMAT_TEXT_N3)&& input.getRdfSyntax()!=null){
			if (tryParseJena(input, output, ToolMarkupDetector.FORMAT_TEXT_N3))
				return output;
		}
		if (!setTriedParse.contains(""+ToolMarkupDetector.FORMAT_TEXT_NT)&& input.getRdfSyntax()!=null){
			if (tryParseJena(input, output, ToolMarkupDetector.FORMAT_TEXT_NT))
				return output;
		}
		
		output.setGuessFormat(ToolMarkupDetector.FORMAT_TEXT);
		return output;
	}

	private static boolean tryParseJena(TaskLoad input, TaskParseRdf output, int format){
		String szJenaSyntax = ToolMarkupDetector.getJenaSyntaxByFormat(format);
		try{
			ToolParseRdfJena.parse(input.getContent(), input.getXmlBase(), szJenaSyntax, output);
			if (output.hasModel()){
		    	 output.setGuessFormat(format);
		    	 return true;
			}else{
				return false;
			}
		}catch (com.hp.hpl.jena.shared.SyntaxError e){
			//e.printStackTrace();
			return false;
		}
	}

	public static String createRuntimeXmlbase(){
		return "http://tw.rpi.edu/_runtime_"+ System.currentTimeMillis()+"_"+String.format("%03.0f", Math.random()*999)+"#";
	}

	////////////////////////////////////////////////
	// functions
	////////////////////////////////////////////////

    /*
    public String toString(){
    	String temp = "";
    	temp += "processed by: " + getCreator()+"\n";
    	if (null == m_model){
        	temp += "result: failed\n";
    	}else{
        	temp += "result: succeed\n";
        	temp += "syntax: " + m_szRdfSyntax +"\n";
        	temp += "# triples: " + m_model.size() +"\n";
    	}
    	
    	return temp;
    }
    */
}

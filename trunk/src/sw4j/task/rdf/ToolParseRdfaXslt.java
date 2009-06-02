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
package sw4j.task.rdf;
/**
 * parse RDFa using xslt
 * TODO: require jdk 1.5.12 or later because memory leak issues.  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6434840
 * 
 *  @author: Li Ding
 */
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import sw4j.task.load.TaskLoad;
import sw4j.util.Sw4jMessage;
import sw4j.util.ToolSafe;

public class ToolParseRdfaXslt {
	public static boolean debug = false;
	protected static Logger getLogger(){
		return Logger.getLogger(ToolParseRdfaXslt.class);
	}

	
	/**
	 * http://ns.inria.fr/grddl/rdfa/
	 * 
	 * @return
	 */
	public static TaskParseRdf parse(TaskLoad data_crawl, TaskParseRdf  output){
		if (debug)
			System.out.println("ToolParseRdfaXslt");

		if (null== data_crawl || ToolSafe.isEmpty(data_crawl.getContent()))
			return null;
		
		 
		
		try {
			javax.xml.transform.Source xmlSource =  new javax.xml.transform.stream.StreamSource( new StringReader(data_crawl.getContent()));
			
			/**
			 * local rdfa xslt source.
			 * The file is a copy of  
			 *      http://ns.inria.fr/grddl/rdfa/2008/04/05/RDFa2RDFXML.xsl
			 */
			javax.xml.transform.Source xsltSource =  
						new javax.xml.transform.stream.StreamSource( 
								ToolParseRdfaXslt.class.getResourceAsStream("RDFa2RDFXML.xsl") );
	
			StringWriter sw = new StringWriter();
			javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(new PrintWriter(sw));
	
		     // create an instance of TransformerFactory
		     javax.xml.transform.TransformerFactory transFact =  javax.xml.transform.TransformerFactory.newInstance(  );
	     
		     javax.xml.transform.Transformer trans;
			 trans = transFact.newTransformer(xsltSource);
		     trans.transform(xmlSource, result);
		     
		     //System.out.println(sw);
		     
		     ToolParseRdfJena.parse(sw.toString(), data_crawl.getXmlBase().toString(), RDFSYNTAX.RDFXML, output);
		     if (output.hasModel()){
		    	 output.setRdfSyntax(RDFSYNTAX.RDFA);
		     }
		     
		     return output;
		     
		} catch (TransformerConfigurationException e) {
			Integer error_level =Sw4jMessage.STATE_FATAL;
			String error_summary = TaskParseRdf.ERROR_SUMMARY_3;
			String error_details = e.getLocalizedMessage();
			String error_creator = ToolParseRdfaXslt.class.getSimpleName();

			output.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true);
				
		} catch (TransformerException e) {
			Integer error_level =Sw4jMessage.STATE_FATAL;
			String error_summary = TaskParseRdf.ERROR_SUMMARY_4;
			String error_details = e.getLocalizedMessage();
			String error_creator = ToolParseRdfaXslt.class.getSimpleName();

			output.getReport().addEntry(error_level, error_summary, error_creator, error_details, true);
		}
	
		return output;
	}
		
}

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
 *  error handling for xml parser
 *  
 *  @author: Li Ding
 */
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import sw4j.task.common.DataTaskReport;
import sw4j.util.Sw4jMessage;

import com.hp.hpl.jena.rdf.model.RDFErrorHandler;

public class ToolMyErrorHandler implements RDFErrorHandler, org.xml.sax.ErrorHandler{ 
    boolean debug = false;
    DataTaskReport m_report = null;
    Logger m_logger = null;
    
    /**
     * constructor
     * 
     * @param report   the error report generated by this error handler
     */
    public ToolMyErrorHandler(DataTaskReport report, Logger logger){
    	m_report = report;
    	m_logger = logger;
    }
	

    /*
     * Create a formatted string from the exception's message
     *
     *@param e the SAX Parse Exception
     *@return a formatted string
     
    private static String format(org.xml.sax.SAXParseException e) 
    {
        String msg = e.getMessage();
        if (msg == null)
            msg = e.toString();
        
        msg = msg.replaceAll("&","&amp;");
        msg = msg.replaceAll("<","&lt;");
        msg = msg.replaceAll(">","&gt;");
        msg = msg.replaceAll("\"","&quot;");
        msg = msg.replaceAll("'","&apos;");
        return msg + "[Line = " + e.getLineNumber() + ", Column = " + e.getColumnNumber() + "]";
    }
    */
    
    ///////////////////////////////////
	//RDFErrorHandler

    /**
     * Handle a fatal parse error
     *
     *@param e the SAX Parse Exception
     */
    public void error(Exception e) {
    	Integer error_level =Sw4jMessage.STATE_ERROR;
        if (e instanceof com.hp.hpl.jena.rdf.arp.ParseException){
        	reportException((org.xml.sax.SAXParseException)e, error_level);
        }else if (e instanceof org.xml.sax.SAXParseException){
        	reportException((org.xml.sax.SAXParseException)e, error_level);
        }else {
        	reportException(e, error_level);
        }
    }
    
    static final int MAX_REPORT = 100;
    private void reportException(org.xml.sax.SAXParseException e, Integer error_level){
    	if (m_report.count_entries(error_level)>MAX_REPORT){
    		System.out.println("too many errors, skip 100+ ");
    		return;
    	}
    	
		String error_summary = TaskParseRdf.ERROR_SUMMARY_1;
		String error_details = String.format("(line: %d, column: %d) %s", 
				e.getLineNumber(),
				e.getColumnNumber(),
				e.getLocalizedMessage());

		String error_creator = this.getClass().getSimpleName();

		m_report.addEntry(error_level, error_summary,  error_creator, error_details, true);
    }

    private void reportException(Exception e, Integer error_level){
		String error_summary = TaskParseRdf.ERROR_SUMMARY_2;
		String error_details =  e.getLocalizedMessage();

		String error_creator = this.getClass().getSimpleName();

		m_report.addEntry(error_level, error_summary, error_creator, error_details, true);
    }
	
    /**
     * Handle a fatal parse error
     *
     *@param e the SAX Parse Exception
     */
    public void fatalError(Exception e){
    	Integer error_level =Sw4jMessage.STATE_FATAL;
    	if (e instanceof com.hp.hpl.jena.rdf.arp.ParseException){
        	reportException((org.xml.sax.SAXParseException)e, error_level );
        }else if (e instanceof org.xml.sax.SAXParseException){
        	reportException((org.xml.sax.SAXParseException)e, error_level );
        }else {
        	reportException(e, error_level );
        }
    }
	
    /**
     * Handle a parse warning
     *
     *@param e the SAX Parse Exception
     */
    public void warning(Exception e){
    	Integer error_level =Sw4jMessage.STATE_WARNING;
    	if (e instanceof com.hp.hpl.jena.rdf.arp.ParseException){
        	reportException((org.xml.sax.SAXParseException)e, error_level );
        }else if (e instanceof org.xml.sax.SAXParseException){
        	reportException((org.xml.sax.SAXParseException)e, error_level );
        }else {
        	reportException(e, error_level );
        }
    }

    /////////////////////////////////////////////////
    // XML syntax checking
	public void error(SAXParseException arg0) throws SAXException {
		error((Exception)arg0);
	}

	public void fatalError(SAXParseException arg0) throws SAXException {
		fatalError((Exception)arg0);
	}

	public void warning(SAXParseException arg0) throws SAXException {
		warning((Exception)arg0);
	}		
		
}

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
package sw4j.task.util;
/**
 * a wrapper of all task reports 
 * 
 * @author Li Ding
 * 
 */

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import sw4j.task.common.DataTaskReport;
import sw4j.util.DataSmartMap;

public class DataTaskResult {
	public ArrayList<DataTaskReport> reports = new ArrayList<DataTaskReport>();
	boolean m_bSuccessful = true;
	public void setSuccessful(boolean bSuccessful){
		m_bSuccessful = bSuccessful;
	}
	public boolean isSuccessful(){
		return m_bSuccessful;
	}
	
	
	public static final String SUMMARY_TAG = "Sw4jResult";
	public static final String SUMMARY_RESULT = "result";
//	public static final String SUMMARY_CNT_ISSUES = "cnt_issues";
	public static final String REPORT_PROP = "hasReport";

	
    public String toHtml(){
    	try{
			String szXml = toXml();

			javax.xml.transform.Source xmlSource =  new javax.xml.transform.stream.StreamSource( new StringReader(szXml));

			javax.xml.transform.Source xsltSource =  
				new javax.xml.transform.stream.StreamSource( 
						DataTaskReport.class.getResourceAsStream("report.xsl") );

			StringWriter sw = new StringWriter();
			PrintWriter out = new PrintWriter(sw);
			javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(out);
		
			 // create an instance of TransformerFactory
			 javax.xml.transform.TransformerFactory transFact =  javax.xml.transform.TransformerFactory.newInstance(  );
		
			 javax.xml.transform.Transformer trans;
			 trans = transFact.newTransformer(xsltSource);
			 trans.transform(xmlSource, result);
			 
/*
			String szContent =null;
				
			Iterator<SwutilEvaluationReport> iter = reports.iterator();
			while (iter.hasNext()){
				SwutilEvaluationReport report = iter.next();
				
				if (ToolCommon.isEmpty(szContent))
					szContent = report.m_interalState.getEntryAsString(LOAD_RAW_CONTENT);
			}
			
			if (!ToolCommon.isEmpty(szContent)){
				out.println("<pre>");
				out.println(szContent);
				out.println("</pre>");
			}
*/			 
			 
			return sw.toString();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

    }

	
    public String toXml(){
		try{
			StringWriter sw = new StringWriter();
			PrintWriter out = new PrintWriter(sw);
			toXml(out);
			return sw.toString();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
    }

    
	public  void toXml(PrintWriter out) throws SAXException, TransformerConfigurationException{
		DataSmartMap evaluation_result = new DataSmartMap(SUMMARY_TAG);
		AttributesImpl atts = new AttributesImpl();
		{
			boolean bIsSucceed =true;
			int nIssues = 0;
			Iterator<DataTaskReport> iter = reports.iterator();
			while (iter.hasNext()){
				DataTaskReport report = iter.next();
				bIsSucceed &= !report.hasFatal();
				nIssues +=report.countIssues();
			}
			
			evaluation_result.put(SUMMARY_RESULT, bIsSucceed?"passed":"failed");
//			evaluation_result.put(SUMMARY_CNT_ISSUES, nIssues);
		}
		
		
		
		
		TransformerHandler hd = null;
		Iterator<DataTaskReport> iter = reports.iterator();
		while (iter.hasNext()){
			DataTaskReport report = iter.next();
			
			if (null== hd){
				hd = report.xmlStartDocument(out);
				evaluation_result.xmlStartContent(hd);
			}
			hd.startElement("","",REPORT_PROP,atts);
			report.xmlStartContent(hd);
			report.xmlEndContent(hd);
			hd.endElement("","",REPORT_PROP);
			
			if (!iter.hasNext()){
				evaluation_result.xmlEndContent(hd);
				report.xmlEndDocument(hd);
			}
		}
	}
	
}

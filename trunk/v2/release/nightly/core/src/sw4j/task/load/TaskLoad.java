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
package sw4j.task.load;
/**
 * 
 * the general abstract task of loading data into memory
 * 
 * @author Li Ding
 * 
 */

import java.io.File;

import sw4j.task.common.DataTaskReport;
import sw4j.task.common.AbstractTaskDesc;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.Sw4jMessage;
import sw4j.util.ToolSafe;
import sw4j.util.ToolHash;
import sw4j.util.ToolIO;
import sw4j.util.ToolURI;

abstract public class TaskLoad extends AbstractTaskDesc{
	////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////
	protected TaskLoad(String szURL, String szXmlBase){
		m_szURL = szURL;
		if (ToolSafe.isEmpty(szXmlBase)){
			szXmlBase = szURL;
		}
		m_szXmlBase = DataQname.extractNamespaceUrl(szXmlBase);
		
		getReport().put(DataTaskReport.LOAD_FILE_OR_URL, m_szURL);
		getReport().put(DataTaskReport.LOAD_XML_BASE, m_szXmlBase);
		m_nState = STATE_INPUT_NEW_TASK;
	}

	////////////////////////////////////////////////
	// sub class
	////////////////////////////////////////////////
	public abstract boolean isHttpLoader();
	protected abstract boolean validateTask();
	
	
	////////////////////////////////////////////////
	// SwutilEvaluationTask (super class)
	////////////////////////////////////////////////
	public static final String ERROR_SUMMARY_1 = "Cannot load content from the specified file or URL.";
	public static final String ERROR_SUMMARY_2 = "Empty text content.";
	
	public static final String REPORT_TITLE ="Load Data";
	public static final String REPORT_DESC ="This service loads data from a file, URL, or test string";

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
	
	public final static int STATE_INPUT_NEW_TASK = 0;
	public final static int STATE_INPUT_VALIDATED_TASK = 20;
	public final static int STATE_OUTPUT_FAILED_CONNECTION_CANNOT_OPEN = 30;
	public final static int STATE_OUTPUT_FAILED_CONNECTION_NOT_HTTP = 31;
	public final static int STATE_OUTPUT_FAILED_DOWNLOAD_BAD_CHARSET = 40;
	public final static int STATE_OUTPUT_FAILED_DOWNLOAD_INTERRUPT = 41;
	public final static int STATE_OUTPUT_FAILED_DOWNLOAD_OUTOF_MEMORY = 42;
	public final static int STATE_OUTPUT_FAILED_HTTP_CODE_BAD_OTHER = 37;
	public final static int STATE_OUTPUT_FAILED_HTTP_CODE_FORBIDDEN = 33;
	public final static int STATE_OUTPUT_FAILED_HTTP_CODE_NOT_FOUND = 34;
	public final static int STATE_OUTPUT_FAILED_HTTP_CODE_PERMANENT_REDIRECTED = 35;
	public final static int STATE_OUTPUT_FAILED_HTTP_CODE_REDIRECTED = 36;
	public final static int STATE_OUTPUT_FAILED_HTTP_NO_CODE = 32;
	public final static int STATE_OUTPUT_FAILED_PRECRAWL_NOT_CRAWL = 22;
	public final static int STATE_OUTPUT_FAILED_PRECRAWL_ROBOTS_TXT = 21;
	public final static int STATE_OUTPUT_INVALID_BAD_URL = 10;
	public final static int STATE_OUTPUT_INVALID_CRAWLER_TRAP = 11;
	public final static int STATE_OUTPUT_INVALID_EMPTY_URL = 12;
	public final static int STATE_OUTPUT_INVALID_EMPTY_TEXT = 13;
	
	public final static int STATE_OUTPUT_SUCCESS_DOWNLOAD = 99;

	private int m_nState = STATE_INPUT_NEW_TASK;
	private String m_szStateMessage = "unknown error when loading data";
	private String m_szStateMessageDetails = null;

	public int getState(){
		return m_nState;
	}
	public String getStateString(){
		switch (m_nState){
		case STATE_INPUT_NEW_TASK :
			return "new task";
		case STATE_INPUT_VALIDATED_TASK :
			return "valid task";
		case STATE_OUTPUT_FAILED_CONNECTION_CANNOT_OPEN :
			return "http connect fail";
		case STATE_OUTPUT_FAILED_CONNECTION_NOT_HTTP :
			return "bad url protocol";
		case STATE_OUTPUT_FAILED_DOWNLOAD_BAD_CHARSET :
			return "bad char encoding";
		case STATE_OUTPUT_FAILED_DOWNLOAD_INTERRUPT :
			return "download interrput";
		case STATE_OUTPUT_FAILED_DOWNLOAD_OUTOF_MEMORY :
			return "out of memory";
		case STATE_OUTPUT_FAILED_HTTP_CODE_BAD_OTHER :
			return "http other code";
		case STATE_OUTPUT_FAILED_HTTP_CODE_FORBIDDEN :
			return "http forbid";
		case STATE_OUTPUT_FAILED_HTTP_CODE_NOT_FOUND :
			return "http not found";
		case STATE_OUTPUT_FAILED_HTTP_CODE_PERMANENT_REDIRECTED :
			return "http moved";
		case STATE_OUTPUT_FAILED_HTTP_CODE_REDIRECTED :
			return "http redirected";
		case STATE_OUTPUT_FAILED_HTTP_NO_CODE :
			return "http no code";
		case STATE_OUTPUT_FAILED_PRECRAWL_NOT_CRAWL :
			return "we avoid";
		case STATE_OUTPUT_FAILED_PRECRAWL_ROBOTS_TXT :
			return "robots.txt";
		case STATE_OUTPUT_INVALID_BAD_URL :
			return "bad url";
		case STATE_OUTPUT_INVALID_CRAWLER_TRAP :
			return "crawler trap";
		case STATE_OUTPUT_INVALID_EMPTY_URL :
			return "empty url";
		case STATE_OUTPUT_INVALID_EMPTY_TEXT:
			return "empty text";
		case STATE_OUTPUT_SUCCESS_DOWNLOAD :
			return "ok";
		default:
			return "unknown";
		}
	}
	public boolean isLoadSucceed(){
		return STATE_OUTPUT_SUCCESS_DOWNLOAD <=getState();
	}
	public boolean isLoadUnfinished(){
		switch (getState()){
		case STATE_INPUT_NEW_TASK: return true;
		case STATE_INPUT_VALIDATED_TASK: return true;
		default:
			return false;
		}
	}
	
	public void setState(int state, String szStateMessage, String szStateMessageDetails){
		m_nState = state;
		m_szStateMessage = szStateMessage;
		m_szStateMessageDetails = szStateMessageDetails;
		
		updateReport();
	}
	
	private void updateReport(){
		
		if (this.isLoadSucceed()){
		//	String error_level =SwutilEvaluationReport.LEVEL_INFO;
		//	String error_summary = "load succeed";
		//	String error_msg = getStateMessage();
		//	String error_details = getStateMessageDetails();
		//	Logger logger = getLogger();
		//	report.addEntry(error_level, error_summary, error_msg, error_location, error_suggestion, error_msg_raw, logger);
		} else if (isLoadUnfinished()){

		} else if (getState()== STATE_OUTPUT_INVALID_EMPTY_TEXT){
			Integer error_level =Sw4jMessage.STATE_FATAL;
			String error_summary = ERROR_SUMMARY_2;
			String error_details = getStateMessageDetails();
			String error_creator = this.getClass().getSimpleName();

			this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, false);

		} else {
			Integer error_level =Sw4jMessage.STATE_FATAL;
			String error_summary = ERROR_SUMMARY_1;
			String error_details = getStateMessageDetails();
			String error_creator = this.getClass().getSimpleName();

			this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, false);
		}
	}

	public String getStateMessage(){
		return String.format("(E%03d) %s", getState(), m_szStateMessage); 
	}
	public String getStateMessageDetails(){
		return m_szStateMessageDetails; 
	}
	

	////////////////////////////////////////////////
	//  input
	////////////////////////////////////////////////
	
	protected String m_szURL =null;
	public String getRawUrl(){
		return m_szURL;
	}
	
	
	protected String m_szXmlBase=null;
	public String getXmlBase(){
		return m_szXmlBase;
	}

	
	////////////////////////////////////////////////
	//  output
	////////////////////////////////////////////////
	private String m_content = null;
	protected void setContent(String szContent){
		m_content = szContent;
		getReport().addInternalState(DataTaskReport.LOAD_RAW_CONTENT, m_content);
	}
	public String getContent(){
		return m_content;
	}
	
	protected String m_content_charset = null;
	public String getContentCharset(){
		return m_content_charset;
	}
	
	protected String m_szRdfSyntax = null;
	public String getRdfSyntax() {
		return this.m_szRdfSyntax;
	}
	protected String m_szMimetype = null;
	public String getMimetype() {
		return this.m_szMimetype;
	}

	protected byte[] m_ignoredBytes = null;
	protected byte[] m_bytes = null;
	
	long m_lastmodified = 0;
	public long getLastmodified(){
		return m_lastmodified;
	}
	
	////////////////////////////////////////////////
	//  functions 
	////////////////////////////////////////////////
	public int getFileLength(){
		if (null==m_bytes)
			return -1;
		else
			return m_bytes.length;
	}
	
	public String getSha1sum(){
		return ToolHash.hash_sum_sha1(m_bytes);
	}
	
	public String getMd5sum(){
		return ToolHash.hash_sum_md5(m_bytes);
	}
	
	public void writeToFile(File f, boolean bGzip) throws Sw4jException{
        ToolSafe.checkNonEmpty(this.m_content, "Expect non empty content");
        ToolSafe.checkNonEmpty(this.m_content_charset, "Expect non empty charset");

        ToolIO.pipeBytesToFile(this.m_bytes, f, false,bGzip);
	}

	public void print(){
		System.out.println(m_nState);
		if (null!=m_content)
			System.out.println(this.m_content.substring(0,Math.min(this.m_content.length(),200)));	
	}

	public static TaskLoad load(String szURL){
		return load(szURL, null, null);
		
	}

	public static TaskLoad load(String szURL, String szXmlBase){
		return load(szURL, szXmlBase, null);
	}

	public static TaskLoad load(String szURL, String szXmlBase, String szRdfSyntax){
		if (ToolURI.isUriHttp(szURL)){
			return ToolLoadHttp.loadUrl(szURL, szXmlBase, szRdfSyntax);
		}else{
			return ToolLoadFile.loadFile(szURL, szXmlBase, szRdfSyntax);
		}
	}

	public static TaskLoad loadText(String szText, String szXmlBase, String szRdfSyntax){
		return ToolLoadText.loadText(szText,szXmlBase, szRdfSyntax);
	}

}

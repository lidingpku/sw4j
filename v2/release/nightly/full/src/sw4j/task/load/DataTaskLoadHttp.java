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
 * loading Web data to memory
 * 
 * @author Li Ding
 * 
 */
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;
import sw4j.util.ToolURI;

public class DataTaskLoadHttp extends TaskLoad{

	public DataTaskLoadHttp(String szURL, String szXmlBase){
		super(szURL,szXmlBase);
	}
	

	
	public HttpURLConnection m_conn = null;
	public int m_nHttpResponseCode = 200;
	public boolean m_bHttpRedirected = false;
	public String m_szRedirectedURL = null;


	public void print(){
		super.print();
		if (null!= m_conn)
			System.out.println(this.m_conn.getHeaderFields());
	}



	/**
	 * validate task.
	 * * check if url is property
	 * * check if url is a crawler trap
	 * 
	 * @return
	 */
	protected boolean validateTask(){
		if (STATE_INPUT_NEW_TASK!= getState()){
			switch (getState()){
			case STATE_OUTPUT_INVALID_BAD_URL:
			case STATE_OUTPUT_INVALID_CRAWLER_TRAP:
				return false;
			default:
				return true;
			}
		}
		
		setState(STATE_INPUT_VALIDATED_TASK, "unknown issue in the file or url", null); 
	
		if (ToolSafe.isEmpty(m_szURL)){
			setState( STATE_OUTPUT_INVALID_BAD_URL, "empty file or url" ,null);
			return false;
		}
		
		try {
			new URL(this.getRawUrl());
		} catch (MalformedURLException e1) {
			setState( STATE_OUTPUT_INVALID_BAD_URL, "the url "+this.m_szURL+" is malformed ", e1.getLocalizedMessage());
			return false;
		}
	
		try {
			ToolURI.validateUri_crawlerTrap(m_szURL);
		} catch (Sw4jException e) {
			getLogger().info(e.getMessage());
			setState( STATE_OUTPUT_INVALID_CRAWLER_TRAP, "this url "+this.m_szURL+" is a crawler trap", null);
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isHttpLoader() {
		return true;
	}




}

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
 * This file used a lot from W3C's RDF/XML validator code
 *  @author: Li Ding
 */

/**
 * 
 * 
 * source:  http://dev.w3.org/cvsweb/java/classes/org/w3c/rdf/examples/ARPServlet.java (v1.78)
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;


import sw4j.util.DataCachedObjectMap;
import sw4j.util.DataQname;
import sw4j.util.ToolSafe;
import sw4j.util.ToolURI;
import sw4j.util.web.DataRobotsTxt;

public class ToolLoadHttp extends ToolLoad{
	private static Logger getLogger() {
		return Logger.getLogger(ToolLoadHttp.class);
	}
	
//	public static String CRAWLER_NAME = "swooglebot"; 
//	public static String HTTP_USER_AGENT = "Swooglebot/2.0. (+http://swoogle.umbc.edu/swooglebot.html)";
//  private final static String HTTP_USER_AGENT = "Mozilla/4.0 (compatible;MSIE 5.5; Windows NT 5.0; H010818)";
	public static String HTTP_ACCEPT_MIME= "application/rdf+xml;q=1, text/xml,application/xml;q=0.6, text/n3,text/rdf+n3,text/turtle;q=0.9, application/rss+xml; q=0.4, application/x-gzip;q=0.3, text/html;q=0.3, text/plain;q=0.1,";
	//http://dowhatimean.net/2008/03/what-is-your-rdf-browsers-accept-header
	//
	// now we plan to support RDF stored in gzip form (assuming the xml:base has been asserted in the zipped RDF)

	//Sets a request property on the supplied connection indicating that a
	// server can respond with gzip-encoded data if it wants to.
	public static String HTTP_ACCEPT_ENCODING = "gzip";

	
//	public static String HTTP_ACCEPT= "application/rdf+xml, text/rdf+n3, application/rdf+n3, text/xml, application/rss+xml, text/*, */*"; 
 	
    private final static int TIME_OUT = 60*1000; // 60 seconds

    private String m_user_agent = null;
	private ToolLoadHttp(String user_agent){
		m_user_agent = user_agent;
        System.setProperty("sun.net.client.defaultConnectTimeout",  ""+ TIME_OUT);
		System.setProperty("sun.net.client.defaultReadTimeout",  ""+TIME_OUT);
	}

	public String getName(){
		int index = m_user_agent.indexOf(" ");
		if (index<=0){
			return null;
		}else{
			return m_user_agent.substring(0, index);
		}
	}
	
	
	private static ToolLoadHttp gBot = null;
	public static ToolLoadHttp getSwaBot(){
		if (null==gBot)
			gBot = new ToolLoadHttp("Swabot: semantic web archive bot");		
		return gBot;
	}
	
	public static String wget(String szURL){
		if(!ToolURI.isUriHttp(szURL))
			return null;
		
		  DataTaskLoadHttp task = new DataTaskLoadHttp(szURL, szURL);
		  ToolLoadHttp.getSwaBot().load(task);
		  if (task.isLoadSucceed())
			  return task.getContent();
		  else
			  return null;
	}
	
	protected void load(DataTaskLoadHttp task){
		// validate task 
		if (!task.validateTask()){
			return;
		}
				
		URL url;
		try {
			url = new URL (task.getRawUrl());
		} catch (MalformedURLException e1) {
			return;
		}
		// assert url != null

		getLogger().info("loading: "+ url.toString());

		//test robots.txt
		if (testDisallow(url.toString())){
			task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_PRECRAWL_ROBOTS_TXT,"the url "+task.m_szURL+" is disallowed by robots.txt from the website", null);
			return;
		}		
		
		if (testDoNotCrawl(url.toString())){
			task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_PRECRAWL_NOT_CRAWL, "the url "+task.m_szURL+" is in do-not-crawl list", null);
			return;
		}

		// connect 
		URLConnection connection =null;
		try {
			connection = connect(url, true);
		} catch (IOException e) {
			getLogger().info(e.getMessage());
			task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_CONNECTION_CANNOT_OPEN, "cannot connect the url "+task.m_szURL+" on the web server", e.getLocalizedMessage());
			return;
		}
		
		// if connection is http connection, then get http connection info
		try{
			task.m_conn = (HttpURLConnection)connection;
	        // process http response information
			task.m_nHttpResponseCode = task.m_conn.getResponseCode();

			//handle response code
			switch (task.m_nHttpResponseCode) {
				case 200:
				case 406:
					// good reponse
					break;
				case 400:	//Bad Request. The client SHOULD NOT repeat the request without modifications. 
				case 401:	//Unauthorized. The client MAY repeat the request with a suitable Authorization header field  
				case 403:	//Forbidden: SHOULD unsubscribe.
				case 410:	//Gone: MUST unsubscribe
					task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_HTTP_CODE_FORBIDDEN, "the url "+task.m_szURL+" is forbidded by the web server, and the http code is "+ task.m_nHttpResponseCode+".", null);
					return;
				case 404:	//Forbidden: file not found
					task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_HTTP_CODE_NOT_FOUND, "the url "+task.m_szURL+" is not found by the web server, and http code is "+ task.m_nHttpResponseCode+".", null);
					return;					
				case 301:	//Permanent redirect: SHOULD check new location thereafter. 
					task.m_bHttpRedirected =true;
					task.m_szRedirectedURL = ToolLoadHttp.extractHTTPredirectedURL(task.m_conn);
					task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_HTTP_CODE_PERMANENT_REDIRECTED, "the url "+task.m_szURL+" is forbiden and permanently redirected to "+ task.m_szRedirectedURL+", and the http code is "+ task.m_nHttpResponseCode+".", null);
					break;
				case 302: 	//redirected
				case 300: 
				case 307:
					//case 4: redirected
					task.m_bHttpRedirected =true;
					task.m_szRedirectedURL = ToolLoadHttp.extractHTTPredirectedURL(task.m_conn);
					task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_HTTP_CODE_REDIRECTED, "the url "+task.m_szURL+" is redirected to "+  task.m_szRedirectedURL+", and the http code is "+ task.m_nHttpResponseCode+".", null);
					break;
				default:
					task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_HTTP_CODE_BAD_OTHER, "stop crawling the url "+task.m_szURL+" due to bad http response code, and the http code is "+ task.m_nHttpResponseCode+".", null);
					return;
			}      
			
			task.m_lastmodified = Math.max(0, task.m_conn.getLastModified());
			
			// extract content mime type
			{
				String szTemp = task.m_conn.getContentType();
				if (!ToolSafe.isEmpty(szTemp)){
					int index = szTemp.indexOf(";");
					if (index>0){
						szTemp = szTemp.substring(0,index).trim();
						task.m_szMimetype = szTemp; 
					}
				}
			}
        } catch (ClassCastException e) {
			task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_CONNECTION_NOT_HTTP, "the java URLConnection is not http connection", e.getLocalizedMessage());
			return;        	
        } catch (IOException e) {
			task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_HTTP_NO_CODE, "exception when connecting to the web server", e.getLocalizedMessage());
			return;        	
		}
        
        // download document; extract content encoding and format
		try {
			InputStream in = connection.getInputStream();
			
        	//determine if the input stream is gzipped
			boolean bZip= false;

        	String content_type = task.m_conn.getContentType();
			if (!bZip && !ToolSafe.isEmpty(content_type)){
				if (content_type.indexOf("application/x-gzip")>=0){
					in = new GZIPInputStream(in);
				}else if (content_type.indexOf("application/zip")>=0){
					in = new ZipInputStream(in);
				}
			}
			
			//example: http://creativecommons.org/licenses/by/2.0/
			 
    		String szContentEncoding = task.m_conn.getContentEncoding();
			if (!bZip && !ToolSafe.isEmpty(szContentEncoding)){
				if ("gzip".equalsIgnoreCase(szContentEncoding)) {
					in = new GZIPInputStream(in);
				}else if ("zip".equalsIgnoreCase(szContentEncoding)) {
					in = new ZipInputStream(in);
				}
			}
			
			saveStream(in, task.m_conn, task);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private URLConnection connect(URL url, boolean bFollowRedirection) throws IOException {        
        URLConnection con = null;
        con = url.openConnection();
        con.setRequestProperty("Accept", HTTP_ACCEPT_MIME);
        con.setRequestProperty("User-Agent", this.m_user_agent);
		con.setRequestProperty("Accept-Encoding", HTTP_ACCEPT_ENCODING);        
        ((HttpURLConnection)con).setInstanceFollowRedirects(bFollowRedirection);	// set follow redirection 
        
        con.connect();
        return con;
    }	
    	
    private static String extractHTTPredirectedURL(URLConnection conn){
    	if (ToolSafe.isEmpty(conn)){
    		getLogger().info("connection not opened yet!");
    		return null;
    	}
    	
		//extract the redirected URL;
		String szTemp = conn.getHeaderField("Location");
		if (!ToolSafe.isEmpty(szTemp)){
			StringTokenizer st = new StringTokenizer(szTemp);
			while (st.hasMoreTokens()){
			  	// pick the first url using http protocol
				szTemp = st.nextToken();
				szTemp = DataQname.extractNamespace(szTemp);
				if (!ToolSafe.isEmpty(szTemp)){
					getLogger().info("Redirected to " + szTemp);
					return szTemp;					
				}
			}
		}
		
    	
		
		return null;
   }
  
    /**
     *  save a inputstream into a string with appropriate content encoding
     * @param bis
     * @return
     * @throws IOException
     * @throws MyHttpException
     */
    /*
    private boolean saveStream(BufferedInputStream bis, DataTaskLoadHttp task) {
    	if (null==bis)
    		return false;

    	try{
        	
        	ToolEncodingDetector oED = new ToolEncodingDetector();
        	oED.extractHTTPcharset(task.m_conn);
        	oED.extractAPPcharset(bis);
            
        	// read ignored bytes
        	bis.reset();
        	task.m_ignoredBytes = new byte[oED.m_ignoreBytes];
        	bis.read(task.m_ignoredBytes);
        	
        	// read rest content
        	byte[] data;
			data = ToolIO.pipeInputStreamToBytes(bis);
        		
        	task.m_content_charset = oED.m_APPFcharset;
        	task.m_content = new String (data, task.m_content_charset);
        	oED.extractXMLcharset(task.m_content);
        	
        	// guess content charset
        	String finalCharset = oED.extractFINALcharset();
        	if (null==finalCharset){
        		getLogger().info("failed to determine charset");
        		task.m_content_charset = null;
        		task.m_content =null;
        		task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_DOWNLOAD_BAD_CHARSET);
        	}else if (!finalCharset.equals(task.m_content_charset)){
        		task.m_content_charset = finalCharset;
            	task.m_content = new String (data, task.m_content_charset);
        	}
        	
        	task.setState( DataTaskLoadHttp.STATE_OUTPUT_SUCCESS_DOWNLOAD);
        	return true;
    	} catch (IOException e) {
    		getLogger().info(e.getMessage());
    		task.m_content_charset = null;
    		task.m_content =null;
    		task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_DOWNLOAD_INTERRUPT);
    		return false;
        }catch (OutOfMemoryError e){
			task.m_content_charset = null;
			task.m_content =null;
			task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_DOWNLOAD_OUTOF_MEMORY);
			return false;        	
		} catch (SwutilException e) {
			task.m_content_charset = null;
			task.m_content =null;
			task.setState(DataTaskLoadHttp.STATE_OUTPUT_FAILED_DOWNLOAD_INTERRUPT);
			return false;
		}
   }
    
*/
    
    
	// cached robots.txt 
	private DataCachedObjectMap<String,DataRobotsTxt> m_mgr_host_robotstxt = new DataCachedObjectMap<String,DataRobotsTxt>();
	private boolean testDisallow(String szURL){
		if (szURL.endsWith("robots.txt"))
			return false;
		
		String szHost = null;
		try {
			URI uri = ToolURI.string2uri(szURL);
			URI uriHost = ToolURI.extractHostUrl(uri);
			if (null!=uriHost)
				szHost = uriHost.toString();
			
			DataRobotsTxt rt = m_mgr_host_robotstxt.get(szHost);
			if (null==rt){
				rt = loadRobotsTxt(uri.toURL(), getName() );
				m_mgr_host_robotstxt.put(szHost, rt);
			}
			
			if (null!=rt)
				return rt.testDisallow(uri);
			else
				return false;
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}	

	protected boolean testDoNotCrawl(String szURL){
		return false;
	}

	
	
	public static DataRobotsTxt loadRobotsTxt(String szURL, String robotName){
		URL url;
		try {
			url = new URL(szURL);
			return loadRobotsTxt(url,robotName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static DataRobotsTxt loadRobotsTxt(URL url, String robotName){
		try {
			URL urlhost = new URL(url.getProtocol(),url.getHost(),url.getPort(),"/robots.txt");
	
			DataRobotsTxt  rt = DataRobotsTxt.parse(ToolLoadHttp.wget(urlhost.toString()), robotName);
			return rt;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static DataTaskLoadHttp loadUrl(String szURL){
		return loadUrl(szURL, null, null);
	}	
	
	public static DataTaskLoadHttp loadUrl(String szURL, String szXmlBase, String szRdfSyntax){
		  DataTaskLoadHttp task = new DataTaskLoadHttp(szURL, szXmlBase);
		  ToolLoadHttp.getSwaBot().load(task);
		  task.m_szRdfSyntax = szRdfSyntax;
		  return task;
	}
	
	
}
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
 * tool for loading data
 * 
 * @author Li Ding
 * 
 */
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.web.AgentEncodingDetector;

public class ToolLoad {

	
	/**
	     *  save a inputstream into a string with appropriate content encoding
	     * @param is
	     * @return
	     * @throws IOException
	     * @throws MyHttpException
	     */
	    
	protected static boolean saveStream(InputStream is, HttpURLConnection conn, TaskLoad task) {
	    	if (null==is)
	    		return false;

	    	BufferedInputStream bis = new BufferedInputStream(is);
	
	    	try{
	        	AgentEncodingDetector oED = new AgentEncodingDetector();
	        	if (null!=conn)
	        		oED.extractHTTPcharset(conn);
	        	oED.extractAPPcharset(bis);
	            
	        	ByteArrayOutputStream out = new ByteArrayOutputStream();
	        	
	        	// read ignored bytes
	        	bis.reset();
	        	if (oED.m_ignoreBytes>0){
		        	task.m_ignoredBytes = new byte[oED.m_ignoreBytes];
		        	bis.read(task.m_ignoredBytes);
		        	out.write(task.m_ignoredBytes);
	        	}
	        	
	        	
	        	
	        	// read rest content
	        	byte[] data;
				data = ToolIO.pipeInputStreamToBytes(bis);
	        	out.write(data);
	        		
	        	task.m_bytes = out.toByteArray();
	        	task.m_content_charset = oED.m_APPFcharset;
	        	task.setContent ( new String (data, task.m_content_charset) );
	        	oED.extractXMLcharset(task.getContent());
	        	
	        	// guess content charset
	        	String finalCharset = oED.extractFINALcharset();
	        	if (null==finalCharset){
	        		task.m_content_charset = null;
	        		task.setContent ( null);
	        		task.setState(TaskLoad.STATE_OUTPUT_FAILED_DOWNLOAD_BAD_CHARSET, "cannot determine charset of the loaded content", null);
	        	}else if (!finalCharset.equals(task.m_content_charset)){
	        		task.m_content_charset = finalCharset;
	            	task.setContent (  new String (data, task.m_content_charset) );
	        	}
	        	
	        	task.setState( TaskLoad.STATE_OUTPUT_SUCCESS_DOWNLOAD, "ok", null);
	        	return true;
	    	} catch (IOException e) {
	    		task.m_content_charset = null;
	    		task.setContent ( null);
	    		task.setState(TaskLoad.STATE_OUTPUT_FAILED_DOWNLOAD_INTERRUPT, "the process of loading content is interrupted", e.getLocalizedMessage());
	    		return false;
	        }catch (OutOfMemoryError e){
				task.m_content_charset = null;
				task.setContent ( null);
				task.setState(TaskLoad.STATE_OUTPUT_FAILED_DOWNLOAD_OUTOF_MEMORY, "java program ran out of memory", e.getLocalizedMessage());
				return false;        	
			} catch (Sw4jException e) {
				task.m_content_charset = null;
				task.setContent ( null );
				task.setState(TaskLoad.STATE_OUTPUT_FAILED_DOWNLOAD_INTERRUPT, "the process of loading content is interrupted", e.getLocalizedMessage());
				return false;
			}
	}

}

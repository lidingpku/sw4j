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
 * loading data from file to memory
 * 
 * @author Li Ding
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import sw4j.util.ToolSafe;

public class ToolLoadFile extends ToolLoad{
	protected static Logger getLogger() {
		return Logger.getLogger(ToolLoadFile.class);
	}
	

	public static DataTaskLoadFile loadFile(String szURL, String szXmlBase, String szRdfSyntax){
		DataTaskLoadFile task = new DataTaskLoadFile(szURL,szXmlBase);
		task.m_szRdfSyntax = szRdfSyntax;

		if (ToolSafe.isEmpty(szURL)){
			task.setState(TaskLoad.STATE_OUTPUT_INVALID_EMPTY_URL, "empty filename or URL", null);
			return task;
		}

		
		// validate task 
		if (!task.validateTask()){
			return task;
		}
				
		File f = task.getFile();
		if (null==f){
			task.setState(TaskLoad.STATE_OUTPUT_FAILED_CONNECTION_CANNOT_OPEN, "file does not exist or cannot open file", null);
			return task;
		}
		// assert url != null

		getLogger().info("loading file: "+ f.getAbsolutePath());
		try {
			task.m_lastmodified = f.lastModified();

			InputStream in = new FileInputStream(f);
			if (szURL.endsWith(".gz")){
				in = new GZIPInputStream(in);
			}else if (szURL.endsWith(".zip")){
				in = new ZipInputStream(in);
			}
			
			
			saveStream(in, null, task);
			
			
		} catch (FileNotFoundException e) {
			task.setState(TaskLoad.STATE_OUTPUT_FAILED_CONNECTION_CANNOT_OPEN, "cannot open file", e.getLocalizedMessage());
		} catch (IOException e) {
			task.setState(TaskLoad.STATE_OUTPUT_FAILED_CONNECTION_CANNOT_OPEN, "failed to convert to gzip", e.getLocalizedMessage());
		}
		return task;
	}
}

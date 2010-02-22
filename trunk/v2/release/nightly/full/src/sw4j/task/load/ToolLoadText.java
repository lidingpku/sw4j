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
 * This file load strings
 *  @author: Li Ding
 */
import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;

import sw4j.util.ToolSafe;

public class ToolLoadText extends ToolLoad{
	protected static Logger getLogger() {
		return Logger.getLogger(ToolLoadText.class);
	}
	

	public static DataTaskLoadText loadText(String szText, String szXmlBase, String szRdfSyntax){
		DataTaskLoadText task = new DataTaskLoadText(szXmlBase);

		if (ToolSafe.isEmpty(szText)){
			task.setState(TaskLoad.STATE_OUTPUT_INVALID_EMPTY_TEXT, "empty text", null);
			return task;
		}
		
		//task.m_lastmodified = 0;
		saveStream(new ByteArrayInputStream(szText.getBytes()), null, task);
		
		if (!ToolSafe.isEmpty(szRdfSyntax)){
			task.m_szRdfSyntax = szRdfSyntax;
		}
		return task;
	}
}

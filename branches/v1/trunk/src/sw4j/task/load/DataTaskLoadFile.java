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

import sw4j.util.ToolSafe;
import sw4j.util.ToolURI;

public class DataTaskLoadFile  extends TaskLoad{

	protected DataTaskLoadFile(String szURL, String szXmlBase) {
		super(szURL, szXmlBase);
	}

	@Override
	public boolean isHttpLoader() {
		return false;
	}

	public File getFile(){
		if ( !ToolSafe.isEmpty(m_szURL)){
			String szTemp= this.getRawUrl();
			String szPrefix1 = "file:/";
			String szPrefix2= "file://";
			String szPrefix3 = "file:///";
			if (szTemp.startsWith(szPrefix1)){
				szTemp = szTemp.substring(szPrefix1.length());
			}else if (szTemp.startsWith(szPrefix2)){
				szTemp = szTemp.substring(szPrefix2.length());
			}else if (szTemp.startsWith(szPrefix3)){
				szTemp = szTemp.substring(szPrefix3.length());
			}else if (ToolURI.isUriHttp(szTemp)){
				return null;
			}
			
			File f = new File(szTemp);
			if (f.exists())
				return f;
		}
		return null;
	}
	
	@Override
	protected boolean validateTask() {

		return true;
	}

}

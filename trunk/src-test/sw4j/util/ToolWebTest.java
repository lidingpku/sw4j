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
package sw4j.util;

import org.junit.Test;

import sw4j.util.ToolWeb;

/**
 * 
 * @author Li Ding
*/

public class ToolWebTest {

	
	@Test
	public void test_remove_markup() {
	  String [] aryURL = new String[]{
		  "http://purl.org/dc/elements/1.1/",
	  };

	  for (int i=0; i<aryURL.length; i++){
		  String szURL = aryURL[i];
		  /*
		  DataTaskLoadHttp task = ToolLoadHttp.loadUrl(szURL);
		  if (null==task){
			  System.out.println(szURL);
			  continue;
		  }
		  
		  if (!task.isLoadSucceed()){
			  task.print();
			  continue;
		  }
		  
		  if (ToolSafe.isEmpty(task.getContent()))
			  continue;
		  */
		  String content;
		try {
			content = ToolIO.pipeUrlToString(szURL);

			//System.out.println(ToolWeb.removeHtmlComment(task.getContent()));
			  System.out.println(ToolWeb.removeMarkup(content,"<rdf:RDF","</rdf:RDF>"));
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  
	  }
	}
}

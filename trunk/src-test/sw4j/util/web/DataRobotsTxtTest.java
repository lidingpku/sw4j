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
package sw4j.util.web;

import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.net.URI;

import org.junit.Test;

import sw4j.task.load.ToolLoadHttp;
import sw4j.util.web.DataRobotsTxt;

/**
 * 
 * @author Li Ding
*/

public class DataRobotsTxtTest {
	@Test
	public void test() {
		String szURL = "http://swoogle.umbc.edu/service/";
		szURL = "http://www.google.com/robots.txt";
		szURL ="http://en.wikipedia.org/wiki/Robots.txt";
		
		try{

			ToolLoadHttp crawler = ToolLoadHttp.getSwaBot();
			DataRobotsTxt  rt = ToolLoadHttp.loadRobotsTxt(szURL,crawler.getName());
			if (null!= rt){
				System.out.println("============ Related Path Cmd ===========");
				PrintWriter pw = new PrintWriter(System.out);
				rt.print(pw);
				pw.flush();
				System.out.println("============ TEST ===========");
				
				System.out.println("test url:"+szURL+"  disallow?"+rt.testDisallow(new URI(szURL)));

				System.out.println("TEST PASS");
				return;
			}else{
				fail();
			}
		}catch (Exception e){
			e.printStackTrace();
			fail();
		}
		
	}
}

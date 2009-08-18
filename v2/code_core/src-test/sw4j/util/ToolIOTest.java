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

import static org.junit.Assert.fail;

import org.junit.Test;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;

/**
 * 
 * @author Li Ding
*/

public class ToolIOTest {
	@Test
	public void testParse_validate_URI() {
		System.out.println("++++++++++ Testing ToolIOTest ++++++++++++");
		
		String [] aryURI = new String []{
			"http://www.cs.rpi.edu/~dingl/foaf.rdf",
		};

		for (int i=0; i<aryURI.length; i++){
			try {
				String szURI = aryURI[i];
				String szText = ToolIO.pipeUrlToString(szURI);
				String szNumberText = ToolIO.pipeStringToLineNumberedString(szText);
				System.out.println(szNumberText);

				DataSmartMap ds = new DataSmartMap();
				ToolIO.pipeStringToSmartMap(szText, ds);
				System.out.println((ds));
			} catch (Sw4jException e) {
				
				System.out.println(e.getMessage());
				fail();
			}
		}
	}
}

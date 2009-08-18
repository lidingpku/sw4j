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
import sw4j.util.Sw4jException;

/**
 * 
 * @author Li Ding
*/

public class DataQnameTest {


	@Test
	public void test_create_by_uri() {
		System.out.println("++++++++++ test_create_by_uri ++++++++++++");
		String [] aryURI = new String []{
				"http://foo.com/foo/", // 
				"http://foo.com/foo#", // 
				"http://foo.com/foo", // 
				"http://foo.com/rdf#Class", // 
				"http://foo.com/rdf#Time_stame", // 
				"http://foo.com/a/%7Eda.ioow",
				"http://foo.com/rdf#foo.aspn%20me", // 
			};
			DataQname.debug=true;
			
			for (int i=0; i<aryURI.length; i++){
				String szURI = aryURI[i];
				try {
					DataQname.create(szURI,null);
				} catch (Sw4jException e) {
					e.printStackTrace();
					System.out.println(aryURI[i]);
					fail("show be correct");
				}
			}
			
			aryURI = new String []{
					"",		// test empty URI
					"http://foo.com/rdf#foo.php?adc",		//
					"http://foo.com/rdf#foo.asp?name=k", // 
					"http://foo.com/rdf#foo.aspna me", // 
				};

				for (int i=0; i<aryURI.length; i++){
					String szURI = aryURI[i];
					try {
						DataQname.create(szURI,null);
						System.out.println(aryURI[i]);
						fail("should report exception");
					} catch (Sw4jException e) {
						e.printStackTrace();
					}
				}
	}

	@Test
	public void test_create_by_ns_ln() {
		System.out.println("++++++++++ test_create_by_ns_ln ++++++++++++");
		String [][] aryURI = new String [][]{
				{"http://foo.com/foo/", "abc"}, // 
				{"http://foo.com/foo#", "abc"},// 
				{"http://foo.com/foo#", "Class"},// 
				{"http://foo.com/foo#", "Time_stame"},// 
				{"http://foo.com/foo#", ""},// 
			};
			DataQname.debug=true;
			
			for (int i=0; i<aryURI.length; i++){
				try {
					DataQname.create(aryURI[i][0],aryURI[i][1]);
				} catch (Sw4jException e) {
					e.printStackTrace();
					System.out.println(aryURI[i][0]);
					System.out.println(aryURI[i][1]);
					fail("show be correct");
				}
			}
			
			aryURI = new String [][]{
					{"", "abc"},// 
					{"http://foo.com/foo", "abc"},// 
					{"http://foo.com/foo#", "foo.php?adc"},// 
					{"http://foo.com/foo#", "foo.asp?name=k"},// 
					{"http://foo.com/foo#", "foo.asp?na  me=k"},// 
				};

				for (int i=0; i<aryURI.length; i++){
					try {
						DataQname.create(aryURI[i][0],aryURI[i][1]);
						System.out.println(aryURI[i][0]);
						System.out.println(aryURI[i][1]);
						fail("should report exception");
					} catch (Sw4jException e) {
						e.printStackTrace();
					}
					
				}
	}	

	@Test
	public void test_ExtractCanonicalNamespace() {
		System.out.println("++++++++++ Testing test_ExtractCanonicalNamespace ++++++++++++");
		String [][] aryURIURI = new String[][]{
				{"http://foo.com/a#dasfioe.ae.ioow","http://foo.com/a#",},
				{"http://foo.com/a/da.ioow","http://foo.com/a/da.ioow",},
				{"http://foo.com/a/%7Eda.ioow","http://foo.com/a/~da.ioow",},
		};
		for (int i=0; i<aryURIURI.length; i++){
			String temp;
				DataQname dq;
				try {
					dq = DataQname.create(aryURIURI[i][0],null);

					temp = dq.getNamespaceCanonical();
					
					if (!temp.equals(aryURIURI[i][1])){
						System.out.println(aryURIURI[i][0]);
						System.out.println(aryURIURI[i][1]);
						System.out.println(temp);
						fail("Failed URI");
					}
				} catch (Sw4jException e) {
					e.printStackTrace();
					System.out.println(aryURIURI[i][0]);
					fail("should be correct");
				}

		}
	}
	
	@Test
	public void test_extractNamespaceUrl() {
		System.out.println("++++++++++ Testing test_extractNamespaceUrl ++++++++++++");
		String [][] aryURIURI = new String[][]{
				{"http://foo.com/a#dasfioe.ae.ioow","http://foo.com/a",},
				{"http://foo.com/a/da.ioow","http://foo.com/a/da.ioow",},
				{"http://foo.com/a/%7Eda.ioow","http://foo.com/a/~da.ioow",},
		};
		DataQname.debug =true;
		
		for (int i=0; i<aryURIURI.length; i++){
			String temp;
			temp  = DataQname.extractNamespaceUrl(aryURIURI[i][0]);
			if (null==temp){
				System.out.println(aryURIURI[i][0]);
				fail("should be correct");
				
			}

			if (!temp.equals(aryURIURI[i][1])){
				System.out.println(aryURIURI[i][0]);
				System.out.println(aryURIURI[i][1]);
				System.out.println(temp);
				fail("Failed URI");
			}

		}
	}
}

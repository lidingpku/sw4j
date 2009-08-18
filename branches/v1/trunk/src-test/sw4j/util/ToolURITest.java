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

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import sw4j.util.Sw4jException;

/**
 * 
 * @author Li Ding
*/

public class ToolURITest {

	/**
	 * Test method for {@link sw4j.util.ToolURI#parse_validate_URI(java.lang.String)}.
	 */
	@Test
	public void testParse_validate_URI() {
		System.out.println("++++++++++ Testing testParse_validate_URI ++++++++++++");
		
		String [] aryURI = new String []{
			"",		// test empty URI
			"http://foo.com/bar/foo/bar/foo/bar/foo/bar/", // crawler trap
			"http:///bar/", // empty domain/authority
			":///bar/", // empty scheme
		};

		for (int i=0; i<aryURI.length; i++){
			try {
				String szURI = aryURI[i];
				ToolURI.validateUri(szURI);
				System.out.println(aryURI[i]);
				fail("should report exception");
			} catch (Sw4jException e) {
				
				System.out.println(e.getMessage());
			}
		}
	}


	/**
	 * Test method for {@link sw4j.util.ToolURI#validateUri_crawlerTrap(java.lang.String)}.
	 */
	@Test
	public void testCheck_crawler_trap() {
		System.out.println("++++++++++ Testing testCheck_crawler_trap ++++++++++++");
		String [] aryURI = new String []{
				"http://iw.stanford.edu", // 
			};

			for (int i=0; i<aryURI.length; i++){
				try {
					String szURI = aryURI[i];
					ToolURI.validateUri_crawlerTrap(szURI);
				} catch (Sw4jException e) {
					
					System.out.println(e.getMessage());
					System.out.println(aryURI[i]);
					fail("should be correct");
				}
			}
			
			aryURI = new String []{
					"",		// test empty URI
					"http://foo.com/bar/foo/bar/foo/bar/foo/bar/",		//
					"http://foo.com/bar/../case/../bar/", // 
				};

				for (int i=0; i<aryURI.length; i++){
					try {
						String szURI = aryURI[i];
						ToolURI.validateUri_crawlerTrap(szURI);
						System.out.println(aryURI[i]);
						fail("should report exception");
					} catch (Sw4jException e) {
						
						System.out.println(e.getMessage());
					}
				}
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#isUriHttp(java.lang.String)}.
	 */
	@Test
	public void testIs_HTTP_URIString() {
		System.out.println("++++++++++ Testing testIs_HTTP_URIString ++++++++++++");
		// good ones
		System.out.println("+++++++testing good URIs++++++++++++");
		String [] szURIs = new String []{
				"http://www.mygrid.org.uk/ontology#Nomenclature+Committee+of+the+International+Union+of+Biochemistry+and+Molecular+",

				"http://blog.glennf.com/", 

				"http://yoda.zoy.org/photos/2004/02-Kamakura/05-Yuigahama_-_Sable.rdf",	// a file
				"http://ilrt.org/discovery/2001/06/schemas/ical-full/hybrid.rdf",
				"http://dev.w3.org/cvsweb/2000/10/swap/Attic/logic.n3?rev=1.2",
				"http://sw.deri.org/~aharth/2004/11/rdfquery-perf/univ20/University18_2.nt",
				"http://high_g.ciao.jp/blog/index.rdf",	// ? cannot have _ in host name according to RFC
				"http://aa.d/%7edaf%3baads",


				"http://www.w3.org/1999/02/22-rdf-syntax-ns#_1",
				"http://purl.org/dc/elements/1.1/creator.name",
				"http://sa.d?http://dfa.d#dafse",
				"http://sa.d?http://dfa.d",
				"http://www.iis.uni-stuttgart.de/lehre/ss04/xml/Jena-2.1/testing/N3/rdf-test-05.n3a",
				
				
				"http://www.cs.umd.edu/projects/plus/DAML/onts/univ1.0.daml#/",
				"https://partner.pricegrabber.com/pricegrabber.dtdproduct_image",
				
				"http://opencyc.sourceforge.net/daml/cyc.daml#/AcademicProfessional",
				
				"HTTp://aa.bb",	// no namespace
				"http://aa.bb",
				
				"http://127.0.0.1:8080/ontologies/MPEG703/FileFormatCS#DescriptionMetadataType",


				"http://aa.d/%7edaf%3baads",
				"http://sa.d?http://dfa.d#dafse",
				"http://sa.d?http://dfa.d",
				"http://dev.w3.org/cvsweb/2000/10/swap/Attic/logic.n3?rev=1.2",
				"http://sw.deri.org/~aharth/2004/11/rdfquery-perf/univ20/University18_2.nt",
				"http://www.cs.umd.edu/projects/plus/DAML/onts/univ1.0.daml#/",

		};
		for (int i=0; i<szURIs.length; i++){
			if (!ToolURI.isUriHttp(szURIs[i])){
				System.out.println(szURIs[i]);
				fail("Expect a good URI");
			}
		}	

		System.out.println("+++++++testing bad URIs++++++++++++");

		// bad ones
		szURIs = new String []{
				// other schema
				"voc://nokia.com/URI-Taxonomy-1.0/",
				"voc://-/0da5edc7-3265-11d6-8917-0003931df47chasEmailAddress",// voc uuid 
				"foo:not-assigned-yetday",  
				"rdf:Recipient",
				"urn:lsid:uniprot.org:enzymes:1.13.11.24",
				"xyz://da.bb/asdf",
				"mid:2000-07-12-meeting-stuff@w3.orgdate",
				"file:///Applications/Protege_2.1_beta/examples/atom/atom.owl#Content",	// uri without host
				"file:publication.o.daml#endDate",
				"urn://govshare.info/rdf/census/waterArea",
				"irc://localhost/foo#SHA",
				"mailto://da.bb/asdf",
				"mailto://da.bb/asdf",
				"file:H:\\WBKR\\WBKR.rdfs#Voorkennis",
			
				// invalid host
				// bad namespace
				"http:///aa",
				//bad schemespecificpart
				"http:/",
				"http://",
				"http:///",
				"http",
				"http:",

				//	no local name
				"http://xsdtestingf1",				
				
				"http:/www.purl.org/stuff/rev#Review",
				
				"http://test/owl_ontology/number_of_species",
				"http://:980/sadf",
				"http://daf:8080/aa",				

				// bad uri
				"http://infomesh.net/n3/2001-06-bookmarks.n3#bookmarks#bookmarkedBy",  //with two # in URI
				"http:///infomesh.net/n3/2001-06-bookmarks.n3#bookmarks#bookmarkedBy",  //with two # in URI
				"http://da.bb/asdf\"asdfas",	// containing " in URI
				
		};
		for (int i=0; i<szURIs.length; i++){
			if (ToolURI.isUriHttp(szURIs[i])&&ToolURI.isUriValid(szURIs[i])){
				System.out.println(szURIs[i]);
				fail("Expect a bad URI");
			}
		}			
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#validateUri_http(java.net.URI)}.
	 */
	@Test
	public void testIs_HTTP_URIURI() {
		// this method has been tested
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#getHostURL(java.lang.String)}.
	 */
	@Test
	public void testGetHostURL() {
		System.out.println("++++++++++ Testing testGetHostURL ++++++++++++");
		String [][] aryURIURI = new String[][]{
				{"http://en.wikipedia.org/wiki/Spider_trap", "http://en.wikipedia.org/"},
		};
		for (int i=0; i<aryURIURI.length; i++){
			String temp;
			try {
				URI uri = ToolURI.string2uri(aryURIURI[i][0]);
				URI hostURI = ToolURI.extractHostUrl(uri);
				if (null==hostURI){
					System.out.println(uri);
					fail("Host URL extraction failed");
				}
					
				temp = hostURI.toString();
				if (!aryURIURI[i][1].equals(temp)){
					System.out.println(aryURIURI[i][1]);
					System.out.println(temp);
					fail("Host URL extraction failed");
				}	
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#url2ip(java.lang.String)}.
	 */
	@Test
	public void testUrl2ipString() {
		System.out.println("++++++++++ Testing testUrl2ipString ++++++++++++");
		{
			String [][] aryURIURI = new String[][]{
					{"http://mit.edu/", "18.7.22.69"},
					{"mailto:joe@foo.com", "127.0.0.1"},
			};
			for (int i=0; i<aryURIURI.length; i++){
				String temp;
				try {
					temp = ToolURI.url2ip(aryURIURI[i][0]);
					if (null==temp && null==aryURIURI[i][1])
						continue;
	
					if (!temp.equals(aryURIURI[i][1])){
						System.out.println(aryURIURI[i][1]);
						System.out.println(temp);
						fail("failed"+aryURIURI[i][0]);
					}	
				} catch (Sw4jException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fail();
				}
			}	
		}
		{
			String [][] aryURIURI = new String[][]{
					{"", null},
			};
			for (int i=0; i<aryURIURI.length; i++){
				try {
					ToolURI.url2ip(aryURIURI[i][0]);
	
					fail();
				} catch (Sw4jException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#url2ip(java.net.URL)}.
	 */
	@Test
	public void testUrl2ipURL() {
		/// tested
	}

	String [][] TEST_URI_ENCODING = new String[][]{
			{"http://mit.edu/", "http%3A%2F%2Fmit.edu%2F"},
			{"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ060+1/Metis---2.1/answer.owl#ns_prove_this", "http%3A%2F%2Finference-web.org%2Fproofs%2Ftptp%2FSolutions%2FPUZ%2FPUZ060%2B1%2FMetis---2.1%2Fanswer.owl%23ns_prove_this"},
	};
	
	/**
	 * Test method for {@link sw4j.util.ToolURI#encodeURIString(java.lang.String)}.
	 */
	@Test
	public void testEncodeURIStringString() {
		System.out.println("++++++++++ Testing testEncodeURIStringString ++++++++++++");
		for (int i=0; i<TEST_URI_ENCODING.length; i++){
			String temp;
			try {
				temp = ToolURI.encodeURIString(TEST_URI_ENCODING[i][0]);
				System.out.println(temp);
				if (!TEST_URI_ENCODING[i][1].equals(temp)){
					System.out.println(TEST_URI_ENCODING[i][1]);
					fail("failed");
				}	
			} catch (Sw4jException e) {
				System.out.println(e.getMessage());
				fail("failed");
			}
		}			
		//
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#decodeURIString(java.lang.String)}.
	 */
	@Test
	public void testDecodeURIStringString() {
		System.out.println("++++++++++ Testing testDecodeURIStringString ++++++++++++");

		for (int i=0; i<TEST_URI_ENCODING.length; i++){
			String temp;
			try {
				temp = ToolURI.decodeURIString(TEST_URI_ENCODING[i][1]);
				System.out.println(temp);
				if (!TEST_URI_ENCODING[i][0].equals(temp)){
					System.out.println(TEST_URI_ENCODING[i][0]);
					System.out.println(temp);
					fail("failed");
				}	
			} catch (Sw4jException e) {
				System.out.println(e.getMessage());
				fail("failed");
			}
		}			
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#decodeURIString(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDecodeURIStringStringString() {
		//tested
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#isUriEqual(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testEqualURI() {
		System.out.println("++++++++++ Testing testEqualURI ++++++++++++");
		System.out.println("++++++++++ Equal URI ++++++++++++");
		String [][] aryURIURI = new String[][]{
				{"http://foo.com/a#£€","http://foo.com/a#%C2%A3%E2%82%AC",},
				{"http://foo.com/aa#foo%7E", "http://foo.com/aa#foo~",},
				{"http://everything2.com/index.pl?node=%55%52%4C%65%73%63%61%70%65%73%65%71%75%65%6E%63%65%73","http://everything2.com/index.pl?node=URLescapesequences"},
		};
		for (int i=0; i<aryURIURI.length; i++){

			// regular URI does not look the same
			try {
				if (new URI(aryURIURI[i][0]).equals(new URI(aryURIURI[i][1])))
					fail();
			} catch (URISyntaxException e) {
				e.printStackTrace();
				fail();
			}
			
			// canonicalized URI are the same
			if (!ToolURI.isUriEqual(aryURIURI[i][0], aryURIURI[i][1])){
				System.out.println(aryURIURI[i][0]);
				System.out.println(aryURIURI[i][1]);
				try {
					System.out.println(ToolURI.string2uri(aryURIURI[i][0]));
					System.out.println(ToolURI.string2uri(aryURIURI[i][1]));
				} catch (Sw4jException e) {
					e.printStackTrace();
				}
				fail("Expect equal URI");
			}
		}
		
		
		System.out.println("++++++++++ NON-Equal URI ++++++++++++");
		aryURIURI = new String[][]{
				{"http://foo.com/a#b","http://foo.com/a#c",},
		};
		for (int i=0; i<aryURIURI.length; i++){
			if (ToolURI.isUriEqual(aryURIURI[i][0], aryURIURI[i][1])){
				System.out.println(aryURIURI[i][0]);
				System.out.println(aryURIURI[i][1]);
				fail("Expect non-equal URI");
			}
		}	
	}
	
	public static final String [][] URISTRING_HTML_ENCODING = new String [][]{
		{"http%3A%2F%2Ffoo.com%2Fa%23","http://foo.com/a#",},
		{"http%3A%2F%2Ffoo.com%2Faa%23foo%22", "http://foo.com/aa#foo\"",},
		{"http%3A%2F%2Ffoo.com%2Faa%23foo%2B", "http://foo.com/aa#foo+",},
	};
	
	/**
	 * Test method for {@link sw4j.util.ToolURI#do_HTTP_URL_encoding(java.lang.String)}.
	 */
	@Test
	public void testDo_HTML_URL_encoding() {
		System.out.println("++++++++++ Testing testDo_HTML_URL_encoding ++++++++++++");
		for (int i=0; i<URISTRING_HTML_ENCODING.length; i++){
			String input = URISTRING_HTML_ENCODING[i][1];
			String temp;
			try {
				temp = ToolURI.encodeURIString(input);
				String expected = URISTRING_HTML_ENCODING[i][0];
				if (!expected.equals(temp)){
					System.out.println(input);
					System.out.println(temp);
					System.out.println(expected);
					fail("Encoding failed");
				}
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test method for {@link sw4j.util.ToolURI#do_HTTP_URL_decoding(java.lang.String)}.
	 */
	@Test
	public void testDo_HTML_URL_decoding() {
		System.out.println("++++++++++ Testing testDo_HTML_URL_decoding ++++++++++++");
		for (int i=0; i<URISTRING_HTML_ENCODING.length; i++){
			String input = URISTRING_HTML_ENCODING[i][0];
			String temp;
			try {
				temp = ToolURI.decodeURIString(input);
				String expected = URISTRING_HTML_ENCODING[i][1];
				if (!expected.equals(temp)){
					System.out.println(input);
					System.out.println(temp);
					System.out.println(expected);
					fail("Encoding failed");
				}
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}



}

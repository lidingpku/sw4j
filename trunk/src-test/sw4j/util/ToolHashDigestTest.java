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

import sw4j.util.ToolHashDigest;

/**
 * 
 * @author Li Ding
*/

public class ToolHashDigestTest {
	@Test
	public void test() {
		String [][] aryTest = new String[][]{
				{"mailto:dingl@cs.rpi.edu", "a7d0967c21ba8952ab0443bf822750d4c44afe2c","6189a98315ae6284599e7b9b8dc7aac9"},
				{"Since its inception the World Wide Web has changed the ways people work, play, communicate, collaborate, and educate. There is, however, a growing realization among researchers across a number of disciplines that without new research aimed at understanding the current, evolving and potential Web, we may be missing or delaying opportunities for new and revolutionary capabilities. If we want to be able to model the Web, if we want to understand the architectural principles that have provided for its growth, and if we want to be sure that it supports the basic social values of trustworthiness, personal control over information, and respect for social boundaries, then we must pursue a research agenda that targets the Web and its use as a primary focus of attention.", "8bf38afea61d0c394d4d4146b718b65ebc14a53c","fbb35d6834147ff727db42f9e6c667f0"},
		};
		for (int i=0; i< aryTest.length; i++){
			String szTemp = ToolHashDigest.hash_sum_sha1(aryTest[i][0].getBytes());
			if (!aryTest[i][1].equals(szTemp))
				fail();
		}
		
		for (int i=0; i< aryTest.length; i++){
			String szTemp = ToolHashDigest.hash_sum_md5(aryTest[i][0].getBytes());
			if (!aryTest[i][2].equals(szTemp))
				fail();
		}
	}
	
	@Test
	public void test_email() {
		String [][] aryTest = new String[][]{
				{"mailto:dingl@cs.rpi.edu", "a7d0967c21ba8952ab0443bf822750d4c44afe2c"},
				{"dingl@cs.rpi.edu", "a7d0967c21ba8952ab0443bf822750d4c44afe2c"},
				{"MailTo:dingl@cs.rpi.edu", "a7d0967c21ba8952ab0443bf822750d4c44afe2c"},
		};
		for (int i=0; i< aryTest.length; i++){
			String szTemp = ToolHashDigest.hash_mbox_sum_sha1(aryTest[i][0]);
			if (!aryTest[i][1].equals(szTemp))
				fail();
		}
		
	}
}

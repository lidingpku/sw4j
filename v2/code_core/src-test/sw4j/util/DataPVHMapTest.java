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


import sw4j.util.DataPVHMap;
import static org.junit.Assert.fail;

/**
 * 
 * @author Li Ding
*/

public class DataPVHMapTest {
	@Test
	public void test_unique() {
		System.out.println("++++++++++ Testing testParse_validate_URI ++++++++++++");
		DataPVHMap<String, Object> dml = new DataPVHMap<String, Object>();
		dml.add("name","TW");
		dml.add("name","TWC");
		dml.add("name","TW");
		dml.add("year", new Integer("1999"));

		System.out.println("++++++++++ field name ++++++++++++");
		System.out.println (dml);
		System.out.println (dml.keySet());
		
		
		System.out.println("++++++++++ test: values vs. valueset  ++++++++++++");
		System.out.println (dml.getValuesAsSet("name"));
		System.out.println (dml.getValues("name"));

		if (dml.getValuesAsSet("name").size()!=dml.getValues("name").size()){
			fail();
		}
		System.out.println("ok");
		
		{
			System.out.println("++++++++++ test: same  ++++++++++++");
			DataPVHMap<String, Object> dml1 = new DataPVHMap<String, Object>();
			dml1.add("name","TW");
			dml1.add("name","TWC");
			dml1.add("year", new Integer("1999"));

			if (!dml.equals(dml1)){
				fail();
			}

			System.out.println("ok");
		}

		{
			System.out.println("++++++++++ test: diff  ++++++++++++");
			DataPVHMap<String, Object> dml1 = new DataPVHMap<String, Object>();
			dml1.add("name","TW");
			dml1.add("name","TWC");
			dml1.add("year", new Integer("1999"));

			if (!dml.equals(dml1)){
				fail();
			}

			System.out.println("ok");
		}


	}
	
}

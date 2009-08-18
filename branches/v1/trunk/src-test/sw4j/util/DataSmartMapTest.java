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

import java.util.HashSet;
import org.junit.Test;
import sw4j.util.DataSmartMap;

/**
 * 
 * @author Li Ding
*/

public class DataSmartMapTest {
	@Test
	public void test_load() {
		DataSmartMap dsm = new DataSmartMap();

		System.out.println("++++++++++ test: add property values ++++++++++++");
		dsm.put("id", 11);
		dsm.put("gid", 11);
		dsm.put("gid", new Integer(293));
		dsm.put("name", "Li Ding");
		dsm.put("description", "Li Ding's information");
		dsm.put("male", true);

		System.out.println("++++++++++ test: set string-valued properties ++++++++++++");
		dsm.addStringProperty("name");
		dsm.addStringProperty("title");
		dsm.addStringProperty("description");
		
		
		System.out.println("++++++++++ field name ++++++++++++");
		System.out.println (dsm.getAllFieldName());
		System.out.println (dsm.getStringProperties());

		System.out.println("++++++++++ toString ++++++++++++");
		System.out.println (dsm.toString());

		System.out.println("++++++++++ toXml ++++++++++++");
		System.out.println (dsm.toXml());

		System.out.println("++++++++++ toHtml ++++++++++++");
		System.out.println (dsm.toHTMLtablerowheader());
		System.out.println (dsm.toHTMLtablerow());

		System.out.println("++++++++++ toCSV ++++++++++++");
		System.out.println (dsm.toCSVrow());

		System.out.println("++++++++++ toProperty ++++++++++++");
		System.out.println (dsm.toProperty());

		System.out.println("++++++++++ toSQL Insert ++++++++++++");
		System.out.println ("INSERT INTO mytable ");
		System.out.println (dsm.getSQLInsert());

		System.out.println("++++++++++ toSQL Update ++++++++++++");
		System.out.println ("UPDATE mytable SET ");
		System.out.println (dsm.getSQLUpdate());
		System.out.println ("WHERE ");
		System.out.println (dsm.getSQLWhere("id"));

		System.out.println("++++++++++ toSQL Where (>1) ++++++++++++");
		HashSet <String> condition = new HashSet<String>();
		condition.add("id");
		condition.add("name");
		System.out.println (dsm.getSQLWhere(condition));
	}
}

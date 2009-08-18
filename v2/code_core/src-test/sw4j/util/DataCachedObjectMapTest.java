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
import sw4j.util.DataCachedObjectMap;

/**
 * 
 * @author Li Ding
*/

public class DataCachedObjectMapTest {

	@Test
	public void test_LRU() {
		System.out.println("+++++++++++++++++++++++test LRU+++++++++++++++++++++++");

		DataCachedObjectMap.debug = true;
		DataCachedObjectMap<String,String> cache = new DataCachedObjectMap<String,String>(5);
		cache.put("1", "hello");
		cache.put("2", "hello");
		cache.put("3", "hello");
		cache.put("4", "hello");
		cache.put("5", "hello");

		String temp;
		
		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("{1=hello, 2=hello, 3=hello, 4=hello, 5=hello}"))
			fail();

		cache.put("6", "hello");

		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("{2=hello, 3=hello, 4=hello, 5=hello, 6=hello}"))
			fail();

		cache.put("2", "hello");

		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("{3=hello, 4=hello, 5=hello, 6=hello, 2=hello}"))
			fail();

		cache.put("7", "hello");

		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("{4=hello, 5=hello, 6=hello, 2=hello, 7=hello}"))
			fail();
	}
	
	@Test
	public void test_scale() {
		System.out.println("+++++++++++++++++++++++test scale+++++++++++++++++++++++");
		DataCachedObjectMap.debug = false;

		DataCachedObjectMap<String,String> cache = new DataCachedObjectMap<String,String>(2);
		long mem0 =Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println(mem0);
		for (int i=0; i<10; i++){
			int randomvalue = (int)(Math.random()* 5 +1);
			String key = ""+randomvalue;

			long mem1 =Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.gc();
			System.gc();
			System.gc();
			System.gc();

			cache.put(key, new String (new char[100000* randomvalue]));

			System.gc();
			System.gc();
			System.gc();
			System.gc();
			long mem =Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

			System.out.print(randomvalue);
			System.out.print("\t");
			System.out.print(mem-mem0);
			System.out.print("\t");
			System.out.print(mem-mem1);
			System.out.print("\t");
			System.out.println(cache.keySet());
		}

		System.out.println(cache.keySet());
	}
	

}

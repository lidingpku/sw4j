package sw4j.util;

import static org.junit.Assert.fail;

import org.junit.Test;

public class DataLRUCacheTest {
	@Test
	public void test_LRU() {
		System.out.println("+++++++++++++++++++++++test LRU+++++++++++++++++++++++");

		DataLRUCache<String> cache = new DataLRUCache<String>(5);
		cache.add("1");
		cache.add("2");
		cache.add("3");
		cache.add("4");
		cache.add("5");

		String temp;
		
		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("[1, 2, 3, 4, 5]"))
			fail();

		cache.add("6");

		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("[2, 3, 4, 5, 6]"))
			fail();

		cache.add("2");

		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("[3, 4, 5, 6, 2]"))
			fail();

		cache.add("7");

		temp = cache.toString();
		System.out.println(temp);
		if (!temp.equals("[4, 5, 6, 2, 7]"))
			fail();
	}
}

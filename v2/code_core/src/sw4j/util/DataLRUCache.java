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

import java.util.LinkedList;

/**
 * A LRU map. Users can specify the max-entry and the expiration time for entry.
 * example:
 *    (p1,v1)
 *    (p2,v2)
 *    (p3,v3)
 * 
 * @author Li Ding
 * 
 */
public class DataLRUCache< V> extends LinkedList<V> {
	
	////////////////////////////////////////////////
	// hidden data
	////////////////////////////////////////////////
	
	private static final long serialVersionUID = 1L;

	protected static boolean debug = false;

	////////////////////////////////////////////////
	// constant
	////////////////////////////////////////////////
	/**
	 * default max entries. the max size of the cache
	 */
	public static final int DEFAULT_MAX_ENTRIES = 100;



	////////////////////////////////////////////////
	// internal data
	////////////////////////////////////////////////
	
	private int m_nLimit = DEFAULT_MAX_ENTRIES;


	////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////

	public DataLRUCache() {
	}

	public DataLRUCache(int limit) {
		this.m_nLimit = limit;
	}

	
	
	////////////////////////////////////////////////
	// functions
	////////////////////////////////////////////////

	@Override
	public boolean add(V value) {
		//keep the value unique
		boolean ret = this.remove(value);
		super.addLast(value);
		
		if (this.size()>this.m_nLimit){
			this.removeFirst();
		}
		
		return !ret;
	}





}

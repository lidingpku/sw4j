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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * a counter for objects
 * 
 * @author Li Ding
 * 
 */
public class DataObjectCounter<V> {
	
	HashMap<V,Integer> m_counter = new HashMap<V,Integer>();
	
	public int count(V object){
		int count = getCount(object);
		count++;
		m_counter.put(object, count);
		return count;
	}
	
	public Set<V> keySet(){
		return m_counter.keySet();
	}
	
	public Set<Map.Entry<V,Integer>> entrySet(){
		return m_counter.entrySet();
	}
	
	public Map<V,Integer> getData(){
		return m_counter;
	}

	
	public int size(){
		return m_counter.size();
	}
	
	public int getCount(V object){
		Integer count = m_counter.get(object);
		if (null==count){
			count = 0;
		}
		return count;
	}
	
	public void clear(){
		this.m_counter.clear();
	}
	

}

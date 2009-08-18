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
package sw4j.task.graph;
/**
 * data structure that encodes a hyperedge which links to one sink from multiple sources
 * 
 * @author Li Ding
 * 
 */
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class DataHyperEdge implements Comparable<DataHyperEdge>{
	Integer m_sink = null;
	TreeSet<Integer> m_sources = new TreeSet<Integer>();
	
	public DataHyperEdge(Integer sink, Collection<Integer> sources){
		m_sink=sink;
		if (null!=sources)
			m_sources.addAll(sources);
	}
	
	public DataHyperEdge(Integer sink){
		m_sink=sink;
	}


	@Override
	public String toString() {
		return m_sink.toString()+", "+m_sources.toString();
	}
	
	public boolean isAtomic(){
		return m_sources.isEmpty();
	}

	public void addSource(Integer v_source) {
		this.m_sources.add(v_source);
	}

	public Integer getSink(){
		return m_sink;
	}

	public Set<Integer> getSources(){
		return m_sources;
	}
	
	public int compareTo(DataHyperEdge o) {
		return this.toString().compareTo(o.toString());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_sink == null) ? 0 : m_sink.hashCode());
		result = prime * result
				+ ((m_sources == null) ? 0 : m_sources.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DataHyperEdge other = (DataHyperEdge) obj;
		if (m_sink == null) {
			if (other.m_sink != null)
				return false;
		} else if (!m_sink.equals(other.m_sink))
			return false;
		if (m_sources == null) {
			if (other.m_sources != null)
				return false;
		} else if (!m_sources.equals(other.m_sources))
			return false;
		return true;
	}
	
}

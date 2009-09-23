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
 * a hyperedge has only one input
 * a hyperedge has multiple outputs
 * a hyperedge is uniquely identified by its input and outputs
 * the weight is an annotation property, default 1
 * 
 * @author Li Ding
 * 
 */
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class DataHyperEdge implements Comparable<DataHyperEdge>{
	public static int DEFAULT_WEIGHT = 1;
	Integer m_output = null;
	TreeSet<Integer> m_input = new TreeSet<Integer>();
	int m_weight = DEFAULT_WEIGHT;
	
	
	public DataHyperEdge(Integer sink){
		this(sink, null, DEFAULT_WEIGHT);
	}

	public DataHyperEdge(Integer sink, int weight){
		this(sink, null, weight);
	}
	
	public DataHyperEdge(Integer sink, Collection<Integer> sources){
		this(sink, sources, DEFAULT_WEIGHT);

	}

	public DataHyperEdge(Integer sink, Collection<Integer> sources, int weight){
		m_output=sink;
		if (null!=sources)
			m_input.addAll(sources);
		m_weight=weight;
	}

	@Override
	public String toString() {
		return m_output+", "+m_input+", "+ m_weight;
	}

	public String getID() {
		return m_output+"-"+m_input;
	}

	public boolean isAtomic(){
		return m_input.isEmpty();
	}

	public void addInput(Integer v_source) {
		this.m_input.add(v_source);
	}

	public Integer getOutput(){
		return m_output;
	}

	public Set<Integer> getInputs(){
		return m_input;
	}
	
	public int getWeight(){
		return m_weight;
	}

	
	public int compareTo(DataHyperEdge o) {
		return this.getID().compareTo(o.getID());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_output == null) ? 0 : m_output.hashCode());
		result = prime * result
				+ ((m_input == null) ? 0 : m_input.hashCode());
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
		return this.getID().equals(other.getID());
	}

	public void setWeigth(Integer weight) {
		m_weight= weight;
	}
	
}

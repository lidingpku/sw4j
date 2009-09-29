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
import java.util.StringTokenizer;
import java.util.TreeSet;

import sw4j.util.ToolSafe;

public class DataHyperEdge implements Comparable<DataHyperEdge>{
	Integer m_output = null;
	TreeSet<Integer> m_input = new TreeSet<Integer>();
	String m_id = DEFAULT_ID;
	
	public static int DEFAULT_WEIGHT = 1;
	public static String DEFAULT_ID = "";	
	int m_weight = DEFAULT_WEIGHT;	
	
	public DataHyperEdge(Integer output){
		this(output, null);
	}

	public DataHyperEdge(Integer output, Collection<Integer> set_input){
		this(output, set_input,  DEFAULT_ID);
	}

	public DataHyperEdge(Integer output, Collection<Integer> set_input, String sz_id){
		this(output, set_input, sz_id, DEFAULT_WEIGHT);
	}

	public DataHyperEdge(Integer output, Collection<Integer> set_input, String sz_id, int weight){
		m_output=output;
		m_weight=weight;
		if (!ToolSafe.isEmpty(set_input))
			m_input.addAll(set_input);
		if (!ToolSafe.isEmpty(sz_id))
			m_id=sz_id;
	}



	public String export() {
		return toString()+",["+ m_weight+"]";
	}

	@Override
	public String toString() {
		return "["+m_id+"],["+m_output+"],"+m_input ;
	}
	
	public static DataHyperEdge parseString(String sz_id, String sz_output, String sz_inputs, String sz_weight){
		if (ToolSafe.isEmpty(sz_output))
			return null;
		
		//parse output node id
		Integer output = new Integer(sz_output);

		DataHyperEdge edge= new DataHyperEdge(output, null, sz_id);

		//parse weight
		if (!ToolSafe.isEmpty(sz_weight)){
			Integer weight = Integer.parseInt(sz_weight);
			edge.setWeight(weight);
		}
		
		//process input
		StringTokenizer st1 = new StringTokenizer(sz_inputs,",");
		while (st1.hasMoreTokens()){
			String temp = st1.nextToken().trim();
			edge.addInput(new Integer(temp));
		}
		
		return edge;
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
	
	public void setWeight(int mWeight) {
		m_weight = mWeight;
	}

	
	public int compareTo(DataHyperEdge o) {
		return this.toString().compareTo(o.toString());
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_input == null) ? 0 : m_input.hashCode());
		result = prime * result
				+ ((m_output == null) ? 0 : m_output.hashCode());
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
		DataHyperEdge other = (DataHyperEdge) obj;
		if (m_input == null) {
			if (other.m_input != null)
				return false;
		} else if (!m_input.equals(other.m_input))
			return false;
		if (m_output == null) {
			if (other.m_output != null)
				return false;
		} else if (!m_output.equals(other.m_output))
			return false;
		return true;
	}

	public void setWeigth(Integer weight) {
		m_weight= weight;
	}

	public boolean hasLoop() {
		return (this.m_input.contains(this.m_output));
	}
	
}

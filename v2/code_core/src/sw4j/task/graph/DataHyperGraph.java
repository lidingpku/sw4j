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
 * Data structure that encodes a hypergraph (i.e. a collection of hyper edges)
 * @author Li Ding
 * @seealso <code>DataHyperEdge</code>
 * 
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import sw4j.util.DataPVHMap;
import sw4j.util.ToolRandom;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

public class DataHyperGraph {
	/**
	 * provenance metadata: associate each hyperedge with its context
	 */
	DataPVHMap<DataHyperEdge,String> m_map_edge_context = new DataPVHMap<DataHyperEdge,String>();
	
	/**
	 * data: all hyperedges, indexed by their output 
	 */
	DataPVHMap<Integer, DataHyperEdge> m_map_output_edge = new DataPVHMap<Integer,DataHyperEdge>();
	
	/**
	 * cost: associate each hyperedge with its weight
	 */
	HashMap<DataHyperEdge, Integer> m_map_edge_weight = new HashMap<DataHyperEdge, Integer>();
	
	/**
	 * all inputs
	 */
	HashSet<Integer> m_inputs = new HashSet<Integer>();

	/**
	 * all axioms 
	 */
	HashSet<Integer> m_axioms = new HashSet<Integer>() ;
		
	/**
	 * all contexts 
	 */
	HashSet<String> m_contexts = new HashSet<String>() ;
	
	/**
	 * default constructor
	 */
	public DataHyperGraph() {
	}

	/**
	 * copy constructor
	 * @param lg
	 */
	public DataHyperGraph(DataHyperGraph lg) {
		this.add(lg);
	}

	/**
	 * add hyperedges from another graph to lg (merge)
	 * 
	 * @param lg
	 */
	public void add(DataHyperGraph lg) {
		if (null!=lg){
			m_map_edge_context.add(lg.m_map_edge_context);			
			m_map_output_edge.add(lg.m_map_output_edge);
			m_map_edge_weight.putAll(lg.m_map_edge_weight);
			m_inputs.addAll(lg.m_inputs);
			m_axioms.addAll(lg.m_axioms);
			m_contexts.addAll(lg.m_contexts);
			clearCache();
		}
	}
	
	/**
	 * clear a hyper graph
	 */
	public void reset(){
		m_map_edge_context.clear();
		m_map_output_edge.clear();
		m_map_edge_weight.clear();
		m_inputs.clear();
		m_axioms.clear();
		m_contexts.clear();
		clearCache();
	}
	
	public void clearCache(){
		
	}

	/**
	 * add a default hyperedge (empty context name)
	 * @param g
	 * @return
	 */
	public boolean add(DataHyperEdge g){
		return add(g, "default", 0);
	}
	
	/**
	 * add a default hyperedge (empty context name)
	 * @param g
	 * @param weight
	 * @return
	 */
	public boolean add(DataHyperEdge g, Integer weight){
		return add(g, "default", weight);
	}
	
	/**
	 * add a default hyperedge (empty weight)
	 * @param g
	 * @param context
	 * @return
	 */
	public boolean add(DataHyperEdge g, String context){
		return add(g, context, 0);
	}
	
	/**
	 * add a new hyper edge
	 * @param g
	 * @param context
	 * @return
	 */
	public boolean add(DataHyperEdge g, String context, Integer weight){
		if (null==context ){
			return false;
		}
		
		HashSet<String> contexts = new HashSet<String>();
		contexts.add(context);
		
		return this.add(g,contexts,weight);
	}

	/**
	 * add a hyperedge with a collection of context
	 * @param g
	 * @param contexts
	 * @return
	 */
	private boolean add(DataHyperEdge g, Collection<String> contexts, Integer weight) {
		if (null==g || null==contexts ){
			return false;
		}
		
		//avoid self-linked hyperedge
		if (g.getInputs().contains(g.getOutput()))
			return false;
		
		m_map_edge_context.add(g, contexts);
		m_map_output_edge.add(g.m_output, g);
		m_map_edge_weight.put(g, weight);
		m_inputs.addAll(g.m_input);
		if (g.isAtomic())
			m_axioms.add(g.getOutput());

		m_contexts.addAll(contexts);
		
		clearCache();
		return true;
	}
	
	public Collection<String> getContexts(){
		HashSet<String> temp = new HashSet<String>();
		temp.addAll(this.m_contexts);
		return temp;

	}
	
	public Collection<String> getContextsByEdge(DataHyperEdge g){
		Collection<String> ret = null;
		if (null!=g){
			ret = this.m_map_edge_context.getValues(g);
		}
		
		if (null==ret){
			ret =new HashSet<String>();
		}
		
		return ret;
	}
	
	public Integer getWeightByEdge(DataHyperEdge g){
		Integer ret = null;
		
		if (null!=g){
			ret = this.m_map_edge_weight.get(g);
		}
		
		return ret;
	}
	
	public DataHyperGraph getHyperGraphByContext(String szContext){
		return getSubHyperGraphs().get(szContext);
	}
	
	
	public Map<String,DataHyperGraph> getSubHyperGraphs(){
		HashMap<String,DataHyperGraph> ret = new HashMap<String,DataHyperGraph>();
		
		Iterator<Map.Entry<DataHyperEdge,Set<String>>> iter = this.m_map_edge_context.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<DataHyperEdge,Set<String>> entry = iter.next();
			
			Iterator<String> iter_context= entry.getValue().iterator();
			while (iter_context.hasNext()){
				String szContext = iter_context.next();
				
				DataHyperGraph g = ret.get(szContext);
				if (null==g){
					g = new DataHyperGraph();
					ret.put(szContext,g);
				}
				
				g.add(entry.getKey());
				
				
				
			}
		}
		return ret;
	}
	/**
	 * list all output vertices
	 * 
	 * @return
	 */
	public Set<Integer> getOutputs() {
		return this.m_map_output_edge.keySet();
	}
	
	/**
	 * list all input vertices
	 * @return
	 */
	public Set<Integer> getInputs() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.m_inputs);
		return temp;
	}
	
	/**
	 * list all hyperedgs
	 * @return
	 */
	public Set<DataHyperEdge> getEdges() {
		return this.m_map_edge_context.keySet();
	}

	/**
	 * list edges sharing the given output
	 * 
	 * @param output
	 * @return
	 */
	public Collection<DataHyperEdge> getEdgesByOutput(Integer output){
		Collection<DataHyperEdge> ret = null;
		if (null!=output){
			ret = this.m_map_output_edge.getValues(output);
		}
		
		if (null==ret){
			ret =new HashSet<DataHyperEdge>();
		}
		
		return ret;
	}
	
	/**
	 * list all vertices
	 * @return
	 */
	public HashSet<Integer> getVertices() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.getInputs());
		temp.addAll(this.getOutputs());
		return temp;
	}

	/**
	 * list all axiom hyperedges
	 * 
	 * @return
	 */
	public HashSet<Integer> getAxioms() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.m_axioms);
		return temp;
	}

	/**
	 * list all root vertices. i.e. outputs not being mentioned as input
	 * @return
	 */
	public HashSet<Integer> getRoots() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.getOutputs());
		temp.removeAll(this.getInputs());
		return temp;
	}
	
	private Collection<Integer> getEdgesSharingOutput(int nMaxResults){
		HashSet<Integer> results = new HashSet<Integer>();
		
		Iterator <Map.Entry<Integer, Set<DataHyperEdge>>> iter = this.m_map_output_edge.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<Integer, Set<DataHyperEdge>> entry = iter.next();
			if (entry.getValue().size()>1){
				results.add(entry.getKey());
			}
			
			if (nMaxResults !=-1 && results.size()>nMaxResults){
				break;
			}
		}		
		return results;
	}
	
	
	
	
	

	/**
	 * a hypergraph is complete iff. any path ends at an axiom vertex
	 * 
	 * @return
	 */
	public boolean isComplete(){
		// all inputs must be a output
		return this.getOutputs().containsAll(this.getInputs());
	}
	
	/**
	 * a hypergraph is single-root iff. this graph has exactly one root, and the root is v(if v is not null)
	 * 
	 * @param v
	 * @return
	 */
	public boolean isSingleRoot(Integer v){
		if (this.getRoots().size()!=1)
			return false;
		
		return ToolSafe.isEmpty(v)|| this.getRoots().contains(v);
	}
	
	/**
	 * a hypergraph is concise iff. no two hyperedges share the same output
	 * 
	 * @param v
	 * @return
	 */
	public boolean isConcise(){
		return (getEdgesSharingOutput(1).size() ==0);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DataHyperGraph))
			return false;
		
		DataHyperGraph other = (DataHyperGraph) obj;
			
		return ToolSafe.isEqual(this.m_map_edge_context, other.m_map_edge_context);
	}

	/**
	 * a hyper graph is acyclic iff. no cycle exists
	 * 
	 * @return
	 */
	public  boolean isAcyclic() {
		return !getDigraph().hasCycle();
	}
	
	public DataDigraph getDigraph(){
		DataDigraph ret = new DataDigraph();
		Iterator<DataHyperEdge> iter = this.getEdges().iterator();
		while (iter.hasNext()){
			DataHyperEdge edge = iter.next();
			ret.add(edge.getOutput(),edge.getInputs());
		}
		
		//ret = ret.create_tc();
		return ret;
	}



	/**
	 * summarize data
	 * 
	 * @return
	 */
	public String data_summary(){
		String ret ="";
		ret += String.format("\n#generated: %s", ToolString.formatXMLDateTime(System.currentTimeMillis()));
		ret += String.format("\n#context (total): %d - %s ", this.getContexts().size(), this.getContexts());
		ret += String.format("\n#hyperedges (total): %d", this.getEdges().size());
		ret += String.format("\n#hyperedges (axioms): %d", this.getAxioms().size());
		ret += String.format("\n#hyperedges (sharing output, limit 10): %d - %s", this.getEdgesSharingOutput(-1).size(), this.getEdgesSharingOutput(10));
		ret += String.format("\n#vertices (total): %d", this.getVertices().size());
		ret += String.format("\n#vertices (outputs): %d", this.getOutputs().size());
		ret += String.format("\n#vertices (inputs): %d", this.getInputs().size());
		ret += String.format("\n#vertices (roots): %d - %s ", this.getRoots().size(), this.getRoots());
		
		return ret;
	}
	
	
	public String data_export(){
		return data_export(null);
	}
	
	/**
	 * export data in to text based exchange format
	 * 
	 * @param reference  - the referenced hypergraph that record the provenance metadata for each hyperedge
	 * @return
	 */
	public String data_export(DataHyperGraph reference){
		if (null==reference)
			reference = this;
		
		String ret = data_summary();
		{
			Iterator<DataHyperEdge> iter= this.getEdges().iterator();
			while (iter.hasNext()){
				DataHyperEdge edge = iter.next();

				Collection<String> contexts = reference.getContextsByEdge(edge);

				Integer weight= reference.getWeightByEdge(edge);
				
				if (!ToolSafe.isEmpty(ret))
					ret +="\n";
				ret += String.format("{%s, %s, %s}", edge, contexts, weight);
			}
			
		}
		return ret;
	}

	/**
	 * list all nodes
	 * @return
	 */
	public Set<Integer> getNodes(){
		TreeSet<Integer> ret = new TreeSet<Integer>();
		for (DataHyperEdge e : this.getEdges()){
			ret.add(e.getOutput());
			ret.addAll(e.getInputs());
		}
		return ret;
	}
	
	/**
	 * export data in to text based exchange format
	 * 
	 * @param reference  - the referenced hypergraph that record the provenance metadata for each hyperedge
	 * @return
	 */
	public String data_export_graphviz(Map<Integer,String> map_node_id_input, Map<Integer,Properties> map_node_params, Map<DataHyperEdge,Properties> map_edge_params, String sz_more ){
		//list all nodes
		Set<Integer> nodes = getNodes();
		
		//prepare map_node_id
		HashMap <Integer,String> map_node_id = new HashMap<Integer,String> ();
		for(Integer node: nodes){
			String id = null;
			if (null!=map_node_id_input)
				id = map_node_id_input.get(node);
			if (null==id)
				id="x_"+node;
			map_node_id.put(node,id);
		}

	
		String ret = "/*\n"+data_summary()+"\n*/";
		ret +="digraph g { rankdir=BT; node [ shape = box];\n";
		ret += sz_more +"\n";
		{
			//print nodes
			for(Integer node: nodes){
				String label = map_node_id.get(node);
				String params ="";
				if (null!=map_node_params){
					Properties prop= map_node_params.get(node);
					if (ToolSafe.isEmpty(prop)){
						prop= new Properties();
					}
					prop.put("shape", "box");

					for (Object key :prop.keySet()){
							params += String.format("%s=\"%s\" ", key, prop.get(key));
					}
					
				}
				ret += String.format(" \"%s\" [ %s ];\n ", label, params);
			}
		}
		{
			//print arcs
			Iterator<DataHyperEdge> iter= this.getEdges().iterator();
			int edgeid=1;
			String label;
			while (iter.hasNext()){
				DataHyperEdge edge = iter.next();
				
				String e = "a_"+edgeid;
				edgeid++;
				
				String params ="";
				if (null!=map_edge_params){
					Properties prop= map_edge_params.get(edge);
					if (ToolSafe.isEmpty(prop)){
						prop= new Properties();
					}
					prop.put("shape", "diamond");
					for (Object key :prop.keySet()){
						params += String.format("%s=\"%s\" ", key, prop.get(key));
					}
				}
				ret += String.format(" \"%s\" [ %s ];\n ", e, params);
				
				Integer output= edge.getOutput();
				label = map_node_id.get(output);
				ret += String.format(" \"%s\" -> \"%s\";\n ", label, e );
				Iterator<Integer> iter_input = edge.getInputs().iterator();
				while (iter_input.hasNext()){
					Integer input= iter_input.next();
					label = map_node_id.get(input);
					ret += String.format(" \"%s\" -> \"%s\";\n ",  e, label );
				}
			}
			
		}
		
		ret += "{ rank=same; ";
		for(Integer leaf: this.getAxioms()){
			String label = map_node_id.get(leaf);
			ret += String.format(" \"%s\"",  label );			
		}
		ret +="}\n";
		ret +="\n}\n";
		return ret;
	}
	/**
	 * import data 
	 * 
	 * @param dump
	 */
	public void data_import(String dump){
		this.reset();

		
		StringTokenizer st = new StringTokenizer(dump, "{}\n");
		while (st.hasMoreTokens()){
			String line = st.nextToken();
			//skip comment line, which starts with #
			if (line.trim().startsWith("#"))
				continue;
			
			int index1 = line.indexOf(",");
			int index2 = line.indexOf("[");
			int index3 = line.indexOf("]");
			int index4 = line.indexOf("[",index3);
			int index5 = line.indexOf("]",index4);
			int index6 = line.indexOf("[",index5);
			int index7 = line.indexOf("]",index6);
			if (index1<0)
				continue;
			if (index2<index1)
				continue;
			if (index3<index2)
				continue;
			if (index4<index3)
				continue;
			if (index5<index4)
				continue;
			if (index6<index5)
				continue;
			if (index7<index6)
				continue;

			Integer output = new Integer(line.substring(0,index1).trim());
			DataHyperEdge g = new DataHyperEdge(output);
	
			{
				String szInputs =line.substring(index2+1, index3).trim();
				if (null!= szInputs && !szInputs.isEmpty()){
					StringTokenizer st1 = new StringTokenizer(szInputs,",");
					while (st1.hasMoreTokens()){
						String temp = st1.nextToken().trim();
						g.addInput(new Integer(temp));
					}
				}
			}
			{
				String szProofs =line.substring(index4+1, index5).trim();
				String cost =line.substring(index6+1, index7).trim();
				if (null!= szProofs ){
					StringTokenizer st1 = new StringTokenizer(szProofs,",");
					TreeSet<String> contexts = new TreeSet<String>();
					while (st1.hasMoreTokens()){
						String temp = st1.nextToken().trim();
						contexts.add(temp);
					}
					
					Integer weight = Integer.parseInt(cost);			
					this.add(g, contexts, weight);
					
				}
			}
			
		}
	}
	
	/**
	 * create a random hypergraph
	 * 
	 * @param total_edges
	 * @param total_vertex
	 * @param max_branch
	 * @return
	 */
	public static DataHyperGraph data_create_random(int total_edges, int total_vertex, int max_branch, int max_weight){
		if (total_vertex<3){
			return null;
		}
		
		//create vertex
		Integer[] ary_v = new Integer[total_vertex];
		for (int i=0; i<ary_v.length; i++){
			ary_v[i]= new Integer(i);
		}
		
		DataHyperGraph lg = new DataHyperGraph();
		
		//create HyperEdge
		for (int i=0;i<total_edges; i++){
			//choose output
			int id_output= (int)(Math.random()* total_vertex);
			Integer v_output = ary_v[id_output];
	
			DataHyperEdge g = new DataHyperEdge( v_output);
			
			//choose inputs
			int iBranch = ToolRandom.randomInt(max_branch);
			if (0==iBranch){
				//System.out.println("no input node" + v_output);
			}
			for (int j=0; j<iBranch; j++){
				int id_input;
				do {
					id_input= ToolRandom.randomInt( total_vertex);
				} while (id_input == id_output);
				Integer v_input = ary_v[id_input];
				g.addInput(v_input);
			}
			int weight= (int)(Math.random()* max_weight);
			lg.add(g, weight);
		}
	
		//create missing missing Hypergraph
		{
			Set<Integer> vertex = lg.getInputs();
			vertex.removeAll(lg.getOutputs());
			Iterator<Integer> iter = vertex.iterator();
			while (iter.hasNext()){
				lg.add(new DataHyperEdge(iter.next()));
			}
		}

		System.out.println("# --- BEGIN Created random Linked Graph ------");
		System.out.println("# 1. Metadata ");
		System.out.println(String.format("# * hyperedges: %d (config %d)" ,  lg.getEdges().size(), total_edges));
		System.out.println(String.format("# * vertices: %d (config %d)" , lg.getVertices().size(), total_vertex));
		System.out.println(String.format("# * max_branch: %d " , max_branch));
		System.out.println("# 2. Data ");
		System.out.println(lg.data_export());
		System.out.println("# ---- END  Created random Linked Graph------");

		return lg;
	}
}

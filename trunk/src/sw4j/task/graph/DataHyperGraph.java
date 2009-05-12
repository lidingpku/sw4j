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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
	 * data: all hyperedges, indexed by their sink 
	 */
	DataPVHMap<Integer, DataHyperEdge> m_map_sink_edge = new DataPVHMap<Integer,DataHyperEdge>();
	
	/**
	 * all sources
	 */
	HashSet<Integer> m_sources = new HashSet<Integer>();

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
			m_map_sink_edge.add(lg.m_map_sink_edge);
			m_sources.addAll(lg.m_sources);
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
		m_map_sink_edge.clear();
		m_sources.clear();
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
		return add(g, "default");
	}
	
	/**
	 * add a new hyper edge
	 * @param g
	 * @param context
	 * @return
	 */
	public boolean add(DataHyperEdge g, String context){
		if (null==context ){
			return false;
		}
		
		HashSet<String> contexts = new HashSet<String>();
		contexts.add(context);
		
		return this.add(g,contexts);
	}

	/**
	 * add a hyperedge with a collection of context
	 * @param g
	 * @param contexts
	 * @return
	 */
	private boolean add(DataHyperEdge g, Collection<String> contexts) {
		if (null==g || null==contexts ){
			return false;
		}
		
		m_map_edge_context.add(g, contexts);
		m_map_sink_edge.add(g.m_sink, g);
		m_sources.addAll(g.m_sources);
		if (g.isAtomic())
			m_axioms.add(g.getSink());

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
	
	/**
	 * list all sink vertices
	 * 
	 * @return
	 */
	public Set<Integer> getSinks() {
		return this.m_map_sink_edge.keySet();
	}
	
	/**
	 * list all source vertices
	 * @return
	 */
	public Set<Integer> getSources() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.m_sources);
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
	 * list edges sharing the given sink
	 * 
	 * @param sink
	 * @return
	 */
	public Collection<DataHyperEdge> getEdgesBySink(Integer sink){
		Collection<DataHyperEdge> ret = null;
		if (null!=sink){
			ret = this.m_map_sink_edge.getValues(sink);
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
		temp.addAll(this.getSources());
		temp.addAll(this.getSinks());
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
	 * list all root vertices. i.e. sinks not being mentioned as source
	 * @return
	 */
	public HashSet<Integer> getRoots() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.getSinks());
		temp.removeAll(this.getSources());
		return temp;
	}
	
	private Collection<Integer> getEdgesSharingSink(int nMaxResults){
		HashSet<Integer> results = new HashSet<Integer>();
		
		Iterator <Map.Entry<Integer, Set<DataHyperEdge>>> iter = this.m_map_sink_edge.entrySet().iterator();
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
		// all sources must be a sink
		return this.getSinks().containsAll(this.getSources());
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
	 * a hypergraph is concise iff. no two hyperedges share the same sink
	 * 
	 * @param v
	 * @return
	 */
	public boolean isConcise(){
		return (getEdgesSharingSink(1).size() ==0);
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
			ret.add(edge.getSink(),edge.getSources());
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
		ret += String.format("\n#hyperedges (sharing sink, limit 10): %d - %s", this.getEdgesSharingSink(-1).size(), this.getEdgesSharingSink(10));
		ret += String.format("\n#vertices (total): %d", this.getVertices().size());
		ret += String.format("\n#vertices (sinks): %d", this.getSinks().size());
		ret += String.format("\n#vertices (sources): %d", this.getSources().size());
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

				if (!ToolSafe.isEmpty(ret))
					ret +="\n";
				ret += String.format("{%s, %s}", edge, contexts);
			}
			
		}
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

			Integer sink = new Integer(line.substring(0,index1).trim());
			DataHyperEdge g = new DataHyperEdge(sink);
	
			{
				String szSources =line.substring(index2+1, index3).trim();
				if (null!= szSources && !szSources.isEmpty()){
					StringTokenizer st1 = new StringTokenizer(szSources,",");
					while (st1.hasMoreTokens()){
						String temp = st1.nextToken().trim();
						g.addSource(new Integer(temp));
					}
				}
			}
			{
				String szProofs =line.substring(index4+1, index5).trim();
				if (null!= szProofs ){
					StringTokenizer st1 = new StringTokenizer(szProofs,",");
					TreeSet<String> contexts = new TreeSet<String>();
					while (st1.hasMoreTokens()){
						String temp = st1.nextToken().trim();
						contexts.add(temp);
					}
					this.add(g, contexts);
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
	public static DataHyperGraph data_create_random(int total_edges, int total_vertex, int max_branch){
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
			//choose sink
			int id_sink= (int)(Math.random()* total_vertex);
			Integer v_sink = ary_v[id_sink];
	
			DataHyperEdge g = new DataHyperEdge( v_sink);
			
			//choose sources
			int iBranch = ToolRandom.randomInt(max_branch);
			if (0==iBranch){
				//System.out.println("no input node" + v_sink);
			}
			for (int j=0; j<iBranch; j++){
				int id_source;
				do {
					id_source= ToolRandom.randomInt( total_vertex);
				} while (id_source == id_sink);
				Integer v_source = ary_v[id_source];
				g.addSource(v_source);
			}
			
			lg.add(g);
		}
	
		//create missing missing Hypergraph
		{
			Set<Integer> vertex = lg.getSources();
			vertex.removeAll(lg.getSinks());
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

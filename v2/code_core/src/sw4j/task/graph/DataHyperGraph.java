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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import sw4j.util.DataColorMap;
import sw4j.util.DataPVHMap;
import sw4j.util.DataSmartMap;
import sw4j.util.ToolMath;
import sw4j.util.ToolRandom;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

import com.csvreader.CsvReader;



public class DataHyperGraph {
	public static int DEFAULT_WEIGHT = 1;
	public static String DEFAULT_CONTEXT ="";
	public static final int HG_CSV_FIELDS=6;
	
	/**
	 * provenance metadata: associate each hyperedge with its context
	 */
	DataPVHMap<DataHyperEdge,String> m_map_edge_context = new DataPVHMap<DataHyperEdge,String>();
	
	/**
	 * data: all hyperedges, indexed by their output 
	 */
	DataPVHMap<Integer, DataHyperEdge> m_map_output_edge = new DataPVHMap<Integer,DataHyperEdge>();
	
	/**
	 * provenance metadata: associate each hyperedge with its label
	 */
	HashMap<DataHyperEdge,String> m_map_edge_label = new HashMap<DataHyperEdge,String>();

	/**
	 * provenance metadata: associate each vertex with its label
	 */
	HashMap<Integer,String> m_map_vertex_label = new HashMap<Integer,String>();
	
	/**
	 * all inputs
	 */
	HashSet<Integer> m_set_vertex_input = new HashSet<Integer>();

	/**
	 * all terminal node 
	 */
	HashSet<Integer> m_set_vertex_leaf = new HashSet<Integer>() ;
		
	/**
	 * all leaf edges 
	 */
	HashSet<DataHyperEdge> m_set_edge_leaf = new HashSet<DataHyperEdge>() ;
	
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
			m_set_vertex_input.addAll(lg.m_set_vertex_input);
			m_set_vertex_leaf.addAll(lg.m_set_vertex_leaf);
			m_set_edge_leaf.addAll(lg.m_set_edge_leaf);
			clearCache();
		}
	}
	
	/**
	 * clear a hyper graph
	 */
	public void reset(){
		m_map_edge_context.clear();
		m_map_output_edge.clear();
		m_set_vertex_input.clear();
		m_set_vertex_leaf.clear();
		m_set_edge_leaf.clear();
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
		return add(g, DEFAULT_CONTEXT);
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
	public boolean add(DataHyperEdge g, Collection<String> contexts) {
		if (null==g || null==contexts ){
			return false;
		}
		
		//TODO we need to rethink it avoid self-linked hyperedge
//		if (g.getInputs().contains(g.getOutput()))
//			return false;
		
		m_map_edge_context.add(g, contexts);
		m_map_output_edge.add(g.m_output, g);
		m_set_vertex_input.addAll(g.m_input);
		if (g.isLeaf()){
			m_set_vertex_leaf.add(g.getOutput());
			m_set_edge_leaf.add(g);
		}
		
		clearCache();
		return true;
	}
	
	public Collection<String> getContexts(){
		return this.m_map_edge_context.getValues();
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
	

	
	public DataHyperGraph getHyperGraphByContext(String szContext){
		return getSubHyperGraphs().get(szContext);
	}
	
	public String getLabel(DataHyperEdge edge){
		return getLabel(edge,edge.toString());
	}
	public String getLabel(DataHyperEdge edge, String defaultLabel){
		String ret = this.m_map_edge_label.get(edge);
		if (ToolSafe.isEmpty(ret))
			ret = defaultLabel;
		return ret;
	}
	
	public String getLabel(Integer vertex){
		return getLabel(vertex,"x_"+vertex.toString());
	}
	public String getLabel(Integer vertex, String defaultLabel){
		String ret = this.m_map_vertex_label.get(vertex);
		if (ToolSafe.isEmpty(ret))
			ret =defaultLabel;
		return ret;
	}

	public void setLabel(DataHyperEdge edge, String label){
		this.m_map_edge_label.put(edge,label);
	}
	
	public void setLabel(Integer vertex, String label){
		this.m_map_vertex_label.put(vertex,label);
	}

	public Map<String,DataHyperGraph> getSubHyperGraphs(){
		HashMap<String,DataHyperGraph> ret = new HashMap<String,DataHyperGraph>();
		
		Iterator<Map.Entry<DataHyperEdge,Set<String>>> iter = this.m_map_edge_context.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<DataHyperEdge,Set<String>> entry = iter.next();
			
			Iterator<String> iter_context= entry.getValue().iterator();
			while (iter_context.hasNext()){
				String context = iter_context.next();
				
				DataHyperGraph g = ret.get(context);
				if (null==g){
					g = new DataHyperGraph();
					ret.put(context,g);
				}
				
				g.add(entry.getKey(),context);
				
				
				
			}
		}
		return ret;
	}
	/**
	 * list all output vertices
	 * 
	 * @return
	 */
	public Set<Integer> getVerticesOutput() {
		return this.m_map_output_edge.keySet();
	}
	
	/**
	 * list all input vertices
	 * @return
	 */
	public Set<Integer> getVerticesInput() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.m_set_vertex_input);
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
		temp.addAll(this.getVerticesInput());
		temp.addAll(this.getVerticesOutput());
		return temp;
	}

	
	/**
	 * list all terminal vertices
	 * 
	 * @return
	 */
	public HashSet<Integer> getVerticesLeaf() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.m_set_vertex_leaf);
		return temp;
	}

	/**
	 * list all leaf hyperedges
	 * 
	 * @return
	 */
	public HashSet<DataHyperEdge> getEdgesLeaf() {
		HashSet<DataHyperEdge> temp = new HashSet<DataHyperEdge>();
		temp.addAll(this.m_set_edge_leaf);
		return temp;
	}

	
	/**
	 * list all root vertices. i.e. outputs not being mentioned as input
	 * @return
	 */
	public HashSet<Integer> getVerticesRoot() {
		HashSet<Integer> temp = new HashSet<Integer>();
		temp.addAll(this.getVerticesOutput());
		temp.removeAll(this.getVerticesInput());
		return temp;
	}
	
	private HashMap<Integer,Integer> countEdgesSharingOutput(int nMaxResults){
		HashMap<Integer,Integer> results = new HashMap<Integer,Integer>();
		
		Iterator <Map.Entry<Integer, Set<DataHyperEdge>>> iter = this.m_map_output_edge.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<Integer, Set<DataHyperEdge>> entry = iter.next();
			if (entry.getValue().size()>1){
				results.put(entry.getKey(), entry.getValue().size());
			}
			
			if (nMaxResults !=-1 && results.size()>nMaxResults){
				break;
			}
		}		
		return results;
	}
	

	private HashMap<DataHyperEdge,Integer> countContextsSharingEdge(int nMaxResults){
		HashMap<DataHyperEdge,Integer> results = new HashMap<DataHyperEdge,Integer>();
		
		for (DataHyperEdge edge: this.getEdges()){
			Collection<String> contexts= this.getContextsByEdge(edge);
			if (!ToolSafe.isEmpty(contexts) && contexts.size()>1){
				results.put(edge, contexts.size());
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
		return this.getVerticesOutput().containsAll(this.getVerticesInput());
	}
	
	/**
	 * a hypergraph is single-root iff. this graph has exactly one root, and the root is v(if v is not null)
	 * 
	 * @param v
	 * @return
	 */
	public boolean isSingleRoot(Integer v){
		if (this.getVerticesRoot().size()!=1)
			return false;
		
		return ToolSafe.isEmpty(v)|| this.getVerticesRoot().contains(v);
	}
	
	/**
	 * a hypergraph is concise iff. no two hyperedges share the same output
	 * 
	 * @param v
	 * @return
	 */
	public boolean isConcise(){
		return (countEdgesSharingOutput(1).size() ==0);
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
		DataDigraph ret = new DataDigraph(ToolMath.max(this.getVertices(),0));
		Iterator<DataHyperEdge> iter = this.getEdges().iterator();
		while (iter.hasNext()){
			DataHyperEdge edge = iter.next();
			ret.add(edge.getOutput(),edge.getInputs());
		}
		
		//ret = ret.create_tc();
		return ret;
	}


	public Integer getTotalWeight(){
		int cost= 0;
		for (DataHyperEdge edge :this.getEdges()){
			cost+= edge.getWeight();
		}
		return cost;
	}
	

	/**
	 * summarize data
	 * 
	 * @return
	 */
	public String data_summary(){
		String ret = "";
		DataSmartMap data =get_data_summary(true);
		

		for (String key: data.getAllFieldName()){
			ret +=String.format("#%s: %s\n", key, data.getAsString(key));
		}
		return ret;
	}
	
	public DataSmartMap get_data_summary(boolean bWithListing){
		DataSmartMap data = new DataSmartMap();

		if(bWithListing){
			data.put("hyperedges[sharing conclusion][example listing]",  this.countEdgesSharingOutput(10));
			data.put("hyperedges[sharing context][example listing]",  this.countContextsSharingEdge(10));
			data.put("context[listing]", this.getContexts().toString());
			data.put("verteics[roots][listing]", this.getVerticesRoot().toString());			
		}
		
		data.put("date", ToolString.formatXMLDateTime(System.currentTimeMillis()));
		data.put("context[total]", this.getContexts().size());
		data.put("hyperedges[total]", this.getEdges().size());
		data.put("hyperedges[terminal]", this.getVerticesLeaf().size());
		{
			Collection<Integer> ary = this.countEdgesSharingOutput(-1).values();
			data.put("hyperedges[sharing output]", ary.size());
			data.put("hyperedges[sharing output max]", ToolMath.max(ary,1));
		}
		{
			Collection<Integer> ary = this.countContextsSharingEdge(-1).values();
			data.put("contexts[sharing hyperedge]", ary.size());
			data.put("contexts[sharing hyperedge max]", ToolMath.max(ary,1));
			
			data.put("hyperedges[occurence]", ToolMath.sum(ary)-ary.size()+this.getEdges().size());
		}
		data.put("vertices[total]", this.getVertices().size());
		data.put("vertices[outputs]", this.getVerticesOutput().size());
		data.put("vertices[inputs]", this.getVerticesInput().size());
		data.put("vertices[roots]", this.getVerticesRoot().size());
		
		return data;
	}
	
	public String data_export(){
		return data_export(null);
	}
	
	/**
	 * export data in to text based exchange format
	 * 
	 * csv format, each row corresponds to a hyperarc
	 * "id", "output-node-id", "input-node-ids", "weight", "contexts"
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
				
				ret += String.format("%s,%s,\"%s\",\"%s\"\n", edge.export().replaceAll("[\\[|\\]]", "\""), contexts.toString().replaceAll("[\\[|\\]]", "\""),getLabel(edge.getOutput(),""),getLabel(edge,""));
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

		CsvReader csv = new CsvReader(new StringReader(dump));

		try {
			while (csv.readRecord()){
				String line = csv.getRawRecord().trim();
				if (line.startsWith("#"))
					continue;
				
				if (csv.getColumnCount()!=HG_CSV_FIELDS)
					continue;
				
				int index=0;
				String sz_output = csv.get(index).trim();

				index++;
				String sz_inputs = csv.get(index).trim();
				
				index++;
				String sz_weight = csv.get(index).trim();

				index++;
				String sz_contexts = csv.get(index).trim();
				
				index++;
				String sz_label_output= csv.get(index).trim();

				index++;
				String sz_label_edge = csv.get(index).trim();

				//create hyperedge
				DataHyperEdge edge = DataHyperEdge.parseString( sz_output, sz_inputs, sz_weight);
				
				if (null==edge)
					continue;

				//add(edge);
				setLabel(edge,sz_label_edge);
				setLabel(edge.getOutput(),sz_label_output);
				
				//parse context
				StringTokenizer st1 = new StringTokenizer(sz_contexts,",");
				TreeSet<String> set_contexts = new TreeSet<String>();
				while (st1.hasMoreTokens()){
					String sz_context = st1.nextToken().trim();
					set_contexts.add(sz_context);
					this.add(edge, sz_context);
				}					
				if (set_contexts.size()==0){
					this.add(edge);	
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			int id_output = ToolRandom.randomInt( total_vertex);
			
			Integer v_output = ary_v[id_output];
	
			DataHyperEdge edge = new DataHyperEdge(v_output);
			if (max_weight>0){
				int weight= ToolRandom.randomInt( max_weight);
				edge.setWeight( weight);
			}
			//choose inputs
			int iBranch = ToolRandom.randomInt(max_branch-1)+1;
			for (int j=0; j<iBranch; j++){
				int id_input;
				do {
					id_input= ToolRandom.randomInt(total_vertex);
				} while (id_input == id_output);
				Integer v_input = ary_v[id_input];
				edge.addInput(v_input);
			}
			lg.add(edge);
		}
	
		//create missing missing Hypergraph
		{
			Set<Integer> vertex = lg.getVerticesInput();
			vertex.removeAll(lg.getVerticesOutput());
			Iterator<Integer> iter = vertex.iterator();
			while (iter.hasNext()){
				lg.add(new DataHyperEdge(iter.next()));
			}
		}

		{
			System.out.println("# --- BEGIN Created random Linked Graph ------");
			System.out.println("# 1. Metadata ");
			System.out.println(String.format("# * hyperedges: %d (config %d)" ,  lg.getEdges().size(), total_edges));
			System.out.println(String.format("# * vertices: %d (config %d)" , lg.getVertices().size(), total_vertex));
			System.out.println(String.format("# * max_branch: %d " , max_branch));
			System.out.println("# 2. Data ");
			System.out.println(lg.data_export());
			System.out.println("# ---- END  Created random Linked Graph------");
		}
		return lg;
	}
	
	public String data_export_graphviz(String sz_more){
		return data_export_graphviz(this,null,null,null,sz_more);
	}

	public String data_export_graphviz_subgraph(String sz_more){
		String sz_content ="";
		for (int node: this.getVertices()){
			sz_content +=String.format("\"%d\";\n",node);
		}
		for (DataHyperEdge edge: this.getEdges()){
			sz_content +=String.format("\"%s\";\n",edge.toString());
		}
		
		return String.format("subgraph cluster_opt \n{  label=\"%s\"; \n fillcolor=cornsilk; style=filled; \n %s \n}\n", sz_more, sz_content);

	}
	
	/**
	 * export hypergraph data into DOT format
	 * http://www.graphviz.org/doc/info/lang.html
	 *
	 * @param hg
	 * @param map_node_id_input
	 * @param map_node_params
	 * @param map_edge_params
	 * @param sz_more
	 * @return
	 */
	public static String data_export_graphviz(DataHyperGraph hg, Map<Integer,String> map_node_id_label, Map<Integer,Properties> map_node_params, Map<DataHyperEdge,Properties> map_edge_params, String sz_more ){
		//list all nodes
		Set<Integer> nodes = hg.getVertices();


		String ret = "/*\n"+hg.data_summary()+"\n*/";
		ret +="digraph g { \n";
		
		if (!ToolSafe.isEmpty(sz_more))
			ret += sz_more +"\n";
		
		{
			//print nodes
			for(Integer node: nodes){
				String params ="";
				Properties prop= null;
				if (null!=map_node_params)
					prop = map_node_params.get(node);
				if (ToolSafe.isEmpty(prop)){
					prop= new Properties();
				}
				prop.put("shape", "box");
				if (hg.getEdgesByOutput(node).size()>1){
					prop.put("color", "red");
					prop.put("style", "filled");					
				}
				prop.put("label", hg.getLabel(node));
				
				for (Object key :prop.keySet()){
					params += String.format("%s=\"%s\" ", key, prop.get(key));
				}

				ret += String.format(" \"%s\" [ %s ];\n ", node, params);
			}
		}
		{
			DataColorMap colors = new DataColorMap();
			//print arcs
			Iterator<DataHyperEdge> iter= hg.getEdges().iterator();
			//int edgeid=1;
			while (iter.hasNext()){
				DataHyperEdge edge = iter.next();

				//String e = "a_"+edgeid;
				//edgeid++;
				String e = edge.toString();

				String params ="";
				Properties prop= null;
				if (null!=map_edge_params)
					prop=map_edge_params.get(edge);
				if (ToolSafe.isEmpty(prop)){
					prop= new Properties();
				}
				if (edge.isLeaf())
					prop.put("shape", "invhouse");
				else
					prop.put("shape", "diamond");

				Collection<String> contexts = hg.getContextsByEdge(edge);
				String color= "";
				for (String context: contexts){
					if (color.length()>0)
						color +=":";
					color+= colors.getColor(context);
				}
				prop.put("color",color);
				//prop.put("color", colors.getColor(contexts.iterator().next()));

				if (contexts.size()>1){
					prop.put("penwidth", 5);					
				}
				prop.put("label","");

				for (Object key :prop.keySet()){
					params += String.format("%s=\"%s\" ", key, prop.get(key));
				}
				ret += String.format(" \"%s\" [ %s ];\n ", e, params);

				Integer output= edge.getOutput();
				ret += String.format(" \"%s\" -> \"%s\";\n ",  e ,output );
				Iterator<Integer> iter_input = edge.getInputs().iterator();
				while (iter_input.hasNext()){
					Integer input= iter_input.next();
					ret += String.format(" \"%s\" -> \"%s\";\n ",   input, e );
				}
			}

		}
/*
		ret += "{ rank=same; ";
		for(Integer leaf: hg.getAxioms()){
			String label = map_node_id.get(leaf);
			ret += String.format(" \"%s\"",  label );			
		}
		ret +="}\n";
		
		ret += "{ rank=same; ";
		for(Integer root: hg.getRoots()){
			String label = map_node_id.get(root);
			ret += String.format(" \"%s\"",  label );
		}
*/		
		ret +="}\n";
		ret +="\n}\n";
		return ret;
	}

	
}

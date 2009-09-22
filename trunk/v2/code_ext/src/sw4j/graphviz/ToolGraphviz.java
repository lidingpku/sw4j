package sw4j.graphviz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.kohsuke.graphviz.Edge;
import org.kohsuke.graphviz.Graph;
import org.kohsuke.graphviz.Node;

import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.ToolSafe;

public class ToolGraphviz {
	/**
	 * export data using graphviz API format
	 * 
	 * @param reference  - the referenced hypergraph that record the provenance metadata for each hyperedge
	 * @return
	 */
	public static Graph data_export_graphvizAPI(DataHyperGraph hg, Map<Integer,String> map_node_id_input, Map<Integer,Properties> map_node_params, Map<DataHyperEdge,Properties> map_edge_params){
		//list all nodes
		Graph g= new Graph();

		g.attr("rankdir","BT");
		Set<Integer> nodes = hg.getNodes();

		//prepare map_node_id
		HashMap <Integer,Node> map_node_id = new HashMap<Integer,Node> ();
		for(Integer node: nodes){
			String id = null;
			if (null!= map_node_id_input)
				id = map_node_id_input.get(node);
			if (null==id)
				id="x_"+node;
			Node node1= new Node();
			node1.id(id);
			map_node_id.put(node,node1);
		}

		{
			//print nodes
			for(Integer node: nodes){

				Node label = map_node_id.get(node);
				if (null!=map_node_params){
					Properties prop= map_node_params.get(node);
					if (ToolSafe.isEmpty(prop)){
						prop= new Properties();
					}
					prop.put("shape", "box");

					for (Object key :prop.keySet()){
						label.attr(key.toString(),prop.get(key).toString());
					}	
				}
				g.node(label);	
			}
		}
		{
			//print arcs
			Iterator<DataHyperEdge> iter= hg.getEdges().iterator();
			int edgeid=1;
			Node label;
			while (iter.hasNext()){
				DataHyperEdge edge = iter.next();

				String e = "a_"+edgeid;
				edgeid++;
				Node node2= new Node();
				node2.id(e);

				if (null!=map_edge_params){
					Properties prop= map_edge_params.get(edge);
					if (ToolSafe.isEmpty(prop)){
						prop= new Properties();
					}
					prop.put("shape", "diamond");
					for (Object key :prop.keySet()){
						node2.attr(key.toString(),prop.get(key).toString());
					}
				}
				g.node(node2);


				Integer output= edge.getOutput();
				label = map_node_id.get(output);
				Edge edge1= new Edge(label, node2);
				g.edge(edge1);
				Iterator<Integer> iter_input = edge.getInputs().iterator();
				while (iter_input.hasNext()){
					Integer input= iter_input.next();
					label = map_node_id.get(input);
					Edge edge2= new Edge(node2, label);
					g.edge(edge2);
				}
			}

		}

		//		g.attr("rank", "same");
		//		for(Integer leaf: this.getAxioms()){
		//			String label = map_node_id.get(leaf);
		//			ret += String.format(" \"%s\"",  label );			
		//		}

		//		Node node1= new Node();
		//		node1.id("I");
		//		Node node2= new Node();
		//		node2.id("you");
		//		node1.attr("shape", "diamond");
		//		node2.attr("shape","box");
		//		Edge edge= new Edge(node1,node2);
		//		edge.attr("label", "love");
		//		g.node(node1);
		//		g.node(node2);
		//		g.edge(edge);
		return g;
	}


	public static void main(String[] args) throws IOException{

		DataHyperGraph g= new DataHyperGraph();
		Graph g1= data_export_graphvizAPI(g, null,null,null);
		File f=new File("docs/test.dot");
		FileOutputStream fop=new FileOutputStream(f);
		g1.writeTo(fop);

	}
}

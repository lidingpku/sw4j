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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;




/**
 * Implementation of AO* search
 * traverse a hypergraph (aka AND/OR graph) and report solutions 
 * search space is pruned by simple optimization criteria
 * 
 * @author Li Ding
 *
 */
public class AgentHyperGraphAoStar extends AgentHyperGraphTraverse{
	
	@Override
	public String getLabel(){
		return "ao";
	}

	HashMap<DataHyperEdge, Integer> m_map_edge_weight = new HashMap<DataHyperEdge, Integer>();
	
	/**
	 * find the subgraph starting from the hyperedge
	 * @param edge
	 * @return
	 */
/*	public static HashMap<DataHyperEdge,Integer> getEdgeWeightMap(DataHyperGraph g, boolean bInf) {
		HashMap<DataHyperEdge,Integer> map_edge_weight = new HashMap<DataHyperEdge,Integer>();
		if (bInf){
			//assign id to hyperedge
			Set<DataHyperEdge> set_edge = g.m_map_edge_context.keySet();
			DataHyperEdge[] ary_edge= new DataHyperEdge[set_edge.size()];
			HashMap<DataHyperEdge,Integer> map_edge_id = new HashMap<DataHyperEdge,Integer>();
			int i =0;
			for (DataHyperEdge edge: set_edge){
				ary_edge[i]=edge;
				map_edge_id.put(edge,i);
				i++;
			}
			
			//create a digraph for hyperedge dependency
			DataDigraph dag = new DataDigraph(ary_edge.length);
			for (DataHyperEdge from: ary_edge ){
				Integer id_from = map_edge_id.get(from);
				if (null==id_from)
					System.out.println("from");
				dag.add(id_from, id_from);
				
				for (Integer input: from.getInputs()){
					for (DataHyperEdge to: g.getEdgesByOutput(input)){
						int id_to= map_edge_id.get(to);
						dag.add(id_from, id_to);
					}
				}
			}
			
			//compute tc
			DataDigraph tc = dag.create_tc();
			
			//use tc to sum the weight to edge
			for (Integer id_from: tc.getFrom()){
				DataHyperEdge from = ary_edge[id_from];
				int sum=0;
				for (Integer id_to: tc.getTo(id_from)){
					DataHyperEdge to = ary_edge[id_to];
					sum += to.getWeight();
				}
				map_edge_weight.put(from, sum);
			}
		}else{
			for (DataHyperEdge edge: g.m_map_edge_context.keySet()){
				map_edge_weight.put(edge, edge.getWeight());
			}
		}
		
		return map_edge_weight;
	}	
*/
	/**
	 * find the subgraph starting from the hyperedge
	 * @param edge
	 * @return
	 */
	public static HashMap<DataHyperEdge,Integer> getEdgeWeightMap2(DataHyperGraph g, boolean bInf) {
		HashMap<DataHyperEdge,Integer> map_edge_weight = new HashMap<DataHyperEdge,Integer>();
		HashMap<Integer,Integer> map_vertex_weight = new HashMap<Integer,Integer>();
		if (bInf){
			//iteratively, assign weight for all vertex
			int nChanged  ;
			do{
				//init
				nChanged = 0;
				
				// get edge weight
				for (DataHyperEdge edge: g.getEdges()){
					//skip processed 
					if (map_edge_weight.keySet().contains(edge))
						continue;
					
					boolean bProcessed = true;
					int total_weight = edge.getWeight();
					if (edge.isLeaf()){
					}else{
						for (Integer input: edge.getInputs()){
							//if (input == edge.getOutput())
							//	continue;
							
							Integer weight = map_vertex_weight.get(input);
							if (null==weight){
								bProcessed =false;
								break;
							}else{
								total_weight += weight;
							}
						}
					}
					
					if (bProcessed){
						map_edge_weight.put(edge, total_weight);
						nChanged ++;
					}
				}

				// get vertex weight
				for (Integer vertex: g.getVertices()){
					//skip processed 
					if (map_vertex_weight.keySet().contains(vertex))
						continue;

					boolean bProcessed = true;
					int minimal_weight = 0;
					
					for (DataHyperEdge edge: g.getEdgesByOutput(vertex)){
						Integer weight = map_edge_weight.get(edge);
						
						if (edge.getInputs().contains(vertex)){
							//handle loop
							//no operation
						}else if (null==weight){
							bProcessed =false;
							break;
						}else {
							if (0==minimal_weight)
								minimal_weight = weight;
							else
								minimal_weight = Math.min( weight, minimal_weight) ;
						}
					}
					
					if (bProcessed){
						map_vertex_weight.put(vertex, minimal_weight);
						nChanged ++;
					}
				}
			}while (nChanged>0);		
	
		}else{
			for (DataHyperEdge edge: g.m_map_edge_context.keySet()){
				map_edge_weight.put(edge, edge.getWeight());
			}
		}

//debug
//System.out.println(g.getEdges().size());
//System.out.println(map_edge_weight.size());
//System.out.println(map_edge_weight);

//System.out.println(g.getVertices().size());
//System.out.println(map_vertex_weight.size());
//System.out.println(map_vertex_weight);

		
		return map_edge_weight;
	}	
	
	@Override
	protected void doTraverseBefore(DataHyperGraph G, Integer v){
		super.doTraverseBefore(G, v);
		
		m_map_edge_weight = getEdgeWeightMap2(G,true);
		/*
		//generate estimated the best edges for each vertex
		for (DataHyperGraph g: G.getSubHyperGraphs().values()){
			HashMap<DataHyperEdge,Integer> map_edge_weight = getEdgeWeightMap2(g,true);
			
			for (DataHyperEdge edge : map_edge_weight.keySet()){
				int weight = map_edge_weight.get(edge);
				Integer best_weight = m_map_edge_weight.get(edge);
				if (null==best_weight || best_weight> weight){
					m_map_edge_weight.put(edge, weight);
				}
			}
		}	
		*/
	}
	
	
	
	@Override
	protected Collection<DataHyperEdge> doNextEdgesReorder(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		ArrayList<DataHyperEdge> new_next = new ArrayList<DataHyperEdge>();

		//System.out.println(Gx.getEdges());
		// greedy, only use the best edge 
		int best_weight = -1;
		for (DataHyperEdge edge: edges){
			Integer weight = m_map_edge_weight.get(edge);
			if (best_weight ==-1 || weight<best_weight){
				new_next.clear();
				best_weight=weight;
			}
			
			if (best_weight==weight){
				new_next.add(edge);
			}
		}
		
		return new_next;
	}



	@Override
	protected int doCheckSolutionBest(DataHyperGraph Gx) {
		int ret =super.doCheckSolutionBest(Gx);
		if (ret==-1){
			this.m_bMustStop=true;
		}
		return ret;
	}
	
	
	
}

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
	

	
	@Override
	protected void doTraverseBefore(DataHyperGraph G, Integer v){
		super.doTraverseBefore(G, v);
		
		
		//generate estimated the best edges for each vertex
		for (DataHyperGraph g: G.getSubHyperGraphs().values()){
			HashMap<DataHyperEdge,Integer> map_edge_weight = g.getEdgeWeightMap(true);
			
			for (DataHyperEdge edge : map_edge_weight.keySet()){
				int weight = map_edge_weight.get(edge);
				Integer best_weight = m_map_edge_weight.get(edge);
				if (null==best_weight || best_weight> weight){
					m_map_edge_weight.put(edge, weight);
				}
			}
		}	
		//System.out.println(m_map_edge_weight);
	}
	
	
	
	@Override
	protected Collection<DataHyperEdge> doNextEdgesReorder(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		ArrayList<DataHyperEdge> new_next = new ArrayList<DataHyperEdge>();

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

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



/**
 * traverse a hypergraph (aka AND/OR graph) and report solutions 
 * search space is pruned by simple optimization criteria
 * 
 * @author Li Ding
 *
 */
public class AgentHyperGraphOptimize extends AgentHyperGraphTraverse{
	
	@Override
	public String getLabel(){
		return "best";
	}
	
	HashSet<Integer> m_runtime_preferred_vertex = new HashSet<Integer>();




	/**
	 * predict the minimum total weight
	 * 
	 * @param g
	 * @param Vx
	 * @return
	 */
	public int predictTotalWeight(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		// we need to include the founded hyperedges and at least |Vx| more hyperedges
		return Gx.getTotalWeight() + Vx.size();
	}
	
	@Override
	protected void doTraverseBefore(DataHyperGraph G, Integer v){
		doInit();
		
		//initiate best weight and preferred vertex
		Map<String,DataHyperGraph> map_graph_context = G.getSubHyperGraphs();
		Iterator<DataHyperGraph> iter = map_graph_context.values().iterator();
		int best_weight = -1;
		DataHyperGraph best_g = null;
		while (iter.hasNext()){
			DataHyperGraph g = iter.next();
			
			if (!isSolution(v, new HashSet<Integer>(), g))
				continue;
						
			int weight = g.getTotalWeight();
			
			if (null==best_g || best_weight > weight){
				best_g =g;
				best_weight = weight;
			}
		}
		
		if (null!=best_g)
			this.m_runtime_preferred_vertex.addAll(best_g.getVertices());
	}
	
	@Override
	public boolean doCheckSolution(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		if (isSolution(v,Vx,Gx)){	

			int ret = doCheckSolutionBest(Gx);
			if (1==ret){
				// update best weight if better weight found
				this.doResetSolution();
			}
			
			if (0<=ret){
				// update best weight if better weight found
				this.doSaveSolution(Gx);
				//update preferred vertex
				this.m_runtime_preferred_vertex.addAll(Gx.getVerticesOutput());
			}

			return true;
		}else{
			return false;			
		}		
	}
	
	
	@Override
	protected boolean isSolutionDiscardable(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		int weight = predictTotalWeight(G, Vx, Gx);

		if (debug){
			log(String.format("edges %d, vertices %d, solutions %d, weight %d, best_q %d. todo %s ", 
					Gx.getEdges().size(), 
					Gx.getVertices().size(),
					this.getResultSolutionFoundCount(),
					weight,
					m_runtime_best_weight,
					Vx.toString()));
		}

		
		// skip worse weight 
		if (m_runtime_best_weight!=-1 && weight > m_runtime_best_weight){
			return true;
		}
		
		return false;
	}
	
	
	@Override
	protected Integer doSelectNextVertex(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){

		if (Vx.isEmpty())
			return null;
		
		// simply pick the edges associated with the first to-visit vertex.
		Integer vh = Vx.iterator().next();
		
		// update vh if it is preferred
		HashSet<Integer> temp = new HashSet<Integer> (Vx);
		temp.retainAll(this.m_runtime_preferred_vertex);
		if (!temp.isEmpty()){
			vh= temp.iterator().next();
		}
		
		return vh;
	}
	
	@Override
	protected Collection<DataHyperEdge> doNextEdgesReorder(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		ArrayList<DataHyperEdge> new_next = new ArrayList<DataHyperEdge>();

		Iterator<DataHyperEdge> iter =edges.iterator();
		while (iter.hasNext()){
			DataHyperEdge e = iter.next();
			
			if (m_runtime_preferred_vertex.containsAll(e.getInputs()))
				new_next.add(0, e);
			else
				new_next.add(e);
		}		
		
		return new_next;
	}
	
	@Override
	public String getResultSummary(DataHyperGraph G) {
		
		String ret = String.format("%s | best-total: %d best-weight: %d" ,
				super.getResultSummary(G),
				this.m_runtime_solutions.size(),
				this.m_runtime_best_weight);
			
		return ret;
	}
}

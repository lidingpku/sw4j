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
	

	
	HashSet<Integer> m_runtime_preferred_vertex = new HashSet<Integer>();


	public int m_runtime_solution_count_best = 0;

	/**
	 * quality of optimal solutions, better quality has lower integer value
	 */
	public int m_runtime_best_quality = -1;

	/**
	 * g function - measure the cost of current solution
	 * @param g
	 * @return
	 */
	public int getQuality(DataHyperGraph g){
		return g.getEdges().size();
	}
	
	/**
	 * predict the minimum total quality
	 * 
	 * @param g
	 * @param Vx
	 * @return
	 */
	public int predictTotalQuality(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		// we need to include the founded hyperedges and at least |Vx| more hyperedges
		return getQuality(Gx) + Vx.size();
	}
	
	@Override
	protected void before_traverse(DataHyperGraph G, Integer v){
		super.before_traverse(G, v);
		
		//initiate best quality and preferred vertex
		Map<String,DataHyperGraph> map_graph_context = G.getSubHyperGraphs();
		Iterator<DataHyperGraph> iter = map_graph_context.values().iterator();
		int best_quality = -1;
		DataHyperGraph best_g = null;
		while (iter.hasNext()){
			DataHyperGraph g = iter.next();
			
			if (!isSolution(v, new HashSet<Integer>(), g))
				continue;
						
			int quality = getQuality(g);
			
			if (null==best_g || best_quality > quality){
				best_g =g;
				best_quality = quality;
			}
		}
		
		if (null!=best_g)
			this.m_runtime_preferred_vertex.addAll(best_g.getVertices());
	}
	
	@Override
	public boolean checkSolution(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		if (super.checkSolution(G, v, Vx, Gx)){
			int quality = getQuality(Gx);

			// init m_query_best_result, just do it one time
			if (-1 == m_runtime_best_quality){
				m_runtime_best_quality = quality;
				m_runtime_solution_count_best =0;
			}

			// update best quality if better quality found
			if (m_runtime_best_quality > quality){
				
				//rest
				m_runtime_solutions.clear();
				m_runtime_best_quality = quality;
				m_runtime_solution_count_best =0;
				
				m_runtime_preferred_vertex.clear();

				m_runtime_timer_start = System.currentTimeMillis();
			}	

			// check if the solution is the best
			if (quality > m_runtime_best_quality){
				// no
				
				// only keep the best solutions
				this.m_runtime_solutions.remove(Gx);
			}else{
				//yes
				
				//increment counter
				m_runtime_solution_count_best ++;
				
				//update preferred vertex
				this.m_runtime_preferred_vertex.addAll(Gx.getSinks());
			}
			
			return true;
		}else{
			return false;			
		}		
	}
	
	
	@Override
	protected boolean canDiscard(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		int quality = predictTotalQuality(G, Vx, Gx);

		if (debug){
			log(String.format("edges %d, vertices %d, solutions %d, quality %d, best_q %d. todo %s ", 
					Gx.getEdges().size(), 
					Gx.getVertices().size(),
					this.m_runtime_solution_count,
					quality,
					m_runtime_best_quality,
					Vx.toString()));
		}

		
		// skip worse quality 
		if (m_runtime_best_quality!=-1 && quality > m_runtime_best_quality){
			return true;
		}
		
		return false;
	}
	
	
	@Override
	protected Integer select_next_vertex(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){

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
	protected Collection<DataHyperEdge> reorder_next_edges(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		ArrayList<DataHyperEdge> new_next = new ArrayList<DataHyperEdge>();

		Iterator<DataHyperEdge> iter =edges.iterator();
		while (iter.hasNext()){
			DataHyperEdge e = iter.next();
			
			if (m_runtime_preferred_vertex.containsAll(e.getSources()))
				new_next.add(0, e);
			else
				new_next.add(e);
		}		
		
		return new_next;
	}
	
	@Override
	public String getSummary(DataHyperGraph G) {
		
		String ret = String.format("%s | best-total: %d best-quality: %d" ,
				super.getSummary(G),
				this.m_runtime_solution_count_best,
				this.m_runtime_best_quality);
			
		return ret;
	}
}

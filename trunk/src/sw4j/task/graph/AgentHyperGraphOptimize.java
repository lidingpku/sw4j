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
import java.util.Set;

import sw4j.util.DataCachedObjectMap;



/**
 * traverse a hypergraph (aka AND/OR graph) and report solutions 
 * search space is pruned by simple optimization criteria
 * 
 * @author Li Ding
 *
 */
public class AgentHyperGraphOptimize extends AgentHyperGraphTraverse{
	

	public ArrayList<DataHyperGraph> m_best_results = new ArrayList<DataHyperGraph>();
	public int max_results = -1;
	
	public DataCachedObjectMap<Integer,Integer> m_cache_bad_branch = new DataCachedObjectMap<Integer,Integer>();
	
	/**
	 * quality of optimal solutions, better quality has lower integer value
	 */
	public int m_best_result_quality = -1;

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
	public boolean isSolution(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		if (super.isSolution(G, v, Vx, Gx)){
			int quality = getQuality(Gx);

			// init m_query_best_result, just do it one time
			if (-1 == m_best_result_quality){
				m_best_result_quality = quality;
			}

			// update best quality if better quality found
			if (m_best_result_quality > quality){
				m_best_results.clear();
				m_best_result_quality = quality;
			}	


			//a best quality result
			if ( (max_results==-1 || max_results >m_best_results.size()) && quality==m_best_result_quality)
				m_best_results.add(Gx);
			
			return true;
		}else{
			return false;			
		}		
	}
	
	@Override
	protected boolean canDiscard(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		int quality = predictTotalQuality(G, Vx, Gx);

		// skip worse quality 
		if (m_best_result_quality!=-1 && quality > m_best_result_quality){
			return true;
		}
		
		return false;
	}
	
	
	@Override
	public String getSummary(DataHyperGraph G) {
		String ret = String.format("found total: %d\n" +
				"best total: %d\n" +
				"best quality: %d" ,
				this.m_count_found,
				this.m_best_results.size(),
				this.m_best_result_quality);
			
		return ret;
	}
}

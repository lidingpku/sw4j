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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * traverse a hypergraph (aka AND/OR graph) and report solutions
 * psudo-code
 * 
 * search
 *   * if isGoal then return 
 *   * step = findNextStep
 *   * processNextStep
 * 
 * 
 * @author Li Ding
 *
 */
public class AgentHyperGraphTraverse {
	
	public static boolean debug = false;
	
	int m_config_solution_count_limit  = -1;
	int m_runtime_solution_count = 0;
	
	long m_config_timer_limit  = -1;
	long m_runtime_process_start;
	long m_runtime_process_end;
	long m_runtime_timer_start;
	long m_runtime_timer_end;

	ArrayList<DataHyperGraph> m_runtime_solutions = new ArrayList<DataHyperGraph>();
	protected int m_config_solutions_limit = 1;
	
	HashSet<Integer> m_runtime_preferred_vertex = new HashSet<Integer>();

	
	boolean m_bMustStop=false;
	
	/**
	 * traverse hypergraph to find solution graph
	 * @param G
	 * @param v
	 */
	public void traverse(DataHyperGraph G, Integer v){
		traverse(G,v, -1, -1, 1);
		System.gc();
		System.gc();
		System.gc();
		System.out.println("free memory:" + Runtime.getRuntime().freeMemory());
	}	
	
	/**
	 * traverse hypergraph to find solution graph with a limit of returned result
	 * @param G
	 * @param v
	 * @param limit - total result to be returned (-1 means no limit)
	 * @return
	 */
	public void traverse(DataHyperGraph G, Integer v, int solution_count_limit, int timeout_limit, int solutions_limit){
		//reset
		m_runtime_solution_count =0;
		m_config_solution_count_limit = solution_count_limit;
		m_config_timer_limit = timeout_limit;
		m_config_solutions_limit= solutions_limit;
		
		//prepare to-visit set
		HashSet<Integer> Vx = new HashSet<Integer> ();
		Vx.add(v);

		m_runtime_process_start = System.currentTimeMillis();
		m_runtime_timer_start = m_runtime_process_start;
		
		do_traverse(G,v,Vx,new DataHyperGraph());
		
		m_runtime_process_end = System.currentTimeMillis();
	}



	public boolean isSolution(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		if (Vx.isEmpty() && Gx.isComplete() && Gx.isSingleRoot(v)){
			this.m_runtime_solution_count ++;

			//add some preferred vertex
			m_runtime_preferred_vertex.addAll(Gx.getSinks());
			
			//if no better answers can be found in 5 minutes, stop search and return the current best
			m_runtime_timer_end = System.currentTimeMillis();
			if (isAboveLimitTimeout())
				m_bMustStop = true;

			//if solution limit has been reached
			if (isAboveLimitSolutionCount())
				m_bMustStop = true;
			
			if (!isAboveLimitSolutions())
				this.m_runtime_solutions.add(Gx);

			if (debug)
			{
				System.out.println(Gx.data_export(G));
			}
			return true;
		}else{
			return false;			
		}
	}


	public boolean isMustStop(){
		return m_bMustStop;
	}
	
	/**
	 * traverse concise subgraphs for a hypergraph starting from a given vertex v.
	 * 
	 * @param G		the given hypergraph
	 * @param v		the given starting vertex
	 * @param Vx	the vertices to be justified
	 * @param Gx	the current path, i.e. concise selection of hyperedge
	 * @return	if some solution was found
	 */
	protected boolean do_traverse(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){

			
		// check if g is not meeting other criteria
		if (canDiscard(G, Vx, Gx))
			return false;

		// check the hyperedge selection
		if (isSolution(G, v, Vx, Gx))
			return true;

		// if there are no more vertices to be investigate
		if (Vx.isEmpty())
			return false;

		// getNextStep
		Iterator<DataHyperEdge> iter = getNextStep(G,v,Vx,Gx).iterator();

		// try each hyperedge
		// in case iter is empty, that mean vh is not justified by any hyper edge
		boolean bRet = false;
		while (iter.hasNext()){
			DataHyperEdge g = iter.next();

			if (isMustStop())
				return false;

			// prepare new to-visit vertex
			HashSet<Integer> new_vx = new HashSet<Integer> ();
			new_vx.addAll(Vx);
			new_vx.addAll(g.getSources());
			new_vx.removeAll(Gx.getSinks());
			new_vx.remove(g.getSink());

			// no need to track provenance in intermediate result
			DataHyperGraph new_gx = new DataHyperGraph(Gx);
			new_gx.add(g);
			
			bRet |= do_traverse(G, v, new_vx, new_gx);
		}
		
		
		return bRet;
	}
	
	protected List<DataHyperEdge> getNextStep(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		ArrayList<DataHyperEdge> new_next = new ArrayList<DataHyperEdge>();

		if (Vx.isEmpty())
			return new_next;
		
		// simply pick the edges associated with the first to-visit vertex.
		Integer vh = Vx.iterator().next();
		HashSet<Integer> temp = new HashSet<Integer> (Vx);
		temp.retainAll(this.m_runtime_preferred_vertex);
		if (!temp.isEmpty()){
			vh= temp.iterator().next();
		}
		
		//filter the alternative edges
		DataDigraph tc = Gx.getDigraph();

		Iterator<DataHyperEdge> iter =G.m_map_sink_edge.getValues(vh).iterator();
		while (iter.hasNext()){
			DataHyperEdge g = iter.next();

			// skip if g is definitely causing incomplete linkedGraph
			if (!G.getSinks().containsAll(g.getSources())){
				continue;
			}

			// avoid cycle
			if (Vx.size()==1 && tc.isReachable(g.getSources(), g.getSink())){
				continue;
			}

			new_next.add(g);
		}		
		
		return new_next;
	}
	
	protected boolean canDiscard(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		if (debug){
			println_sys(String.format("edges %d, vertices %d, solutions %d. todo %s ", 
					Gx.getEdges().size(), 
					Gx.getVertices().size(),
					this.m_runtime_solution_count,
					Vx.toString()));
		}
		
		return false;
	}

	public String getSummary(DataHyperGraph G) {
		return String.format("found total: %d", this.m_runtime_solution_count);
	}
	
	
	protected void println_sys(String szText){
		System.out.println("[T:"+(System.currentTimeMillis()-this.m_runtime_process_start)/1000+"] "+szText);
	}
	
	public boolean isAboveLimitTimeout(){
		return (-1 != m_config_timer_limit && (m_runtime_timer_end - m_runtime_timer_start)>m_config_timer_limit);
	}

	public boolean isAboveLimitSolutionCount(){
		return -1 != m_config_solution_count_limit &&  m_runtime_solution_count> m_config_solution_count_limit;
	}

	public boolean isAboveLimitSolutions(){
		return -1 != m_config_solution_count_limit &&  m_runtime_solutions.size() >m_config_solutions_limit;
	}
		
	public List<DataHyperGraph> getSolutions(){
		return this.m_runtime_solutions;
	}

	public int getSolutionCount() {
		return this.m_runtime_solution_count;
	}
	
	public double getProcessSeconds(){
		return (this.m_runtime_process_end- this.m_runtime_process_start)/1000.0;
	}
}

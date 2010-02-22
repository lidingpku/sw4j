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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;

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
	
	public String getLabel(){
		return "enum";
	}

	// threshold for how many seconds can be used between two effective solutions
	long m_config_timer_limit  = -1;
	long m_runtime_timer_start;
	long m_runtime_timer_end;
	protected boolean isAboveLimitTimeout(){
		return (-1 != m_config_timer_limit && (m_runtime_timer_end - m_runtime_timer_start)>= m_config_timer_limit);
	}

	// how many seconds have been used 
	long m_runtime_process_start;
	long m_runtime_process_end;
	/**
	 * return the seconds used by this process
	 * @return
	 */
	public double getResultProcessSeconds(){
		return (this.m_runtime_process_end- this.m_runtime_process_start)/1000.0;
	}

	
	// threshold for how many solutions have been seen
	int m_config_solution_found_limit  = -1;
	int m_runtime_solution_found_count = 0;
	protected boolean isAboveLimitSolutionCount(){
		return -1 != m_config_solution_found_limit &&  m_runtime_solution_found_count>= m_config_solution_found_limit;
	}

	/**
	 * return how many solutions have been found
	 * @return
	 */
	public int getResultSolutionFoundCount() {
		return this.m_runtime_solution_found_count;
	}
	
	
	// record samples of found solutions
	int m_config_solutions_saved_limit = 1;
	ArrayList<DataHyperGraph> m_runtime_solutions = new ArrayList<DataHyperGraph>();

	protected void doSaveSolution(DataHyperGraph g){
		if (this.isMustStop())
			return ;

		this.m_runtime_solution_found_count ++;
		if (-1 == m_config_solutions_saved_limit ||  m_runtime_solutions.size() < m_config_solutions_saved_limit){
			m_runtime_solutions.add(g);
			
			//reset time
			m_runtime_timer_start = System.currentTimeMillis();
		}
	}
	
	protected void doResetSolution(){
		m_runtime_solutions.clear();
	}
	
	
	// file based run time control to stop the process
	String m_szFileName_runtime = null;


	/**
	 * return a list of recorded solutions
	 * @return
	 */
	public List<DataHyperGraph> getResultSolutions(){
		return this.m_runtime_solutions;
	}

	
	/**
	 * traverse hypergraph to find solution graph
	 * @param G
	 * @param v
	 */
	public void traverse(DataHyperGraph G, Integer v){
		traverse(G, v, -1, -1, 1);
	}	
	
	/**
	 * traverse hypergraph to find solution graph with a limit of returned result
	 * @param G
	 * @param v
	 * @param limit - total result to be returned (-1 means no limit)
	 * @return
	 */
	public void traverse(DataHyperGraph G, Integer v, int solution_found_limit, int timeout_limit, int solutions_saved_limit){
		doTraverseBefore(G,v);
		
		//additional init
		m_config_solution_found_limit = solution_found_limit;
		m_config_timer_limit = timeout_limit;
		m_config_solutions_saved_limit= solutions_saved_limit;
		
		HashSet<Integer> Vx = new HashSet<Integer> ();
		Vx.add(v);

		// run process
		doTraverse(G, v, Vx, new DataHyperGraph());
		
		
		doTraverseAfter();
		
	}

	protected void doTraverseBefore(DataHyperGraph G, Integer v){
		//init
		doInit();
	}
	
	protected void doInit(){
		m_runtime_solution_found_count =0;

		m_runtime_process_start = System.currentTimeMillis();
		m_runtime_timer_start = m_runtime_process_start;
		
		// set run token file
		try {
			m_szFileName_runtime = "run-"+m_runtime_timer_start;
			ToolIO.pipeStringToFile(this.getClass().getName()+" remove file to stop",m_szFileName_runtime,false,false);
		} catch (Sw4jException e) {
			e.printStackTrace();
		}		
	}

	protected void doTraverseAfter(){
		//record when finished
		m_runtime_process_end = System.currentTimeMillis();

		// remove run token file
		File f=null;
		if (null!= m_szFileName_runtime && (f=new File(m_szFileName_runtime)).exists()){
			f.delete();
		}

		System.gc();
		System.gc();
		//System.out.println("free memory:" + Runtime.getRuntime().freeMemory());
		
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
	protected boolean doTraverse(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		// check if g is not meeting other criteria
		if (isSolutionDiscardable(G, Vx, Gx))
			return false;

		// check the hyperedge selection
		if (doCheckSolution(G, v, Vx, Gx))
			return true;

		// stop if there are no more vertices
		if (Vx.isEmpty())
			return false;

		//select one vertex 
		Integer vh = doSelectNextVertex(G,v,Vx,Gx);
		
		if (null==vh)
			return false;
		

		//list the edge (filter, reorder...)
		Iterator<DataHyperEdge> iter = doNextEdgesReorder(G,v,Vx,Gx, 
										doNextEdgesFilter(G,v,Vx,Gx, 
												G.getEdgesByOutput(vh)) ).iterator();

		// try each edge,
		// in case iter is empty, that mean vh is not justified by any hyper edge
		boolean bRet = false;
		while (iter.hasNext()){
			DataHyperEdge g = iter.next();

			if (isMustStop())
				return false;

			// prepare new to-visit vertex
			HashSet<Integer> new_vx = new HashSet<Integer> ();
			new_vx.addAll(Vx);
			new_vx.addAll(g.getInputs());
			new_vx.removeAll(Gx.getVerticesOutput());
			new_vx.remove(g.getOutput());

			// no need to track provenance in intermediate result
			DataHyperGraph new_gx = new DataHyperGraph(Gx);
			new_gx.add(g);
			
			bRet |= doTraverse(G, v, new_vx, new_gx);
		}
		
		
		return bRet;
	}
	
	/**
	 * check if we can discard a search branch. Normal traverse will say "no" but further optimization can say yes.
	 * 
	 * @param G
	 * @param Vx
	 * @param Gx
	 * @return
	 */
	protected boolean isSolutionDiscardable(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		if (debug){
			log(String.format("edges %d, vertices %d, solutions %d. todo %s ", 
					Gx.getEdges().size(), 
					Gx.getVertices().size(),
					this.m_runtime_solution_found_count,
					Vx.toString()));
		}
		
		return false;
	}
	
	/**
	 * weight of the best solution, better quality has lower integer value
	 */
	public int m_runtime_best_weight= -1;
	public DataHyperGraph m_runtime_best_subgraph = null;
	public int getResultBestWeight(){
		return m_runtime_best_weight;
	}

	protected boolean isSolution(Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		boolean bRet = (Vx.isEmpty() && Gx.isComplete() && Gx.isSingleRoot(v) && Gx.isAcyclic());
		
		return bRet;
	}
	
	protected int doCheckSolutionBest(DataHyperGraph Gx){
		int weight= Gx.getTotalWeight();
		if (m_runtime_best_weight== -1 || weight<m_runtime_best_weight){
			m_runtime_best_weight=weight;
			m_runtime_best_subgraph=Gx;
			return 1;
		}else if(weight == m_runtime_best_weight){
			return 0;
		}else{
			return -1;
		}
	}
	protected boolean doCheckSolution(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		if (isSolution(v,Vx,Gx)){	

			doCheckSolutionBest(Gx);
			
			
			doSaveSolution(Gx);

			if (debug){
				System.out.println(Gx.data_summary());
			}
			return true;
		}else{
			return false;			
		}
	}


	protected boolean isMustStop(){
		//stop if two many solutions were found
		if (isAboveLimitSolutionCount())
			return true;

		//stop if no better answers can be found in 5 minutes
		m_runtime_timer_end = System.currentTimeMillis();
		if (isAboveLimitTimeout())
			return  true;

		//stop if the run token file is removed by user
		if (null!= m_szFileName_runtime && !new File(m_szFileName_runtime).exists())
			return  true;

		if (m_bMustStop)
			return true;
		
		return false;	
	}
	
	protected boolean m_bMustStop=false;
	

	protected Integer doSelectNextVertex(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		// simply pick the edges associated with the first to-visit vertex.
		if (!Vx.isEmpty())
			return Vx.iterator().next();
		else
			return null;
	}
	
	protected Collection<DataHyperEdge> doNextEdgesFilter(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		ArrayList<DataHyperEdge> new_next = new ArrayList<DataHyperEdge>();

		//generate a directed graph 
		DataDigraph adj = Gx.getDigraph();

		Iterator<DataHyperEdge> iter =edges.iterator();
		while (iter.hasNext()){
			DataHyperEdge g = iter.next();
			
			// skip if g is definitely causing incomplete linkedGraph
			if (!G.getVerticesOutput().containsAll(g.getInputs())){
				continue;
			}

			// avoid cycle
			if (adj.isReachable(g.getInputs(), g.getOutput())){
				continue;
			}
			// avoid self-loop
			if (g.hasLoop()){
				continue;
			}

			//if (m_runtime_preferred_vertex.containsAll(g.getSources()))
			//	new_next.add(0, g);
			//else
			new_next.add(g);
		}		
		
		return new_next;
	}
	protected Collection<DataHyperEdge> doNextEdgesReorder(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		return edges;
	}	


	public String getResultSummary() {
		return getResultSummaryData().toString();
	}
	
	public DataSmartMap getResultSummaryData(){
		DataSmartMap data = new DataSmartMap();
		data.put("alg_solutions_best", this.getResultSolutions().size());
		data.put("alg_solutions_found", this.getResultSolutionFoundCount());
		data.put("alg_seconds_spent", this.getResultProcessSeconds());
		data.put("alg_weight_best", this.m_runtime_best_weight);
		return data;
	}
	
	
	protected void log(String szText){
		System.out.println("[T:"+(System.currentTimeMillis()-this.m_runtime_process_start)/1000+"] "+szText);
	}
	
}

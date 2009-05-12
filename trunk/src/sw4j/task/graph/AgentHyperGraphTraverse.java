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

import sw4j.util.ToolString;

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

	/**
	 * traverse hypergraph to find solution graph
	 * @param G
	 * @param v
	 */
	public void traverse(DataHyperGraph G, Integer v){
		traverse(G,v, -1);
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
	public void traverse(DataHyperGraph G, Integer v, int limit){
		//reset
		m_count_found =0;
		m_nLimit = limit;
		
		//prepare to-visit set
		HashSet<Integer> Vx = new HashSet<Integer> ();
		Vx.add(v);

		do_traverse(G,v,Vx,new DataHyperGraph());
	}

	int m_nLimit  = -1;
	int m_count_found = 0;
	


	public boolean isSolution(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		if (Vx.isEmpty() && Gx.isComplete() && Gx.isSingleRoot(v)){
			this.m_count_found ++;

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
		if (-1 == m_nLimit)
			return false;
		
		if (m_nLimit > m_count_found)
			return false;
		
		return true;
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
		if (debug){
			System.out.println(ToolString.printCollectionToString(Vx));
			System.out.println(Gx.data_summary());
		}
			
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
		Integer vh= Vx.iterator().next();
		Iterator<DataHyperEdge> iter =G.m_map_sink_edge.getValues(vh).iterator();
		
		DataDigraph tc = Gx.getDigraph();

		while (iter.hasNext()){
			DataHyperEdge g = iter.next();

			// skip if g is definitely causing incomplete linkedGraph
			if (!G.getSinks().containsAll(g.getSources())){
				continue;
			}

			// avoid cycle
			if (tc.isReachable(g.getSources(), g.getSink())){
				continue;
			}
			

			new_next.add(g);
		}		
		
		return new_next;
	}
	
	protected boolean canDiscard(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		return false;
	}

	public String getSummary(DataHyperGraph G) {
		return String.format("found total: %d", this.m_count_found);
	}
}

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
 * Data structure that encodes a directed graph
 * @author Li Ding
 * 
 */
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sw4j.util.DataPVHMap;

public class DataDigraph extends DataPVHMap<Integer, Integer> {

	/**
	 * make transitive closure without using self-reflective relation (v,v)
	 */
	public DataDigraph create_tc(){
		DataDigraph dd = new DataDigraph();
		dd.add(this);
		
		Iterator<Integer> iter = this.keySet().iterator();
		while (iter.hasNext()){
			Integer v = iter.next();
			do_dfs_tc(dd, v, v, new HashSet<Integer>());
		}
		return dd;
	}
	
	public static void do_dfs_tc(DataDigraph dd, Integer v, Integer sink, HashSet<Integer> visited){
		visited.add(sink);
		HashSet<Integer> temp = new HashSet<Integer>(dd.getValues(sink));
		temp.removeAll(visited);
		Iterator<Integer> iter = temp.iterator();
		while (iter.hasNext()){
			Integer source = iter.next();
			
			dd.add(v, source);
			do_dfs_tc(dd,v, source, visited);
		}
	}
	
	public boolean isReachable(Integer sink, Integer source){
		
		HashSet<Integer> visited = new HashSet<Integer> ();
		visited.add(sink);
		
		HashSet<Integer> goal = new HashSet<Integer> ();
		goal.add(source);
		
		return do_check_connected(visited, this.getValues(sink), goal);
		
	}

	private HashSet<Integer> getNext(Collection<Integer> from){
		HashSet<Integer> next = new HashSet<Integer> (); 
		Iterator<Integer> iter = from.iterator();
		while (iter.hasNext()){
			Integer node = iter.next();
			
			next.addAll(this.getValues(node));
		}
		return next;
	}
	
	private boolean do_check_connected(Collection<Integer> visited, Collection<Integer> visiting,  Set<Integer> goal){
		if (null==visited)
			visited = new HashSet<Integer> ();
		
		if (goal.removeAll(visiting))
			return true;
		
		if (null==visiting || visiting.isEmpty())
			return false;

		Collection<Integer> new_visiting = getNext(visiting);

		
		new_visiting.removeAll(visited);
		new_visiting.removeAll(visiting);
		visited.addAll(visiting);
		
		return do_check_connected(visited, new_visiting, goal);
	}

	public boolean isReachable(Set<Integer> sinks, Integer source){
		Set<Integer> visiting = sinks;
		
		HashSet<Integer> goal = new HashSet<Integer> ();
		goal.add(source);
		
		return do_check_connected(null, visiting, goal);
	}
	
	public boolean hasCycle(){
		Iterator<Integer> iter = this.keySet().iterator();
		while (iter.hasNext()){
			Integer sink = iter.next();
			if (isReachable(sink, sink))
				return true;
		}
		return false;		
	}
}

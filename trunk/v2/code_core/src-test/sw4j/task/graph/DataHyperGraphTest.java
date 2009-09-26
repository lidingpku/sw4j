package sw4j.task.graph;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolRandom;


public class DataHyperGraphTest {
	
	static boolean debug= false;

	@Test
	public  void test_random_gen(){
		System.out.println("======================================================================");
		System.out.println("Test Random Linked Graph Generation");
		DataHyperGraph.data_create_random(10,5,3,10);
	}
	
	@Test
	public  void test_export_import(){
		System.out.println("======================================================================");
		System.out.println("Test Import Export");
		DataHyperGraph lg = DataHyperGraph.data_create_random(10,5,3,10);
		
		DataHyperGraph lg1 = new DataHyperGraph();
		lg1.data_import(lg.data_export());

		if (!lg1.equals(lg)){
			System.out.println("failed");
			System.out.println(lg1.data_export());
			System.out.println("----");
			System.out.println(lg.data_export());
			fail();
		}else{
			System.out.println("succeed");
		}
	}	
	

	private void do_traverse(DataHyperGraph lg, Integer v, int total, int optimized_total, int best_total, int best_quality){
		{
			System.out.println();
			System.out.println("-------"+AgentHyperGraphTraverse.class.getSimpleName()+"-------");
			AgentHyperGraphTraverse alg = new AgentHyperGraphTraverse();
			AgentHyperGraphTraverse.debug= debug;
			do_traverse(lg, v, alg);

			if (-1!=total && alg.m_runtime_solution_count!=total){
				fail();
			}
		}
		
		{
			System.out.println();
			System.out.println("-------"+AgentHyperGraphOptimize.class.getSimpleName()+"-------");
			AgentHyperGraphOptimize alg = new AgentHyperGraphOptimize();
			AgentHyperGraphTraverse.debug= debug;
			do_traverse(lg, v, alg);
			
			if (-1!=best_total && alg.m_runtime_solution_count_best!=best_total){
				fail();
			}
			
			if (-1!=best_quality && alg.m_runtime_best_quality!=best_quality){
				fail();
			}

//			if (-1!=optimized_total && alg.m_runtime_solution_count!=optimized_total){
//				System.out.println(alg.m_runtime_solutions);
//				fail();
//			}

		}		
		
	}
	
	private void do_traverse(DataHyperGraph lg, Integer v, AgentHyperGraphTraverse alg){
		System.out.println("-------");
		System.out.println("1.  input ");
		System.out.println("starting from "+ v);
		if (debug)
			System.out.println(lg.data_export());

		
		System.out.println("-------");
		System.out.println("2.  traverse ");

		alg.traverse(lg, v,-1, -1, -1);
		
		System.out.println("-------");
		System.out.println("3. output summary ");
		System.out.println(alg.getSummary(lg));

	}

	@Test
	public void test_traverse_random(){
		System.out.println("======================================================================");
		System.out.println("test_traverse_random");
		DataHyperGraph lg = DataHyperGraph.data_create_random(20,10,5,10);
		Integer v = (ToolRandom.randomSelect(lg.getOutputs()));
		do_traverse(lg,v, -1, -1, -1, -1);		
	}
	
	
	@Test
	public void test_traverse_case2(){
		System.out.println("======================================================================");
		System.out.println("test_traverse_case2");
		DataHyperGraph lg = create_case2();
		debug=true;
		Integer v = new Integer(0);
		do_traverse(lg,v, 8, 6, 4, 7);

	}

	
	private static DataHyperGraph create_case2(){
		DataHyperGraph lg = new DataHyperGraph();

		DataHyperEdge g;
		{
			g = new DataHyperEdge(0);
			g.addInput(1);
			g.addInput(2);
			g.addInput(3);
			lg.add(g,"A");

			g = new DataHyperEdge(1);
			g.addInput(11);	//t
			lg.add(g,"A");

			g = new DataHyperEdge(1);
			g.addInput(10);	//f
			lg.add(g,"A");

			g = new DataHyperEdge(2);
			g.addInput(21);	//t
			lg.add(g,"A");

			g = new DataHyperEdge(2);
			g.addInput(20);	//f
			lg.add(g,"A");

			g = new DataHyperEdge(3);
			g.addInput(31);	//t
			lg.add(g,"A");

			g = new DataHyperEdge(3);
			g.addInput(30);	//f
			lg.add(g,"A");

			
			g = new DataHyperEdge(10);
			g.addInput(21);	//f
			g.addInput(31);	//f
			lg.add(g,"A");
			
			
			g = new DataHyperEdge(20);
			g.addInput(11);	//f
			g.addInput(31);	//f
			lg.add(g,"A");
			
			
			g = new DataHyperEdge(30);
			g.addInput(11);	//f
			g.addInput(21);	//f
			lg.add(g,"A");
			

			int [] axioms= new int []{
					11,21, 31,
			};
			for (int i=0; i<axioms.length; i++){
				g = new DataHyperEdge(axioms[i]);
				lg.add(g,"A");
			}
			
			
		}
		return lg;
	}
	
	
	
	@Test
	public void test_traverse_worstcase1(){
		System.out.println("======================================================================");
		System.out.println("test_traverse_worstcase1");
		DataHyperGraph lg = create_worstcase1(5);
		Integer v = new Integer(1);
		do_traverse(lg,v,16,2,1,5);		

	}

	
	private static DataHyperGraph create_worstcase1(int max){
		DataHyperGraph lg = new DataHyperGraph();

		Integer v0 = new Integer(0);
		for (int i=1; i<max; i++){
			Integer vi = new Integer(i);

			{
				DataHyperEdge g = new DataHyperEdge(vi);
				g.addInput( new Integer(i+1));
				lg.add(g);
			}
			{
				DataHyperEdge g = new DataHyperEdge(vi);
				g.addInput( new Integer(i+1));
				g.addInput( v0);
				lg.add(g);
			}
		}
		{
			Integer vi = new Integer(max);
			DataHyperEdge g = new DataHyperEdge(vi);
			lg.add(g);
		}
		{
			DataHyperEdge g = new DataHyperEdge(v0);
			lg.add(g);
		}
		return lg;
	}
	
	
	/**
Here is my rewrite of the example

Formula:
1. If it's winter, then it's raining.
      w => r
2. If you have out an umbrella, then it's raining.
      u => r
3. If it's raining, then you're singing.
      r => s
4, If it's winter, then you have out an umbrella.
      w => u
5. If it's raining and you have out an umbrella, then if it's raining
  you are singing.
      (r & u) = (r => s)
6. You have out an umbrella.
      u
7. It's winter.
      w
8. It's winter.
      r
11.  You're singing
      s


Conjecture:
  11

Proof A:
Axioms: 2,3,4,7
Steps:
7+4 => 6    
6+2 => 8   
8+3 => 11  

Proof B:
Axioms: 1,5,6,7
Steps:
1+7 => 8  
8+5 => 13    "u => (r => s)"
6+13 => 3   
8+3 => 11 
	 * @return
	 */
	@Test
	public void test_rain(){
		System.out.println("======================================================================");
		System.out.println("test_rain");
		DataHyperGraph[] lgs =create_test_rain();
		DataHyperGraph lg =lgs[2];
		Integer v = new Integer(11);
		do_traverse(lg,v,7,5,2,5);		
	}

	private static DataHyperGraph[] create_test_rain(){
		DataHyperGraph lg_a = new DataHyperGraph();
		DataHyperGraph lg_b = new DataHyperGraph();
		HashMap<Integer,String> map_id_label = new HashMap<Integer,String>();

		Integer v1 = new Integer(1);//, "w=>r" , "If it's winter, then it's raining ");
		map_id_label.put(v1, "w=>r");
		Integer v2 = new Integer(2);//, "u=>r" , "If you have out an umbrella, then it's raining.");
		map_id_label.put(v2, "u=>r");
		Integer v3 = new Integer(3);//, "r=>s" , "If it's raining, then you're singing.");
		map_id_label.put(v3, "r=>s");
		Integer v4 = new Integer(4);//, "w=>u" , "If it's winter, then you have out an umbrella.");
		map_id_label.put(v4, "w=>u");
		Integer v5 = new Integer(5);//, "(r & u) => (r=>s)" , "If it's raining and you have out an umbrella, then if it's raining you are singing.");
		map_id_label.put(v5, "(r & u) => (r=>s)");
		Integer v6 = new Integer(6);//, "u" , "You have out an umbrella.");
		map_id_label.put(v6, "u");
		Integer v7 = new Integer(7);//, "w" , "It's winter.");
		map_id_label.put(v7, "w");
		Integer v8 = new Integer(8);//, "r" , "It's raining.");
		map_id_label.put(v8, "r");
		Integer v9 = v6;
		Integer v10 = v8;
		Integer v11 = new Integer(11);//, "s" , "You're singing.");
		map_id_label.put(v11, "s");
		Integer v12 = v8;
		Integer v13 = new Integer(13);//, "u=>(r=>s)" , "If you have out an umbrella, then if it's raining, then you're singing.");
		map_id_label.put(v13, "u=>(r=>s)");
		Integer v14 = v3;
		Integer v15 =v11;
		
		DataHyperEdge g;
		{
			g = new DataHyperEdge(v4);
			lg_a.add(g,"A");
			g = new DataHyperEdge(v7);
			lg_a.add(g,"A");
			g = new DataHyperEdge(v2);
			lg_a.add(g,"A");
			g = new DataHyperEdge(v3);
			lg_a.add(g,"A");
			
			g = new DataHyperEdge(v9);
			g.addInput(v7);
			g.addInput(v4);
			lg_a.add(g,"A");

			g = new DataHyperEdge(v10);
			g.addInput(v9);
			g.addInput(v2);
			lg_a.add(g,"A");

			g = new DataHyperEdge(v11);
			g.addInput(v10);
			g.addInput(v3);
			lg_a.add(g,"A");
		}
		
		{
			g = new DataHyperEdge(v1);
			lg_b.add(g,"B");
			g = new DataHyperEdge(v7);
			lg_b.add(g,"B");
			g = new DataHyperEdge(v6);
			lg_b.add(g,"B");
			g = new DataHyperEdge(v5);
			lg_b.add(g,"B");

			g = new DataHyperEdge(v12);
			g.addInput(v1);
			g.addInput(v7);
			lg_b.add(g,"B");
			
			g = new DataHyperEdge(v13);
			g.addInput(v5);
			g.addInput(v12);
			lg_b.add(g,"B");

			g = new DataHyperEdge(v14);
			g.addInput(v13);
			g.addInput(v6);
			lg_b.add(g,"B");

			g = new DataHyperEdge(v15);
			g.addInput(v14);
			g.addInput(v12);
			lg_b.add(g,"B");

		}		
		
		DataHyperGraph lg = new DataHyperGraph();
		lg.add(lg_a);
		lg.add(lg_b);
		
		
		System.out.println("Proof-A");
		System.out.println(lg_a);
		System.out.println("Proof-B");
		System.out.println(lg_b);
		
		DataHyperGraph[] lgs = new DataHyperGraph[]{
				lg_a,
				lg_b,
				lg
		};
		
		return lgs;
	}
	
	
	
	/**
	 * 
	 */

	
	@Test
	public  void test_2_paulo(){
		System.out.println("======================================================================");
		System.out.println("test_2_paulo");
		DataHyperGraph[] lgs =create_test_2_paulo();
		DataHyperGraph lg =lgs[2];
		{
			Integer v = new Integer(1);
			do_traverse(lg,v,2,3,2,4);		
		}
		
		{
			Integer v = new Integer(11);
			do_traverse(lg,v,2,3,2,4);		
		}
	}

	private static DataHyperGraph[] create_test_2_paulo(){
		DataHyperGraph lg_a = new DataHyperGraph();
		DataHyperGraph lg_b = new DataHyperGraph();

		Integer v1 = new Integer(1);//, "di1" );
		Integer v2 = new Integer(2);//, "ci1" );
		Integer v3 = new Integer(3);//, "bi1" );
		Integer v4 = new Integer(4);//, "ai1");
		
		Integer v11 = new Integer(11);//, "di2" );
		Integer v12 = v2;
		Integer v13 = new Integer(13);//, "bi2" );
		Integer v14 = new Integer(14);//, "ai2");
		
		DataHyperEdge g;
		{
			g = new DataHyperEdge(v4);
			lg_a.add(g,"p1");
			
			g = new DataHyperEdge(v1);
			g.addInput(v2);
			lg_a.add(g,"p1");

			g = new DataHyperEdge(v2);
			g.addInput(v3);
			lg_a.add(g,"p1");

			g = new DataHyperEdge(v3);
			g.addInput(v4);
			lg_a.add(g,"p1");
		}
		
		{
			g = new DataHyperEdge(v14);
			lg_b.add(g,"p2");

			g = new DataHyperEdge(v11);
			g.addInput(v12);
			lg_b.add(g,"p2");
			
			g = new DataHyperEdge(v12);
			g.addInput(v13);
			lg_b.add(g,"p2");

			g = new DataHyperEdge(v13);
			g.addInput(v14);
			lg_b.add(g,"p2");

		}		
		
		DataHyperGraph lg = new DataHyperGraph();
		lg.add(lg_a);
		lg.add(lg_b);
		
		
		System.out.println("p1");
		System.out.println(lg_a);
		System.out.println("p2");
		System.out.println(lg_b);
		
		DataHyperGraph[] lgs = new DataHyperGraph[]{
				lg_a,
				lg_b,
				lg
		};
		
		return lgs;
	}
	
	@Test
	public  void test_lg1(){
		String szFilename = "files/hypergraph_test/lg1.txt";
		DataHyperGraph lg = new DataHyperGraph();
		try {
			String szGraph = ToolIO.pipeFileToString(szFilename); 
			System.out.println(szGraph);
			
			lg.data_import(szGraph);
			

			Iterator<Integer> iter = lg.getRoots().iterator();
			while (iter.hasNext()){
				Integer root = iter.next();
				do_traverse(lg,root,-1,-1,-1,-1);		
			}

			
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package sw4j.graphviz;

import static org.junit.Assert.fail;

import java.util.HashMap;


import org.junit.Test;

import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;


public class ToolGraphvizTest {
	
	static boolean debug= false;

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
		System.out.println(ToolGraphviz.data_export_graphvizAPI(lg1,null,null,null));
	}	
	
	@Test
	public  DataHyperGraph[] create_test_rain(){
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
		
		System.out.println(ToolGraphviz.data_export_graphvizAPI(lg,map_id_label, null, null));
		return lgs;
	}
	
}

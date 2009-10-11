package sw4j.task.graph;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;


public class DataHyperGraphTest {
	
	static boolean debug= false;
	
	//@Test
	public void gen_test(){
		this.create_test_search_aostar();
		this.create_test_search_case2();
		this.create_test_search_worstcase(5);
		this.create_test_search_paulo();
		this.create_test_search_rain();
	}
	@Test
	public  void test_random_many() throws IOException, Sw4jException{
		HashSet<String> set_filename = new HashSet<String>();
		System.out.println("======================================================================");
		System.out.println("Test Random test_random_many");
		{
			int i=20;
			for (int j=1; j<=3; j++){
				DataHyperGraph lg=null;
				do {
					lg=DataHyperGraph.data_create_random(i*2,i,3,-1);
				}while (lg.getRoots().size()==0);
				String filename= save_graph(String.format("z%03d_%02d",i,j),lg, true, "output/hypergraph/test_random_many/");
				set_filename.add(filename);
				
				do_search(filename);
			}
		}

	}
	//@Test
	public  void test_random_scale() throws IOException, Sw4jException{
		HashSet<String> set_filename = new HashSet<String>();
		System.out.println("======================================================================");
		System.out.println("Test Random test_random_scale");

		for (int i=8; i<1000; i*=2){
			for (int j=1; j<=2; j++){
				DataHyperGraph lg=null;
				do {
					lg=DataHyperGraph.data_create_random(i*3/2,i,3,-1);
				}while (lg.getRoots().size()==0);
				String filename= save_graph(String.format("%z03d_%02d",i,j),lg, false,"output/hypergraph/test_random_scale/");
				set_filename.add(filename);
				
				do_search(filename);

			}
		}

	}
	
	public static String save_graph(String problem, DataHyperGraph lg){
		return save_graph(problem, lg, true, "");
	}
	public static String save_graph(String problem, DataHyperGraph lg, boolean bGenerateGraph, String path){
		String sz_file_output_common= "files/hypergraph_test/test_"+problem;
		if (!ToolSafe.isEmpty(path)){
			sz_file_output_common= path+problem;
		}
		String sz_filename= sz_file_output_common+".csv";
		ToolIO.pipeStringToFile(lg.data_export(), new File(sz_filename));

		if (!bGenerateGraph)
			return sz_filename;
		
		try {
			File f_dot = new File(sz_file_output_common+".dot");
			String sz_content = lg.data_export_graphviz(String.format("label=%s",problem ));
			
			ToolIO.pipeStringToFile(sz_content, f_dot.getAbsolutePath(), false);

			String [] formats = new String[]{"png"};
//			String [] formats = new String[]{"svg","png"};
			for (String format :formats){
				File f_format = new File(sz_file_output_common+"."+format);
				String command = "dot  -T"+format+" -o"+ f_format.getAbsolutePath() +" "+ f_dot.getAbsolutePath() ;
				System.out.println("run command: "+command);
				Runtime.getRuntime().exec(command);
			}
		} catch (Sw4jException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sz_filename;
	}
	
	public static void save_diff(String problem, DataHyperGraph lg, DataHyperGraph lg_all){
		String sz_file_output_common= "output/hypergraph/diff_"+problem;

		try {
			File f_dot = new File(sz_file_output_common+".dot");
			String sz_subgraph = lg.data_export_graphviz_subgraph(String.format("label=%s",problem ));
			String sz_content = lg_all.data_export_graphviz(String.format("%s",sz_subgraph ));
			
			ToolIO.pipeStringToFile(sz_content, f_dot.getAbsolutePath(), false);

			String [] formats = new String[]{"png"};
//			String [] formats = new String[]{"svg","png"};
			for (String format :formats){
				File f_format = new File(sz_file_output_common+"."+format);
				String command = "dot  -T"+format+" -o"+ f_format.getAbsolutePath() +" "+ f_dot.getAbsolutePath() ;
				System.out.println("run command: "+command);
				Runtime.getRuntime().exec(command);
			}
		} catch (Sw4jException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	
	@Test
	public  void test2_export_import(){
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
	

	public static final String OPT_ROOT="root";

	public static final String OPT_ENUM="enum";
	public static final String OPT_ENUM_FOUND="enum found";
	public static final String OPT_ENUM_W="enum weight";
	public static final String OPT_BEST="best";
	public static final String OPT_BEST_FOUND="best found";
	public static final String OPT_BEST_W="best weight";
	public static final String OPT_AO="ao";
	public static final String OPT_AO_FOUND="ao found";
	public static final String OPT_AO_W="ao weight";
	
	@Test
	public  void test_search() throws Sw4jException, IOException{
		String [][] ary_problem= new String[][]{
				{	
					"search_paulo",
					"1",
					String.format("%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,",
							OPT_ENUM,2, OPT_ENUM_FOUND,2,  	OPT_ENUM_W,4, 
							OPT_BEST,2, OPT_BEST_FOUND,2,	OPT_BEST_W,4, 
							OPT_AO,2, 	OPT_AO_FOUND,2,	OPT_AO_W,4)
				},
				{	
					"search_rain",
					"0",
					String.format("%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,",
							OPT_ENUM,7, OPT_ENUM_FOUND,7,  	OPT_ENUM_W,5, 
							OPT_BEST,2, OPT_BEST_FOUND,2,	OPT_BEST_W,5, 
							OPT_AO,1, 	OPT_AO_FOUND,1,	OPT_AO_W,5)
				},
				{	
					"search_worst5",
					"1",
					String.format("%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,",
							OPT_ENUM,16, OPT_ENUM_FOUND,16,  	OPT_ENUM_W,5, 
							OPT_BEST,1, OPT_BEST_FOUND,16,	OPT_BEST_W,5, 
							OPT_AO,1, 	OPT_AO_FOUND,1,	OPT_AO_W,5)
				},
				{	
					"search_case2",
					"0",
					String.format("%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,",
							OPT_ENUM,8, OPT_ENUM_FOUND,8,  	OPT_ENUM_W,7, 
							OPT_BEST,4, OPT_BEST_FOUND,5,	OPT_BEST_W,7, 
							OPT_AO,1, 	OPT_AO_FOUND,1,	OPT_AO_W,7)
				},
				
				//this one AO start does not yield optimal solution
				{	
					"search_aostar",
					"0",
					String.format("%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,%s=%d,",
							OPT_ENUM,2, OPT_ENUM_FOUND,2,  	OPT_ENUM_W,4, 
							OPT_BEST,1, OPT_BEST_FOUND,2,	OPT_BEST_W,4, 
							OPT_AO,1, 	OPT_AO_FOUND,1,	OPT_AO_W,5)
				},
		};
		
		for (String problem[]: ary_problem){
			System.out.println("======================================================================");
			System.out.println("loading - "+problem[0]);
			DataHyperGraph lg = new DataHyperGraph();
			lg.data_import(ToolIO.pipeFileToString("files/hypergraph_test/test_"+problem[0]+".csv"));
			if (lg.getEdges().size()==0){
				fail(problem[0]);
			}
			do_search(lg, problem);
		}
		
	}	
	private void do_search(String filename) throws IOException, Sw4jException{
		File f= new File(filename);
		DataHyperGraph lg = new DataHyperGraph();
		lg.data_import(ToolIO.pipeFileToString(filename));
		if (lg.getEdges().size()==0){
			fail();
		}
		
		for (int root:lg.getRoots()){
			String []problem = new String[]{
					f.getName().substring(0,f.getName().lastIndexOf("."))+"_root_"+root,
					""+root,
			};
			
			do_search(lg,problem);
		}
	}
	
	private void do_search(DataHyperGraph lg, String[] problem) throws IOException{

		AgentHyperGraphTraverse.debug= debug;

		AgentHyperGraphTraverse[] ary_alg= new AgentHyperGraphTraverse[]{
				 new AgentHyperGraphTraverse(),
				 new AgentHyperGraphOptimize(),
				 new AgentHyperGraphAoStar(),
		};
		
		String [][] propname= new String[][]{
				{OPT_ENUM,OPT_ENUM_FOUND,OPT_ENUM_W}, 
				{OPT_BEST,OPT_BEST_FOUND,OPT_BEST_W} ,
				{OPT_AO,OPT_AO_FOUND,OPT_AO_W} ,
		};
		
		//root
		int v= Integer.parseInt(problem[1]);
		String output="";
		for (int i=0; i<ary_alg.length;i++)
		{

			AgentHyperGraphTraverse alg = ary_alg[i];
			System.out.println("\n-------"+alg.getClass().getSimpleName()+"-------");
			alg.traverse(lg, v,-1, -1, -1);
			output+=String.format("%s=%d,%s=%d,%s=%d,",
									propname[i][0],alg.getResultSolutions().size(),
									propname[i][1],alg.getResultSolutionFoundCount(),
									propname[i][2],alg.getResultBestWeight());
			
			if (null!=alg.m_runtime_best_subgraph)
				alg.m_runtime_best_subgraph.data_summary();
			else
				fail("no solution "+ problem[0]);
			save_diff(problem[0]+"_"+propname[i][0],alg.m_runtime_best_subgraph,lg);
			System.out.println(String.format("time spend %2.3f seconds",alg.getResultProcessSeconds()));
		}
		
		if (problem.length>=3){
			if (!output.equals(problem[2])){
				System.out.println("expected: "+problem[2]);
				System.out.println("actually: "+output);
				fail(problem[0]);
			}
		}else{
			System.out.println("actually: "+output);			
		}
	}

	
	
	
	
/*	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void test3_traverse_case2(){
		System.out.println("======================================================================");
		System.out.println("test_traverse_case2");
		DataHyperGraph lg = create_case2();
		debug=true;
		Integer v = new Integer(0);
		do_traverse(lg,v, 8, 6, 4, 7,-1,-1);

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void do_traverse(DataHyperGraph lg, Integer v, int total, int optimized_total, int best_total, int best_quality, int ao_total, int ao_quality){
		
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
		
		{
			System.out.println();
			System.out.println("-------"+AgentHyperGraphOptimizeAoStar.class.getSimpleName()+"-------");
			AgentHyperGraphOptimize alg = new AgentHyperGraphOptimizeAoStar();
			AgentHyperGraphTraverse.debug= debug;
			do_traverse(lg, v, alg);

			if (-1!=ao_total && alg.m_runtime_solution_count_best!=ao_total){
				fail();
			}
			
			if (-1!=ao_quality && alg.m_runtime_best_quality!=ao_quality){
				fail();
			}


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
		do_traverse(lg,v, -1, -1, -1, -1, -1, -1);		
	}
	
	
*/
	private  DataHyperGraph create_test_search_aostar(){
		DataHyperGraph lg = new DataHyperGraph();

		DataHyperEdge g;
		{
			g = new DataHyperEdge(0);
			g.addInput(1);
			g.addInput(2);
			g.addInput(3);
			lg.add(g,"A");

			g = new DataHyperEdge(1);
			g.addInput(4);	//t
			lg.add(g,"A");

			g = new DataHyperEdge(1);
			g.addInput(2);	//f
			g.addInput(3);	//f
			lg.add(g,"A");
			
			int [] axioms= new int []{
					2,3,4,
			};
			for (int i=0; i<axioms.length; i++){
				g = new DataHyperEdge(axioms[i]);
				lg.add(g,"A");
			}
		}
		
		save_graph("search_aostar", lg);

		return lg;
	}
	
	private  DataHyperGraph create_test_search_case2(){
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
		
		save_graph("search_case2", lg);

		return lg;
	}
	
		
	private  DataHyperGraph create_test_search_worstcase(int max){
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

		save_graph("search_worst"+max, lg);

		
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

	private  DataHyperGraph[] create_test_search_rain(){
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
		Integer v11 = new Integer(0);//, "s" , "You're singing.");
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

		for(Integer v: map_id_label.keySet()){
			String label = map_id_label.get(v);
			lg.setLabel(v, label);
		}
		
		save_graph("search_rain", lg);
		return lgs;
	}
	
	
	



	private  DataHyperGraph[] create_test_search_paulo(){
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
		
		save_graph("search_paulo", lg);

		return lgs;
	}
	
}

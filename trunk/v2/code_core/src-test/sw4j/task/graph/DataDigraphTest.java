package sw4j.task.graph;

import static org.junit.Assert.fail;

import org.junit.Test;

import sw4j.util.DataPVHMap;
import sw4j.util.ToolRandom;

public class DataDigraphTest {
	@Test
	public  void test1(){
		DataDigraph dd = new DataDigraph(4);
		dd.add(1,2);
		dd.add(2,3);
		dd.tc();
		if (!dd.isReachable(1, 3)){
			fail("expect connected");
		}

	}	

	@Test
	public  void test_cycle(){
		{
			DataDigraph dd = new DataDigraph(4);
			dd.add(1,1);
			dd.add(2,3);
			if (!dd.hasCycle()){
				fail("expect cycle");
			}			
		}
		
		{
			DataDigraph dd = new DataDigraph(4);
			dd.add(1,2);
			dd.add(2,1);
			if (dd.hasCycle()){
				fail("expect no cycle");
			}		
			dd.tc();
			if (!dd.hasCycle()){
				fail("expect cycle");
			}			
		}
	}	
	
	@Test
	public  void test_cycle_random(){
		int max_index =10;
		int total_arc = 20;
		
		DataDigraph dd = new DataDigraph(max_index+1);
		for (int i=0;i<total_arc; i++){
			dd.add(ToolRandom.randomInt(max_index), ToolRandom.randomInt(max_index));
		}

		DataDigraph tc = dd.create_tc();
		System.out.println(tc.hasCycle());
		tc.reflex();
		System.out.println(dd);
		System.out.println(tc);

	}	
	
	@Test
	public  void test_transitive_correct(){
		int max_index =100;
		int total_arc = 1000;
		
		DataDigraph dd = new DataDigraph(max_index+1);
		DataPVHMap<Integer,Integer> dd2= new DataPVHMap<Integer,Integer>(); 
		for (int i=0;i<total_arc; i++){
			int from =ToolRandom.randomInt(max_index);
			int to =ToolRandom.randomInt(max_index);
			dd.add(from,to );
			dd2.add(from, to);
		}
		
		dd.tc();
		DataDigraph dd1 = DataDigraph.create(dd2);
		dd1.tc();
		
		//System.out.println(dd);
		if (dd.size()!=dd1.size()){
			System.out.println(dd);
			System.out.println(dd1);
			fail();
		}

	}	

	@Test
	public  void test_transitive(){
		
		DataDigraph dd1 = new DataDigraph(10);
		dd1.add(1, 2);
		dd1.add(2, 3);
		dd1.add(3, 1);
		dd1.add(3, 4);

		DataDigraph dd2 = new DataDigraph(10);
		dd2.add(2, 1);
		dd2.add(1, 3);
		dd2.add(3, 2);
		dd2.add(3, 4);
		
		//System.out.println(dd);
		if (dd1.size()!=dd2.size()){
			System.out.println(dd1);
			System.out.println(dd2);
			fail();
		}

		dd1.tc();
		dd2.tc();
		
		if (dd1.size()!=dd2.size()){
			System.out.println(dd1);
			System.out.println(dd2);
			fail();
		}

	}	

	//@Test
	public  void test_create_tc_scale_random(){
		int max_index =1000;
		int total_arc = 100000;
		
		DataDigraph dd = new DataDigraph(max_index+1);
		for (int i=0;i<total_arc; i++){
			dd.add(ToolRandom.randomInt(max_index), ToolRandom.randomInt(max_index));
		}

		DataDigraph tc = dd.create_tc();
		System.out.println(tc.hasCycle());

	}	
	
	
}

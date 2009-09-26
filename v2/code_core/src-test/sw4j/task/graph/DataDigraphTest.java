package sw4j.task.graph;

import static org.junit.Assert.fail;

import org.junit.Test;

import sw4j.util.ToolRandom;

public class DataDigraphTest {
	@Test
	public  void test1(){
		DataDigraph dd = new DataDigraph(4);
		dd.add(1,2);
		dd.add(2,3);
		dd.create_tc(false);
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
			dd.create_tc(false);
			if (!dd.hasCycle()){
				fail("expect cycle");
			}			
		}
	}	
	
	@Test
	public  void test_cycle_random(){
		int max_index =10;
		int total_arc = 10;
		
		DataDigraph dd = new DataDigraph(max_index+1);
		for (int i=0;i<total_arc; i++){
			dd.add(ToolRandom.randomInt(max_index), ToolRandom.randomInt(max_index));
		}

		DataDigraph tc = dd.create_tc(true);
		System.out.println(tc.hasCycle());
		//System.out.println(dd);
		System.out.println(tc);

	}	

	@Test
	public  void test_create_tc_random(){
		int max_index =1000;
		int total_arc = 100000;
		
		DataDigraph dd = new DataDigraph(max_index+1);
		for (int i=0;i<total_arc; i++){
			dd.add(ToolRandom.randomInt(max_index), ToolRandom.randomInt(max_index));
		}

		DataDigraph tc = dd.create_tc(true);
		System.out.println(tc.hasCycle());

	}	
}

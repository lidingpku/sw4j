package sw4j.util;

import org.junit.Test;

public class DataTableTest {
	@Test
	public void test_load() {
		
		String [] aryURI = new String []{
			"http://www.data.gov/data_gov_catalog.csv",
		};
		
		for (int i=0;i<aryURI.length; i++){
			String szURI = aryURI[i];
			
			DataTable table = DataTable.fromCSV(szURI);
			
			System.out.println(table.m_data.iterator().next().toTemplate("catalog"));
		}
	}
	
}

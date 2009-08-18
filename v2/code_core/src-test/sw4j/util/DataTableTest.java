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
			
			DataSmartMap map = new DataSmartMap();
			map.putAll(table.m_header, table.m_values.iterator().next(), null);
			System.out.println(map.toTemplate("catalog"));
		}
	}
	
}

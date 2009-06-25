package sw4j.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataTable {
	ArrayList<DataSmartMap> m_data = new ArrayList<DataSmartMap>();
	
	public static DataTable fromCSV(String szURL){
		DataTable  table = new DataTable();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader (ToolIO.pipeUrlToInputStream(szURL)));
			String line = null;
			int rowid=0;
			ToolCsvParser parser = new ToolCsvParser();
			List<String> fields = null;
			while (null!=(line=reader.readLine())){
				if (0 == rowid){
					//process header row
					fields = new ArrayList<String>( parser.parse(line));
				}else{
					//process rows
					List<String> values = parser.parse(line);
					
					DataSmartMap map = new DataSmartMap();
										
					map.putAll(fields, values, new HashSet<String>());
					
					table.m_data.add(map);
				}
				rowid++;
			}
			
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return table;
	}
}

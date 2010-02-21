package sw4j.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataTable {
	//ArrayList<DataSmartMap> m_data = new ArrayList<DataSmartMap>();
	ArrayList<String> m_header = new ArrayList<String>();
	ArrayList<List<String>> m_values = new ArrayList<List<String>>();
	
	public List<String> getHeader(){
		return this.m_header;
	}
	public List<List<String>> getValues(){
		return this.m_values;
	}
	
	public static DataTable fromCSV(String szURL){
		DataTable  table = new DataTable();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader (ToolIO.pipeUrlToInputStream(szURL, true)));
			String line = null;
			int rowid=0;
			ToolCsvParser parser = new ToolCsvParser();
			while (null!=(line=reader.readLine())){
				if (0 == rowid){
					//process header row
					table.m_header = new ArrayList<String>( parser.parse(line));
				}else{
					//process rows
					List<String> values = new ArrayList<String>( parser.parse(line));
					
					table.m_values.add(values);
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

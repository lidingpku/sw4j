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
package sw4j.task.common;
/**
 * report (a collection of message) about task execution results
 * 
 * @author Li Ding
 * 
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import sw4j.util.DataPVHMap;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jMessage;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.transform.sax.*; 

public class DataTaskReport extends DataSmartMap{
	////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////
	public DataTaskReport(String szTitle, String szDescription){
		super(REPORT_TAG);
		this.put(REPORT_TITLE, szTitle);
		this.put(REPORT_DESC, szDescription);
		
		//getLogger().info(getIntroductionMessage());
	}
	
	////////////////////////////////////////////////
	// Configure  
	////////////////////////////////////////////////
	
	public static final String LOAD_FILE_OR_URL = "file_or_url";
	public static final String LOAD_XML_BASE = "xml_base";
	public static final String LOAD_RAW_CONTENT = "raw_content";

	public static final String REPORT_TAG = "Sw4jReport";
	public static final String REPORT_TITLE = "report_title";
	public static final String REPORT_DESC = "report_description";
	public static final String REPORT_IS_CHOSEN = "report_is_chosen";

	public static final String ENTRY_PROP = "hasMessage";
	public static final String ENTRY_TAG = "Sw4jMessage";
	
	public static final String RAW_LINE_PROP = "hasRawLine";
	public static final String RAW_LINE_TAG = "Sw4jRawLine";
	public static final String RAW_LINE_NUMBER = "line_number";
	public static final String RAW_LINE_CONTENT = "line_content";
	

	public static int MAX_ENTRY = 100; // the maximum allowed entries in error report (too many entries won't help) 

	////////////////////////////////////////////////
	// state  
	////////////////////////////////////////////////
    private DataPVHMap <Integer, DataSmartMap> m_entries = new DataPVHMap <Integer, DataSmartMap>();

    private DataSmartMap m_interalState = new DataSmartMap();
    
	////////////////////////////////////////////////
	// input
	////////////////////////////////////////////////
	public void addInternalState(String id, String data)
	{
		m_interalState.put(id, data);
	}		

	public DataSmartMap addEntry(
			Integer error_level, 
			String error_summary,  
			String error_creator,  
			String error_detail,
			boolean bUseMaxEntry)
	{
		
		// skip errors when there are too many
		if (bUseMaxEntry){
			if (m_entries.getValuesCount(error_level)>MAX_ENTRY){
				getLogger().info("too many messages, skipped");
				return null;
			}
		}
		
		Sw4jMessage msg = new Sw4jMessage(ENTRY_TAG, error_level,error_summary,error_detail,error_creator);
		
		m_entries.add(error_level, msg);
		
		return msg;
	}	
	
	////////////////////////////////////////////////
	// functions
	////////////////////////////////////////////////

	public String getIntroductionMessage(){
		return String.format("Starting Task  %s ...\n", this.getAsString(REPORT_TITLE));
	}

	public String getConclusionMessage(){
		return String.format("Task %s %s \n", this.getAsString(REPORT_TITLE), this.isClean()?"succeed":"has issues") ;
	}

	/**
	 * true if the validation report is free of any warning, error or fatal errors.
	 * @return
	 */
	public boolean isClean(){
		//Iterator<Map.Entry<String, DataSmartMap>> iter = m_entries.entrySet().iterator();
		HashSet<Integer> skips = new HashSet<Integer>();
		skips.add(Sw4jMessage.STATE_INFO);
		return !test_entries(skips);
	}
	
	/**
	 * true if this report has fatal errors
	 * @return
	 */
	public boolean hasFatal(){
		//Iterator<Map.Entry<String, DataSmartMap>> iter = m_entries.entrySet().iterator();
		HashSet<Integer> skips = new HashSet<Integer>();
		skips.add(Sw4jMessage.STATE_INFO);
		skips.add(Sw4jMessage.STATE_WARNING);
		skips.add(Sw4jMessage.STATE_ERROR);
		return test_entries(skips);
	}

	/**
	 * true if this report has fatal errors
	 * @return
	 */
	public boolean hasError(){
		//Iterator<Map.Entry<String, DataSmartMap>> iter = m_entries.entrySet().iterator();
		HashSet<Integer> skips = new HashSet<Integer>();
		skips.add(Sw4jMessage.STATE_INFO);
		skips.add(Sw4jMessage.STATE_WARNING);
		return test_entries(skips);
	}

	/**
	 * check if there are some issues outside the skip list 
	 * 
	 * @param skips	the levels of issues to be skipped
	 * @param required	the levels of issues to be required
	 * @return
	 */
	private boolean test_entries(Set<Integer> skips){
		boolean bHasIssue = false;
		Iterator<Integer> iter = m_entries.keySet().iterator();
		while (iter.hasNext()){
			Integer key = iter.next();
			if (skips.contains(key)){
				continue;
			}
			
			bHasIssue = true;
		}		
		return bHasIssue;
	}
	
	public  int countIssues() {
		HashSet<Integer> skips = new HashSet<Integer>();
		skips.add(Sw4jMessage.STATE_INFO);
		return count_entries(skips);
	}
	
	public  int count_entries(Set<Integer> skips) {
		Set<Integer> keys = m_entries.keySet();
		keys.removeAll(skips);
		return keys.size();
/*		int sum = 0;
		Iterator<Map.Entry<String, List<DataSmartMap>>> iter = m_entries.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<String, List<DataSmartMap>>  entry = iter.next();
			if (!skips.contains(entry.getKey())){
				sum ++;
			}
		}		
		return sum;
*/	}
	
	public  int count_entries(Integer key) {
		return this.m_entries.getValuesCount(key);
	}
	
	/**
	 * check if there are some issues outside the skip list 
	 * 
	 * @param skips	the levels of issues to be skipped
	 * @return
	 */
	public boolean hasSummary( String szSummary){
		Iterator<Integer> iter = m_entries.keySet().iterator();
		while (iter.hasNext()){
			Integer key = iter.next();
			Collection<DataSmartMap> list_data = m_entries.getValues(key);
			if (null!=list_data){
				Iterator<DataSmartMap> iter_data = list_data.iterator();
				while (iter_data.hasNext()){
					DataSmartMap  data = iter_data.next();
					
					if (data.getAsString(Sw4jMessage.FIELD_SUMMARY).equals(szSummary)){
						return true;
					}
				}
			}
		}		
		return false;
	}

	
	@Override
	public void xmlStartContent(TransformerHandler hd) throws SAXException{
		AttributesImpl atts = new AttributesImpl();
		// write metadata
		super.xmlStartContent(hd);

		// write members
		Iterator<Integer> iter = m_entries.keySet().iterator();
		while (iter.hasNext()){
			Integer key = iter.next();
			Collection<DataSmartMap> list_data = m_entries.getValues(key);
			if (null!=list_data){
				Iterator<DataSmartMap> iter_data = list_data.iterator();
				while (iter_data.hasNext()){
					DataSmartMap  data = iter_data.next();

					hd.startElement("","",ENTRY_PROP,atts);
					data.xmlStartContent(hd);
					data.xmlEndContent(hd);
					hd.endElement("","",ENTRY_PROP);
				}
			}
		}		
	}

	@Override
	public String toString(){
		return super.toString()+ this.m_entries.toPrettyString();
	}

}

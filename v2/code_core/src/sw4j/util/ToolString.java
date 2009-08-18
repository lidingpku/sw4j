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

package sw4j.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
//import java.util.TimeZone;
import java.util.TimeZone;

/**
 * some convenient string functions
 * <ul>
 * <li>ensure a string is non-empty, throw exception otherwise</li>
 * </ul>
 * 
 * @author Li Ding
 * 
 */

public class ToolString {
	
	////////////////////////////////////////////////
	// functions
	////////////////////////////////////////////////
	
	/**
	 * in szText, extract a section identified by the "szBegin" and "szEnd" landmark  
	 * 
	 * @param szText
	 * @param szBegin
	 * @param szEnd
	 * @return
	 */
	public static String section_extract(String szText,String szBegin, String szEnd){
		/*int indexBegin= szText.indexOf(szBegin);
		if (indexBegin<0)
			return null;
		int indexEnd= szText.indexOf(szEnd,indexBegin);
		if (indexEnd<0)
			return null;
		
		return szText.substring(indexBegin, indexEnd+szEnd.length());
		*/
		String [] ret  = section_split(szText, szBegin,szEnd);
		if (null!=ret){
			return ret[1];
		}else{
			return null;
		}
	}

	/**
	 * split szText into three sections, identified by the "szBegin" and "szEnd" landmark  
	 * 
	 * @param szText
	 * @param szBegin
	 * @param szEnd
	 * @return
	 */
	public static String[] section_split(String szText,String szBegin, String szEnd){
		int indexBegin= szText.indexOf(szBegin);
		if (indexBegin<0)
			return null;
		int indexEnd= szText.indexOf(szEnd,indexBegin);
		if (indexEnd<0)
			return null;
		
		String [] ret = new String[3];
		ret[0]= szText.substring(0,indexBegin);
		ret[1]= szText.substring(indexBegin, indexEnd+szEnd.length());
		ret[2]= szText.substring(indexEnd+szEnd.length());
		return ret;
	}
	

	/**
	 * in szText, replace a section identified by the "szBegin" and "szEnd" landmark with szToReplace
	 * 
	 * @param szText
	 * @param szBegin
	 * @param szEnd
	 * @param szToReplace
	 * @return
	 */
	public static String section_replace(String szText,String szBegin, String szEnd, String szToReplace){
		int indexBegin= szText.indexOf(szBegin);
		if (indexBegin<0)
			return null;
		int indexEnd= szText.indexOf(szEnd,indexBegin);
		if (indexEnd<0)
			return null;
		
		return szText.substring(0, indexBegin)+ szToReplace + szText.substring(indexEnd+szEnd.length());
	}

	/**
	 * check if the sequence of landmarks exist in the text 
	 * 
	 * @param szText
	 * @param szLandMarks
	 * @return
	 */
	public static boolean match(String szText, String [] szLandMarks){
		if (ToolSafe.isEmpty(szText))
			return false;
		
		if (ToolSafe.isEmpty(szLandMarks))
			return false;

		szText = szText.toLowerCase();
		
		boolean bRet = true;
		int index = 0;
		for (int i=0; i<szLandMarks.length; i++){
			String landmark = szLandMarks[i].toLowerCase();
			index= szText.indexOf(landmark, index);
			if (index<0)
				return false;
		}
		return bRet;
	}

	public static String formatXMLDateTime(long date, TimeZone tz){
		final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		if (null!=tz)
			sf.setTimeZone(tz);
		return sf.format(date);
	}
	public static String formatXMLDateTime(long date){
		return formatXMLDateTime(date, null);
	}

	@SuppressWarnings("unchecked")
	public static String printCollectionToString(Collection data) {
		if (ToolSafe.isEmpty(data))
			return "";
	
		String temp = "";
		Iterator iter = data.iterator();
		while (iter.hasNext()) {
			String  entry = iter.next().toString();
			temp += entry;
			temp += "\n";
		}
		return temp;
	}

	@SuppressWarnings("unchecked")
	public static String printMapToString(Map data) {
		if (ToolSafe.isEmpty(data))
			return "";
		
		String temp = "";
		Iterator<Map.Entry> iter = data.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = iter.next();
			temp += entry;
			temp += "\n";
		}
		return temp;
	}

}

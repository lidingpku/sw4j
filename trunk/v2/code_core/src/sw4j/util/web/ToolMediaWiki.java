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
package sw4j.util.web;

/**
 * general wiki dump generation functions for semantic mediaWiki
 * 
 * @author Li Ding
 */
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;


public class ToolMediaWiki {
	

	public static void create_wiki_dump(Map<String,String> map_pages,  String szFileName){
		create_wiki_dump(map_pages, false, szFileName);
	}	
	
	public static void create_wiki_dump(Map<String,String> map_pages, boolean bNoTimestamp, String szFileName){
		try {
			PrintWriter out =  ToolIO.prepareUtf8Writer((ToolIO.prepareFileOutputStream(szFileName, false)));
			create_wiki_dump(map_pages, bNoTimestamp,out);
			out.close();
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void create_wiki_dump(Map<String,String> map_pages, boolean bNoTimestamp, PrintWriter out){
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<mediawiki>");

		String szTime= ToolString.formatXMLDateTime(System.currentTimeMillis(), TimeZone.getTimeZone( "GMT" ));
		
		
		String [] szTemplates={
" <page>\n" +
"     <title>",

"</title>\n" +
"     <revision>\n" ,

"       <timestamp>",

"</timestamp>\n" ,

"       <text xml:space=\"preserve\">",

"</text>\n" +
"     </revision>\n" +
" </page>"};
			
		Iterator<Map.Entry<String,String>> iter= map_pages.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<String, String> entry=iter.next();
			String szTemp = "";
			szTemp += szTemplates[0];
			szTemp += entry.getKey();
			szTemp += szTemplates[1];
			if (!bNoTimestamp){
				szTemp += szTemplates[2];
				szTemp += szTime;
				szTemp += szTemplates[3];
			}
			szTemp += szTemplates[4];
			szTemp += entry.getValue();
			szTemp += szTemplates[5];
			
			out.println(szTemp);
			out.flush();
		}
		
		out.println("</mediawiki>");
		out.flush();
	}
}

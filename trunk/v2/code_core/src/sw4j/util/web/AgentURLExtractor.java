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

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sw4j.util.DataQname;
import sw4j.util.ToolString;



/**
 * extract links in a html document
 *
 * @author Li Ding
 */
public class AgentURLExtractor {
	
	protected void addURL( String szURL){
		if (testExtNOLINK(szURL))
			return;
		m_data.add(szURL);
	}	

	private void processHttp(String szText){
		Pattern pattern;
		Matcher matcher;
		String temp,temp1;
//		int indexBegin,  indexEnd;

		pattern = Pattern.compile (ToolString.PATTERN_URL, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher( szText);
		while (matcher.find()){
			temp = matcher.group().trim();
			//System.out.println( temp );
//			indexBegin = temp.lastIndexOf("<b>")+3; 
//			indexEnd = temp.lastIndexOf("</b>");
			//temp1 = temp.substring(0, temp.length()-1);
			temp1 = DataQname.extractNamespaceUrl(temp);
//			temp1= temp1.replaceAll(",","");
//			temp1= temp1.trim();
			//System.out.println(temp1);
			
			if (null==temp1){
				System.out.println(temp);
				return;
			}
			try{	
//				URL tempURL = new URL( temp1 );
				
				addURL(temp1);
				
					
			}catch(Exception e){
				//e.printStackTrace();
				//System.out.println(temp);
			}
		}
	}

	private void processJavascript(String szText, URL currentURL, String szBaseURL){
		Pattern pattern;
		Matcher matcher;
		String temp;

		// check if java script has been used
		pattern = Pattern.compile ("JavaScript", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher( szText);
		if (!matcher.find())
			return;
		
		// extract url
		pattern = Pattern.compile ("window.location\\s*=\\s*\\S+\\s", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher( szText);
		while (matcher.find()){
			temp = matcher.group();
			int index = temp.indexOf("=");
			temp = temp.substring(index+1).trim();
			index = temp.indexOf(temp.substring(0,1),1);
			if (index>0)
				temp = temp.substring(1, index);
			
			try{	
				URL tempURL = new URL( currentURL, temp);

				if (null== tempURL)
					continue;
				
				
				if (!tempURL.getProtocol().toLowerCase().equals("http"))
					continue;
					
				String szTempURL = tempURL.toString();

				addURL(szTempURL);
			}catch(Exception e){
				//e.printStackTrace();
				//System.out.println(temp);
			}
		}
	}	
	
	private void processHref(String szText, URL currentURL, String szBaseURL){
		Pattern pattern;
		Matcher matcher;
		String temp,temp1;

		pattern = Pattern.compile ("\\shref=[\\S]+\\s", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher( szText);
		while (matcher.find()){
			temp = matcher.group();
			//System.out.println( temp );
			
			// remove surrounding text
			temp = temp.substring("href=".length()+1);
			//check if > already exists.
			int index= temp.indexOf(">");
			if (index>0){
				temp=temp.substring(0,index);
			}
			temp = temp.trim();
			//remove quotes
			String [] QUOTE= new String[] {"\"","'"};
			temp1 = null;
			for (int i=0; i<QUOTE.length; i++){
				if (temp.startsWith(QUOTE[i])){
					int indexEnd = temp.indexOf(QUOTE[i],1);
					if (indexEnd>0){
						temp1 = temp.substring(1,indexEnd);
						temp1 = temp1.trim();
						break;
					}
				}
			}
			if (null==temp1)
				continue;
			
			try{	
				if (temp1.startsWith("mailto:"))
					continue;

				// automatic link when list directory
				if (temp1.startsWith("?N=D"))
					continue;
				if (temp1.startsWith("?M="))
					continue;
				if (temp1.startsWith("?S="))
					continue;
				if (temp1.startsWith("?D="))
					continue;
				if (temp1.startsWith("?C="))
					continue;
				if (temp1.startsWith("?O="))
					continue;
				
				URL tempURL = new URL( currentURL, temp1);

				if (null== tempURL)
					continue;
				
				
				if (!tempURL.getProtocol().toLowerCase().equals("http"))
					continue;
					
				String szTempURL = tempURL.toString();

				addURL(szTempURL);
	
			}catch(Exception e){
				//System.out.println(temp);
				//e.printStackTrace();
			}
        
		}
	}
	
	private void processMetaLink(String szText, URL currentURL, String szBaseURL){
		Pattern pattern;
		Matcher matcher;
		String temp,temp1;
		int indexBegin,  indexEnd;

		pattern = Pattern.compile ("<link[^>]+>", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher( szText);
		while (matcher.find()){
			temp = matcher.group();
			temp1 = temp.toLowerCase();
			//System.out.println( temp );
			boolean flag = false;
			
			if (!flag)
				flag = (temp1.indexOf(".rdf")>0);

			if (!flag)
				flag = (temp1.indexOf("/rdf")>0);

			if (!flag)
				flag = (temp1.indexOf("application/rdf+xml")>0);

			if (!flag)
				flag = (temp1.indexOf("application/rss+xml")>0);

			if (!flag)
				flag = (temp1.indexOf("text/rdf")>0);
			
			if (!flag)
				continue;
			
			String keyword = "href=\"";
			indexBegin = temp1.indexOf(keyword)+keyword.length(); 
			indexEnd = temp1.indexOf("\"",indexBegin);
			if (indexEnd<indexBegin)
				continue;
				
			temp1 = temp.substring(indexBegin, indexEnd);
			temp1= temp1.trim();
			
			try{	
				if (temp1.startsWith("mailto:"))
					continue;

				// automatic link when list directory
				if (temp1.equals("?N=D"))
					continue;
				if (temp1.equals("?M=A"))
					continue;
				if (temp1.equals("?S=A"))
					continue;
				if (temp1.equals("?D=A"))
					continue;
				
				URL tempURL = new URL( currentURL, temp1);

				if (null== tempURL)
					continue;
				
				
				if (!tempURL.getProtocol().toLowerCase().equals("http"))
					continue;
					
				String szTempURL = tempURL.toString();

				addURL(szTempURL);
	
			}catch(Exception e){
				//System.out.println(temp);
				//e.printStackTrace();
			}
        
		}
	}

	private void processFrameLink(String szText, URL currentURL, String szBaseURL){
		Pattern pattern;
		Matcher matcher;
		String temp;

		// check if java script has been used
		pattern = Pattern.compile ("<frame[^>]+>", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher( szText);
		if (!matcher.find())
			return;
		
		// extract url
		pattern = Pattern.compile ("src\\s*=\\s*\\S+\\s", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher( szText);
		while (matcher.find()){
			temp = matcher.group();
			int index = temp.indexOf("=");
			temp = temp.substring(index+1).trim();
			index = temp.indexOf(temp.substring(0,1),1);
			temp = temp.substring(1, index);
			
			try{	
				URL tempURL = new URL( currentURL, temp);

				if (null== tempURL)
					continue;
				
				
				if (!tempURL.getProtocol().toLowerCase().equals("http"))
					continue;
					
				String szTempURL = tempURL.toString();

				addURL(szTempURL);
			}catch(Exception e){
				//e.printStackTrace();
				//System.out.println(temp);
			}
		}
	}	
	int m_count;
	
	
	protected Set<String> m_data = new HashSet<String>();
	public Set<String> getData (){return m_data;}
	
	public static Set<String> process(String szText, String szURL){
		AgentURLExtractor te = new AgentURLExtractor();
		te.m_data.clear();
		
		if (null!=szText){
			try {
				te.processHttp(szText);

				if (null!=szURL){
					URL curURL = new URL( szURL);
					te.processHref(szText, curURL, szURL);
					te.processMetaLink(szText, curURL, szURL);
					te.processJavascript(szText,curURL,szURL);
					te.processFrameLink(szText,curURL,szURL);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
		return te.m_data;
	}
	
	

	private static Set<String> g_ext_nlink =null;
	public static boolean testExtNOLINK(String szURL){
		if (null== g_ext_nlink){
			g_ext_nlink = new HashSet<String>();
			g_ext_nlink.add( "mp3");
			g_ext_nlink.add( "jpg");
			g_ext_nlink.add( "jpeg");
			g_ext_nlink.add( "gif");
			g_ext_nlink.add( "bmp");
			g_ext_nlink.add( "png");
			g_ext_nlink.add( "swf");
			g_ext_nlink.add( "wmv");
			g_ext_nlink.add( "ico");
			
			g_ext_nlink.add( "pdf");
			g_ext_nlink.add( "ps");
			g_ext_nlink.add( "doc");
			g_ext_nlink.add( "ppt");
			
		}
		String suffix = ToolWeb.getSuffix(szURL);
		suffix.toLowerCase();
		return g_ext_nlink.contains(suffix);
	}
	
	protected void printCollection(Collection<Object> data){
		
		System.out.println("----------- collection begin ------------");
		System.out.println(data.size());
		Iterator<Object> iter = data.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next().toString());
		}
		System.out.println(data.size());
		System.out.println("----------- collection end------------");
	}
	
}

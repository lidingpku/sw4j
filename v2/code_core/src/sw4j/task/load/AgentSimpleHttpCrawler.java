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
package sw4j.task.load;

/**
 * do simple crawling
 * 
 * @author Li Ding
 */
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import sw4j.util.ToolSafe;
import sw4j.util.web.AgentURLExtractor;

public class AgentSimpleHttpCrawler {
	
	public static boolean debug = false;
	public boolean canContinue(){
		return true;
	}
	
	public String m_seed_url = null;
	public  int m_max_crawl_depth=10;
	public Set<String> m_allowed_url_patterns = new HashSet<String>();

	
	/**
	 * 
	 * @throws IOException 
	 */
	public void crawl() {
		crawl(new HashSet<String>());
	}
	
	public void crawl(Set<String> visited_urls) {
		TreeSet<String> to_visit = new TreeSet<String>();
		HashSet<String> visited= new HashSet<String>();

		//init
		to_visit.add(m_seed_url);
		visited.addAll(visited_urls);
	

		//limited depth crawl
		for (int i = 0; i<= this.getMaxCrawlDepth() && !to_visit.isEmpty(); i++){
			// init next round to visit
			TreeSet<String> to_visit_next = new TreeSet<String>();
			
			// remove all visited 
			to_visit.removeAll(visited);

			Iterator<String> iter= to_visit.iterator();
			while (iter.hasNext()){
				if (!canContinue())
					return;

				// init
				String szURL = iter.next();
				TaskLoad data_load = null;
				
				visited.add(szURL);

				// check if the URL is allowed to be crawl
				if (!this.isAllowed(szURL)){
					System.out.println("disallowed url: "+ szURL);
					continue;
				}
				
				// retrieve content from the url
				if (debug)
					System.out.println("download file");
				data_load = TaskLoad.load(szURL);

				if (!data_load.isLoadSucceed())
					continue;
				
				//this URL is confirmed to be allowed and downloadable 
				process_confirmed_url(szURL);
				
				//if there more links to follow, then extract URls
				process_link_extraction(data_load, to_visit_next);
				
				//remove disallowed urls
				Iterator<String> iter_url = to_visit_next.iterator();
				while (iter_url.hasNext()){
					String szTemp = iter_url.next();
					if (!this.isAllowed(szTemp)){
						iter_url.remove();
						continue;
					}
				}
				
			}
			
			to_visit = to_visit_next;
		}		
	}
	
	private void process_confirmed_url(String szURL) {
		if (!isWebDirectory(szURL))
			System.out.println(szURL);
	}


	private boolean isWebDirectory(String szURL) {
		return szURL.endsWith("/");
	}

	private boolean isAllowed(String szURL) {
		if (m_seed_url.equals(szURL))
			return true;
		
		if (m_allowed_url_patterns.size()==0)
			return true;
		
		Iterator<String> iter = m_allowed_url_patterns.iterator();
		while (iter.hasNext()){
			String pattern = iter.next();
			if (szURL.matches(pattern))
				return true;
		}
		return false;
	}

	private int getMaxCrawlDepth() {
		return m_max_crawl_depth;
	}

	private void process_link_extraction(
			TaskLoad data_load,
			Collection<String> urls){
		if (ToolSafe.isEmpty(urls))
			return;
		
		if (null== data_load)
			return;
		
		String szText = data_load.getContent();
		if (!ToolSafe.isEmpty(szText)){
			Set<String> links =AgentURLExtractor.process(szText, data_load.getXmlBase());
			urls.addAll(links);
		}
	}
	

}

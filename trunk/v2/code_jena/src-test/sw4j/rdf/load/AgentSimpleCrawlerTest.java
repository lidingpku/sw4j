package sw4j.rdf.load;

import org.junit.Test;

import sw4j.task.load.AgentSimpleHttpCrawler;

public class AgentSimpleCrawlerTest {
	@Test
	public void test_file_load() {
		String [][] aryURI_prefix = new String [][]{
				{"/work/iw/swa/test/task_20080502_pml_iwregistry.n3",  "http://inference-web.org/registry/.*"},
//				{"/work/iw/swa/test/task_20080502_pml_iwproofs.n3",  "http://inference-web.org/proofs/.*"},
		};
		System.out.println("test Task Crawler");
		for (int i=0; i<aryURI_prefix.length; i++){
			
			AgentSimpleHttpCrawler crawler = new AgentSimpleHttpCrawler();
			crawler.m_seed_url = aryURI_prefix[i][0];
			crawler.m_allowed_url_patterns.add(aryURI_prefix[i][1]);
			crawler.m_max_crawl_depth=3;
			crawler.crawl();
		}
	}
}

package sw4j.app.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.servlet.DiffService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.rdf.load.TaskDiff;

public class DiffServiceTest {
	@Test
	public void test_11() throws IOException{
		DiffService svc = new DiffService();
		svc.params.put(DiffService.PARAM_CUR, "http://data-gov.tw.rpi.edu/raw/92/2009-08-07/data-92.rdf");
		svc.params.put(DiffService.PARAM_PREV, "http://data-gov.tw.rpi.edu/raw/92/2009-08-06/data-92.rdf");
		svc.params.put(DiffService.PARAM_ROOT_TYPE_URI, "http://data-gov.tw.rpi.edu/2009/data-gov-twc.rdf#DataEntry");
		svc.params.put(DiffService.PARAM_OUTPUT, TaskDiff.DIFF_RDF);
		svc.params.put(DiffService.PARAM_RSS_LINK_PROP,  "http://data-gov.tw.rpi.edu/vocab/p/92/url");
		svc.params.put(DiffService.PARAM_RSS_TITLE_PROP,  "http://data-gov.tw.rpi.edu/vocab/p/92/title");
		svc.params.put(DiffService.PARAM_RSS_TITLE, "hello world");

		DataServletResponse ret = svc.run();
		
		System.out.println(ret.getMimeType());
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}	


	@Test
	public void test1b() throws IOException{
		DiffService svc = new DiffService();
		svc.params.put(DiffService.PARAM_CUR, "http://data-gov.tw.rpi.edu/raw/92/2009-08-19/data-92.rdf");
		svc.params.put(DiffService.PARAM_PREV, "http://data-gov.tw.rpi.edu/raw/92/2009-08-17/data-92.rdf");
		svc.params.put(DiffService.PARAM_ROOT_TYPE_URI, "http%3A%2F%2Fdata-gov.tw.rpi.edu%2F2009%2Fdata-gov-twc.rdf%23DataEntry");
		svc.params.put(DiffService.PARAM_OUTPUT, TaskDiff.DIFF_RSS);
		svc.params.put(DiffService.PARAM_XMLBASE, "http://data-gov.tw.rpi.edu/raw/92/diff-data-92.rdf");


		DataServletResponse ret = svc.run();
		
		System.out.println(ret.getMimeType());
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}	
	@Test
	public void test2() throws IOException{
		DiffService svc = new DiffService();
		svc.params.put(DiffService.PARAM_CUR, "http://data-gov.tw.rpi.edu/raw/92/2009-07-03/today.rss");
		svc.params.put(DiffService.PARAM_PREV, "http://data-gov.tw.rpi.edu/raw/92/2009-06-24/today.rss");
				
		DataServletResponse ret = svc.run();
		
		System.out.println(ret.getMimeType());
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}
}

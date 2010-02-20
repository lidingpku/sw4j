package sw4j.app.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.servlet.SparqlService;
import sw4j.app.servlet.common.DataServletResponse;

public class SparqlServiceTest {
	@Test
	public void test1() throws IOException{
		SparqlService svc = new SparqlService();
		svc.params.put(SparqlService.PARAM_QUERY_URL, "http://data-gov.tw.rpi.edu/sparql/construct-92-rss.sparql");
		//wrong parame, and will be override by the actual output of sparql 
		svc.params.put(SparqlService.PARAM_OUTPUT, "sparql/xml");
		
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}
	
	@Test
	public void test2() throws IOException{
		SparqlService svc = new SparqlService();
		svc.params.put(SparqlService.PARAM_QUERY_URL, "http://onto.rpi.edu/alpha/sparql/test3.sparql");
		svc.params.put(SparqlService.PARAM_OUTPUT, "sparql/xml");

		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content!=null)
			fail("URL should be succeed");		
	}


	@Test
	public void test3() throws IOException{
		
		SparqlService svc = new SparqlService();
		svc.params.put(SparqlService.PARAM_QUERY_URL, "http://onto.rpi.edu/alpha/sparql/test7.sparql");
		DataServletResponse ret1 = svc.run();
		
		
		svc = new SparqlService();
		svc.params.put(SparqlService.PARAM_QUERY_URL, "http://onto.rpi.edu/alpha/sparql/test7.sparql");
		svc.params.put(SparqlService.PARAM_USE_PELLET, "yes");
		DataServletResponse ret2 = svc.run();

		ret1.output(new PrintWriter(System.out));
		ret2.output(new PrintWriter(System.out));
		if (ret1.equals(ret2)){
			fail("results should not be the same");
		}
	}
}

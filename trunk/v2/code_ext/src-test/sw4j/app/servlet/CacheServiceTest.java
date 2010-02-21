package sw4j.app.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.servlet.CacheService;
import sw4j.app.servlet.SparqlService;
import sw4j.app.servlet.common.DataServletResponse;

public class CacheServiceTest {
	@Test
	public void test1() throws IOException{
		CacheService svc = new CacheService();
		svc.params.put(SparqlService.PARAM_URI, "http://www.cs.rpi.edu/~dingl/foaf.rdf#me");
		//wrong parame, and will be override by the actual output of sparql 
		svc.params.put(SparqlService.PARAM_OUTPUT, "sparql/xml");
		
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}

	//bad localname
	@Test
	public void test1b() throws IOException{
		CacheService svc = new CacheService();
		svc.params.put(SparqlService.PARAM_URI, "http://www.cs.rpi.edu/~dingl/foaf.rdf#bad");
		//wrong parame, and will be override by the actual output of sparql 
		svc.params.put(SparqlService.PARAM_OUTPUT, "sparql/xml");
		
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}

	//bad url
	@Test
	public void test1c() throws IOException{
		CacheService svc = new CacheService();
		svc.params.put(SparqlService.PARAM_URI, "http://www.cs.rpi.edu/~dingl/bad");
		//wrong parame, and will be override by the actual output of sparql 
		svc.params.put(SparqlService.PARAM_OUTPUT, "sparql/xml");
		
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}

	@Test
	public void test2() throws IOException{
		CacheService svc = new CacheService();
		svc.params.put(CacheService.PARAM_QUERY, "select * WHERE {?s ?p ?o} limit 10");
		//wrong parame, and will be override by the actual output of sparql 
		svc.params.put(CacheService.PARAM_OUTPUT, "sparql/xml");
		
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}

	@Test
	public void test3() throws IOException{
		CacheService svc = new CacheService();
		svc.params.put(CacheService.PARAM_QUERY, "describe ?s WHERE {?s ?p ?o} limit 10");
		//wrong parame, and will be override by the actual output of sparql 
		svc.params.put(CacheService.PARAM_OUTPUT, "rdf/xml");
		
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}

}

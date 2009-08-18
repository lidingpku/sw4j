package sw4j.pellet.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.servlet.PelletSparqlService;
import sw4j.app.servlet.common.DataServletResponse;

public class PelletSparqlServiceTest {

	@Test
	public void test2() throws IOException{
		PelletSparqlService svc = new PelletSparqlService();
		svc.queryURL = "http://onto.rpi.edu/alpha/sparql/test3.sparql";
		svc.rdfsyntax= "sparql/xml";
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		
		if (ret.m_model_content!=null)
			fail("URL should be succeed");		
	}

	@Test
	public void test1() throws IOException{
		
		PelletSparqlService svc = new PelletSparqlService();
		svc.queryURL = "http://onto.rpi.edu/alpha/sparql/test7.sparql";
		DataServletResponse ret1 = svc.run();
		
		
		svc = new PelletSparqlService();
		svc.bUsePellet =true;
		svc.queryURL = "http://onto.rpi.edu/alpha/sparql/test7.sparql";
		DataServletResponse ret2 = svc.run();

		ret1.output(new PrintWriter(System.out));
		ret2.output(new PrintWriter(System.out));
		if (ret1.equals(ret2)){
			fail("results should not be the same");
		}
	}

}

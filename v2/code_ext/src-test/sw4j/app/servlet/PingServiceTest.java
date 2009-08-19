package sw4j.app.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.servlet.PingService;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.rdf.load.RDFSYNTAX;


public class PingServiceTest {
	@Test
	public void test_3() throws IOException {
		PingService svc = new PingService();
		svc.params.put(AbstractService.PARAM_URL, "http://tw.rpi.edu");
		svc.params.put(PingService.PARAM_VALIDATE_RDF, "true");
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.getContent().indexOf("alive")<0)
			fail("URL should be alive");
	}

	@Test
	public void test_rdf_output()  throws IOException {
		PingService svc = new PingService();
		svc.params.put(AbstractService.PARAM_URL, "http://www.cs.rpi.edu/~dingl/foaf.rdf");
		svc.params.put(AbstractService.PARAM_OUTPUT, RDFSYNTAX.RDFXML);
		svc.params.put(PingService.PARAM_VALIDATE_RDF, "true");
		DataServletResponse ret = svc.run();
		ret.output(new PrintWriter(System.out));
	}
	
	@Test
	public void test1() throws IOException {
		PingService svc = new PingService();
		svc.params.put(AbstractService.PARAM_URL, "http://tw.rpi.edu");
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.getContent().indexOf("alive")<0)
			fail("URL should be alive");
	}
	
	@Test
	public void test2()  throws IOException {
		PingService svc = new PingService();
		svc.params.put(AbstractService.PARAM_URL, "http://tw1.rpi.edu");
		
		DataServletResponse ret = svc.run();
		ret.output(new PrintWriter(System.out));
		if (ret.getContent().indexOf("alive")>=0)
			fail("URL should not be alive");
	}


}

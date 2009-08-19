package sw4j.app.servlet;


import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.servlet.ConvertService;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;



public class ConvertServiceTest {
	@Test
	public void test_1a() throws IOException {
		ConvertService svc = new ConvertService();
		svc.params.put(AbstractService.PARAM_URL, "http://www.cs.rpi.edu/~dingl/foaf.rdf");
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}

	@Test
	public void test_1b() throws IOException {
		ConvertService svc = new ConvertService();
		svc.params.put(AbstractService.PARAM_URL, "http://dig.csail.mit.edu/TAMI/2007/amord/air.ttl");
		svc.params.put(AbstractService.PARAM_OUTPUT, "N-TRIPLE");
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}


}

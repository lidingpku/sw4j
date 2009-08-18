package sw4j.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.task.rdf.RDFSYNTAX;


public class PingServiceTest {
	@Test
	public void test_3() throws IOException {
		PingService svc = new PingService();
		svc.szURL = "http://tw.rpi.edu";
		svc.bValidateRDF = true;
		
		DataServeletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.getContent().indexOf("alive")<0)
			fail("URL should be alive");
	}

	@Test
	public void test_rdf_output()  throws IOException {
		PingService svc = new PingService();
		svc.szURL = "http://www.cs.rpi.edu/~dingl/foaf.rdf";
		svc.rdfsyntax = RDFSYNTAX.RDFXML;
		svc.bValidateRDF = true;
		DataServeletResponse ret = svc.run();
		ret.output(new PrintWriter(System.out));
	}
	
	@Test
	public void test1() throws IOException {
		PingService svc = new PingService();
		svc.szURL = "http://tw.rpi.edu";
		
		DataServeletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.getContent().indexOf("alive")<0)
			fail("URL should be alive");
	}
	
	@Test
	public void test2()  throws IOException {
		PingService svc = new PingService();
		svc.szURL = "http://tw1.rpi.edu";
		
		DataServeletResponse ret = svc.run();
		ret.output(new PrintWriter(System.out));
		if (ret.getContent().indexOf("alive")>=0)
			fail("URL should not be alive");
	}


}

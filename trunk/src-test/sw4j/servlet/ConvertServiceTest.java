package sw4j.servlet;


import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;



public class ConvertServiceTest {
	@Test
	public void test_1a() throws IOException {
		ConvertService svc = new ConvertService();
		svc.szURL = "http://www.cs.rpi.edu/~dingl/foaf.rdf";
		
		DataServeletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}

	@Test
	public void test_1b() throws IOException {
		ConvertService svc = new ConvertService();
		svc.szURL = "http://www.cs.rpi.edu/~dingl/foaf.rdf";
		svc.rdfsyntax = "N-TRIPLE";
		
		DataServeletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}


}

package sw4j.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

public class SparqlServiceTest {

	@Test
	public void test1() throws IOException{
		SparqlService svc = new SparqlService();
		svc.queryURL = "http://data-gov.tw.rpi.edu/sparql/construct-catalog-url.sparql";
		
		DataServeletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}

}

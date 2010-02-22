package sw4j.app.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.oie.InspectMissingPropertyValue;
import sw4j.app.oie.InspectUnexpectedIndividualType;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;

public class OieServiceTest {
	@Test
	public void test_empty_text() throws IOException{
		AbstractService svc = new OieService();
		
		DataServletResponse ret = svc.run();
		
		if (ret.isSucceed()){
			ret.output(new PrintWriter(System.out));
			fail("should have some errors");
		}
	}

	@Test
	public void test_ok() throws IOException{
		AbstractService svc = new OieService();
		svc.params.put(OieService.PARAM_inputUrl, "http://inference-web.org/proofs/tonys/tonys.owl");
		svc.params.put(OieService.PARAM_inputMethod, "uri");
		
		DataServletResponse ret = svc.run();
		
		
		if (!ret.isSucceed()){
			ret.output(new PrintWriter(System.out));
			fail("should be correct");
		}
	}

	@Test
	public void test_bad_rdf() throws IOException{
		AbstractService svc = new OieService();
		svc.params.put(OieService.PARAM_inputUrl, "http://tw.rpi.edu/2008/04/rdf_fatal_not_rdf.rdf");
		svc.params.put(OieService.PARAM_inputMethod, "uri");
		svc.params.put( InspectUnexpectedIndividualType.class.getName(),"on");
		svc.params.put( InspectMissingPropertyValue.class.getName(),"on");
		
		DataServletResponse ret = svc.run();
		
		if (ret.isSucceed()){
			ret.output(new PrintWriter(System.out));
			fail("should have some errors");
		}
	}
}

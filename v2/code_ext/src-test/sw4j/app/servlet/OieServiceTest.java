package sw4j.app.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.oie.InspectMissingPropertyValue;
import sw4j.app.oie.InspectUnexpectedIndividualType;
import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;

public class OieServiceTest {
	@Test
	public void test1() throws IOException{
		AbstractService svc = new OieService();
		svc.params.put(OieService.PARAM_inputUrl, "http://inference-web.org/proofs/tonys/tonys.owl");
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}

	@Test
	public void test2() throws IOException{
		AbstractService svc = new OieService();
		svc.params.put(OieService.PARAM_inputUrl, "http://tw.rpi.edu/2008/04/rdf_fatal_not_rdf.rdf");
		svc.params.put( InspectUnexpectedIndividualType.class.getName(),"on");
		svc.params.put( InspectMissingPropertyValue.class.getName(),"on");
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}
}

package sw4j.app.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;

public class OieServiceTest {
	@Test
	public void test1() throws IOException{
		AbstractService svc = new OieService();
		svc.params.put(OieService.PARAM_inputUrl, "http://inference-web.org/proofs/tonys/tonys.owl");
		svc.params.put(OieService.PARAM_inputMethod, "InspectUnexpectedIndividualType");
		
		DataServletResponse ret = svc.run();
		
		ret.output(new PrintWriter(System.out));
	}
	
}

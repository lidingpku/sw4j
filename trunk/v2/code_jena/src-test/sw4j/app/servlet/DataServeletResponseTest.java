package sw4j.app.servlet;

import org.junit.Test;

import sw4j.app.servlet.common.DataServletResponse;

import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DataServeletResponseTest {

	@Test
	public void test1(){
		DataServletResponse response = DataServletResponse.createResponse(ModelFactory.createDefaultModel(), null, null);
		System.out.println(response.getMimeType());
		
	}
}

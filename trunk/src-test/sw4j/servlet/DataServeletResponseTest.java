package sw4j.servlet;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DataServeletResponseTest {

	@Test
	public void test1(){
		DataServeletResponse response = DataServeletResponse.createResponse(ModelFactory.createDefaultModel(), null, null);
		System.out.println(response.getMimeType());
		
	}
}

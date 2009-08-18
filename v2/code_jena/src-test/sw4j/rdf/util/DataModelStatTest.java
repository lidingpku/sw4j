package sw4j.rdf.util;



import org.junit.Test;

import sw4j.rdf.util.AgentModelStat;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DataModelStatTest {

	@Test
	public void test_traverse() {
		String szURL ="http://inference-web.org/2.0/pml-provenance.owl";
		AgentModelStat stat = new AgentModelStat();
		Model m = ModelFactory.createDefaultModel();
		m.read(szURL);
		stat.traverse(m);
		stat.print();
	}

}

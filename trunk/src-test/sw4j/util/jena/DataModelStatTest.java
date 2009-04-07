package sw4j.util.jena;



import org.junit.Test;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DataModelStatTest {

	@Test
	public void test_traverse() {
		String szURL ="http://inference-web.org/2.0/pml-provenance.owl";
		DataModelStat stat = new DataModelStat();
		Model m = ModelFactory.createDefaultModel();
		m.read(szURL);
		stat.traverse(m);
		stat.print();
	}

}

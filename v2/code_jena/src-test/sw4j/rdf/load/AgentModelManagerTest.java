package sw4j.rdf.load;

import static org.junit.Assert.*;

import org.junit.Test;

import sw4j.rdf.load.AgentModelManager;
import sw4j.task.load.ToolLoadHttp;
import sw4j.util.Sw4jException;

import com.hp.hpl.jena.rdf.model.Model;


public class AgentModelManagerTest {

	@Test
	public void testGet() {
		assertTrue(null!=AgentModelManager.get());
		assertTrue(AgentModelManager.get().equals(AgentModelManager.get()));
	}

	@Test
	public void testLoadModel() {
		String [] aryURL = new String []{
				"http://inference-web.org/registry/DPR/Told.owl#Told",
				"http://inference-web.org/proofs/test/2007-08-gila/04_clr.owl",
				"http://inference-web.org/2.0/pml-provenance.owl",
		};
		
		for (int i=0; i<aryURL.length; i++){
			try {
				String szURI = aryURL[i];
				Model m = AgentModelManager.get().loadModel(szURI);
				if (null== m)
					fail();

				//ToolJena.printModel(m);
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
		}
	}
/*
	@Test
	public void testReadOntModel() {
		String [] aryURL = new String []{
			"http://inference-web.org/registry/DPR/Told.owl#Told",
			"http://inference-web.org/proofs/test/2007-08-gila/04_clr.owl",
			"http://inference-web.org/2.0/pml-provenance.owl",
		};
		
		OntModel m =ToolPellet.createOntModel();;
		for (int i=0; i<aryURL.length; i++){
			try {
				String szURI = aryURL[i];
				ModelManager.get().readOntModel(m, szURI);
			} catch (SwutilException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
		}
		//ToolJena.printModel(m);
	}
*/
	@Test
	public void testReadText() {
		String [] aryURL = new String []{
			"http://inference-web.org/registry/DPR/Told.owl#Told",
			"http://inference-web.org/proofs/test/2007-08-gila/04_clr.owl",
			"http://inference-web.org/2.0/pml-provenance.owl",
		};
		
		for (int i=0; i<aryURL.length; i++){
			try {
				String szURI = aryURL[i];
				String szText  = ToolLoadHttp.wget(szURI);
				Model m = AgentModelManager.loadModelFromText(szText, null, null);
				if (null== m)
					fail();

				//ToolJena.printModel(m);
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
		}
	}


}

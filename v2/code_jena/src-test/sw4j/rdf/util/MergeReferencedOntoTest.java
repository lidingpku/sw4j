package sw4j.rdf.util;

import java.util.Map;
import org.junit.Test;

import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.ToolJena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class MergeReferencedOntoTest {
	
	@Test
	public void MergeTest(){
		
		String [][] addresses = new String [][]{			
				
				{"http://tw.rpi.edu/2008/04/wine-instance_uit.rdf", "files/evaluation_test_merged/wine-instance_uit.rdf"},
				{"http://tw.rpi.edu/2008/04/wine-instance_rit.rdf", "files/evaluation_test_merged/wine-instance_rit.rdf"},
				{"http://tw.rpi.edu/2008/04/wine-instance_nsit.rdf", "files/evaluation_test_merged/wine-instance_nsit.rdf"},
				{"http://tw.rpi.edu/2008/04/wine-instance_mpv.rdf", "files/evaluation_test_merged/wine-instance_mpv.rdf"},
				{"http://tw.rpi.edu/2008/04/wine-instance_epv.rdf", "files/evaluation_test_merged/wine-instance_epv.rdf"},
				
				{"http://tw.rpi.edu/2008/04/owldl_error_datatype_datetime.owl", "files/evaluation_test_merged/owldl_error_datatype_datetime.owl"},
				{"http://tw.rpi.edu/2008/04/owldl_error_cardinality_hasname.owl", "files/evaluation_test_merged/owldl_error_cardinality_hasname.owl"},				
				{"http://tw.rpi.edu/2008/04/owldl_error_disjointwith.owl", "files/evaluation_test_merged/owldl_error_disjointwith.owl"},
				
							
				//{"http://data.semanticweb.org/dumps/conferences/eswc-2007-complete.rdf", "files/evaluation_test_merged/eswc-2007-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/conferences/eswc-2008-complete.rdf", "files/evaluation_test_merged/eswc-2008-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/conferences/iswc-2006-complete.rdf", "files/evaluation_test_merged/iswc-2006-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/conferences/iswc-aswc-2007-complete.rdf", "files/evaluation_test_merged/iswc-aswc-2007-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/workshops/esoe-2007-complete.rdf", "files/evaluation_test_merged/esoe-2007-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/workshops/fews-2007-complete.rdf", "files/evaluation_test_merged/fews-2007-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/workshops/first-2007-complete.rdf", "files/evaluation_test_merged/first-2007-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/workshops/om-2007-complete.rdf", "files/evaluation_test_merged/om-2007-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/workshops/peas-2007-complete.rdf", "files/evaluation_test_merged/peas-2007-complete.rdf"},
				//{"http://data.semanticweb.org/dumps/workshops/semwebdesign-2007-complete.rdf", "files/evaluation_test_merged/semwebdesign-2007-complete.rdf"}

		};
		
		for (int i=0; i< addresses.length; i++){
			String szURL = addresses[i][0];
			Map<String, Model> data =  AgentModelManager.get().loadModelRecursive(szURL, true, true);
			Model model_data_all =  ModelFactory.createDefaultModel();
			ToolJena.model_merge(model_data_all, data.values());
			ToolJena.printModelToFile(model_data_all, addresses[i][1], "RDF/XML", false);			

		}
	}
	
}

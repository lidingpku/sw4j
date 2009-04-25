package sw4j.example;

import sw4j.task.util.ToolOwl2SemanticMediaWiki;


public class TaskOwl2WikiDump {
	public static void main(String[] args) {

		String [][] aryInputOutput = new String [][]{
				{"http://www.w3.org/2002/07/owl#", "owl", "files/owl2wikidump/owl.xml"},
				{"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf", "files/owl2wikidump/rdf.xml"},
				{"http://www.w3.org/2000/01/rdf-schema#", "rdfs","files/owl2wikidump/rdfs.xml"},
				{"http://inference-web.org/2.0/ds.owl#", "ds", "files/owl2wikidump/ds.xml"},
				{"http://inference-web.org/2.0/pml-provenance.owl#", "pmlp","files/owl2wikidump/pmlp.xml"},
				{"http://inference-web.org/2.0/pml-justification.owl#", "pmlj","files/owl2wikidump/pmlj.xml"},
		};
		for (int i=0; i<aryInputOutput.length; i++){

			String szOntologyNamespace = aryInputOutput[i][0];
			String szPrefix = aryInputOutput[i][1];
			String  szFilename = aryInputOutput[i][2];
			ToolOwl2SemanticMediaWiki.dump_ontology(szOntologyNamespace, szPrefix, szFilename);
		}
	}
}

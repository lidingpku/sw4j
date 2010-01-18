package sw4j.example;

import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import sw4j.servlet.vocabulary.DGTWC;
import sw4j.task.util.ToolOwl2SemanticMediaWiki;


public class TaskOwl2WikiDump {
	public static void main(String[] args) {

		String [][] aryInputOutput = new String [][]{
				{DGTWC.getURI(), DGTWC.class.getSimpleName().toLowerCase(),"files/owl2wikidump/"+DGTWC.class.getSimpleName().toLowerCase()+".xml"},
				{DC.getURI(), DC.class.getSimpleName().toLowerCase(),"files/owl2wikidump/"+DC.class.getSimpleName().toLowerCase()+".xml"},
				{OWL.getURI(), OWL.class.getSimpleName().toLowerCase(), "files/owl2wikidump/"+OWL.class.getSimpleName().toLowerCase()+".xml"},
				{RDF.getURI(), RDF.class.getSimpleName().toLowerCase(), "files/owl2wikidump/"+RDF.class.getSimpleName().toLowerCase()+".xml"},
				{RDFS.getURI(), RDFS.class.getSimpleName().toLowerCase(),"files/owl2wikidump/"+RDFS.class.getSimpleName().toLowerCase()+".xml"},
				{DCTerms.getURI(), DCTerms.class.getSimpleName().toLowerCase(),"files/owl2wikidump/"+DCTerms.class.getSimpleName().toLowerCase()+".xml"},
				{FOAF.getURI(), FOAF.class.getSimpleName().toLowerCase(),"files/owl2wikidump/"+FOAF.class.getSimpleName().toLowerCase()+".xml"},
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
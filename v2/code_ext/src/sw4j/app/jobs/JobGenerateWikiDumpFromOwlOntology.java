package sw4j.app.jobs;

import sw4j.rdf.util.ToolOwl2SemanticMediaWiki;

import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;



public class JobGenerateWikiDumpFromOwlOntology {
	public static void main(String[] args) {

		String [][] aryInputOutput = new String [][]{
				{DC.getURI(), DC.class.getSimpleName().toLowerCase(),"output/owl2wikidump/"+DC.class.getSimpleName().toLowerCase()+".xml"},
				{OWL.getURI(), OWL.class.getSimpleName().toLowerCase(), "output/owl2wikidump/"+OWL.class.getSimpleName().toLowerCase()+".xml"},
				{RDF.getURI(), RDF.class.getSimpleName().toLowerCase(), "output/owl2wikidump/"+RDF.class.getSimpleName().toLowerCase()+".xml"},
				{RDFS.getURI(), RDFS.class.getSimpleName().toLowerCase(),"output/owl2wikidump/"+RDFS.class.getSimpleName().toLowerCase()+".xml"},
				{DCTerms.getURI(), DCTerms.class.getSimpleName().toLowerCase(),"output/owl2wikidump/"+DCTerms.class.getSimpleName().toLowerCase()+".xml"},
				{FOAF.getURI(), FOAF.class.getSimpleName().toLowerCase(),"output/owl2wikidump/"+FOAF.class.getSimpleName().toLowerCase()+".xml"},
				{"http://rdfs.org/sioc/ns#", "sioc",  "output/owl2wikidump/sioc.xml"},
				{"http://www.w3.org/2003/01/geo/wgs84_pos#", "geo","output/owl2wikidump/"+"geo"+".xml"},
				{"http://inference-web.org/2.0/ds.owl#", "ds", "output/owl2wikidump/ds.xml"},
				{"http://inference-web.org/2.0/pml-provenance.owl#", "pmlp","output/owl2wikidump/pmlp.xml"},
				{"http://inference-web.org/2.0/pml-justification.owl#", "pmlj","output/owl2wikidump/pmlj.xml"},
				{"http://inference-web.org/2.0/pml-trust.owl#", "pmlt","output/owl2wikidump/pmlt.xml"},
				{"http://inference-web.org/2.0/pml-relation.owl#", "pmlr","output/owl2wikidump/pmlr.xml"},
				{"http://www.obofoundry.org/ro/ro.owl#", "ro","output/owl2wikidump/ro.xml"},
				{"http://www.w3.org/2004/03/trix/swp-2/","swp","output/owl2wikidump/swp.xml"},
		};
		for (int i=0; i<aryInputOutput.length; i++){

			String szOntologyNamespace = aryInputOutput[i][0];
			String szPrefix = aryInputOutput[i][1];
			String  szFilename = aryInputOutput[i][2];
			ToolOwl2SemanticMediaWiki.dump_ontology(szOntologyNamespace, szPrefix, szFilename);
		}
		aryInputOutput = new String [][]{
				{"http://www.w3.org/2004/02/skos/core#", "skos", "output/owl2wikidump/skos.xml","http://www.w3.org/TR/skos-reference/skos.rdf"},
				{"http://creativecommons.org/ns#", "cc","output/owl2wikidump/cc.xml","http://creativecommons.org/schema.rdf"},
				{"http://rdfs.org/ns/void#", "void","output/owl2wikidump/void.xml","http://rdfs.org/ns/void/rdf"},
				{"http://xmlns.com/wot/0.1/", "wot","output/owl2wikidump/wot.xml","http://xmlns.com/wot/0.1/index.rdf"},
		};
		for (int i=0; i<aryInputOutput.length; i++){

			String szOntologyNamespace = aryInputOutput[i][0];
			String szPrefix = aryInputOutput[i][1];
			String  szFilename = aryInputOutput[i][2];
			String szOntologyUrl = aryInputOutput[i][3];
			ToolOwl2SemanticMediaWiki.dump_ontology(szOntologyUrl, szOntologyNamespace, szPrefix, szFilename);
		}
	}
}

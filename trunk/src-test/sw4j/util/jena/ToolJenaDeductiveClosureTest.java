package sw4j.util.jena;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mindswap.pellet.jena.OWLReasoner;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;



public class ToolJenaDeductiveClosureTest {
	protected Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}

	static boolean debug = true;
	/**
	 * print model
	 * @param m
	 */
	private void debug0(Model m){
		if (!debug)
			return;
		System.out.println("------debug0------------");
		if (null==m){
			System.out.println("empty model");
			return;
		}
			
		System.out.println(m.size());
		ToolJena.printModel(m);
	}
	/**
	 * list individual and its details
	 * @param ont
	 */
	private void debug1(OntModel ont){
		if (!debug)
			return;
		System.out.println("------debug1------------");
		Iterator<Individual> iter  = ont.listIndividuals();
		while (iter.hasNext()){
			Individual ind = iter.next();
			System.out.println(ind);
			System.out.println(ind.listRDFTypes(true).toSet());
		}
	}
	
	/**
	 * add oneof etc.
	 * @param ont
	 */
	private void debug2(Model m){
		if (!debug)
			return;
		System.out.println("------debug2------------");
		Iterator<Statement> iter = m.listStatements(null, OWL.oneOf,(String)null);
		while (iter.hasNext()){
			Statement stmt = iter.next();
			System.out.println(stmt);
			List<RDFNode> inds = ToolJena.getListMembers(m, (Resource)stmt.getObject());
			System.out.println(inds);
			Iterator<RDFNode> iter_ind = inds.iterator();
			while (iter_ind.hasNext()){
				Resource ind = (Resource) iter_ind.next();
				m.add(ind, RDF.type, stmt.getObject());
				//m.add(m.createStatement(ind, RDF.type, stmt.getObject()));
			}
		}
	}

	/**
	 * pellet sparql
	 * @param m
	 */
	private void debug3(Model m){
		if (!debug)
			return;
		System.out.println("------debug3------------");
		// user owl reasoner to run sparql query
		OWLReasoner reasoner = new OWLReasoner();   // for counting OWL Species
		reasoner.load(m);
		
		String queryString =
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
			"SELECT ?s ?p ?o " +"\n" +
//			"FROM NAMED <http://ex.com/foo> " +"\n" +
			"WHERE " +
			"{ " +"\n" +
			"	{ " +"\n" +
				"?s  rdf:type  owl:Thing ." +"\n" +
				"?s  ?p  ?o ." +"\n" +
			"	}"+
			"}";
		ResultSet results =null;
		results = reasoner.execQuery(queryString) ;
		// Output query results	
		ByteArrayOutputStream sw = new ByteArrayOutputStream();
		ResultSetFormatter.out(sw,results);
		String ret = sw.toString();
		System.out.println("sparqlresult:"+ ret);
	}
	
	
	/** 
	 * 2009.04.05
	 * the recent pellet add nothing in its deductive closure, therefore changed all the test
				{"files/deductive/test1-rdfs-subclass.rdf","original","5","deductive(pellet)","34","deductive(rdfs)","5","deductive(true)","18"},

				{"files/deductive/test8-foaf.rdf","original","571","deductive(pellet)","838","deductive(rdfs)","571","deductive(true)","775"},
				// this test will fail since OWL reasoner does not agree 
				{"files/deductive/test1-subclass.owl","original","5","deductive(pellet)","34","deductive(rdfs)","5","deductive(true)","18"},

				{"files/deductive/test6-onproperty-subproperty-datatypeproperty.owl","original","13","deductive(pellet)","0","deductive(rdfs)","13","deductive(true)","0"},
				{"files/deductive/test6-onproperty-subproperty-objectproperty.owl","original","13","deductive(pellet)","45","deductive(rdfs)","13","deductive(true)","34"},

				{"files/deductive/test5-onproperty-subproperty.owl","original","12","deductive(pellet)","39","deductive(rdfs)","12","deductive(true)","28"},

				{"files/deductive/test4-hasvalue.owl","original","7","deductive(pellet)","29","deductive(rdfs)","7","deductive(true)","18"},

				{"files/deductive/test3-subproperty.owl","original","8","deductive(pellet)","30","deductive(rdfs)","8","deductive(true)","20"},


				{"files/deductive/test2-equivalentclass.owl","original","12","deductive(pellet)","50","deductive(rdfs)","12","deductive(true)","39"},

				
				// pellet does not handle anonymous nominal class in expected way 
				// because it treats anonymous class the same as named class 
		 		{"files/deductive/test7-oneof-anonymous.owl","original","8","deductive(pellet)","18","deductive(rdfs)","8","deductive(true)","14"},
				{"files/deductive/test7-oneof-named.owl","original","8","deductive(pellet)","26","deductive(rdfs)","8","deductive(true)","14"},
	 * @throws Sw4jException
	 */
	@Test
	public void general_test() throws Sw4jException{
		String [][]  aryURL = new String[][]{
				

				{"files/deductive/test1-rdfs-subclass.rdf",
					"original","5",
					"deductive(pellet)","39",	//34
					"deductive(rdfs)","5",
					"deductive(true)","21",	//18
				},
				
				{"files/deductive/test1-subclass.owl",
					"original","5",
					"deductive(pellet)","36",//"34",
					"deductive(rdfs)","5",
					"deductive(true)","18"},

				{"files/deductive/test6-onproperty-subproperty-objectproperty.owl",
						"original","13",
						"deductive(pellet)","49",//"45",
						"deductive(rdfs)","13",
						"deductive(true)","36",//"34"
						},

				{"files/deductive/test5-onproperty-subproperty.owl",
							"original","12",
							"deductive(pellet)","39",
							"deductive(rdfs)","12",
							"deductive(true)","26",//"28"
							},

				{"files/deductive/test4-hasvalue.owl",
								"original","7",
								"deductive(pellet)","32",//"29",
								"deductive(rdfs)","7",
								"deductive(true)","19",//"18"
								},

				{"files/deductive/test3-subproperty.owl",
									"original","8",
									"deductive(pellet)","31",//"30",
									"deductive(rdfs)","8",
									"deductive(true)","19", //"20"
									},


				{"files/deductive/test2-equivalentclass.owl",
										"original","12",
										"deductive(pellet)","52",//"50",
										"deductive(rdfs)","12",
										"deductive(true)","36",//"39"
										},

				
											
				{"files/deductive/test7-oneof-named.owl",
												"original","8",
												"deductive(pellet)","30", //"26",
												"deductive(rdfs)","8",
												"deductive(true)","16" //"14"
												},
				
				{"files/deductive/test8-foaf.rdf",
					"original","571",
					"deductive(pellet)","973", //"838",
					"deductive(rdfs)","571",
					"deductive(true)","907", //"775",
			},
		};
		
		for (int i=0; i<aryURL.length; i++){
			String szFileOrUrl = aryURL[i][0];
			
			//load jena  model
			Model m0 = ModelFactory.createDefaultModel();
			m0.read( ToolIO.pipeFileToInputStream(szFileOrUrl),"http://example.org/rdf");
			//AgentModelManager mgr =AgentModelManager.get();
			//Model m0 = mgr.loadModel(szFileOrUrl);
			
			
			//debug1((OntModel)m);
			//debug2(m);
			//debug3(m);
			
			// run test
			long size;
			String [] arySize = new String [4];
			size = m0.size();
			arySize[0] = ""+size;
			//debug0(m0);
		
		
			//debug
			//deductive(pellet)
			OntModel m1 = ToolPellet.createOntModel();
			m1.add(m0);
			size = 0;
			if (m1.validate().isValid()){
				m1.add(m1);
				size = m1.listStatements().toSet().size();
			}else{
				m1 = null;
			}
			arySize[1] = ""+size;
			//debug0(m1);
			
			//debug
			//deductive(rdfs)
			OntModel m2 = ToolJena.model_createDeductiveClosureRDFS(m0);
			size = m2.size();
			arySize[2] = ""+size;
			//debug0(m2);
			
			
			//Model m = ModelFactory.createDefaultModel();
			//deductive(true)
			Model m3 = ToolJena.model_createDeductiveClosure(m0);
			size =0;
			if (null!= m3){
				size = m3.size();
			}
			arySize[3] = ""+size;
			
			//debug0(m3);
			//debug0(m3.getRawModel());
			//debug0(m3.getDeductionsModel());
			
			//print result;
			String szTemp = szFileOrUrl+"\n";
			boolean bMistake = false;
			for (int j=0; j<arySize.length; j++){
				String fieldname = aryURL[i][2*j+1];
				String expected = aryURL[i][2*j+2];
				String found = arySize[j];
				szTemp += String.format("[%s]: %b. expecte=%s,found=%s \n", fieldname, expected.equals(found), expected, found);
				if (!expected.equals(found)){
					bMistake =true;
				}
			}
			if (bMistake){
				System.out.println(szTemp);
				//debug0(m0);
				//debug0(m1);
				//debug0(m2);
				//debug0(m3);
				fail("has mistakes");
			}
		}
	}
	
	//TODO
	// the following code has not been tested yet
	
	public void todo_test() throws Sw4jException{
		String [][]  aryURL = new String[][]{
				// this test will fail since OWL reasoner does not agree 
				 {"files/deductive/test6-onproperty-subproperty-datatypeproperty.owl",
				 "original","13",
				 "deductive(pellet)","0",
				 "deductive(rdfs)","13",
				 "deductive(true)","0"
				 },

				// pellet does not handle anonymous nominal class in expected way 
				// because it treats anonymous class the same as named class 
		 		{"files/deductive/test7-oneof-anonymous.owl",
											"original","8",
											"deductive(pellet)","18",
											"deductive(rdfs)","8",
											"deductive(true)","14"
											},
				  

		};
		
		for (int i=0; i<aryURL.length; i++){
			String szFileOrUrl = aryURL[i][0];
			
			//load jena  model
			Model m0 = ModelFactory.createDefaultModel();
			m0.read( ToolIO.pipeFileToInputStream(szFileOrUrl),"http://example.org/rdf");
			//AgentModelManager mgr =AgentModelManager.get();
			//Model m0 = mgr.loadModel(szFileOrUrl);
			
			
			//debug1((OntModel)m);
			//debug2(m);
			//debug3(m);
			
			// run test
			long size;
			String [] arySize = new String [4];
			size = m0.size();
			arySize[0] = ""+size;
			//debug0(m0);
		
		
			//debug
			//deductive(pellet)
			OntModel m1 = ToolPellet.createOntModel();
			m1.add(m0);
			size = 0;
			if (m1.validate().isValid()){
				m1.add(m1);
				size = m1.listStatements().toSet().size();
			}else{
				m1 = null;
			}
			arySize[1] = ""+size;
			//debug0(m1);
			
			//debug
			//deductive(rdfs)
			OntModel m2 = ToolJena.model_createDeductiveClosureRDFS(m0);
			size = m2.size();
			arySize[2] = ""+size;
			//debug0(m2);
			
			
			//Model m = ModelFactory.createDefaultModel();
			//deductive(true)
			Model m3 = ToolJena.model_createDeductiveClosure(m0);
			size =0;
			if (null!= m3){
				size = m3.size();
			}
			arySize[3] = ""+size;
			
			//debug0(m3);
			//debug0(m3.getRawModel());
			//debug0(m3.getDeductionsModel());
			
			//print result;
			String szTemp = szFileOrUrl+"\n";
			boolean bMistake = false;
			for (int j=0; j<arySize.length; j++){
				String fieldname = aryURL[i][2*j+1];
				String expected = aryURL[i][2*j+2];
				String found = arySize[j];
				szTemp += String.format("[%s]: %b. expecte=%s,found=%s \n", fieldname, expected.equals(found), expected, found);
				if (!expected.equals(found)){
					bMistake =true;
				}
			}
			if (bMistake){
				System.out.println(szTemp);
				//debug0(m0);
				//debug0(m1);
				//debug0(m2);
				//debug0(m3);
				fail("has mistakes");
			}
		}
	}	
	/*@Test
	public void test(){
		String szFileOrUrl = "files/deductive/test6-maxcard1-eq.owl";
		ModelManager mgr =ModelManager.get();
		try {
			OntModel m1;
			m1 = ToolJena.getDeductiveClosureOWLDL( mgr.loadModel(szFileOrUrl));
			System.out.println("OWL:"+m1.size());
			ToolJena.printModel(m1);

			OntModel m2;
			m2 = ToolJena.getDeductiveClosureRDFS( mgr.loadModel(szFileOrUrl));
			System.out.println("RDFS:"+m2.size());
			ToolJena.printModel(m2);

			OntModel m3;
			m3 = ToolJena.getDeductiveClosureRDFS( m1);
			System.out.println("ALL:"+ m3.size());
			//ToolJena.printModel(m3);
			ToolJena.printModel(m3.remove(m1));
			
		} catch (SwutilException e) {
			e.printStackTrace();
		}
	}
	*/
}

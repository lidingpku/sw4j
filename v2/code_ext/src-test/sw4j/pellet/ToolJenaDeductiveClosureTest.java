package sw4j.pellet;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import sw4j.pellet.ToolPellet;
import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.ToolJena;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.DataSmartMap;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;



public class ToolJenaDeductiveClosureTest {
	protected Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}

	static boolean debug = true;
	
	
	final static String FIELD_URL = "url";
	final static String FIELD_ORIGINAL = "original";
	final static String FIELD_DEDUCTIVE_PELLET = "deductive pellet";
	final static String FIELD_DEDUCTIVE_RDFS = "deductive rdfs transitive only";
	final static String FIELD_DEDUCTIVE_SW4J = "deductive sw4j";

	
	@Test
	public void test_single_file(){
		Iterator<DataSmartMap> iter = getTestData1().iterator();
		while (iter.hasNext()){
			DataSmartMap entry = iter.next();

			String szFileOrUrl = entry.getAsString(FIELD_URL);
			
			//load jena model
			Model model = ModelFactory.createDefaultModel();
			try {
				model = AgentModelManager.get().loadModel(szFileOrUrl);
				//model.read( ToolIO.prepareFileInputStream(szFileOrUrl),"http://example.org/rdf");
			} catch (Sw4jException e) {
				e.printStackTrace();
				fail("cannot load");
			}
			
			do_test(model,entry);	
		}
	}
	
	@Test
	public void test_load_reference(){
		Iterator<DataSmartMap> iter = getTestData2().iterator();
		while (iter.hasNext()){
			DataSmartMap entry = iter.next();

			String szFileOrUrl = entry.getAsString(FIELD_URL);
			
			//load jena model
			
			Model model = ModelFactory.createDefaultModel();
			Map<String,Model> data = AgentModelManager.get().loadModelRecursive(szFileOrUrl, true, true);
			
			if (data.values().size()==0){
				fail("cannot load");
			}else if (data.values().size()>1){
				System.out.println("referenced model loaded");
			}
			
			ToolJena.model_merge(model, data.values());


			do_test(model,entry);
		}
	}
	
	@Test
	public void test_single_file_inconsistent(){
		Iterator<DataSmartMap> iter = getTestData3().iterator();
		while (iter.hasNext()){
			DataSmartMap entry = iter.next();

			String szFileOrUrl = entry.getAsString(FIELD_URL);
			
			//load jena model
			Model model = ModelFactory.createDefaultModel();
			try {
				model.read( ToolIO.prepareFileInputStream(szFileOrUrl),"http://example.org/rdf");
			} catch (Sw4jException e) {
				e.printStackTrace();
				fail("cannot load");
			}
			
			
			//load pellet deductive model
			Model model_pellet_deductive=null;
			try {
				model_pellet_deductive = ToolPellet.model_createDeductiveClosure(null, model);

				check_consistency(model_pellet_deductive);
				
				fail("expect inconsistency");
				
			} catch (Sw4jException e) {
				
				System.out.println("++++++++++++++++++++++++++++++");
				System.out.println("I'm expecting it!");
				
				System.out.println(e.getMessage());
			}			
		}
	}
	


	// consistent model
	private ArrayList<DataSmartMap> getTestData1(){
		ArrayList<DataSmartMap> data = new ArrayList<DataSmartMap>(); 


	
		//////////////////////////////////////////////////////////////////
		{
			//transitive subclass
			//rdfs class 
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test1-subclass-for-rdfsclass.rdf");
			entry.put(FIELD_ORIGINAL, 5);
			entry.put(FIELD_DEDUCTIVE_PELLET, 39);  //34
 			entry.put(FIELD_DEDUCTIVE_RDFS, 9); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 39);  
			
			data.add(entry);
		}
		{
			//transitive subclass
			//rdfs class 
//	DataSmartMap entry = new DataSmartMap();
	//entry.put(FIELD_URL, "http://www.cs.rpi.edu/~michaj6/provenance/UCDGC.owl");
//			entry.put(FIELD_ORIGINAL, 5);
	//		entry.put(FIELD_DEDUCTIVE_PELLET, 39);  //34
 		//	entry.put(FIELD_DEDUCTIVE_RDFS, 9); 
			//entry.put(FIELD_DEDUCTIVE_SW4J, 39);  
			
		//	data.add(entry);
		}


		
		{
			//transitive subclass
			//owl class, I can infer additional triples (c  rdf:type  owl:Class)
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test1-subclass-for-owlclass.owl");
			entry.put(FIELD_ORIGINAL, 5);
			entry.put(FIELD_DEDUCTIVE_PELLET, 36);  //34
 			entry.put(FIELD_DEDUCTIVE_RDFS, 9); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 36);  
			
			data.add(entry);
		}
		

		
		
		{
			// equivalentclass + intersection of  (list)
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test2-equivalentclass-intersectionof.owl");
			entry.put(FIELD_ORIGINAL, 12);
			entry.put(FIELD_DEDUCTIVE_PELLET, 52);  //50
 			entry.put(FIELD_DEDUCTIVE_RDFS, 14); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 52);  //39
			
			data.add(entry);
		}


		{
			//transitive subproperty
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test3-subproperty.owl");
			entry.put(FIELD_ORIGINAL, 8);
			entry.put(FIELD_DEDUCTIVE_PELLET, 31);  //30
 			entry.put(FIELD_DEDUCTIVE_RDFS, 12); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 31);  //20
			
			data.add(entry);
		}
		
		
		{
			// transitive subclass
			// restriction (anonymous subclass relation)
			// has value restriction inferred on instance property
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test4-hasvalue.owl");
			entry.put(FIELD_ORIGINAL, 7);
			entry.put(FIELD_DEDUCTIVE_PELLET, 32);  //29
 			entry.put(FIELD_DEDUCTIVE_RDFS, 9); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 34);  //18 
			
			data.add(entry);
		}

		
		{
			// transitive subclass
			// restriction 
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test5-onproperty-subproperty.owl");
			entry.put(FIELD_ORIGINAL, 12);
			entry.put(FIELD_DEDUCTIVE_PELLET, 39);  
 			entry.put(FIELD_DEDUCTIVE_RDFS, 17); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 41);   
			
			data.add(entry);
		}
		
		{
			// transitive subclass
			// restriction 
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test6-onproperty-subproperty-objectproperty.owl");
			entry.put(FIELD_ORIGINAL, 13);
			entry.put(FIELD_DEDUCTIVE_PELLET, 49);  //45
 			entry.put(FIELD_DEDUCTIVE_RDFS, 18);	 
			entry.put(FIELD_DEDUCTIVE_SW4J, 51);  //34 
			
			data.add(entry);
		}
		
		{
			// transitive subclass
			// cardinality restriction  => inference same
			
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test6-maxcard1-eq.owl");
			entry.put(FIELD_ORIGINAL, 13);
			entry.put(FIELD_DEDUCTIVE_PELLET, 45);  //45
 			entry.put(FIELD_DEDUCTIVE_RDFS, 17);	 
			entry.put(FIELD_DEDUCTIVE_SW4J, 47);  //34 
			
			data.add(entry);
		}
		

		


		{
			// one of
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test7-oneof-named.owl");
			entry.put(FIELD_ORIGINAL, 8);
			entry.put(FIELD_DEDUCTIVE_PELLET, 30);  //26
 			entry.put(FIELD_DEDUCTIVE_RDFS, 8); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 30);  //14
			
			data.add(entry);
		}


		{
			// transitive subclass
			// anonymous  
			
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test7-oneof-anonymous.owl");
			entry.put(FIELD_ORIGINAL, 8);
			entry.put(FIELD_DEDUCTIVE_PELLET, 22);  //34
 			entry.put(FIELD_DEDUCTIVE_RDFS, 8); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 24);  
			
			data.add(entry);
		}	
		
		{
			// unionof => subclass  
			
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test8-unionof-named.owl");
			entry.put(FIELD_ORIGINAL, 8);
			entry.put(FIELD_DEDUCTIVE_PELLET, 40);  //34
 			entry.put(FIELD_DEDUCTIVE_RDFS, 8); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 40);  
			
			data.add(entry);
		}
		
		{
			// unionof => subclass  
			
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test8-unionof-anonymous.owl");
			entry.put(FIELD_ORIGINAL, 8);
			entry.put(FIELD_DEDUCTIVE_PELLET, 32);  //34
 			entry.put(FIELD_DEDUCTIVE_RDFS, 8); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 36);  
			
			data.add(entry);
		}	
		
		{
			// rdfs label ??
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test9-foaf.rdf");
			entry.put(FIELD_ORIGINAL, 571);
			entry.put(FIELD_DEDUCTIVE_PELLET, 973);  
 			entry.put(FIELD_DEDUCTIVE_RDFS, 615); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 976);  
			
			data.add(entry);
		}

		{
			// rdfs label ??
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test9-ok_pml_200407_tonysns1_0.owl");
			entry.put(FIELD_ORIGINAL, 11);
			entry.put(FIELD_DEDUCTIVE_PELLET, 78);  //838
 			entry.put(FIELD_DEDUCTIVE_RDFS, 11); 
			entry.put(FIELD_DEDUCTIVE_SW4J, 78);  //775
			
			data.add(entry);
		}

		
		return data;
	}
	
	// transitive model
	private ArrayList<DataSmartMap> getTestData2(){
		ArrayList<DataSmartMap> data = new ArrayList<DataSmartMap>(); 

				
		{
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "http://inferenceweb.stanford.edu/2004/07/iw.owl");
			entry.put(FIELD_ORIGINAL, 616);
			entry.put(FIELD_DEDUCTIVE_PELLET, 1152);//1443); 
 			entry.put(FIELD_DEDUCTIVE_RDFS, 993);	 
			entry.put(FIELD_DEDUCTIVE_SW4J, 1572);   
			
			data.add(entry);
		}
		
		{
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "http://tw.rpi.edu/2008/04/wine-instance_mpv.rdf");
			entry.put(FIELD_ORIGINAL, 179);
			entry.put(FIELD_DEDUCTIVE_PELLET, 346);//364); 
 			entry.put(FIELD_DEDUCTIVE_RDFS, 206);	 
			entry.put(FIELD_DEDUCTIVE_SW4J, 1572);   
			
			data.add(entry);
		}

		
		{
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "http://tw.rpi.edu/2008/03/wine.owl");
			entry.put(FIELD_ORIGINAL, 627);
			entry.put(FIELD_DEDUCTIVE_PELLET, 1182);//1473); 
 			entry.put(FIELD_DEDUCTIVE_RDFS, 1004);	 
			entry.put(FIELD_DEDUCTIVE_SW4J, 1572);   
			
			// this dataset is too large for pellet to process
			//data.add(entry);
		}
		
		{
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test9-ok_pml_200407_tonysns1_0.owl");
			entry.put(FIELD_ORIGINAL, 627);
			entry.put(FIELD_DEDUCTIVE_PELLET, 1182);//1473); 
 			entry.put(FIELD_DEDUCTIVE_RDFS, 1004);	 
			entry.put(FIELD_DEDUCTIVE_SW4J, 1572);   
			
			data.add(entry);
		}
		
		
		
		return data;
	}
	
	// inconsistent model
	protected ArrayList<DataSmartMap> getTestData3(){
		ArrayList<DataSmartMap> data = new ArrayList<DataSmartMap>(); 

		{
			// transitive subclass
			// restriction 
			// inconsistency
			DataSmartMap entry = new DataSmartMap();
			entry.put(FIELD_URL, "files/deductive_test/test6-onproperty-subproperty-datatypeproperty.owl");

			
			data.add(entry);
		}
		return data;
	}
	private void do_test(Model model, DataSmartMap entry){ 
		if (debug){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println(entry.getAsString(FIELD_URL));
		}
		ArrayList<String> message = new ArrayList<String>();
		String szTemp;
		boolean bMistake=false;
		
		//check_consistency(model);
		szTemp= check_size(model, entry, FIELD_ORIGINAL);
		message.add( szTemp );
		if (!ToolSafe.isEmpty(szTemp))
			bMistake =true;

		
		
		//load pellet model
		OntModel model_pellet = ToolPellet.createOntModel();
		model_pellet.add(model);
		
		
		// additional check
		if (model_pellet.getBaseModel().size()!= model.size()){
			fail("pellet base model is different from original model");
		}

		//load pellet deductive model
		Model model_pellet_deductive=null;
		try {
			model_pellet_deductive = ToolPellet.model_createDeductiveClosure(null, model);

			check_consistency(model_pellet_deductive);
			szTemp= check_size(model_pellet_deductive, entry, FIELD_DEDUCTIVE_PELLET);
			message.add( szTemp );
			if (!ToolSafe.isEmpty(szTemp))
				bMistake =true;
			
		} catch (Sw4jException e) {
			e.printStackTrace();
			
			fail(e.getMessage());
		}
		
		// additional check
		// getDeductionsModel() returns empty results
		//if (model_pellet.getDeductionsModel().size()!= model_pellet_deductive.size()){
		//	fail("pellet deduction model is different from ont.getDeductionsModel model");
		//}

		//load RDFS deductive model
		Model model_rdfs_deductive = model_createTransitiveClosure_RDFS(model);
		
		check_consistency(model_rdfs_deductive);
		szTemp= check_size(model_rdfs_deductive, entry, FIELD_DEDUCTIVE_RDFS) ;
		message.add( szTemp );
		if (!ToolSafe.isEmpty(szTemp))
			bMistake =true;

		
		/*
		//load Sw4j model
		Model model_sw4j_deductive = model_createDeductiveClosure(model);

		check_consistency(model_sw4j_deductive);
		szTemp= check_size(model_sw4j_deductive, entry, FIELD_DEDUCTIVE_SW4J) ;
		message.add( szTemp );
		if (!ToolSafe.isEmpty(szTemp))
			bMistake =true;

		System.out.println("----------------------------------");
		model_sw4j_deductive.remove(model_pellet_deductive);
		ToolJena.printModel(model_sw4j_deductive);
		System.out.println("----------------------------------");
		
		*/
		if (bMistake){
			System.out.println("------------summary----------------------");
			System.out.println(entry.getAsString(FIELD_URL));
			System.out.println(ToolString.printCollectionToString(message));
			fail();
		}
		if (debug){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
	}
	

	
	
	
	
	@SuppressWarnings("unchecked")
	private void check_consistency(Model m){
		if (debug){
			ToolJena.printModel(m);
			System.out.println(m.size());
		}

		OntModel ontmodel= ToolPellet.createOntModel();
		ontmodel.add(m);
		Iterator iter_v = ontmodel.validate().getReports();
		boolean failed = iter_v.hasNext();
		while (iter_v.hasNext()){
			System.out.println(iter_v.next());
		}
		
		if (failed){
			ToolJena.printModel(m);
			fail("bad deductive model");
		}
	}
	
	private String check_size(Model m, DataSmartMap config, String fieldname){
		if (debug){
			System.out.println(fieldname);
			System.out.println("--------------------------------------------------------------");
		}
		String found =  ""+m.size();
		String expected = config.getAsString(fieldname);
		if (!found.equals(expected)){
			return String.format("[%s]: expecte=%s, found=%s \n", fieldname,  expected, found);
		}else{
			return "";
		}
			
	}
		
	
	
	
	
	/**
	 * pellet sparql
	 * @param m
	protected void debug3(Model m){
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
		 */

	


	// only count transtive inference 
	private static Model model_createTransitiveClosure_RDFS(Model m){
		OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_TRANS_INF );
		ToolJena.model_merge(ont, m);
		
		//InfModel ont = ModelFactory.createRDFSModel(m);
		
		Model deduction = ModelFactory.createDefaultModel();
		ToolJena.model_merge(deduction,ont);
		
		//filterMetaStatement(deduction);
		return deduction;
	}


	
	public static Model model_createDeductiveClosure(Model m){
		// copy the model
		OntModel ont = ToolPellet.createOntModel();
		ToolJena.model_merge(ont,m);
		
		if (!ToolJena.isConsistent(ont)){
			System.out.println(ont.validate().getReports().next());
			// cannot work on invalid model
			return null;
		}
		// 
		//getLogger().info(ont.size());
		
		//fix the deductive closure
		do_makeDeductiveClosure_rule1(ont);
		if (!ToolJena.isConsistent(ont)){
			System.out.println(ont.validate().getReports().next());
			// cannot work on invalid model
			return null;
		}

		do_makeDeductiveClosure_rule2(ont);
		//ont.add(ont);

		if (!ToolJena.isConsistent(ont)){
			System.out.println(ont.validate().getReports().next());
			// cannot work on invalid model
			return null;
		}
		
		Model deduction = ModelFactory.createDefaultModel();
		ToolJena.model_merge(deduction,ont);

		//filterMetaStatement(deduction);

		return deduction;
		
	}
	
	
	protected static Model filterMetaStatement(Model m){
		StmtIterator  iter = m.listStatements();
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			// skip definition related to meta ontology
			if (ToolJena.test_meta_namespace(stmt.getSubject()))
				iter.remove();
		}
		return m;
	}
	
	/**
	 * handle anonymous class other than restriction 
	 * @param m
	 */
	private static void do_makeDeductiveClosure_rule1(Model m){
		Property [][] aryProperty = new Property [][]{
			{OWL.oneOf, RDF.type},
			{OWL.unionOf, RDFS.subClassOf},	
			{OWL.intersectionOf, RDFS.subClassOf},	
		};
		boolean [] aryDirection = new boolean []{
			false,	// root  <-- member
			false,	// root  <-- member	
			true,	// root  --> member
		};
		
		//list of anonymous resource having named eqivalent resource
		HashSet<Resource> set_eq = new HashSet<Resource>();
		{
			StmtIterator iter = m.listStatements(null, OWL.equivalentClass, (RDFNode)null);
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement(); 
				if (stmt.getSubject().isURIResource()){
					if (stmt.getObject().isAnon())
						set_eq.add((Resource)stmt.getObject());
				}
				
				if (stmt.getObject().isURIResource()){
					if (stmt.getSubject().isAnon())
						set_eq.add(stmt.getSubject());
				}
			}
		}
		
		Model model_list = ModelFactory.createDefaultModel();
		{
			model_list.add(m.listStatements(null, RDF.first, (RDFNode)null));
			model_list.add(m.listStatements(null, RDF.rest, (RDFNode)null));
		}
		
		for (int i=0; i<aryProperty.length; i++){
			Property prop = aryProperty[i][0];
			StmtIterator iter = m.listStatements(null, prop,(String)null);
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement();
				if (!stmt.getSubject().isAnon())
					continue;
				
				// check if it has equivalent named class
				boolean bHasNamedEqClass =false;
				if (set_eq.size()>0 && stmt.getObject().isAnon()){
					
					bHasNamedEqClass = set_eq.contains(stmt.getSubject());
					/*
					Iterator iter_eq = m_eq.listSubjectsWithProperty(OWL.equivalentClass,stmt.getObject());
					while (iter_eq.hasNext()){
						RDFNode node = (RDFNode)iter_eq.next();
						if (node.isURIResource())
							bHasNamedEqClass =true;
					}
					iter_eq = m_eq.listObjectsOfProperty((Resource)stmt.getObject(), OWL.equivalentClass);
					while (iter_eq.hasNext()){
						RDFNode node = (RDFNode)iter_eq.next();
						if (node.isURIResource())
							bHasNamedEqClass =true;
					}
					*/
				}
				if (bHasNamedEqClass)
					continue;
				
				// make sure all its list members are related to it
				List<RDFNode> inds = ToolJena.getListMembers(model_list, (Resource)stmt.getObject());
				Iterator<RDFNode> iter_ind = inds.iterator();
				while (iter_ind.hasNext()){
					RDFNode ind = iter_ind.next();
					if (ind.isResource()){
						if (aryDirection[i])
							m.add(stmt.getSubject(), aryProperty[i][1],ind );
						else
							m.add((Resource)ind, aryProperty[i][1], stmt.getSubject());
					}
					//m.add(m.createStatement(ind, RDF.type, stmt.getObject()));
				}
			}
		}
	}

	/**
	 * handle transitive inference 
	 * @param m
	 */
	private static void do_makeDeductiveClosure_rule2(Model m){
		
		Resource config = ModelFactory.createDefaultModel()
        					.createResource()
        					.addProperty(ReasonerVocabulary.PROPsetRDFSLevel, "simple");
		Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(config);
		InfModel ont = ModelFactory.createInfModel(reasoner, ModelFactory.createDefaultModel());
		/*
		OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_TRANS_INF );
		ont.setStrictMode( false );
		Iterator<Statement> iter = m.listStatements();
		while (iter.hasNext()){
			Statement stmt = iter.next();
			
			if (stmt.getPredicate().equals(RDFS.subClassOf)){
			}else if (stmt.getPredicate().equals(RDFS.subPropertyOf)){
			}else{
				continue;
			}
			
			ont.add(stmt);
		}
		*/
		ont.add( m.listStatements(null, RDFS.subClassOf,(RDFNode)null));
		ont.add( m.listStatements(null, RDFS.subPropertyOf,(RDFNode)null));
		
		m.add(ont.listStatements(null, RDFS.subClassOf,(RDFNode)null));
		m.add(ont.listStatements(null, RDFS.subPropertyOf,(RDFNode)null));
		
	}	
}

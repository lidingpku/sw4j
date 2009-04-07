/**
MIT License

Copyright (c) 2009 

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

package sw4j.util.jena;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import sw4j.util.DataPVCMap;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

/**
 * provide many useful RDF manipulation functions
 * 
 * @author Li Ding
 * 
 */
public class ToolJena {

	private static Logger getLogger(){
		return Logger.getLogger(ToolJena.class);
	}

	////////////////////////////////////////////////
	// RDF List <=> rdfs:member
	////////////////////////////////////////////////
	/**
	 * parse an RDF:List into a java List object
	 * 
	 * @param m
	 * @param root
	 * @return
	 */
	public static List<RDFNode> getListMembers(Model m, Resource root) {
		return getListMembers(m, root, RDF.first, RDF.rest);
	}

	/**
	 * parse an a list  into a java List object
	 * 
	 * @param m
	 * @param root
	 * @return
	 */
	public static List<RDFNode> getListMembers(Model m, Resource root, Property first, Property rest) {
		ArrayList<RDFNode> data = new ArrayList<RDFNode>();
		Resource node = root;
		while (null != node) {
			NodeIterator iter_obj = m.listObjectsOfProperty(node, first);
			if (iter_obj.hasNext()) {
				data.add(iter_obj.nextNode());
			}

			iter_obj = m.listObjectsOfProperty(node, rest);
			if (iter_obj.hasNext()) {
				node = (Resource) iter_obj.next();
			} else {
				node = null;
			}
		}
		return data;
	}	

	////////////////////////////////////////////////
	// generate a model from the current model
	////////////////////////////////////////////////
	
	/**
	 * generate a new model given a model, generate rdfs:member base on list (first, rest) assertions 
	 * - optionally remove all ds:List assertions 
	 * 
	 * @param m
	 * @param first
	 * @param rest
	 * @param bRemoveListTriple
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void model_update_List2Map(Model m, Property first, Property rest, boolean bRemoveListTriple) {
		Set<Resource> subjects = m.listSubjects().toSet();

		// subjects of list
		Set<Resource> subjects_first = m.listSubjectsWithProperty(first).toSet();
		Set<RDFNode> objects_rest = m.listObjectsOfProperty(rest).toSet();

		// exclude subjects that are of type List
		subjects.removeAll(subjects_first);

		// exclude none root node 
		subjects_first.removeAll(objects_rest);
		
		// add converted list members
		Model mi = ModelFactory.createDefaultModel();
		Iterator<Resource> iter_sub = subjects_first.iterator();
		while (iter_sub.hasNext()) {
			Resource root = iter_sub.next();

			
			List<RDFNode> members = ToolJena.getListMembers(m, root, first, rest);
			Iterator<RDFNode> iter_member = members.iterator();
			
			while (iter_member.hasNext()){
				RDFNode member = iter_member.next();
				mi.add(mi.createStatement(root, RDFS.member, member));
			}
			
			//keep the type of the root
			//mi.add(m.listStatements(root, RDF.type, (RDFNode)null));
		}

		//add the rest triples
		StmtIterator iter = m.listStatements();
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			if (bRemoveListTriple){
				if (!subjects.contains(stmt.getSubject()))
					iter.remove();
			}
		}

		// member statements
		m.add(mi);
	}	
	
	/**
	 * recursively traverse an RDF:List and put triples into a model
	 * 
	 * @param m
	 * @param root
	 * @return
	 */
	/*
	public static Model getModel_byList(Model m, Resource root) {
		Model list_m = ModelFactory.createDefaultModel();
		Resource node = root;
		while (null != node) {
			Iterator iter_obj = m.listStatements(node, RDF.type, RDF.List);
			if (iter_obj.hasNext()) {
				list_m.add((Statement) iter_obj.next());
			}

			iter_obj = m.listStatements(node, RDF.first, (RDFNode) null);
			if (iter_obj.hasNext()) {
				list_m.add((Statement) iter_obj.next());
			}

			iter_obj = m.listStatements(node, RDF.rest, (RDFNode) null);
			if (iter_obj.hasNext()) {
				Statement stmt = (Statement) iter_obj.next();
				list_m.add(stmt);
				if (stmt.getObject().isResource()) {
					node = (Resource) stmt.getObject();
				} else {
					node = null;
				}
			} else {
				node = null;
			}
		}
		return list_m;
	}
    */
	
	/**
	 * create a new model from the given model that includes triples only
	 * describing res.
	 * 
	 * Limitation: we only collect triples having the resource as the subject by
	 * assuming all resources in m are not anonymous. future work may consider
	 * better huristics.
	 * 
	 * 
	 * @param m
	 *            the given graph
	 * @param res
	 *            the resource to be described
	 * @param bRecursive
	 *            recursively get the entire graph rooted from res 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Model getModel_byDescription(Model m, Resource res, boolean bRecursive) {
		Model m_desc = ModelFactory.createDefaultModel();
		m_desc.add(m.listStatements(res, null, (RDFNode) null));
		
		if (bRecursive){
			Set<RDFNode> visited = new HashSet<RDFNode>();
			visited.add(res);
			
			boolean bContinue=true;
			while (bContinue){
				//reset
				bContinue =false;
				
				Set targets = m_desc.listObjects().toSet();
				//remove visited
				targets.removeAll(visited);
				//remove literals
				{
					Iterator<RDFNode> iter = targets.iterator();
					while (iter.hasNext()){
						RDFNode target = iter.next();
						if (target.isLiteral())
							iter.remove();
					}
				}
				
				//update visited
				visited.addAll(targets);
				
				//add new triples if found
				Iterator<RDFNode> iter = targets.iterator();
				while (iter.hasNext()){
					RDFNode target = iter.next();
				
					StmtIterator iter_stmt = m.listStatements((Resource)target, null, (RDFNode) null);
					if (iter_stmt.hasNext()){
						// add new triple
						m_desc.add(iter_stmt);
						bContinue=true;
					}
				}
			}
				
		}
		// TODO maybe we want provide CBD support
		return m_desc;
	}

	////////////////////////////////////////////////
	// namespace
	////////////////////////////////////////////////
	

	
	/** list all namespaces used in this model
	 *  TODO
	 *  
	 * @param m
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static TreeSet<String> namespace_listByJena(Model m){
		TreeSet<String> ret = new TreeSet<String>();
		ret.addAll(m.listNameSpaces().toSet());
		return ret;
	}
	
	public static Set<String> namespace_listByParse(Set<Resource> set_res){
		Set<String> namespaces = new HashSet<String>();
		Iterator<Resource> iter = set_res.iterator();
		while (iter.hasNext()){
			Resource res = iter.next();
			if (res.isURIResource()){
				namespaces.add(DataQname.extractNamespace(res.getURI()));
			}
		}
		return namespaces;
	}	
	
	
	////////////////////////////////////////////////
	// model print
	////////////////////////////////////////////////
	
	/**
	 * print model to a string
	 * 
	 * @param m
	 * @param rdfsyntax
	 * @return
	 */
	public static String printModelToString(Model m, String rdfsyntax) {
		if (null==m)
			return "";
		StringWriter sw = new StringWriter();
		RDFWriter writer = m.getWriter(rdfsyntax);
		writer.setProperty("showXmlDeclaration", "true");
		writer.write(m, sw, null);
		return sw.toString();
	}

	/**
	 * print model to an RDF/XML string
	 * 
	 * @param m
	 * @param rdfsyntax
	 * @return
	 */
	public static String printModelToString(Model m) {
		return  printModelToString(m,  "RDF/XML-ABBREV");
	}

	/**
	 * print model to file
	 * 
	 * @param model
	 * @param szFilename
	 * @return
	 */
	public static boolean printModelToFile(Model model, String szFilename) {
		return printModelToFile(model, new File(szFilename),"RDF/XML-ABBREV" , false);
	}

	public static boolean printModelToFile(Model model, String szFilename, String szRdfSyntax, boolean bGzip) {
		return printModelToFile(model, new File(szFilename), szRdfSyntax,bGzip);
	}
	

	public static boolean printModelToFile(Model model, File f, String szRdfSyntax, boolean bGzip) {
		
		try {
			if (null==model)
				return false;
			
			if (ToolSafe.isEmpty(szRdfSyntax))
				szRdfSyntax = "RDF/XML";
			// cannote use RDF/XML-ABBREV, has some sirious problem in write results
			// negative example: http://inference-web.org/proofs/tptp/Solutions/LAT/LAT195-1/Vampire---9.0/answer.owl
			
			//getLogger().info(	"writing RDF data to file: " + f.getAbsolutePath());

			OutputStream _fos = ToolIO.prepareFileOutputStream(f,false, bGzip);
			RDFWriter writer = model.getWriter(szRdfSyntax);
			if (szRdfSyntax.startsWith("RDF/XML")){
				writer.setProperty("allowBadURIs", true);
				writer.setProperty("showXmlDeclaration", true);
			}
			writer.write(model, _fos, "");
			_fos.close();
			return true;
		} catch (IOException e) {
			getLogger().info(e.getMessage());
			return false;	
			//throw new RuntimeException(ioxc.toString());
		} catch (Sw4jException e) {
			getLogger().info(e.getMessage());
			return false;	
		}

	}	
	
	/**
	 * just print the triples line by line
	 * 
	 * @param m
	 */
	public static void printModel(Model m) {
		StmtIterator iter = m.listStatements();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			System.out.println(stmt);
		}
	}

	
	////////////////////////////////////////////////
	// model analysis
	////////////////////////////////////////////////
	
	/**
	 * split subjects into instance, ontology, and unknown
	 * excluding class/property definition.
	 * 
	 * @param model_data
	 * @return
	 */
	
	public static final int SUBJECT_INSTANCE = 0;
	public static final int SUBJECT_ONTOLOGY = 1;
	public static final int SUBJECT_UNKNOWN = 2;
	@SuppressWarnings("unchecked")
	public static List<Set<Resource>> splitSubjects(Model model_data) {
		ArrayList<Set<Resource>> ret = new ArrayList<Set<Resource>>();
		
		ret.add(new HashSet<Resource>());	// instance
		ret.add(new HashSet<Resource>());	// ontology
		ret.add(new HashSet<Resource>());	// unknown

		ret.get(SUBJECT_UNKNOWN).addAll(model_data.listSubjects().toSet());
		StmtIterator iter = model_data.listStatements(null, RDF.type, (String) null);
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			ret.get(SUBJECT_UNKNOWN).remove(stmt.getSubject());
			
			// skip meta-class
			if (ToolModelAnalysis.testMetaClass(stmt.getObject())) {
				ret.get(SUBJECT_ONTOLOGY).add(stmt.getSubject());
				continue;
			}

			// skip meta-property
			if (ToolModelAnalysis.testMetaProperty(stmt.getObject())) {
				ret.get(SUBJECT_ONTOLOGY).add(stmt.getSubject());
				continue;
			}

			ret.get(SUBJECT_INSTANCE).add(stmt.getSubject());
		}
		return ret;
	}
	
	
	public static Map<Resource, DataInstance> listInstanceDescription(Model m){
		HashMap<Resource, DataInstance> data = new HashMap<Resource, DataInstance>();
		StmtIterator iter = m.listStatements();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource subject = stmt.getSubject();
			DataInstance di = data.get(subject);
			if (null== di){
				di = new DataInstance(subject);
				data.put(subject, di);
			}
			di.addDescription(stmt);
		}
		
		return data;

	}

	// ////////////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////////////

	public static final String NODE_URI = "u"; //

	public static final String NODE_BNODE = "b"; //

	public static final String NODE_LITERAL = "l"; //

	public static String getNodeType(RDFNode node) {
		try {
			Resource temp = (Resource) node;
			if (temp.isAnon()) {
				return NODE_BNODE;
			} else {
				return NODE_URI;
			}
		} catch (Exception e) {
			return NODE_LITERAL;
		}
	}



	public static Individual getIndividual(String uriStr, OntModel model) {
		Individual result = null;
		if (model != null) {
			RDFNode _rdf = null;
			_rdf = model.getIndividual(uriStr);
			if (_rdf != null && _rdf.canAs(Individual.class)) {
				result = (Individual) _rdf.as(Individual.class);
			} else {
				System.out
						.println("DataObjectManager.getIndividual: Could not read individual with URI "
								+ uriStr);
			}
		}
		return result;
	}
	
	/*
	@SuppressWarnings("unchecked")
	public static Resource getMostspecificRDFType(Individual ind){
		List types = ind.listRDFTypes(true).toList();
		if (ToolCommon.safe_is_empty(types)){
			getLogger().info("empty types");
			return null;
		}
		if (types.size()==1){
			getLogger().debug("only one element, no need to work");
			return (Resource)types.iterator().next();
		}

		// collect required namespaces
		Iterator iter = types.iterator();
		LinkedHashSet<String> namespaces = new LinkedHashSet<String>();
		while (iter.hasNext()){
			String ns = ToolURI.extractNamespace(iter.next().toString());
			if (null!=ns)
				namespaces.add(ns);
		}	

		if (namespaces.size()<1){
			getLogger().info("no namespaces has been parsed from types "+ types);
			return null;
		}

		// build the model
		Model m = ModelFactory.createDefaultModel();
		iter = namespaces.iterator();
		while (iter.hasNext()){
			String ns = iter.next().toString();
			try {
				ModelManager.get().readModel(m, ns);
			} catch (SwutilException e) {
				getLogger().error(e.getMessage());
			}
		}
		
		return getMostspecificRDFType(types,m);
	}
	*/
	public static Resource getMostspecificRDFType(List<Resource> types, Model m){
		if (null==m){
			getLogger().info("empty model");
			return null;
		}
		
		if (ToolSafe.isEmpty(types)){
			getLogger().info("empty types");
			return null;
		}

		if (types.size()==1){
			getLogger().debug("only one element, no need to work");
			return types.iterator().next();
		}
		
		// build a map for rdfs:subClassOf relations
		DataPVCMap<Resource,Resource> mapSubclass = new DataPVCMap<Resource,Resource>(true);
		{
			StmtIterator iter = m.listStatements(null, RDFS.subClassOf, (RDFNode)null);
			while (iter.hasNext()){
				Statement stmt =  iter.nextStatement();
				if (!stmt.getObject().isResource()){
					getLogger().info("encountered a bad RDF triple "+ stmt);
				}
				mapSubclass.add( stmt.getSubject(), (Resource)stmt.getObject());
			}
		}
		
		// find the most specific type
		HashSet<Resource> frontClasses = new HashSet<Resource>();
		frontClasses.addAll(types);
		HashSet<Resource> superClasses = new HashSet<Resource>();
		{
			Iterator<Resource> iter = frontClasses.iterator();
			while (iter.hasNext()){
				Resource curRes = iter.next();
				if (superClasses.contains(curRes)){
					continue;
				}
				List<Resource> classes = mapSubclass.getValues(curRes);
				if (null!=classes){
					classes.remove(curRes); // should not add self
					superClasses.addAll(classes);
				}
			}
		}
		frontClasses.removeAll(superClasses);
		
		if (frontClasses.size()==1)
			return frontClasses.iterator().next();
		else{
			getLogger().info("expected exactly one most specific class, but found "+ frontClasses);
			return null;
		}
	}
	

	

	public static Model  sparql_createModel(String queryString){	
		Query query = QueryFactory.create(queryString) ;
		Dataset dataset = DatasetFactory.create(query.getGraphURIs(), query.getNamedGraphURIs());
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
		Model resultModel =null;
		if (query.isDescribeType())
			resultModel = qexec.execDescribe() ;
		else if (query.isConstructType())
			resultModel = qexec.execConstruct() ;
		else
			return null;
		
		qexec.close() ;
		return resultModel;
		
	}

	@SuppressWarnings("unchecked")
	public static Dataset prepareDataset(Query query,  boolean usePellet){
		Dataset dataset = DatasetFactory.create(query.getGraphURIs(), query.getNamedGraphURIs());
		
		if (usePellet){
			Iterator iter  = dataset.listNames();
			while (iter.hasNext()){
				String name = (String)iter.next();
				Model m = dataset.getNamedModel(name);
				Model deductive_closure = model_createDeductiveClosureRDFS(model_createDeductiveClosureOWLDL(m));
				ToolJena.model_merge(m ,deductive_closure);
			}
			
			Model m = dataset.getDefaultModel();
			Model deductive_closure = model_createDeductiveClosureRDFS(model_createDeductiveClosureOWLDL(m));
//			Model deductive_closure = getDeductiveClosureOWLDL(m);
			ToolJena.model_merge(m ,deductive_closure);
		}
		return dataset;
	}
	
	
	public static String  sparql_select(String queryString,  boolean usePellet){
	
		Query query = QueryFactory.create(queryString) ;
	
		//System.out.println(queryString);
		Dataset dataset = prepareDataset(query, usePellet);
		
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
		ResultSet results =null;
		if (query.isSelectType())
			results = qexec.execSelect() ;
		else{
			return "";
		}

		//System.out.println(results.getRowNumber());
		// Output query results	
		ByteArrayOutputStream sw = new ByteArrayOutputStream();
		ResultSetFormatter.out(sw,results, query);
		String ret = sw.toString();
		//ret  = ret.replaceAll("\r", "<br/>\r");
		//String ret = ResultSetFormatter.asXMLString(results);
		
		// Important - free up resources used running the query
		qexec.close() ;
		
		return ret;
	}	

	public static  OntModel model_createDeductiveClosureOWLDL(Model m){
		OntModel ont = ToolPellet.createOntModel();
		ont.setStrictMode( false );
		model_merge(ont, m);
		ont.loadImports();
		return ont;
	}

	public static OntModel model_createDeductiveClosureRDFS(Model m){
		OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_TRANS_INF );
		ont.setStrictMode( false );
		model_merge(ont, m);
		return ont;
	}
	
	public static Model model_createDeductiveClosure(Model m){
		// copy the model
		OntModel ont = ToolPellet.createOntModel();
		model_merge(ont,m);
		
		if (!ont.validate().isValid()){
			// cannot work on invalid model
			return null;
		}
		return model_createDeductiveClosure(ont);
	}
	
	public static Model model_createDeductiveClosure(OntModel ont){
		// addsu
		
		// 
		//getLogger().info(ont.size());
		
		//fix the deductive closure
		do_makeDeductiveClosure_rule1(ont);
		do_makeDeductiveClosure_rule2(ont);
		//ont.add(ont);
		
		
		// return result
		//getLogger().info(ont.size());
		Model result_model = ModelFactory.createDefaultModel();
		StmtIterator  iter = ont.listStatements();
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			// skip definition related to meta ontology
			if (test_meta_namespace(stmt.getSubject()))
				continue;

			//if (OWL.Nothing.equals(stmt.getObject()))
			//	continue;
			
			result_model.add(stmt);
		}

		model_copyNsPrefix(result_model,ont);
		return result_model;
		
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
					
					bHasNamedEqClass = set_eq.contains(stmt.getObject());
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
							m.add((Resource)stmt.getObject(), aryProperty[i][1],(Resource)ind );
						else
							m.add((Resource)ind, aryProperty[i][1], stmt.getObject());
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
	
	public static boolean test_meta_namespace(RDFNode node){
		if (!node.isURIResource())
			return false;
		Resource res = (Resource) node;
		if (ToolSafe.isEmpty(res.getNameSpace()))
			return false;
		
		if (RDF.getURI().equals(res.getNameSpace())){
			return true;
		}
		if (RDFS.getURI().equals(res.getNameSpace())){
			return true;
		}
		if (OWL.getURI().equals(res.getNameSpace())){
			return true;
		}
		
		return false;
	}
	
	/**
	 * add m to target
	 * @param m
	 * @param ref
	 */
	public static void model_merge(Model m, Model ref){
		m.add(ref);
		model_copyNsPrefix(m,ref);
	}
	
	/**
	 * copy ns prefix declaration from the other model
	 * @param m
	 * @param ref
	 */
	public static void model_copyNsPrefix(Model m, Model ref){
		NsIterator iter = m.listNameSpaces();
		while (iter.hasNext()){
			String ns = iter.nextNs();
			String prefix = ref.getNsURIPrefix(ns);
			if (null!= prefix)
				m.setNsPrefix(prefix,ns);
		}
 	}	
	/**
	 * add m to target
	 * @param target
	 * @param m
	 */
	public static Model model_clone( Model m){
		Model target= ModelFactory.createDefaultModel();
		model_merge(target, m);
		return target;
	}

/*
	public static TreeSet<String> extractLinks(Model m, boolean bUseImport, boolean bUseNamespace){
		TreeSet<String> new_urls = new TreeSet<String>(); 
		
		// find new urls using owl:imports
		if (null!=m){
			if (bUseImport){
				//all_namespaces.add(szOntologyURI);	// add imported namespace
				NodeIterator iter_import = m.listObjectsOfProperty(OWL.imports);
				while (iter_import.hasNext()){
					RDFNode node = iter_import.nextNode();
					if (node.isURIResource()){
						String szNewURL = ((Resource)node).getURI();
						szNewURL = ToolURI.extractNamespaceCanonical(szNewURL);
						if (null!= szNewURL)
							new_urls.add(szNewURL);
					}
				}
			}
			if (bUseNamespace){
				//all_namespaces.add(szOntologyURI);	// add imported namespace
				ToolModelStat stat = new ToolModelStat();
				stat.traverse(m);
	
				Iterator<String> iter_ns = stat.getReferencedOntologies().iterator();
				while (iter_ns.hasNext()){
					String szNewURL = iter_ns.next();
					szNewURL = ToolURI.extractNamespaceCanonical(szNewURL);
					if (null!= szNewURL)
						new_urls.add(szNewURL);
					
				}
			}
		}
		
		// skip any RDF, RDFS, OWL namespace
		new_urls.removeAll(ToolModelStat.getMetaMamespaces());
		
		return new_urls;
	}
*/
	
	
	public static  String fromatRDFnode(RDFNode node, boolean bDetail){
		if (node.isURIResource()){
			DataQname dq;
			try {
				dq = DataQname.create(getNodeString(node));
				String szLn = dq.getLocalname();
				String szNs = dq.getNamespace();
				
				if (!ToolSafe.isEmpty(szLn)&& !ToolSafe.isEmpty(szNs) && !bDetail){
					return String.format("[%s]",szLn); 
				}
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			return "<"+node.toString()+">";
		}else if (node.isAnon()){
			return "<"+node.toString()+">";			
		}else{
			return node.toString();
		}
	}	
	
	public static String getNodeString(RDFNode node) {
		String type = getNodeType(node);
		if (type.equals(NODE_LITERAL)) {
			return ((Literal) node).getString().trim();
		} else {
			Resource res = (Resource) node;
			return getNodeStringRes(res);
		}
	}

	public static String getNodeStringRes(Resource res) {
		// I have seen some ugly swds which contain white-spaces at the end of
		// URI
		// e.g.
		// http://www.dbis.informatik.uni-frankfurt.de/~tolle/RDF/FondsSchema/Schema/Asset.rdf
		// so I have to trim the NodeString when accessing the URI.
		return res.toString().trim();
	}	
	public static Literal createDatetimeLiteral(Model m, long millisecond){
		return m.createTypedLiteral(ToolString.formatXMLDateTime(millisecond), XSDDatatype.XSDdateTime);
	}

	@SuppressWarnings("unchecked")
	public static Set listLinkedResources(Model m){
		Set<RDFNode> ret = m.listObjects().toSet();
		ret.addAll(m.listSubjects().toSet());
		ret.removeAll(m.listObjectsOfProperty(RDF.type).toSet());

		Iterator<RDFNode> iter = ret.iterator();
		while (iter.hasNext()){
			RDFNode node = iter.next();
			if (!node.isURIResource()){
				iter.remove();
				continue;
			}
			//to simplify the case, only include Resource with URI
			try {
				DataQname dq = DataQname.create(getNodeString(node));
				if (dq.hasLocalname()){
					iter.remove();
					continue;
				}
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
/*			//as of now, skip obvious non-rdf data by file extension
			String uri = node.toString().toLowerCase();
			final String [] FILTER = new String []{
				".jpeg",	
				".jpg",	
				".png",	
				".gif",	
			};
			for (int i=0; i<FILTER.length; i++){
				if (uri.endsWith(FILTER[i]))
					iter.remove();
			}
*/		}
		
		return ret;
	}
	
}

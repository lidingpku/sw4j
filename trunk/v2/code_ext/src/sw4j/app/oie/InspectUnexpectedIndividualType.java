package sw4j.app.oie;


import sw4j.rdf.util.ToolJena;
import sw4j.task.common.AbstractTaskDesc;
import sw4j.util.Sw4jMessage;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * TODO
 * 
 * @author dingl
 *
 */
public class InspectUnexpectedIndividualType extends AbstractTaskDesc{
	public static final String REPORT_TITLE ="Unexpected individual type evaluation";
	public static final String REPORT_DESC ="This service checks if the actual instance types is compatible with the expected ones by the corresponding ontologies";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}
	
	public static final String ERROR_SUMMARY_1 = "individual type unexpected by rdfs:domain";
	public static final String ERROR_SUMMARY_2 = "individual type unexpected by rdfs:range";
	public static final String ERROR_SUMMARY_3 = "individual type unexpected by owl:allValuesFrom";
	
	public static InspectUnexpectedIndividualType inspect(Model model_data, Model model_onto, Model model_all){
		InspectUnexpectedIndividualType task= new InspectUnexpectedIndividualType();

		task.do_validate(model_data, model_onto, model_all);
		return task;
	}
	
	/**
	 * 
	 * Unexpected Individual Type 
	 * -- each known type of an individual should be  
	 * compatible with (being subclass or superclass) each expected types in the corresponding ontology.
	 * 
	 * @param model_data
	 * @param model_onto
	 * @return
	 */	
	public boolean do_validate(Model model_data, Model model_onto, Model model_all){
		boolean bRet =true;
		//System.out.println(model_data.getNsPrefixMap());

		//System.out.println(m_sz_sparql_cardinality);
		//ToolJena.printModel(model_data);
		//System.out.println("================");
		//ToolJena.printModel(model_onto);

		String [][] aryQuery = new String[][]{
			{sz_sparql_domain, ERROR_SUMMARY_1, "rdfs:domain"},
			{sz_sparql_range, ERROR_SUMMARY_2, "rdfs:range"},
			{sz_sparql_allvaluesfrom, ERROR_SUMMARY_3, "owl:allValuesFrom"},
		};
		
		//System.out.println(model_all.listStatements(null, RDFS.subClassOf, (String)null).toSet().toString().replaceAll("\\]", "\n"));
		//System.out.println(model_all.listStatements().toSet().toString().replaceAll("\\],","\n"));

		for (int i=0; i<aryQuery.length; i++){
			String szQuery = aryQuery[i][0];			
			Query query = QueryFactory.create(szQuery) ;			
			DataSource datasource = DatasetFactory.create(model_all);
			//Instance data: D
			datasource.addNamedModel("http://ex.com/foo", model_data);
			//Instance data + All reference ontologies: D+O'			
			Model model_data_onto = ModelFactory.createDefaultModel();
			ToolJena.model_merge(model_data_onto, model_data);
			ToolJena.model_merge(model_data_onto, model_onto);			
			datasource.addNamedModel("http://ex.com/data_onto", model_data_onto);	
			
			QueryExecution qexec = QueryExecutionFactory.create(query, datasource) ;
			ResultSet results = qexec.execSelect();
			
			while (results.hasNext()){
				QuerySolution  solution  = results.nextSolution();
				
				Integer error_level =Sw4jMessage.STATE_WARNING;
				String error_summary = aryQuery[i][1];
				
				translateQueryResult(solution, szQuery, aryQuery[i][2]);
				
				String error_details = translateQueryResultDetails(solution, szQuery, aryQuery[i][2]);
				//System.out.println(error_details);
				String error_creator = this.getClass().getSimpleName();

				if (null == this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true))
					break;
			}
		}	

		return bRet;
	}
	
	private String translateQueryResult (QuerySolution solution, String szSparql, String szMessage){
		String szRet = solution.toString();
		Resource ind = (Resource)solution.get("?i");
		Resource prop = (Resource)solution.get("?p");
		Resource cls = (Resource)solution.get("?c");
		Resource cls1 = (Resource)solution.get("?c1");
		
		szRet = String.format("Individual %s: its known type %s " +
				"is not compatible with the expected type %s which is suggested by the %s restriction on the property %s. " ,
				ToolJena.fromatRDFnode(ind,false),
				ToolJena.fromatRDFnode(cls,false),
				ToolJena.fromatRDFnode(cls1,false),
				szMessage,
				ToolJena.fromatRDFnode(prop,false)
		);
		
		return szRet;
	}
	
	private String translateQueryResultDetails (QuerySolution solution, String szSparql, String szMessage){
		String szRet = solution.toString();
		Resource ind = (Resource)solution.get("?i");
		Resource prop = (Resource)solution.get("?p");
		Resource cls = (Resource)solution.get("?c");
		Resource cls1 = (Resource)solution.get("?c1");
		
		if (sz_sparql_domain.equals(szSparql))
			szRet = String.format("Individual %s: its known type %s " +
					"is not compatible with the expected type %s which is suggested by the %s restriction on the property %s. " +
					"Check the triple (%s, %s, ...) " ,
				ToolJena.fromatRDFnode(ind,true),
				ToolJena.fromatRDFnode(cls,true),
				ToolJena.fromatRDFnode(cls1,true),
				szMessage,
				ToolJena.fromatRDFnode(prop,true),
				ToolJena.fromatRDFnode(ind,true),
				ToolJena.fromatRDFnode(prop,true)
				);
		else
			szRet = String.format("Individual %s: its known type %s " +
					"is not compatible with the expected type %s suggested by the %s restriction on the property %s. " + 
					"Check the triple (..., %s, %s .) " ,
					ToolJena.fromatRDFnode(ind,true),
					ToolJena.fromatRDFnode(cls,true),
					ToolJena.fromatRDFnode(cls1,true),
					szMessage,
					ToolJena.fromatRDFnode(prop,true),
					ToolJena.fromatRDFnode(prop,true),
					ToolJena.fromatRDFnode(ind,true)
				);
			
		
		return szRet;
	}
/*
	private String translateQueryResultType (QuerySolution solution, String szSparql, String szMessage){
		String szRet = solution.toString();
		Resource prop = (Resource)solution.get("?p");
		Resource cls = (Resource)solution.get("?c");
		Resource cls1 = (Resource)solution.get("?c1");
		
		if (sz_sparql_domain.equals(szSparql))
			szRet = String.format("Individual x: its known type %s " +
					"is not compatible with the expected type %s which is suggested by the %s restriction on the property %s. " +
					"Check the triple (x, %s, ...) " ,
				ToolJena.fromatRDFnode(cls,true),
				ToolJena.fromatRDFnode(cls1,true),
				szMessage,
				ToolJena.fromatRDFnode(prop,true),
				ToolJena.fromatRDFnode(prop,true)
				);
		else
			szRet = String.format("Individual x: its known type %s " +
					"is not compatible with the expected type %s suggested by the %s restriction on the property %s. " + 
					"Check the triple (..., %s, x .) " ,
					ToolJena.fromatRDFnode(cls,true),
					ToolJena.fromatRDFnode(cls1,true),
					szMessage,
					ToolJena.fromatRDFnode(prop,true),
					ToolJena.fromatRDFnode(prop,true)
				);
			
		
		return szRet;
	}
	
*/
	
	String sz_sparql_domain =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c ?p  ?c1 " +"\n" +
		"WHERE { " +"\n" +
			"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?i  ?p  ?x . }" + "\n" +
			"	GRAPH <http://ex.com/data_onto> {" +"\n" +
					"?i rdf:type ?c .  "+ "\n" +
					"?p  rdfs:domain  ?c1 . }" +"\n" +
			"	OPTIONAL { ?c rdfs:subClassOf ?c3 .   " +
			"			?c3 rdfs:subClassOf ?c1 . }" +"\n" +
			"	OPTIONAL { ?c1 rdfs:subClassOf ?c4 .   " +
			"			?c4 rdfs:subClassOf ?c . }" +"\n" +			
			"	FILTER( !isBlank(?c) && (?c != <http://www.w3.org/2002/07/owl#Thing>) && !isBlank(?c1) && !BOUND(?c3) && !BOUND(?c4))  " +"\n" +
		"}" +"\n";

	String sz_sparql_range =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c ?p  ?c1 " +"\n" +
		"WHERE { " +"\n" +
			"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?x  ?p  ?i . }" + "\n" +
			"	GRAPH <http://ex.com/data_onto> {" +"\n" +
					"?i rdf:type ?c .  "+ "\n" +
					"?p  rdfs:range  ?c1 . }" +"\n" +
			"	OPTIONAL { ?c rdfs:subClassOf ?c3 .   " +
			"			?c3 rdfs:subClassOf ?c1 . }" +"\n" +
			"	OPTIONAL { ?c1 rdfs:subClassOf ?c4 .   " +
			"			?c4 rdfs:subClassOf ?c . }" +"\n" +			
			"	FILTER( !isBlank(?c) && (?c != <http://www.w3.org/2002/07/owl#Thing>) && !isBlank(?c1) && !BOUND(?c3) && !BOUND(?c4))  " +"\n" +
		"}" +"\n";
			
	String sz_sparql_allvaluesfrom =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c  ?p ?c1 " +"\n" +
		"WHERE { " +"\n" +
			"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?x  ?p  ?i .}" + "\n" +
			"	GRAPH <http://ex.com/data_onto> {" +"\n" +			
					"?x rdf:type ?c2 . } "+ "\n" +
			"?i rdf:type ?c .  "+ "\n" +
			"?c2 rdfs:subClassOf ?c20 ." +"\n" +
			"?c20 rdfs:subClassOf ?r ." +"\n" +
			"?r owl:onProperty   ?p ." +"\n" +
			"?r owl:allValuesFrom ?c1 ." +"\n" +
			"OPTIONAL { ?c rdfs:subClassOf ?c3 .   " +
			"			?c3 rdfs:subClassOf ?c1 . }" +"\n" +
			"OPTIONAL { ?c1 rdfs:subClassOf ?c4 .   " +
			"			?c4 rdfs:subClassOf ?c . }" +"\n" +			
			"	FILTER( !isBlank(?c) && (?c != <http://www.w3.org/2002/07/owl#Thing>) && !isBlank(?c1) && !BOUND(?c3) && !BOUND(?c4))  " +"\n" +
		"}" +"\n";
	
	/*
	String sz_sparql_domain =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c ?p  ?c1 " +"\n" +
//		"FROM NAMED <http://ex.com/foo> " +"\n" +
		"WHERE { " +"\n" +
			"	GRAPH <http://ex.com/foo> {" +"\n" +
			"?i  ?p  ?x . }" + "\n" +

//			"{" +"\n" +
			"?i rdf:type ?c .  "+ "\n" +
			"?p  rdfs:domain  ?c1 ." +"\n" +
			"FILTER( !isBlank(?c) )  " +"\n" +
			"FILTER( !isBlank(?c1) )  " +"\n" +
			"OPTIONAL { ?c rdfs:subClassOf ?c3 .   " +
			"			?c3 rdfs:subClassOf ?c1 . }" +"\n" +
			"FILTER( !BOUND(?c3) )  " +"\n" +
			"OPTIONAL { ?c1 rdfs:subClassOf ?c4 .   " +
			"			?c4 rdfs:subClassOf ?c . }" +"\n" +
			"FILTER( !BOUND(?c4) )  " +"\n" +
//		"}" +"\n" +
		"}" +"\n";
	
	String sz_sparql_range =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c  ?p ?c1 " +"\n" +
//		"FROM NAMED <http://ex.com/foo> " +"\n" +
		"WHERE { " +"\n" +
		"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?x  ?p  ?i . }" +"\n" +
//		"{" +"\n" +
			"?i rdf:type ?c .  "+ "\n" +
			"?p  rdfs:range  ?c1 ." +"\n" +
			"FILTER( !isBlank(?c) )  " +"\n" +
			"FILTER( !isBlank(?c1) )  " +"\n" +
			"OPTIONAL { ?c rdfs:subClassOf ?c3 .   " +
			"			?c3 rdfs:subClassOf ?c1 . }" +"\n" +
			"FILTER( !BOUND(?c3) )  " +"\n" +
			"OPTIONAL { ?c1 rdfs:subClassOf ?c4 .   " +
			"			?c4 rdfs:subClassOf ?c . }" +"\n" +
			"FILTER( !BOUND(?c4) )  " +"\n" +
//		"}" +"\n" +
		"}" +"\n";
	

	String sz_sparql_allvaluesfrom =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c  ?p ?c1 " +"\n" +
//		"FROM NAMED <http://ex.com/foo> " +"\n" +
		"WHERE { " +"\n" +
		"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?x  ?p  ?i ." +"\n" +
					"}" +
		"	GRAPH ?g {" +"\n" +
					"?i rdf:type ?c .  "+ "\n" +
					"}" +
//		"{" +"\n" +
			"?o rdf:type ?c .  "+ "\n" +
			"?o rdf:type ?c1 .  "+ "\n" +
			"FILTER( ?c!=?c1 )  " +"\n" +
			"?r rdf:type owl:Restriction ." +"\n" +
			"?r owl:onProperty   ?p ." +"\n" +
			"?r owl:allValuesFrom ?c1 ." +"\n" +
			"OPTIONAL { ?c rdfs:subClassOf ?c3 .   " +
			"			?c3 rdfs:subClassOf ?c1 . }" +"\n" +
			"FILTER( !BOUND(?c3) )  " +"\n" +
			"OPTIONAL { ?c1 rdfs:subClassOf ?c4 .   " +
			"			?c4 rdfs:subClassOf ?c . }" +"\n" +
			"FILTER( !BOUND(?c4) )  " +"\n" +
//		"}" +"\n" +
		"}" +"\n";
	
	String sz_sparql_allvaluesfrom =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c  ?p ?c1 " +"\n" +
//		"FROM NAMED <http://ex.com/foo> " +"\n" +
		"WHERE { " +"\n" +
		"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?x  ?p  ?i .}" + "\n" +
//		"{" +"\n" +
			"?i rdf:type ?c .  "+ "\n" +
			"?x rdf:type ?c2 .  "+ "\n" +
			"?c2 rdfs:subClassOf ?r ." +"\n" +
			"?r owl:onProperty   ?p ." +"\n" +
			"?r owl:allValuesFrom ?c1 ." +"\n" +
			"FILTER( !isBlank(?c) )  " +"\n" +
			"FILTER( !isBlank(?c1) )  " +"\n" +
			"OPTIONAL { ?c rdfs:subClassOf ?c3 .   " +
			"			?c3 rdfs:subClassOf ?c1 . }" +"\n" +
			"FILTER( !BOUND(?c3) )  " +"\n" +
			"OPTIONAL { ?c1 rdfs:subClassOf ?c4 .   " +
			"			?c4 rdfs:subClassOf ?c . }" +"\n" +
			"FILTER( !BOUND(?c4) )  " +"\n" +
//		"}" +"\n" +
		"}" +"\n";
		*/
}

package sw4j.task.oie;

import sw4j.task.common.AbstractTaskDesc;
import sw4j.util.Sw4jMessage;
import sw4j.util.rdf.ToolJena;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * we currently only consider cardinality 1
 * 
 * @author Li Ding
 *
 */
public class InspectMissingPropertyValue extends AbstractTaskDesc{
	public static final String REPORT_TITLE ="Missing property value evaluation";
	public static final String REPORT_DESC ="This service checks if the instances missing property values according to the owl:cardinality and owl:minCardinality restrictions in the corresponding ontologies";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}

	public static final String ERROR_SUMMARY_1 = "missing value according to owl:minCardinality";
	public static final String ERROR_SUMMARY_2 = "missing value according to owl:cardinality";
	public static final String ERROR_SUMMARY_3 = "missing value according to owl:minCardinality (inferred type)";
	public static final String ERROR_SUMMARY_4 = "missing value according to owl:cardinality (inferred type)";
	
	public static InspectMissingPropertyValue inspect(Model model_data, Model model_all){
		InspectMissingPropertyValue task= new InspectMissingPropertyValue();

		task.do_validate(model_data, model_all);
		return task;
	}
	
	/**
	 * check for 
	 * 
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
SELECT ?i ?p  ?c 
FROM NAMED <__g_data__>
WHERE { 
	{
		?c rdfs:subClassOf ?r.
		?r rdf:type owl:Restriction .
		?r owl:onProperty   ?p .
		?r owl:cardinality ?card .
		FILTER( ?card = 1 ) 
		OPTIONAL { ?i ?p ?o .    }
			FILTER( !BOUND(?i) )
	}

	GRAPH <g_data> {
		?i rdf:type ?c .
	}
}

	 * @param model_data
	 * @param model_onto
	 * @return
	 */	
	@SuppressWarnings("unchecked")
	public boolean do_validate(Model model_data, Model model_all){
		boolean bRet =true;
		
		//System.out.println(m_sz_sparql_cardinality);
		//ToolJena.printModel(model_all);

		String [][] aryQuery = new String[][]{
			{sz_sparql_minCardinality, ERROR_SUMMARY_1},
			{sz_sparql_cardinality, ERROR_SUMMARY_2},
			{sz_sparql_minCardinality_inf, ERROR_SUMMARY_3},
		};
		
		//debug code
		//System.out.println("------");
		//ToolJena.printModel(model_all);
		//System.out.println("------");
		//ToolJena.printModel(model_data);
		//System.out.println("------");

		for (int i=0; i<aryQuery.length; i++){
			String szQuery = aryQuery[i][0];
			
			Query query = QueryFactory.create(szQuery) ;
			
			DataSource datasource = DatasetFactory.create(model_all);
			datasource.addNamedModel("http://ex.com/foo", model_data);
			
			
			QueryExecution qexec = QueryExecutionFactory.create(query, datasource) ;
			ResultSet results = qexec.execSelect() ;
			
			while (results.hasNext()){
				QuerySolution  solution  = results.nextSolution();
				
				
				Integer error_level =Sw4jMessage.STATE_WARNING;
				String error_summary = aryQuery[i][1];
				
				translateQueryResult(solution);
				
				String error_details =translateQueryResultDetails(solution);
				String error_creator = this.getClass().getSimpleName();

				if (null == this.getReport().addEntry(error_level, error_summary, error_creator, error_details, true))
					break;
			}
		}	

		return bRet;
	}
	
	private String translateQueryResult (QuerySolution solution){
		String szRet = solution.toString();
		Resource ind = (Resource)solution.get("?i");
		Resource prop = (Resource)solution.get("?p");
		Resource cls = (Resource)solution.get("?c");
		
		szRet = String.format(
				"Individual %s : instance of %s should have at least one value for the property %s.",
				ToolJena.fromatRDFnode(ind,false),
				ToolJena.fromatRDFnode(cls,false),
				ToolJena.fromatRDFnode(prop,false)
				);
			
		
		return szRet;
	}
	

	private String translateQueryResultDetails (QuerySolution solution){
		String szRet = solution.toString();
		Resource ind = (Resource)solution.get("?i");
		Resource prop = (Resource)solution.get("?p");
		Resource cls = (Resource)solution.get("?c");
		
		szRet = String.format(
				"According to the applicable cardinality restrictions in the corresponding ontologies, the individual %s which has type %s should have at least one value for the property %s."+
				"You are expected to add the triple in the form of (%s, %s, ...) to the instance data. " ,
				ToolJena.fromatRDFnode(ind, true),
				ToolJena.fromatRDFnode(cls, true),
				ToolJena.fromatRDFnode(prop, true),
				ToolJena.fromatRDFnode(ind, true),
				ToolJena.fromatRDFnode(prop, true)
				);
			
		
		return szRet;
	}


	String sz_sparql_minCardinality= "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
	"SELECT ?i ?p  ?c " +"\n" +
//	"FROM NAMED <http://ex.com/foo> " +"\n" +
	"WHERE { " +"\n" +
	"	{GRAPH <http://ex.com/foo> {" +"\n" +
	"?i rdf:type ?c .}  "+ "\n" +
//	"{" +"\n" +
		"?i rdf:type ?c .   "+ "\n" +
		"?c rdfs:subClassOf ?c1." +"\n" +
		"?c1 rdfs:subClassOf ?r." +"\n" +
		"?r rdf:type owl:Restriction ." +"\n" +
		"?r owl:onProperty   ?p ." +"\n" +
		"?r owl:minCardinality ?card ." +"\n" +
		"FILTER( ?card > 0 ) " +"\n" +
		"}" +
		"OPTIONAL { ?i ?p ?o .   }" +"\n" +
		"FILTER( !BOUND(?o) )  " +"\n" +
//	"}" +"\n" +
	"}" +"\n";
	
	String sz_sparql_cardinality= "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
	"SELECT ?i ?p  ?c " +"\n" +
//	"FROM NAMED <http://ex.com/foo> " +"\n" +
	"WHERE { " +"\n" +
	"	GRAPH <http://ex.com/foo> {" +"\n" +
	"?i rdf:type ?c .}  "+ "\n" +
//	"{" +"\n" +
		"?c rdfs:subClassOf ?c1." +"\n" +
		"?c1 rdfs:subClassOf ?r." +"\n" +
		"?r rdf:type owl:Restriction ." +"\n" +
		"?r owl:onProperty   ?p ." +"\n" +
		"?r owl:cardinality ?card ." +"\n" +
		"FILTER( ?card > 0 ) " +"\n" +
		"OPTIONAL { ?i ?p ?o .   }" +"\n" +
		"FILTER( !BOUND(?o) )  " +"\n" +
//	"}" +"\n" +
	"}" +"\n";

	
	String sz_sparql_minCardinality_inf= "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
	"SELECT ?i ?p  ?c " +"\n" +
//	"FROM NAMED <http://ex.com/foo> " +"\n" +
	"WHERE { " +"\n" +
		"GRAPH <http://ex.com/foo> " +
		"{" +"\n" +
			"?x  ?y  ?i ." +
		"}  "+ "\n" +
//		"{" +"\n" +
		"?c rdfs:subClassOf ?c1." +"\n" +
		"?c1 rdfs:subClassOf ?r." +"\n" +
		"?r rdf:type owl:Restriction ." +"\n" +
		"?r owl:onProperty   ?p ." +"\n" +
		"?r owl:minCardinality ?card ." +"\n" +
		"FILTER( ?card > 0 ) " +"\n" +
		"OPTIONAL { ?i ?p ?o .   }" +"\n" +
		"FILTER( !BOUND(?o) )  " +"\n" +
		"?i rdf:type ?c . " +"\n" +
//		"}" +"\n" +
	"}" +"\n";	
	
}

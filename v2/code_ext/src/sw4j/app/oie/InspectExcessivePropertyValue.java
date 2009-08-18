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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Literal;
import java.util.HashMap;
import java.util.Map;



public class InspectExcessivePropertyValue extends AbstractTaskDesc{
	public static final String REPORT_TITLE ="Excessive property value evaluation";
	public static final String REPORT_DESC ="This service checks if the instances have excessive property values according to the owl:cardinality and owl:maxCardinality restrictions in the corresponding ontologies";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}	
	
	public static final String ERROR_SUMMARY_1 = "excessive value according to owl:cardinality.";
	public static final String ERROR_SUMMARY_2 = "excessive value according to owl:maxCardinality.";
	
	public static InspectExcessivePropertyValue inspect(Model model_data, Model model_all){
		InspectExcessivePropertyValue task= new InspectExcessivePropertyValue();

		task.do_validate(model_data, model_all);
		return task;
	}		
	
	/**
	 * 
	 * Nonspecific Individual Type 
	 * @param model_data
	 * @param model_onto
	 * @return
	 */	
	@SuppressWarnings("unchecked")
	public boolean do_validate(Model model_data, Model model_all){
		boolean bRet =true;
		
		String [][] aryQuery = new String[][]{				
				{sz_sparql_cardinality, ERROR_SUMMARY_1},
				{sz_sparql_maxCardinality, ERROR_SUMMARY_2},
			};
		
		for (int i=0; i<aryQuery.length; i++){
			String szQuery = aryQuery[i][0];			
			Query query = QueryFactory.create(szQuery) ;			
			DataSource datasource = DatasetFactory.create(model_all);			
			datasource.addNamedModel("http://ex.com/foo", model_data);			
			QueryExecution qexec = QueryExecutionFactory.create(query, datasource) ;
			ResultSet results = qexec.execSelect();		

			Resource ind = null;
			Resource p = null;
			Property pro = null;
			//Resource x = null;
			Resource c = null;
			Literal card = null;
			int n = 0;
			Map<Resource, Resource> checked_ind_p_pairs = new HashMap<Resource, Resource>();
			QuerySolution  solution;
			//given the triple (i, p, ...), check the issue
			while (results.hasNext()){
				//retrieve the (i p x) and the cardinality restriction, card, on class c
				solution  = results.nextSolution();				
				ind = (Resource)solution.get("?i");				
				p = (Resource)solution.get("?p");				
				
				// do not repeatedly checking issues with given (i, p) pairs
				if (checked_ind_p_pairs.containsKey(ind) &&  (p==checked_ind_p_pairs.get(ind))){
					continue;
				}else{
					checked_ind_p_pairs.put(ind, p);
					//x = (Resource)solution.get("?x");
					c = (Resource)solution.get("?c");				
					card = solution.getLiteral("?card");
					n = card.getInt();
					
					//let m=|(i p x)|
					pro = model_all.getProperty(p.getURI());
					int m = model_all.listObjectsOfProperty(ind, pro).toList().size();
					//NodeIterator iter = model_all.listObjectsOfProperty(ind, pro);
					//int m = 0;
					//while (iter.hasNext()){
					//	RDFNode o = iter.nextNode();
					//	m++;
					//}
					
					// if m > n, report epv issue
					if (m > n){				
						Integer error_level = Sw4jMessage.STATE_WARNING;
						String error_summary = aryQuery[i][1];

						String.format(
								"Individual %s : instance of %s should have at most %s values for the property %s.",
								ToolJena.fromatRDFnode(ind,false),
								ToolJena.fromatRDFnode(c,false),
								Integer.toString(n),
								ToolJena.fromatRDFnode(p,false)
								);
						
						String error_details = String.format(
								"According to the applicable cardinality restrictions in the corresponding ontologies, the individual %s which has type %s should have at most %s values for the property %s."+
								"However there are %s values for property %s. " +
								"You are expected to delete some triples in the form of (%s, %s, ...) from the instance data. " ,
								ToolJena.fromatRDFnode(ind, true),
								ToolJena.fromatRDFnode(c, true),
								Integer.toString(n),
								ToolJena.fromatRDFnode(p, true),
								Integer.toString(m),
								ToolJena.fromatRDFnode(p, true),
								ToolJena.fromatRDFnode(ind, true),
								ToolJena.fromatRDFnode(p, true)
								);
						//System.out.println(error_details);
						String error_creator = this.getClass().getSimpleName();

						this.getReport().addEntry(error_level, error_summary, error_creator, error_details, true);				
					}
					
				}				
			}
		}
			
		return bRet;
	}		
	
	
		
	String sz_sparql_cardinality= "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
	"SELECT DISTINCT ?i ?p ?x ?c ?card " +"\n" +
	"WHERE { " +"\n" +
	"	GRAPH <http://ex.com/foo> {" +"\n" +
		"?i ?p ?x .}  "+ "\n" +
		"?i rdf:type ?c .   "+ "\n" +
		"?c rdfs:subClassOf ?r ." +"\n" +
		"?r rdf:type owl:Restriction ." +"\n" +
		"?r owl:onProperty ?p ." +"\n" +
		"?r owl:cardinality ?card ." +"\n" +
		"FILTER( isLITERAL(?card) && (?card > 0) ) " +"\n" +
	"}" +"\n";
	
	String sz_sparql_maxCardinality= "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
	"SELECT DISTINCT ?i ?p ?x ?c ?card " +"\n" +
	"WHERE { " +"\n" +
	"	GRAPH <http://ex.com/foo> {" +"\n" +
		"?i ?p ?x .}  "+ "\n" +
		"?i rdf:type ?c .   "+ "\n" +
		"?c rdfs:subClassOf ?r ." +"\n" +
		"?r rdf:type owl:Restriction ." +"\n" +
		"?r owl:onProperty ?p ." +"\n" +
		"?r owl:maxCardinality ?card ." +"\n" +
		"FILTER( isLITERAL(?card) && (?card > 0) ) " +"\n" +
	"}" +"\n";
}



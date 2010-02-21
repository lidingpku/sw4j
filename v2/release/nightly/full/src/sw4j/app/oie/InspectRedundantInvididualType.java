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



public class InspectRedundantInvididualType extends AbstractTaskDesc{
	public static final String REPORT_TITLE ="Redundant individual type evaluation";
	public static final String REPORT_DESC ="This service checks if the instance has some redundant types";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}
	
	public static final String ERROR_SUMMARY = "redundant individual type by rdfs:subClassOf";

	
	public static InspectRedundantInvididualType inspect(Model model_data, Model model_onto, Model model_all){
		InspectRedundantInvididualType task= new InspectRedundantInvididualType();

		task.do_validate(model_data, model_onto, model_all);
		return task;
	}
	
	/**
	 * 
	 * Redundant Individual Type
	 * 
	 * @param model_data
	 * @param model_onto
	 * @param model_all
	 * @return
	 */	
	public boolean do_validate(Model model_data, Model model_onto, Model model_all){
		boolean bRet =true;		
		
		String [][] aryQuery = new String[][]{
			{sz_sparql, ERROR_SUMMARY, "rdfs:subClassOf"},
		};
		
		for (int i=0; i<aryQuery.length; i++){
			String szQuery = aryQuery[i][0];
			
			Query query = QueryFactory.create(szQuery) ;
			
			DataSource datasource = DatasetFactory.create(model_all);
			//Instance data: D
			datasource.addNamedModel("http://ex.com/foo", model_data);
			//Instance data + All reference ontologies: D+O'
			Model model_data_onto = ModelFactory.createDefaultModel();
			ToolJena.update_copy(model_data_onto, model_data);
			ToolJena.update_copy(model_data_onto, model_onto);			
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

				if (null == this.getReport().addEntry(error_level, error_summary, error_creator, error_details, true))
					break;
			}
		}	

		return bRet;
	}
	
	private String translateQueryResult (QuerySolution solution, String szSparql, String szMessage){
		String szRet = solution.toString();
		Resource ind = (Resource)solution.get("?i");
		Resource class1 = (Resource)solution.get("?c1");		
		
		szRet = String.format("Individual %s: has a redundant type %s .",				
				ToolJena.fromatRDFnode(ind,false),
				ToolJena.fromatRDFnode(class1,false)
		);		
		return szRet;
	}
	
	private String translateQueryResultDetails (QuerySolution solution, String szSparql, String szMessage){
		String szRet = solution.toString();
		Resource ind = (Resource)solution.get("?i");
		Resource class1 = (Resource)solution.get("?c1");
		Resource class2 = (Resource)solution.get("?c2");
		
		String szType = "rdf:type";
		
		szRet = String.format("Individual %s: has a redundant type %s ." +
				"Because it has already been defined to be of type %s which is %s of %s . " +
				"You are expected to delete the triple (%s, %s, %s) from the instance data. " ,
				ToolJena.fromatRDFnode(ind,false),
				ToolJena.fromatRDFnode(class1,false),
				ToolJena.fromatRDFnode(class2,false),
				szMessage,
				ToolJena.fromatRDFnode(class1,false),
				ToolJena.fromatRDFnode(ind,false),
				szType,
				ToolJena.fromatRDFnode(class1,false)
		);
		
		return szRet;
	}


	
	String sz_sparql =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c1 ?c2 " +"\n" +
		"WHERE { " +"\n" +
		"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?i  rdf:type  ?c1 .}" + "\n" +
		"	GRAPH <http://ex.com/data_onto> {" +"\n" +
					"?i  rdf:type  ?c2 .}" + "\n" +
			"?c2 rdfs:subClassOf ?c1 ." +"\n" +			
			"FILTER( (?c1 != ?c2) && (?c1 != <http://www.w3.org/2002/07/owl#Thing>) )  " +"\n" +
		"}" +"\n";
}


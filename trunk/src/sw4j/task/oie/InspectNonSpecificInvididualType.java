package sw4j.task.oie;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;



public class InspectNonSpecificInvididualType extends AbstractTaskDesc{
	public static final String REPORT_TITLE ="Nonspecific individual type evaluation";
	public static final String REPORT_DESC ="This service checks if the instance has some non-specific types";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}
	
	public static final String ERROR_SUMMARY = "Nonspecific individual type by rdfs:subClassOf";
	
	public static InspectNonSpecificInvididualType inspect(Model model_data, Model model_all){
		InspectNonSpecificInvididualType task= new InspectNonSpecificInvididualType();

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
			{sz_sparql, ERROR_SUMMARY},
		};
		
		for (int i=0; i<aryQuery.length; i++){
			String szQuery = aryQuery[i][0];			
			Query query = QueryFactory.create(szQuery) ;			
			DataSource datasource = DatasetFactory.create(model_all);			
			datasource.addNamedModel("http://ex.com/foo", model_data);			
			QueryExecution qexec = QueryExecutionFactory.create(query, datasource) ;
			ResultSet results = qexec.execSelect();
			
			//retrieve individual i, its declared types set dtSet in instance data, and the sub-classes set subSet
			Resource ind = null;
			Resource pind = null;
			Resource cls = null;
			Resource sub = null;
			Set<Resource> dtSet = new HashSet();
			Set<Resource> subSet = new HashSet();
			QuerySolution  solution;			
			while (results.hasNext()){				
				solution  = results.nextSolution();				
				ind = (Resource)solution.get("?i");				
				
				if ( (null == pind) || (! pind.equals(ind))){
					//check the if the previous individual, pind, has NSIT issue
					if (null != pind) {
						if (checkNSIT(model_all, dtSet, subSet)==true) {
							reportNSIT(pind, dtSet, aryQuery[i][1]);
						}
					}
					
					//new individual					
					dtSet.clear();
					cls = (Resource)solution.get("?c");
					dtSet.add(cls);					
					subSet.clear();
					sub = (Resource)solution.get("?subc");
					subSet.add(sub);
					
					//save previous individual
					pind = ind;				
				}else{
					//same individual
					cls = (Resource)solution.get("?c");
					dtSet.add(cls);					
					sub = (Resource)solution.get("?subc");
					subSet.add(sub);				
				}
				
				//handle the last individual
				if (!results.hasNext()){
					if (checkNSIT(model_all, dtSet, subSet)==true) {
						reportNSIT(pind, dtSet, aryQuery[i][1]);					
					}
				}
				
			}
		}
			
		return bRet;
	}	
	
	private boolean checkNSIT(Model model_all, Set<Resource> dtSet, Set<Resource> subSet){
		//compute leafSet from subSet
		Set<Resource> tempSet = new HashSet<Resource>(subSet);
		Set<Resource> leafSet = new HashSet<Resource>(subSet);		
	
		Iterator<Resource> iter1 = subSet.iterator();
		while (iter1.hasNext()){
			Resource sub = iter1.next();
			
			//if sub has any subclasses in tempSet, sub is not leaf node
			Iterator<Resource> iter2 = tempSet.iterator();
			while (iter2.hasNext()){
				Resource temp = iter2.next();				
				Property subClassof = model_all.getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");
				if ( (!temp.equals(sub)) && model_all.contains(temp, subClassof, sub)){
					leafSet.remove(sub);
					break;
				}
			}
		}
		
		//if dtSet and leafSet have no common class members, report: i has USIT issue 	
		Boolean USIT_Flag = true;
		Iterator<Resource> iter4 = dtSet.iterator();
		while (iter4.hasNext()){
			Resource dt = iter4.next();
			if (leafSet.contains(dt)){
				USIT_Flag = false;
				break;
			}
		}
		
		return USIT_Flag;	
	}
	
	private void reportNSIT(Resource ind, Set<Resource> dtSet, String error_summary){
		Integer error_level =Sw4jMessage.STATE_WARNING;		
//		String error_msg = 	String.format("Individual %s defined to have types %s which are not leaf nodes in class hierarchy.",				
//				ToolJena.fromatRDFnode(ind,false),
//				dtSet.toString());
		String error_details = String.format("Individual %s defined to have types %s which are not leaf nodes in class hierarchy." +
				"You are expected to add a more specific type, the leaf node in class hierarchy, to the instance data. " ,
				ToolJena.fromatRDFnode(ind,false),
				dtSet.toString());
		//System.out.println(error_details);
		String error_creator = this.getClass().getSimpleName();

		this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true);					
	}

	
	//query i's type c and it's offspring classes
	String sz_sparql =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" + 
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
		"SELECT ?i ?c ?subc" +"\n" +
		"WHERE { " +"\n" +
		"	GRAPH <http://ex.com/foo> {" +"\n" +
					"?i  rdf:type  ?c .}" + "\n" +		
			"?subc rdfs:subClassOf ?c ." +"\n" +
			"FILTER ( isIRI(?c) && isIRI(?subc) && (?subc != ?c) && (?c != <http://www.w3.org/2002/07/owl#Thing>)&& (?subc != <http://www.w3.org/2002/07/owl#Nothing>))"  +"\n" +
		"}" +"\n" +
	    "ORDER BY ?i ?c ?subc" + "\n";
}
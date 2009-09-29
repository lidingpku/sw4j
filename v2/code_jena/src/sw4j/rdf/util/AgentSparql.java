package sw4j.rdf.util;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import sw4j.rdf.load.RDFSYNTAX;
import sw4j.util.Sw4jException;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;

public class AgentSparql {

	public int INF_NONE = 0;
	public int INF_RDFS = 1;
	
	protected int inf_option=0;
	protected Query query;
	protected Dataset dataset;
	protected QueryExecution qexec;
	protected Syntax defaultQuerySyntax= Syntax.syntaxARQ;
	
	public Object exec(String queryString, String syntax){
		this.query = QueryFactory.create(queryString, defaultQuerySyntax) ;
		
		Dataset dataset = DatasetFactory.create(query.getGraphURIs(), query.getNamedGraphURIs());
		return exec(queryString, dataset, syntax);
	}
	
	public Object exec(String queryString, Dataset dataset, String syntax){
		this.query = QueryFactory.create(queryString, defaultQuerySyntax) ;
		
		this.dataset = dataset;
		run_sparql();
		
		Object ret = gerRet(syntax);
		
		this.qexec.close() ;
		return ret;
	}
	
	protected void run_sparql(){
		if (inf_option == INF_NONE)
			qexec = QueryExecutionFactory.create(query, dataset);
		else{
			reset_dataset();
			qexec = QueryExecutionFactory.create(query, dataset);
		}
	}
	
	private void reset_dataset(){
		DataSource ds = DatasetFactory.create();
		{
			Model m =dataset.getDefaultModel();
			try {
				ds.setDefaultModel(get_new_model(m));
			} catch (Sw4jException e) {
				ds.setDefaultModel(m);
				e.printStackTrace();
			}
		}
		Iterator<String> iter= dataset.listNames();
		while (iter.hasNext()){
			String name = iter.next();
			Model m =dataset.getNamedModel(name);
			try {
				Model m1= get_new_model(m);
				ds.addNamedModel(name, m1);		
			} catch (Sw4jException e) {
				ds.addNamedModel(name, m);
				e.printStackTrace();
			}					
		}
		dataset=ds;
	}
	
	protected Model get_new_model(Model m) throws Sw4jException{
		if (inf_option== INF_RDFS){
			return ToolJena.create_deduction(m);
		}else{
			return m;
		}
	}
	
	private Object gerRet(String szRdfSyntax){
		Object ret = null;
		if (query.isDescribeType()){
			ret = qexec.execDescribe();
		}else if (query.isConstructType()){
			ret = qexec.execConstruct() ;
		}else if (query.isSelectType()){
			ResultSet results = qexec.execSelect() ;
			ByteArrayOutputStream sw = new ByteArrayOutputStream();
			if (RDFSYNTAX.SPARQL_XML.equals(szRdfSyntax)){
				ResultSetFormatter.outputAsXML(sw, results);
			}else if (RDFSYNTAX.SPARQL_JSON.equals(szRdfSyntax)){
				ResultSetFormatter.outputAsJSON(sw, results);
			}else{
				ResultSetFormatter.out(sw,results, query);				
			}
					
			ret = sw.toString();
		}else if (query.isAskType()){
			ret = qexec.execAsk() ;
		}
		
		return ret;
	}
}

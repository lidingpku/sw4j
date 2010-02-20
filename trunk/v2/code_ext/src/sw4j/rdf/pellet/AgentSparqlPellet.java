package sw4j.rdf.pellet;


import com.hp.hpl.jena.rdf.model.Model;
import sw4j.rdf.util.AgentSparql;
import sw4j.util.Sw4jException;

public class AgentSparqlPellet extends AgentSparql{

	public int INF_OWLDL=3;
	
	public AgentSparqlPellet(boolean bUsePellet){
		if (bUsePellet)
			this.inf_option = this.INF_OWLDL = 3;
	}
	
	@Override
	protected Model get_new_model(Model m) throws Sw4jException{
		if (inf_option== INF_OWLDL){
			return ToolPellet.model_createDeductiveClosure(m);
		}else{
			return m;
		}
	}

}

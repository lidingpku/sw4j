package sw4j.app.oie;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.rdf.load.AgentModelLoader;
import sw4j.task.load.TaskLoad;


public class AgentModelLoaderOie extends AgentModelLoader {
	

	public AgentModelLoaderOie(TaskLoad load) {
		super(load);
	}

	InspectLoadReferencedOntology m_task_reference=null;
	boolean b_load_reference=false;
	
	InspectOwlDl m_inspect_owldl=null;
	boolean b_inspect_owl =false;


	final public InspectLoadReferencedOntology getLoadReference(){
		if (!b_load_reference && null==m_task_reference){
			if (null!=getParseRdf() && null!=getParseRdf().getReport() && !getParseRdf().getReport().hasFatal())
				m_task_reference = InspectLoadReferencedOntology.inspect(getModelData(), getModelXmlBase());
			b_load_reference =true;
		}
		
		return m_task_reference;
	}


	final public InspectOwlDl getInspectOwl(){
		if (!b_inspect_owl && null==m_inspect_owldl){
			if (null!=getLoadReference() && null!=getLoadReference().getReport() && !getLoadReference().getReport().hasError())
				m_inspect_owldl = InspectOwlDl.inspect(getModelData(), getLoadReference().getModelOnto(), getModelXmlBase());
			b_inspect_owl = true;
		}
		return m_inspect_owldl;
	}



	final public Model getModelAll_plain(){
		if (null!= getInspectOwl())
			return getInspectOwl().getModelAll();
		return null;
	}
	
	final public Model getModelAll_deduction(){
		if (null!= getInspectOwl())
			return getInspectOwl().getModelAll_deduction();
		return null;
	}

	//updated by jiao 2008-06-25
	final public Model getModelOnto(){		
		if (null!= getLoadReference())
			return getLoadReference().getModelOnto();
		return null;
	}

}

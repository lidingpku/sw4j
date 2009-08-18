package sw4j.task.oie;


import org.apache.log4j.Logger;

import sw4j.task.load.TaskLoad;
import sw4j.task.util.AgentModelManager;
import sw4j.task.util.DataTaskResult;

public class DefaultOwlInstanceDataChecker {
	
	public DefaultOwlInstanceDataChecker(){
        AgentModelManager.get().setCaching(false);
	}
	
	private Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}

	
	/**
	 * validate OWL instance data at the specified file/URL 
	 * 
	 * 
	 * @param szFileOrURI
	 * @return
	 */
	final public DataTaskResult inspect(String szFileOrURI, String szXmlBase, DataEvaluationConfig vc){
		//run rdf parse and validation
		TaskLoad task_load = TaskLoad.load(szFileOrURI, szXmlBase);
		
		return inspect(new AgentModelLoaderOie(task_load), vc);
	}
	
	/**
	 * validate OWL instance data in the supplied text 
	 * 
	 * 
	 * @param szText		the text contain content of PML document
	 * @param szRdfSyntax
	 * @return
	 */
	final public DataTaskResult inspectText(String szText, String szXmlBase, String szRdfSyntax, DataEvaluationConfig vc){

		TaskLoad task_load = TaskLoad.loadText(szText, szXmlBase, szRdfSyntax);

		//run rdf parse and validation
		return inspect(new AgentModelLoaderOie(task_load), vc);
	}	
	
	/**
	 * validate OWL instance data  
	 * 
	 * 
	 * @param model_load		the RDF model to be validated
	 * @return
	 */
	public DataTaskResult inspect(AgentModelLoaderOie model_load, DataEvaluationConfig vc){
		DataTaskResult der = model_load.load();
		return inspect(model_load, der, vc);
	}

	public DataTaskResult inspect(AgentModelLoaderOie model_load, DataTaskResult der, DataEvaluationConfig vc){
		// check if data has been loaded
		if (!der.isSuccessful()){
			return der;
		}
		
		//load referenced ontology, it is ok if referenced ontology cannot be loaded
		if (null!= model_load.getLoadReference())
			der.reports.add(model_load.getLoadReference().getReport());
		
		if (null!= model_load.getInspectOwl()){
			//if (vc.test_checkbox(DataEvaluationConfig.CHECK_OWL_DL))
			
			getLogger().info(model_load.getInspectOwl().getReport().getConclusionMessage());
			der.reports.add(model_load.getInspectOwl().getReport());
			
		    if (!model_load.getInspectOwl().isConsistent()) {
				getLogger().info("Final Result: OWL DL semantic inconsistency!");
				der.setSuccessful(false);
				return der;
		    }
		    if (model_load.getInspectOwl().getReport().hasError()) {
				getLogger().info("Final Result: OWL DL inference failed!");
				der.setSuccessful(false);
				return der;
		    }

		    if (null==model_load.getModelAll_deduction()) {
				der.setSuccessful(false);
				return der;
		    }
		}
		
/*			
		SwutilEvaluationReport report  = task_load.getReport(); 
		getLogger().info(report.getConclusionMessage());
		reports.add(report);
		if (!task_load.isLoadSucceed()){
			getLogger().info("Final Result: data load failed!");
			return reports;
		}
		
		// parse and validate RDF
		DataTaskParseRdf task_rdf = ToolParseRdf.parse(task_load);
		if (null==task_rdf){
			getLogger().info("Final Result: RDF parse/validation failed!");
			return reports;
		}
		getLogger().info(task_rdf.getReport().getConclusionMessage());
		reports.add(task_rdf.getReport());
		if (!task_rdf.hasModel() || task_rdf.getReport().hasFatal()) {
			getLogger().info("Final Result: RDF parse/validation failed!");
			return reports;
		}

		DataTaskLoadReference task_refonto = DataTaskLoadReference();
		
		
		Model model_data = task_rdf.getModel();
		// run regular OWL semantic consistency checking
		InspectOwlDl inspect_owldl = null;
		//if (vc.test_checkbox(DataEvaluationConfig.CHECK_OWL_DL))
		{
			inspect_owldl = InspectOwlDl.inspect(model_data, szXMLBase, true);

			getLogger().info(inspect_owldl.getReport().getConclusionMessage());
			reports.add(inspect_owldl.getReport());
		    if (!inspect_owldl.isConsistent()) {
				getLogger().info("Final Result: OWL DL semantic inconsistency!");
				return reports;
		    }
		    if (inspect_owldl.getReport().hasError()) {
				getLogger().info("Final Result: OWL DL inference failed!");
				return reports;
		    }
		}

		Model model_all= inspect_owldl.getModelAll();
		OntModel model_onto = inspect_owldl.getModelOnto();
*/
		// check 1: MPV

		if (vc.test_checkbox(DataEvaluationConfig.CHECK_MISSING_PROPERTY_VALUE)){
			InspectMissingPropertyValue inspector = InspectMissingPropertyValue.inspect(model_load.getModelData(),model_load.getModelAll_deduction());
			getLogger().info(inspector.getReport().getConclusionMessage());
			der.reports.add(inspector.getReport());
		}	
		
		// check 2: EPV
		if (vc.test_checkbox(DataEvaluationConfig.CHECK_EXCESSIVE_PROPERTY_VALUE)){
			InspectExcessivePropertyValue inspector = InspectExcessivePropertyValue.inspect(model_load.getModelData(),model_load.getModelAll_deduction());
			getLogger().info(inspector.getReport().getConclusionMessage());
			der.reports.add(inspector.getReport());
		}
		
		// check 3: UIT
		if (vc.test_checkbox(DataEvaluationConfig.CHECK_UNEXPECTED_INDIVIDUAL_TYPE)){
			InspectUnexpectedIndividualType inspector = InspectUnexpectedIndividualType.inspect(model_load.getModelData(), model_load.getModelOnto(), model_load.getModelAll_deduction());
			getLogger().info(inspector.getReport().getConclusionMessage());
			der.reports.add(inspector.getReport());
		}
		
		// check 4: RIT
		if (vc.test_checkbox(DataEvaluationConfig.CHECK_REDUNDANT_INDIVIDUAL_TYPE)){
			InspectRedundantInvididualType inspector = InspectRedundantInvididualType.inspect(model_load.getModelData(),model_load.getModelOnto(), model_load.getModelAll_deduction());
			getLogger().info(inspector.getReport().getConclusionMessage());
			der.reports.add(inspector.getReport());
		}
		
		// check 5: NSIT
		if (vc.test_checkbox(DataEvaluationConfig.CHECK_NONSPECIFIC_INDIVIDUAL_TYPE)){
			InspectNonSpecificInvididualType inspector = InspectNonSpecificInvididualType.inspect(model_load.getModelData(),model_load.getModelAll_deduction());
			getLogger().info(inspector.getReport().getConclusionMessage());
			der.reports.add(inspector.getReport());
		}
		
		der.setSuccessful(true);
		return der;
		
	}
	
	public static void main(String[] args){
		//example code for calling this validator
        DefaultOwlInstanceDataChecker validator = new DefaultOwlInstanceDataChecker();
        DataTaskResult der = validator.inspect("/dl/workspace/cvs-project-gila2/gila2/logs/owl/gila.owl", null, new DataEvaluationConfig());
        System.out.println(der.toXml());
	}
}

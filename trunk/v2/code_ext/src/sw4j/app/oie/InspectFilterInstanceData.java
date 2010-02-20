package sw4j.app.oie;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.task.common.AbstractTaskDesc;
import sw4j.util.Sw4jMessage;

public class InspectFilterInstanceData extends AbstractTaskDesc{

	////////////////////////////////////////////////
	// SwutilEvaluationTask (super class)
	////////////////////////////////////////////////
	public static final String ERROR_SUMMARY_1 = "No Instance-data.";
	public static final String WARN_SUMMARY_2 = "some ontological definition exists.";

	public static final String REPORT_TITLE ="Inspect presence of instance data";
	public static final String REPORT_DESC ="This service checkes there exists instance data in the supplied data, and keep only instance data thereafter";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}
	
	public static InspectFilterInstanceData inspect(Model model_data){
		InspectFilterInstanceData task = new InspectFilterInstanceData();
		if (null==model_data)
			return null;
		
		// TODO  try to split the ontology
		Model m = model_data;
		
		if (null==m || m.isEmpty()){
			Integer error_level =Sw4jMessage.STATE_ERROR;
			String error_summary = ERROR_SUMMARY_1;
			String error_details = "";
			String error_creator = task.getClass().getSimpleName();

			task.getReport().addEntry(error_level, error_summary,  error_creator, error_details, false);
			return task;
		}

		if (m.size()> model_data.size()){
			Integer error_level =Sw4jMessage.STATE_WARNING;
			String error_summary = WARN_SUMMARY_2;
			String error_details = String.format("%d triples are kept and %d triples has been removed",m.size(), model_data.size()-m.size());
			String error_creator = task.getClass().getSimpleName();

			task.getReport().addEntry(error_level, error_summary,  error_creator, error_details, false);
		}
		
		model_data.removeAll();
		model_data.add(m);
		
		return task;
	}


	
}

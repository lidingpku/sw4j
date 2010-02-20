package sw4j.app.oie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.ValidityReport;

import sw4j.app.pellet.ToolPellet;
import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.AgentModelStat;
import sw4j.rdf.util.ToolModelAnalysis;
import sw4j.task.common.AbstractTaskDesc;
import sw4j.util.Sw4jMessage;
import sw4j.util.ToolSafe;

public class InspectLoadReferencedOntology extends AbstractTaskDesc{

	////////////////////////////////////////////////
	// SwutilEvaluationTask (super class)
	////////////////////////////////////////////////
	public static final String ERROR_SUMMARY_1 = "Failed loading one referenced ontology. ";
	public static final String INFO_SUMMARY_2 = "Succeed loading one referenced ontology. ";
	public static final String ERROR_SUMMARY_4 = "Semantic inconsistency (OWL DL) found in one referenced ontology, skip it.";
	public static final String ERROR_SUMMARY_5 = "Semantic inconsistency (OWL DL) found when merging referenced ontologies, skip all.";
	
	public static final String ERROR_SUMMARY_6 = "Unable to find definition of a class used in the instance data.";
	public static final String ERROR_SUMMARY_7 = "Unable to find definition of a property used in the instance data.";

	public static final String REPORT_TITLE ="Load referenced ontology";
	public static final String REPORT_DESC ="This service loads referenced ontologies of RDF data";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}
	
	public static InspectLoadReferencedOntology inspect(Model model_data, String szBaseUrl){
		InspectLoadReferencedOntology task = new InspectLoadReferencedOntology();
		if (null==model_data)
			return null;
		
		task.loadReferenceOntologies(model_data, szBaseUrl);
		return task;
	}

	private Map<String, Model> m_loadedOntologies = new HashMap<String, Model>(); // all referenced ontologies
	private AgentModelStat m_stat_data = new AgentModelStat();
	private AgentModelStat m_stat_onto = new AgentModelStat();
	
	private OntModel m_model_onto_ont = ToolPellet.createOntModel();
	public Model getModelOnto(){
		return m_model_onto_ont.getBaseModel();
	}
	
	
	/**
	 * given a model_data, find all its referenced ontology (excluding meta ontologies). Then recursively get imported ontologies
	 * @param model
	 * @param urls
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public void loadReferenceOntologies( Model model_data, String szXmlBase){
		// extract links
		m_stat_data.traverse(model_data);
		Set<String> urls =  m_stat_data.getNormalizedLinks(true, true);

		if (!ToolSafe.isEmpty(szXmlBase)){
			urls.remove(szXmlBase);
		}
		
		// step 1: load referenced ontologies
		Map<String, Model> data =  AgentModelManager.get().loadModelRecursive(urls, true, true);
		Iterator<Map.Entry<String, Model>> iter_data = data.entrySet().iterator();
		while (iter_data.hasNext()){
			Map.Entry<String, Model> entry = iter_data.next();
			String szURL = entry.getKey();
			Model m = entry.getValue();
			
			// case 1: the the ontology cannot be loaded
			if (null==m){
				Integer error_level =Sw4jMessage.STATE_WARNING;
				String error_summary = ERROR_SUMMARY_1;
				String error_details = "unreachable referenced ontology: "+szURL;
				String error_creator = this.getClass().getSimpleName();

				this.getReport().addEntry(error_level, error_summary,  error_creator, error_details,false);
				continue;
			}
			
			// case 2: check if a referenced ontology is inconsistent  
			OntModel ont = ToolPellet.createOntModel();
			ont.add(m);
			ValidityReport report = ont.validate();						
			if (!report.isValid()){
				Integer error_level =Sw4jMessage.STATE_WARNING;
				String error_summary = ERROR_SUMMARY_4;
				String error_details =  "inconsistent referenced ontology: "+szURL;
				Iterator iter_issue= report.getReports();
				//only report 10 errors
				int max_entry =10;
				while (iter_issue.hasNext()){
					max_entry--;
					if (max_entry<0)
						break;
					error_details += "\n[ISSUE]"+iter_issue.next().toString();
				}
				String error_creator = this.getClass().getSimpleName();

				this.getReport().addEntry(error_level, error_summary,  error_creator, error_details,false);
				continue;
			}
			
			//case 3: loaded, and consistent
			m_loadedOntologies.put(szURL, m);
			m_model_onto_ont.add(m);			
			{
				Integer error_level =Sw4jMessage.STATE_INFO;
				String error_summary = INFO_SUMMARY_2;
				String error_details = "loaded referenced ontology: "+szURL;
				String error_creator = this.getClass().getSimpleName();

				this.getReport().addEntry(error_level, error_summary,  error_creator, error_details,false);
			}
			
		}
		
		// step 2: check owl consistency when merging all consistent referenced ontologies
		{
			ValidityReport report = m_model_onto_ont.validate();						
			if (!report.isValid()){
				Integer error_level =Sw4jMessage.STATE_WARNING;
				String error_summary = ERROR_SUMMARY_5;
				String error_details = "the refernced ontologies (consistent ones) cannot merge into a consistent ontology.";
				Iterator iter= report.getReports();
				//only report 10 results
				int max_entry = 10;
				while (iter.hasNext()){
					max_entry --;
					if (max_entry<0)
						break;
					error_details += "\n[ISSUE]"+iter.next().toString();
				}
				String error_creator = this.getClass().getSimpleName();
	
				this.getReport().addEntry(error_level, error_summary, error_creator, error_details, false);
				
				// if the referenced ontologies are inconsistent, then return
				// will not use any referenced ontology
				return;
			}
		}	
		
		// step 3: check if all used terms (classes and properties) are properly defined
		m_stat_onto.traverse(m_model_onto_ont.getBaseModel());
		{
			// check classes
			String key1 = AgentModelStat.META_USAGE_INS_C;
			String key2 = AgentModelStat.META_USAGE_DEF_C;
			Set<String> instantiation = this.m_stat_data.getMetaTermsByUsage(key1);
			Set<String> definition_data = this.m_stat_data.getMetaTermsByUsage(key2);
			Set<String> definition_onto = this.m_stat_onto.getMetaTermsByUsage(key2);
			instantiation.removeAll(definition_data);
			instantiation.removeAll(definition_onto);
			Iterator<String> iter = instantiation.iterator();
			while (iter.hasNext()){
				String szTerm = iter.next();
				if (ToolModelAnalysis.useMetaNamespace(szTerm))
					continue;
				
				Integer error_level =Sw4jMessage.STATE_WARNING;
				String error_summary = ERROR_SUMMARY_6;
				String error_details = "undefined class: "+szTerm;
				String error_creator = this.getClass().getSimpleName();

				if (null == this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true))
					break;

			}
		}
		{
			// check classes
			String key1 = AgentModelStat.META_USAGE_INS_P;
			String key2 = AgentModelStat.META_USAGE_DEF_P;
			Set<String> instantiation = this.m_stat_data.getMetaTermsByUsage(key1);
			Set<String> definition_data = this.m_stat_data.getMetaTermsByUsage(key2);
			Set<String> definition_onto = this.m_stat_onto.getMetaTermsByUsage(key2);
			instantiation.removeAll(definition_data);
			instantiation.removeAll(definition_onto);
			Iterator<String> iter = instantiation.iterator();
			while (iter.hasNext()){
				String szTerm = iter.next();
				if (ToolModelAnalysis.useMetaNamespace(szTerm))
					continue;
				
				Integer error_level =Sw4jMessage.STATE_WARNING;
				String error_summary = ERROR_SUMMARY_7;
				String error_details =  "undefined property: "+szTerm;
				String error_creator = this.getClass().getSimpleName();

				if (null == this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true))
					break;

			}			
		}
	}	
	
}

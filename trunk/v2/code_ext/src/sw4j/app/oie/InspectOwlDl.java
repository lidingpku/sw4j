package sw4j.app.oie;

import java.util.Iterator;

import sw4j.app.pellet.ToolPellet;
import sw4j.rdf.util.ToolJena;
import sw4j.task.common.AbstractTaskDesc;
import sw4j.util.Sw4jException;
import sw4j.util.Sw4jMessage;
import sw4j.util.ToolString;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.ValidityReport;

public class InspectOwlDl extends AbstractTaskDesc{

	public static final String ERROR_SUMMARY_2 = "Semantic inconsistency (OWL DL) found in OWL instance data and its referenced ontologies";
	public static final String ERROR_SUMMARY_3 = "Semantic inconsistency found when computing dedutive closure";

	public static final String REPORT_TITLE ="OWL-DL semantics evaluation";
	public static final String REPORT_DESC ="This service checks if instance data is semantically consistent (OWL DL) with the corresponding ontologies";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}
	
	//private Model m_model_all= null; //ModelFactory.createDefaultModel();   // all referenced ontologies
	//private OWLReasoner m_reasoner_all = null;   // for counting OWL Species
	private OntModel m_ontmodel_all= null; //ModelFactory.createDefaultModel();   // all referenced ontologies


	
	public boolean isConsistent(){
		return this.getReport().isClean();
		//return getReasonerAll().isConsistent();
	}
	/*	
	private OWLReasoner getReasonerAll(){
		return (OWLReasoner)(getModelAllInf().getReasoner());

		if (null== m_reasoner_all){
			m_reasoner_all = new OWLReasoner();
			if (null!= m_model_all){
				m_reasoner_all.load(m_model_all);
			}
		}
		return m_reasoner_all;
	}
*/	

	public Model getModelAll(){
		return m_ontmodel_all.getBaseModel();
	}

	Model m_model_all_deduction =null; 
	public Model getModelAll_deduction() {
		if (null==m_model_all_deduction){
			try {
				m_model_all_deduction = ToolPellet.model_createDeductiveClosure(null, m_ontmodel_all);
			} catch (Sw4jException e) {
				//TODO e.printStackTrace();
			}
		}
		return m_model_all_deduction;
	}
	
	public static InspectOwlDl inspect(Model model_data, Model model_onto, String szBaseUrl){
		InspectOwlDl task = new InspectOwlDl();

		task.do_validate(model_data, model_onto, szBaseUrl);
		return task;
	}
	
/*	private List<String> toStringList(Iterator iter){
		List<String> list = new ArrayList<String>();
		while (iter.hasNext()){
			list.add(iter.next().toString());
		}
		return list;
	}
*/	
	@SuppressWarnings("unchecked")
	private void do_validate(Model model_data, Model model_onto, String szXmlBase){
		this.m_ontmodel_all = ToolPellet.createOntModel();  //ModelFactory.createDefaultModel();   // all referenced ontologies

		// step 3: check owl consistency when merging the data with all consistent referenced ontologies  
		// if referenced ontologies does not exist, or cannot be load,is not asked to load;  we will just focus on the instance data itself
		ToolJena.update_copy(m_ontmodel_all, model_data);
		
		
		if (null!=model_onto && !model_onto.isEmpty())
			ToolJena.update_copy(this.m_ontmodel_all, model_onto);
		
		try {
			
			ValidityReport report  = m_ontmodel_all.validate();
			if (!report.isValid()){
				Iterator iter = report.getReports();
				while (iter.hasNext()){
					String error_details  = iter.next().toString();
					
					// rewirte error remport
					//updated by jiao 4/3/2008
					
					String error_msg = null;
					
					//case1: plain literal missing rdf:datatype attribute
					String szTemp = error_details.toLowerCase();
					String [] test1 = new String []{
						"plain literal",
						"does not belong to datatype",
						"literal value may be missing the rdf:datatype attribute",
						};
					if (ToolString.match(szTemp, test1)){
						error_msg = "Plain literal missing rdf:datatype attribute.";
					}
					
					//case2: Individual is forced to belong to a class and its complement
					String [] test2 = new String []{
							"individual",
							"is forced to belong to class",
							"and its complement",
							};
					if (ToolString.match(szTemp, test2)){
						error_msg = "Individual can not be an instance of two disjoint classes.";
					}
				
					//case3: Violating cardinality restriction
					String [] test3 = new String []{
							"individual",
							"has more than",
							"values for property",
							"violating the cardinality restriction",
							};
					if (ToolString.match(szTemp, test3)){
						error_msg = "Cardinality restriction violated.";					
					}
					
					Integer error_level =Sw4jMessage.STATE_ERROR;
					String error_summary = ERROR_SUMMARY_2;
					error_details = error_msg +error_details;
					String error_creator = this.getClass().getSimpleName();
	
					if (null == this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true))
						break;
				}	
				
				// if the inferred model is invalid, then report error.
				return;
			}
		


		
		// step 4. compute deductive closure using transitive/reflective reasoning results (subclassof,subpropertyof)
		
			//m_model_all = ToolJena.model_createDeductiveClosure(model_all_inf);
		}catch (Exception e){
			Integer error_level =Sw4jMessage.STATE_FATAL;
			String error_summary = ERROR_SUMMARY_3;
			String error_details = e.getLocalizedMessage();
			String error_creator = this.getClass().getSimpleName();

			this.getReport().addEntry(error_level, error_summary, error_creator, error_details, false);		
			
			return;
		}
		
		
		/*  the following lines does generate expected validation report
		if (!m_reasoner_all.isConsistent()){
			Iterator iter = m_reasoner_all.getKB().getExplanationSet().iterator();
			while (iter.hasNext()){
				String msg  = iter.next().toString();
				m_report.add(DataValidationReport.ERROR_TYPE_ERROR, ERROR_MSG_2, msg, getLogger());
			}				
		}			
		*/
		
		//TODO the details of species checking is too much 
		//generate validation report - owl species results
		//OWLSpeciesReport report = m_reasoner_all.getSpecies().getReport();
		//m_report.add(DataValidationReport.ERROR_TYPE_INFO, ERROR_MSG_3, report.toString(), getLogger());
	}

	
	
	/**
	 *  load all ontologies referenced by the RDF data from szFileOrURI.
	 *  
	 * 
	 * @param model  the target model to be filled
	 * @param szFileOrURI	the RDF data as starting point
	 * @param bInclusive   include the RDF data from szFileOrURI or not (if false, we only include the referenced ontologies).
	 */
/*	public void readOntModel(OntModel model, String szFileOrURI, String szXmlBase, boolean bInclusive) {
		TreeSet<String> set_urls = new TreeSet<String>();
		Model m;
		try {
			m = ModelManager.get().loadModel(szFileOrURI, szXmlBase);
			if (ToolCommon.isEmpty(szXmlBase))
				szXmlBase = ToolURI.extractNamespaceCanonical(szFileOrURI); 

			readOntModel(model, m, szXmlBase, bInclusive);
		} catch (SwutilException e) {
			
		}
	}
*/	
	
	
}

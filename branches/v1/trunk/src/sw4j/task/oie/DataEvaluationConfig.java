package sw4j.task.oie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import sw4j.task.load.TaskLoad;
import sw4j.task.rdf.TaskParseRdf;
import sw4j.util.ToolSafe;

public class DataEvaluationConfig {

	/**
	 * check load content
	 */
	public static final String CHECK_LOAD=  TaskLoad.class.getSimpleName();

	/**
	 * check RDF syntax  -- required to be run any time
	 */
	public static final String CHECK_RDF=  TaskParseRdf.class.getSimpleName();

	/**
	 * semantic consistency (OWL DL) -- The OWL instance data and its 
	 * referenced ontologies are semantically consistent (within OWL DL).
	 */
	public static final String CHECK_OWL_DL = InspectOwlDl.class.getSimpleName(); 
	
	/**
	 * Unexpected Individual Type -- each known type of an individual is 
	 * compatible with (being subclass or superclass) each expected types in the corresponding ontology.
	 *       
	 * related to: rdfs:domain; rdfs:range; owl:allValuesFrom      
	 */
	public static final String CHECK_UNEXPECTED_INDIVIDUAL_TYPE = InspectUnexpectedIndividualType.class.getSimpleName();

	/**
	 * Unexpected property value -- the value of a property is not the same as the expected value 
	 * defined by the corresponding ontology.
	 *  
	 *  related to owl:hasValue
	 *   
	 */
	public static final String CHECK_UNEXPECTED_PROPERTY_VALUE= "CHECK_UNEXPECTED_PROPERTY_VALUE";

	
	/**
	 * Missing property value -- an individual is supposed to be described by at least 
	 *  N different pairs of (property,value) according to the corresponding ontology,
	 *  but there are not enough pairs in provided instance data.
	 *  
	 *  related to owl:minCardinality; owl:cardinality, owl:someValuesFrom
	 *   
	 */
	public static final String CHECK_MISSING_PROPERTY_VALUE = InspectMissingPropertyValue.class.getSimpleName();
	
	/**
	 * Excessive property value -- an individual is supposed to be described by at most 
	 *  N different pairs of (property,value) according to the corresponding ontology,
	 *  but there are too many pairs in provided instance data . Note there are not enough
	 *  owl:sameAs statements to reduce the number of pairs to N or lower. 
	 *  
	 *  related to owl:minCardinality; owl:cardinality
	 */
	public static final String CHECK_EXCESSIVE_PROPERTY_VALUE = InspectExcessivePropertyValue.class.getSimpleName();


	/** 
	 * Redundant individual type --  e.g. x a  Person; a Agent. 
	 * 
	 */ 
	public static final String CHECK_REDUNDANT_INDIVIDUAL_TYPE = InspectRedundantInvididualType.class.getSimpleName();
	
	/** 
	 * Nonspecific individual type --  the type of an individual is a non-leaf node in
	 * the class hierarchy.  
	 * 
	 */ 
	public static final String CHECK_NONSPECIFIC_INDIVIDUAL_TYPE = InspectNonSpecificInvididualType.class.getSimpleName();
	
	/** 
	 * ambiguous  individual type --  two known types of an individual are not compatible with each other.  
	 * (i) #foo  a foaf:Person,foaf:Group.
	 * (ii) no subclass/superclass relation between foaf:Person and foaf:Group in foaf ontology
	 * 
	 */ 
	public static final String CHECK_AMBIGUOUS_INDIVIDUAL_TYPE = "CHECK_AMBIGUOUS_INDIVIDUAL_TYPE";
	
	/** 
	 * OWL FULL resource --  RDF resources causing OWL FULL status. 
	 * (i) not individual, class or property
	 * (ii) individual and (class or property) 
	 * 
	 * 
	public static final String CHECK_OWL_FULL_RESOURCE = "CHECK_OWL_FULL_RESOURCE";
	*/
	
	
	public static final String CHECK_MISC= "CHECK_MISC";
	
	public List<String> m_data =null;
	
	public static List<String> m_gAll = null;
	public static List<String> checkAll(){
		if (null== m_gAll){
			m_gAll = new ArrayList<String>();
			m_gAll.add(CHECK_RDF);
			m_gAll.add(CHECK_OWL_DL);
			m_gAll.add(CHECK_UNEXPECTED_INDIVIDUAL_TYPE);
			m_gAll.add(CHECK_UNEXPECTED_PROPERTY_VALUE);
			m_gAll.add(CHECK_MISSING_PROPERTY_VALUE);
			m_gAll.add(CHECK_EXCESSIVE_PROPERTY_VALUE);
			m_gAll.add(CHECK_REDUNDANT_INDIVIDUAL_TYPE);
			m_gAll.add(CHECK_NONSPECIFIC_INDIVIDUAL_TYPE);
			m_gAll.add(CHECK_AMBIGUOUS_INDIVIDUAL_TYPE);
		}
		return m_gAll;
	}
	
	
	public DataEvaluationConfig(){
		m_data = checkAll();
	}

	public DataEvaluationConfig(Collection <String> selected_options){
		m_data  = new ArrayList<String>();
		Iterator<String> iter = checkAll().iterator();
		while (iter.hasNext()){
			String szOption = iter.next();
			
			if (selected_options.contains(szOption)){
				m_data.add(szOption);
			}
		}
	}
	
	public boolean test_checkbox(String field){
		return !ToolSafe.isEmpty(field)&& m_data.contains(field) ;
	}
	
	public String toString(){
		return m_data.toString();
	}
	
	
}

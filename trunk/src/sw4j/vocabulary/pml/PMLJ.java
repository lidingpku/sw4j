/**
 * PMLJ.java
 *
 *  This file is automatically generated by PML API 
 *	generated on  Mon Jun 29 06:25:48 EDT 2009
 *  
 *  @author: Li Ding (http://www.cs.rpi.edu/~dingl )
 *
 */
package  sw4j.vocabulary.pml;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 *
 *  
 *  Ontology information 
 *   	label:  PML2 justification ontology@en
 *   	comment:   The justification part of PML2 ontology. It is a fundamental component of PML2 ontology.@en
 */
public class PMLJ{

    protected static final String NS = "http://inference-web.org/2.0/pml-justification.owl#";

    public static final String getURI(){ return NS; }

	// Class (8)
	 public final static String Mapping_lname = "Mapping";
	 public final static String Mapping_qname = "pmlj:Mapping";
	 public final static String Mapping_uri = "http://inference-web.org/2.0/pml-justification.owl#Mapping";
	 public final static Resource  Mapping = ResourceFactory.createResource(Mapping_uri);

	 public final static String AbstractionRule_lname = "AbstractionRule";
	 public final static String AbstractionRule_qname = "pmlj:AbstractionRule";
	 public final static String AbstractionRule_uri = "http://inference-web.org/2.0/pml-justification.owl#AbstractionRule";
	 public final static Resource  AbstractionRule = ResourceFactory.createResource(AbstractionRule_uri);

	 public final static String JustificationElement_lname = "JustificationElement";
	 public final static String JustificationElement_qname = "pmlj:JustificationElement";
	 public final static String JustificationElement_uri = "http://inference-web.org/2.0/pml-justification.owl#JustificationElement";
	 public final static Resource  JustificationElement = ResourceFactory.createResource(JustificationElement_uri);

	 public final static String InferenceStep_lname = "InferenceStep";
	 public final static String InferenceStep_qname = "pmlj:InferenceStep";
	 public final static String InferenceStep_uri = "http://inference-web.org/2.0/pml-justification.owl#InferenceStep";
	 public final static Resource  InferenceStep = ResourceFactory.createResource(InferenceStep_uri);

	 public final static String Query_lname = "Query";
	 public final static String Query_qname = "pmlj:Query";
	 public final static String Query_uri = "http://inference-web.org/2.0/pml-justification.owl#Query";
	 public final static Resource  Query = ResourceFactory.createResource(Query_uri);

	 public final static String Question_lname = "Question";
	 public final static String Question_qname = "pmlj:Question";
	 public final static String Question_uri = "http://inference-web.org/2.0/pml-justification.owl#Question";
	 public final static Resource  Question = ResourceFactory.createResource(Question_uri);

	 public final static String NodeSet_lname = "NodeSet";
	 public final static String NodeSet_qname = "pmlj:NodeSet";
	 public final static String NodeSet_uri = "http://inference-web.org/2.0/pml-justification.owl#NodeSet";
	 public final static Resource  NodeSet = ResourceFactory.createResource(NodeSet_uri);

	 public final static String NodeSetList_lname = "NodeSetList";
	 public final static String NodeSetList_qname = "pmlj:NodeSetList";
	 public final static String NodeSetList_uri = "http://inference-web.org/2.0/pml-justification.owl#NodeSetList";
	 public final static Resource  NodeSetList = ResourceFactory.createResource(NodeSetList_uri);

	// Property (21)
	 public final static String hasAnswer_lname = "hasAnswer";
	 public final static String hasAnswer_qname = "pmlj:hasAnswer";
	 public final static String hasAnswer_uri = "http://inference-web.org/2.0/pml-justification.owl#hasAnswer";
	 public final static Property  hasAnswer = ResourceFactory.createProperty(hasAnswer_uri);

	 public final static String hasMetaBinding_lname = "hasMetaBinding";
	 public final static String hasMetaBinding_qname = "pmlj:hasMetaBinding";
	 public final static String hasMetaBinding_uri = "http://inference-web.org/2.0/pml-justification.owl#hasMetaBinding";
	 public final static Property  hasMetaBinding = ResourceFactory.createProperty(hasMetaBinding_uri);

	 public final static String isQueryFor_lname = "isQueryFor";
	 public final static String isQueryFor_qname = "pmlj:isQueryFor";
	 public final static String isQueryFor_uri = "http://inference-web.org/2.0/pml-justification.owl#isQueryFor";
	 public final static Property  isQueryFor = ResourceFactory.createProperty(isQueryFor_uri);

	 public final static String mapFrom_lname = "mapFrom";
	 public final static String mapFrom_qname = "pmlj:mapFrom";
	 public final static String mapFrom_uri = "http://inference-web.org/2.0/pml-justification.owl#mapFrom";
	 public final static Property  mapFrom = ResourceFactory.createProperty(mapFrom_uri);

	 public final static String hasConclusion_lname = "hasConclusion";
	 public final static String hasConclusion_qname = "pmlj:hasConclusion";
	 public final static String hasConclusion_uri = "http://inference-web.org/2.0/pml-justification.owl#hasConclusion";
	 public final static Property  hasConclusion = ResourceFactory.createProperty(hasConclusion_uri);

	 public final static String fromAnswer_lname = "fromAnswer";
	 public final static String fromAnswer_qname = "pmlj:fromAnswer";
	 public final static String fromAnswer_uri = "http://inference-web.org/2.0/pml-justification.owl#fromAnswer";
	 public final static Property  fromAnswer = ResourceFactory.createProperty(fromAnswer_uri);

	 public final static String hasAntecedentList_lname = "hasAntecedentList";
	 public final static String hasAntecedentList_qname = "pmlj:hasAntecedentList";
	 public final static String hasAntecedentList_uri = "http://inference-web.org/2.0/pml-justification.owl#hasAntecedentList";
	 public final static Property  hasAntecedentList = ResourceFactory.createProperty(hasAntecedentList_uri);

	 public final static String fromQuery_lname = "fromQuery";
	 public final static String fromQuery_qname = "pmlj:fromQuery";
	 public final static String fromQuery_uri = "http://inference-web.org/2.0/pml-justification.owl#fromQuery";
	 public final static Property  fromQuery = ResourceFactory.createProperty(fromQuery_uri);

	 public final static String isExplanationOf_lname = "isExplanationOf";
	 public final static String isExplanationOf_qname = "pmlj:isExplanationOf";
	 public final static String isExplanationOf_uri = "http://inference-web.org/2.0/pml-justification.owl#isExplanationOf";
	 public final static Property  isExplanationOf = ResourceFactory.createProperty(isExplanationOf_uri);

	 public final static String hasVariableMapping_lname = "hasVariableMapping";
	 public final static String hasVariableMapping_qname = "pmlj:hasVariableMapping";
	 public final static String hasVariableMapping_uri = "http://inference-web.org/2.0/pml-justification.owl#hasVariableMapping";
	 public final static Property  hasVariableMapping = ResourceFactory.createProperty(hasVariableMapping_uri);

	 public final static String hasAnswerPattern_lname = "hasAnswerPattern";
	 public final static String hasAnswerPattern_qname = "pmlj:hasAnswerPattern";
	 public final static String hasAnswerPattern_uri = "http://inference-web.org/2.0/pml-justification.owl#hasAnswerPattern";
	 public final static Property  hasAnswerPattern = ResourceFactory.createProperty(hasAnswerPattern_uri);

	 public final static String hasSourceUsage_lname = "hasSourceUsage";
	 public final static String hasSourceUsage_qname = "pmlj:hasSourceUsage";
	 public final static String hasSourceUsage_uri = "http://inference-web.org/2.0/pml-justification.owl#hasSourceUsage";
	 public final static Property  hasSourceUsage = ResourceFactory.createProperty(hasSourceUsage_uri);

	 public final static String mapTo_lname = "mapTo";
	 public final static String mapTo_qname = "pmlj:mapTo";
	 public final static String mapTo_uri = "http://inference-web.org/2.0/pml-justification.owl#mapTo";
	 public final static Property  mapTo = ResourceFactory.createProperty(mapTo_uri);

	 public final static String fromAnswerOrQuery_lname = "fromAnswerOrQuery";
	 public final static String fromAnswerOrQuery_qname = "pmlj:fromAnswerOrQuery";
	 public final static String fromAnswerOrQuery_uri = "http://inference-web.org/2.0/pml-justification.owl#fromAnswerOrQuery";
	 public final static Property  fromAnswerOrQuery = ResourceFactory.createProperty(fromAnswerOrQuery_uri);

	 public final static String hasInferenceRule_lname = "hasInferenceRule";
	 public final static String hasInferenceRule_qname = "pmlj:hasInferenceRule";
	 public final static String hasInferenceRule_uri = "http://inference-web.org/2.0/pml-justification.owl#hasInferenceRule";
	 public final static Property  hasInferenceRule = ResourceFactory.createProperty(hasInferenceRule_uri);

	 public final static String hasPatternNodeSet_lname = "hasPatternNodeSet";
	 public final static String hasPatternNodeSet_qname = "pmlj:hasPatternNodeSet";
	 public final static String hasPatternNodeSet_uri = "http://inference-web.org/2.0/pml-justification.owl#hasPatternNodeSet";
	 public final static Property  hasPatternNodeSet = ResourceFactory.createProperty(hasPatternNodeSet_uri);

	 public final static String isFromEngine_lname = "isFromEngine";
	 public final static String isFromEngine_qname = "pmlj:isFromEngine";
	 public final static String isFromEngine_uri = "http://inference-web.org/2.0/pml-justification.owl#isFromEngine";
	 public final static Property  isFromEngine = ResourceFactory.createProperty(isFromEngine_uri);

	 public final static String isConsequentOf_lname = "isConsequentOf";
	 public final static String isConsequentOf_qname = "pmlj:isConsequentOf";
	 public final static String isConsequentOf_uri = "http://inference-web.org/2.0/pml-justification.owl#isConsequentOf";
	 public final static Property  isConsequentOf = ResourceFactory.createProperty(isConsequentOf_uri);

	 public final static String hasIndex_lname = "hasIndex";
	 public final static String hasIndex_qname = "pmlj:hasIndex";
	 public final static String hasIndex_uri = "http://inference-web.org/2.0/pml-justification.owl#hasIndex";
	 public final static Property  hasIndex = ResourceFactory.createProperty(hasIndex_uri);

	 public final static String hasDischarge_lname = "hasDischarge";
	 public final static String hasDischarge_qname = "pmlj:hasDischarge";
	 public final static String hasDischarge_uri = "http://inference-web.org/2.0/pml-justification.owl#hasDischarge";
	 public final static Property  hasDischarge = ResourceFactory.createProperty(hasDischarge_uri);

	 public final static String hasInferenceEngine_lname = "hasInferenceEngine";
	 public final static String hasInferenceEngine_qname = "pmlj:hasInferenceEngine";
	 public final static String hasInferenceEngine_uri = "http://inference-web.org/2.0/pml-justification.owl#hasInferenceEngine";
	 public final static Property  hasInferenceEngine = ResourceFactory.createProperty(hasInferenceEngine_uri);

	// Instance (0)


}


 

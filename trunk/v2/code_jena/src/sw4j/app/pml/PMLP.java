/**
 * PMLP.java
 *
 *  This file is automatically generated by PML API 
 *	generated on  Tue Sep 15 14:27:26 EDT 2009
 *  
 *  @author: Li Ding (http://www.cs.rpi.edu/~dingl )
 *
 */
package  sw4j.app.pml;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 *
 *  
 *  Ontology information 
 *   	label:  PML2 provenance ontology@en
 *   	comment:   The provenance part of PML2 ontology. It is a fundamental component of PML2 ontology.@en
 */
public class PMLP{

    protected static final String NS = "http://inference-web.org/2.0/pml-provenance.owl#";

    public static final String getURI(){ return NS; }

	// Class (30)
	 public final static String PrettyNameMapping_lname = "PrettyNameMapping";
	 public final static String PrettyNameMapping_qname = "pmlp:PrettyNameMapping";
	 public final static String PrettyNameMapping_uri = "http://inference-web.org/2.0/pml-provenance.owl#PrettyNameMapping";
	 public final static Resource  PrettyNameMapping = ResourceFactory.createResource(PrettyNameMapping_uri);

	 public final static String SourceUsage_lname = "SourceUsage";
	 public final static String SourceUsage_qname = "pmlp:SourceUsage";
	 public final static String SourceUsage_uri = "http://inference-web.org/2.0/pml-provenance.owl#SourceUsage";
	 public final static Resource  SourceUsage = ResourceFactory.createResource(SourceUsage_uri);

	 public final static String TranslationRule_lname = "TranslationRule";
	 public final static String TranslationRule_qname = "pmlp:TranslationRule";
	 public final static String TranslationRule_uri = "http://inference-web.org/2.0/pml-provenance.owl#TranslationRule";
	 public final static Resource  TranslationRule = ResourceFactory.createResource(TranslationRule_uri);

	 public final static String WebService_lname = "WebService";
	 public final static String WebService_qname = "pmlp:WebService";
	 public final static String WebService_uri = "http://inference-web.org/2.0/pml-provenance.owl#WebService";
	 public final static Resource  WebService = ResourceFactory.createResource(WebService_uri);

	 public final static String DocumentFragmentByOffset_lname = "DocumentFragmentByOffset";
	 public final static String DocumentFragmentByOffset_qname = "pmlp:DocumentFragmentByOffset";
	 public final static String DocumentFragmentByOffset_uri = "http://inference-web.org/2.0/pml-provenance.owl#DocumentFragmentByOffset";
	 public final static Resource  DocumentFragmentByOffset = ResourceFactory.createResource(DocumentFragmentByOffset_uri);

	 public final static String InferenceEngine_lname = "InferenceEngine";
	 public final static String InferenceEngine_qname = "pmlp:InferenceEngine";
	 public final static String InferenceEngine_uri = "http://inference-web.org/2.0/pml-provenance.owl#InferenceEngine";
	 public final static Resource  InferenceEngine = ResourceFactory.createResource(InferenceEngine_uri);

	 public final static String Sensor_lname = "Sensor";
	 public final static String Sensor_qname = "pmlp:Sensor";
	 public final static String Sensor_uri = "http://inference-web.org/2.0/pml-provenance.owl#Sensor";
	 public final static Resource  Sensor = ResourceFactory.createResource(Sensor_uri);

	 public final static String Document_lname = "Document";
	 public final static String Document_qname = "pmlp:Document";
	 public final static String Document_uri = "http://inference-web.org/2.0/pml-provenance.owl#Document";
	 public final static Resource  Document = ResourceFactory.createResource(Document_uri);

	 public final static String Organization_lname = "Organization";
	 public final static String Organization_qname = "pmlp:Organization";
	 public final static String Organization_uri = "http://inference-web.org/2.0/pml-provenance.owl#Organization";
	 public final static Resource  Organization = ResourceFactory.createResource(Organization_uri);

	 public final static String Software_lname = "Software";
	 public final static String Software_qname = "pmlp:Software";
	 public final static String Software_uri = "http://inference-web.org/2.0/pml-provenance.owl#Software";
	 public final static Resource  Software = ResourceFactory.createResource(Software_uri);

	 public final static String IdentifiedThing_lname = "IdentifiedThing";
	 public final static String IdentifiedThing_qname = "pmlp:IdentifiedThing";
	 public final static String IdentifiedThing_uri = "http://inference-web.org/2.0/pml-provenance.owl#IdentifiedThing";
	 public final static Resource  IdentifiedThing = ResourceFactory.createResource(IdentifiedThing_uri);

	 public final static String Format_lname = "Format";
	 public final static String Format_qname = "pmlp:Format";
	 public final static String Format_uri = "http://inference-web.org/2.0/pml-provenance.owl#Format";
	 public final static Resource  Format = ResourceFactory.createResource(Format_uri);

	 public final static String Person_lname = "Person";
	 public final static String Person_qname = "pmlp:Person";
	 public final static String Person_uri = "http://inference-web.org/2.0/pml-provenance.owl#Person";
	 public final static Resource  Person = ResourceFactory.createResource(Person_uri);

	 public final static String DocumentFragmentByRowCol_lname = "DocumentFragmentByRowCol";
	 public final static String DocumentFragmentByRowCol_qname = "pmlp:DocumentFragmentByRowCol";
	 public final static String DocumentFragmentByRowCol_uri = "http://inference-web.org/2.0/pml-provenance.owl#DocumentFragmentByRowCol";
	 public final static Resource  DocumentFragmentByRowCol = ResourceFactory.createResource(DocumentFragmentByRowCol_uri);

	 public final static String AgentList_lname = "AgentList";
	 public final static String AgentList_qname = "pmlp:AgentList";
	 public final static String AgentList_uri = "http://inference-web.org/2.0/pml-provenance.owl#AgentList";
	 public final static Resource  AgentList = ResourceFactory.createResource(AgentList_uri);

	 public final static String InferenceRule_lname = "InferenceRule";
	 public final static String InferenceRule_qname = "pmlp:InferenceRule";
	 public final static String InferenceRule_uri = "http://inference-web.org/2.0/pml-provenance.owl#InferenceRule";
	 public final static Resource  InferenceRule = ResourceFactory.createResource(InferenceRule_uri);

	 public final static String EmptyInformation_lname = "EmptyInformation";
	 public final static String EmptyInformation_qname = "pmlp:EmptyInformation";
	 public final static String EmptyInformation_uri = "http://inference-web.org/2.0/pml-provenance.owl#EmptyInformation";
	 public final static Resource  EmptyInformation = ResourceFactory.createResource(EmptyInformation_uri);

	 public final static String Agent_lname = "Agent";
	 public final static String Agent_qname = "pmlp:Agent";
	 public final static String Agent_uri = "http://inference-web.org/2.0/pml-provenance.owl#Agent";
	 public final static Resource  Agent = ResourceFactory.createResource(Agent_uri);

	 public final static String LearnedSourceUsage_lname = "LearnedSourceUsage";
	 public final static String LearnedSourceUsage_qname = "pmlp:LearnedSourceUsage";
	 public final static String LearnedSourceUsage_uri = "http://inference-web.org/2.0/pml-provenance.owl#LearnedSourceUsage";
	 public final static Resource  LearnedSourceUsage = ResourceFactory.createResource(LearnedSourceUsage_uri);

	 public final static String Ontology_lname = "Ontology";
	 public final static String Ontology_qname = "pmlp:Ontology";
	 public final static String Ontology_uri = "http://inference-web.org/2.0/pml-provenance.owl#Ontology";
	 public final static Resource  Ontology = ResourceFactory.createResource(Ontology_uri);

	 public final static String Dataset_lname = "Dataset";
	 public final static String Dataset_qname = "pmlp:Dataset";
	 public final static String Dataset_uri = "http://inference-web.org/2.0/pml-provenance.owl#Dataset";
	 public final static Resource  Dataset = ResourceFactory.createResource(Dataset_uri);

	 public final static String PrettyNameMappingList_lname = "PrettyNameMappingList";
	 public final static String PrettyNameMappingList_qname = "pmlp:PrettyNameMappingList";
	 public final static String PrettyNameMappingList_uri = "http://inference-web.org/2.0/pml-provenance.owl#PrettyNameMappingList";
	 public final static Resource  PrettyNameMappingList = ResourceFactory.createResource(PrettyNameMappingList_uri);

	 public final static String Source_lname = "Source";
	 public final static String Source_qname = "pmlp:Source";
	 public final static String Source_uri = "http://inference-web.org/2.0/pml-provenance.owl#Source";
	 public final static Resource  Source = ResourceFactory.createResource(Source_uri);

	 public final static String DocumentFragment_lname = "DocumentFragment";
	 public final static String DocumentFragment_qname = "pmlp:DocumentFragment";
	 public final static String DocumentFragment_uri = "http://inference-web.org/2.0/pml-provenance.owl#DocumentFragment";
	 public final static Resource  DocumentFragment = ResourceFactory.createResource(DocumentFragment_uri);

	 public final static String Language_lname = "Language";
	 public final static String Language_qname = "pmlp:Language";
	 public final static String Language_uri = "http://inference-web.org/2.0/pml-provenance.owl#Language";
	 public final static Resource  Language = ResourceFactory.createResource(Language_uri);

	 public final static String Website_lname = "Website";
	 public final static String Website_qname = "pmlp:Website";
	 public final static String Website_uri = "http://inference-web.org/2.0/pml-provenance.owl#Website";
	 public final static Resource  Website = ResourceFactory.createResource(Website_uri);

	 public final static String Information_lname = "Information";
	 public final static String Information_qname = "pmlp:Information";
	 public final static String Information_uri = "http://inference-web.org/2.0/pml-provenance.owl#Information";
	 public final static Resource  Information = ResourceFactory.createResource(Information_uri);

	 public final static String Publication_lname = "Publication";
	 public final static String Publication_qname = "pmlp:Publication";
	 public final static String Publication_uri = "http://inference-web.org/2.0/pml-provenance.owl#Publication";
	 public final static Resource  Publication = ResourceFactory.createResource(Publication_uri);

	 public final static String DeclarativeRule_lname = "DeclarativeRule";
	 public final static String DeclarativeRule_qname = "pmlp:DeclarativeRule";
	 public final static String DeclarativeRule_uri = "http://inference-web.org/2.0/pml-provenance.owl#DeclarativeRule";
	 public final static Resource  DeclarativeRule = ResourceFactory.createResource(DeclarativeRule_uri);

	 public final static String MethodRule_lname = "MethodRule";
	 public final static String MethodRule_qname = "pmlp:MethodRule";
	 public final static String MethodRule_uri = "http://inference-web.org/2.0/pml-provenance.owl#MethodRule";
	 public final static Resource  MethodRule = ResourceFactory.createResource(MethodRule_uri);

	// Property (47)
	 public final static String hasMimetype_lname = "hasMimetype";
	 public final static String hasMimetype_qname = "pmlp:hasMimetype";
	 public final static String hasMimetype_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasMimetype";
	 public final static Property  hasMimetype = ResourceFactory.createProperty(hasMimetype_uri);

	 public final static String hasFormat_lname = "hasFormat";
	 public final static String hasFormat_qname = "pmlp:hasFormat";
	 public final static String hasFormat_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasFormat";
	 public final static Property  hasFormat = ResourceFactory.createProperty(hasFormat_uri);

	 public final static String hasURL_lname = "hasURL";
	 public final static String hasURL_qname = "pmlp:hasURL";
	 public final static String hasURL_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasURL";
	 public final static Property  hasURL = ResourceFactory.createProperty(hasURL_uri);

	 public final static String hasVersion_lname = "hasVersion";
	 public final static String hasVersion_qname = "pmlp:hasVersion";
	 public final static String hasVersion_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasVersion";
	 public final static Property  hasVersion = ResourceFactory.createProperty(hasVersion_uri);

	 public final static String hasLongPrettyName_lname = "hasLongPrettyName";
	 public final static String hasLongPrettyName_qname = "pmlp:hasLongPrettyName";
	 public final static String hasLongPrettyName_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasLongPrettyName";
	 public final static Property  hasLongPrettyName = ResourceFactory.createProperty(hasLongPrettyName_uri);

	 public final static String hasPrettyString_lname = "hasPrettyString";
	 public final static String hasPrettyString_qname = "pmlp:hasPrettyString";
	 public final static String hasPrettyString_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasPrettyString";
	 public final static Property  hasPrettyString = ResourceFactory.createProperty(hasPrettyString_uri);

	 public final static String hasAbstract_lname = "hasAbstract";
	 public final static String hasAbstract_qname = "pmlp:hasAbstract";
	 public final static String hasAbstract_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasAbstract";
	 public final static Property  hasAbstract = ResourceFactory.createProperty(hasAbstract_uri);

	 public final static String hasFromCol_lname = "hasFromCol";
	 public final static String hasFromCol_qname = "pmlp:hasFromCol";
	 public final static String hasFromCol_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasFromCol";
	 public final static Property  hasFromCol = ResourceFactory.createProperty(hasFromCol_uri);

	 public final static String hasPrettyName_lname = "hasPrettyName";
	 public final static String hasPrettyName_qname = "pmlp:hasPrettyName";
	 public final static String hasPrettyName_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasPrettyName";
	 public final static Property  hasPrettyName = ResourceFactory.createProperty(hasPrettyName_uri);

	 public final static String hasFromLanguage_lname = "hasFromLanguage";
	 public final static String hasFromLanguage_qname = "pmlp:hasFromLanguage";
	 public final static String hasFromLanguage_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasFromLanguage";
	 public final static Property  hasFromLanguage = ResourceFactory.createProperty(hasFromLanguage_uri);

	 public final static String hasDescription_lname = "hasDescription";
	 public final static String hasDescription_qname = "pmlp:hasDescription";
	 public final static String hasDescription_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasDescription";
	 public final static Property  hasDescription = ResourceFactory.createProperty(hasDescription_uri);

	 public final static String hasPrettyNameMappingList_lname = "hasPrettyNameMappingList";
	 public final static String hasPrettyNameMappingList_qname = "pmlp:hasPrettyNameMappingList";
	 public final static String hasPrettyNameMappingList_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasPrettyNameMappingList";
	 public final static Property  hasPrettyNameMappingList = ResourceFactory.createProperty(hasPrettyNameMappingList_uri);

	 public final static String hasAuthorList_lname = "hasAuthorList";
	 public final static String hasAuthorList_qname = "pmlp:hasAuthorList";
	 public final static String hasAuthorList_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasAuthorList";
	 public final static Property  hasAuthorList = ResourceFactory.createProperty(hasAuthorList_uri);

	 public final static String hasPublisher_lname = "hasPublisher";
	 public final static String hasPublisher_qname = "pmlp:hasPublisher";
	 public final static String hasPublisher_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasPublisher";
	 public final static Property  hasPublisher = ResourceFactory.createProperty(hasPublisher_uri);

	 public final static String hasFromRow_lname = "hasFromRow";
	 public final static String hasFromRow_qname = "pmlp:hasFromRow";
	 public final static String hasFromRow_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasFromRow";
	 public final static Property  hasFromRow = ResourceFactory.createProperty(hasFromRow_uri);

	 public final static String hasReferenceSourceUsage_lname = "hasReferenceSourceUsage";
	 public final static String hasReferenceSourceUsage_qname = "pmlp:hasReferenceSourceUsage";
	 public final static String hasReferenceSourceUsage_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasReferenceSourceUsage";
	 public final static Property  hasReferenceSourceUsage = ResourceFactory.createProperty(hasReferenceSourceUsage_uri);

	 public final static String hasRawString_lname = "hasRawString";
	 public final static String hasRawString_qname = "pmlp:hasRawString";
	 public final static String hasRawString_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasRawString";
	 public final static Property  hasRawString = ResourceFactory.createProperty(hasRawString_uri);

	 public final static String hasISBN_lname = "hasISBN";
	 public final static String hasISBN_qname = "pmlp:hasISBN";
	 public final static String hasISBN_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasISBN";
	 public final static Property  hasISBN = ResourceFactory.createProperty(hasISBN_uri);

	 public final static String hasToOffset_lname = "hasToOffset";
	 public final static String hasToOffset_qname = "pmlp:hasToOffset";
	 public final static String hasToOffset_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasToOffset";
	 public final static Property  hasToOffset = ResourceFactory.createProperty(hasToOffset_uri);

	 public final static String hasToRow_lname = "hasToRow";
	 public final static String hasToRow_qname = "pmlp:hasToRow";
	 public final static String hasToRow_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasToRow";
	 public final static Property  hasToRow = ResourceFactory.createProperty(hasToRow_uri);

	 public final static String hasOwner_lname = "hasOwner";
	 public final static String hasOwner_qname = "pmlp:hasOwner";
	 public final static String hasOwner_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasOwner";
	 public final static Property  hasOwner = ResourceFactory.createProperty(hasOwner_uri);

	 public final static String isMemberOf_lname = "isMemberOf";
	 public final static String isMemberOf_qname = "pmlp:isMemberOf";
	 public final static String isMemberOf_uri = "http://inference-web.org/2.0/pml-provenance.owl#isMemberOf";
	 public final static Property  isMemberOf = ResourceFactory.createProperty(isMemberOf_uri);

	 public final static String hasReplacee_lname = "hasReplacee";
	 public final static String hasReplacee_qname = "pmlp:hasReplacee";
	 public final static String hasReplacee_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasReplacee";
	 public final static Property  hasReplacee = ResourceFactory.createProperty(hasReplacee_uri);

	 public final static String hasSource_lname = "hasSource";
	 public final static String hasSource_qname = "pmlp:hasSource";
	 public final static String hasSource_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasSource";
	 public final static Property  hasSource = ResourceFactory.createProperty(hasSource_uri);

	 public final static String hasInferenceEngineRule_lname = "hasInferenceEngineRule";
	 public final static String hasInferenceEngineRule_qname = "pmlp:hasInferenceEngineRule";
	 public final static String hasInferenceEngineRule_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasInferenceEngineRule";
	 public final static Property  hasInferenceEngineRule = ResourceFactory.createProperty(hasInferenceEngineRule_uri);

	 public final static String usesInferenceEngine_lname = "usesInferenceEngine";
	 public final static String usesInferenceEngine_qname = "pmlp:usesInferenceEngine";
	 public final static String usesInferenceEngine_uri = "http://inference-web.org/2.0/pml-provenance.owl#usesInferenceEngine";
	 public final static Property  usesInferenceEngine = ResourceFactory.createProperty(usesInferenceEngine_uri);

	 public final static String hasName_lname = "hasName";
	 public final static String hasName_qname = "pmlp:hasName";
	 public final static String hasName_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasName";
	 public final static Property  hasName = ResourceFactory.createProperty(hasName_uri);

	 public final static String hasUsageQueryContent_lname = "hasUsageQueryContent";
	 public final static String hasUsageQueryContent_qname = "pmlp:hasUsageQueryContent";
	 public final static String hasUsageQueryContent_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasUsageQueryContent";
	 public final static Property  hasUsageQueryContent = ResourceFactory.createProperty(hasUsageQueryContent_uri);

	 public final static String hasRuleExample_lname = "hasRuleExample";
	 public final static String hasRuleExample_qname = "pmlp:hasRuleExample";
	 public final static String hasRuleExample_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasRuleExample";
	 public final static Property  hasRuleExample = ResourceFactory.createProperty(hasRuleExample_uri);

	 public final static String hasDataCollectionEndDateTime_lname = "hasDataCollectionEndDateTime";
	 public final static String hasDataCollectionEndDateTime_qname = "pmlp:hasDataCollectionEndDateTime";
	 public final static String hasDataCollectionEndDateTime_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasDataCollectionEndDateTime";
	 public final static Property  hasDataCollectionEndDateTime = ResourceFactory.createProperty(hasDataCollectionEndDateTime_uri);

	 public final static String hasEnglishDescriptionTemplate_lname = "hasEnglishDescriptionTemplate";
	 public final static String hasEnglishDescriptionTemplate_qname = "pmlp:hasEnglishDescriptionTemplate";
	 public final static String hasEnglishDescriptionTemplate_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasEnglishDescriptionTemplate";
	 public final static Property  hasEnglishDescriptionTemplate = ResourceFactory.createProperty(hasEnglishDescriptionTemplate_uri);

	 public final static String hasEscapeCharacterSequence_lname = "hasEscapeCharacterSequence";
	 public final static String hasEscapeCharacterSequence_qname = "pmlp:hasEscapeCharacterSequence";
	 public final static String hasEscapeCharacterSequence_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasEscapeCharacterSequence";
	 public final static Property  hasEscapeCharacterSequence = ResourceFactory.createProperty(hasEscapeCharacterSequence_uri);

	 public final static String hasUsageDateTime_lname = "hasUsageDateTime";
	 public final static String hasUsageDateTime_qname = "pmlp:hasUsageDateTime";
	 public final static String hasUsageDateTime_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasUsageDateTime";
	 public final static Property  hasUsageDateTime = ResourceFactory.createProperty(hasUsageDateTime_uri);

	 public final static String hasConfidenceValue_lname = "hasConfidenceValue";
	 public final static String hasConfidenceValue_qname = "pmlp:hasConfidenceValue";
	 public final static String hasConfidenceValue_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasConfidenceValue";
	 public final static Property  hasConfidenceValue = ResourceFactory.createProperty(hasConfidenceValue_uri);

	 public final static String hasMember_lname = "hasMember";
	 public final static String hasMember_qname = "pmlp:hasMember";
	 public final static String hasMember_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasMember";
	 public final static Property  hasMember = ResourceFactory.createProperty(hasMember_uri);

	 public final static String hasToCol_lname = "hasToCol";
	 public final static String hasToCol_qname = "pmlp:hasToCol";
	 public final static String hasToCol_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasToCol";
	 public final static Property  hasToCol = ResourceFactory.createProperty(hasToCol_uri);

	 public final static String hasToLanguage_lname = "hasToLanguage";
	 public final static String hasToLanguage_qname = "pmlp:hasToLanguage";
	 public final static String hasToLanguage_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasToLanguage";
	 public final static Property  hasToLanguage = ResourceFactory.createProperty(hasToLanguage_uri);

	 public final static String hasShortPrettyName_lname = "hasShortPrettyName";
	 public final static String hasShortPrettyName_qname = "pmlp:hasShortPrettyName";
	 public final static String hasShortPrettyName_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasShortPrettyName";
	 public final static Property  hasShortPrettyName = ResourceFactory.createProperty(hasShortPrettyName_uri);

	 public final static String hasDocument_lname = "hasDocument";
	 public final static String hasDocument_qname = "pmlp:hasDocument";
	 public final static String hasDocument_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasDocument";
	 public final static Property  hasDocument = ResourceFactory.createProperty(hasDocument_uri);

	 public final static String hasPublicationDateTime_lname = "hasPublicationDateTime";
	 public final static String hasPublicationDateTime_qname = "pmlp:hasPublicationDateTime";
	 public final static String hasPublicationDateTime_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasPublicationDateTime";
	 public final static Property  hasPublicationDateTime = ResourceFactory.createProperty(hasPublicationDateTime_uri);

	 public final static String hasModificationDateTime_lname = "hasModificationDateTime";
	 public final static String hasModificationDateTime_qname = "pmlp:hasModificationDateTime";
	 public final static String hasModificationDateTime_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasModificationDateTime";
	 public final static Property  hasModificationDateTime = ResourceFactory.createProperty(hasModificationDateTime_uri);

	 public final static String hasDataCollectionStartDateTime_lname = "hasDataCollectionStartDateTime";
	 public final static String hasDataCollectionStartDateTime_qname = "pmlp:hasDataCollectionStartDateTime";
	 public final static String hasDataCollectionStartDateTime_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasDataCollectionStartDateTime";
	 public final static Property  hasDataCollectionStartDateTime = ResourceFactory.createProperty(hasDataCollectionStartDateTime_uri);

	 public final static String hasCreationDateTime_lname = "hasCreationDateTime";
	 public final static String hasCreationDateTime_qname = "pmlp:hasCreationDateTime";
	 public final static String hasCreationDateTime_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasCreationDateTime";
	 public final static Property  hasCreationDateTime = ResourceFactory.createProperty(hasCreationDateTime_uri);

	 public final static String hasLanguage_lname = "hasLanguage";
	 public final static String hasLanguage_qname = "pmlp:hasLanguage";
	 public final static String hasLanguage_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasLanguage";
	 public final static Property  hasLanguage = ResourceFactory.createProperty(hasLanguage_uri);

	 public final static String hasEncoding_lname = "hasEncoding";
	 public final static String hasEncoding_qname = "pmlp:hasEncoding";
	 public final static String hasEncoding_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasEncoding";
	 public final static Property  hasEncoding = ResourceFactory.createProperty(hasEncoding_uri);

	 public final static String hasContent_lname = "hasContent";
	 public final static String hasContent_qname = "pmlp:hasContent";
	 public final static String hasContent_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasContent";
	 public final static Property  hasContent = ResourceFactory.createProperty(hasContent_uri);

	 public final static String hasFromOffset_lname = "hasFromOffset";
	 public final static String hasFromOffset_qname = "pmlp:hasFromOffset";
	 public final static String hasFromOffset_uri = "http://inference-web.org/2.0/pml-provenance.owl#hasFromOffset";
	 public final static Property  hasFromOffset = ResourceFactory.createProperty(hasFromOffset_uri);

	// Instance (0)


}


 

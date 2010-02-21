/**
 * PMLR.java
 *
 *  This file is automatically generated by PML API 
 *	generated on  Sat Jan 16 17:14:23 EST 2010
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
 *   	label:  PML2 relation ontology@en
 *   	comment:   The relation part of PML2 ontology. @en
 */
public class PMLR{

    protected static final String NS = "http://inference-web.org/2.0/pml-relation.owl#";

    public static final String getURI(){ return NS; }

	// Class (2)
	 public final static String AllSame_lname = "AllSame";
	 public final static String AllSame_qname = "pmlr:AllSame";
	 public final static String AllSame_uri = "http://inference-web.org/2.0/pml-relation.owl#AllSame";
	 public final static Resource  AllSame = ResourceFactory.createResource(AllSame_uri);

	 public final static String Step_lname = "Step";
	 public final static String Step_qname = "pmlr:Step";
	 public final static String Step_uri = "http://inference-web.org/2.0/pml-relation.owl#Step";
	 public final static Resource  Step = ResourceFactory.createResource(Step_uri);

	// Property (14)
	 public final static String hasPart_lname = "hasPart";
	 public final static String hasPart_qname = "pmlr:hasPart";
	 public final static String hasPart_uri = "http://inference-web.org/2.0/pml-relation.owl#hasPart";
	 public final static Property  hasPart = ResourceFactory.createProperty(hasPart_uri);

	 public final static String isMemberOf_lname = "isMemberOf";
	 public final static String isMemberOf_qname = "pmlr:isMemberOf";
	 public final static String isMemberOf_uri = "http://inference-web.org/2.0/pml-relation.owl#isMemberOf";
	 public final static Property  isMemberOf = ResourceFactory.createProperty(isMemberOf_uri);

	 public final static String isPartOf_lname = "isPartOf";
	 public final static String isPartOf_qname = "pmlr:isPartOf";
	 public final static String isPartOf_uri = "http://inference-web.org/2.0/pml-relation.owl#isPartOf";
	 public final static Property  isPartOf = ResourceFactory.createProperty(isPartOf_uri);

	 public final static String hasCondition_lname = "hasCondition";
	 public final static String hasCondition_qname = "pmlr:hasCondition";
	 public final static String hasCondition_uri = "http://inference-web.org/2.0/pml-relation.owl#hasCondition";
	 public final static Property  hasCondition = ResourceFactory.createProperty(hasCondition_uri);

	 public final static String hasOutput_lname = "hasOutput";
	 public final static String hasOutput_qname = "pmlr:hasOutput";
	 public final static String hasOutput_uri = "http://inference-web.org/2.0/pml-relation.owl#hasOutput";
	 public final static Property  hasOutput = ResourceFactory.createProperty(hasOutput_uri);

	 public final static String hasRelation_lname = "hasRelation";
	 public final static String hasRelation_qname = "pmlr:hasRelation";
	 public final static String hasRelation_uri = "http://inference-web.org/2.0/pml-relation.owl#hasRelation";
	 public final static Property  hasRelation = ResourceFactory.createProperty(hasRelation_uri);

	 public final static String hasContext_lname = "hasContext";
	 public final static String hasContext_qname = "pmlr:hasContext";
	 public final static String hasContext_uri = "http://inference-web.org/2.0/pml-relation.owl#hasContext";
	 public final static Property  hasContext = ResourceFactory.createProperty(hasContext_uri);

	 public final static String hasOperation_lname = "hasOperation";
	 public final static String hasOperation_qname = "pmlr:hasOperation";
	 public final static String hasOperation_uri = "http://inference-web.org/2.0/pml-relation.owl#hasOperation";
	 public final static Property  hasOperation = ResourceFactory.createProperty(hasOperation_uri);

	 public final static String dependsOnDirect_lname = "dependsOnDirect";
	 public final static String dependsOnDirect_qname = "pmlr:dependsOnDirect";
	 public final static String dependsOnDirect_uri = "http://inference-web.org/2.0/pml-relation.owl#dependsOnDirect";
	 public final static Property  dependsOnDirect = ResourceFactory.createProperty(dependsOnDirect_uri);

	 public final static String hasRestfulUri_lname = "hasRestfulUri";
	 public final static String hasRestfulUri_qname = "pmlr:hasRestfulUri";
	 public final static String hasRestfulUri_uri = "http://inference-web.org/2.0/pml-relation.owl#hasRestfulUri";
	 public final static Property  hasRestfulUri = ResourceFactory.createProperty(hasRestfulUri_uri);

	 public final static String hasWeight_lname = "hasWeight";
	 public final static String hasWeight_qname = "pmlr:hasWeight";
	 public final static String hasWeight_uri = "http://inference-web.org/2.0/pml-relation.owl#hasWeight";
	 public final static Property  hasWeight = ResourceFactory.createProperty(hasWeight_uri);

	 public final static String dependsOn_lname = "dependsOn";
	 public final static String dependsOn_qname = "pmlr:dependsOn";
	 public final static String dependsOn_uri = "http://inference-web.org/2.0/pml-relation.owl#dependsOn";
	 public final static Property  dependsOn = ResourceFactory.createProperty(dependsOn_uri);

	 public final static String hasInput_lname = "hasInput";
	 public final static String hasInput_qname = "pmlr:hasInput";
	 public final static String hasInput_uri = "http://inference-web.org/2.0/pml-relation.owl#hasInput";
	 public final static Property  hasInput = ResourceFactory.createProperty(hasInput_uri);

	 public final static String hasMember_lname = "hasMember";
	 public final static String hasMember_qname = "pmlr:hasMember";
	 public final static String hasMember_uri = "http://inference-web.org/2.0/pml-relation.owl#hasMember";
	 public final static Property  hasMember = ResourceFactory.createProperty(hasMember_uri);

	// Instance (2)
	 public final static String necessary_condition_lname = "necessary_condition";
	 public final static String necessary_condition_qname = "pmlr:necessary_condition";
	 public final static String necessary_condition_uri = "http://inference-web.org/2.0/pml-relation.owl#necessary_condition";
	 public final static Resource  necessary_condition = ResourceFactory.createResource(necessary_condition_uri);

	 public final static String sufficient_condition_lname = "sufficient_condition";
	 public final static String sufficient_condition_qname = "pmlr:sufficient_condition";
	 public final static String sufficient_condition_uri = "http://inference-web.org/2.0/pml-relation.owl#sufficient_condition";
	 public final static Resource  sufficient_condition = ResourceFactory.createResource(sufficient_condition_uri);



}


 

/**
 * PMLDS.java
 *
 *  This file is automatically generated by PML API 
 *	generated on  Sat Jan 16 17:14:23 EST 2010
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
 *   	label:  Data Structure Ontology (v 0.1.1)@en
 *   	comment:   This ontology offers OWL-Lite definition for object list. It is a restricted version of OWL-S ObjectList (http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl). 
  It is compatible to rdf:List with the following differences: (i) OWL individuals as list members and (ii) appropriate property cardinality restriction. The range of first will specified by the subclasses. 
  @en
 */
public class PMLDS{

    protected static final String NS = "http://inference-web.org/2.0/ds.owl#";

    public static final String getURI(){ return NS; }

	// Class (1)
	 public final static String List_lname = "List";
	 public final static String List_qname = "pmlds:List";
	 public final static String List_uri = "http://inference-web.org/2.0/ds.owl#List";
	 public final static Resource  List = ResourceFactory.createResource(List_uri);

	// Property (2)
	 public final static String first_lname = "first";
	 public final static String first_qname = "pmlds:first";
	 public final static String first_uri = "http://inference-web.org/2.0/ds.owl#first";
	 public final static Property  first = ResourceFactory.createProperty(first_uri);

	 public final static String rest_lname = "rest";
	 public final static String rest_qname = "pmlds:rest";
	 public final static String rest_uri = "http://inference-web.org/2.0/ds.owl#rest";
	 public final static Property  rest = ResourceFactory.createProperty(rest_uri);

	// Instance (1)
	 public final static String nil_lname = "nil";
	 public final static String nil_qname = "pmlds:nil";
	 public final static String nil_uri = "http://inference-web.org/2.0/ds.owl#nil";
	 public final static Resource  nil = ResourceFactory.createResource(nil_uri);



}


 

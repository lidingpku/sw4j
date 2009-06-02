/**
 * RESPONSE.java
 *
 *  This file is automatically generated by PML API 
 *	generated on  Wed Nov 14 00:37:38 EST 2007
 *  
 *  @author: Li Ding (http://www.cs.rpi.edu/~dingl )
 *
 */
package  vocabulary;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 *
 *  
 *  Ontology information 
 *   	label:  Service Response Ontology@en-US
 *   	comment:   This ontology defines Response of service. This ontology is create by Li Ding (http://www.cs.rpi.edu/~dingl/).
  @en-US
 */
public class RESPONSE{

    protected static final String NS = "http://inference-web.org/2007/10/service/response.owl#";

    public static final String getURI(){ return NS; }

	// Class (1)
	 public final static String ServiceResponse_lname = "ServiceResponse";
	 public final static String ServiceResponse_uri = "http://inference-web.org/2007/10/service/response.owl#ServiceResponse";
	 public final static Resource  ServiceResponse = ResourceFactory.createResource(ServiceResponse_uri);

	// Property (2)
	 public final static String hasMsg_lname = "hasMsg";
	 public final static String hasMsg_uri = "http://inference-web.org/2007/10/service/response.owl#hasMsg";
	 public final static Property  hasMsg = ResourceFactory.createProperty(hasMsg_uri);

	 public final static String hasResult_lname = "hasResult";
	 public final static String hasResult_uri = "http://inference-web.org/2007/10/service/response.owl#hasResult";
	 public final static Property  hasResult = ResourceFactory.createProperty(hasResult_uri);

	// Instance (2)
	 public final static String failed_lname = "failed";
	 public final static String failed_uri = "http://inference-web.org/2007/10/service/response.owl#failed";
	 public final static Resource  failed = ResourceFactory.createResource(failed_uri);

	 public final static String succeed_lname = "succeed";
	 public final static String succeed_uri = "http://inference-web.org/2007/10/service/response.owl#succeed";
	 public final static Resource  succeed = ResourceFactory.createResource(succeed_uri);



}


 

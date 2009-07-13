package sw4j.task.rdf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sw4j.util.DataPVHMap;
import sw4j.util.ToolSafe;
import sw4j.util.rdf.ToolJena;
import sw4j.vocabulary.pml.PMLOWL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RSS;

public class TaskDiff {
	
	public static final String DIFF_RSS = "rss";
	public static final String DIFF_RDF = "rdf";
	public static final String DIFF_RDF_ADD = "rdf_add";
	public static final String DIFF_RDF_DEL = "rdf_del";
	
	
	static long gid = 0;
	static long getGid(){
		return gid++;
	}
	Resource createResource(Model m){
		if (ToolSafe.isEmpty(this.m_xmlbase))
			return m.createResource();
		else
			return m.createResource(String.format("%s#id_%s",this.m_xmlbase, getGid() ));
	}
	
	public Resource m_res_root_type = null;
	public String m_xmlbase = null;
	public Model m_model_cur = null;
	String m_url_cur = null;
	public Model m_model_prev = null;
	String m_url_prev = null;
	
	public Model m_model_add = null;
	public Model m_model_del = null;
	
	
	Set<Resource> m_roots_add;
	Set<Resource> m_roots_del;
	Set<Resource> m_roots_other;
	Set<Resource> m_roots_update = new HashSet<Resource>();
	HashMap<Resource,DataPVHMap<Property, RDFNode>> m_map_root_add = new HashMap<Resource,DataPVHMap<Property, RDFNode>>();
	HashMap<Resource,DataPVHMap<Property, RDFNode>> m_map_root_del = new HashMap<Resource,DataPVHMap<Property, RDFNode>>();
	HashMap<Resource,DataPVHMap<Property, RDFNode>> m_map_root_update_cur = new HashMap<Resource,DataPVHMap<Property, RDFNode>>();
	HashMap<Resource,DataPVHMap<Property, RDFNode>> m_map_root_update_prev = new HashMap<Resource,DataPVHMap<Property, RDFNode>>();
	
	 public static TaskDiff create(Model model_cur, Model model_prev, String sz_root_type_uri, String url_cur, String url_prev, String xmlbase){
		 if (ToolSafe.isEmpty(model_cur))
			 return null;

		 if (ToolSafe.isEmpty(model_prev))
			 return null;
		 
		 TaskDiff task = new TaskDiff();
		 task.m_xmlbase = xmlbase;
		 task.m_model_cur = model_cur;
		 task.m_url_cur = url_cur;
		 task.m_model_prev = model_prev;
		 task.m_url_prev = url_prev;
		 if (!ToolSafe.isEmpty(sz_root_type_uri))
			 task.m_res_root_type = model_cur.getResource(sz_root_type_uri);
		 
		 task.run();
		 return task;
	 }
	 
	 @SuppressWarnings("unchecked")
	private void run(){
		 m_model_add = ToolJena.model_diff(m_model_cur, m_model_prev);
		 m_model_del = ToolJena.model_diff( m_model_prev,m_model_cur);
		 
		Set<Resource> roots_cur =null;
		Set<Resource> roots_prev =null;
		if (ToolSafe.isEmpty(m_res_root_type)){
			roots_cur = (Set<Resource>) (m_model_cur.listSubjects()).toSet();
			roots_prev = (Set<Resource>) (m_model_prev.listSubjects()).toSet();
		}else{
			roots_cur = (Set<Resource>) (m_model_cur.listSubjectsWithProperty( RDF.type, m_res_root_type)).toSet();
			roots_prev = (Set<Resource>) (m_model_prev.listSubjectsWithProperty( RDF.type, m_res_root_type)).toSet();
		}
        

	    // 1. compute new roots
		 m_roots_add =  new HashSet<Resource>(roots_cur);
		 m_roots_add.removeAll(roots_prev);
	        
		// Set t =  m_model_add.listSubjects().toSet();
		// t.removeAll(m_roots_add);
		// System.out.println(t);
		 
        // 2. compute deleted roots
        m_roots_del = new HashSet<Resource>(roots_prev);
        m_roots_del.removeAll(roots_cur);
	        
	        // 3. compute updated roots
        m_roots_other = new HashSet<Resource>(roots_cur);
        m_roots_other.retainAll(roots_prev);

        // 4. compute details of updated roots, 
       Iterator<Resource> iter_other = m_roots_other.iterator();
        while (iter_other.hasNext()){
            Resource subject = iter_other.next();
            check_update_details(subject, subject, new HashSet<Resource>());
        }
        
        m_roots_update.addAll(m_map_root_add.keySet());
        m_roots_update.addAll(m_map_root_del.keySet());
        m_roots_update.addAll(m_map_root_update_cur.keySet());
        m_roots_update.addAll(m_map_root_update_prev.keySet());
	 }
	 
	 private void check_update_details(Resource subject, Resource root_subject, Set<Resource> visited){
		 	if (visited.contains(subject))
		 		return;
		 	else
		 		visited.add(subject);

	        	StmtIterator iter_stmt;
	            DataPVHMap<Property, RDFNode> pvm_cur = new  DataPVHMap<Property, RDFNode>();
	        	iter_stmt = m_model_cur.listStatements(subject, null, (String)null);
	        	while (iter_stmt.hasNext()){
	        		Statement stmt = iter_stmt.nextStatement();
	        		pvm_cur.add(stmt.getPredicate(),stmt.getObject());
	        	}
	        	
	            DataPVHMap<Property, RDFNode> pvm_prev = new  DataPVHMap<Property, RDFNode>();
	        	iter_stmt = m_model_prev.listStatements(subject, null, (String)null);
	        	while (iter_stmt.hasNext()){
	        		Statement stmt = iter_stmt.nextStatement();
	        		pvm_prev.add(stmt.getPredicate(),stmt.getObject());
	        	}

	        	// 4 compute details
	        	Iterator<Property> iter_property;
	            DataPVHMap<Property, RDFNode> pvm_add = new  DataPVHMap<Property, RDFNode>();
	            DataPVHMap<Property, RDFNode> pvm_delete = new  DataPVHMap<Property, RDFNode>();
	            DataPVHMap<Property, RDFNode> pvm_update_cur = new  DataPVHMap<Property, RDFNode>();            
	            DataPVHMap<Property, RDFNode> pvm_update_prev = new  DataPVHMap<Property, RDFNode>();

	            // 4a. 4c update - add/update property
	            iter_property = pvm_cur.keySet().iterator();
	            while (iter_property.hasNext()){
	            	Property property = iter_property.next();
	            	if (pvm_prev.keySet().contains(property)){
	            		Collection<RDFNode> nodes_cur = pvm_cur.getValues(property);
	            		Collection<RDFNode> nodes_prev = pvm_prev.getValues(property);
	            		
	            		HashSet<RDFNode> nodes_add = new HashSet<RDFNode>(nodes_cur);
	            		nodes_add.removeAll(nodes_prev);
	            		HashSet<RDFNode> nodes_delete = new HashSet<RDFNode>(nodes_prev);
	            		nodes_delete.removeAll(nodes_cur);
	            		HashSet<RDFNode> nodes_same = new HashSet<RDFNode>(nodes_prev);
	            		nodes_same.retainAll(nodes_cur);
	            		
	            		if (nodes_add.size()>0){
	                		pvm_update_cur.add(property, nodes_add);
	            		}
	                	if (nodes_delete.size()>0){
	                		pvm_update_prev.add(property, nodes_delete);            			
	            		}
	                	if (nodes_same.size()>0){
	             	       Iterator<RDFNode> iter_other = nodes_same.iterator();
		           	        while (iter_other.hasNext()){
		           	        	RDFNode node = iter_other.next();
		           	        	if (node.isLiteral())
		           	        		continue;
		           	  
		                		check_update_details( (Resource)node, root_subject, visited); 
		           	        }
	            		}
	                	
	            	}else{
	            		pvm_add.add(property, pvm_cur.getValues(property));
	            	}
	            }

	        	// 4b. update - del property
	            iter_property = pvm_prev.keySet().iterator();
	            while (iter_property.hasNext()){
	            	Property property = iter_property.next();
	            	if (pvm_cur.keySet().contains(property)){
	            	}else{
	            		pvm_delete.add(property, pvm_prev.getValues(property));
	            	}
	            }

	            if (pvm_add.entrySet().size()>0)
	            	m_map_root_add.put(root_subject,pvm_add);
	            if (pvm_delete.entrySet().size()>0)
	            	m_map_root_del.put(root_subject,pvm_delete);
	            if (pvm_update_cur.entrySet().size()>0)
	            	m_map_root_update_cur.put(root_subject,pvm_update_cur);
	            if (pvm_update_prev.entrySet().size()>0)
	            	m_map_root_update_prev.put(root_subject,pvm_update_prev);
	        
	 }
	 
	 
	 public Model getOutputRdfDiff(){
		 Model m = ModelFactory.createDefaultModel();
		 
		 //add item
		 {
			 Iterator<Resource> iter = this.m_roots_add.iterator();
			 while (iter.hasNext()){
				 Resource root = iter.next();
				 
				 Resource entry = this.createResource(m);
				 entry.addProperty(PMLOWL.hasInstanceReference, root);
				 if (!ToolSafe.isEmpty(this.m_res_root_type))
					 entry.addProperty(PMLOWL.hasInstanceType, this.m_res_root_type );
				 else{
					 entry.addProperty(PMLOWL.hasInstanceType, (Resource)( ToolJena.getValueOfProperty(m_model_cur, root, RDF.type, OWL.Thing)));
				 }
				 entry.addProperty(PMLOWL.hasDiffRelation, PMLOWL.hasDiffAddInstance);
				 entry.addProperty(PMLOWL.hasDiffSourceCur, m.createResource(this.m_url_cur));
				 entry.addProperty(PMLOWL.hasDiffSourcePrev, m.createResource(this.m_url_prev));
				 entry.addProperty(RDFS.comment, getDiffDescription(root));
			 }
		 }
		 
		 //deleted item
		 {
			 Iterator<Resource> iter = this.m_roots_del.iterator();
			 while (iter.hasNext()){
				 Resource root = iter.next();

				 Resource entry = this.createResource(m);
				 entry.addProperty(PMLOWL.hasInstanceReference, root);
				 if (!ToolSafe.isEmpty(this.m_res_root_type))
					 entry.addProperty(PMLOWL.hasInstanceType, this.m_res_root_type );
				 else{
					 entry.addProperty(PMLOWL.hasInstanceType, (Resource)( ToolJena.getValueOfProperty(m_model_prev, root, RDF.type, OWL.Thing)));
				 }
				 entry.addProperty(PMLOWL.hasDiffRelation, PMLOWL.hasDiffDelInstance);
				 entry.addProperty(PMLOWL.hasDiffSourceCur, m.createResource(this.m_url_cur));
				 entry.addProperty(PMLOWL.hasDiffSourcePrev, m.createResource(this.m_url_prev));
				 entry.addProperty(RDFS.comment, getDiffDescription(root));
			}
		 }
	

		 //updated item
		 /*
		 {
			 Iterator<Resource> iter = this.m_roots_update.iterator();
			 while (iter.hasNext()){
				 Resource root = iter.next();
				 
				 Resource entry = this.createResource(m);
				 entry.addProperty(PMLOWL.hasInstanceReference, root);
				 entry.addProperty(PMLOWL.hasInstanceType, this.m_res_root_type );
				 entry.addProperty(PMLOWL.hasDiffRelation, PMLOWL.hasDiffUpdateInstance);
				 entry.addProperty(PMLOWL.hasDiffSourceCur, this.m_url_cur);
				 entry.addProperty(PMLOWL.hasDiffSourcePrev, this.m_url_prev);
				 
				 

			 }
		 }*/
		 
		 {
			 Iterator<Map.Entry<Resource,DataPVHMap<Property, RDFNode>>> iter = this.m_map_root_add.entrySet().iterator();
			 while(iter.hasNext()){
				 Map.Entry<Resource,DataPVHMap<Property, RDFNode>> item = iter.next();
				 Resource root = item.getKey();
				 Iterator<Map.Entry<Property, Set<RDFNode>>> iter_item = item.getValue().entrySet().iterator();
				 while (iter_item.hasNext()){
					 Map.Entry<Property, Set<RDFNode>> pvs = iter_item.next();
					 Property property =pvs.getKey();
					 
					 Resource entry = this.createResource(m);
					 entry.addProperty(PMLOWL.hasInstanceReference, root);
					 if (!ToolSafe.isEmpty(this.m_res_root_type))
						 entry.addProperty(PMLOWL.hasInstanceType, this.m_res_root_type );
					 else{
						 entry.addProperty(PMLOWL.hasInstanceType, (Resource)( ToolJena.getValueOfProperty(m_model_cur, root, RDF.type, OWL.Thing)));
					 }
					 entry.addProperty(PMLOWL.hasDiffRelation, PMLOWL.hasDiffUpdateInstanceAddProperty);
					 entry.addProperty(PMLOWL.hasDiffSourceCur, m.createResource(this.m_url_cur));
					 entry.addProperty(PMLOWL.hasDiffSourcePrev, m.createResource(this.m_url_prev));
					 entry.addProperty(PMLOWL.hasPropertyReference, property );
					 entry.addProperty(RDFS.comment, getDiffDescription(root,property));

				 }
			 }
		 }
		 {
			 Iterator<Map.Entry<Resource,DataPVHMap<Property, RDFNode>>> iter = this.m_map_root_del.entrySet().iterator();
			 while(iter.hasNext()){
				 Map.Entry<Resource,DataPVHMap<Property, RDFNode>> item = iter.next();
				 Resource root = item.getKey();
				 Iterator<Map.Entry<Property, Set<RDFNode>>> iter_item = item.getValue().entrySet().iterator();
				 while (iter_item.hasNext()){
					 Map.Entry<Property, Set<RDFNode>> pvs = iter_item.next();
					 Property property =pvs.getKey();

					 Resource entry = this.createResource(m);
					 entry.addProperty(PMLOWL.hasInstanceReference, root);
					 if (!ToolSafe.isEmpty(this.m_res_root_type))
						 entry.addProperty(PMLOWL.hasInstanceType, this.m_res_root_type );
					 else{
						 entry.addProperty(PMLOWL.hasInstanceType, (Resource)( ToolJena.getValueOfProperty(m_model_prev, root, RDF.type, OWL.Thing)));
					 }
					 entry.addProperty(PMLOWL.hasDiffRelation, PMLOWL.hasDiffUpdateInstanceDelProperty);
					 entry.addProperty(PMLOWL.hasDiffSourceCur, m.createResource(this.m_url_cur));
					 entry.addProperty(PMLOWL.hasDiffSourcePrev, m.createResource(this.m_url_prev));
					 entry.addProperty(PMLOWL.hasPropertyReference, property );
					 entry.addProperty(RDFS.comment, getDiffDescription(root,property));

				 }
			 }
		 }
		 {
			 Iterator<Map.Entry<Resource,DataPVHMap<Property, RDFNode>>> iter = this.m_map_root_update_cur.entrySet().iterator();
			 while(iter.hasNext()){
				 Map.Entry<Resource,DataPVHMap<Property, RDFNode>> item = iter.next();
				 Resource root = item.getKey();
				 Iterator<Map.Entry<Property, Set<RDFNode>>> iter_item = item.getValue().entrySet().iterator();
				 while (iter_item.hasNext()){
					 Map.Entry<Property, Set<RDFNode>> pvs = iter_item.next();
					 Property property =pvs.getKey();

					 Resource entry = this.createResource(m);
					 entry.addProperty(PMLOWL.hasInstanceReference, root);
					 if (!ToolSafe.isEmpty(this.m_res_root_type))
						 entry.addProperty(PMLOWL.hasInstanceType, this.m_res_root_type );
					 else{
						 entry.addProperty(PMLOWL.hasInstanceType, (Resource)( ToolJena.getValueOfProperty(m_model_cur, root, RDF.type, OWL.Thing)));
					 }
					 entry.addProperty(PMLOWL.hasDiffRelation, PMLOWL.hasDiffUpdateInstanceUpdateProperty);
					 entry.addProperty(PMLOWL.hasDiffSourceCur, m.createResource(this.m_url_cur));
					 entry.addProperty(PMLOWL.hasDiffSourcePrev, m.createResource(this.m_url_prev));
					 entry.addProperty(PMLOWL.hasPropertyReference, property );
					 entry.addProperty(RDFS.comment, getDiffDescription(root,property));

				 }
			 }
		 }
		 
		 
		 ToolJena.model_copyNsPrefix(m, m_model_cur);
		 m.setNsPrefix(PMLOWL.class.getSimpleName().toLowerCase(), PMLOWL.getURI());
		 
		 return m;
	 }

	 private String prettyDescription(Resource root, Model m){
		 String ret ="";
		 StmtIterator iter = m.listStatements(root, null, (String)null);
		 while (iter.hasNext()){
			 Statement stmt = iter.nextStatement();
			 
			 if (stmt.getPredicate().equals(RDF.type))
				 continue;
			 ret += String.format(" --> %s. %s<br/>", ToolJena.prettyPrint(stmt.getPredicate()), ToolJena.prettyPrint(stmt.getObject()));
		 }
		 return ret;	 
	 }
	 
	 private static String prettyRDFNodes(Collection<RDFNode> nodes){
		 String ret ="";
		 if (ToolSafe.isEmpty(nodes))
			 return ret;
		 Iterator<RDFNode> iter = nodes.iterator();
		 while (iter.hasNext()){
			 RDFNode node = iter.next();
			 ret += String.format("%s, ", ToolJena.prettyPrint(node));
		 }
		 return ret;
	 }
	 
	 public String getDiffDescription(Resource root){
		 return getDiffDescription(root, null);
	 }
	 

	 private static String getDiffDescription2(String title, String type, Resource root, Property property, HashMap<Resource,DataPVHMap<Property, RDFNode>> map){
		 String ret = String.format("%s %s<br/>",
				 title,
				 ToolJena.prettyPrint(root));
		 if (ToolSafe.isEmpty(property)){
			 
			 Iterator<Map.Entry<Property, Set<RDFNode>>> iter_item = map.get(root).entrySet().iterator();
			 while (iter_item.hasNext()){
				 Map.Entry<Property, Set<RDFNode>> pvs = iter_item.next();
				 property =pvs.getKey();

				 ret += String.format(" --> %s . [%s]{%s} <br/>",
						 ToolJena.prettyPrint(property),
						 type,
						 prettyRDFNodes(pvs.getValue()));
			 }

		 }else{
			 ret += String.format(" --> %s . [%s]{%s}",
					 ToolJena.prettyPrint(property),
					 type,
					 prettyRDFNodes(map.get(root).getValues((property))));
		 }
		 return ret;

	 }
	 
	 private static String getDiffDescription3(String title, Resource root, Property property, HashMap<Resource,DataPVHMap<Property, RDFNode>> map_cur, HashMap<Resource,DataPVHMap<Property, RDFNode>> map_prev){
		 String ret = String.format("%s %s<br/>",
				 title,
				 ToolJena.prettyPrint(root));
		 if (ToolSafe.isEmpty(property)){
			 

			 HashSet<Property> props = new HashSet<Property>();
			 props.addAll(map_cur.get(root).keySet());
			 props.addAll(map_prev.get(root).keySet());
			 
			 Iterator<Property> iter = props.iterator();
			 while (iter.hasNext()){
				 ret += String.format("  --> %s . [add] {%s} [del] {%s}<br/>",
						 ToolJena.prettyPrint(property),
						 prettyRDFNodes(map_cur.get(root).getValues((property))),
						 prettyRDFNodes(map_prev.get(root).getValues((property))));				 
			 }

		 }else{
			 ret += String.format(" --> %s . [add] {%s} [del] {%s}",
					 ToolJena.prettyPrint(property),
					 prettyRDFNodes(map_cur.get(root).getValues((property))),
					 prettyRDFNodes(map_prev.get(root).getValues((property))));
		 }
		 return ret;

	 }
	 
	 public String getDiffDescription(Resource root, Property property){
		 if (m_roots_add.contains(root)){
			 return String.format("[add instance] %s <br/> %s",
					 ToolJena.prettyPrint(root),
					 prettyDescription(root,  m_model_cur));
		 }else if (m_roots_del.contains(root)){
			 return String.format("[del instance] %s <br/> %s",
					 ToolJena.prettyPrint(root),
					 prettyDescription(root,  m_model_prev));
		 }else if (m_map_root_add.keySet().contains(root) && !ToolSafe.isEmpty(property)){
			 return getDiffDescription2("[update instance]", "add", root, property, m_map_root_add );
		 }else if (m_map_root_del.keySet().contains(root)){
			 return getDiffDescription2("[update instance]","del", root, property, m_map_root_del );
		 }else if (m_map_root_update_cur.keySet().contains(root)&& !ToolSafe.isEmpty(property)){
			 return getDiffDescription3("[update instance]",root, property, m_map_root_update_cur, m_map_root_update_prev  );

		 }
		 
		 return "";
	 }
	 
	 public Model getOutputRss(String rss_title, Property p_title, Property p_link){
		 Model m = ModelFactory.createDefaultModel();

		 //add channel
		 Resource channel = m.createResource(this.m_xmlbase, RSS.channel);
		 
		 
		 channel.addProperty(RSS.link, this.m_xmlbase);
		 channel.addProperty(RSS.title, rss_title);
		 
		 //add item
		 {
			 Iterator<Resource> iter = this.m_roots_add.iterator();
			 while (iter.hasNext()){
				 Resource root = iter.next();

				 String title = "[new]" + ToolJena.getValueOfProperty(this.m_model_cur,  root, p_title,"");
				 String link = ToolJena.getValueOfProperty(this.m_model_cur,  root, p_link,"");
				 String description = getDiffDescription(root);

				 m.add(root, RDF.type, RSS.item);
				 m.add(root, RSS.title, title);
				 if (!ToolSafe.isEmpty(link))
					 m.add(root, RSS.link, link);
				 if (!ToolSafe.isEmpty(description))
					 m.add(root, RSS.description, description);
			 }
		 }
		 
		 //deleted item
		 {
			 Iterator<Resource> iter = this.m_roots_del.iterator();
			 while (iter.hasNext()){
				 Resource root = iter.next();

				 String title = "[deleted]" + ToolJena.getValueOfProperty(this.m_model_prev,  root, p_title,"");
				 String link = ToolJena.getValueOfProperty(this.m_model_prev,  root, p_link,"");
				 String description = getDiffDescription(root);

				 m.add(root, RDF.type, RSS.item);
				 m.add(root, RSS.title, title);
				 if (!ToolSafe.isEmpty(link))
					 m.add(root, RSS.link, link);
				 if (!ToolSafe.isEmpty(description))
					 m.add(root, RSS.description, description);
			}
		 }
		 
		 //updated item
		/* {
			 Iterator<Resource> iter = this.m_roots_update.iterator();
			 while (iter.hasNext()){
				 Resource root = iter.next();
				 
				 String title = "[updated]" + ToolJena.getValueOfProperty(this.m_model_cur,  root, p_title,"");
				 String link = ToolJena.getValueOfProperty(this.m_model_cur,  root, p_link,"");
				 String description = getDiffDescription(root);

				 m.add(root, RDF.type, RSS.item);
				 m.add(root, RSS.title, title);
				 if (!ToolSafe.isEmpty(link))
					 m.add(root, RSS.link, link);
				 if (!ToolSafe.isEmpty(description))
					 m.add(root, RSS.description, description);
				 
			 }
		 }*/
		 
		 
		 {
			 Iterator<Map.Entry<Resource,DataPVHMap<Property, RDFNode>>> iter = this.m_map_root_add.entrySet().iterator();
			 while(iter.hasNext()){
				 Map.Entry<Resource,DataPVHMap<Property, RDFNode>> item = iter.next();
				 Resource root = item.getKey();

				 String title = "[updated (add)]" + ToolJena.getValueOfProperty(this.m_model_cur,  root, p_title,"");
				 String link = ToolJena.getValueOfProperty(this.m_model_cur,  root, p_link,"");
				 String description = "";
				 
				 Iterator<Map.Entry<Property, Set<RDFNode>>> iter_item = item.getValue().entrySet().iterator();
				 while (iter_item.hasNext()){
					 Map.Entry<Property, Set<RDFNode>> pvs = iter_item.next();
					 Property property =pvs.getKey();
					 
					 description += getDiffDescription(root, property) +"<p/>";

				 }

				 m.add(root, RDF.type, RSS.item);
				 m.add(root, RSS.title, title);
				 if (!ToolSafe.isEmpty(link))
					 m.add(root, RSS.link, link);
				 if (!ToolSafe.isEmpty(description))
					 m.add(root, RSS.description, description);
			 }
		 }
		 {
			 Iterator<Map.Entry<Resource,DataPVHMap<Property, RDFNode>>> iter = this.m_map_root_del.entrySet().iterator();
			 while(iter.hasNext()){
				 Map.Entry<Resource,DataPVHMap<Property, RDFNode>> item = iter.next();
				 Resource root = item.getKey();
				 
				 String title = "[updated (del)]" + ToolJena.getValueOfProperty(this.m_model_prev,  root, p_title,"");
				 String link = ToolJena.getValueOfProperty(this.m_model_prev,  root, p_link,"");
				 String description = "";

				 Iterator<Map.Entry<Property, Set<RDFNode>>> iter_item = item.getValue().entrySet().iterator();
				 while (iter_item.hasNext()){
					 Map.Entry<Property, Set<RDFNode>> pvs = iter_item.next();
					 Property property =pvs.getKey();

					 description += getDiffDescription(root, property) +"<p/>";
				 }
				 
				 m.add(root, RDF.type, RSS.item);
				 m.add(root, RSS.title, title);
				 if (!ToolSafe.isEmpty(link))
					 m.add(root, RSS.link, link);
				 if (!ToolSafe.isEmpty(description))
					 m.add(root, RSS.description, description);
			 }
		 }
		 {
			 Iterator<Map.Entry<Resource,DataPVHMap<Property, RDFNode>>> iter = this.m_map_root_update_cur.entrySet().iterator();
			 while(iter.hasNext()){
				 Map.Entry<Resource,DataPVHMap<Property, RDFNode>> item = iter.next();
				 Resource root = item.getKey();

				 String title = "[updated (update)]" + ToolJena.getValueOfProperty(this.m_model_cur,  root, p_title,"");
				 String link = ToolJena.getValueOfProperty(this.m_model_cur,  root, p_link,"");
				 String description = "";
				 
				 Iterator<Map.Entry<Property, Set<RDFNode>>> iter_item = item.getValue().entrySet().iterator();
				 while (iter_item.hasNext()){
					 Map.Entry<Property, Set<RDFNode>> pvs = iter_item.next();
					 Property property =pvs.getKey();

					 description += getDiffDescription(root, property) +"<p/>";

				 }

				 m.add(root, RDF.type, RSS.item);
				 m.add(root, RSS.title, title);
				 if (!ToolSafe.isEmpty(link))
					 m.add(root, RSS.link, link);
				 if (!ToolSafe.isEmpty(description))
					 m.add(root, RSS.description, description);
			 }
		 }		 
		 
		 ToolJena.model_copyNsPrefix(m, m_model_cur);
		 m.setNsPrefix("", RSS.getURI());
		 
		 return m;
	 }


}

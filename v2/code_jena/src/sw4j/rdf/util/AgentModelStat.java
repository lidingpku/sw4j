/**
MIT License

Copyright (c) 2009 

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */


package sw4j.rdf.util;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import sw4j.util.DataPVCMap;
import sw4j.util.DataQname;
import sw4j.util.ToolSafe;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
/**
 * statistics for an RDF graph
 * 
 * @author Li Ding
 *
 */
public class AgentModelStat {
	private Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}
	public static final String META_USAGE_DEF_C = "defined as class";
	public static final String META_USAGE_DEF_I = "defined as instance";
	public static final String META_USAGE_DEF_P = "defined as property";
	public static final String META_USAGE_INS_C = "instantiated as class";
	public static final String META_USAGE_INS_P = "instantiated as property";
	public static final String META_USAGE_REF_C = "referenced as class";
	public static final String META_USAGE_REF_P = "referenced as property";
	private Set<String> m_imported_ontology = new HashSet<String>();
	private DataPVCMap<String, Resource> m_map_relation_bnode = new DataPVCMap<String, Resource>();
	private DataPVCMap<String, String> m_map_relation_named = new DataPVCMap<String, String>();
	private Set<String> m_namespace_ontology = new TreeSet<String>();

	private void do_collect_import(Statement stmt) {
		if (OWL.imports.equals(stmt.getPredicate())){
			String  ns = ToolJena.getNodeString(stmt.getObject());
			//m_namespace_ontology.add(ns);
			m_imported_ontology.add(ns);
		}
		//TODO
	}

	private boolean do_count_instance_usage(String usage, RDFNode node){
		if (node.isURIResource()){
			m_map_relation_named.add(usage, ToolJena.getNodeString(node));
			return true;
		}else if (node.isAnon()){
			m_map_relation_bnode.add(usage, (Resource)node);
			return true;
		}else{
			return false;
		}
	}

	private boolean do_count_meta_usage(String usage, RDFNode node){
		if (node.isURIResource()){
			String ns = DataQname.extractNamespace(((Resource)node).getURI());
			if (!ToolSafe.isEmpty(ns))
				m_namespace_ontology.add(ns);
			
			m_map_relation_named.add(usage, ToolJena.getNodeString(node));
			return true;
		}else if (node.isAnon()){
			m_map_relation_bnode.add(usage, (Resource)node);
			return true;
		}else{
			return false;
		}
	}

	public Set<String> getMetaTermsByUsage(String szUsage){
		return m_map_relation_named.getValuesAsSet(szUsage);
	}

	/*
	public Set<String> getReferencedOntologies(){
		return m_namespace_ontology;
	}
	
	public Set<String> getImportedOntologies(){
		return m_imported_ontology;
	}
	*/
	
	public Set<String> getNormalizedLinks(boolean bUseImport, boolean bUseNamespace){
		Set<String> ret = new HashSet<String>();
		
		if (bUseImport){
			Iterator<String> iter_ns = m_imported_ontology.iterator();
			while (iter_ns.hasNext()){
				String szNewURL = iter_ns.next();
				szNewURL = DataQname.extractNamespaceUrl(szNewURL);
				if (null== szNewURL)
					continue;
				
				if (ToolModelAnalysis.getMetaMamespaces().contains(szNewURL))
					continue;
				
				ret.add(szNewURL);
			}
		}
		
		if (bUseNamespace){
			Iterator<String> iter_ns = m_namespace_ontology.iterator();
			while (iter_ns.hasNext()){
				String szNewURL = iter_ns.next();
				szNewURL = DataQname.extractNamespaceUrl(szNewURL);
				if (null== szNewURL)
					continue;
	
				if (ToolModelAnalysis.getMetaMamespaces().contains(szNewURL))
					continue;
				
				ret.add(szNewURL);
			}
		}
		
		
		return ret;
	}

	public boolean hasInstanceData(){
		if (!ToolSafe.isEmpty(m_map_relation_named.getValues(META_USAGE_INS_C)))
			return true;
		if (!ToolSafe.isEmpty(m_map_relation_bnode.getValues(META_USAGE_INS_C)))
			return true;
		
		return false;
	}

	public boolean hasOntology(){
		if (!ToolSafe.isEmpty(m_map_relation_named.getValues(META_USAGE_DEF_C)))
			return true;
		if (!ToolSafe.isEmpty(m_map_relation_bnode.getValues(META_USAGE_DEF_C)))
			return true;
		
		if (!ToolSafe.isEmpty(m_map_relation_named.getValues(META_USAGE_DEF_P)))
			return true;
		if (!ToolSafe.isEmpty(m_map_relation_bnode.getValues(META_USAGE_DEF_P)))
			return true;
		
		return false;
	}

	public void print(){
		System.out.println("namespace:"+m_namespace_ontology);
		System.out.println(m_map_relation_named);
		System.out.println("imported:"+m_imported_ontology);
	}

	public void traverse(Model m){
		StmtIterator iter;
		iter = m.listStatements();
		while (iter.hasNext()){
			Statement stmt      = iter.nextStatement();  // get next statement
			Resource subject = stmt.getSubject();
			//String sz_subject = ToolJena.getNodeString(subject);
			//String sz_ns_subject = ToolURI.extractNamespace(sz_subject);
	
			Property predicate = stmt.getPredicate();
			String sz_predicate = ToolJena.getNodeString(predicate);
			//String sz_ns_predicate = ToolURI.extractNamespace(sz_predicate);
			
			RDFNode object= stmt.getObject();
			String sz_object = ToolJena.getNodeString(object);
			//String sz_ns_object= null;
			if (object.isURIResource())
				DataQname.extractNamespace(sz_object);
	
			do_collect_import(stmt);
	
			do_count_meta_usage(META_USAGE_INS_P, predicate);
				
			// when predicate is type
			if (RDF.type.equals(predicate)){
				if (!do_count_meta_usage(META_USAGE_INS_C, object))
					getLogger().error("rdf:type has an literal object. see "+stmt);
	
				if (ToolModelAnalysis.testMetaClass(object)){
					do_count_meta_usage(META_USAGE_DEF_C, subject);
				}else if (ToolModelAnalysis.testMetaProperty(object)){
					do_count_meta_usage(META_USAGE_DEF_P, subject);
				}else{
					do_count_instance_usage(META_USAGE_DEF_I, subject);
				}
				
				continue;
			}
			
			// otherwise check if the predicate indicates class/property usage
			String type_predicate = ToolModelAnalysis.checkRelationNonInstance(sz_predicate);
			if (!ToolSafe.isEmpty(type_predicate)){
				if (ToolModelAnalysis.RELATION_RES_CC.equals(type_predicate)){
					do_count_meta_usage(META_USAGE_REF_C, subject);
					if (!do_count_meta_usage(META_USAGE_REF_C, object))
						getLogger().error("object should not be literal. see "+stmt);
				}else if (ToolModelAnalysis.RELATION_RES_CP.equals(type_predicate)){
					do_count_meta_usage(META_USAGE_REF_C, subject);
					if (!do_count_meta_usage(META_USAGE_REF_P, object))
						getLogger().error("object should not be literal. see "+stmt);
				}else if (ToolModelAnalysis.RELATION_RES_PC.equals(type_predicate)){
					do_count_meta_usage(META_USAGE_REF_P, subject);
					if (!do_count_meta_usage(META_USAGE_REF_C, object))
						getLogger().error("object should not be literal. see "+stmt);
				}else if (ToolModelAnalysis.RELATION_RES_PP.equals(type_predicate)){
					do_count_meta_usage(META_USAGE_REF_P, subject);
					if (!do_count_meta_usage(META_USAGE_REF_P, object))
						getLogger().error("object should not be literal. see "+stmt);
				}else if (ToolModelAnalysis.RELATION_RES_C.equals(type_predicate)){
					do_count_meta_usage(META_USAGE_REF_C, subject);
				}else if (ToolModelAnalysis.RELATION_RES_CLC.equals(type_predicate)){
					do_count_meta_usage(META_USAGE_REF_C, subject);
					if (!do_count_meta_usage(META_USAGE_REF_C, object))
						getLogger().error("object should not be literal. see "+stmt);
					
					if (object.isResource()){
						List<RDFNode> nodes = ToolJena.getListMembers(m, (Resource)object);
						Iterator<RDFNode> iter_node = nodes.iterator();
						while (iter_node.hasNext()){
							do_count_meta_usage(META_USAGE_REF_C, iter_node.next());
						}
					}
				}
				
				continue;
			}
						
		}
		
	}

}

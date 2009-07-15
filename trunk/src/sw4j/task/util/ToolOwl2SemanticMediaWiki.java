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
package sw4j.task.util;

/**
 * general wiki dump generation functions for semantic mediaWiki
 * 
 * @author Li Ding
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import sw4j.util.DataQname;
import sw4j.util.ToolSafe;
import sw4j.util.rdf.ToolJena;
import sw4j.util.web.ToolMediaWiki;


public class ToolOwl2SemanticMediaWiki {
	
	public static void dump_ontology(String szOntologyNamespace, String szPrefix, String szFilename ){
		HashMap<String,String> map_pages = get_wikidump_pages(szOntologyNamespace, szPrefix);
		
		System.out.println(map_pages.size() +" page generated!");
		ToolMediaWiki.create_wiki_dump( map_pages, szFilename);				
	}
	
	/**
	 * create wiki dump of an ontology
	 * 
	 * @param szFileOrUri
	 * @param szOntologyNamespace
	 * @return a list of page name and page content mappings.
	 */
	private static HashMap<String, String> get_wikidump_pages(String szOntologyNamespace, String szPrefix) {
		HashMap<String, String> data= new HashMap<String, String>();	
		HashMap<String, String> data_import= new HashMap<String, String>();	
		HashMap<String, String> data_import_default= new HashMap<String, String>();	

		HashMap<String, TreeSet<String>> data1= new HashMap<String, TreeSet<String>>();	
		HashMap<String, TreeSet<String>> data2= new HashMap<String, TreeSet<String>>();	
		HashMap<String, TreeSet<String>> data3= new HashMap<String, TreeSet<String>>();	
		

		HashMap<Resource, ArrayList<Statement>> map_anon_x_stmts= new HashMap<Resource, ArrayList<Statement>>();	
		HashMap<Resource, ArrayList<Statement>> map_res_anon_stmts= new HashMap<Resource, ArrayList<Statement>>();	
		HashMap<Resource, Resource> map_anon_res= new HashMap<Resource, Resource>();	
		HashMap<Resource, Resource> map_anon_anon= new HashMap<Resource, Resource>();	
		HashMap<Resource, String> map_res_wikins = new HashMap<Resource, String>();
		
		String szSMW_import_page = String.format("MediaWiki:smw_import_%s", szPrefix);
		AgentModelLoader loader = new AgentModelLoader(szOntologyNamespace);
		Model m = loader.getModelData();
		
		String szOntologyNamespaceUrl = DataQname.extractNamespaceUrl(szOntologyNamespace);
		
		if (null== m)
			return data;

		System.out.println(m.getNsPrefixMap());
		
		//identify all classes
		{
			HashSet<Resource> classlist = new HashSet<Resource>();
			classlist.add(OWL.Class);
			classlist.add(OWL.DeprecatedClass);
			classlist.add(RDFS.Class);

			HashSet<Resource> propertylist = new HashSet<Resource>();
			propertylist.add(OWL.DatatypeProperty);
			propertylist.add(OWL.ObjectProperty);
			propertylist.add(OWL.AnnotationProperty);
			propertylist.add(OWL.DeprecatedProperty);
			propertylist.add(RDF.Property);

			HashMap<Resource,String[]> prop_wikins = new HashMap<Resource,String[]>();
			prop_wikins.put(RDF.type, new String[]{null,"Category:"});
			
			prop_wikins.put(RDFS.domain, new String[]{"Property:","Category:"});
			prop_wikins.put(RDFS.range, new String[]{"Property:","Category:"});
			
			prop_wikins.put(RDFS.subClassOf, new String[]{"Category:","Category:"});
			
			prop_wikins.put(RDFS.subPropertyOf, new String[]{"Property:","Property:"});
			
			StmtIterator iter = m.listStatements();
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement();

				if (stmt.getPredicate().equals(RDF.type)){
					if (classlist.contains(stmt.getObject()))
						map_res_wikins.put(stmt.getSubject(),"Category:");

					if (propertylist.contains(stmt.getObject()))
						map_res_wikins.put(stmt.getSubject(),"Property:");
				}

				String[] wikins = prop_wikins.get(stmt.getPredicate());
				if (null!=wikins){
					if (null!=wikins[0]){
						map_res_wikins.put(stmt.getSubject(), wikins[0]);
					}
						
					if (null!=wikins[1]){
						if (stmt.getObject().isResource()){
							Resource resObj= (Resource)stmt.getObject();
							if (!XSD.getURI().equals(resObj.getNameSpace()))
								map_res_wikins.put((Resource)(stmt.getObject()), wikins[1]);
						}
							
					}
				}
				
			}
		}
					
		//initialize direct description
		{
			StmtIterator iter = m.listStatements();
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement();
				if (stmt.getSubject().isAnon()){
					if (stmt.getObject().isAnon()){
						System.out.println(stmt);
						map_anon_anon.put((Resource)(stmt.getObject()), stmt.getSubject());
					}
					      
					ArrayList<Statement> stmts= map_anon_x_stmts.get(stmt.getSubject());
					if (null==stmts){
						stmts = new ArrayList<Statement>();
					}
					stmts.add(stmt);
					map_anon_x_stmts.put(stmt.getSubject(), stmts);
					
				}else{
					String sub= toWiki(m,stmt.getSubject(),map_res_wikins);
					TreeSet<String> content = data1.get(sub);
					DataQname dq = ToolJena.getDataQname(stmt.getSubject());
						
					if (null==content){
						content= new TreeSet<String>();
						data1.put(sub, content);
						content.add( toWiki("member of", szOntologyNamespaceUrl, false));
						
						if (null!=dq && dq.hasPrefix() && dq.getPrefix().equals(szPrefix) ){
							content.add( toWiki("imported from", dq.getPrefix()+":"+dq.getLocalname(), false));
							content.add( String.format("\n* see aslo [[%s]]",szSMW_import_page));
						}						

					}
					if (stmt.getObject().isAnon()){
						map_anon_res.put((Resource)(stmt.getObject()), stmt.getSubject());

						ArrayList<Statement> stmts= map_res_anon_stmts.get(stmt.getSubject());
						if (null==stmts){
							stmts = new ArrayList<Statement>();
						}
						stmts.add(stmt);
						map_res_anon_stmts.put(stmt.getSubject(), stmts);
					}else{
						content.add( toWiki(m, stmt,map_res_wikins));
						
						if (stmt.getObject().isURIResource()){
							Resource resObj =(Resource)stmt.getObject();
							

							HashSet<String> setMetaURI = new HashSet<String>();
							setMetaURI.add(OWL.getURI());
							setMetaURI.add(RDFS.getURI());
							setMetaURI.add(RDF.getURI());

							if (stmt.getPredicate().equals(RDF.type)){
								
								if (setMetaURI.contains(resObj.getNameSpace())){
									content.add(toWiki("tag", toWiki(m, resObj, map_res_wikins), false));
								}else{
									content.add(toWiki( null, toWiki(m, resObj, map_res_wikins), false));
								}
							}
							
							if (stmt.getPredicate().equals(RDFS.subClassOf)){
								content.add(toWiki( null, toWiki(m, resObj, map_res_wikins), false));
							}

							String szTypeString = null;
							HashMap<Resource,String> map_range_wikitype = new HashMap<Resource,String>();
							map_range_wikitype.put(XSD.anyURI, "URL");
							map_range_wikitype.put(RDFS.Literal, "Text");
							map_range_wikitype.put(XSD.xstring, "Text");
							map_range_wikitype.put(XSD.dateTime, "Date");
							map_range_wikitype.put(XSD.negativeInteger, "Number");
							map_range_wikitype.put(XSD.integer, "Number");
							map_range_wikitype.put(XSD.nonNegativeInteger, "Number");
							if (stmt.getPredicate().equals(RDFS.range)){
								String wikitype = map_range_wikitype.get(resObj);
								if (null!=wikitype){
									szTypeString = "Type:"+wikitype;
									content.add( toWiki("has type", szTypeString, false));

									data_import.put(sub, String.format("%s|%s", dq.getLocalname(),szTypeString));
								}
								
							}

							if ( !data_import.containsKey(sub) && !ToolSafe.isEmpty(dq.getLocalname())){
								String wikins = map_res_wikins.get(stmt.getSubject());
								if ("Category:".equals(wikins)){
									szTypeString = "Category";	
								}else{
									szTypeString = "Type:Page";	
								}
								data_import_default.put(sub, String.format("%s|%s", dq.getLocalname(),szTypeString));
							}

						}

						
					}
					
				}
			}
		}
		
		//clean up map_anon_anon, and map_anon_res
		{
			Iterator<Map.Entry<Resource,Resource>> iter= map_anon_anon.entrySet().iterator();
			while (iter.hasNext()){
				Map.Entry<Resource,Resource> entry = iter.next();
				if (map_anon_res.keySet().contains(entry.getKey()))
						continue;
				Resource temp= entry.getValue();
				while (temp.isAnon()){
					
					if (null!=map_anon_res.get(temp)){
						temp=map_anon_res.get(temp);
						map_anon_res.put(entry.getKey(),temp);
						break;
					}else {
						temp=map_anon_anon.get(temp);
					}
				}
			}
		}
		

		
		//create data_2
		{
			HashSet<Resource> resources = new HashSet<Resource>(map_anon_res.values());
			Iterator<Resource> iter= resources.iterator();
			while (iter.hasNext()){
				Resource res = iter.next();

				String sub =toWiki(m, res,map_res_wikins); 
				TreeSet<String> content = data2.get(sub);
				if (null==content){
					content= new TreeSet<String>();
					data2.put(sub, content);
				}

				ArrayList<Statement> stmts =map_res_anon_stmts.get(res);

				if (null!=stmts){
					Iterator<Statement> iter_stmt = stmts.iterator();
					while (iter_stmt.hasNext()){
						Statement stmt = iter_stmt.next();

						content.add(toWiki(m, stmt,map_res_wikins));
					}
				}

			}
		}			
		
		//create data_3
		{
			Iterator<Map.Entry<Resource,Resource>> iter= map_anon_res.entrySet().iterator();
			while (iter.hasNext()){
				Map.Entry<Resource,Resource> entry = iter.next();

				String sub =toWiki(m, entry.getValue(),map_res_wikins); 
				TreeSet<String> content = data3.get(sub);
				if (null==content){
					content= new TreeSet<String>();
					data2.put(sub, content);
				}

				ArrayList<Statement> stmts =map_anon_x_stmts.get(entry.getKey());
				
				if (null!=stmts){
					String temp = String.format("\n===%s===",toWiki(m, entry.getKey(),map_res_wikins));
					Iterator<Statement> iter_stmt = stmts.iterator();
					while (iter_stmt.hasNext()){
						Statement stmt = iter_stmt.next();

						temp += toWiki(m, stmt, map_res_wikins);
					}
					content.add(temp);
				}

			}
		}

		//merge all data 
		{
			Iterator <String> iter = data1.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				
				String content = "";

				TreeSet<String> temp;
				
				temp = data1.get(key);
				if (null!=temp && !temp.isEmpty()){
					content += "==direct description==";
					Iterator<String> iter_content = temp.iterator();
					while (iter_content.hasNext()){
						content += iter_content.next();
					}
				}

				temp = data2.get(key);
				if (null!=temp && !temp.isEmpty()){
					content += "\n==more description==";
					Iterator<String> iter_content = temp.iterator();
					while (iter_content.hasNext()){
						content += iter_content.next();
					}
				}

				temp = data3.get(key);
				if (null!=temp && !temp.isEmpty()){
					Iterator<String> iter_content = temp.iterator();
					while (iter_content.hasNext()){
						content += iter_content.next();
					}
				}


				data.put(key,content);
			}
		}
		
		// add MediaWiki:SMW_import_xx  page
		// http://xmlns.com/foaf/0.1/|[http://www.foaf-project.org/ Friend Of A Friend]
		{
			String szTemp = String.format("%s|[%s %s]", szOntologyNamespace, szOntologyNamespaceUrl, szPrefix);
			data_import_default.putAll(data_import);
			Iterator<String> iter = data_import_default.values().iterator();
			while (iter.hasNext()){
				szTemp += String.format("\n %s", iter.next());
			}
			
			data.put(szSMW_import_page, szTemp);
		}
		

		return data;
	}
	


	private static String toWiki(String predicate, String object, boolean bOriginal){
		if (null==predicate || predicate.isEmpty()){
			return String.format("\n* %s [[:%s]][[%s]]", bOriginal?"[original]":"[<font color='red'>inferred</font>]", object, object);			
		}
		
		return String.format("\n* %s [[Property:%s|%s]]: [[%s::%s]]", bOriginal?"[original]":"[<font color='red'>inferred</font>]", predicate, predicate, predicate, object);
	}

	private static String toWiki(Model m, Statement stmt, HashMap<Resource, String> map_res_wikins){
		//String sub= toWiki(m,stmt.getSubject(),map_res_wikins);
		String pred= toWiki(m,stmt.getPredicate(), null);
		String obj= toWiki(m,stmt.getObject(),map_res_wikins);

		if (stmt.getObject().isAnon()){
			return String.format("\n* %s [[Property:%s|%s]] [[#%s]]", "[original]", pred, pred, obj);
		}else if (stmt.getSubject().isAnon()){
			if (stmt.getObject().isLiteral())
				return String.format("\n* %s [[Property:%s|%s]] %s", "[original]",  pred, pred, obj);
			else
				return String.format("\n* %s [[Property:%s|%s]] [[:%s]]", "[original]",  pred, pred, obj);
		}else{
			return toWiki(pred, obj, true);				
		}
	}
	
	
	private static String toWiki(Model m, RDFNode node, HashMap<Resource, String> map_res_wikins){
		if (node.isURIResource()){
			
			Resource res = (Resource) node;
			String szRes=res.getURI();
			//Iterator<Map.Entry<String, String>> iter = m.getNsPrefixMap().entrySet().iterator();
			//String szNs="";
			String szPrefix=null;
			String szLn =null;
			//while (iter.hasNext()){
			//	Map.Entry<String, String> entry =iter.next();
			//	if (szRes.startsWith(entry.getValue())&& !entry.getKey().isEmpty()){
			//		szNs = entry.getValue();
			//		szPrefix= entry.getKey();
			//		szLn = szRes.substring(szNs.length());
			//		break;
			//	}
			//}
			
			DataQname dq = ToolJena.getDataQname(node);
			if (null!=dq){
				szPrefix= dq.getPrefix();
				szLn =dq.getLocalname();
			}
			//if (szLn.isEmpty()){
			//	szLn =szRes;
			//}
			
			String wikins="";
			if (null!=map_res_wikins){
				wikins= map_res_wikins.get(res);
				if (node instanceof Property){
					wikins="Property:";
				}else if (null==wikins){
					wikins="";
				}
			}
			if (null!=szPrefix && !szLn.isEmpty())
				return String.format("%s%s:%s", wikins, szPrefix, szLn);
			else
				return String.format("%s%s", wikins, szRes);

		}else if (node.isLiteral()){
			return ((Literal)node).getString().replaceAll("\n", " ");
		}else {
			return node.toString();
		}
	}
	
	
}

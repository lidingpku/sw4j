package sw4j.example.datagov;

import java.util.HashMap;
import sw4j.servlet.vocabulary.DGTWC;
import sw4j.task.util.AgentModelManager;
import sw4j.util.Sw4jException;
import sw4j.util.ToolHash;
import sw4j.util.ToolSafe;
import sw4j.util.rdf.ToolJena;
import sw4j.util.web.ToolMediaWiki;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class TaskCatalogRdf2WikiDump {
	public static void main(String[] args) {
		/*
		String [][] aryInputOutput = new String [][]{
				{"http://data-gov.tw.rpi.edu/raw/92/data.rdf","files/rdf2wikidump/92.xml",DGTWC.DataEntry.getURI(), "i.data_gov_catalog_entry"}
		};
		for (int i=0; i<aryInputOutput.length; i++){

			String sz_url_input = aryInputOutput[i][0];
			String  sz_file_output = aryInputOutput[i][1];
			String sz_type = aryInputOutput[i][2];
			String sz_template_name = aryInputOutput[i][3];
			run(sz_url_input, sz_file_output, sz_type, sz_template_name);
		}*/
		run_catalog();
	}

	public static void run_catalog(){
		String sz_url_input = "http://data-gov.tw.rpi.edu/raw/92/data.rdf";
		String  sz_file_output1 = "files/rdf2wikidump/92prop.xml";
		String  sz_file_output2 = "files/rdf2wikidump/92.xml";
		String sz_type = DGTWC.DataEntry.getURI();
		String sz_template_name1 =  "Asserted Metadata";
		String sz_template_name2 =  "Additional Metadata";

		HashMap<String,String> map_pages1 = new HashMap<String,String> ();
		HashMap<String,String> map_pages2 = new HashMap<String,String> ();

		//load RDF
		Model m;
		try {
			m = AgentModelManager.get().loadModel(sz_url_input);
			if (null== m)
				return;

			Model m_metadata = ModelFactory.createDefaultModel();
			HashMap<Resource,String> map_uri_id = new HashMap<Resource,String> ();
			//extract entry id
			{
				Property p = m.createProperty("http://data-gov.tw.rpi.edu/vocab/p/92/url");
				StmtIterator iter = m.listStatements(null, p, (String)null);
				while (iter.hasNext()){
					Statement stmt = iter.nextStatement();
					Resource subject = stmt.getSubject();
					
					String object = ToolJena.getNodeString(stmt.getObject());
					String id = object.substring(object.lastIndexOf("/")+1);
					
					map_uri_id.put(subject,id);
					
					m_metadata.add(m_metadata.createStatement(subject, DC.identifier, id));
					m_metadata.add(m_metadata.createStatement(subject, RDFS.seeAlso, "http://data-gov.tw.rpi.edu/raw/"+id+"/index.rdf"));
					m_metadata.add(m_metadata.createStatement(subject, DC.source, subject));
				}
			}		
			ToolJena.model_copyNsPrefix(m_metadata,m);
			System.out.println(m.getNsPrefixMap());
			
			//process access point
			{
				StmtIterator iter = m.listStatements(null, null, (String)null);
				while (iter.hasNext()){
					Statement stmt = iter.nextStatement();
					Resource subject = stmt.getSubject();
					String sz_predicate = stmt.getPredicate().getURI();
					String sz_object = ToolJena.getNodeString(stmt.getObject());
					String object_page = "URLSHA1/"+ ToolHash.hash_sum_sha1(sz_object.getBytes());
					
					if (sz_predicate.endsWith("_access_point")){
						String title_datafile=object_page;

						m_metadata.add(m_metadata.createStatement(subject, DC.relation, title_datafile));
						
						
						String sz_format = sz_predicate.substring(0,sz_predicate.indexOf("_access_point"));

						String sz_content = String.format("*[[dc:relation::Dataset_%s]]\n*[[dc:format::%s]]\n[[Category:foaf:Document]]", map_uri_id.get(subject), sz_format);

						map_pages2.put(title_datafile, sz_content);

					}
					
				}				
			}
			
			//process properties 
			{
				StmtIterator iter = m.listStatements(null, null, (String)null);
				while (iter.hasNext()){
					Statement stmt = iter.nextStatement();
				//	Resource subject = stmt.getSubject();
					
					String sz_content="";
					if (stmt.getObject().isURIResource()){
						sz_content  += String.format("*[[has type::Type:URL]]\n*[[rdf:type::owl:DatatypeProperty]]\n*[[dc:relation::Dataset_92]]");
					}else{
						sz_content  += String.format("*[[has type::Type:Text]]\n*[[rdf:type::owl:DatatypeProperty]]\n*[[dc:relation::Dataset_92]]");						
					}
					
					String title_property=String.format("Property:92/%s", stmt.getPredicate().getLocalName());
					map_pages1.put(title_property, sz_content);

				}				
			}		
			
			/*
			//for template
			TreeSet<String> values =new TreeSet<String>();
			{
				StmtIterator iter = m.listStatements(null, null, (String)null);
				while (iter.hasNext()){
					Statement stmt = iter.nextStatement();
					
					if (stmt.getPredicate().getNameSpace().endsWith("92/")){
						String ln = stmt.getPredicate().getLocalName();
						String prefix = "92/";
						String value;
						value = String.format("-->{{#if: {{{%s|}}} |<span></span>\n* %s : [[%s%s::{{{%s}}}]]\n}}<!--", ln,ln,prefix, ln,ln);
						values.add(value);
					}
				}				
			}	
			System.out.println(sz_template_name1);
			System.out.println("<!--"+ToolString.printCollectionToString(values)+"-->");
			
			//for template
			values =new TreeSet<String>();
			{
				StmtIterator iter = m_metadata.listStatements(null, null, (String)null);
				while (iter.hasNext()){
					Statement stmt = iter.nextStatement();
					
					String prefix = ToolJena.getNodePrefix(stmt.getPredicate());
					String ln = stmt.getPredicate().getLocalName();
					String value = null;
					if (!ToolSafe.isEmpty(prefix)){
						prefix +=":";
						value = String.format("-->{{#if: {{{%s|}}} |<span></span>\n* %s : [[%s%s::{{{%s}}}]]\n}}<!--", ln,ln,prefix, ln,ln);
					}else
						value = stmt.getPredicate().getURI();
					
					values.add(value);
				}				
			}	
			System.out.println(sz_template_name2);
			System.out.println("<!--"+ToolString.printCollectionToString(values)+"-->");
			*/
			
			Resource type = m.createResource(sz_type);
			//convert RDF data to a map of (title, content)
			ResIterator iter = m.listSubjectsWithProperty(RDF.type,type);
			while (iter.hasNext()){
				Resource subject = iter.nextResource();
				String id = map_uri_id.get(subject);
				
				String title_dataset =  String.format("Dataset_%s", id); //subject.getURI(); //String.format("%s_%s", sz_template_name , subject.getLocalName());

				String content1 =  dump_instance_to_template(m, subject, sz_template_name1);
				String content2 =  dump_instance_to_template(m_metadata, subject, sz_template_name2);
				map_pages2.put(title_dataset, "<![CDATA["+content1+content2+"[[Category:dgtwc:Dataset]]]]>");

			}
			
			
			//save the map to wikidump
			System.out.println(map_pages1.size() +" page generated!");
			//System.out.println(map_pages);
			ToolMediaWiki.create_wiki_dump( map_pages1, sz_file_output1);				

			System.out.println(map_pages2.size() +" page generated!");
			//System.out.println(map_pages);
			ToolMediaWiki.create_wiki_dump( map_pages2, sz_file_output2);				

			//System.out.println(ToolString.printCollectionToString(map_pages2.keySet()));

			
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		
	}
/*	
	public static void run(String sz_url_input, String sz_file_output, String sz_type, String sz_template_name){
		//load RDF
		Model m;
		try {
			m = AgentModelManager.get().loadModel(sz_url_input);
			if (null== m)
				return;

			HashMap<String,String> map_pages = new HashMap<String,String> ();
			
			Resource type = m.createResource(sz_type);
			//convert RDF data to a map of (title, content)
			ResIterator iter = m.listSubjectsWithProperty(RDF.type,type);
			while (iter.hasNext()){
				Resource subject = iter.nextResource();
				String title = subject.getURI(); //String.format("%s_%s", sz_template_name , subject.getLocalName());

				String content =  dump_instance_to_template(m, subject, sz_template_name);
				map_pages.put(title, content);

			}
			
			//save the map to wikidump
			System.out.println(map_pages.size() +" page generated!");
			//System.out.println(map_pages);
			ToolMediaWiki.create_wiki_dump( map_pages, sz_file_output);				

		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		
	}
*/	

	private static String dump_instance_to_template(Model m, Resource subject,	String sz_template_name) {
		String sz_content ="";
		StmtIterator iter = m.listStatements(subject, null, (String)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			String value = ToolJena.getNodeString(stmt.getObject());

			/*
			if (stmt.getObject().isURIResource()){
				String name = ((Resource)(stmt.getObject())).getLocalName();
				if (null!=name && name.length()>0)
					value =name;
			}*/
			if (stmt.getPredicate().equals(RDF.type))
				continue;
			
			
			// remove markups
			value = value.replaceAll("<[^>]+>", "");
			value = value.replaceAll("\\s+", " ");
			
			//escape data
			//value = ToolWeb.escapeHTML(value);

			String prefix = ToolJena.getNodePrefix(stmt.getPredicate());
			String ln = stmt.getPredicate().getLocalName();
			String ns = stmt.getPredicate().getNameSpace();

			
			if (!ToolSafe.isEmpty(prefix)){
				prefix +=":";
				sz_content +=String.format("* [[Property:%s%s|%s]]: [[%s%s::%s]]\n", prefix,ln, ln, prefix, ln, value);
			}else
				if (ns.startsWith("http://data-gov.tw.rpi.edu/vocab/p/")){
					prefix =ns.substring( "http://data-gov.tw.rpi.edu/vocab/p/".length() );
					sz_content +=String.format("* [[Property:%s%s|%s]]: [[%s%s::%s]]\n", prefix,ln, ln, prefix, ln, value);
				}else
					sz_content +=String.format("* %s: %s\n", stmt.getPredicate().getURI(), value);

			
			
		}
		return String.format("\n==%s==\n%s\n", sz_template_name, sz_content);
	}
}

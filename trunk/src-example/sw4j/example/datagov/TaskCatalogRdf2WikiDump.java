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
		String  sz_file_output_prop = "files/rdf2wikidump/92prop.xml";
		String  sz_file_output_bot = "files/rdf2wikidump/92bot.xml";
		String  sz_file_output_man = "files/rdf2wikidump/92man.xml";
		String sz_type = DGTWC.DataEntry.getURI();

		HashMap<String,String> map_pages_property = new HashMap<String,String> ();
		HashMap<String,String> map_pages_bot = new HashMap<String,String> ();
		HashMap<String,String> map_pages_man = new HashMap<String,String> ();

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
					String sz_object = ToolJena.getNodeString(stmt.getObject());
					String object_page = "URLSHA1/"+ ToolHash.hash_sum_sha1(sz_object.getBytes());
					
					String sz_predicate_localname = stmt.getPredicate().getLocalName();

					if (sz_predicate_localname.endsWith("_access_point")){
						String title_datafile=object_page;

						m_metadata.add(m_metadata.createStatement(subject, DC.relation, title_datafile));
						
						String sz_format = sz_predicate_localname.substring(0,sz_predicate_localname.indexOf("_access_point"));

						String sz_content = String.format(
								"<!---->{{#set:dc:relation=Dataset_%s}}<!--\n" +
								"-->{{#set:rdfs:seeAlso=%s}}<!--\n" +
								"-->{{#set:dc:format=%s}}<!--\n-->\n[[Category:dgtwc:Datafile]]",
								map_uri_id.get(subject),
								sz_object,
								sz_format);

						save_page(map_pages_bot,map_pages_man, title_datafile, sz_content);
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
						sz_content  += String.format("*[[has type::Type:URL]]\n*[[rdf:type::Category:owl:DatatypeProperty]]\n*[[dc:relation::Dataset_92]]");
					}else{
						sz_content  += String.format("*[[has type::Type:Text]]\n*[[rdf:type::Category:owl:DatatypeProperty]]\n*[[dc:relation::Dataset_92]]");						
					}
					
					String title_property=String.format("Property:92/%s", stmt.getPredicate().getLocalName());
					String temp = map_pages_property.get(title_property);
					if (null==temp || temp.indexOf("URL")>0)
						map_pages_property.put(title_property, sz_content);

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

				String content1 =  dump_instance_to_wiki(m, subject);
				String content2 =  dump_instance_to_wiki(m_metadata, subject);
				

				save_page(map_pages_bot,map_pages_man, title_dataset, content1+content2+"[[Category:dgtwc:Dataset]]");

			}
			
			
			//save the map to wikidump
			System.out.println(map_pages_property.size() +" page generated!");
			//System.out.println(map_pages);
			ToolMediaWiki.create_wiki_dump( map_pages_property, true, sz_file_output_prop);				

			System.out.println(map_pages_bot.size() +" page generated!");
			//System.out.println(map_pages);
			ToolMediaWiki.create_wiki_dump( map_pages_bot, sz_file_output_bot);				

			System.out.println(map_pages_man.size() +" page generated!");
			//System.out.println(map_pages);
			ToolMediaWiki.create_wiki_dump( map_pages_man, true, sz_file_output_man);				
			//System.out.println(ToolString.printCollectionToString(map_pages_bot.keySet()));

			
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

	private static void save_page(HashMap<String, String> map_pages_bot,
			HashMap<String, String> map_pages_man,
			String title, String content) {
		String title_bot= "BOT:"+title;
		map_pages_bot.put(title_bot, "<![CDATA[<noinclude>This is an automatically generated page, DO NOT modify this page.</noinclude><includeonly>"+content+"</includeonly>]]>");
		map_pages_man.put(title, String.format("{{:%s}}", title_bot));
	}

	private static String dump_instance_to_wiki(Model m, Resource subject) {
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
			
			
			// remove markups and â??
			value = value.replaceAll("<[^>]+>", "");
			value = value.replaceAll("\\s+", " ");
			value = value.replaceAll("â", " ");
			
			//escape data
			//value = ToolWeb.escapeHTML(value);

			String prefix = ToolJena.getNodePrefix(stmt.getPredicate());
			String ln = stmt.getPredicate().getLocalName();
			String ns = stmt.getPredicate().getNameSpace();

			
			if (!ToolSafe.isEmpty(prefix)){
				prefix +=":";
				sz_content +=String.format("-->{{#set:%s%s=%s}}<!--\n", prefix, ln, value);
			}else
				if (ns.startsWith("http://data-gov.tw.rpi.edu/vocab/p/")){
					prefix =ns.substring( "http://data-gov.tw.rpi.edu/vocab/p/".length() );
					sz_content +=String.format("-->{{#set:%s%s=%s}}<!--\n", prefix, ln, value);
				}else
					sz_content +=String.format("* %s: %s\n", stmt.getPredicate().getURI(), value);

			
			
		}
		return String.format("<!--%s-->", sz_content);
	}
	
}

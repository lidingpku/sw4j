package sw4j.example.datagov;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import sw4j.servlet.vocabulary.DGTWC;
import sw4j.task.load.ToolLoadHttp;
import sw4j.task.util.AgentModelManager;
import sw4j.util.Sw4jException;
import sw4j.util.rdf.ToolJena;
import sw4j.util.web.ToolMediaWiki;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class TaskIndexRdf2WikiDump {
	public static void main(String[] args) {
		run();
	}
	
	public static void run(){
		String  sz_url_base ="http://data-gov.tw.rpi.edu/raw/";
		String  sz_file_output_bot = "files/rdf2wikidump/92bot_index.xml";
		HashMap<String,String> map_pages_bot = new HashMap<String,String> ();
		
		//get index file urls
		Set<String> dirs = ToolLoadHttp.listWebDirItems(sz_url_base, true, false);
		Iterator<String> iter = dirs.iterator();
		while (iter.hasNext()){
			String item  =iter.next();
			int id_dataset = Integer.parseInt(item);
			String sz_url_input = String.format("%s%d/index.rdf",sz_url_base,id_dataset);
			
			String title = String.format("BOT:Dataset_%d_index",id_dataset);
			String content = getPageContent(sz_url_input,id_dataset);
			
			map_pages_bot.put(title, content);
			
		//	System.out.println(content);
		}
		
		System.out.println(map_pages_bot.size() +" page generated!");
		//System.out.println(map_pages);
		ToolMediaWiki.create_wiki_dump( map_pages_bot, sz_file_output_bot);				

	}

	
	
	public static String getPageContent(String sz_url_input, int id){
		String sz_content ="";

		//load RDF
		Model m;
		try {
			m = AgentModelManager.get().loadModel(sz_url_input);
			if (null== m)
				return sz_content;

			sz_content += String.format("<![CDATA[<noinclude>This is an automatically generated page for [[Dataset_%d]], DO NOT modify this page.</noinclude><includeonly>[[Category:Converted Dataset]]<!--\n", id);
			// list special annotations
			Property p = DGTWC.number_of_entries;
			sz_content += TaskCatalogRdf2WikiDump.to_hidden_wiki(
					DGTWC.number_of_entries_qname,
					ToolJena.getValueOfProperty(m, null, p, "0") );
			
			/*
			p = DGTWC.number_of_bytes;
			sz_content += String.format("-->{{#set:%s=%s}}<!--\n", 
					DGTWC.number_of_bytes_qname,
					ToolJena.getValueOfProperty(m, null, p, "0") );
			*/
			
			p = DGTWC.number_of_properties;
			sz_content += TaskCatalogRdf2WikiDump.to_hidden_wiki(
					DGTWC.number_of_properties_qname,
					ToolJena.getValueOfProperty(m, null, p, "0") );
			
			p = DGTWC.number_of_triples;
			sz_content += TaskCatalogRdf2WikiDump.to_hidden_wiki(
					DGTWC.number_of_triples_qname,
					ToolJena.getValueOfProperty(m, null, p, "0") );

			{
				p = DGTWC.complete_data;
				NodeIterator iter = m.listObjectsOfProperty(p);
				while (iter.hasNext()){
					RDFNode node = iter.nextNode();

					sz_content += TaskCatalogRdf2WikiDump.to_hidden_wiki(
							DGTWC.complete_data_qname,
							ToolJena.getNodeString(node));
				}
			}

			{
				p = DGTWC.link_data;
				NodeIterator iter = m.listObjectsOfProperty(p);
				while (iter.hasNext()){
					RDFNode node = iter.nextNode();

					sz_content += TaskCatalogRdf2WikiDump.to_hidden_wiki(
							DGTWC.link_data_qname,
							ToolJena.getNodeString(node));
				}
			}
			// list properties
			{
				sz_content += "\n\nproperties\n";
				StmtIterator iter = m.listStatements(null, RDF.type, RDF.Property);
				while (iter.hasNext()){
					Statement stmt = iter.nextStatement();
					Resource subject = stmt.getSubject();

					sz_content += TaskCatalogRdf2WikiDump.to_hidden_wiki("usesProperty",
							String.format("Property:%d/%s", id, subject.getLocalName() ));
				}				
			}		
			sz_content += "--></includeonly>]]>";


			return sz_content;
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
	}

}

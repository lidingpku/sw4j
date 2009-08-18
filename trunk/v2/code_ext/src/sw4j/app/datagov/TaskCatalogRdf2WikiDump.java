package sw4j.app.datagov;

import java.util.HashMap;

import sw4j.rdf.util.ToolJena;
import sw4j.util.ToolHash;
import sw4j.util.ToolSafe;
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
		run_catalog();
	}

	
	
	public static void run_catalog(){
		String sz_url_input = "http://data-gov.tw.rpi.edu/raw/92/data-92.rdf";

		//load RDF
		Model m = ModelFactory.createDefaultModel();
		m.read(sz_url_input);
		if (m.size()==0)
			return;
		
		save_property(m);
		save_entries(m);
		save_states();
	}
		
	public static void save_states(){
		String  sz_file_output_prop = "files/rdf2wikidump/states.xml";
		HashMap<String,String> map_pages_states= new HashMap<String,String> ();

		for (int i=0; i< aryStateAbbrev.length; i++){
			String sz_content = String.format("[[rdfs:seeAlso::http://en.wikipedia.org/wiki/%s]][[Category:%s]]",  aryStateAbbrev[i][0], aryStateAbbrev[i][2]);
			String sz_title = String.format("%s", aryStateAbbrev[i][0]);
			map_pages_states.put(sz_title, sz_content);

			sz_content = String.format("#REDIRECT [[%s]]", aryStateAbbrev[i][0]);
			sz_title = String.format("%s", aryStateAbbrev[i][1]);
			map_pages_states.put(sz_title, sz_content);
		}

		//save the map to wikidump
		System.out.println(map_pages_states.size() +" page generated!");
		//System.out.println(map_pages);
		ToolMediaWiki.create_wiki_dump( map_pages_states, false, sz_file_output_prop);				
		
	}
	public static void save_property(Model m){
		String  sz_file_output_prop = "files/rdf2wikidump/92prop.xml";
		HashMap<String,String> map_pages_property = new HashMap<String,String> ();

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
					sz_content  += String.format("*[[has type::Type:String]]\n*[[rdf:type::Category:owl:DatatypeProperty]]\n*[[dc:relation::Dataset_92]]");						
				}
				
				String title_property=String.format("Property:92/%s", stmt.getPredicate().getLocalName());
				String temp = map_pages_property.get(title_property);
				if (null==temp || temp.indexOf("URL")>0)
					map_pages_property.put(title_property, sz_content);

			}				
		}	

		//save the map to wikidump
		System.out.println(map_pages_property.size() +" page generated!");
		//System.out.println(map_pages);
		ToolMediaWiki.create_wiki_dump( map_pages_property, true, sz_file_output_prop);				

	}
		
	public static void save_entries(Model m){
		String  sz_file_output_bot = "files/rdf2wikidump/92bot.xml";
		String  sz_file_output_man = "files/rdf2wikidump/92man.xml";
		String sz_type = DGTWC.DataEntry.getURI();

		HashMap<String,String> map_pages_bot = new HashMap<String,String> ();
		HashMap<String,String> map_pages_man = new HashMap<String,String> ();

		
		//extract metadata to be posted on wiki
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

					String sz_content = "<!--\n";
					sz_content +=to_hidden_wiki("dc:relation","Dataset_"+map_uri_id.get(subject));
					sz_content +=to_hidden_wiki("rdfs:seeAlso",sz_object);
					sz_content +=to_hidden_wiki("dc:format",sz_format);
					sz_content += "-->[[Category:dgtwc:Datafile]]";
					
					save_page(map_pages_bot,map_pages_man, title_datafile, sz_content);
				}
				
			}				
		}
		
		
		Resource type = m.createResource(sz_type);
		//convert RDF data to a map of (title, content)
		ResIterator iter = m.listSubjectsWithProperty(RDF.type,type);
		while (iter.hasNext()){
			Resource subject = iter.nextResource();
			String id = map_uri_id.get(subject);
			
			String title_dataset =  String.format("Dataset_%s", id); //subject.getURI(); //String.format("%s_%s", sz_template_name , subject.getLocalName());

			String content1 =  dump_instance_to_wiki(m, subject);
			String content2 =  dump_instance_to_wiki(m_metadata, subject);
			

			save_page(
					map_pages_bot,
					map_pages_man, 
					title_dataset, 
					String.format("<!--\n-->%s%s<!--\n-->[[Category:dgtwc:Dataset]]<!--\n-->{{#ifexist:BOT:%s_index|{{BOT:%s_index}}}}<!--\n-->{{#ifexist:BOT:Dataset_at_data.gov|{{BOT:Dataset_at_data.gov}}}}<!--\n-->", content1,content2,title_dataset,title_dataset));

		}
		
		

		System.out.println(map_pages_bot.size() +" page generated!");
		//System.out.println(map_pages);
		ToolMediaWiki.create_wiki_dump( map_pages_bot, sz_file_output_bot);				

		System.out.println(map_pages_man.size() +" page generated!");
		//System.out.println(map_pages);
		ToolMediaWiki.create_wiki_dump( map_pages_man, true, sz_file_output_man);				
		//System.out.println(ToolString.printCollectionToString(map_pages_bot.keySet()));
	}


	private static void save_page(HashMap<String, String> map_pages_bot,
			HashMap<String, String> map_pages_man,
			String title, String content) {
		String title_bot= "BOT:"+title;
		String content_bot = String.format("<![CDATA[<noinclude>This is an automatically generated page for [[%s]], DO NOT modify this page.</noinclude><includeonly>%s</includeonly>]]>", title, content);
		map_pages_bot.put(title_bot, content_bot);
		
		map_pages_man.put(title, String.format("{{:%s}}", title_bot));
	}

	private static String dump_instance_to_wiki(Model m, Resource subject) {
		String sz_content ="";
		StmtIterator iter = m.listStatements(subject, null, (String)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			String value = ToolJena.getNodeString(stmt.getObject());
			String property = stmt.getPredicate().getURI();

			/*
			if (stmt.getObject().isURIResource()){
				String name = ((Resource)(stmt.getObject())).getLocalName();
				if (null!=name && name.length()>0)
					value =name;
			}*/
			if (stmt.getPredicate().equals(RDF.type))
				continue;
			
			
			// remove markups and �??
			value = value.replaceAll("<[^>]+>", "");
			value = value.replaceAll("\\s+", " ");
			
			//////////////////////////////////////////////
			//////////////////////////////////////////////
			//////////////////////////////////////////////
			//special treatments
			value = value.replaceAll("�", " ");

			if ( (property.indexOf("title")>0 && value.indexOf("Toxics Release Inventory data for ")>0)
					||(property.indexOf("title")>0 && value.indexOf("Toxics Release Inventory for ")>0)){
				sz_content += "-->[[Category:Skipped Subset Dataset]]<!--\n";
				
				sz_content +=String.format("-->%s<!--", get_EPA_annotation(value));
			}

			
			//////////////////////////////////////////////
			
			//escape data
			//value = ToolWeb.escapeHTML(value);

			String prefix = ToolJena.getNodePrefix(stmt.getPredicate());
			String ln = stmt.getPredicate().getLocalName();
			String ns = stmt.getPredicate().getNameSpace();

			
			if (!ToolSafe.isEmpty(prefix)){
				prefix +=":";
				sz_content += to_hidden_wiki(prefix+ln, value);
			}else
				if (ns.startsWith("http://data-gov.tw.rpi.edu/vocab/p/")){
					prefix =ns.substring( "http://data-gov.tw.rpi.edu/vocab/p/".length() );
					sz_content += to_hidden_wiki(prefix+ln, value);
				}else
					sz_content +=String.format("* %s: %s\n", stmt.getPredicate().getURI(), value);

			
			
		}
		return String.format("<!--%s-->", sz_content);
	}
	
	static String to_hidden_wiki(String property, String value){
		//return String.format("-->{{#set:%s=%s}}<!--\n",property, value);
		return String.format("-->[[%s::%s| ]]<!--\n",property, value);
	}
	
	static String[][] aryStateAbbrev = new String[][]{
		{"Alaska","AK","US State"},
		{"Alabama","AL","US State"},
		{"Arkansas","AR","US State"},
		{"American Samoa","AS","US Territory"},
		{"Arizona","AZ","US State"},
		{"California","CA","US State"},
		{"Colorado","CO","US State"},
		{"Connecticut","CT","US State"},
		{"District of Columbia","DC","US Territory"},
		{"Delaware","DE","US State"},
		{"Florida","FL","US State"},
		{"Georgia","GA","US State"},
		{"Guam","GU","US Territory"},
		{"Hawaii","HI","US State"},
		{"Iowa","IA","US State"},
		{"Idaho","ID","US State"},
		{"Illinois","IL","US State"},
		{"Indiana","IN","US State"},
		{"Kansas","KS","US State"},
		{"Kentucky","KY","US State"},
		{"Louisiana","LA","US State"},
		{"Massachusetts","MA","US State"},
		{"Maryland","MD","US State"},
		{"Maine","ME","US State"},
		{"Michigan","MI","US State"},
		{"Minnesota","MN","US State"},
		{"Missouri","MO","US State"},
		{"Mariana Islands","MP","US Territory"},
		{"Mississippi","MS","US State"},
		{"Montana","MT","US State"},
		{"North Carolina","NC","US State"},
		{"North Dakota","ND","US State"},
		{"Nebraska","NE","US State"},
		{"New Hampshire","NH","US State"},
		{"New Jersey","NJ","US State"},
		{"New Mexico","NM","US State"},
		{"Nevada","NV","US State"},
		{"New York","NY","US State"},
		{"Ohio","OH","US State"},
		{"Oklahoma","OK","US State"},
		{"Oregon","OR","US State"},
		{"Pennsylvania","PA","US State"},
		{"Puero Rico","PR","US Territory"},
		{"Rhode Island","RI","US State"},
		{"South Carolina","SC","US State"},
		{"South Dakota","SD","US State"},
		{"Tennessee","TN","US State"},
		{"Texas","TX","US State"},
		{"Utah","UT","US State"},
		{"Virginia","VA","US State"},
		{"Virgin Islands","VI","US Territory"},
		{"Vermont","VT","US State"},
		{"Washington","WA","US State"},
		{"Wisconsin","WI","US State"},
		{"West Virgina","WV","US State"},
		{"Wyoming","WY","US State"},
	};

	static String get_EPA_annotation(String value){
		String year = value.substring(0,4);
		/*
		 * http://data-gov.tw.rpi.edu/raw/191/EPA_TRI_05YR00001.rdf
		 * 
		 */

		int nSuperDataId = 191;
		if (year.equals("2005")){
			nSuperDataId =191;
		}else if (year.equals("2006")){
			nSuperDataId =249;
		}else if (year.equals("2007")){
			nSuperDataId =307;
		}
		
		String sz_sparql ="";
		String sz_description =""; 
		if (value.indexOf("Federal Facilities")>0){
			sz_description = "This is a dataset is related to [[dgtwc:coverage::Federal Facilities]].";
			sz_sparql = String.format(
					"\nCONSTRUCT {    ?s  ?p  ?o .  }" +
					"\nFROM <http://data-gov.tw.rpi.edu/raw/%d/data-%d.nt> " +
					"\nWHERE {" +
					"\n	?s  ?p  ?o ." +
					"\n   ?s  ?p1  \"YES\"." +
					"\n    FILTER regex(str(?p1), \"federal_facility_ind\") " +
					"\n}",
					nSuperDataId,
					nSuperDataId );
		}else{
			for (int i=0; i<aryStateAbbrev.length; i++){
				String state = aryStateAbbrev[i][0];
				if (value.indexOf(state)>0){
					sz_description = String.format("This is a dataset is related to the US state(or territory) [[%s]].", state);
					sz_sparql=String.format(
							"\nCONSTRUCT {    ?s  ?p  ?o .  }" +
							"\nFROM <http://data-gov.tw.rpi.edu/raw/%d/data-%d.nt.gz> " +
							"\nWHERE {" +
							"\n	?s  ?p  ?o ." +
							"\n   ?s  ?p1 \"NO\"." +
							"\n   ?s  ?p2 \"%s\" ." +
							"\n    FILTER regex(str(?p1), \"federal_facility_ind\") " +
							"\n    FILTER regex(str(?p2), \"facility_state\") " +
							"\n}",
							nSuperDataId,
							nSuperDataId,
							aryStateAbbrev[i][1]);
				}
			}
		}
		
		
		return String.format("This dataset is a subset of [[dgtwc:coverage::Dataset_%d]]. %s" +
				" You may obtain this dataset using the following sparql " +
				"query on the complete data of that dataset. \n<pre>%s</pre>", 
				nSuperDataId,
				sz_description,
				sz_sparql);
	}
}

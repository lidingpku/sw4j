package sw4j.example.datagov;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import sw4j.servlet.vocabulary.DGTWC;
import sw4j.util.DataTable;
import sw4j.util.ToolHash;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;
import sw4j.util.rdf.ToolJena;

public class TaskCatalogCsv2Rdf {
	public static final String VER = "1.0";
	
	public static void main(String[] args) {
		String sz_url_server = "http://data-gov.tw.rpi.edu";
		String sz_url_csv = "http://www.data.gov/data_gov_catalog.csv";
		String sz_id = "92";
		String sz_dir_output = "../data-gov/raw/"+sz_id+"/";
		String sz_url_catalog = "http://data-gov.tw.rpi.edu/raw/"+sz_id+"/";
		
		String sz_url_mapping = "http://data-gov.tw.rpi.edu/map/map00092.csv";
		loadCsv(sz_url_csv, sz_url_mapping, sz_id, sz_url_server,sz_url_catalog,sz_dir_output);
	}
	
	public static void loadCsv(String sz_url_csv, String sz_url_mapping,  String sz_id, String sz_url_server,String sz_url_catalog, String sz_dir_output){
		String szPropertyNamespace = String.format("%s/vocab/p/%s/",sz_url_server,sz_id);			
		String szInstanceNamespace = String.format("%s/vocab/",sz_url_server);
		String szWikiNamespace = String.format("%s/wiki/",sz_url_server);

		String szIndexPath= "index.rdf";
		String szDataPath = "data.rdf";
		String szOutputDataFile = sz_dir_output+szDataPath;
		String szOutputIndexFile = sz_dir_output+szIndexPath;
		String szOutputDataUrl = sz_url_catalog+szDataPath;
		String szOutputIndexUrl = sz_url_catalog+szIndexPath;

		DataTable map =null;
		//if (!ToolSafe.isEmpty(sz_url_mapping))
		//	map = DataTable.fromCSV(sz_url_mapping);
	
		DataTable table = DataTable.fromCSV(sz_url_csv);

		// model index
		Model model_index = ModelFactory.createDefaultModel();
		Resource res_dataset_file = model_index.createResource(szOutputIndexUrl);
		annotateFile(res_dataset_file);
		
		// model data
		Model model_data = ModelFactory.createDefaultModel();
		Resource res_data_file = model_data.createResource(szOutputDataUrl);
		annotateFile(res_data_file);

		// model data
		List<Property> properties = processHeader(table.getHeader(),model_data, szPropertyNamespace);
		int index = 0;
		Iterator<List<String>> iter = table.getValues().iterator();
		while (iter.hasNext()){
			List<String> row = iter.next();
			index++;
			
			Resource instance = model_data.createResource(String.format("%s#entry%05d",szOutputDataUrl, index)); 
			processRow(instance, properties, row, map, szInstanceNamespace);
			
		}
		model_data.setNsPrefix( "dgp"+sz_id, szPropertyNamespace  );
		model_data.setNsPrefix( "dg",szInstanceNamespace);
		model_data.setNsPrefix(DGTWC.class.getSimpleName().toLowerCase(),DGTWC.getURI());
		//ToolJena.printModel(model_data);
		//System.out.println(model_data.getNsPrefixMap());
		ToolJena.printModelToFile(model_data, szOutputDataFile, "RDF/XML",false);
		
		
		// model index
		Resource res_dataset = model_index.createResource(szOutputIndexUrl+"#me");
		res_dataset.addProperty(RDF.type, DGTWC.Dataset);
		res_dataset.addProperty(DC.source, model_index.createResource(sz_url_csv));
		res_dataset.addProperty(DGTWC.complete_data, res_data_file);
		String sz_datafile_hash = "URLSHA1/"+ToolHash.hash_sum_sha1(sz_url_csv.getBytes());
		res_dataset.addProperty(RDFS.isDefinedBy,  model_index.createResource(szInstanceNamespace+sz_datafile_hash) );
		res_dataset.addProperty(RDFS.seeAlso,  model_index.createResource(szWikiNamespace+sz_datafile_hash) );
		res_dataset.addLiteral(DGTWC.number_of_entries, table.getValues().size());
		res_dataset.addLiteral(DGTWC.number_of_properties, table.getHeader().size());
		res_dataset.addLiteral(DGTWC.number_of_triples, model_data.size());

		res_data_file.addProperty(DGTWC.isPartOf, res_dataset);
		
		//model_index.setNsPrefix("dgp",szPropertyNamespace);
		model_index.setNsPrefix(DGTWC.class.getSimpleName().toLowerCase(),DGTWC.getURI());
		//model_index.setNsPrefix( "dg",szInstanceNamespace);
		model_index.setNsPrefix( "dgp"+sz_id, szPropertyNamespace  );
		ToolJena.printModelToFile(model_index, szOutputIndexFile, "RDF/XML",false);
		

	}
	


	public static String processProperty(String szName){
		if (ToolSafe.isEmpty(szName))
			return null;
		String szTemp = szName;
		szTemp = szTemp.replaceAll("\\W+", " ");
		szTemp = szTemp.trim();
		szTemp = szTemp.replaceAll(" ", "_");
		szTemp = szTemp.toLowerCase();
		//szTemp = szTemp.substring(0,1).toUpperCase()+szTemp.substring(1);
		return szTemp;
	}
	
	public static String processValue(String szValue){
		String szTemp = szValue;
		szTemp = szTemp.trim();
		return szTemp;
	}
	
	public static String processResource(String szValue){
		String szTemp = szValue;
		szTemp = szTemp.trim();
		szTemp = szTemp.replaceAll(" ", "_");
		//szTemp = szTemp.substring(0,1).toUpperCase()+szTemp.substring(1);
		return szTemp;
	}
	
	public static List<Property> processHeader(List<String> header, Model m, String szPropertyNamespace){
		List<Property> properties = new ArrayList<Property>();
		Iterator<String> iter_header = header.iterator();
		while (iter_header.hasNext()){
			String szName = iter_header.next();
			szName = processProperty(szName);
			
			properties.add(m.createProperty(szPropertyNamespace+szName));
			
		}
		return properties;
	}
	
	private static void processRow(Resource instance, List<Property> header, List<String> row, DataTable map, String szInstanceNamespace){
		Iterator<Property> iter_header = header.iterator();
		Iterator<String> iter_row = row.iterator();


		/*
//		Iterator<String> iter_map_header = map.getHeader().iterator();
		Iterator<List<String>> iter_map = null;

		 Iterator<String> iter_map_type = null;

		if (!ToolSafe.isEmpty(map)){
			iter_map = map.getValues().iterator();

			iter_map_type = iter_map.next().iterator();
		}
		*/
		Pattern pattern_href = Pattern.compile("^<a +href=\"([^\"]+)\">[\\s]*h?[tf]tps?[^<]+</a>$");
		Pattern pattern_url = Pattern.compile("^h?[tf]tps?://[\\S]+$");

		while (iter_row.hasNext()){
			String row_value = iter_row.next();
			Property p = iter_header.next();
			
			//TODO verify map_header
			//String map_header = iter_map_header.next();


			if (ToolSafe.isEmpty(row_value))
				continue;
			
				/*
			// process mapping file
			String map_type = null;
			if (!ToolSafe.isEmpty(map)){
				map_type = iter_map_type.next();
			}

			if ("WIKI".equalsIgnoreCase(map_type)){
				instance.addProperty(p, instance.getModel().createResource( szInstanceNamespace + processResource(row_value)));
			}else if ("CSV".equalsIgnoreCase(map_type)){
				StringTokenizer st = new StringTokenizer(row_value,",");
				while (st.hasMoreTokens()){
					String szTemp = st.nextToken();
					if (!ToolSafe.isEmpty(szTemp)){
						szTemp = szInstanceNamespace + processResource(szTemp);
						instance.addProperty(p, instance.getModel().createResource(szTemp));						
					}
				}
			}else
			*/{
				 //System.out.println(row_value);
				 row_value = row_value.trim();
				 Matcher m1 = pattern_href.matcher(row_value);
				 Matcher m2 = pattern_url.matcher(row_value);
				 if (m1.matches()){
				//	System.out.println(m1.group(1).trim());
					instance.addProperty(p, instance.getModel().createResource(m1.group(1).trim()));					
				 }else if (m2.matches()){
				//	 System.out.println(m2.group().trim());
					instance.addProperty(p, instance.getModel().createResource(m2.group().trim()));					
				 }else{

					instance.addProperty(p, row_value);									 
				 }
			}
			
			//add some special parsing results
			
		}
	}
	
	@SuppressWarnings("unused")
	private static void annotateFile(Resource res_file){
		res_file.addProperty(RDF.type, FOAF.Document);
		res_file.addProperty(DC.creator, res_file.getModel().createResource("http://tw.rpi.edu"));
		res_file.addProperty(DC.date, ToolString.formatXMLDateTime(System.currentTimeMillis()));
	}
}

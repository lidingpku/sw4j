package sw4j.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import sw4j.util.DataTable;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;
import sw4j.util.rdf.ToolJena;
import sw4j.vocabulary.pml.PMLP;
import sw4j.vocabulary.pml.PMLR;

public class TaskCsv2Rdf {
	public static final String VER = "1.0";
	
	public static void main(String[] args) {
		String szWikiBase = "http://data-gov.tw.rpi.edu/vocab/";
		String szInputUrl = "http://www.data.gov/data_gov_catalog.csv";
		String szMapUrl = "http://data-gov.tw.rpi.edu/map/map00092.csv";
		String szDatasetID = "92";
		String szOutputDir = "../data-gov/raw/"+szDatasetID+"/";
		String szOutputBase = "http://data-gov.tw.rpi.edu/raw/"+szDatasetID+"/";
		loadCsv(szInputUrl, szMapUrl, szDatasetID, szWikiBase,szOutputBase,szOutputDir);
	}
	
	public static void loadCsv(String szInputUrl, String szMapUrl,  String szDatasetID, String szWikiBase,String szOutputBase, String szOutputDir){
		String szPropertyNamespace = szWikiBase + "Property:";
		String szInstanceNamespace = szWikiBase;

		String szIndexPath= "index.rdf";
		String szDataPath = "catalog.rdf";
		String szOutputDataFile = szOutputDir+szDataPath;
		String szOutputIndexFile = szOutputDir+szIndexPath;
		String szOutputDataUrl = szOutputBase+szDataPath;
		String szOutputIndexUrl = szOutputBase+szIndexPath;

		DataTable map =null;
		if (!ToolSafe.isEmpty(szMapUrl))
			map = DataTable.fromCSV(szMapUrl);
	
		DataTable table = DataTable.fromCSV(szInputUrl);

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
		model_data.setNsPrefix( "dgp",szPropertyNamespace);
		model_data.setNsPrefix( "dg",szInstanceNamespace);
		model_data.setNsPrefix(PMLP.class.getSimpleName().toLowerCase(),PMLP.getURI());
		model_data.setNsPrefix(PMLR.class.getSimpleName().toLowerCase(),PMLR.getURI());
		ToolJena.printModelToFile(model_data, szOutputDataFile, "RDF/XML",false);
		
		
		// model index
		Resource res_dataset = model_index.createResource(szOutputIndexUrl+"#me");
		res_dataset.addProperty(RDF.type, PMLP.Dataset);
		res_dataset.addProperty(DC.source, model_index.createResource(szInputUrl));
		res_dataset.addProperty(PMLR.hasPart, res_data_file);
		res_dataset.addProperty(RDFS.seeAlso,  model_index.createResource(szInstanceNamespace+"Dataset"+szDatasetID) );
		res_dataset.addLiteral(model_index.createProperty(szPropertyNamespace+"Number_of_entries"), table.getValues().size());
		res_dataset.addLiteral(model_index.createProperty(szPropertyNamespace+"Number_of_columns"), table.getHeader().size());
		res_dataset.addLiteral(model_index.createProperty(szPropertyNamespace+"Number_of_triples"), model_data.size());

		res_data_file.addProperty(PMLR.isPartOf, res_dataset);
		
		//model_index.setNsPrefix("dgp",szPropertyNamespace);
		model_index.setNsPrefix(PMLP.class.getSimpleName().toLowerCase(),PMLP.getURI());
		model_index.setNsPrefix(PMLR.class.getSimpleName().toLowerCase(),PMLR.getURI());
		model_index.setNsPrefix( "dg",szInstanceNamespace);
		model_index.setNsPrefix( "dgp",szPropertyNamespace);
		ToolJena.printModelToFile(model_index, szOutputIndexFile, "RDF/XML",false);
		

	}
	


	public static String processProperty(String szName){
		if (ToolSafe.isEmpty(szName))
			return null;
		String szTemp = szName;
		szTemp = szTemp.replaceAll("/\\W+/", " ");
		szTemp = szTemp.trim();
		szTemp = szTemp.replaceAll(" ", "_");
		szTemp = szTemp.toLowerCase();
		szTemp = szTemp.substring(0,1).toUpperCase()+szTemp.substring(1);
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
		szTemp = szTemp.substring(0,1).toUpperCase()+szTemp.substring(1);
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

//		Iterator<String> iter_map_header = map.getHeader().iterator();
		Iterator<List<String>> iter_map = null;

		Iterator<String> iter_map_type = null;

		if (!ToolSafe.isEmpty(map)){
			iter_map = map.getValues().iterator();

			iter_map_type = iter_map.next().iterator();
		}
		
		Pattern pattern_href = Pattern.compile("^<a +href=\"([^\"]+)\">[\\s]*h?[tf]tps?[^<]+</a>$");
		Pattern pattern_url = Pattern.compile("^h?[tf]tps?://[\\S]+$");

		while (iter_row.hasNext()){
			String row_value = iter_row.next();
			Property p = iter_header.next();
			
			//TODO verify map_header
			//String map_header = iter_map_header.next();

			// process mapping file
			String map_type = null;
			if (!ToolSafe.isEmpty(map)){
				map_type = iter_map_type.next();
			}

			if (ToolSafe.isEmpty(row_value))
				continue;
			
				
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
/*			}else if ("URL".equalsIgnoreCase(map_type)){
				instance.addProperty(p, instance.getModel().createResource(row_value));
*/			}else{
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
		res_file.addProperty(RDF.type, PMLP.Document);
		res_file.addProperty(PMLP.hasPublisher, res_file.getModel().createResource("http://data-gov.tw.rpi.edu/vocab/Tetherless_World_Constellation"));
		res_file.addProperty(PMLP.hasCreationDateTime, ToolString.formatXMLDateTime(System.currentTimeMillis()));
		res_file.addProperty(PMLP.hasVersion, VER);
	}
}

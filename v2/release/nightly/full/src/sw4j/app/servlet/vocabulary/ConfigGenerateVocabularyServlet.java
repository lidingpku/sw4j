package sw4j.app.servlet.vocabulary;


import sw4j.rdf.util.ToolOwl2Java;
import sw4j.util.Sw4jException;


/**
 * 
 * @author Li Ding
 *
 */
public class ConfigGenerateVocabularyServlet {
	
	
	public static void main(String [] arg){
		try {
			String [][] aryOntologyURL = new String [][]{
//					{"http://inference-web.org/2007/10/service/search.owl#" , "SEARCH"},
//					{"http://inference-web.org/2007/10/service/iwsearch.owl#", "IWSEARCH"},
//					{"http://inference-web.org/2007/10/service/archive.owl#", "ARCHIVE"},
//					{"http://inference-web.org/2007/10/service/pmlvalidation.owl#", "PML_VALIDATION"},
//					{"http://inference-web.org/2007/10/service/response.owl#", "RESPONSE"},
//					{"http://tw.rpi.edu/2008/sw/archive.owl#", "ARCHIVE", "archive"},
					{"http://tw.rpi.edu/2009/sw/response.owl#", "RESPONSE", "response"},
					{"http://tw.rpi.edu/2009/sw/rdf-metadata.owl#", "RM", "rm"},
//					{"http://data-gov.tw.rpi.edu/2009/data-gov-twc.rdf#", "DGTWC", "dgtwc"},
			};
			
			for (int i=0; i< aryOntologyURL.length; i++){
				ToolOwl2Java.genSimpleJavaCode(
						aryOntologyURL[i][0], 
						"src-example", 
						"sw4j.app.servlet.vocabulary" ,
						aryOntologyURL[i][1], 
						aryOntologyURL[i][2], 
						true);
			}
			
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

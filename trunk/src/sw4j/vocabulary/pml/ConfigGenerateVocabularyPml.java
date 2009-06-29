package sw4j.vocabulary.pml;


import sw4j.task.util.ToolOwl2Java;
import sw4j.util.Sw4jException;


/**
 * 
 * @author Li Ding
 *
 */
public class ConfigGenerateVocabularyPml {
	
	
	public static void main(String [] arg){
		try {
			String [][] aryOntologyURL = new String [][]{
					{"http://inference-web.org/2.0/pml-provenance.owl#", "pmlp"},
					{"http://inference-web.org/2.0/pml-justification.owl#", "pmlj"},
					{"http://inference-web.org/2.0/pml-relation.owl#", "pmlr"},
					{"http://inference-web.org/2.0/ds.owl#", "pmlds"},
					{"http://inferenceweb.stanford.edu/2004/03/iw.owl#","iw200403"},
					{"http://inferenceweb.stanford.edu/2004/07/iw.owl#","iw200407"},
			};
			
			for (int i=0; i< aryOntologyURL.length; i++){
				ToolOwl2Java.genSimpleJavaCode(
						aryOntologyURL[i][0], 
						"src", 
						"sw4j.vocabulary.pml" ,
						aryOntologyURL[i][1].toUpperCase(), 
						aryOntologyURL[i][1], 
						true);
			}
			
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

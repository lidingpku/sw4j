package sw4j.rdf.util;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.Test;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ToolModelDiffTest {
	public Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}
	@Test
	public void test_canonical(){
		//szURI = "http://data.semanticweb.org/conference/iswc/2009/complete";
		//szURI = "http://www.cs.rpi.edu/~dingl/foaf.rdf";
		String [] files = new String []{
				"files/canonical_test/test1_bnode_signed_by_triple.rdf",
				"files/canonical_test/test2_bnode_signed_by_triples.rdf",
				"files/canonical_test/test3_bnode_indistinguishable.rdf",
				"files/canonical_test/test4_typed_literal.rdf",
		};
		
		for (String file: files){
			
			getLogger().info("=======================");
			getLogger().info("testing: "+ file);
			Model m = ModelFactory.createDefaultModel() ;
			String sz_xmlbase =  "http://foo.com/rdf";
			try {
				m.read(ToolIO.prepareFileInputStream(file),sz_xmlbase);
				String ret = ToolModelDiff.printModel_cannonical_carroll(m);
				getLogger().info ("\n"+ret);
				
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
		}
		
	}

}

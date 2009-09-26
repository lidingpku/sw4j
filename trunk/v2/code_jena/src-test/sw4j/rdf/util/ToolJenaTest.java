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
package sw4j.rdf.util;

import static org.junit.Assert.fail;


import org.junit.Test;

import sw4j.rdf.load.AgentModelLoader;
import sw4j.rdf.util.ToolJena;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDFS;
/**
 * 
 * @author Li Ding
*/

public class ToolJenaTest {

	@Test 
	public void testSignBlanknode(){
		String [] urls = new String []{
				"http://tw.rpi.edu/2009/08/test1.rdf",	
				"http://tw.rpi.edu/2009/08/test4.rdf",	
				"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/answer.owl",
			};
			for (int i=0; i<urls.length; i++){
				String szURL =urls[i];
				Model m = ModelFactory.createDefaultModel();
				m.read(szURL);

				Model ret = ToolJena.model_signBlankNode(m,"http://tw.rpi.edu/test#");
				System.out.println(ToolJena.printModelToString(ret));
			}
	}
	
	@Test
	public void testReadList() {
		String szURL;
		szURL = "files/jena_test/list1.rdf";
		Model m = ModelFactory.createDefaultModel() ;
		Property PMLDS_first= m.createProperty("http://inference-web.org/2.0/ds.owl#first");
		Property PMLDS_rest = m.createProperty("http://inference-web.org/2.0/ds.owl#rest");
		try {
			m.read(ToolIO.prepareFileInputStream(szURL), "http://foo.com/rdf");
			System.out.println("original");
			System.out.println(m.size());
			ToolJena.printModel(m);
			
			Model m1 = 	ToolJena.model_clone(m);
			ToolJena.model_update_List2Map(m1, PMLDS_first, PMLDS_rest, null, false);
			System.out.println("added");
			System.out.println(m1.size());
			ToolJena.printModel(m1);
			
			if (m.size()+2!=m1.size()){
				fail();
			}
			
			Model m2 = 	ToolJena.model_clone(m);
			ToolJena.model_update_List2Map(m2, PMLDS_first, PMLDS_rest,null, true);
			System.out.println("added and remove");
			System.out.println(m2.size());
			ToolJena.printModel(m2);
			
			System.out.println(ToolJena.getNodePrefix(PMLDS_first));
			
			if (m.size()-3!=m2.size()){
				fail();
			}

			
		} catch (Sw4jException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_deductive_closure() {
		String [] urls = new String []{
			"files/deductive_test/test1-subclass-for-rdfsclass.rdf",	
			"http://inference-web.org/2.0/pml-provenance.owl",	
		};
		for (int i=0; i<urls.length; i++){
			String szURL =urls[i];
			AgentModelLoader loader= new AgentModelLoader(szURL);
			if (null == loader.getModelData())
				continue;
			
			Model m = loader.getModelData();
			int size_old = m.listStatements(null,RDFS.subClassOf,(String)null).toSet().size();
			System.out.println(ToolString.printCollectionToString(m.listStatements(null,RDFS.subClassOf,(String)null).toSet()));
			ToolJena.model_add_transtive(m, RDFS.subClassOf);
			int size_new = m.listStatements(null,RDFS.subClassOf,(String)null).toSet().size();
			System.out.println(size_new-size_old);
			System.out.println(ToolString.printCollectionToString(m.listStatements(null,RDFS.subClassOf,(String)null).toSet()));
		}
	}
/*	
	@Test
	public void test_deductive_closure() {
		String [] urls = new String []{
			"files/deductive/test1-subclass.owl",	
			"files/deductive/test2-equivalentclass.owl",	
			"http://tw.rpi.edu/2008/03/wine.owl",	
		};
		for (int i=0; i<urls.length; i++){
			String szURL =urls[i];
			Model m = ModelFactory.createDefaultModel();
			try {
				if (ToolURI.isUriHttp(szURL)){
					m.read(ToolIO.pipeUrlToInputStream(szURL), szURL);
				}else{
					m.read(ToolIO.pipeFileToInputStream(szURL), szURL);					
				}
				//m = AgentModelManager.get().loadModel(szURL);
				Model m1 =ToolJena.model_createTransitiveClosure_RDFS(m);
				Model m2 =ToolJena.model_createDeductiveClosure_OWLDL(m);
				System.out.println(
						m.size()
						+ "," + m1.listStatements().toSet().size() 
						+ "," + m2.listStatements().toSet().size()
									);
				print_s_o(m.listStatements(null, RDFS.subClassOf, (String)null));
				System.out.println("---");
				print_s_o(m1.listStatements(null, RDFS.subClassOf, (String)null));
				System.out.println("---");
				print_s_o(m2.listStatements(null, RDFS.subClassOf, (String)null));
				System.out.println("---");
				print_s_o(ToolJena.model_createTransitiveClosure_RDFS(m2).listStatements(null, RDFS.subClassOf, (String)null));
			} catch (Sw4jException e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	private void print_s_o(StmtIterator iter ){
		while (iter.hasNext()){
			Statement stmt = (Statement) iter.next();
			System.out.println (stmt.getSubject()+"->"+stmt.getObject());
		}
	}
*/	
	

	
	//@Test
	public void testWriteModelToFile() {
		String szURI;
		szURI = "http://inference-web.org/registry/DPR/Told.owl#Told";
		Model m = ModelFactory.createDefaultModel() ;
		m.read(szURI);
		ToolJena.printModel(m);
		if (null!= m)
			ToolJena.printModelToFile(m, "test-pml-writer.rdf", null,false);
		
	}

/*	@Test
	public void test_ontmodel() {
		String szURI;
		szURI = "http://inference-web.org/registry/DPR/Told.owl#Told";
		
		Model m = null;
		OntModel model_onto=ToolPellet.createOntModel();
		System.out.println(model_onto.listStatements(null,RDFS.subClassOf,(RDFNode)null).toSet());
		
		try {
			m =ModelManager.get().loadModel(szURI);
			ToolJena.mergeModel(model_onto, m);
			ModelManager.get().readOntModel(model_onto, m, null);
		} catch (SwutilException e) {
			// in case cannot load URL, simply skip it.
			// TODO remove bad ones from index
			e.printStackTrace();
			fail();
		}
	
		DataMultiValueMap mapInstanceTypes = ToolJena.getInstanceTypes(model_onto);
		DataMultiValueMap mapInstanceTypes_direct = ToolJena.getInstanceTypes(m);

		// index pml instances
		Set<Resource> insts = PMLAnalyzer.listDescribedPmlInstances(m);

		Iterator<Resource> iter_inst = insts.iterator();
		while (iter_inst.hasNext()){
			Resource inst = iter_inst.next();

			List types = mapInstanceTypes.get(inst);
			List types_direct = mapInstanceTypes_direct.get(inst);

			System.out.println(inst);
			System.out.println(types_direct);
			System.out.println(types);
			
			if (!types.containsAll(types_direct)){
				fail();
			}
		}
			
	}
	
*/	
}

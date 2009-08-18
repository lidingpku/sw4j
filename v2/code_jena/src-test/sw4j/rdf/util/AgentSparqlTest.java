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

import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.AgentSparql;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import com.hp.hpl.jena.rdf.model.Model;
/**
 * 
 * @author Li Ding
*/

public class AgentSparqlTest {
	@Test
	public void test_SPARQL() {
	
		{
			String queryText;
			try {
				queryText = ToolIO.pipeUrlToString("http://onto.rpi.edu/alpha/sparql/test1.sparql");
				Model m = (Model) new AgentSparql().exec(queryText, RDFSYNTAX.RDFXML);
		        if (null!=m && m.size()>0)
		            m.write(System.out, RDFSYNTAX.RDFXML);
		        else
		        	fail();
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }
		
		{
			String queryText;
			try {
				queryText = ToolIO.pipeUrlToString("http://onto.rpi.edu/alpha/sparql/test4.sparql");
				Object ret = new AgentSparql().exec(queryText, RDFSYNTAX.RDFXML);
		        if (!ToolSafe.isEmpty(ret))
		            System.out.println(ret);
		        else
		        	fail();
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }

		
		{
			String queryText = 
				"PREFIX dc:      <http://purl.org/dc/elements/1.1/> " +
				"PREFIX rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX foaf:      <http://xmlns.com/foaf/0.1/> " +
				"describe ?p " +
				"FROM NAMED <http://www.cs.rpi.edu/~dingl/foaf.rdf> " +
				"WHERE {GRAPH ?g { ?p rdf:type foaf:Person}} ";
			Model m = (Model) new AgentSparql().exec(queryText, RDFSYNTAX.RDFXML);
	        if (null!=m && m.size()>0)
	            m.write(System.out, RDFSYNTAX.N3);
	        else
	        	fail();
		}

		
		{
			String queryText;
			try {
				queryText = ToolIO.pipeFileToString("files/sparql_test/test2.sparql");
				Model m = (Model) new AgentSparql().exec(queryText, RDFSYNTAX.RDFXML);
		        if (null!=m && m.size()>0)
		            m.write(System.out, RDFSYNTAX.RDFXML);
		        else
		        	fail();
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }

		{// default graph, without inference
			String queryText = 
			"PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX pmlp:      <http://inference-web.org/2.0/pml-provenance.owl#> " +
			"PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> " +
			"PREFIX foaf:      <http://xmlns.com/foaf/0.1/> " +
			"SELECT ?cls " +
			"FROM <http://inference-web.org/2.0/pml-provenance.owl> " +
			"WHERE {?cls rdfs:subClassOf  pmlp:Source } ";
			System.out.println( new AgentSparql().exec(queryText, RDFSYNTAX.RDFXML));
		}

		{
			String queryText;
			try {
				queryText = ToolIO.pipeFileToString("files/sparql_test/test11.sparql");
				System.out.println(new AgentSparql().exec(queryText, RDFSYNTAX.RDFXML));
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }
	}
}

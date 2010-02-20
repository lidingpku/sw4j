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
package sw4j.pellet;

import static org.junit.Assert.fail;


import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.pellet.AgentSparqlPellet;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;


/**
 * 
 * @author Li Ding
*/

public class AgentSparqlPelletTest {
	@Test
	public void test(){
		{
			String queryText;
			try {
				queryText = ToolIO.pipeFileToString("files/sparql_test/test12.sparql");
				//System.out.println( queryText );
				System.out.println( new AgentSparqlPellet(true).exec(queryText, ""));
				
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }
		
	}
	
	//@Test
	public void test_SPARQL() {
	
		{
			String queryText;
			try {
				queryText = ToolIO.pipeFileToString("files/sparql_test/test10.sparql");
				Object ret = new AgentSparqlPellet(true).exec(queryText, RDFSYNTAX.SPARQL_XML);
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
			String queryText;
			try {
				queryText = ToolIO.pipeUrlToString("http://tw.rpi.edu/proj/portal.wiki/images/d/dd/CoreQuery3_0.sparql");
				Object ret =  new AgentSparqlPellet(true).exec(queryText, RDFSYNTAX.SPARQL_XML);
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
			String queryText;
			try {
				queryText = ToolIO.pipeUrlToString("http://onto.rpi.edu/alpha/sparql/test1.sparql");
				Model m = (Model) new AgentSparqlPellet(true).exec(queryText, RDFSYNTAX.SPARQL_XML);
		        if (null!=m && m.size()>0)
		            m.write(System.out, RDFSYNTAX.RDFXML);
		        else
		        	fail();
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }
		
	
		{// named graph, expect pattern in named graph, with Pellet inference
			String queryText = 
				"PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX pmlp:      <http://inference-web.org/2.0/pml-provenance.owl#> " +
				"PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX foaf:      <http://xmlns.com/foaf/0.1/> " +
				"SELECT ?cls " +
				"FROM NAMED  <http://inference-web.org/2.0/pml-provenance.owl> " +
				"WHERE {GRAPH ?g {?cls rdfs:subClassOf  pmlp:Source } }";
			System.out.println(  new AgentSparqlPellet(true).exec(queryText, null));
		}
		
		{// named graph, expect pattern in default graph,  with Pellet inference, no result (we need to be specific the pattern is on default graph or a named graph)
			String queryText = 
				"PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX pmlp:      <http://inference-web.org/2.0/pml-provenance.owl#> " +
				"PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX foaf:      <http://xmlns.com/foaf/0.1/> " +
				"SELECT ?cls " +
				"FROM  NAMED <http://inference-web.org/2.0/pml-provenance.owl> " +
				"WHERE { ?cls rdfs:subClassOf  pmlp:Source  }";
			System.out.println( new AgentSparqlPellet(true).exec(queryText, null));
		}

		{// named graph, expect pattern in default graph, expect more )
			String queryText = 
				"PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX pmlp:      <http://inference-web.org/2.0/pml-provenance.owl#> " +
				"PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX foaf:      <http://xmlns.com/foaf/0.1/> " +
				"SELECT ?cls " +
				"FROM   <http://inference-web.org/2.0/pml-provenance.owl> " +
				"WHERE { ?cls rdfs:subClassOf  pmlp:Source  }";
			System.out.println( new AgentSparqlPellet(true).exec(queryText, null));
		}

		{// named graph, expect pattern in default graph, expect more )
			String queryText = 
				"PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX pmlp:      <http://inference-web.org/2.0/pml-provenance.owl#> " +
				"PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX foaf:      <http://xmlns.com/foaf/0.1/> " +
				"SELECT ?cls count(*)" +
				"FROM   <http://inference-web.org/2.0/pml-provenance.owl> " +
				"WHERE { ?cls rdfs:subClassOf  pmlp:Source  }" +
				"GROUP BY ?cls";
			System.out.println( new AgentSparqlPellet(true).exec(queryText, null));
		}

		{
			String queryText;
			try {
				queryText = ToolIO.pipeFileToString("files/sparql_test/test6.sparql");
				System.out.println( new AgentSparqlPellet(true).exec(queryText, null));
				
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }
		{
			String queryText;
			try {
				queryText = ToolIO.pipeFileToString("files/sparql_test/test12.sparql");
				System.out.println( queryText );
				System.out.println( new AgentSparqlPellet(true).exec(queryText, ""));
				
			} catch (Sw4jException e) {
				
				e.printStackTrace();
	        	fail();
			}
        }
	}
	

}

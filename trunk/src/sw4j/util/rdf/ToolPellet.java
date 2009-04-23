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
package sw4j.util.rdf;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.PelletOptions.MonitorType;
import org.mindswap.pellet.jena.PelletReasonerFactory;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Pellet interface
 * 
 * @author Li Ding
 *
 */
public class ToolPellet {
	// disable pellet logging
	static {
		Logger.getLogger("org.mindswap.pellet.PelletOptions").setLevel(Level.OFF);
		Logger.getLogger("org.mindswap.pellet.ABox").setLevel(Level.OFF);
		Logger.getLogger("org.mindswap.pellet.KnowledgeBase").setLevel(Level.OFF);
		Logger.getLogger("org.mindswap.pellet.taxonomy.CDOptimizedTaxonomyBuilder").setLevel(Level.OFF);
		Logger.getLogger("org.mindswap.pellet.taxonomy.Taxonomy").setLevel(Level.OFF);
		Logger.getLogger("org.mindswap.pellet.jena.OWLLoader").setLevel(Level.OFF);

		PelletOptions.USE_CLASSIFICATION_MONITOR=MonitorType.NONE;
	}
	

	public static OntModel createOntModel(){
		return ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC );

	}


	
}

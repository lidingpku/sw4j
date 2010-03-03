package sw4j.rdf.diff;


import java.util.HashSet;

import org.apache.log4j.Logger;

import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataPVTMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ToolModelDiff {

	
	private static Logger getLogger(){
		return Logger.getLogger(ToolModelDiff.class);
	}

	/**
	 * this implementation is based on Carroll's 2003 paper  "One-step Deterministic Labeling"
	 * there are some limitations
	 * 
	 * @param m
	 * @return
	 */
	public  String printModel_cannonical_carroll_one_step(Model m){
		DataDiffGraph g = DataDiffGraph.create(m);

		cannonical_carroll_one_step(g);
		
		// validation, optional
		g.isSigned();		
		
		return g.toString();

	}

	public  String printModel_cannonical_carroll_nd(Model m){
		DataDiffGraph g = DataDiffGraph.create(m);

		cannonical_carroll_nd(g);
		
		// validation, optional
		g.isSigned();		
		
		return g.toString();

	}

	public  void cannonical_carroll_one_step(DataDiffGraph g){
		// step 1-5 
		// 1,2 canonicalize the graph into NDataDiffTriple format
		// 3,4 replace blanknode and add comments
		// 5. sort
		for (DataDiffTriple triple: g.getData()){
			triple.init();
		}

//getLogger().info("\n"+g.toString());
		
		// pre step6: detect indistinguishable triples (having the same content)
		DataPVTMap<String,DataDiffTriple> map_content_triple =  new DataPVTMap<String, DataDiffTriple>();
		for (DataDiffTriple triple: g.getData()){
			map_content_triple.add(triple.getContentString(),triple);
		}
		
		// pre step6. build a table for blank node
		DataObjectGroupMap<String> map_bnode_id = new DataObjectGroupMap<String>();

		// step 6: process distinguishable triples
		for (String key : map_content_triple.keySet()){
			//skip indistinguishable triples 
			if (map_content_triple.getValuesCount(key)>1){
				getLogger().info("found indistinguishable triples " + key + " - " + map_content_triple.getValuesCount(key));
				continue;
			}

			// this is the only triple
			DataDiffTriple triple = map_content_triple.getValues(key).iterator().next();
		
			// replace common label with id
			triple.updateContent(map_bnode_id, false);
		}

//getLogger().info("\n"+g.toString());
	
		// step 7,8: process all triples
		for (DataDiffTriple triple: g.getData()){
			// replace common label with id
			triple.updateContent(map_bnode_id,true);
		}


		g.close();
		//getLogger().info("\n"+g.toString());

	}

	public void cannonical_carroll_nd(DataDiffGraph g){

		//A. Perform a one-step deterministic labeling
		cannonical_carroll_one_step(g);
		
		//B. If there are no hard to label nodes, then stop. [Ensures idempotence]
		if (g.isSigned())
			return;
		
		//C. Delete all triples with predicate c14n:true . [B ensures idempotence]
		g.cleanup_c14n();
		
		//D. Perform a one-step deterministic labeling.
		cannonical_carroll_one_step(g);




		/*
		 * E. Using a new lookup table from step D, and a new counter, scan the file from top
				to bottom, performing these steps on each line:
				a. If there is a “~” in object position:
				i. Extract the blank node identifier from the final comment in the line.
				Remove the comment.
				ii. Look the identifier up in the table.
				iii. If there is no entry: add an entry “x” to the table; and add a new triple to
				the graph with subject being the blank node identified by the identifier
				from the comment, predicate being c14n:true and object being the
				string form of the counter; increment the counter.
				b. If there is a “~” in subject position, use the same subprocedure to possibly
				create a distinctive triple for the subject as well.
		 */
		DataObjectGroupMap<String> map_bnode_id = new DataObjectGroupMap<String>();

		// step 6: process distinguishable triples
		HashSet<DataDiffTriple> new_data = new HashSet<DataDiffTriple>();
		for (DataDiffTriple triple: g.getData()){
			// replace common label with id
			if (!triple.isSignedObject()){
				Integer id = map_bnode_id.addObject(triple.o);
				DataDiffTriple new_triple = new DataDiffTriple(triple.o, DataDiffTriple.C14N_TRUE_URI, id.toString(), true,false);
				new_data.add(new_triple);
			}
				
			if (!triple.isSignedSubject()){
				Integer id = map_bnode_id.addObject(triple.s);
				DataDiffTriple new_triple = new DataDiffTriple(triple.s, DataDiffTriple.C14N_TRUE_URI, id.toString(), true,false);
				new_data.add(new_triple);
			}
		}
		
		for (DataDiffTriple triple: new_data){
			g.add(triple);
		}

		
		
		//F. Perform a one-step deterministic labeling (of the new modified graph, with a new lookup table and counter).
		cannonical_carroll_one_step(g);
		
		// remove c14n triple
		g.cleanup_c14n();
	}
}

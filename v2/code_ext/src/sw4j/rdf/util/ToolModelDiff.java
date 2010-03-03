package sw4j.rdf.util;

import java.util.TreeSet;

import org.apache.log4j.Logger;

import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataPVTMap;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

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
	public static String printModel_cannonical_carroll(Model m){

		class Triple implements Comparable<Triple>{
			String s="";
			String p="";
			String o="";
			String c_s="";
			String c_o="";
			Statement statement=null;
			
			public static final String BNODE_LABEL= "~";
			
			public Triple(Statement stmt){
				if (stmt.getSubject().isAnon()){
					s= BNODE_LABEL;
					c_s = ToolJena.getNTripleString(stmt.getSubject());
				}else{
					s= ToolJena.getNTripleString(stmt.getSubject());
				}
				
				p = ToolJena.getNTripleString(stmt.getPredicate());
				
				if (stmt.getObject().isAnon()){
					o= BNODE_LABEL;
					c_o = ToolJena.getNTripleString(stmt.getObject());
				}else{
					o= ToolJena.getNTripleString(stmt.getObject());
				}
				statement = stmt;
			}

			public boolean isSigned(){
				return ToolSafe.isEmpty(c_s)&& ToolSafe.isEmpty(c_o);
			}
			
			public String toString(){
				if (isSigned())
					return getContentString();
				else
					return String.format("%s %s %s . # %s %s ", s,p,o,c_s,c_o); 
			}

			public String getContentString(){
				return String.format("%s %s %s . ", s,p,o); 
			}
			
			public void updateContent(DataObjectGroupMap<RDFNode> map_bnode_id, boolean bReadOnly){
				//6b. assign id to object
				if (statement.getObject().isAnon() && o.equals(BNODE_LABEL)){
					RDFNode bnode = statement.getObject();
					Integer id=null;
					if (bReadOnly)
						id = map_bnode_id.getGid(bnode);
					else
						id = map_bnode_id.addObject(bnode);
					
					if (null!=id){
						o = "_:g"+(id+1);
					
						//remove comment
						c_o="";
					}
				}
				
				//6c. assign id to subject
				if (statement.getSubject().isAnon()&& s.equals(BNODE_LABEL)){
					RDFNode bnode = statement.getSubject();
					Integer id=null;
					if (bReadOnly)
						id = map_bnode_id.getGid(bnode);
					else
						id = map_bnode_id.addObject(bnode);

					if (null!=id){
						s = "_:g"+(id+1);
	
						//remove comment
						c_s="";
					}
				}
			}

			@Override
			public int compareTo(Triple arg0) {
				return this.toString().compareTo(arg0.toString());
			}
		}
		
		// step 1-5 
		// 1,2 canonicalize the graph into NTriple format
		// 3,4 replace blanknode and add comments
		// 5. sort
		TreeSet<Triple> triples = new TreeSet<Triple>();
		for(Statement stmt: m.listStatements().toList()){
			triples.add( new Triple(stmt));
		}

		// pre step6: detect indistinguishable triples (having the same conent)
		DataPVTMap<String,Triple> map_content_triple =  new DataPVTMap<String, Triple>();
		for (Triple triple: triples){
			map_content_triple.add(triple.getContentString(),triple);
		}
		// pre step6. build a table for blank node
		DataObjectGroupMap<RDFNode> map_bnode_id = new DataObjectGroupMap<RDFNode>();

		// step 6: process distinguishable triples
		for (String key : map_content_triple.keySet()){
			//skip indistinguishable triples 
			if (map_content_triple.getValuesCount(key)>1){
				getLogger().info("found indistinguishable triples " + key + " - " + map_content_triple.getValuesCount(key));
				continue;
			}

			// this is the only triple
			Triple triple = map_content_triple.getValues(key).iterator().next();
		
			// replace common label with id
			triple.updateContent(map_bnode_id, false);
		}
		
		// step 7,8: process all triples
		for (Triple triple: triples){
			// replace common label with id
			triple.updateContent(map_bnode_id,true);
		}
		
		// validation
		// check if there are still some unsigned triples
		for (Triple triple: triples){
			if (!triple.isSigned())
				getLogger().info("some triple not signed yet " + triple);				
		}
		
		// produce output
		return ToolString.printCollectionToString(triples);
	}

}

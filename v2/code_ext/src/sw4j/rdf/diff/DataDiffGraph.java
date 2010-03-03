package sw4j.rdf.diff;

import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

import sw4j.util.ToolString;

public class DataDiffGraph {
	private TreeSet<DataDiffTriple> triples = new TreeSet<DataDiffTriple>();
	private boolean isSigned = false;

	private  Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}
	
	public static DataDiffGraph create(Model m){
		DataDiffGraph g = new DataDiffGraph();
		for(Statement stmt: m.listStatements().toList()){
			g.add( new DataDiffTriple(stmt));
		}
		
		return g;
	}
	
	public void add(DataDiffTriple triple){
		triples.add(triple);
	}
	
	public TreeSet<DataDiffTriple> getData(){
		return triples;
	}
	
	public boolean isSigned(){
		isSigned = true;
		// check if there are still some unsigned triples
		for (DataDiffTriple triple: triples){
			if (!triple.isSigned()){
				getLogger().info("triple not signed yet " + triple);
				isSigned = false;
			}
		}			
		return isSigned;
	}
	
	public String toString(){
		return ToolString.printCollectionToString(triples);
	}


	public void close() {
		for (DataDiffTriple triple: triples){
			triple.close();
		}
	}

	public void cleanup_c14n() {
		Iterator<DataDiffTriple> iter = this.triples.iterator();
		while (iter.hasNext()){
			DataDiffTriple triple = iter.next();
			if (triple.p.equals(DataDiffTriple.C14N_TRUE_URI)){
				iter.remove();
			}
		}		
	}
}

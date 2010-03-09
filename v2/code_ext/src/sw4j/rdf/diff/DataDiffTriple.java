package sw4j.rdf.diff;

import com.hp.hpl.jena.rdf.model.Statement;

import sw4j.rdf.util.ToolJena;
import sw4j.util.DataObjectGroupMap;
import sw4j.util.ToolSafe;

public class DataDiffTriple implements Comparable<DataDiffTriple>{
	String s="";
	String p="";
	String o="";
	boolean is_anno_subject =false;
	boolean is_anno_object =false;
	String comment_s="";
	String comment_o="";
	
	//Statement statement=null;
	
	public static final String BNODE_LABEL= "~";
	public static final String BNODE_PREFIX= "_:g";
	public static final String C14N_TRUE_URI = "http://www-uk.hpl.hp.com/people/jjc/rdf/c14n#true";

	public DataDiffTriple(Statement stmt){
		this(ToolJena.getNTripleString(stmt.getSubject()),
				ToolJena.getNTripleString(stmt.getPredicate()),
				ToolJena.getNTripleString(stmt.getObject()),
				stmt.getSubject().isAnon(),
				stmt.getObject().isAnon());
		//	statement = stmt;
	}
	public DataDiffTriple(String s, String p, String o, boolean is_anno_subject, boolean is_anno_object){

		this.s= s;
		this.p = p;
		this.o= o;

		this.is_anno_subject = is_anno_subject;
		this.is_anno_object = is_anno_object;
	}
	
	public void init(){		
		if (is_anno_subject){
			comment_s = s;
			s= BNODE_LABEL;
		}
		
		if (is_anno_object){
			comment_o = o;
			o= BNODE_LABEL;
		}
	}

	public boolean isSigned(){
		return isSignedSubject()&& isSignedObject();
	}
	
	public  boolean isSignedSubject(){
		if (!is_anno_subject)
			return true;
		
		if (s.startsWith(BNODE_PREFIX))
			return true;
		
		return false;
	}

	public boolean isSignedObject(){
		if (!is_anno_object)
			return true;
		
		if (o.startsWith(BNODE_PREFIX))
			return true;
		
		return false;
	}

	public String toString(){
		if (isSigned())
			return getContentString();
		else
			return String.format("%s %s %s . # %s %s ", s,p,o,comment_s,comment_o); 
	}

	public String getContentString(){
		return String.format("%s %s %s . ", s,p,o); 
	}
	
	public void updateContent(DataObjectGroupMap<String> map_bnode_id, boolean bReadOnly){
		//6b. assign id to object
		if (!isSignedObject()){
			String bnode = comment_o;
			Integer id=null;
			if (bReadOnly)
				id = map_bnode_id.getGid(bnode);
			else
				id = map_bnode_id.addObject(bnode);
			
			if (null!=id){
				o = BNODE_PREFIX+(id+1);
			
				//remove comment
				comment_o="";
			}
		}
		
		//6c. assign id to subject
		if (!isSignedSubject()){
			String bnode = comment_s;
			Integer id=null;
			if (bReadOnly)
				id = map_bnode_id.getGid(bnode);
			else
				id = map_bnode_id.addObject(bnode);

			if (null!=id){
				s = BNODE_PREFIX+(id+1);

				//remove comment
				comment_s="";
			}
		}
	}
	
	/**
	 * reset comments
	 */
	public void close(){
		if (!ToolSafe.isEmpty(comment_s)){
			s= comment_s;
			comment_s = "";
			
		}
		
		if (!ToolSafe.isEmpty(comment_o)){
			s= comment_o;
			comment_o = "";
		}
	}

	@Override
	public int compareTo(DataDiffTriple arg0) {
		return this.toString().compareTo(arg0.toString());
	}

}

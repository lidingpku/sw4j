package sw4j.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class DataObjectGroupMap <V>{

	//eq relation
	DataPVCMap<V,V> m_map_uri_uri = new DataPVCMap<V,V>();
	
	// original node to group (merged node)
	HashMap<V,Integer> m_map_uri_gid = new HashMap<V,Integer>();
	
	//group (merged node) to original node
	DataPVCMap<Integer,V> m_map_gid_uris = new DataPVCMap<Integer,V>();
	
	private static int ggid= 0;
	public static Integer create_gid(){
		return new Integer(ggid++);
	}
	
	public Integer add(V uri){
		Integer gid = this.m_map_uri_gid.get(uri);
		if (null==gid){
			gid = create_gid();
			do_add(uri, gid);
		}
		return gid;
	}
	
	public Integer add(V uri, Integer gid){
		if (null!=gid){
			do_remove(uri);
			do_add(uri,gid);
			return gid;
		}else{
			return add(uri);
		}
	}

	public Integer getGid(V uri){
		Integer gid = this.m_map_uri_gid.get(uri);
		return gid;
	}

	public Collection<V> getIds(Integer gid){
		return this.m_map_gid_uris.getValues(gid);
	}

	public void addLink(V uri1, V uri2){
		do_merge(uri1,uri2);
		m_map_uri_uri.add(uri1,uri2);
	}
	
	public void normalize(){
		Iterator<V> iter = this.m_map_uri_uri.keySet().iterator();
		while (iter.hasNext()){
			V uri1 = iter.next();
			Iterator <V> iter_2 = this.m_map_uri_uri.getValues(uri1).iterator();
			while (iter_2.hasNext()){
				V uri2 = iter_2.next();
				
				do_merge(uri1,uri2);
			}
		}
	}
	
	private void do_merge(V uri1, V uri2){
		Integer gid1 = this.m_map_uri_gid.get(uri1);
		Integer gid2 = this.m_map_uri_gid.get(uri2);
		
		if (null!=gid1){
			if (null!=gid2){
				//both non-empty
				if (gid1.equals(gid2))
					return ;
				
				List<V> group1 = this.m_map_gid_uris.getValues(gid1);  
				List<V> group2 = this.m_map_gid_uris.getValues(gid2);
				
				Iterator<V> iter = group2.iterator();
				while (iter.hasNext()){
					V uri2x = iter.next();
					this.m_map_uri_gid.put(uri2x, gid1);
				}
				
				group1.addAll(group2);
				
				this.m_map_gid_uris.remove(gid2);
				
			}else{
				// empty gid2
				do_add(uri2,gid1);
			}
		}else{
			if (null!=gid2){
				// empty gid1
				do_add(uri1,gid2);
				
			}else{
				// both empty
				gid1 =add(uri1);
				do_add(uri2,gid1);
				
			}
		}
	}
	
	private void do_remove(V uri){
		Integer gid = this.m_map_uri_gid.get(uri);
		if (null==gid){
			this.m_map_gid_uris.getValues(gid).remove(uri);
			this.m_map_uri_gid.put(uri, null);
		}
	}

	private void do_add(V uri, Integer gid){
		this.m_map_gid_uris.add(gid, uri);
		this.m_map_uri_gid.put(uri,gid);
	}
	

	public String toString(){
		return this.m_map_gid_uris.m_index.toString();// +"\n"+ this.m_map_uri_uri.toString()+"\n";
	}

	public int getTotalIds() {
		return this.m_map_uri_gid.size();
	}
	public int getTotalGroups() {
		return this.m_map_gid_uris.keySet().size();
	}

	public String toPrettyString() {
		String ret = "";
		Iterator <Integer> iter = this.m_map_gid_uris.keySet().iterator();
		while (iter.hasNext()){
			Integer gid = iter.next();
			ret+="\n";
			ret+="------------------";
			ret+="\n";
			ret+=gid;
			ret+="\n";
			ret+= ToolSafe.printCollectionToString(this.getIds(gid));
		}
		return ret;
	}
}

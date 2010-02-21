package sw4j.app.servlet;

import java.io.File;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;

import sw4j.app.servlet.common.AbstractService;
import sw4j.app.servlet.common.DataServletResponse;
import sw4j.rdf.util.AgentSparql;
import sw4j.rdf.util.ToolJena;
import sw4j.task.common.DataTaskReport;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;

public class CacheService extends AbstractService{
	@Override
	public String getName() {
		return "TW Linked Data Cache Service";
	}

	@Override
	public String getHomepage() {
		try {
			return ToolIO.pipeInputStreamToString(DataTaskReport.class.getResourceAsStream("www/lodc.html"));
		} catch (Sw4jException e) {
			//e.printStackTrace();
			return "";
		}
	}
	   	
   	
	public static Dataset gTdb = null;
	public static Dataset getTdb(){
		if (null==gTdb){
			String directory = "/work/tdb/sw4j/" ;
			File dir = new File(directory);
			if (!dir.exists()){
				dir.mkdirs();
			}
			
			gTdb = TDBFactory.createDataset(directory) ;
			
			getLogger().info("TDB loaded with "+ gTdb.getDefaultModel().size()+" triples.");
		}
		return gTdb;
	}
	
	
	static Logger getLogger(){
		return Logger.getLogger(CacheService.class);
	}
	
	public DataServletResponse run(){
		String uri=this.getUriParam(PARAM_URI);
		String query=this.params.getAsString(PARAM_QUERY);
		String output = this.params.getAsString(AbstractService.PARAM_OUTPUT);

		String url= DataQname.extractNamespaceUrl(uri);

		
		boolean bEmptyURI= ToolSafe.isEmpty(uri);
		boolean bEmptyQuery= ToolSafe.isEmpty(query);
	
        
    	Dataset dataset = getTdb();
        if (!bEmptyURI){
        	Model tdb = dataset.getDefaultModel();
        	String msg = "";

        	//load uri
        	Model m = ModelFactory.createDefaultModel();
        	try{
            	m.read(url);
        	}catch(Exception e){
            	DataServletResponse ret = DataServletResponse.createResponse("cannot load RDF data from "+uri +"\n"+e.getMessage(), false, this);
            	return ret;
        	}
        	
        	if (m.size()>0){
        		msg+=String.format(" %d triples loaded from %s\n",m.size(),uri);
        	}
        	

        	//update TDB store     	
        	Resource subject = m.createResource(uri);
        	Model m_old = ToolJena.createCBD(tdb, subject);
        	tdb.remove(m_old);
        	
        	
        	Model m_new= ToolJena.createCBD(m, subject);
        	tdb.add(m_new);

        	if (m_new.size()>m_old.size()){
        		msg+=String.format(" %d triples added\n",m_new.size()-m_old.size());
        	}else{
        		msg+=String.format(" %d triples removed\n",m_old.size()-m_new.size());        		
        	}
        		

        	// see http://openjena.org/wiki/TDB/JavaAPI
        	tdb.commit();
        	//TDB.sync(tdb);
        	
        	DataServletResponse ret = DataServletResponse.createResponse(msg, true, this);
        	return ret;
        	
        }else if (!bEmptyQuery){
            // run sparql and return result
            Object results;
            
           	results = new AgentSparql().exec(query, dataset, output);
            
            if (ToolSafe.isEmpty(results)){
            	DataServletResponse ret = DataServletResponse.createResponse("empty result.", false, this);
            	return ret;
            }else if (results instanceof Model){
            	DataServletResponse ret = DataServletResponse.createResponse((Model)results, null, output);
            	return ret;
            }else{
            	DataServletResponse ret = DataServletResponse.createResponse(results.toString(), true, this);
            	return ret;
            }
        	
        }
        
    	return null;    	
	}

}

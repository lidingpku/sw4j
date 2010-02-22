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
package sw4j.rdf.load;

/**
 * load RDF data from a file or webpage.
 * # loadModel() - load RDF data via file or URI into jena.Model
 *    [OPTION 1] user can specify the xml:base while parsing
 *     
 * # loadOntModel() - load RDF data via file or URI into jena.OntModel
 *
 * # reports warnings and errors using log4j.Logger 
 *   throws IWSharedException when failed loading
 *  
 * # two level cache option: 
 *   (i) Jena.FileManager - file-system cache (use location-mapping.n3); 
 *   (ii) setCaching() - in-memory cache  
 *  
 *  @author Li Ding
 */

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import sw4j.rdf.util.AgentModelStat;
import sw4j.rdf.util.ToolModelAnalysis;
import sw4j.task.load.TaskLoad;
import sw4j.util.DataCachedObjectMap;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;

import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.LocationMapper;

public class AgentModelManager {
	public static Logger getLogger(){
		return Logger.getLogger(AgentModelManager.class);
	}

	protected AgentModelManager(){
		//load preloaded models
		loadPreloadedModels();
	}
	

	@SuppressWarnings("unchecked")
	private void loadPreloadedModels(){
		String filename = "/work/swutil/location.config";
		File config = new File(filename);
		if (!config.exists())
			return;

		getLogger().info("loading configure file from "+filename);
		AgentModelLoader result = new AgentModelLoader(filename);
		Model m = result.getModelData();
		if (null==m)
			return;
		
		LocationMapper mapper = new LocationMapper(m);
		Iterator iter =mapper.listAltEntries();
		while (iter.hasNext()){
			String url = (String) iter.next();
			String location = mapper.getAltEntry(url);
			
			getLogger().info("preloading ontology "+url +" from "+ location);
			AgentModelLoader loader = new AgentModelLoader(location);
			Model model = loader.getModelData(); 
			if (null!=model){
				getLogger().info("preloading url ok");
				m_preloaded_model.put(url,model);
			}else{
				getLogger().info("preloading url failed");				
			}
		}
			
	}
	
	private static AgentModelManager gModelManager = new AgentModelManager();

	public static AgentModelManager get() {
		return gModelManager;
	}

	// we use cache by default
	Map<String, Model> m_preloaded_model = new HashMap<String, Model>(); //  the default is no cache
	DataCachedObjectMap<String, Model> m_cache_model = null; //  the default is no cache
	DataCachedObjectMap<String, Map<String,Model>> m_cache_ontmodel = null; //new DataCachedObjectMap<String, OntModel>();


	public void setCaching(boolean bUseCache) {
		if (bUseCache){
			if (this.m_cache_model == null) {
				this.m_cache_model = new DataCachedObjectMap<String, Model>();
				this.m_cache_ontmodel = new DataCachedObjectMap<String, Map<String,Model>>();
			}
		} else {
			this.m_cache_model = null;
			this.m_cache_ontmodel = null;
		}
	}

	public Model loadModel(String szFileOrURI) throws Sw4jException {
		return loadModel(szFileOrURI, null,null);
	}
	
	/**
	 * load a (read only) jena model.
	 * Do not change the model 
	 * 
	 * @param szFileOrURI	the location of RDF data
	 * @param szBaseURI	 the suggested xml:base (URL or URI)
	 * @return
	 * @throws Sw4jException
	 */
	public Model loadModel(String szFileOrURI, String szXmlBase) throws Sw4jException {
		return loadModel(szFileOrURI, szXmlBase, null);
	}

	public Model loadModel(String szFileOrURI, String szXmlBase, String szRdfSyntax) throws Sw4jException {
		ToolSafe.checkNonEmpty(szFileOrURI,
				"expecting a non-empty filename or URI");

		// if base URI is not specified, assume it is the same as szFileOrURI
		if (ToolSafe.isEmpty(szXmlBase)){
			szXmlBase = szFileOrURI;
		}
		String szKey = null;
		try{
			DataQname dqn = DataQname.create(szXmlBase, null);
			szXmlBase = dqn.getNamespaceCanonical(); 
			szKey = szXmlBase; //ToolURI.extractNamespaceCanonical(szFileOrURI);
		}catch(Sw4jException e){
			szXmlBase = TaskParseRdf.createRuntimeXmlbase();
		}
		
		
		// the model to be parsed from the szFileOrUri
		Model m = null;
		
		// when we can parse the namespace of the szFileOrURI,
		// the namespace can uniquely identify the szFileOrURI
		if (null!= szKey){
			//first try to use preloaded model
			m = m_preloaded_model.get(szKey);
			if (null!=m){
				getLogger().info("using preloaded model " + szKey);
				return m;
			}
			
			
			//if we use cache, then check if the corresponding content has been cached
			if (null != this.m_cache_model) {
				m = this.m_cache_model.get(szKey);
				
				// if there is a hit on cache
				if (null!=m){
					getLogger().info("using cacehd model " + szKey);
					return m;
				}
			}
		}
		
		// we should not load meta ontology
		if (ToolModelAnalysis.getMetaMamespaces().contains(szXmlBase)){
			return null;
		}
		
		AgentModelLoader result = new AgentModelLoader(szFileOrURI, szXmlBase, szRdfSyntax);
		
/*		// otherwise, we need to load the szFileOrURI for model
		DataTaskLoad task_load = ToolLoad.load(szFileOrURI, szXmlBase, szRdfSyntax);
		if (null==task_load)
			return null;
			
		if (!task_load.isLoadSucceed()){
			return null;
		}
		
		DataTaskParseRdf task_rdf = ToolParseRdf.parse(task_load);
		if (null==task_rdf)
			return null;
*/		
		m =  result.getModelData();

		// when the parse failed 
		if (null==m){
			return null;
		}
		
		//if we have key for cache
		if (null != szKey){
			//if we use cache, then update cached with new results
			if (null != this.m_cache_model) {
				this.m_cache_model.put(szKey, m);
			}
		}
		
		return m;
	}	
	
	/**
	 * load model from text, no cache
	 * 
	 * @param szText
	 * @param szBaseURI
	 * @param szRdfSyntax
	 * @return
	 * @throws Sw4jException
	 */
	public static Model loadModelFromText(String szText, String szXmlBase, String szRdfSyntax) throws Sw4jException {
		ToolSafe.checkNonEmpty(szText,
				"expecting non-empty text");
		
		// otherwise, we need to load the szFileOrURI for model
		TaskLoad task_load = TaskLoad.loadText(szText, szXmlBase, szRdfSyntax);
		if (null==task_load)
			return null;
			
		if (!task_load.isLoadSucceed()){
			return null;
		}
		
		TaskParseRdf task_rdf = TaskParseRdf.parse(task_load);
		if (null==task_rdf)
			return null;
		
		return  task_rdf.getModel();		
	}
	
	public Map<String,Model> loadModelRecursive(String url, boolean bUseImport, boolean bUseNamespace){
		HashSet<String> urls = new HashSet<String>();
		urls.add(url);
		return this.loadModelRecursive(urls, bUseImport, bUseNamespace);
	}	
	
	/**
	 * load a set of models (using import)
	 * @param urls
	 * @return
	 */
	public Map<String,Model> loadModelRecursive(Set<String> urls, boolean bUseImport, boolean bUseNamespace){
		Map<String,Model> ret = new TreeMap<String,Model>();
		if (ToolSafe.isEmpty(urls)){
			return ret;
		}

		// normalize urls
		TreeSet<String> urls_normalized = new TreeSet<String>();
		{
			Iterator<String> iter = urls.iterator();
			while (iter.hasNext()){
				String szURL = iter.next();
				
				DataQname dqn;
				try {
					dqn = DataQname.create(szURL,null);
					szURL = dqn.getNamespaceCanonical(); 
				} catch (Sw4jException e) {
					// skip bad url
				}
				if (null!=szURL)
					urls_normalized.add(szURL);
			}
		}
		
		
		// cache 
		String szKey = urls_normalized.toString();
		if (null != this.m_cache_ontmodel) {
			Map<String,Model> cached  = this.m_cache_ontmodel.get(szKey);
			if (null!= cached)
				return cached;
		}
		
		// load urls
		{
			
			Set<String> to_visit = new HashSet<String>();
			Set<String> visited = new HashSet<String>();

			// init 
			to_visit.addAll(urls_normalized);
			
			//visit
			while (to_visit.size()>0){
				Set<String> to_visit_temp = new HashSet<String>();
	
				Iterator<String> iter = to_visit.iterator();
				while (iter.hasNext()){
					String szURL =iter.next().toString();
					
					if (visited.contains(szURL))
						continue;
	
					visited.add(szURL);
	
					Model m =null;
					try{
						 m = loadModel(szURL);
					}catch(Sw4jException e){						
						getLogger().warn(e.getLocalizedMessage());
					}
						
					// record download result
					ret.put(szURL,m);

					// add new links
					if (null !=m){
						AgentModelStat stat = new AgentModelStat();
						stat.traverse(m);
						to_visit_temp.addAll( stat.getNormalizedLinks(bUseImport, bUseNamespace));
					}
				}
				to_visit.addAll(to_visit_temp);
				to_visit.removeAll(visited);
			}			
		}
		
		// update cache
		if (null != this.m_cache_ontmodel) {
			this.m_cache_ontmodel.put(szKey, ret);
		}
		
		return ret;
	}

	public void clearCache() {
		if (null!= this.m_cache_model)
			this.m_cache_model.clear();
		if (null!= this.m_cache_ontmodel)
			this.m_cache_ontmodel.clear();
	}
	 
	/**
	 * load all referenced ontologies and return the deductive closure
	 * 
	 * @param m
	 * @param szURL
	 * @return
	 */
	
/*	public Model loadAll(Model m, String szURL){
		InspectOwlDl inspect_owldl = InspectOwlDl.inspect(m, szURL, true);
		if (!inspect_owldl.isConsistent()){
			System.out.println("inconsistent pml data");
		}
		return inspect_owldl.getModelAll();
	}
*/		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	/**
	 * read data into a jena model
	 * 
	 * @param model  the target model to be filled
	 * @param szFileOrURI  the location of RDF data
	 * @return the model if can load. otherwise throw an IWSException
	 * @throws SwutilException
	 * 
	 */
	/*
	public void readModel(Model model, String szFileOrURI) throws SwutilException {
		readModel(model, szFileOrURI, szFileOrURI);
	}

	public void readModel(Model model, String szFileOrURI, String szBaseURI) throws SwutilException {
		ToolChecker.check_string_non_empty(szFileOrURI,
				"expecting a non-empty filename or URI");

		//FileManager mgr = FileManager.get();
		// mgr.removeCacheModel(szFileOrURI);

		//String temp = mgr.mapURI(szFileOrURI);
		//System.out.println(temp);

		try {

			String szKey = ToolURI.extractNamespaceCanonical(szFileOrURI);
			String szXmlBase = ToolURI.extractNamespace(szBaseURI);  
			
			// the model may have no namespace, e.g. a local file, then we will not cache it.
			if (null == szKey){
				DataTaskLoad data_load = ToolLoader.load(szFileOrURI);
				
				if (null==data_load){
					// cannot connect to the file or uri 
					return;
				}
				
				if (ToolCommon.safe_is_empty(data_load.getContent())){
					// empty content from the file or uri 
					// various reasons, e.g. robots.txt, 404
					return;
				}
				
				DataTaskRdfParse data_rdf = ToolParseRdf.parse(data_load);
				
				//mgr.readModel(model, szFileOrURI);
				return;
			}
			
			//otherwise the model is cacheable
			Model m = null;

			if (null != this.m_cache_model) {
				m = this.m_cache_model.get(szKey);
			}

			// cache failed or not using cache
			if (null == m) {
				Logger.getLogger(this.getClass()).info(
						"loading jena Model from file or URI: " + szFileOrURI);

				
				if (ToolCommon.safe_is_empty(szXmlBase) || szFileOrURI.startsWith(szXmlBase))
					m  = mgr.loadModel(szFileOrURI);
				else
					m = mgr.loadModel(szFileOrURI, szXmlBase, null);			
				

			}


			if (null != m && !m.isEmpty()) {
				model.add(m);

				// update cache
				if (null != this.m_cache_model) {
					this.m_cache_model.put(szKey, m);
				}
			} else {
				throw new SwutilException(
						SwutilException.ERROR_CANNOT_LOAD_RDF_TRIPLE,						
						//"No RDF data can be detected in the document."
						//updated by jiao 4/3/2008
						"No RDF triples can be detected in the document.", 
						"No RDF triples can be detected in the document.");
			}
		} catch (JenaException e) {
			String abs;
			String msg = e.getMessage();
			if (null==msg){
				abs = msg="n/a";
			}else{			
				//updated by jiao 4/3/2008		
			
				//case1: prolog
				msg =msg.toLowerCase();
				String szTest1, szTest2;
				szTest1 = "content is not allowed in prolog";
				boolean b1 =  (msg.indexOf(szTest1)>=0);
				
				//case2: Syntax error
				szTest1 = "the element type";
				szTest2	= "must be terminated by the matching end-tag";
				boolean b2 = (msg.indexOf(szTest1)>=0 && msg.indexOf(szTest2)>=0);
			
				if (b1){
					abs = "Can not parse RDF triples from the input document. prolog is expected at the beginning of RDF/XML documents.";
				}else if (b2){	
					abs = "RDF syntax error found in the parsing process.";					
				}else {
					abs = msg;				
				}
			}			
			throw new SwutilException(SwutilException.ERROR_CANNOT_LOAD_RDF_TRIPLE,
					abs, msg+">\n"+"        <FILE/URL: "+szFileOrURI);

		} catch (Exception e) {
			String abs;
			String msg = e.getMessage();
			if (null==msg){
				abs = msg="n/a";
			}else{	
				//updated by jiao 4/3/2008
				
				//case1: invalid URI
				msg =msg.toLowerCase();
				String szTest1;
				szTest1 = "not found: http:";
				boolean b1 =  (msg.indexOf(szTest1)>=0);
				
				if (b1){
					abs = "Can not load RDF triples from the specified URI.";					
				}else {
					abs = msg;
				}
				
			}
			throw new SwutilException(SwutilException.ERROR_CANNOT_LOAD_RDF_TRIPLE,
					abs, msg+">\n"+"        <FILE/URL: "+szFileOrURI);
		}
	}

	
	public static void readModelFromText(Model model, String szText, String szURL, String szRdfSyntax) throws SwutilException {
		try {
			Model m = ModelFactory.createDefaultModel();
			if (ToolCommon.safe_is_empty(szRdfSyntax))
				// read text as RDF/XML
				// TODO guess RDF syntax
				m.read(ToolIO.pipeStringToInputStream(szText),szURL);
			else
				m.read(ToolIO.pipeStringToInputStream(szText), szURL, szRdfSyntax);
			
			model.add(m);
		}catch (JenaException e){			
			String abs;
			String msg = e.getMessage();
			if (null==msg){
				abs = msg="n/a";
			}else{			
				//updated by jiao 4/3/2008			
			
				//case1: prolog
				msg =msg.toLowerCase();
				String szTest1, szTest2;
				szTest1 = "content is not allowed in prolog";
				boolean b1 =  (msg.indexOf(szTest1)>=0);
				
				//case2: Syntax error
				szTest1 = "the element type";
				szTest2	= "must be terminated by the matching end-tag";
				boolean b2 = (msg.indexOf(szTest1)>=0 && msg.indexOf(szTest2)>=0);		
			
				if (b1){
					abs = "Can not parse RDF triples from the input document. prolog is expected at the beginning of RDF/XML documents.";
				}else if (b2){	
					abs = "RDF syntax error found in the parsing process.";					
				}else {
					abs = msg;				
				}
			}
			
			//getLogger().info("failed parsing RDF data from text or file");
			throw new SwutilException (SwutilException.ERROR_CANNOT_LOAD_RDF_TRIPLE, abs, msg);
		}catch (Exception e) {
			String abs;
			String msg = e.getMessage();
			if (null==msg){
				abs = msg="n/a";
			}else{	
				//updated by jiao 4/3/2008
				
				//case1: invalid URI
				msg =msg.toLowerCase();
				String szTest1;
				szTest1 = "not found: http:";
				boolean b1 =  (msg.indexOf(szTest1)>=0);
				
				if (b1){
					abs = "Can not load RDF triples from the specified URI.";					
				}else {
					abs = msg;
				}
				
			}			
			throw new SwutilException (SwutilException.ERROR_CANNOT_LOAD_RDF_TRIPLE, abs, msg);
		}
		
	}	
*/



	/**
	 * load a model and its transitively imported models
	 * 
	 * @param model  the target model to be filled
	 * @param set_urls  a set of urls to be loaded
	 * @throws SwutilException
	 */
/*	public void readOntModel(OntModel model, TreeSet<String> set_urls)
			throws SwutilException {
		
		if (null==set_urls){
			throw new SwutilException(SwutilException.ERROR_EMPTY_URLS);
		}

		String szKey = set_urls.toString();
		Logger.getLogger(this.getClass()).info(
				"loading jena OntModel from file or URI: " + szKey);

		OntModel m = null;


		if (null==m){
			m = ModelFactory.createOntologyModel(model.getSpecification());
		
			//otherwise
			//transitively load all imported namespaces into this model
			Set<String> to_visit = new HashSet<String>();
			//Set<String> all_namespaces = new HashSet<String>();
			Set<String> visited = new HashSet<String>();
	
			{
				Iterator iter = set_urls.iterator();
				while (iter.hasNext()){
					String szURL =iter.next().toString();
					String szTodoURI = ToolURI.extractNamespace(szURL);
					to_visit.add(szTodoURI);
				}
			}
			while (to_visit.size()>0){
				Set<String> to_visit_temp = new HashSet<String>();
	
				Iterator<String> iter = to_visit.iterator();
				while (iter.hasNext()){
					String szURL =iter.next().toString();
					String szOntologyURI = ToolURI.extractNamespace(szURL);
					
					if (ToolCommon.isEmpty(szOntologyURI))
						continue;
					
					if (visited.contains(szOntologyURI))
						continue;
	
					visited.add(szOntologyURI);
	
					//if (all_namespaces.contains(szOntologyURI))
					//	continue;


					try{
						ToolJena.mergeModel(m, loadModel(szOntologyURI));
						
						//all_namespaces.add(szOntologyURI);	// add imported namespace
						NodeIterator iter_import = m.listObjectsOfProperty(OWL.imports);
						while (iter_import.hasNext()){
							to_visit_temp.add(iter_import.nextNode().toString());
						}

					}catch(SwutilException e){						
						getLogger().warn(e.getMessage());
					}
				
				}
				to_visit.addAll(to_visit_temp);
				to_visit.removeAll(visited);
			}
		}
		
		
		// add to model
		ToolJena.mergeModel(model, m);
	}
	
*/
}

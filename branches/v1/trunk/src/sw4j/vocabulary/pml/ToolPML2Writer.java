package sw4j.vocabulary.pml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sw4j.util.ToolSafe;



import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ToolPML2Writer {

	
	public static final String DEFAULT_NAMESPACE= "http://provenanceweb.org/base#";
	String m_szXmlBase ;
	Model m_model;
	
	public String getXmlBase() {
		return m_szXmlBase;
	}
	public Model getModel() {
		return m_model;
	}
	
	public ToolPML2Writer(){
		this (ModelFactory.createDefaultModel(), DEFAULT_NAMESPACE);
	}
	public ToolPML2Writer(Model m){
		this (m, DEFAULT_NAMESPACE);
	}

	public ToolPML2Writer(Model m, String szXmlBase){
		if (ToolSafe.isEmpty(m))
			m = ModelFactory.createDefaultModel();
		if (ToolSafe.isEmpty(szXmlBase))
			szXmlBase = DEFAULT_NAMESPACE;
		m_szXmlBase = szXmlBase;
		m_model = m;
	}

	static int ggid =0;
	public Resource createResource(){
		ggid++;
		return m_model.createResource(String.format("%sggid%05d",m_szXmlBase,ggid));
	}
	
	public Resource createResource(Integer id){
		return m_model.createResource(String.format("%sid%05d",m_szXmlBase, id));
	}

	public Resource createResource(String szUri){
		return m_model.createResource(szUri);
	}

	public List<Resource> createResource(List<Integer> ids){
		ArrayList<Resource> ret = new ArrayList<Resource>();
		Iterator<Integer> iter = ids.iterator();
		while (iter.hasNext()){
			ret.add(createResource(iter.next()));
		}
		return ret;
	}
	
	public static Resource addInformation(Model m, Resource res, String szRawString, Resource language, String szURL, String szLabel, String szMimetype){
		res.addProperty(RDF.type, PMLP.Information);
		
		if (!ToolSafe.isEmpty(szRawString))
			res.addProperty(PMLP.hasRawString, szRawString);

		if (!ToolSafe.isEmpty(szRawString))
			res.addProperty(PMLP.hasLanguage, language);
		
		if (!ToolSafe.isEmpty(szURL))
			res.addProperty(PMLP.hasURL, m.createTypedLiteral(szURL, XSDDatatype.XSDanyURI ));
		
		if (!ToolSafe.isEmpty(szLabel)){
			res.addProperty(RDFS.label, szLabel);
			res.addProperty(PMLP.hasPrettyString, szLabel);
		}
		
		if (!ToolSafe.isEmpty(szMimetype))
			res.addProperty(PMLP.hasMimetype, szMimetype);
		
		return res;
	}
	
	
	public static Resource addNodeSet(Model m, Resource res, Resource info, List<Resource> steps){
		res.addProperty(RDF.type, PMLJ.NodeSet);
		
		res.addProperty(PMLJ.hasConclusion, info);

		if (!ToolSafe.isEmpty(steps)){
			Iterator<Resource> iter = steps.iterator();
			while(iter.hasNext()){
				res.addProperty(PMLJ.isConsequentOf, iter.next());
			}
		}
		return res;
	}

	public static Resource addNodeSet(Model m, Resource res, Resource info, Resource step){
		res.addProperty(RDF.type, PMLJ.NodeSet);
		
		res.addProperty(PMLJ.hasConclusion, info);

		if (!ToolSafe.isEmpty(step)){
			res.addProperty(PMLJ.isConsequentOf, step);
		}
		return res;
	}
	
	public static void appendStep(Model m, Resource ns, Resource info, Resource is, List<Resource> antecedents, String szURL ){
		addInformation(m, info, null,null,szURL,null,null);
		addNodeSet(m, ns, info, is);
		addInferenceStep(m, is, null,null,null, antecedents);
	}

	
	public static Resource addInferenceStep(Model m, Resource res, Resource inferenceRule, Resource inferenceEngine, String params, List<Resource> antecedents ){
		res.addProperty(RDF.type, PMLJ.InferenceStep);
		
		if (!ToolSafe.isEmpty(inferenceRule))
			res.addProperty(PMLJ.hasInferenceRule, inferenceRule);
		if (!ToolSafe.isEmpty(inferenceEngine))
			res.addProperty(PMLJ.hasInferenceEngine, inferenceEngine);
		if (!ToolSafe.isEmpty(params))
			res.addProperty(m.createProperty(PMLJ.getURI()+"hasParameter"), params);
		
		if (!ToolSafe.isEmpty(antecedents)){
			Iterator<Resource> iter = antecedents.iterator();
			Resource parent = null;
			while(iter.hasNext()){
				Resource list = m.createResource();
				list.addProperty(RDF.type, PMLJ.NodeSetList);
				list.addProperty(PMLDS.first, iter.next());
				if (null!=parent)
					parent.addProperty(PMLDS.rest, list);
				else
					res.addProperty(PMLJ.hasAntecedentList, list);
				parent = list;
			}
		}
		return res;
	}
}

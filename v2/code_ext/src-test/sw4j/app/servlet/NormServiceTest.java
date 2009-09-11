package sw4j.app.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLR;
import sw4j.app.servlet.common.DataServletResponse;

public class NormServiceTest {
	@Test
	public void test_sign() throws IOException{
		NormService svc = new NormService();
		svc.params.put(NormService.PARAM_URL, "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/answer.owl");
		svc.params.put(NormService.PARAM_OPTION, NormService.PARAM_VALUE_OPTION_SIGN);

		DataServletResponse ret = svc.run();
		if (ret.m_model_content==null || !ret.isSucceed())
			fail("URL should be succeed");		

		 
		Iterator<Resource> iter = ret.m_model_content.listSubjects();
		while (iter.hasNext()){
			Resource res = iter.next();
			if (res.isAnon())
				fail("no uri should be blank node after sign");
		}
	}
	
	@Test
	public void test_unsign() throws IOException{
		NormService svc = new NormService();
		svc.params.put(NormService.PARAM_URL, "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/answer.owl");
		svc.params.put(NormService.PARAM_TYPE_URI, PMLJ.NodeSet.getURI());
		svc.params.put(NormService.PARAM_OPTION, NormService.PARAM_VALUE_OPTION_UNSIGN);

		DataServletResponse ret = svc.run();
		
		if (ret.m_model_content==null || !ret.isSucceed())
			fail("URL should be succeed");		
		 
		Iterator<Resource> iter = ret.m_model_content.listSubjectsWithProperty(RDF.type, PMLJ.NodeSet);
		while (iter.hasNext()){
			Resource res = iter.next();
			if (res.isURIResource())
				fail("uri should be blank node after sign");
		}
	}
	
	@Test
	public void test_dlist() throws IOException{
		NormService svc = new NormService();
		svc.params.put(NormService.PARAM_URL, "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/answer.owl");
		svc.params.put(NormService.PARAM_OPTION, NormService.PARAM_VALUE_OPTION_DLIST);

		DataServletResponse ret = svc.run();
		
		if (ret.m_model_content==null || !ret.isSucceed())
			fail("URL should be succeed");		
		 
		Iterator<Resource> iter = ret.m_model_content.listSubjectsWithProperty(PMLR.hasPart, (RDFNode)null);
		if (!iter.hasNext())
			fail("some list should be generated");
	}
	
	@Test
	public void test_tp() throws IOException{
		NormService svc = new NormService();
		svc.params.put(NormService.PARAM_URL, "http://inference-web.org/2009/09/test-transitive.rdf");
		svc.params.put(NormService.PARAM_TP_URI, DC.source);
		svc.params.put(NormService.PARAM_OPTION, NormService.PARAM_VALUE_OPTION_TP);

		DataServletResponse ret = svc.run();
		if (ret.m_model_content==null || !ret.isSucceed())
			fail("URL should be succeed");		
		 
		Iterator<Statement> iter = ret.m_model_content.listStatements(null,DC.source, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt= iter.next();
			if (stmt.getSubject().isAnon())
				continue;
			if (stmt.getObject().isAnon())
				continue;
			System.out.println(stmt);
		}

	}

	@Test
	public void test_deduct_owl() throws IOException{
		NormService svc = new NormService();
		svc.params.put(NormService.PARAM_URL, "http://inference-web.org/2.0/pml-provenance.owl");
		svc.params.put(NormService.PARAM_OPTION, NormService.PARAM_VALUE_OPTION_DEDUCT_OWL);

		DataServletResponse ret = svc.run();

		if (ret.m_model_content==null || !ret.isSucceed())
			fail("URL should be succeed");		
		 
		Iterator<Statement> iter = ret.m_model_content.listStatements(null,RDFS.subClassOf, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt= iter.next();
			if (stmt.getSubject().isAnon())
				continue;
			if (stmt.getObject().isAnon())
				continue;
			
			System.out.println(stmt);
		}
	}
	
	@Test
	public void test_deduct_rdfs() throws IOException{
		NormService svc = new NormService();
		svc.params.put(NormService.PARAM_URL, "http://inference-web.org/2.0/pml-provenance.owl");
		svc.params.put(NormService.PARAM_OPTION, NormService.PARAM_VALUE_OPTION_DEDUCT_RDFS);

		DataServletResponse ret = svc.run();
		
		if (ret.m_model_content==null || !ret.isSucceed())
			fail("URL should be succeed");		
		 
		Iterator<Statement> iter = ret.m_model_content.listStatements(null,RDFS.subClassOf, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt= iter.next();
			if (stmt.getSubject().isAnon())
				continue;
			if (stmt.getObject().isAnon())
				continue;
			System.out.println(stmt);
		}
	}

}

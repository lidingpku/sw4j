package sw4j.task.oie;

import static org.junit.Assert.fail;

import java.util.Iterator;
import org.apache.log4j.Logger;
import org.junit.Test;

import sw4j.task.common.DataTaskReport;
import sw4j.task.load.TaskLoad;
import sw4j.task.load.ToolLoadHttp;
import sw4j.task.oie.DataEvaluationConfig;
import sw4j.task.oie.DefaultOwlInstanceDataChecker;
import sw4j.task.oie.InspectMissingPropertyValue;
import sw4j.task.oie.InspectOwlDl;
import sw4j.task.rdf.TaskParseRdf;
import sw4j.task.util.DataTaskResult;
import sw4j.util.ToolSafe;


public class DefaultOwlInstanceDataCheckerTest {
	protected Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}
	
	@Test
	public void simple_test_ok(){
		String [] address = new String[]{			
			"files/evaluation_test/ok_pml_200407_tonysns1_0.owl",
			//"http://www.cs.rpi.edu/~dingl/foaf.rdf",
			"files/evaluation_test/ok_pml2_data.owl",
			"files/evaluation_test/ok_DR202ns1_47.owl",
		};

		for (int i=0; i< address.length; i++){
			String szURL  = address[i];
			DefaultOwlInstanceDataChecker checker = new DefaultOwlInstanceDataChecker();
			verifyEmptyReport(checker.inspect(szURL,"", new DataEvaluationConfig()));
		}
	}
	

	@Test
	public void test_check_rdf(){
		String [][] address_issue1 = new String [][]{
				// non-exist URL
				{null, TaskLoad.REPORT_TITLE, TaskLoad.ERROR_SUMMARY_1},
				// non-exist URL
				{"http://tw.rpi.edu/proofs/tonys0000/tonys_1/ns2", TaskLoad.REPORT_TITLE, TaskLoad.ERROR_SUMMARY_1},
				// empty file
				{"files/evaluation_test/rdf_fatal_empty_file.rdf", TaskParseRdf.REPORT_TITLE, TaskParseRdf.ERROR_SUMMARY_5},
				// empty file
				{"files/evaluation_test/rdf_fatal_only_comment.rdf", TaskParseRdf.REPORT_TITLE, TaskParseRdf.ERROR_SUMMARY_1},
				// bad rdf
				{"files/evaluation_test/rdf_fatal_bad_rdf.rdf", TaskParseRdf.REPORT_TITLE, TaskParseRdf.ERROR_SUMMARY_1},
				// not rdf
				{"files/evaluation_test/rdf_fatal_not_rdf.rdf", TaskParseRdf.REPORT_TITLE, TaskParseRdf.ERROR_SUMMARY_1},
		};
		
		for (int i=0; i< address_issue1.length; i++){
			verifyReport(address_issue1[i][0], address_issue1[i][1], address_issue1[i][2]);
		}

		// check a correct rdf document
		String szURL = "http://www.cs.rpi.edu/~dingl/foaf.rdf";
		getLogger().info("*************start load text data***************");
		String szContent = ToolLoadHttp.wget(szURL);
		getLogger().info("*************end load text data***************");

		//verifyReportText (szContent, null, null);

		// check a bad RDF document
		szContent = szContent.substring(0,szContent.length()-4);
		verifyReportText( szContent, 
						TaskParseRdf.REPORT_TITLE, 
						TaskParseRdf.ERROR_SUMMARY_1);
			
		// check an empty rdf document
		verifyReportText( "", 
				TaskParseRdf.REPORT_TITLE,  
				TaskParseRdf.ERROR_SUMMARY_5);
	}
	

	//@Test
	public void test_reference_ontology(){
		

		String [][] address_issue = new String [][]{
			{"files/evaluation_test/reference_warn_undefined_class.rdf", InspectLoadReferencedOntology.REPORT_TITLE, InspectLoadReferencedOntology.ERROR_SUMMARY_6},
			{"files/evaluation_test/reference_warn_undefined_property.rdf", InspectLoadReferencedOntology.REPORT_TITLE, InspectLoadReferencedOntology.ERROR_SUMMARY_7},
			{"files/evaluation_test/reference_warn_eswc.rdf", InspectLoadReferencedOntology.REPORT_TITLE, InspectLoadReferencedOntology.ERROR_SUMMARY_1},
			//{"http://data.semanticweb.org/dumps/conferences/eswc-2007-complete.rdf",DataTaskLoadReference.REPORT_TITLE, DataTaskLoadReference.ERROR_SUMMARY_1},
			{"files/evaluation_test/reference_warn_bad_namespace.owl", InspectLoadReferencedOntology.REPORT_TITLE, InspectLoadReferencedOntology.ERROR_SUMMARY_1},
			{"files/evaluation_test/reference_warn_pml_undefined_fromoriginalanswer.owl", InspectLoadReferencedOntology.REPORT_TITLE, InspectLoadReferencedOntology.ERROR_SUMMARY_7},
			
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}

	@Test
	public void test_owldl(){
		

		String [][] address_issue = new String [][]{				
			{"files/evaluation_test/owldl_error_datatype_datetime.owl", InspectOwlDl.REPORT_TITLE, InspectOwlDl.ERROR_SUMMARY_2},
			{"files/evaluation_test/owldl_error_datatype_hasindex.owl", InspectOwlDl.REPORT_TITLE, InspectOwlDl.ERROR_SUMMARY_2},
			{"files/evaluation_test/owldl_error_disjointwith.owl", InspectOwlDl.REPORT_TITLE, InspectOwlDl.ERROR_SUMMARY_2},
			{"files/evaluation_test/owldl_error_cardinality_hasname.owl", InspectOwlDl.REPORT_TITLE, InspectOwlDl.ERROR_SUMMARY_2},
			{"files/evaluation_test/owldl_error_foaf_disjoin.owl", InspectOwlDl.REPORT_TITLE, InspectOwlDl.ERROR_SUMMARY_2},
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}

	@Test
	public void test_mpv(){
		

		String [][] address_issue = new String [][]{
			{"http://tw.rpi.edu/2008/04/wine-instance_mpv.rdf", InspectMissingPropertyValue.REPORT_TITLE, InspectMissingPropertyValue.ERROR_SUMMARY_2},
			{"files/evaluation_test/mpv_warn_cardinality.owl", InspectMissingPropertyValue.REPORT_TITLE, InspectMissingPropertyValue.ERROR_SUMMARY_2},
			{"files/evaluation_test/mpv_warn_mincardinality.owl", InspectMissingPropertyValue.REPORT_TITLE, InspectMissingPropertyValue.ERROR_SUMMARY_3},
			{"files/evaluation_test/mpv_warn_cardinality_external_ref.owl", InspectMissingPropertyValue.REPORT_TITLE, InspectMissingPropertyValue.ERROR_SUMMARY_2},
			{"files/evaluation_test/mpv_pml_200606_tonys_4.owl", InspectMissingPropertyValue.REPORT_TITLE, InspectMissingPropertyValue.ERROR_SUMMARY_3},
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}	
	
	@Test
	public void test_uit(){
		

		String [][] address_issue = new String [][]{
			{"http://tw.rpi.edu/2008/04/wine-instance_uit.rdf", InspectUnexpectedIndividualType.REPORT_TITLE, InspectUnexpectedIndividualType.ERROR_SUMMARY_3},
			{"files/evaluation_test/uit_warn_incompatible_domain.owl", InspectUnexpectedIndividualType.REPORT_TITLE, InspectUnexpectedIndividualType.ERROR_SUMMARY_1},
			{"files/evaluation_test/uit_warn_incompatible_range.owl", InspectUnexpectedIndividualType.REPORT_TITLE, InspectUnexpectedIndividualType.ERROR_SUMMARY_2},			
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}	
	
	//added by jiao 2008-06-19
	@Test
	public void test_rit(){		
	

		String [][] address_issue = new String [][]{
			{"http://tw.rpi.edu/2008/04/wine-instance_rit.rdf", InspectRedundantInvididualType.REPORT_TITLE, InspectRedundantInvididualType.ERROR_SUMMARY},
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}
	
	//added by jiao 2008-06-20
	@Test
	public void test_nsit(){		
	

		String [][] address_issue = new String [][]{
			{"http://tw.rpi.edu/2008/04/wine-instance_nsit.rdf", InspectNonSpecificInvididualType.REPORT_TITLE, InspectNonSpecificInvididualType.ERROR_SUMMARY},
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}
	
	//added by jiao 2008-06-22
	@Test
	public void test_epv(){		
	

		String [][] address_issue = new String [][]{
			{"http://tw.rpi.edu/2008/04/wine-instance_epv.rdf", InspectExcessivePropertyValue.REPORT_TITLE, InspectExcessivePropertyValue.ERROR_SUMMARY_1},
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}

	//@Test
	public void test_duplicated_error(){
		

		String [][] address_issue = new String [][]{
			{"files/evaluation_test/special_duplicated_entries.rdf", InspectUnexpectedIndividualType.REPORT_TITLE, InspectUnexpectedIndividualType.ERROR_SUMMARY_2},			
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}		
	/*
	@Test
	public void test_epv(){
		
		DefaultOwlInstanceDataChecker validator = new DefaultOwlInstanceDataChecker();

		String [][] address_issue = new String [][]{
			{"files/evaluation_test/epv_error_cardinality_fromanswerorquery.owl", DataEvaluationConfig.CHECK_EXCESSIVE_PROPERTY_VALUE, InspectExcessivePropertyValue.ERROR_SUMMARY_1},
			{"files/evaluation_test/epv_error_maxcardinality_hasauthorlist.owl", DataEvaluationConfig.CHECK_EXCESSIVE_PROPERTY_VALUE, InspectExcessivePropertyValue.ERROR_SUMMARY_1},			
		};
		
		for (int i=0; i< address_issue.length; i++){
			verifyReport(address_issue[i][0], address_issue[i][1], address_issue[i][2]);
		}
	}	
	

	 	System.out.println("test externally defined resoure");

		String szFileOrUrl;

		// class/property definition
		szFileOrUrl = "pml/files/validator_test/error_pml_undefined_fromoriginalanswer.owl";
		testValidateUrl(szFileOrUrl,"PML validation, undefined term - fromoriginalanswer", true, true);
		
		szFileOrUrl = "pml/files/validator_test/error_pml_undefined_firstsubmissiondatetime.owl";
		testValidateUrl(szFileOrUrl,"PML validation, undefined term - firstsubmissiondatetime", true, true);

		szFileOrUrl = "pml/files/validator_test/error_inferred_pml_instance.rdf";
		testValidateUrl(szFileOrUrl,"PML validation on instance data with addtional ontology", false, true);

		szFileOrUrl = "pml/files/validator_test/warning_pml_unexpected_pml_definition.owl";
		testValidateUrl(szFileOrUrl,"PML validation on unexpected pml class/property redefinition", false, true);

	 */
	

	private void verifyReportText(String szText, String title, String message){
		getLogger().info("checking Text: ");//+szText.substring(0,Math.max(szText.length(), 20)));
		getLogger().info("-------------------------------------------------------------------------------");

		DefaultOwlInstanceDataChecker checker = new DefaultOwlInstanceDataChecker();

		if (ToolSafe.isEmpty(message))
			verifyEmptyReport(checker.inspectText(szText,null, "", new DataEvaluationConfig()));
		else{
			verifyReport( 
					checker.inspectText(szText,null, "", new DataEvaluationConfig()), 
					title, 
					message);
		}

	}
	
	private void verifyReport(String szFileOrUrl, String title, String message){
		getLogger().info("checking "+szFileOrUrl);
		getLogger().info("-------------------------------------------------------------------------------");
		
		DefaultOwlInstanceDataChecker checker = new DefaultOwlInstanceDataChecker();
		if (ToolSafe.isEmpty(message))
			verifyEmptyReport(checker.inspect(szFileOrUrl,null, new DataEvaluationConfig()));
		else{
			DataTaskResult der = checker.inspect(szFileOrUrl, null, new DataEvaluationConfig());
			verifyReport(der, title, message);
		}
		
	}
		
	private void verifyReport(DataTaskResult der, String title, String message){
		
		
		Iterator<DataTaskReport> iter = der.reports.iterator();
		while (iter.hasNext()){
			DataTaskReport report = iter.next();
			if (title.equals(report.getAsString(DataTaskReport.REPORT_TITLE))){
				if (report.hasSummary(message)){
						//found expected results
						return;
				}
			}
		}
		
		System.out.println("expect: "+ title+"---"+message);
		System.out.println("found: " + ToolSafe.printCollectionToString(der.reports));
		//System.out.println(SwutilEvaluationReport.toXml(reports));
		fail();
	}	
	
	

	
	private void verifyEmptyReport(DataTaskResult dataEvaluationResult){
		Iterator<DataTaskReport> iter = dataEvaluationResult.reports.iterator();
		while (iter.hasNext()){
			DataTaskReport report = iter.next();
			if (!report.isClean()){
				System.out.println(report.toXml());
				fail();
			}
		}		
	}
		
}

package sw4j.rdf.load;

import static org.junit.Assert.fail;

import java.io.File;
import org.junit.Test;

import sw4j.rdf.load.TaskParseRdf;
import sw4j.rdf.load.ToolParseRdfJena;
import sw4j.rdf.load.ToolParseRdfaXslt;
import sw4j.rdf.util.ToolJena;
import sw4j.task.load.TaskLoad;


public class RdfParserTest {


	@Test
	public void test_rdfa_parse(){
		String [][] aryURL = new String [][]{
//				  {	"http://tw.rpi.edu/portal/News","1"},		//
				  {"http://data-gov.tw.rpi.edu/wiki/Dataset_92","5"},


		};
		test_rdf_rdfa_parse(aryURL);
	}
	
	@Test
	public void test_gzip() {
		String [][] aryURL = new String [][]{
				//{"/work/iw/swa/swoogle3cache/2005/01/24/110080_nt","29"},
				{"http://tw.rpi.edu/2008/05/foaf.rdf","156"},
				{"http://tw.rpi.edu/2008/05/foaf.rdf.gz","156"},
		};
		
		test_rdf_rdfa_parse(aryURL);		
	}
	
	@Test
	public	void test_rdf_misc(){ 
		String [][] aryURL = new String [][]{
				//{"/work/iw/swa/output/history/e/e7/ee7b89b808241ab4d0954c99861df4147dbdcfde_00062","22"},
				 // { "/work/iw/swa/swoogle3cache/2005/06/01/262253","22"},
				//{"http://doapspace.org/doap/sf/beaconcache","45"},				
				  {	"http://inference-web.org/proofs/tptp/Solutions/LAT/LAT195-1/SOS---2.0/answer.owl","6997"},		//[-,+] embedded rdf in html comment section
				  {	"files/parse_test/swa_1207509187343_8783_index.n3","8"},		//[-,+] embedded rdf in html comment section
				  {	"files/parse_test/task_20080405_1.txt_result.n3","21"},		//[-,+] embedded rdf in html comment section
		};
		test_rdf_rdfa_parse(aryURL);		
	}

	@Test
	public	void test_rdf_in_pdf(){ 
		String [][] aryURL = new String [][]{
				  {	"http://www.cs.rpi.edu/~dingl/temp/demo.pdf","2"},		//[-,+] embedded rdf in html comment section;  strict mode 2
		};
		test_rdf_rdfa_parse(aryURL);		
	}
	
	@Test
	public void test_rdfa_xslt(){
		ToolParseRdfaXslt.debug =true;
		//  http://www.w3.org/2006/07/SWD/RDFa/testsuite/
		String [][] aryURL = new String [][]{
				{ "http://inference-web.org/proofs/", null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0001.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0001.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0002.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0002.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0003.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0003.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0004.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0004.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0005.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0005.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0006.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0006.xhtml", "2"},
		/*		{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0007.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0007.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0008.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0008.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0009.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0009.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0010.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0010.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0011.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0011.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0012.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0012.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0013.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0013.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0014.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0014.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0015.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0015.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0016.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0016.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0017.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0017.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0018.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0018.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0019.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0019.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0020.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0020.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0021.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0021.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0022.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0022.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0023.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0023.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0024.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0024.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0025.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0025.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0026.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0026.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0027.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0027.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0028.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0028.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0029.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0029.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0030.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0030.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0031.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0031.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0032.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0032.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0033.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0033.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0034.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0034.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0035.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0035.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0036.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0036.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0037.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0037.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0038.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0038.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0039.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0039.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0040.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0040.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0041.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0041.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0042.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0042.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0043.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0043.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0044.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0044.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0045.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0045.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0046.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0046.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0047.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0047.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0048.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0048.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0049.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0049.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0050.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0050.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0051.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0051.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0052.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0052.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0053.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0053.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0054.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0054.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0055.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0055.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0056.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0056.xhtml", "4"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0057.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0057.xhtml", "5"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0058.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0058.xhtml", "7"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0059.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0059.xhtml", "8"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0060.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0060.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0061.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0061.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0062.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0062.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0063.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0063.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0064.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0064.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0065.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0065.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0066.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0066.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0067.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0067.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0068.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0068.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0069.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0069.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0070.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0070.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0071.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0071.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0072.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0072.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0073.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0073.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0074.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0074.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0075.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0075.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0076.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0076.xhtml", "23"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0077.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0077.xhtml", "23"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0078.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0078.xhtml", "7"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0079.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0079.xhtml", "4"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0080.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0080.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0081.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0081.xhtml", "7"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0082.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0082.xhtml", "10"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0083.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0083.xhtml", "7"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0084.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0084.xhtml", "10"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0085.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0085.xhtml", "9"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0086.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0086.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0087.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0087.xhtml", "23"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0088.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0088.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0089.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0089.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0090.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0090.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0091.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0091.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0092.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0092.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0093.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0093.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0094.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0094.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0095.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0095.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0096.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0096.xhtml", "3"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0097.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0097.xhtml", "4"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0098.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0098.xhtml", "2"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0099.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0099.xhtml", "1"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0100.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0100.xhtml", "0"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0101.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0101.xhtml", "0"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0102.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0102.xhtml", "0"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0103.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/0103.xhtml", "0"},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/1001.sparql",null},
				{ "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/1001.xhtml", "6"},
			*/	
		};
		test_rdf_rdfa_parse(aryURL);
	}
	

	@Test
	public void test_rdf_parse(){
		String [][] aryURL = new String [][]{
				  {	"http://creativecommons.org/licenses/by/2.0/","6"},		//[-,+] embedded rdf in html comment section

				  { "http://ilrt.org/discovery/2001/06/schemas/swws/index.rdf", "19"},	//embed

				{ "http://starsandgarters.blogs.com", null}, //html
				{ "http://dankoleary.squarespace.com/danblog/rdf.xml", null}, //html
				{  "http://rdfweb.org/mt/foaflog/archives/2004/02/12/20.07.32/", null},  //html
				//{ "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl", null}, // robots
				{  "http://www.librarian.net/index.rdf", null}, // 404

				{"http://www.cs.rpi.edu/~dingl/foaf.rdf", "156"},	//109
				{  "http://inference-web.org/2.0/pml-provenance.owl#", "505"}, //501
				{ "http://alteree.hardcore.lt/rdql/owl/test.owl", "508"},  //509
				{ "http://www.daml.org/services/daml-s/2001/10/Time.daml", "34"},	// 22 rx;	non strict mode 37
 
				{ "http://dig.csail.mit.edu/TAMI/2008/12/AIR/air.ttl", "115"},
				{ "http://www.daml.org/2003/06/ruletests/translation-4.n3", "4"},
				{  "http://eulersharp.sourceforge.net/temp/sethC.n3", "1"},
				{ "http://www.daml.org/2003/06/ruletests/translation-3.n3", "23"},	// 22 n3
				{ "http://www.w3.org/2001/03swell/cp.n3", "16"}, // 22 n3
				
				{ "http://www.w3.org/2000/10/swap/model.n3", null},		// forall
				//	{ "http://www.daml.org/2003/02/fips55/PA.owl", "87304"},  //6M

					{ "http://eulersharp.sourceforge.net/2003/03swap/rdfs-rules.n3", "44"},


		};
		test_rdf_rdfa_parse(aryURL);
	}
	
	
	private void test_rdf_rdfa_parse(String [][] aryURL){ 

		try {
			ToolParseRdfJena.debug=true;
			for (int i=0; i<aryURL.length; i++){
				String szURL = aryURL[i][0];
				TaskLoad data_crawl = TaskLoad.load(szURL);
				
				//System.out.println(data_crawl.getContent());
				TaskParseRdf data_rdfparse = TaskParseRdf.parse(data_crawl);
				if (null != aryURL[i][1]){
					if (data_rdfparse==null){
						System.out.println(aryURL[i][0]);
						System.out.println("cannot connect");
						
						System.out.println("expected: "+ aryURL[i][1]);
						  fail();
					}

					if (!data_rdfparse.hasModel()){
						System.out.println(aryURL[i][0]);
						System.out.println("not rdf");
						System.out.println(data_rdfparse.getReport());
						System.out.println("expected: "+ aryURL[i][1]);
						  fail();
					}

					//					data_rdfparse.getModel().write(new FileWriter("test1.rdf"));
					if (!ToolJena.printModelToFile(data_rdfparse.getModel(), new File("output/rdfparse/test.rdf")))
						fail();
					
					if (!aryURL[i][1].equals(""+data_rdfparse.getModel().size())){
						System.out.println(aryURL[i][0]);
						System.out.println("bad model");
						System.out.println(data_rdfparse.getReport());
						System.out.println("expected: "+ aryURL[i][1]);
						System.out.println("found: "+ data_rdfparse.getModel().size());
						fail();
					}
						
				}else{
					

					if (null!=data_rdfparse && data_rdfparse.hasModel()){
						System.out.println(aryURL[i][0]);
						System.out.println("should not be rdf");
						System.out.println("expected: "+ aryURL[i][1]);
						  fail();
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
		
}

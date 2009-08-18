package sw4j.task.load;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import sw4j.task.load.DataTaskLoadHttp;
import sw4j.task.load.ToolLoadHttp;


public class ToolLoaderHttpTest {

	@Test
	public void test_gzip() {
		String [] aryURL = new String []{
			"http://tw.rpi.edu/2008/05/foaf.rdf.gz",
			"http://tw.rpi.edu/2008/05/foaf.rdf",
		};
		
		for (int i=0; i<aryURL.length; i++){
			  String szURL = aryURL[i];
			  long current = System.currentTimeMillis();
			  DataTaskLoadHttp task = ToolLoadHttp.loadUrl(szURL);
			 // assertTrue(task.m_bHttpRedirected);
			  System.out.println(task.m_szRedirectedURL);
			  task.print();			
			  System.out.println(System.currentTimeMillis()-current);
		}
	}
	
	@Test
	public void test_crawl_redirect() {
		  String szURL;
		  szURL = "http://purl.org/dc/elements/1.1/";
		  szURL = "http://swoogle.umbc.edu/swooglebot.html";
		  szURL = "http://iw.rpi.edu/projects/tami/s9/s9-policy.n3";

		  DataTaskLoadHttp task = ToolLoadHttp.loadUrl(szURL);
		 // assertTrue(task.m_bHttpRedirected);
		  System.out.println(task.m_szRedirectedURL);
		  task.print();
	}
	
	@Test
	public void test_encoding(){
    	TreeMap<String,String> map_url_encoding = new TreeMap<String,String>();
//		map_url_encoding.put("http://eb2.cs.umbc.edu/swoogle/test/test-iso.txt","UTF-8"); // this is because eb2 publishes its content with UTF-8 in content-type field
//		map_url_encoding.put("http://eb2.cs.umbc.edu/swoogle/test/test-utf8.txt","UTF-8");
//		map_url_encoding.put("http://eb2.cs.umbc.edu/swoogle/test/test-utf16le.txt","UTF-16LE");
//		map_url_encoding.put("http://eb2.cs.umbc.edu/swoogle/test/test-utf16be.txt","UTF-16BE");
		
		map_url_encoding.put("http://windtear.net/index.rdf","GB2312");
//    	map_url_encoding.put("http://www.cs.umbc.edu/~dingli1/foaf.rdf","UTF-8");
//    	map_url_encoding.put("http://www.orablogs.com/mt-bin/mt-comments271276.cgi?entry_id=536","ISO-8859-1");

    	map_url_encoding.put("http://blogs.linux.ie/fuzzbucket/2004/08/15/the-fuzzycam/","UTF-8");	// javamail failed at this url
    	

	//	map_url_encoding.put("http://dannyayers.com/2003/12/ibis.n3","ISO-8859-1");//"UTF-8");
		map_url_encoding.put("http://ontology.ihmc.us/Actor.owl","ISO-8859-1");
		map_url_encoding.put("http://mogatu.umbc.edu/ont/2004/01/BDI.owl","ISO-8859-1");
		map_url_encoding.put("http://tinysofa.iszerviz.hu/releases/server-1.0/i586/tinysofa/rdfs/resources/cyrus-sasl-md5.rdf","ISO-8859-1");
		map_url_encoding.put("http://joi.ito.com/jp/index.xml","UTF-8");	// XML, UTF-8 
		map_url_encoding.put("http://otb2.at.infoseek.co.jp/db.xml","UTF-8");
		//map_url_encoding.put("http://kwark.yi.org/Gfx/gfx/2003/2003Week17/dscn9628.Sprang-Capelle.jpg?tmpl=image-foaf","ISO-8859-1");
		map_url_encoding.put("http://purl.org/dc/elements/1.1/","UTF-8");
		map_url_encoding.put("http://www.cnnic.net.cn/html/Dir/2005/03/03/2788.htm","ISO-8859-1");  //
		map_url_encoding.put("http://www.daml.org/2001/06/ontoweb-daml/locations.n3","ISO-8859-1");
		map_url_encoding.put("http://www.informatik.uni-trier.de/~ley/db/indices/a-tree/m/Ma:Fan=Yuan.html","UTF-8");    	
    	
		Iterator<Map.Entry<String,String>>iter = map_url_encoding.entrySet().iterator();
		  while (iter.hasNext()){
			  Map.Entry<String,String> entry = (Map.Entry<String,String>)iter.next();
			  String szURL =entry.getKey().toString();
			  String szEncoding = entry.getValue().toString();
			  System.out.println("//+++++++++++++++++++++++++++++");
			  System.out.println(szURL);
			  
			  DataTaskLoadHttp task = ToolLoadHttp.loadUrl(szURL);
			  
			  if (task.isLoadSucceed()){
				  if (!szEncoding.equalsIgnoreCase(task.m_content_charset)){
					  System.out.println("expect "+ szEncoding +" but found "+ task.m_content_charset);
					  System.out.println(task.m_conn.getHeaderFields());
					  fail();
				  }
			  }else{
				 // fail();
				  System.out.println(task.getState());
			  }
		  }
	}
	
	
}

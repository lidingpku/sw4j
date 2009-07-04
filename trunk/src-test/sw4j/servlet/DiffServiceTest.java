package sw4j.servlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

public class DiffServiceTest {

	@Test
	public void test1() throws IOException{
		DiffService svc = new DiffService();
		svc.szCur = "http://data-gov.tw.rpi.edu/catalog/2009/07-03-today.rss";
		svc.szPrev = "http://data-gov.tw.rpi.edu/catalog/2009/06-24-today.rss";
		
		DataServeletResponse ret = svc.run();
		
		System.out.println(ret.getMimeType());
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}
	
	@Test
	public void test2() throws IOException{
		DiffService svc = new DiffService();
		svc.szPrev = "http://data-gov.tw.rpi.edu/catalog/2009/07-03-today.rss";
		svc.szCur = "http://data-gov.tw.rpi.edu/catalog/2009/06-24-today.rss";
		
		DataServeletResponse ret = svc.run();
		
		System.out.println(ret.getMimeType());
		ret.output(new PrintWriter(System.out));
		if (ret.m_model_content==null)
			fail("URL should be succeed");		
	}
}

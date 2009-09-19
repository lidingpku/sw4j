package sw4j.util;
import static org.junit.Assert.fail;

import org.junit.Test;


public class ToolStringTest {
	@Test
	public void test_xml(){
		ToolString.debug=false;
		{
			String [] aryText = new String[]{
					"http://tw.rpi.edu",
					"http://tw.rpi.edu C^C",
			};
			for (String text: aryText){
				String ret=null;
				if (text.equals((ret=ToolString.protectSpecialCharactersForXml(text)))){
					System.out.println(ret);
				}else{
					fail("no xml, should not change");
				}
			}
			
		}
		
		{
			String [] aryText = new String[]{
					"http://tw.rpi.edu français",
					"<a href=\"http://tw2.tw.rpi.edu\">http://tw2.tw.rpi.edu</a>",
			};
			for (String text: aryText){
				String ret=null;
				if (!text.equals((ret=ToolString.protectSpecialCharactersForXml(text)))){
					System.out.println(ret);
				}else{
					fail("xml stuff should be encoded");
				}
			}
			
		}
		
	}
	
	@Test
	public void test_hyperlink(){
		ToolString.debug=false;
		{
			String [] aryText = new String[]{
					"http://tw.rpi.edu",
					"<a href=\"http://tw2.tw.rpi.edu\">http://tw2.tw.rpi.edu</a>",
			};
			for (String text: aryText){
				String ret=null;
				if (null!=(ret=ToolString.parse_hyperlink(text))){
					System.out.println(ret);
				}else{
					fail("should parse successfully");
				}
			}
			
		}
		
		{
			String [] aryText = new String[]{
					"afshttp://tw.rpi.edu",
					"http://tw.rpi.edu adsa",
					"<a href=\"http://tw2.tw.rpi.edu\">http://tw.rpi.edu</a>",
			};
			for (String text: aryText){
				String ret=null;
				if (null!=(ret=ToolString.parse_hyperlink(text))){
					System.out.println(ret);
					fail("should parse failed");
				}else{
					System.out.println("right, no match!");					
				}
			}
			
		}
	}
}

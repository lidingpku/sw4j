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
package sw4j.util.web;
/**
 * detect encodings 
 *
 * @author Li Ding
 */
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.StringTokenizer;

import javax.mail.internet.ContentType;

//import org.apache.regexp.RE;
//import org.apache.regexp.RESyntaxException;





public class AgentEncodingDetector {

	public String m_HTTPcharset = null;
	
    public String extractHTTPcharset(URLConnection conn){
        //determine HTTP charset

        m_HTTPcharset = null;
        
        if (null==conn)
        	return null;
        
        String contentT = conn.getContentType();
        if (null == contentT)
        	return null;

        // remove some obvious errors   e.g. ; before charset;
    	contentT=contentT.trim();
    	while (contentT.startsWith(";"))
    		contentT =contentT.substring(1).trim();

    	// use java mail function to extract charset
        ContentType contentType = null;
        try {
        	
            contentType = new ContentType( contentT );
            m_HTTPcharset = contentType.getParameter("charset");
        } catch (javax.mail.internet.ParseException e) {
        	System.out.println(contentT);
        }

        if (m_HTTPcharset != null)
        	m_HTTPcharset = m_HTTPcharset.toUpperCase();
        
        return m_HTTPcharset;
    }
    
    public String m_APPFcharset = null;  // 'charset' according to XML APP. F
    public String m_szIgnored=null;
    public int m_ignoreBytes=0;
    
    /**
     * detect file encoding by checking the first serveral byte of the file
     * this is useful for XML parsing.
     * http://www.w3.org/TR/REC-xml/#sec-guessing-no-ext-info
     * @param bis
     * @return
     * @throws IOException caused by reading/reseting the inputstream
     */
    public String extractAPPcharset(BufferedInputStream bis) throws IOException{
        m_APPFcharset = null; 
        m_szIgnored =null;
        m_ignoreBytes = 0;

        // read start of file as bytes
        bis.mark(200); // mark start so that we can get back to it
        String s = "";
        int c;
        int numRead = 0;
        while ((c = bis.read()) != -1) {
            s += (char)c;
            if (numRead++ >= 195) break;
        }


        //////////////////////////////////////////////////////////////////////////////////
        // check encoding by parse first 195 bytes
        //////////////////////////////////////////////////////////////////////////////////
        
        if (s.startsWith("\u00FE\u00FF")) {
            m_APPFcharset = "UTF-16BE";
            m_ignoreBytes = 2;
            m_szIgnored = "\u00FE\u00FF";
		}
        else if (s.startsWith("\u00FF\u00FE")) {
            m_APPFcharset = "UTF-16LE";
            m_ignoreBytes = 2;
            m_szIgnored = "\u00FF\u00FE";
        }
        else if (s.startsWith("\u00EF\u00BB\u00BF")) {
            m_APPFcharset = "UTF-8";
            m_ignoreBytes = 3;
            m_szIgnored = "\u00EF\u00BB\u00BF";
        }
        else if (s.startsWith("\u0000<\u0000?")) {
            m_APPFcharset = "UTF-16BE";
        }
        else if (s.startsWith("<\u0000?\u0000")) {
            m_APPFcharset = "UTF-16LE";
        }
        else if (s.startsWith("<?xml")) {
            m_APPFcharset = "ISO-8859-1";  //to not loose any bytes 
        }
        else if (s.startsWith("\u004C\u006F\u00A7\u0094")) {
            m_APPFcharset = "CP037";  // EBCDIC
        }
        else {
            m_APPFcharset = "ISO-8859-1";  //to not loose any bytes
        }

        if (m_APPFcharset != null)
        	m_APPFcharset = m_APPFcharset.toUpperCase();
        
        return m_APPFcharset;
    }
    
    
    public String m_XMLcharset =null;
    public String m_szXMLprolog = "";
    public int m_nEndOfXMLprolog =0;
    
    public String extractXMLcharset(String text){
    	m_XMLcharset =null;
    	if (null==m_APPFcharset)
    		return null;
    	
    	// remove comments before <?xml ...
    	int index_xml_begin =-1;
    	int index_comment_begin= 0;  // ^<!--
    	int index_comment_end =0;   // -->^
    	String pad="";
    	while (true) {
    		index_xml_begin=text.indexOf("<?xml", index_comment_end);
    		if (index_xml_begin<0)
    			return null;
    		
    		index_comment_begin=text.indexOf("<!--", index_comment_end);
    		if (index_comment_begin<0 || index_comment_begin>index_xml_begin)
    			break;
    		else {//if (index_xml_begin>index_comment_begin)
    			pad =text.substring(index_comment_end,index_comment_begin);
    			
    			// the padding between comments can only be whitespace
    			if (pad.length()>0 && !pad.matches("\\s+"))
    				return null;

    			// look for the end of comment and redo
    			index_comment_end = text.indexOf("-->", index_comment_begin+4);
    			if (index_comment_end<0){
    				// [bad xml] comment not closed
    				System.out.println("[WARNING]: comment tag mismatch, --> expected ");
    				break;
    			}else{
    				index_comment_end+=3;
    			}
    		}
    	}
    	
    	// extract XML prolog
    	m_szXMLprolog="";
    	if (index_xml_begin>=0){
    		int index_xml_end = text.indexOf("?>",index_xml_begin);
    		if (index_xml_end<0)
    			return null;
    		else{
    			m_szXMLprolog = text.substring(index_xml_begin, index_xml_end+2);
    			m_nEndOfXMLprolog = index_xml_end+2;
    		}
    	}

    	// extract encoding from 
        StringTokenizer st = new StringTokenizer(m_szXMLprolog," \t\n\r=\"'");
        while(st.hasMoreTokens()){
	        String token= st.nextToken();
        	if (!token.equalsIgnoreCase("encoding"))
        		continue;
        	
        	m_XMLcharset = st.nextToken();
        	break;
	    }
        
        // validate
        try {
	        if (null==m_XMLcharset){
	        	return null;
	        }else if (m_XMLcharset.length()<3){
	        	System.out.println("too short encoding declaration");
	        	System.out.println (m_XMLcharset);
	        	m_XMLcharset = null;
	        }else if (!Charset.isSupported(m_XMLcharset)) {
	        	System.out.println("unsupported encoding");
	        	System.out.println (m_XMLcharset);
	        	m_XMLcharset = null;
	        }
        }catch (IllegalCharsetNameException e){
        	System.out.println (e.getLocalizedMessage());
        	System.out.println (m_XMLcharset);
        	m_XMLcharset = null;
        }
        
    	/*
        RE r;
        try {
            r = new RE("<\\?xml[ \\t\\n\\r]+version[ \\t\\n\\r]?=[ \\t\\n\\r]?(['\"])([a-zA-Z0-9_:]|\\.|-)+\\1[ \\t\\n\\r]+encoding[ \\t\\n\\r]?=[ \\t\\n\\r]?(['\"])([A-Za-z]([A-Za-z0-9._]|-)*)\\3");
        } catch (RESyntaxException e) {
            //throw new MyHttpException("Wrong regular expression syntax.");
        	e.printStackTrace();
        	return null;
        }
        // r.setMatchFlags(MATCH_NORMAL | MATCH_SINGLELINE); 
        String m_XMLcharset = null;
        if (r.match(text) && r.getParenStart(0)==0)
            m_XMLcharset = r.getParen(4);
    	*/

        if (m_XMLcharset != null)
            m_XMLcharset = m_XMLcharset.toUpperCase();     	
        return m_XMLcharset;
    }
    

    String m_FINALcharset = null;
    public String extractFINALcharset(){
    	m_FINALcharset =null;
    	
        if (null == m_XMLcharset){
            m_FINALcharset = m_HTTPcharset; 
        }else if (null == m_HTTPcharset){
            m_FINALcharset = m_XMLcharset; 
        }else if (m_HTTPcharset.equals(m_XMLcharset)){
            m_FINALcharset = m_XMLcharset; 
        }else {
        	System.out.println("[WARNING]: Charset conflict: Content-Type: "
                    + m_HTTPcharset + ". XML encoding: " +  m_XMLcharset + ".");
        	
            m_FINALcharset = m_HTTPcharset; 
        }

        if (null==m_APPFcharset){
        	// do nothing
        	
        }else if (null == m_FINALcharset){
        	m_FINALcharset = m_APPFcharset;  // try the detected charset
        	
        }else if (m_FINALcharset.equals(m_APPFcharset)){
        	// do nothing

        }else if ( m_FINALcharset.equals("UTF-16")){
        	//http://en.wikipedia.org/wiki/UTF-16
            // The UTF-16 encoding scheme mandates that the byte order must be declared by prepending a Byte Order Mark before the first serialized character.
            if (m_ignoreBytes == 2)
                m_FINALcharset = m_APPFcharset;  // use correct endianness
            else{
            	System.out.println("[WARNING]: Illegal XML, UTF-16 without BOM.");
                m_FINALcharset =null;  //throw new MyHttpException("Illegal XML: UTF-16 without BOM.");
            }
            
        }else if ( m_ignoreBytes>0 ){
        	System.out.println("[WARNING]: Charset conflict: final-charset: "
                    + m_FINALcharset + ". detected charset: " +  m_APPFcharset + ".");
        	m_FINALcharset = m_APPFcharset;  // change final charset to the right encoding
        }else {
        	// do nothing because we can't get more heuritics to 
        	
        }
        return m_FINALcharset;
    }    

}

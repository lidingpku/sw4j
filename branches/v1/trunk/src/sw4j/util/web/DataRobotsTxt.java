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

import java.io.*;
import java.util.*;
import java.net.*;

import org.apache.log4j.Logger;

import sw4j.util.ToolSafe;


/**
 * parse procedure are following this document
 * http://www.robotstxt.org/wc/norobots-rfc.html

 * @author Li Ding
 *
 * TODO  we need to handle escaped characters
 */
public class DataRobotsTxt {
	private static Logger getLogger() {
		return Logger.getLogger(DataRobotsTxt.class);
	}

    static final int STATE_NON_EXIST =-1;	// the robots.txt does not exist
    static final int STATE_READY=0;	
    static final int STATE_USER_AGENT = 1;
    static final int STATE_CMD= 2;
    static final int STATE_SUCCESS= 100;

    static final int MODE_ANY=0;
    static final int MODE_THIS_CRAWLER= 1;
    
    private boolean matchPrefixIngoreCase(String line, String prefix){
    	if (line.length()<prefix.length())
    		return false;
    	return line.substring(0,prefix.length()).equalsIgnoreCase(prefix);
    }
    
    int m_nState = STATE_READY;
    
    int m_nMode;
    HashSet<String> m_setPath = new HashSet<String>();
    ArrayList<PathCmd> m_aryPathCmd = new ArrayList<PathCmd>();
	
	public static final int PATH_NOT_MATCH =0; 
	public static final int PATH_ALLOW =1; 
	public static final int PATH_DISALLOW =2; 
	
    public class PathCmd{
    	
    	public boolean disallow;
    	public String path;
    	
    	public PathCmd(boolean disallow, String p){
    		this.path=p;
    		this.disallow= disallow;
    	}
    	
    	public int testDisallow(String p){
    		if (disallow&&(this.path.length()==0))
    			return PATH_ALLOW;
    		
    		if (p.startsWith(this.path)){
    			if (disallow)
    				return PATH_DISALLOW;
    			else
    				return PATH_ALLOW;
    		}else
    			return PATH_NOT_MATCH;
    	}
    	
    	public void print( PrintWriter out){
    		if (disallow)
    			out.print("Disallow: ");
    		else
    			out.print("Allow: ");
    		out.println(path);
    	}
    }
    
    
    private void processLine(String line, String robotName){
    	//preprocess, remove blank space, empty line and comments
    	int index = line.indexOf("#");
    	if (index>=0)
    		line = line.substring(0,index);
    	line =line.trim();
    	if (line.length()==0)
    		return;
    	
    	// process state
    	String expect;
    	switch(m_nState){
    	case STATE_READY:
    		// expecting for user-agent line
    		expect = "user-agent:";
    		if (matchPrefixIngoreCase(line,expect)){
    			String temp = line.substring(expect.length()).trim().toLowerCase();
    			
    			if (m_nMode==MODE_THIS_CRAWLER){
    	    		if (temp.equals(robotName))
    	    			m_nState = STATE_USER_AGENT;
    			}else if (m_nMode==MODE_ANY){
    	    		if (temp.equals("*"))
    	    			m_nState = STATE_USER_AGENT;
    			}
	    		//else: other constraints will not apply to swooglebot
    		}//else: not start with user-agent:
    		break;
    	
    	case STATE_USER_AGENT:
    		expect = "user-agent:";
    		if (matchPrefixIngoreCase(line,expect)){
    			return; // skip the following user-agents in the same category
    		}
    		
    		// no break here
    	
    	case STATE_CMD:
    		expect = "user-agent:";
    		if (matchPrefixIngoreCase(line,expect)){
    			m_nState = STATE_SUCCESS;
    			return;
    		}

    		m_nState = STATE_CMD;
    		// expecting disallow
    		expect = "disallow:";
    		if (matchPrefixIngoreCase(line,expect)){
    			String temp = line.substring(expect.length()).trim();
    			temp = ToolWeb.decodeURL(temp);
    			if (!m_setPath.contains(temp)){
    				m_setPath.add(temp);	//avoid duplicate
    				m_aryPathCmd.add ( new PathCmd(true, temp ) );
    			}
    			return;
    		}

    		// expecting allow
    		expect = "allow:";
    		if (matchPrefixIngoreCase(line,expect)){
    			String temp = line.substring(expect.length()).trim();
    			temp = ToolWeb.decodeURL(temp);
    			temp = ToolWeb.decodeURL(temp);
    			if (!m_setPath.contains(temp)){
    				m_setPath.add(temp);	//avoid duplicate
    				m_aryPathCmd.add ( new PathCmd(false, temp ) );
    			}
    			return;
    		}
    		
    		break;
    	
    	case STATE_SUCCESS:
    		break;	//do nothing
    	}
    }
    
    
	public static DataRobotsTxt parse(String szText, String robotName){
		DataRobotsTxt rt = new DataRobotsTxt();
		if (ToolSafe.isEmpty(szText)){
			rt.m_nState = STATE_NON_EXIST;
			return rt;
		}

		try {
			// setup parsing mode
			if (szText.indexOf(robotName)>=0)
				rt.m_nMode = MODE_THIS_CRAWLER;
			else
				rt.m_nMode = MODE_ANY;
			
			BufferedReader bufReader = new BufferedReader(new StringReader(szText)); 
		    String line = null;
		    while ((line=bufReader.readLine()) != null){
		    	rt.processLine(line,robotName);
		    }
		    
		    if (rt.m_aryPathCmd.size()>0)
		    	return rt;
		    
		}catch (IOException e){
			getLogger().info(e.getMessage());
		}

		rt.m_nState = STATE_NON_EXIST;
		return rt;		
	}
	
	protected void print(PrintWriter out){
		Iterator<PathCmd> iter = this.m_aryPathCmd.iterator();
		while(iter.hasNext()){
			PathCmd entry = iter.next();
			entry.print(out);
		}
	}
	
	public String toString(){
		StringWriter sw = new StringWriter();
		print(new PrintWriter(sw));
		return sw.toString();
	}

	
	public boolean testDisallow(URI uri) {
		// since no robots.txt exist, all URIs are allowed
		if (STATE_NON_EXIST == this.m_nState)
			return false;
		
		//mach path command in sequence
		Iterator<PathCmd> iter = this.m_aryPathCmd.iterator();
		while(iter.hasNext()){
			PathCmd entry = iter.next();
			int ret = PATH_DISALLOW;
			// ret = entry.testDisallow(uri.getPath());
			try{
				ret = entry.testDisallow(uri.toURL().getFile());
			} catch (Exception e){
			}
			if (ret ==PATH_ALLOW)
				return false;	
			else if (ret ==PATH_DISALLOW)
				return true;
		}
		return false; //by default assume allow
	}
	
	public static void main(String[] args) {

	}
}

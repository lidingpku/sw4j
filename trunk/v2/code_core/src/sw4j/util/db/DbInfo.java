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


package sw4j.util.db;

/**
 *
 * Store database connection information
 * @author  Li Ding
 */
public class DbInfo {

    protected String host;
    protected String user;
    protected String passwd;
    protected String db;
    protected String jdbc_driver = "com.mysql.jdbc.Driver";

	public ToolDBAccess newDB (){
		return new ToolDBAccess(this);
	}
	
	ToolDBAccess m_ToolDBAccess =null; 
	public ToolDBAccess getDB (){
		if (null ==  m_ToolDBAccess)
			m_ToolDBAccess = newDB();
		return m_ToolDBAccess;
	}
	
	
	public DbInfo(String szHost, String szUser, String szPass, String szDB){
		db = szDB;
		host = szHost+szDB;
		user = szUser;
		passwd = szPass;
	}
	
	public DbInfo(String szHost, String szUser, String szPass, String szDB, String szJdbcDriver){
		db = szDB;
		host = szHost+szDB;
		user = szUser;
		passwd = szPass;
		jdbc_driver = szJdbcDriver;
	}

    public void print(){
        System.out.println( host );
        System.out.println( user );
        System.out.println( passwd );
    }

}

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
/*
 *
 * Created on April 8, 2004, 5:14 PM
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;
import java.text.SimpleDateFormat;
/**
 *
 * @author  Li Ding
 */
public class ToolDBAccess {
    private DbInfo m_info = null;
    
    public static boolean debug = false;
    private int cOperation = 0;
    private Connection conn = null;
    ///////////////////////////////////////
    
    /** Creates a new instance of ToolDBIO */
    public ToolDBAccess(DbInfo info) {
        m_info = info;
        register();
    }

    public static String getDateTime(){
        /* The DATETIME type is used when you need values that contain both
            date and time information. MySQL retrieves and displays DATETIME 
            values in 'YYYY-MM-DD HH:MM:SS' format. The supported range is
            '1000-01-01 00:00:00' to '9999-12-31 23:59:59'. (``Supported''
            means that although earlier values might work, there is no 
         guarantee that they will.) 
         **/
        long intTime = System.currentTimeMillis();
        //Date date = new Date(intTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(new Date(intTime));

    }

    private void reconnect()throws SQLException{
        if (null!= conn){
            if (debug)
            	System.out.println( "db reconnect");
            conn.close();
            conn = null;
			System.gc();
        }
        
        conn = DriverManager.getConnection(m_info.host+"?useUnicode=true&characterEncoding=UTF-8", m_info.user, m_info.passwd);
    }
    /**Register MySQL driver
     */
    public void register(){
        try {
                Class.forName( m_info.jdbc_driver ).newInstance();
        } catch (InstantiationException e) {
                e.printStackTrace();
        } catch (IllegalAccessException e) {
                e.printStackTrace();
        } catch (ClassNotFoundException e) {
                e.printStackTrace();
        }
    }

    private Statement operate(String szSQL, int trial)throws SQLException{
       if (debug)
            System.out.println( "[SQL] "+szSQL);

		Statement stmt= createStmt();

       // stmt.setMaxRows(10000);
		try {
			stmt.execute(szSQL);
		}catch(com.mysql.jdbc.CommunicationsException e){
	       if (debug)
	       		System.out.println( "[error] "+e);	
      	   
	       conn = null;	
	       if (trial>0){
	       		return operate(szSQL, trial-1);
	       }
    	}        
        /* Note: By default a Connection object is in auto-commit mode, 
           which means that it automatically commits changes after 
           executing each statement. If auto-commit mode has been 
           disabled, the method commit must be called explicitly in order 
           to commit changes; otherwise, database changes will not be saved. 
           */

        if (debug)
            System.out.println( "[SQL SUCCESS] ");
        return stmt;
    }
    
    public ResultSet operateRead(String szSQL, int limit){
        try{
            if (limit>0){
                cOperation+=limit;
                szSQL += " LIMIT "+ limit;
            }
            else
                cOperation++;
            
            Statement stmt = operate( szSQL,1 );
            ResultSet rs = stmt.getResultSet();
            if (null == rs)
                return null;

            if (!rs.first()){
                rs.close();
                return null;
            }        

            return rs;
        }catch (SQLException e){
            System.out.println("operateRead");
            e.printStackTrace();
            return null;
        }
    }
    
    public void operateWrite(String szSQL) throws SQLException{
        cOperation++;

        if (debug)
        	System.out.println(szSQL);
        else{
        	Statement stmt = operate( szSQL,1 );
        	stmt.close();
        }
    }

	public Statement createStmt()throws SQLException{

		if (cOperation > 500){
			reconnect();
			cOperation = 0;
		}
        
		if ((null == conn)||conn.isClosed())
			conn = DriverManager.getConnection(m_info.host+"?useUnicode=true&characterEncoding=UTF-8", m_info.user, m_info.passwd);
        
		if (null == conn){
			System.out.println("Fatal error: conn == null");
			return null;
		}

		Statement stmt = conn.createStatement();
		if (null == stmt){
			System.out.println("Fatal error: stmt == null");
			return null;
		}
		return stmt;
	}

/*
    public PreparedStatement prepareStatement(String szSQL) throws SQLException{
        cOperation++;
        if (cOperation > 1000){
            reconnect();
            cOperation = 0;
        }
        
        if (null == conn)
            conn = DriverManager.getConnection(m_info.host, m_info.user, m_info.passwd);

        if (null == conn){
            System.out.println("Fatal error: conn == null");
            return null;
        }
        
        return conn.prepareStatement(szSQL);
        
    }
*/
	
	
	
    public void test(){
    	
        String sql = null; // The SQL statement to execute
        try {
            sql = "show tables";
            
            ResultSet rs = this.operateRead(sql,-1);
            
            if (!rs.first())
                System.out.println("no results");
            else
                while(!rs.isAfterLast()){
                    System.out.print( rs.getString(1));
                    System.out.print( "\t");
                    //System.out.print( results.getInt(1));
                    rs.next();
                    System.out.println();
                }
        } catch (SQLException e1) {
                e1.printStackTrace();
        }
    }
}

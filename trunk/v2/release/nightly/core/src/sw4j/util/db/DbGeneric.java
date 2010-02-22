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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sw4j.util.DataSmartMap;



/**

 * Perform simple database operation, store database connection
 * @author Li Ding
 *
 */
public class DbGeneric extends DbInfo{
	public boolean DEBUG = false;	//if debug, not database write will be executed.
	
	
	protected DbGeneric(String host, String user, String pass, String szDB){
		super(host, user, pass, szDB);
	}

	protected DbGeneric(DbSettings settings, String szDB){
		super(	"jdbc:"
				+ settings.getPropertyByIndex(DbSettings.DB_TYPE)
				+"://"
				+ settings.getPropertyByIndex(DbSettings.DB_HOST)
				+"/", 
				settings.getPropertyByIndex(DbSettings.DB_USER), 
				settings.getPropertyByIndex(DbSettings.DB_PASS), 
				szDB);
	}
	
	   ///////////////////////////////////////////////////////////////
    //  generic operations
    ///////////////////////////////////////////////////////////////

	public boolean genericWrite(String szSQLW){
		try {
			if ((null==szSQLW)||(szSQLW.length()==0)){
				System.out.println("genericWrite warning: empty sql");
				return false;
			}
				
			if (DEBUG)
				System.out.println(szSQLW);
			else
				getDB().operateWrite(szSQLW);
			return true;
		}catch(SQLException e){
			//System.out.println(szSQLW);
			//e.printStackTrace();
			if (e.getMessage().indexOf("Duplicate entry")<0)
				e.printStackTrace();
			return false;
		} 	
	}
	
	public String queryString(String szSQL){
		try {
			return getDB().operateRead(szSQL,-1).getString(1);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		} 	
	}

	public int queryInt(String szSQL){
		try {
			return getDB().operateRead(szSQL,-1).getInt(1);
		}catch(SQLException e){
			e.printStackTrace();
			return 0;
		} 	
	}

	public double queryDouble(String szSQL){
		try {
			return getDB().operateRead(szSQL,-1).getDouble(1);
		}catch(SQLException e){
			e.printStackTrace();
			return 0;
		} 	
	}
	
	private static SimpleDateFormat gSDF = new SimpleDateFormat("yyyyMMddHHmmss");
	public static String getDateTimeString(String szLongValue){
		if (null== szLongValue)
			return null;
		else
			return gSDF.format( new Date(Long.parseLong(szLongValue)));
	}
	
	/*	return value
	 * 	 (i)	bReturnID = ture:   reutrn ID (>0) if success,  0 if failed
	 * 	 (ii)	bReturnID = false:	return 1 if success, 0 if failed		       
	 * 
	 */	
    
	public int genericInsert(  String szTable,
								String szOptWhere,
								DataSmartMap sf,
								boolean bReturnID){
		String szWhere = "";
		String szSQLW  = " INSERT INTO "+ szTable + sf.getSQLInsert();
		int ret = 0;
		if (!genericWrite( szSQLW )){
			// insertion failed
			szWhere = " WHERE " + szOptWhere;	// already exist
		}else
			ret =1;
			
        
		if (bReturnID){
			String szSQL =  "SELECT max(id) FROM " + szTable + szWhere;
			try {
				ResultSet rs = getDB().operateRead( szSQL, -1 );

				if (null!= rs){
					int id = rs.getInt(1);
					rs.close();
					return id;
				}
			}catch (SQLException e){
				System.out.println(szSQL);
				e.printStackTrace();
			}
			return 0;
		}else
			return ret;
	}      

    public void runSQLs(String[] arySQL){
		for (int i=0;i<arySQL.length; i++){
			runSQL(arySQL[i]);
		}
    }

    public void runSQL(String szSQL){
    	if (null==szSQL || szSQL.length()==0)
    		return;
    	
		if (!genericWrite(szSQL)){
			System.out.println("Fatal Error:"+ szSQL);
		}
    }
    
    String m_szSQLbatch="";
    public void runSQLbatch(String szSQL){
    	final int LIMIT =100000;
    	if (null==szSQL || szSQL.length()==0){
    		// no more sqls coming, we need to commit all pooled sql
    		runSQL(m_szSQLbatch);
    		m_szSQLbatch = "";
    		return;
    	}
    	
    	// if input very large, we execute it first
    	if (szSQL.length()>LIMIT){
    		runSQL(szSQL);
    		return;
    	}
    	
    	if (m_szSQLbatch.length()==0)
    		m_szSQLbatch = szSQL;
    	else
    		m_szSQLbatch +=";"+szSQL;
    	
    	if (m_szSQLbatch.length()>LIMIT){
    		runSQL(m_szSQLbatch);
    		m_szSQLbatch="";
    		return;
    	}
    }

    String m_szSQLbatchInsert="";
    public void runSQLbatchInsert(String szTable, DataSmartMap sf){
    	if (null==sf ){
    		// no more sqls coming, we need to commit all pooled sql
    		runSQL(m_szSQLbatchInsert);
    		m_szSQLbatchInsert = "";
    		return;
    	}
    	
    	if (m_szSQLbatchInsert.length()==0){
    		m_szSQLbatchInsert = "INSERT INTO "+szTable+" "+sf.getSQLInsert();
    	}else{
    		m_szSQLbatchInsert += ", ("+sf.getSQLInsertValues()+")"; 
    	}
    	
    	if (m_szSQLbatchInsert.length()>1000){
    		runSQL(m_szSQLbatchInsert);
    		m_szSQLbatchInsert="";
    		return;
    	}
    }

}

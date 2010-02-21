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

import sw4j.util.DataCachedObjectMap;
import sw4j.util.DataSmartMap;

/**
 * access table with caching unique value and premary key
 * @author Li Ding
 *
 */
public abstract class TableCached{
	protected DbGeneric m_ToolDB = null;
	protected String m_szDB = null;
	protected String m_szTable = null;
	protected String m_szIdField = null;
	protected String m_szUniqueField = null;
	protected DataCachedObjectMap<String,Integer> m_cache = null;
	
	abstract public DataSmartMap createDataSmartMap();

	public static final int STATE_FAIL = 10;
	public static final int STATE_CACHE_HIT = 11;
	public static final int STATE_DB_HIT = 12;
	public static final int STATE_DB_INSERTED = 13;
	
	protected int m_nState = STATE_CACHE_HIT;
	public boolean isCacheHit(){
		return m_nState == STATE_CACHE_HIT;
	}
	public boolean isDbHit(){
		return m_nState == STATE_DB_HIT;
	}
	public boolean isDbInserted(){
		return m_nState == STATE_DB_INSERTED;
	}
	
	protected TableCached(DbGeneric toolDB, String szDB, String szTable, String szIdField, String szUniqueField){
		m_ToolDB =toolDB;
		m_szDB = szDB;
		m_szTable = szTable;
		m_szIdField = szIdField;
		m_szUniqueField = szUniqueField;
		m_cache = new DataCachedObjectMap<String,Integer>();
		
	}

	protected TableCached(DbGeneric toolDB, String szDB, String szTable,  String szIdField, String szUniqueField, int cachesize){
		m_ToolDB =toolDB;
		m_szDB = szDB;
		m_szTable = szTable;
		m_szIdField = szIdField;
		m_szUniqueField = szUniqueField;
		m_cache = new DataCachedObjectMap<String,Integer>(cachesize);
	}
	
	protected String genTableName(){
		if (m_szTable.indexOf(".")>0)
			return m_szTable;
		else
			return m_szDB+"."+m_szTable;
	}




	public int findInCache(String key){
		int id= m_cache.get( key );
		if (id>0)
			m_nState = STATE_CACHE_HIT;
		else
			m_nState = STATE_FAIL;
		return id;
	}
	
	protected int findInDB(String key, String szSQL){
		try {
			ResultSet rs = m_ToolDB.getDB().operateRead( szSQL, -1 );

			if (null!= rs){
				int id = rs.getInt(1);
				rs.close();
				
				//cache
				m_cache.put( key, id );
				
				return id;
			}
		}catch (SQLException e){
			System.out.println(szSQL);
			e.printStackTrace();
		}
		
		m_nState = STATE_FAIL;
		return 0;
	}
	
	public int find(String key){
		if ((null ==key)||(key.length()==0))
			return 0;
		
		DataSmartMap sf = new DataSmartMap();
		sf.addStringProperty(m_szUniqueField);
		sf.put(m_szUniqueField,key);
		return find(sf);
	}
	
	public int find(DataSmartMap sf){
		String key = sf.getAsString(m_szUniqueField);
		
		if ((null ==key)||(key.length()==0))
			return 0;	// can not do any thing without specifying key;

		int id;
		id =findInCache(key);
		if (id>0)
			return id;
		
		String szSQL =  "SELECT "+m_szIdField+" FROM " + genTableName() + " WHERE " + sf.getSQLWhere(m_szUniqueField);

		m_nState = STATE_DB_HIT;
		id =findInDB(key, szSQL );
		return id;
		
	}
	
	public int simpleFindInsert(DataSmartMap sf){
		String key = sf.getAsString(m_szUniqueField);
		if ((null ==key)||(key.length()==0))
			return 0;	// can not do any thing without specifying key;

		int id= findInCache( key );
		if (id>0)
			return id;
		
		boolean inserted = simpleInsert(sf);
		
		String szWhere="";
		if (!inserted){
			szWhere = " WHERE " + sf.getSQLWhere(m_szUniqueField);
		}
		String szSQL =  "SELECT max("+m_szIdField+") FROM " +  genTableName() + szWhere;
		m_nState = STATE_DB_HIT;
		return findInDB(key, szSQL);
	}
    
	public boolean simpleInsert(  DataSmartMap sf)
	{
		String key = sf.getAsString(m_szUniqueField);
		
		if ((null ==key)||(key.length()==0))
			return false;	// can not do any thing without specifying key;

		int id= findInCache( key );
		if (id>0){
			return false;	// already in cache, should also be in db
		}
		
		String szSQLW  = "INSERT INTO "+ genTableName() + sf.getSQLInsert();
		
		if (m_ToolDB.genericWrite( szSQLW )){
			m_nState = STATE_DB_INSERTED;
			return true;
		}else{
			// insertion failed,  assert already exist
			m_nState = STATE_DB_HIT;
			return false;
		}
	}      
	
	
	public static void main(String[] args) {
	}
}

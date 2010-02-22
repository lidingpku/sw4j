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


/**
 * access table
 * @author Li Ding
 */
package sw4j.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import sw4j.util.DataCachedObjectMap;
import sw4j.util.DataSmartMap;


/**
 * @author Li Ding
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class DbTable{
	protected DbGeneric m_db = null;
	protected String m_szDB = null;
	protected String m_szTable = null;

	protected DataCachedObjectMap<DataSmartMap,Integer> m_cache = null;
	protected String m_szIdField = null;
	protected Set<String> m_setKeyFields = null;
	protected Set<String> m_setStringFields = null;
	
	public DataSmartMap createDataSmartMap(){
		DataSmartMap sf = new DataSmartMap();
		if (null!= m_setStringFields)
			sf.addStringProperties(m_setStringFields);
		return sf;
	}
	
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
	
	protected DbTable(DbGeneric toolDB, String szDB, String szTable, Set<String> setStringFields, String szIdField, String  keyField){
		this(toolDB, szDB, szTable,setStringFields, szIdField, szIdField, -1);
	}

	protected DbTable(DbGeneric toolDB, String szDB, String szTable, Set<String> setStringFields,  String szIdField, String keyField, int cachesize){
		this(toolDB, szDB, szTable,setStringFields, szIdField, new HashSet<String>(), cachesize);
		this.m_setKeyFields.add(keyField);
	}
	
	protected DbTable(DbGeneric toolDB, String szDB, String szTable, Set<String> setStringFields,  String szIdField, Set<String> setKeyFields, int cachesize){
		m_db =toolDB;
		m_szDB = szDB;
		m_szTable = szTable;
		m_setStringFields = setStringFields;
		m_szIdField = szIdField;
		m_setKeyFields = setKeyFields;
		if (-1== cachesize)
			m_cache = new DataCachedObjectMap<DataSmartMap,Integer>();
		else
			m_cache = new DataCachedObjectMap<DataSmartMap,Integer>(cachesize);		
	}

	protected String genTableName(){
		if (m_szTable.indexOf(".")>0)
			return m_szTable;
		else
			return m_szDB+"."+m_szTable;
	}




	public int findInCache(DataSmartMap sfkey){
		int id= m_cache.get( sfkey );
		if (id>0)
			m_nState = STATE_CACHE_HIT;
		else
			m_nState = STATE_FAIL;
		return id;
	}
	
	protected int findInDB(DataSmartMap sfkey, String szSQL){
		try {
			ResultSet rs = m_db.getDB().operateRead( szSQL, -1 );

			if (null!= rs){
				int id = rs.getInt(1);
				rs.close();
				
				//cache
				m_cache.put( sfkey, id );
				
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
		
		if (this.m_setKeyFields.size()!=1){
			return 0;
		}
		
		DataSmartMap sf = createDataSmartMap();
		sf.put((String)(m_setKeyFields.iterator().next()), key);
		return find(sf);
	}
	
	public int find(DataSmartMap sfkey){
		if (sfkey.getData().isEmpty())
			return 0;	// can not do any thing without specifying key;

		int id;
		id =findInCache(sfkey);
		if (id>0)
			return id;
		
		String szSQL =  "SELECT "+m_szIdField+" FROM " + genTableName() + " WHERE " + sfkey.getSQLWhere(this.m_setKeyFields);

		m_nState = STATE_DB_HIT;
		id =findInDB(sfkey, szSQL );
		return id;
		
	}
	
	public int simpleFindInsert(DataSmartMap sf){

//		String key = sf.getEntry(m_szUniqueField);
//		if ((null ==key)||(key.length()==0))
//			return 0;	// can not do any thing without specifying key;

		int id= findInCache( sf );
		if (id>0)
			return id;
		
		boolean inserted = simpleInsert(sf);
		
		String szWhere="";
		if (!inserted){
			szWhere = " WHERE " + sf.getSQLWhere(this.m_setKeyFields);
		}
		String szSQL =  "SELECT max("+m_szIdField+") FROM " +  genTableName() + szWhere;
		m_nState = STATE_DB_HIT;
		return findInDB(sf, szSQL);
	}
    
	public boolean simpleInsert(  DataSmartMap sf)
	{
//		String key = sf.getEntry(m_szUniqueField);
		
//		if ((null ==key)||(key.length()==0))
//			return false;	// can not do any thing without specifying key;

		int id= findInCache( sf );
		if (id>0){
			return false;	// already in cache, should also be in db
		}
		
		String szSQLW  = "INSERT INTO "+ genTableName() + sf.getSQLInsert();
		
		if (m_db.genericWrite( szSQLW )){
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

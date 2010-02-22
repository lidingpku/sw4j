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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 *	load database configuraiton from configuraiton file
 * @author Li
 *
 */
public class DbSettings extends Properties{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DbSettings() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean load(String filename){
		try {
			this.load(new FileInputStream(filename));
			return true;
		}catch (IOException e){
			e.printStackTrace();
			System.out.println("cannot find Settings file: "+filename);
			//System.exit(0); //if the settings cannot be loaded, than we must stop any further operation
			return false;
		} 
	}
	
	final static String [] FIELDS_BASIC ={
		"db_type",
		"db_host",
		"db_user",
		"db_pass",
		"db_name",
		"db_table_prefix",
	};
	
	public static final int DB_TYPE =0;
	public static final int DB_HOST =1;
	public static final int DB_USER =2;
	public static final int DB_PASS =3;
	public static final int DB_NAME =4;
	public static final int DB_TABLE_PREFIX =5;
	
	public String getPropertyByIndex(int index){
		return getProperty(FIELDS_BASIC[index]);
	}	
	public static void main(String[] args) throws IOException {
		//test 
		DbSettings settings = new DbSettings();
		settings.load("sw4j.config.db.test");
		
		System.out.println(settings);
	}
}

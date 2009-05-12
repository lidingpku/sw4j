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


package sw4j.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;






/**
 * provide functions for read/write access and pipes among files, URLs, streams.
 * 
 * @author Li Ding
 * 
 */
public class ToolIO {

	private static Logger getLogger() {
		return Logger.getLogger(ToolIO.class);
	}

	
	///////////////////////////////////////////////////////
	// prepare
	///////////////////////////////////////////////////////


	/** 
	 * convert a string to File object
	 * 
	 * @param szFileName
	 * @return
	 * @throws Sw4jException 
	 */
	public static File prepareFile(String szFileName) throws Sw4jException{
		// validate 1: empty uri
		ToolSafe.checkNonEmpty(szFileName, "Need non-empty file name.");

		return new File(szFileName);
	}

	/**
	 *  prepare inputStream from a file
	 * @param f
	 * @return
	 * @throws Sw4jException
	 */
	public 	static FileInputStream prepareFileInputStream(String szFileName) throws Sw4jException {
		return prepareFileInputStream(prepareFile(szFileName));
	}	


	/**
	 *  prepare inputStream from a file
	 * @param f
	 * @return
	 * @throws Sw4jException
	 */
	public 	static FileInputStream prepareFileInputStream(File f) throws Sw4jException {
		try {
			return new FileInputStream(f);
		} catch (FileNotFoundException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
			throw e1;
		}
	}	


	/**
	 * prepare an input stream from url
	 * @param szUrl
	 * @return
	 * @throws Sw4jException
	 */
	public static InputStream pipeUrlToInputStream(String szUrl) throws Sw4jException {
		URL url = ToolURI.string2url(szUrl);
		return pipeUrlToInputStream(url);
	}	


	/**
	 * prepare an input stream from url
	 * @param url
	 * @return
	 * @throws Sw4jException
	 */
	private static InputStream pipeUrlToInputStream(URL url) throws Sw4jException {
		try {
			URLConnection conn = null;
			conn = url.openConnection();
			conn.connect();
			return conn.getInputStream();
		} catch (IOException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
			throw e1;
		}
	}	
	

	public static OutputStream prepareFileOutputStream(String szFileName, boolean bGzip)
	throws Sw4jException {
		//File _dump = new File(szFileName);
		return prepareFileOutputStream(prepareFile(szFileName), false, bGzip);
	}

	/**
	 * NOTE: bGzip will not be used if bAppend is true because we cannot append a gzip file 
	 * 
	 * @param f
	 * @param bAppend
	 * @param bGzip
	 * @return
	 * @throws Sw4jException
	 */
	public static OutputStream prepareFileOutputStream(File f, boolean bAppend, boolean bGzip)
	throws Sw4jException {
		try {
			File _dir = f.getAbsoluteFile().getParentFile();
			if (!_dir.exists()) {
				_dir.mkdirs();
			}

			OutputStream out = new FileOutputStream(f,bAppend);
			if (!bAppend && bGzip){
				out = new GZIPOutputStream(out);
			}
			return out;
			
		} catch (FileNotFoundException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
			throw e1;
		} catch (IOException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
			throw e1;
		}	
	}


	///////////////////////////////////////////////////////
	// URL functions
	///////////////////////////////////////////////////////



	/**
	 * pipe the content obtained from a URL into a byte array
	 * 
	 * @param szUrl
	 * @return non-null byte array if succeed, otherwise an exception will be thrown
	 * @throws Sw4jException
	 */
	public static byte[] pipeUrlToBytes(String szUrl) throws Sw4jException {
		return pipeUrlToBytes(ToolURI.string2url(szUrl));
	}

	public static byte[] pipeUrlToBytes(URL url) throws Sw4jException {
		BufferedInputStream bis = new BufferedInputStream(pipeUrlToInputStream(url));
		return  pipeInputStreamToBytes(bis);
	}


	/**
	 * pipe the content from a URL into a file (download to file)
	 * 
	 * @param szUrl
	 * @param szFileName
	 * @throws Sw4jException
	 */
	public static void pipeUrlToFile(String szUrl, String szFileName, boolean bGzip)
	throws Sw4jException {
		byte[] data = pipeUrlToBytes(szUrl);
		File output = prepareFile(szFileName);
		pipeBytesToFile(data, output, false, bGzip);
	}
	/**
	 * pipe the content from a URL into a string (download to string)
	 * @param szUrl
	 * @return
	 * @throws Sw4jException
	 */
	public static String pipeUrlToString(String szUrl) throws Sw4jException {
		return pipeInputStreamToString( pipeUrlToInputStream(szUrl));
	}







	///////////////////////////////////////////////////////
	// File functions
	///////////////////////////////////////////////////////

	/**
	 * pipe the content of a file into a byte array
	 * 
	 * @param szFileName
	 * @return a non-null byte array will be returned if succeed.
	 * @throws Sw4jException
	 */
	public static byte[] pipeFileToBytes(String szFileName) throws Sw4jException {
		File f = prepareFile(szFileName);
		return pipeFileToBytes (f);
	}

	/**
	 *  pipe the content of a file into a byte array
	 * @param f
	 * @return
	 * @throws Sw4jException
	 */
	public 	static byte[] pipeFileToBytes(File f) throws Sw4jException {
		return pipeInputStreamToBytes(prepareFileInputStream(f));
	}

	/**
	 * pipe the content of a file into a string 
	 * 
	 * TODO: we should not simply use
	 * java default encoding to parse the byte array obtained from the file.
	 * 
	 * @param szFileName
	 * @return non-null string if succeed, otherwise an exception will be thrown
	 * @throws Sw4jException
	 */
	public static String pipeFileToString(String szFileName)
	throws Sw4jException {
		File f = prepareFile(szFileName);
		return pipeFileToString (f);	
	}

	public static String pipeFileToString(File f) throws Sw4jException {
		return pipeInputStreamToString(prepareFileInputStream(f));
	}	

	public static void pipeFileToFile(File from, File to) throws Sw4jException{
		ToolIO.pipeInputStreamToOutputStream(prepareFileInputStream(from), prepareFileOutputStream(to, false, false));
	}


	///////////////////////////////////////////////////////
	// input stream  functions
	///////////////////////////////////////////////////////

	/**
	 * pipe inputstream into a local byte array
	 * 
	 * @param isr
	 * @return a non-null byte array will be returned if succeed. otherwise an
	 *         SwutilException will be returned.
	 * @throws IOException
	 * @throws Sw4jException
	 */
	public static byte[] pipeInputStreamToBytes(InputStream isr)
	throws Sw4jException {
		ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
		pipeInputStreamToOutputStream(isr,bytearray);
		return bytearray.toByteArray();
	}



	
	public static void pipeInputStreamToOutputStream(InputStream in, OutputStream out) throws Sw4jException{
		long bytenum = 0;
		final int BUFFER_SIZE = 8192;
		byte[] buffer = new byte[BUFFER_SIZE];
		int nRead = -1;
		try {
			while ((nRead = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
				out.write(buffer, 0, nRead);
				bytenum += nRead;
			}
			in.close();
		} catch (IOException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());

			try {
				in.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			throw e1;
		}
	}

	/**
	 * pipe stuff to String  (we can just use new String(byte[]) because it uses the heap and may cause outOfMemory exception 
	 * 
	 * @param inputReader
	 * @return
	 * @throws Sw4jException
	 */
	public static String pipeInputStreamToString(InputStream in) throws Sw4jException{
		// TODO: need charset detector if processing non-English stuff
		// we assume default charset
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuffer fileData = new StringBuffer(1000);
		char[] buf = new char[1024];
		int numRead=0;
		try{
			while((numRead=reader.read(buf)) != -1){
				fileData.append(buf, 0, numRead);
			}
			reader.close();
			return fileData.toString();
		} catch (IOException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
			throw e1;
		}

	}

	

	///////////////////////////////////////////////////////
	// String functions
	///////////////////////////////////////////////////////


	/**
	 * pipe a string into file storage.
	 * 
	 * @param szContent
	 * @param szFileName
	 * @return 
	 * @throws Sw4jException
	 */
	public static void pipeStringToFile(String szContent, String szFileName, boolean bGzip)
	throws Sw4jException {
		pipeStringToFile(szContent, szFileName, bGzip, false);
	}

	public static void pipeStringToFile(String szContent, String szFileName, boolean bGzip, boolean bAppend)
	throws Sw4jException {
		pipeStringToFile(szContent, prepareFile(szFileName), bGzip, bAppend);
	}

	public static void pipeStringToFile(String szContent, File f, boolean bGzip, boolean bAppend)
	throws Sw4jException {
		ToolSafe.checkNonEmpty(szContent, "Need non-empty content");
		pipeBytesToFile(szContent.getBytes(), f, bAppend, bGzip);
	}

	public static StringReader pipeStringToReader(String szContent)
	throws Sw4jException {
		ToolSafe.checkNonEmpty(szContent, "Need non-empty content");
		return new StringReader(szContent);
	}


	public static String pipeStringToLineNumberedString(String szText) throws Sw4jException{
		StringWriter sw =new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		pipeStringToLineNumberedString(szText,out);
		return sw.toString();
	}

	public static void pipeStringToLineNumberedString(String szText, PrintWriter out)
	throws Sw4jException {
		LineNumberReader reader = new LineNumberReader(pipeStringToReader(szText));
		String line=null;
		try {
			while (null!=(line=reader.readLine())){
				out.println(String.format("%d: %s", reader.getLineNumber(), line));
			}
			out.flush();
			out.close();
			reader.close();

		} catch (IOException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
		}
	}
	
	public static void pipeStringToSmartMap(String szText, DataSmartMap ds)
	throws Sw4jException {
		LineNumberReader reader = new LineNumberReader(pipeStringToReader(szText));
		String line=null;
		String format = "L%0"+(Math.log10(szText.length()))+"d";
		try {
			while (null!=(line=reader.readLine())){
				//			line = ToolWeb.string2htmlStringWhitespace(line);
				ds.put(String.format(format,reader.getLineNumber()), line);
			}

			reader.close();
		} catch (IOException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
		}
	}	
	

	///////////////////////////////////////////////////////
	// bytes functions
	///////////////////////////////////////////////////////

	/**
	 * pipe a list of bytes into a file 
	 * 
	 * @param data
	 * @param f
	 * @return true if succeed.
	 * @throws Sw4jException
	 */
	public static void pipeBytesToFile(byte[] data, File f, boolean bAppend, boolean bGzip)
	throws Sw4jException {

		try {
			OutputStream out = prepareFileOutputStream(f, bAppend, bGzip);
			if (data.length>0){
				out.write(data);
			}
			out.flush();
			out.close();
			return ;
		} catch (IOException e) {
			Sw4jException e1 = new Sw4jException(Sw4jMessage.STATE_FATAL, e);
			getLogger().error(e1.getMessage());
			throw e1;
		}
	}

	public static InputStream pipeBytesToInputStream(byte[] data)
	throws Sw4jException {
		return new ByteArrayInputStream(data);
	}


	static public boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }

}

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
package sw4j.task.load;
/**
 * 
 * loading data from string to memory
 * 
 * @author Li Ding
 * 
 */
public class DataTaskLoadText  extends TaskLoad{

	protected DataTaskLoadText(String szXmlBase) {
		// automatically generate url for jena parsing
		super( null , szXmlBase );
//		super( "http://tw.rpi.edu/_runtime_"+ System.currentTimeMillis()+"_"+String.format("%03.0f", Math.random()*999)	, szXmlBase );
	}

	@Override
	public boolean isHttpLoader() {
		return false;
	}


	@Override
	protected boolean validateTask() {

		return true;
	}

}
